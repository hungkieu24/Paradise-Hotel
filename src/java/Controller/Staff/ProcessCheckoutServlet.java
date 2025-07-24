package Controller.Staff;

import Dal.BookingDAO;
import Dal.ServiceDAO;
import Dal.VNPayPaymentDAO;
import Dal.InvoiceDAO;
import Dal.LoyaltyPointDAO;
import Dal.RoomDAO;
import Model.UserAccount;
import Model.Booking;
import Model.VNPayPayment;
import Model.Invoice;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name="ProcessCheckoutServlet", urlPatterns={"/process-checkout"})
public class ProcessCheckoutServlet extends HttpServlet {
    private final BookingDAO bookingDAO = new BookingDAO();
    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final VNPayPaymentDAO paymentDAO = new VNPayPaymentDAO();
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final LoyaltyPointDAO loyaltyPointDAO = new LoyaltyPointDAO();
    private final RoomDAO roomDAO = new RoomDAO();

    @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check session and role
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        UserAccount staffUser = (UserAccount) session.getAttribute("user");
        String userRole = staffUser.getRole(); 

        String bookingIdParam = request.getParameter("bookingId");
        String paymentMethod = request.getParameter("paymentMethod");
        String amountToPayParam = request.getParameter("amountToPay");
        
        try {
            if (bookingIdParam == null || bookingIdParam.trim().isEmpty()) {
                response.sendRedirect("staff-checkout?bookingId=&error=invalid_booking");
                return;
            }

            int bookingId = Integer.parseInt(bookingIdParam);
            double amountToPay = Double.parseDouble(amountToPayParam);
            
            // Get booking information
            Booking booking = bookingDAO.getBookingById(bookingId);
            
            if (booking == null) {
                response.sendRedirect("staff-checkout?bookingId=" + bookingId + "&error=booking_not_found");
                return;
            }

            // Security check
            if (booking.getBranchId() != staffUser.getBranchId()) {
                response.sendRedirect("staff-checkout?bookingId=" + bookingId + "&error=permission_denied");
                return;
            }

            // Check booking status
            if (!"CheckedIn".equals(booking.getStatus())) {
                response.sendRedirect("staff-checkout?bookingId=" + bookingId + "&error=invalid_status");
                return;
            }

            // Process payment
            boolean paymentSuccess = processPayment(booking, amountToPay, paymentMethod, staffUser);
            
            if (paymentSuccess) {
                // Update booking status to Completed (not just CheckedOut)
                booking.setStatus("Completed");
                booking.setPaymentStatus("Paid");
                booking.setTotalPrice(amountToPay);
                
                // Update booking in database
                boolean bookingUpdated = bookingDAO.updateBookingAfterCheckout(booking);
                
                if (bookingUpdated) {
                    // Mark all unpaid services as paid
                    serviceDAO.markAllServicesAsPaid(bookingId);

                    // Set assigned rooms back to Available status
//                    roomDAO.updateRoomStatusAfterCheckout(bookingId, "Available");

                    // Award loyalty points (1 point per 100,000 VND)
//                    awardLoyaltyPoints(booking.getUserId(), amountToPay);

                    // Generate invoice
                    generateInvoice(booking, amountToPay);
          
                    // Redirect to success page
                    response.sendRedirect("checkout-success?bookingId=" + bookingId + 
                        "&amount=" + amountToPay + "&method=" + paymentMethod + "&status=completed");
                } else {
                    response.sendRedirect("staff-checkout?bookingId=" + bookingId + "&error=update_failed");
                }
            } else {
                response.sendRedirect("staff-checkout?bookingId=" + bookingId + "&error=payment_failed");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect("staff-checkout?bookingId=" + bookingIdParam + "&error=invalid_format");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("staff-checkout?bookingId=" + bookingIdParam + "&error=system_error");
        }
    }

    private boolean processPayment(Booking booking, double amount, String paymentMethod, UserAccount staff) {
        try {
            // Create payment record with staff info
            VNPayPayment payment = new VNPayPayment();
            payment.setBookingId(booking.getId());
            payment.setAmount(amount);
            payment.setStatus("Completed");
            payment.setPaidAt(new Timestamp(new Date().getTime()));
            
            // Save payment
            int paymentId = paymentDAO.createPayment(payment);
            
            if (paymentId > 0) {
                // Create transaction record with staff info
                String txnRef = "CHECKOUT_" + booking.getId() + "_" + System.currentTimeMillis();
                String staffNote = "Processed by: " + staff.getUsername() + " (" + staff.getId() + ")";
                
                paymentDAO.createTransactionWithStaffInfo(paymentId, txnRef, paymentMethod.toUpperCase(), 
                    amount, staffNote);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void awardLoyaltyPoints(String userId, double amount) {
        try {
            int pointsToAward = (int) (amount / 100000);
            if (pointsToAward > 0) {
                loyaltyPointDAO.addPoints(userId, pointsToAward, "Booking completion reward - Checkout payment");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateInvoice(Booking booking, double amount) {
        try {
            Invoice invoice = new Invoice();
            invoice.setBookingId(booking.getId());
            invoice.setTotalAmount(amount);
            invoice.setIssuedAt(new Timestamp(new Date().getTime()));
            
            invoiceDAO.createInvoice(invoice);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}