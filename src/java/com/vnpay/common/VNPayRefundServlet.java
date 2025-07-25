package com.vnpay.common;

import Dal.BookingDAO;
import Dal.LoyaltyPointDAO;
import Dal.VNPayPaymentDAO;
import Dal.VNPayPaymentDAO.RefundCalculationResult;
import Dal.WalletDAO;
import Dal.WalletTransactionDAO;
import Model.Booking;
import Model.UserAccount;
import Model.Wallet;
import Model.WalletTransaction;
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
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@WebServlet("/vnpay-refund")
public class VNPayRefundServlet extends HttpServlet {

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
            String confirmLateCancel = request.getParameter("confirmLateCancel");

            if (bookingIdStr == null || amountStr == null) {
                session.setAttribute("refundMsg", "Missing necessary information!");
                response.sendRedirect("myBooking");
                return;
            }

            int bookingId = Integer.parseInt(bookingIdStr);
            double originalAmount = Double.parseDouble(amountStr);

            // 1. Kiểm tra booking có thể refund không
            BookingDAO bookingDAO = new BookingDAO();
            if (!bookingDAO.isBookingRefundable(bookingId)) {
                session.setAttribute("refundMsg", "Booking is non-refundable!");
                response.sendRedirect("myBooking");
                return;
            }

            // 2. Kiểm tra đã refund chưa
            VNPayPaymentDAO paymentDAO = new VNPayPaymentDAO();
            if (paymentDAO.isAlreadyRefunded(bookingId)) {
                session.setAttribute("refundMsg", "Booking has been refunded!");
                response.sendRedirect("myBooking");
                return;
            }

            // 3. Tính toán refund amount dựa trên thời gian
            RefundCalculationResult refundResult = paymentDAO.calculateRefundAmount(bookingId, originalAmount);

            if (refundResult.refundPercentage == 0 && !"true".equals(confirmLateCancel)) {
                // Nếu không được hoàn tiền và chưa confirm, hiển thị modal xác nhận
                session.setAttribute("lateCancelBookingId", bookingId);
                session.setAttribute("lateCancelAmount", originalAmount);
                session.setAttribute("lateCancelReason", refundReason);
                session.setAttribute("refundMsg", "LATE_CANCEL_CONFIRM");
                response.sendRedirect("myBooking");
                return;
            }

            if (refundResult.refundPercentage == 0 && "true".equals(confirmLateCancel)) {
                // User confirmed late cancellation - cancel without refund
                boolean cancelled = bookingDAO.cancelBookingWithoutRefund(bookingId,
                        "Cancelled after 2-day refund period: " + (refundReason != null ? refundReason : "No reason provided"));

                if (cancelled) {
                    session.setAttribute("refundMsg", "Booking has been successfully canceled. Because it has been more than 2 days since payment, no refund is available.");
                } else {
                    session.setAttribute("refundMsg", "Error when canceling booking!");
                }
                response.sendRedirect("myBooking");
                return;
            }

