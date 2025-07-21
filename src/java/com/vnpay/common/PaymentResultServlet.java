package com.vnpay.common;

import Dal.BookingDAO;
import Dal.LoyaltyPointDAO;
import Dal.VNPayPaymentDAO;
import Model.UserAccount;
import Model.VNPayPayment;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/payment-result")
public class PaymentResultServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            // Capture payment response
            Map<String, String> fields = new HashMap<>();
            for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
                String fieldName = params.nextElement();
                String fieldValue = request.getParameter(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    fields.put(fieldName, fieldValue);
                }
            }

            String vnp_SecureHash = request.getParameter("vnp_SecureHash");

            fields.remove("vnp_SecureHashType");
            fields.remove("vnp_SecureHash");

            // Verify signature
            String signValue = Config.hashAllFieldsDebug(fields); // THAY ĐỔI TỪ hashAllFields SANG hashAllFieldsDebug
            // THÊM LOGGING CHO HASH CALCULATION

            boolean isValidSignature = signValue.equals(vnp_SecureHash);

            // Get parameters from request
            String vnp_TxnRef = request.getParameter("vnp_TxnRef");
            String vnp_TransactionNo = request.getParameter("vnp_TransactionNo");
            String vnp_BankCode = request.getParameter("vnp_BankCode");
            String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
            String vnp_PayDate = request.getParameter("vnp_PayDate");
            String vnp_Amount = request.getParameter("vnp_Amount");
            String vnp_OrderInfo = request.getParameter("vnp_OrderInfo");

            // Add parameter validation
            if (vnp_TxnRef == null || vnp_ResponseCode == null || vnp_Amount == null) {
                System.err.println("Missing required parameters");
                request.setAttribute("status", "INVALID_REQUEST");
                request.setAttribute("errorMessage", "Missing required parameters");
                request.getRequestDispatcher("vnpay_return.jsp").forward(request, response);
                return;
            }

            int bookingId = Integer.parseInt(vnp_TxnRef.split("-")[1]);

            // VNPay trả về amount theo đơn vị xu (VND * 100)
            long vnpAmountLong = Long.parseLong(vnp_Amount);
            double amount = vnpAmountLong / 100.0; // Cho hiển thị
            double vnpAmountOriginal = vnpAmountLong; // Cho lưu DB

            HttpSession session = request.getSession();
            UserAccount user = (UserAccount) session.getAttribute("user");
            if (user == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            // Add signature validation check
            // Add signature validation check
            if (!isValidSignature) {

                request.setAttribute("vnp_TxnRef", vnp_TxnRef);
                request.setAttribute("vnp_TransactionNo", vnp_TransactionNo);
                request.setAttribute("vnp_BankCode", vnp_BankCode);
                request.setAttribute("vnp_ResponseCode", vnp_ResponseCode);
                request.setAttribute("vnp_PayDate", vnp_PayDate);
                request.setAttribute("vnp_Amount", amount);
                request.setAttribute("vnp_OrderInfo", vnp_OrderInfo);
                request.setAttribute("status", "INVALID_SIGNATURE");
                request.getRequestDispatcher("vnpay_return.jsp").forward(request, response);
                return;
            }

            // Add response code validation
            if (!"00".equals(vnp_ResponseCode)) {
                request.setAttribute("vnp_TxnRef", vnp_TxnRef);
                request.setAttribute("vnp_TransactionNo", vnp_TransactionNo);
                request.setAttribute("vnp_BankCode", vnp_BankCode);
                request.setAttribute("vnp_ResponseCode", vnp_ResponseCode);
                request.setAttribute("vnp_PayDate", vnp_PayDate);
                request.setAttribute("vnp_Amount", amount);
                request.setAttribute("vnp_OrderInfo", vnp_OrderInfo);
                request.setAttribute("status", "FAILED");
                request.getRequestDispatcher("vnpay_return.jsp").forward(request, response);
                return;
            }

            // Add duplicate payment check
            VNPayPaymentDAO daoCheck = new VNPayPaymentDAO();
            if (daoCheck.hasExistingPayment(bookingId)) {
                request.setAttribute("vnp_TxnRef", vnp_TxnRef);
                request.setAttribute("vnp_TransactionNo", vnp_TransactionNo);
                request.setAttribute("vnp_BankCode", vnp_BankCode);
                request.setAttribute("vnp_ResponseCode", vnp_ResponseCode);
                request.setAttribute("vnp_PayDate", vnp_PayDate);
                request.setAttribute("vnp_Amount", amount);
                request.setAttribute("vnp_OrderInfo", vnp_OrderInfo);
                request.setAttribute("status", "SUCCESS");
                request.getRequestDispatcher("vnpay_return.jsp").forward(request, response);
                return;
            }

            VNPayPayment payment = new VNPayPayment();
            if ("Customer".equals(user.getRole())) {
                payment.setBookingId(bookingId);
                payment.setAmount(amount);
                payment.setStatus("Completed");
                payment.setPaidAt(new Timestamp(System.currentTimeMillis()));
                VNPayPaymentDAO dao = new VNPayPaymentDAO();
                int paymentId = dao.createPayment(payment);

                // Add transaction info saving
                if (paymentId > 0) {
                    dao.createTransaction(paymentId, vnp_TxnRef, vnp_TransactionNo, vnp_BankCode, vnp_PayDate);

                    // TẠO VNPAY TRANSACTION RECORD
                    if (paymentId > 0) {
                        // CHỈ GỌI createTransactionComplete với đầy đủ thông tin
                        dao.createTransactionComplete(paymentId, vnp_TxnRef, vnp_TransactionNo,
                                vnp_ResponseCode, Double.parseDouble(vnp_Amount), vnp_BankCode, vnp_SecureHash, vnp_PayDate);
                    }
                }

                BookingDAO bookingDAO = new BookingDAO();
                bookingDAO.updateBookingStatus(bookingId, "Paid");
                bookingDAO.updateBookingPaymentStatus(bookingId, "Paid");
                bookingDAO.updateBookingServicePaidStatus(bookingId, "Paid");
                awardLoyaltyPointsForPayment(user.getId(), amount);

                request.setAttribute("vnp_TxnRef", vnp_TxnRef);
                request.setAttribute("vnp_TransactionNo", vnp_TransactionNo);
                request.setAttribute("vnp_BankCode", vnp_BankCode);
                request.setAttribute("vnp_ResponseCode", vnp_ResponseCode);
                request.setAttribute("vnp_PayDate", vnp_PayDate);
                request.setAttribute("vnp_Amount", amount);
                request.setAttribute("vnp_OrderInfo", vnp_OrderInfo);
                request.setAttribute("status", "SUCCESS");
            } else {
                payment.setBookingId(bookingId);
                payment.setAmount(amount);
                payment.setStatus("Completed");
                payment.setPaidAt(new Timestamp(System.currentTimeMillis()));
                VNPayPaymentDAO dao = new VNPayPaymentDAO();
                int paymentId = dao.createPayment(payment);

                // Add transaction info saving
                if (paymentId > 0) {
                    // CHỈ GỌI createTransactionComplete với đầy đủ thông tin
                    dao.createTransactionComplete(paymentId, vnp_TxnRef, vnp_TransactionNo,
                            vnp_ResponseCode, Double.parseDouble(vnp_Amount), vnp_BankCode, vnp_SecureHash, vnp_PayDate);
                }

                BookingDAO bookingDAO = new BookingDAO();
                bookingDAO.updateBookingStatus(bookingId, "Completed");
                request.setAttribute("vnp_TxnRef", vnp_TxnRef);
                request.setAttribute("vnp_TransactionNo", vnp_TransactionNo);
                request.setAttribute("vnp_BankCode", vnp_BankCode);
                request.setAttribute("vnp_ResponseCode", vnp_ResponseCode);
                request.setAttribute("vnp_PayDate", vnp_PayDate);
                request.setAttribute("vnp_Amount", amount);
                request.setAttribute("vnp_OrderInfo", vnp_OrderInfo);
                request.setAttribute("status", "SUCCESS");
            }

            request.getRequestDispatcher("vnpay_return.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            System.err.println("Invalid number format in payment parameters: " + e.getMessage());
            request.setAttribute("status", "INVALID_FORMAT");
            request.setAttribute("errorMessage", "Invalid parameter format");
            request.getRequestDispatcher("vnpay_return.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("Error processing payment result: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("status", "ERROR");
            request.setAttribute("errorMessage", "Internal server error");
            request.getRequestDispatcher("vnpay_return.jsp").forward(request, response);
        }
    }

    private void setErrorAttributes(HttpServletRequest request, String status, String errorMessage,
            String vnp_TxnRef, String vnp_TransactionNo, String vnp_BankCode,
            String vnp_ResponseCode, String vnp_PayDate, String vnp_Amount, String vnp_OrderInfo) {

        request.setAttribute("status", status);
        request.setAttribute("errorMessage", errorMessage);
        request.setAttribute("vnp_TxnRef", vnp_TxnRef);
        request.setAttribute("vnp_TransactionNo", vnp_TransactionNo);
        request.setAttribute("vnp_BankCode", vnp_BankCode);
        request.setAttribute("vnp_ResponseCode", vnp_ResponseCode);
        request.setAttribute("vnp_PayDate", vnp_PayDate);
        request.setAttribute("vnp_Amount", vnp_Amount);
        request.setAttribute("vnp_OrderInfo", vnp_OrderInfo);
    }

    private void setSuccessAttributes(HttpServletRequest request, String vnp_TxnRef,
            String vnp_TransactionNo, String vnp_BankCode, String vnp_ResponseCode,
            String vnp_PayDate, String vnp_Amount, String vnp_OrderInfo) {

        request.setAttribute("status", "SUCCESS");
        request.setAttribute("vnp_TxnRef", vnp_TxnRef);
        request.setAttribute("vnp_TransactionNo", vnp_TransactionNo);
        request.setAttribute("vnp_BankCode", vnp_BankCode);
        request.setAttribute("vnp_ResponseCode", vnp_ResponseCode);
        request.setAttribute("vnp_PayDate", vnp_PayDate);
        request.setAttribute("vnp_Amount", vnp_Amount);
        request.setAttribute("vnp_OrderInfo", vnp_OrderInfo);
    }

    private void awardLoyaltyPointsForPayment(String userId, double amount) {
        try {
            LoyaltyPointDAO loyaltyPointDAO = new LoyaltyPointDAO();
            int pointsToAward = (int) (amount / 100000); // 100,000 VND = 1 point

            if (pointsToAward > 0) {
                // Sử dụng method mới để cập nhật cả points và total_spending
                boolean success = loyaltyPointDAO.addPointsWithSpending(userId, pointsToAward,
                        "Online booking payment reward", amount);

                if (success) {

                    // THÊM DÒNG NÀY: Kiểm tra và cập nhật tier
                    loyaltyPointDAO.checkAndUpdateTier(userId);
                } else {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to award loyalty points for user: " + userId);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
