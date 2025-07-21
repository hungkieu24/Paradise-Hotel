package com.vnpay.common;

import Dal.BookingDAO;
import Dal.LoyaltyPointDAO;
import Dal.VNPayPaymentDAO;
import Model.Booking;
import Model.UserAccount;
import Utility.EmailUtility;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@WebServlet("/vnpay-refund")
public class VNPayRefundServlet extends HttpServlet {

    // ĐỊNH NGHĨA RefundResult CLASS Ở ĐẦU FILE
    private static class RefundResult {

        boolean success = false;
        String message = "";
        String responseCode = "";
        String txnRef = "";
        String transactionNo = "";
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            String bookingIdStr = request.getParameter("bookingId");
            String amountStr = request.getParameter("amount");
            String refundReason = request.getParameter("refundReason");
            String confirmLateCancel = request.getParameter("confirmLateCancel"); // New parameter for late cancellation confirmation

            if (bookingIdStr == null || amountStr == null) {
                session.setAttribute("refundMsg", "Thiếu thông tin cần thiết!");
                response.sendRedirect("myBooking");
                return;
            }

            int bookingId = Integer.parseInt(bookingIdStr);
            double amount = Double.parseDouble(amountStr);

            // 1. Kiểm tra booking có thể refund không
            BookingDAO bookingDAO = new BookingDAO();
            if (!bookingDAO.isBookingRefundable(bookingId)) {
                session.setAttribute("refundMsg", "Booking không thể hoàn tiền!");
                response.sendRedirect("myBooking");
                return;
            }

            // 2. Kiểm tra đã refund chưa
            VNPayPaymentDAO paymentDAO = new VNPayPaymentDAO();
            if (paymentDAO.isAlreadyRefunded(bookingId)) {
                session.setAttribute("refundMsg", "Booking đã được hoàn tiền!");
                response.sendRedirect("myBooking");
                return;
            }

            // 3. Kiểm tra thời gian thanh toán (1 ngày)
            boolean isPaymentOlderThanOneDay = paymentDAO.isPaymentOlderThanOneDay(bookingId);

            if (isPaymentOlderThanOneDay && !"true".equals(confirmLateCancel)) {
                // Nếu đã quá 1 ngày và chưa confirm, redirect về trang với thông báo xác nhận
                session.setAttribute("lateCancelBookingId", bookingId);
                session.setAttribute("lateCancelAmount", amount);
                session.setAttribute("lateCancelReason", refundReason);
                session.setAttribute("refundMsg", "LATE_CANCEL_CONFIRM");
                response.sendRedirect("myBooking");
                return;
            }

            if (isPaymentOlderThanOneDay && "true".equals(confirmLateCancel)) {
                // User confirmed late cancellation - cancel without refund
                boolean cancelled = bookingDAO.cancelBookingWithoutRefund(bookingId,
                    "Cancelled after 1-day refund period: " + (refundReason != null ? refundReason : "No reason provided"));

                if (cancelled) {
                    session.setAttribute("refundMsg", "Booking đã được hủy thành công. Do đã quá 1 ngày từ lúc thanh toán nên không được hoàn tiền.");
                } else {
                    session.setAttribute("refundMsg", "Lỗi khi hủy booking!");
                }
                response.sendRedirect("myBooking");
                return;
            }

            // 4. Lấy thông tin transaction để refund (normal refund within 1 day)
            VNPayPaymentDAO.VNPayTransactionInfo txnInfo = paymentDAO.getTransactionInfoByBookingId(bookingId);
            if (txnInfo == null) {
                session.setAttribute("refundMsg", "Không tìm thấy thông tin giao dịch!");
                response.sendRedirect("myBooking");
                return;
            }

            // 5. Process VNPay refund (normal refund within 1 day)
            RefundResult result = processVNPayRefund(txnInfo, amount);

            if (result.success) {
                updateRefundStatus(bookingId, amount, refundReason);
                sendRefundConfirmationEmail(user.getEmail(), bookingId, amount, result.transactionNo);
                int pointsDeducted = (int) (amount / 100000);// Format    số tiền
                DecimalFormat formatter = new DecimalFormat("#,###");
                String formattedAmount = formatter.format(amount);

                session.setAttribute("refundMsg",
                        "✅ HOÀN TIỀN THÀNH CÔNG QUA VNPAY<br><br>"
                        + "💰 Số tiền: " + formattedAmount + " VND<br><br>"
                        + "🏦 Mã giao dịch VNPay: " + result.transactionNo + "<br><br>"
                        + "📅 Thời gian: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "<br><br>"
                        + (pointsDeducted > 0 ? "🎯 Điểm loyalty bị trừ: " + pointsDeducted + " điểm<br><br>" : "")
                        + "⏰ Tiền sẽ về tài khoản trong 1-3 ngày làm việc" + "<br><br>"
                        + "CẢM ƠN BẠN ĐÃ SỬ DỤNG DỊCH VỤ!!!");

            } else {
                session.setAttribute("refundMsg",
                        "❌ VNPAY TỪ CHỐI HOÀN TIỀN<br>"
                        + "📋 Lý do: " + result.message + "<br>"
                        + "🔢 Mã lỗi VNPay: " + result.responseCode + "<br>"
                        + "📞 Liên hệ hotline để được hỗ trợ");
            }

        } catch (Exception e) {
            System.err.println("Error processing refund: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("refundMsg", "Lỗi hệ thống khi xử lý hoàn tiền!");
        }

        response.sendRedirect("myBooking");
    }

