package com.vnpay.common;

import Dal.BookingDAO;
import Dal.VNPayPaymentDAO;
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

        System.out.println("=== REFUND REQUEST START ===");

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

            System.out.println("Refund params - BookingId: " + bookingIdStr + ", Amount: " + amountStr);

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
                System.out.println("Booking " + bookingId + " is not refundable");
                session.setAttribute("refundMsg", "Booking không thể hoàn tiền!");
                response.sendRedirect("myBooking");
                return;
            }

            // 2. Kiểm tra đã refund chưa
            VNPayPaymentDAO paymentDAO = new VNPayPaymentDAO();
            if (paymentDAO.isAlreadyRefunded(bookingId)) {
                System.out.println("Booking " + bookingId + " already refunded");
                session.setAttribute("refundMsg", "Booking đã được hoàn tiền!");
                response.sendRedirect("myBooking");
                return;
            }

            // 3. Lấy thông tin transaction để refund
            VNPayPaymentDAO.VNPayTransactionInfo txnInfo = paymentDAO.getTransactionInfoByBookingId(bookingId);
            if (txnInfo == null) {
                System.out.println("No transaction info found for booking: " + bookingId);
                session.setAttribute("refundMsg", "Không tìm thấy thông tin giao dịch!");
                response.sendRedirect("myBooking");
                return;
            }

            System.out.println("Transaction found - TxnRef: " + txnInfo.vnpTxnRef + ", PayDate: " + txnInfo.payDate);

            // 4. Process VNPay refund
            RefundResult result = processVNPayRefund(txnInfo, amount);

            if (result.success) {
                updateRefundStatus(bookingId, amount, refundReason);
                sendRefundConfirmationEmail(user.getEmail(), bookingId, amount, result.transactionNo);                // Format    số tiền
                DecimalFormat formatter = new DecimalFormat("#,###");
                String formattedAmount = formatter.format(amount);

                session.setAttribute("refundMsg",
                        "✅ HOÀN TIỀN THÀNH CÔNG QUA VNPAY<br>"
                        + "💰 Số tiền: " + formattedAmount + " VND<br>"
                        + "🏦 Mã giao dịch VNPay: " + result.transactionNo + "<br>"
                        + "📅 Thời gian: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "<br>"
                        + "⏰ Tiền sẽ về tài khoản trong 1-3 ngày làm việc" + "<br>"
                        + "CẢM ƠN BẠN ĐÃ SỬ DỤNG DỊCH VỤ!!!");

                System.out.println("=== VNPAY REFUND COMPLETED ===");
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
            System.out.println("Processing VNPay refund...");

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

            System.out.println("=== REFUND HASH DEBUG ===");
            System.out.println("Hash data: " + hash_Data);
            System.out.println("Secret key: " + Config.secretKey);

            String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hash_Data);
            System.out.println("Generated hash: " + vnp_SecureHash);

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

            System.out.println("Calling VNPay refund API...");
            System.out.println("Request params: " + vnp_Params);

            // Call VNPay API
            URL url = new URL(Config.vnp_ApiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            // Send request
            String jsonInputString = new com.google.gson.Gson().toJson(vnp_Params);
            System.out.println("JSON request: " + jsonInputString);

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

            System.out.println("VNPay Refund Response: " + response.toString());

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
                System.out.println("VNPay refund successful");
            } else {
                result.success = false;
                System.out.println("VNPay refund failed with code: " + responseCodeFromVNP);
            }

        } catch (Exception e) {
            result.success = false;
            result.message = "API call failed: " + e.getMessage();
            System.err.println("VNPay refund API error: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    private void updateRefundStatus(int bookingId, double amount, String reason) {
        try {
            VNPayPaymentDAO paymentDAO = new VNPayPaymentDAO();
            BookingDAO bookingDAO = new BookingDAO();

            // 1. Update VNPayPayment status = 'Refunded'
            boolean paymentUpdated = paymentDAO.refundPaymentByBookingId(bookingId);

            // 2. Update VNPayTransaction is_refunded = 1
            boolean transactionUpdated = paymentDAO.updateTransactionRefundStatus(bookingId);

            // 3. Update Booking với cancel_reason
            boolean bookingUpdated = bookingDAO.updateBookingForRefund(bookingId, "Cancelled", "Unpaid", reason);

            System.out.println("✅ Refund completed - Payment: " + paymentUpdated
                    + ", Transaction: " + transactionUpdated
                    + ", Booking: " + bookingUpdated);

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
            System.out.println("✅ Refund confirmation email sent to: " + userEmail);

        } catch (Exception e) {
            System.err.println("❌ Failed to send refund email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
