package com.vnpay.common;

import Dal.BookingDAO;
import Dal.BookingRoomTypeDAO;
import Dal.InvoiceDAO;
import Dal.LoyaltyPointDAO;
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
            // THÊM LOGGING CHI TIẾT
            System.out.println("=== PAYMENT RESULT PROCESSING ===");

            try {
                // Capture payment response
                Map<String, String> fields = new HashMap<>();
                for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
                    String fieldName = params.nextElement();
                    String fieldValue = request.getParameter(fieldName);
                    if (fieldValue != null && !fieldValue.isEmpty()) {
                        fields.put(fieldName, fieldValue);
                        // THÊM LOGGING CHO TỪNG PARAMETER
                        System.out.println("Param: " + fieldName + " = " + fieldValue);
                    }
                }

                String vnp_SecureHash = request.getParameter("vnp_SecureHash");
                // THÊM LOGGING CHO HASH
                System.out.println("Received vnp_SecureHash: " + vnp_SecureHash);

                fields.remove("vnp_SecureHashType");
                fields.remove("vnp_SecureHash");

                // Verify signature
                String signValue = Config.hashAllFieldsDebug(fields); // THAY ĐỔI TỪ hashAllFields SANG hashAllFieldsDebug
                // THÊM LOGGING CHO HASH CALCULATION
                System.out.println("Calculated hash: " + signValue);
                System.out.println("Hash comparison result: " + signValue.equals(vnp_SecureHash));

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

                System.out.println("=== PAYMENT AMOUNT DEBUG ===");
                System.out.println("vnp_Amount raw: " + vnp_Amount);
                System.out.println("vnp_Amount parsed: " + vnpAmountLong);
                System.out.println("Final amount (VND): " + amount);
                System.out.println("Booking ID: " + bookingId);

                // Add signature validation check
                // Add signature validation check
                if (!isValidSignature) {
                    System.out.println("⚠️ SIGNATURE VALIDATION FAILED - PROCEEDING FOR TESTING");
                    System.out.println("Expected: " + signValue);
                    System.out.println("Received: " + vnp_SecureHash);

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
                    System.out.println("Payment failed with response code: " + vnp_ResponseCode + " for booking: " + bookingId);
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
                    System.out.println("Payment already exists for booking: " + bookingId);
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
                payment.setBookingId(bookingId);
                payment.setAmount(amount);
                payment.setStatus("Completed");
                payment.setPaidAt(new Timestamp(System.currentTimeMillis()));
                VNPayPaymentDAO dao = new VNPayPaymentDAO();
                int paymentId = dao.createPayment(payment);

                // Add transaction info saving
                if (paymentId > 0) {
                    dao.createTransaction(paymentId, vnp_TxnRef, vnp_TransactionNo, vnp_BankCode, vnp_PayDate);
                    System.out.println("Payment and transaction saved successfully for Customer booking: " + bookingId);
                    // THÊM LOGGING CHI TIẾT VÀ TẠO TRANSACTION
                    System.out.println("=== PAYMENT SUCCESS DETAILS ===");
                    System.out.println("Booking ID: " + bookingId);
                    System.out.println("Payment ID: " + paymentId);
                    System.out.println("Amount: " + amount);
                    System.out.println("Transaction No: " + vnp_TransactionNo);
                    System.out.println("Bank Code: " + vnp_BankCode);

                    // TẠO VNPAY TRANSACTION RECORD
                    if (paymentId > 0) {
                        // CHỈ GỌI createTransactionComplete với đầy đủ thông tin
                        dao.createTransactionComplete(paymentId, vnp_TxnRef, vnp_TransactionNo,
                                vnp_ResponseCode, Double.parseDouble(vnp_Amount), vnp_BankCode, vnp_SecureHash, vnp_PayDate);
                        System.out.println("VNPayTransaction created successfully with amount: " + vnp_Amount);
                    }
                    System.out.println("VNPayTransaction created successfully");
                }

                BookingDAO bookingDAO = new BookingDAO();
                bookingDAO.updateBookingStatus(bookingId, "Paid");
                bookingDAO.updateBookingPaymentStatus(bookingId, "Paid");

                // THÊM LOGGING CHO DATABASE UPDATE
                System.out.println("Booking status updated to Paid for booking: " + bookingId);

                bookingDAO.updateBookingStatus(bookingId, "Paid");
                bookingDAO.updateBookingPaymentStatus(bookingId, "Paid");
                request.setAttribute("vnp_TxnRef", vnp_TxnRef);
                request.setAttribute("vnp_TransactionNo", vnp_TransactionNo);
                request.setAttribute("vnp_BankCode", vnp_BankCode);
                request.setAttribute("vnp_ResponseCode", vnp_ResponseCode);
                request.setAttribute("vnp_PayDate", vnp_PayDate);
                request.setAttribute("vnp_Amount", amount);
                request.setAttribute("vnp_OrderInfo", vnp_OrderInfo);
                request.setAttribute("status", "SUCCESS");

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
}