            // 4. Process wallet refund với amount đã tính toán
            processWalletRefund(bookingId, refundResult.refundAmount, refundResult.refundPercentage,
                              originalAmount, refundReason, user, session);

        } catch (Exception e) {
            System.err.println("Error processing refund: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("refundMsg", "System error while processing refund!");
        }

        response.sendRedirect("myBooking");
    }



    // Thay thế phần VNPay refund bằng wallet refund với tính toán phần trăm
    private void processWalletRefund(int bookingId, double refundAmount, int refundPercentage,
                                   double originalAmount, String refundReason, UserAccount user, HttpSession session) {
        try {
            // 1. Cập nhật wallet balance
            WalletDAO walletDAO = new WalletDAO();
            boolean walletUpdated = walletDAO.updateWalletBalance(user.getId(), refundAmount);

            if (walletUpdated) {
                // 2. Tạo transaction record
                WalletTransactionDAO transactionDAO = new WalletTransactionDAO();
                Wallet wallet = walletDAO.getWalletByUserId(user.getId());

                WalletTransaction transaction = new WalletTransaction();
                transaction.setWalletID(wallet.getWalletID());
                transaction.setTransactionType("Refund");
                transaction.setAmount(refundAmount);
                transaction.setDescription("Booking refund #" + bookingId + " (" + refundPercentage + "% of original) - " + refundReason);
                transaction.setStatus("Success");
                transaction.setBookingID(bookingId);
                transaction.setBankAccountID(0);
                transaction.setCreatedAt(new Timestamp(new Date().getTime()));

                transactionDAO.addWalletTransaction(transaction);

                // 3. Cập nhật VNPayPayment status thành Refunded
                VNPayPaymentDAO paymentDAO = new VNPayPaymentDAO();
                paymentDAO.updatePaymentStatus(bookingId, "Refunded");

                // 4. Cập nhật booking status và lấy số điểm bị trừ
                int pointsDeducted = updateRefundStatus(bookingId, refundAmount, refundReason);

                // 5. Thông báo thành công với thông tin chi tiết
                DecimalFormat formatter = new DecimalFormat("#,###");
                String formattedRefundAmount = formatter.format(refundAmount);
                String formattedOriginalAmount = formatter.format(originalAmount);

                String refundMessage = "✅ REFUND SUCCESSFULLY INTO WALLET<br><br>"
                        + "💰 Original Amount: " + formattedOriginalAmount + " VND<br>"
                        + "💰 Refund Amount: " + formattedRefundAmount + " VND (" + refundPercentage + "%)<br><br>"
                        + "📅 Time: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "<br><br>"
                        + "💳 Money has been added to your wallet<br><br>"
                        + "⭐ Points deducted: " + pointsDeducted + " points<br><br>";

                if (refundPercentage < 100) {
                    refundMessage += "ℹ️ Refund rate: " + refundPercentage + "% due to cancellation timing<br><br>";
                }

                refundMessage += "THANK YOU FOR USING OUR SERVICE!!!";

                session.setAttribute("refundMsg", refundMessage);
            } else {
                session.setAttribute("refundMsg", "❌ Error when depositing money into wallet!");
            }

        } catch (Exception e) {
            System.err.println("Error processing wallet refund: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("refundMsg", "❌ System error while processing refund!");
        }
    }

    // Cập nhật trạng thái refund và trừ điểm loyalty
    private int updateRefundStatus(int bookingId, double refundAmount, String reason) {
        int pointsDeducted = 0;
        try {
            BookingDAO bookingDAO = new BookingDAO();
            LoyaltyPointDAO loyaltyPointDAO = new LoyaltyPointDAO();

            // Update payment and booking status with refund amount
            boolean bookingUpdated = bookingDAO.updateBookingForRefund(bookingId, "Cancelled", "Refunded", reason, refundAmount);

            // Get booking info and subtract points based on original amount
            Booking booking = bookingDAO.getBookingById(bookingId);
            if (booking != null && booking.getUserId() != null) {
                // Tính số điểm sẽ bị trừ dựa trên số tiền gốc (không phải refund amount)
                double originalAmount = booking.getTotalPrice();
                pointsDeducted = (int) (originalAmount / 100000);

                boolean pointsDeductedSuccess = loyaltyPointDAO.subtractPointsForRefund(
                        booking.getUserId(),
                        originalAmount, // Trừ điểm dựa trên số tiền gốc
                        "Points deducted for refund - Booking #" + bookingId
                );

                if (pointsDeductedSuccess) {
                    loyaltyPointDAO.checkAndUpdateTier(booking.getUserId());
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Error updating refund status: " + e.getMessage());
            e.printStackTrace();
        }

        return pointsDeducted;
    }

    private void sendRefundConfirmationEmail(String userEmail, int bookingId, double amount, String transactionNo) {
        try {
            String subject = "Refund Confirmation Successful - Booking #" + bookingId;

            DecimalFormat formatter = new DecimalFormat("#,###");
            String formattedAmount = formatter.format(amount);
            String currentDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());

            String emailContent
                    = "<h2>✅ Refund successfully to wallet!</h2>"
                    + "<p><strong>Booking ID:</strong> #" + bookingId + "</p>"
                    + "<p><strong>Refund amount:</strong> " + formattedAmount + " VND</p>"
                    + "<p><strong>Refund to:</strong> E-wallet</p>"
                    + "<p><strong>Processing time:</strong> " + currentDate + "</p>"
                    + "<p style='color: #666;'>Money has been loaded into your wallet.</p>"
                    + "<p>Thank you for using the service!</p>";

            EmailUtility.sendRefundEmail(userEmail, subject, emailContent);

        } catch (Exception e) {
            System.err.println("❌ Failed to send refund email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