    private RefundResult processVNPayRefund(VNPayPaymentDAO.VNPayTransactionInfo txnInfo, double amount) {
        RefundResult result = new RefundResult();

        try {
            // Prepare refund parameters
            String vnp_RequestId = Config.getRandomNumber(8);
            String vnp_Version = "2.1.0";
            String vnp_Command = "refund";
            String vnp_TmnCode = Config.vnp_TmnCode;
            String vnp_TransactionType = "02";
            String vnp_TxnRef = txnInfo.vnpTxnRef;
            String vnp_Amount = String.valueOf((long) (amount * 100));
            String vnp_OrderInfo = "Refund for booking";
            String vnp_TransactionDate = txnInfo.payDate;
            String vnp_CreateBy = "System";
            String vnp_CreateDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String vnp_IpAddr = "127.0.0.1";

            // SỬA: Tạo hash theo đúng format VNPay Refund API
            String hash_Data = String.join("|",
                    vnp_RequestId, vnp_Version, vnp_Command, vnp_TmnCode,
                    vnp_TransactionType, vnp_TxnRef, vnp_Amount,
                    txnInfo.vnpTransactionNo != null ? txnInfo.vnpTransactionNo : "", // Thêm vnp_TransactionNo
                    vnp_TransactionDate, vnp_CreateBy, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo);

            String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hash_Data);

            // Tạo JSON request
            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_RequestId", vnp_RequestId);
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_TransactionType", vnp_TransactionType);
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_Amount", vnp_Amount);
            vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
            vnp_Params.put("vnp_TransactionDate", vnp_TransactionDate);
            vnp_Params.put("vnp_CreateBy", vnp_CreateBy);
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
            vnp_Params.put("vnp_SecureHash", vnp_SecureHash);

            // THÊM vnp_TransactionNo nếu có
            if (txnInfo.vnpTransactionNo != null && !txnInfo.vnpTransactionNo.isEmpty()) {
                vnp_Params.put("vnp_TransactionNo", txnInfo.vnpTransactionNo);
            }

            // Call VNPay API
            URL url = new URL(Config.vnp_ApiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            // Send request
            String jsonInputString = new com.google.gson.Gson().toJson(vnp_Params);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read response
            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            // Parse response
            com.google.gson.JsonObject responseJson = com.google.gson.JsonParser.parseString(response.toString()).getAsJsonObject();
            String responseCodeFromVNP = responseJson.get("vnp_ResponseCode").getAsString();

            result.responseCode = responseCodeFromVNP;
            result.message = responseJson.has("vnp_Message")
                    ? responseJson.get("vnp_Message").getAsString() : "Unknown";
            result.transactionNo = responseJson.has("vnp_TransactionNo")
                    ? responseJson.get("vnp_TransactionNo").getAsString() : "";

            if ("00".equals(responseCodeFromVNP)) {
                result.success = true;
            } else {
                result.success = false;
            }

        } catch (Exception e) {
            result.success = false;
            result.message = "API call failed: " + e.getMessage();
            System.err.println("VNPay refund API error: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    // Thêm log để debug
    private void updateRefundStatus(int bookingId, double amount, String reason) {
        try {

            VNPayPaymentDAO paymentDAO = new VNPayPaymentDAO();
            BookingDAO bookingDAO = new BookingDAO();
            LoyaltyPointDAO loyaltyPointDAO = new LoyaltyPointDAO();

            // Update payment and booking status with refund amount
            boolean paymentUpdated = paymentDAO.refundPaymentByBookingId(bookingId);
            boolean transactionUpdated = paymentDAO.updateTransactionRefundStatus(bookingId);
            boolean bookingUpdated = bookingDAO.updateBookingForRefund(bookingId, "Cancelled", "Paid", reason, amount);

            // Get booking info and subtract points
            Booking booking = bookingDAO.getBookingById(bookingId);
            if (booking != null && booking.getUserId() != null) {

                // Check current points before deduction
                int currentPoints = loyaltyPointDAO.getPointsByUser(booking.getUserId());

                boolean pointsDeducted = loyaltyPointDAO.subtractPointsForRefund(
                        booking.getUserId(),
                        amount,
                        "Points deducted for refund - Booking #" + bookingId
                );

                if (pointsDeducted) {
                    int newPoints = loyaltyPointDAO.getPointsByUser(booking.getUserId());

                    loyaltyPointDAO.checkAndUpdateTier(booking.getUserId());
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Error updating refund status: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendRefundConfirmationEmail(String userEmail, int bookingId, double amount, String transactionNo) {
        try {
            String subject = "Xác nhận hoàn tiền thành công - Booking #" + bookingId;

            DecimalFormat formatter = new DecimalFormat("#,###");
            String formattedAmount = formatter.format(amount);
            String currentDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());

            String emailContent
                    = "<h2>✅ Hoàn tiền thành công!</h2>"
                    + "<p><strong>Booking ID:</strong> #" + bookingId + "</p>"
                    + "<p><strong>Số tiền hoàn:</strong> " + formattedAmount + " VND</p>"
                    + "<p><strong>Mã giao dịch VNPay:</strong> " + transactionNo + "</p>"
                    + "<p><strong>Thời gian xử lý:</strong> " + currentDate + "</p>"
                    + "<p style='color: #666;'>Tiền sẽ được chuyển về tài khoản của bạn trong 1-3 ngày làm việc.</p>"
                    + "<p>Cảm ơn bạn đã sử dụng dịch vụ!</p>";

            EmailUtility.sendRefundEmail(userEmail, subject, emailContent);

        } catch (Exception e) {
            System.err.println("❌ Failed to send refund email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
