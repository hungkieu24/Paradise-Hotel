package com.vnpay.common;

import Dal.BookingDAO;
import Dal.BookingRoomTypeDAO;
import Dal.InvoiceDAO;
import Dal.LoyaltyPointDAO;
import Dal.RoomDAO;
import Dal.UserAccountDAO;
import Dal.VNPayPaymentDAO;
import Model.Booking;
import Model.BookingRoomType;
import Model.Invoice;
import Model.Room;
import Model.UserAccount;
import Model.VNPayPayment;
import Utility.EmailUtility;
import java.io.IOException;
import java.sql.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import Dal.LoyaltyPointDAO;

@WebServlet("/payment-result")
public class PaymentResultServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        if ("Customer".equalsIgnoreCase(user.getRole())) {
            try {
                // Get basic parameters
                String vnp_TxnRef = request.getParameter("vnp_TxnRef");
                String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
                String vnp_Amount = request.getParameter("vnp_Amount");
                String vnp_TransactionNo = request.getParameter("vnp_TransactionNo");
                String vnp_BankCode = request.getParameter("vnp_BankCode");
                String vnp_PayDate = request.getParameter("vnp_PayDate");
                String vnp_OrderInfo = request.getParameter("vnp_OrderInfo");

                // Validate required parameters
                if (vnp_TxnRef == null || vnp_ResponseCode == null || vnp_Amount == null) {
                    request.setAttribute("status", "INVALID_REQUEST");
                    request.setAttribute("errorMessage", "Missing required parameters");
                    request.getRequestDispatcher("vnpay_return.jsp").forward(request, response);
                    return;
                }

                int bookingId = Integer.parseInt(vnp_TxnRef.split("-")[1]);
                double amount = Long.parseLong(vnp_Amount) / 100.0;

                // Check payment success
                if (!"00".equals(vnp_ResponseCode)) {
                    setErrorAttributes(request, "FAILED", "Payment failed",
                            vnp_TxnRef, vnp_TransactionNo, vnp_BankCode, vnp_ResponseCode,
                            vnp_PayDate, String.valueOf(amount), vnp_OrderInfo);
                    request.getRequestDispatcher("vnpay_return.jsp").forward(request, response);
                    return;
                }

                // Check duplicate payment
                VNPayPaymentDAO paymentDAO = new VNPayPaymentDAO();
                if (paymentDAO.hasExistingPayment(bookingId)) {
                    setSuccessAttributes(request, vnp_TxnRef, vnp_TransactionNo,
                            vnp_BankCode, vnp_ResponseCode, vnp_PayDate, String.valueOf(amount), vnp_OrderInfo);
                    request.getRequestDispatcher("vnpay_return.jsp").forward(request, response);
                    return;
                }

                // Create payment record
                VNPayPayment payment = new VNPayPayment();
                payment.setBookingId(bookingId);
                payment.setAmount(amount);
                payment.setStatus("Completed");
                payment.setPaidAt(new Timestamp(System.currentTimeMillis()));

                int paymentId = paymentDAO.createPayment(payment);

                // Update booking status
                BookingDAO bookingDAO = new BookingDAO();
                bookingDAO.updateBookingStatus(bookingId, "Paid");
                bookingDAO.updateBookingPaymentStatus(bookingId, "Paid");
                bookingDAO.updateBookingServicePaidStatus(bookingId, "Paid");

                // Award loyalty points
                awardLoyaltyPointsForPayment(user.getId(), amount);

                // Set success attributes
                setSuccessAttributes(request, vnp_TxnRef, vnp_TransactionNo,
                        vnp_BankCode, vnp_ResponseCode, vnp_PayDate, String.valueOf(amount), vnp_OrderInfo);

                request.getRequestDispatcher("vnpay_return.jsp").forward(request, response);

            } catch (Exception e) {
                System.err.println("Error processing payment result: " + e.getMessage());
                e.printStackTrace();
                request.setAttribute("status", "ERROR");
                request.setAttribute("errorMessage", "Internal server error");
                request.getRequestDispatcher("vnpay_return.jsp").forward(request, response);
            }
        } else {
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
            String signValue = Config.hashAllFields(fields);
            boolean isValidSignature = signValue.equals(vnp_SecureHash);

            // Get parameters from request
            String vnp_TxnRef = request.getParameter("vnp_TxnRef");
            String vnp_TransactionNo = request.getParameter("vnp_TransactionNo");
            String vnp_BankCode = request.getParameter("vnp_BankCode");
            String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
            String vnp_PayDate = request.getParameter("vnp_PayDate");
            String vnp_Amount = request.getParameter("vnp_Amount");
            String vnp_OrderInfo = request.getParameter("vnp_OrderInfo");

            int bookingId = Integer.parseInt(vnp_TxnRef.split("-")[1]);
            double amount = Double.parseDouble(vnp_Amount) / 100.0;

            VNPayPayment payment = new VNPayPayment();
            payment.setBookingId(bookingId);
            payment.setAmount(amount);
            payment.setStatus("Completed");
            payment.setPaidAt(new Timestamp(System.currentTimeMillis()));

            VNPayPaymentDAO dao = new VNPayPaymentDAO();
            int paymentId = dao.createPayment(payment);
            BookingDAO bookingDAO = new BookingDAO();
            Booking bookingCurrent = bookingDAO.getBookingById(bookingId);

            bookingDAO.updateBookingStatus(bookingId, "Completed");
            bookingDAO.updateBookingPaymentStatus(bookingId, "Paid");
            request.setAttribute("vnp_TxnRef", vnp_TxnRef);
            request.setAttribute("vnp_TransactionNo", vnp_TransactionNo);
            request.setAttribute("vnp_BankCode", vnp_BankCode);
            request.setAttribute("vnp_ResponseCode", vnp_ResponseCode);
            request.setAttribute("vnp_PayDate", vnp_PayDate);
            request.setAttribute("vnp_Amount", vnp_Amount);
            request.setAttribute("vnp_OrderInfo", vnp_OrderInfo);
            request.setAttribute("status", "SUCCESS");

            InvoiceDAO invoiceDAO = new InvoiceDAO();
            Invoice invoice = new Invoice();
            invoice.setBookingId(bookingId);
            invoice.setTotalAmount(amount);
            invoiceDAO.createInvoice(invoice);

            UserAccountDAO userDAO = new UserAccountDAO();

            UserAccount user2 = userDAO.findById(bookingCurrent.getUserId());

            LoyaltyPointDAO loyaltyPointDAO = new LoyaltyPointDAO();

            loyaltyPointDAO.addPoints(bookingCurrent.getUserId(), (int) amount / 100000, "Payment success " + amount);
            boolean loyaltyUpdated = loyaltyPointDAO.updateTotalSpending(bookingCurrent.getUserId(), amount);
            RoomDAO roomDAO = new RoomDAO();
            roomDAO.updateRoomStatusAfterCheckout(bookingId, "Available");

            UserAccount staff = (UserAccount) session.getAttribute("user");
            Integer branchId = staff.getBranchId();

            List<Room> bookingRoomList = bookingDAO.getRoomsByBookingIdAndBranch(bookingId, branchId != null ? branchId : 1);
            BookingRoomTypeDAO bookingRoomTypeDAO = new BookingRoomTypeDAO();
            List<BookingRoomType> bookingRoomTypes = bookingRoomTypeDAO.getBookingRoomTypesByBookingId(bookingId);

            java.util.Date now = new java.util.Date();
            java.util.Date checkInTime = bookingCurrent.getCheckIn();

            try {
                EmailUtility.sendInvoice(user2.getEmail(), "Invoice",
                        vnp_Amount,
                        vnp_Amount, bookingRoomList, user2, bookingCurrent,
                        bookingRoomTypes);
            } catch (Exception e) {
            }
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
}
