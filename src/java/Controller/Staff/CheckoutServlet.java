package Controller.Staff;

import Dal.BookingDAO;
import Dal.UserAccountDAO;
import Dal.RoomDAO;
import Dal.ServiceDAO;
import Dal.LoyaltyPointDAO;
import Model.UserAccount;
import Model.Booking;
import Model.Room;
import Model.Service;
import Model.BookingRoomType;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@WebServlet(name = "CheckoutServlet", urlPatterns = {"/staff-checkout"})
public class CheckoutServlet extends HttpServlet {

    private final BookingDAO bookingDAO = new BookingDAO();
    private final UserAccountDAO userAccountDAO = new UserAccountDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final LoyaltyPointDAO loyaltyPointDAO = new LoyaltyPointDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check session and role
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        UserAccount staffUser = (UserAccount) session.getAttribute("user");
        String userRole = staffUser.getRole();

        if (!"Staff".equalsIgnoreCase(userRole) && !"Manager".equalsIgnoreCase(userRole) && !"Admin".equalsIgnoreCase(userRole)) {
            response.sendRedirect("access-denied.jsp");
            return;
        }
        String bookingIdParam = request.getParameter("bookingId");
        String errorMessage = null;

        try {
            if (bookingIdParam == null || bookingIdParam.trim().isEmpty()) {
                errorMessage = "Booking ID is required";
                request.setAttribute("errorMessage", errorMessage);
                request.getRequestDispatcher("staff-checkout.jsp").forward(request, response);
                return;
            }
            int bookingId = Integer.parseInt(bookingIdParam);

            // Get booking information
            Booking booking = bookingDAO.getBookingById(bookingId);

            if (booking == null) {
                errorMessage = "Booking not found";
                request.setAttribute("errorMessage", errorMessage);
                request.getRequestDispatcher("staff-checkout.jsp").forward(request, response);
                return;
            }

            // Security check
            if (booking.getBranchId() != staffUser.getBranchId()) {
                errorMessage = "You don't have permission to checkout this booking";
                request.setAttribute("errorMessage", errorMessage);
                request.getRequestDispatcher("staff-checkout.jsp").forward(request, response);
                return;
            }

            // Check if booking can be checked out
            if (!"CheckedIn".equals(booking.getStatus())) {
                errorMessage = "Only checked-in bookings can be checked out. Current status: " + booking.getStatus();
                request.setAttribute("errorMessage", errorMessage);
                request.getRequestDispatcher("staff-checkout.jsp").forward(request, response);
                return;
            }

            // Get customer information
            UserAccount customer = userAccountDAO.getUserInfoById(booking.getUserId());
            if (customer != null) {
                String customerRank = loyaltyPointDAO.getRankByUserId(customer.getId());
                customer.setRank(customerRank != null ? customerRank : "Member");
            }

            // Get booking room types
            List<BookingRoomType> bookingRoomTypes = bookingDAO.getBookingRoomTypesByBookingId(bookingId);

            // ==== SET SỐ ĐÊM CHO TỪNG BookingRoomType ====
            if (bookingRoomTypes != null && booking != null) {
                LocalDateTime checkIn = booking.getCheckIn() != null ? booking.getCheckIn().toLocalDateTime() : null;
                LocalDateTime checkOut = booking.getCheckOut() != null ? booking.getCheckOut().toLocalDateTime() : null;
                int numberOfNights = 1;
                try {
                    if (checkIn != null && checkOut != null) {
                        numberOfNights = (int) calculateNumberOfNights(checkIn, checkOut);
                    }
                } catch (Exception ex) {
                    numberOfNights = 1; 
                }
                for (BookingRoomType brt : bookingRoomTypes) {
                    brt.setNumberOfNights(numberOfNights);
                }
            }

            // Get assigned rooms
            List<Room> assignedRooms = roomDAO.getAssignedRoomsByBookingId(bookingId);

            // Get all services (paid and unpaid)
            List<Service> allServices = serviceDAO.getServicesByBookingId(bookingId);

            // Calculate checkout details
            Map<String, Object> checkoutDetails = calculateCheckoutDetails(booking, bookingRoomTypes, allServices, customer);

            // Set attributes
            request.setAttribute("booking", booking);
            request.setAttribute("customer", customer);
            request.setAttribute("bookingRoomTypes", bookingRoomTypes);
            request.setAttribute("assignedRooms", assignedRooms);
            request.setAttribute("allServices", allServices);
            request.setAttribute("checkoutDetails", checkoutDetails);

            // Check for error messages from redirect
            String error = request.getParameter("error");
            if ("payment_failed".equals(error)) {
                errorMessage = "Payment processing failed. Please try again.";
            } else if ("system_error".equals(error)) {
                errorMessage = "System error occurred. Please try again.";
            } else if ("booking_not_found".equals(error)) {
                errorMessage = "Booking not found.";
            } else if ("permission_denied".equals(error)) {
                errorMessage = "You don't have permission to access this booking.";
            } else if ("invalid_status".equals(error)) {
                errorMessage = "Booking status is invalid for checkout.";
            } else if ("update_failed".equals(error)) {
                errorMessage = "Failed to update booking status.";
            }

            request.setAttribute("errorMessage", errorMessage);

        } catch (NumberFormatException e) {
            errorMessage = "Invalid booking ID format";
            request.setAttribute("errorMessage", errorMessage);
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "An error occurred while preparing checkout: " + e.getMessage();
            request.setAttribute("errorMessage", errorMessage);
        }

        request.getRequestDispatcher("staff-checkout.jsp").forward(request, response);
    }

    private Map<String, Object> calculateCheckoutDetails(Booking booking,
            List<BookingRoomType> bookingRoomTypes, List<Service> services, UserAccount customer) {
        Map<String, Object> details = new HashMap<>();

        try {
            BigDecimal totalRoomPriceBD = BigDecimal.ZERO;
            if (bookingRoomTypes != null && !bookingRoomTypes.isEmpty()) {
                for (BookingRoomType brt : bookingRoomTypes) {
                    // getTotalPrice() sẽ tính: pricePerNight * quantity * numberOfNights
                    BigDecimal roomSubtotal = brt.getTotalPrice();
                    if (roomSubtotal != null) {
                        totalRoomPriceBD = totalRoomPriceBD.add(roomSubtotal);
                    }
                }
            }

            double totalRoomPrice = totalRoomPriceBD.doubleValue();

            // Calculate service totals
            double totalServicePrice = 0.0;
            double paidServicePrice = 0.0;
            double unpaidServicePrice = 0.0;

            if (services != null && !services.isEmpty()) {
                for (Service service : services) {
                    double serviceTotal = service.getPrice() * service.getQuantity();
                    totalServicePrice += serviceTotal;

                    String status = service.getBookingServiceStatus();
                    if (status != null && "Paid".equalsIgnoreCase(status.trim())) {
                        paidServicePrice += serviceTotal;
                    } else {
                        unpaidServicePrice += serviceTotal;
                    }
                }
            }

            // Calculate rank discount
            double rankDiscountPercent = 0.0;
            if (customer != null && customer.getRank() != null) {
                String rank = customer.getRank().toLowerCase().trim();
                switch (rank) {
                    case "silver":
                        rankDiscountPercent = 5.0;
                        break;
                    case "gold":
                        rankDiscountPercent = 10.0;
                        break;
                    case "vip":
                        rankDiscountPercent = 15.0;
                        break;
                    default:
                        rankDiscountPercent = 0.0;
                        break;
                }
            }

            // Apply discount only to unpaid amount
            double discountableAmount = totalRoomPrice + unpaidServicePrice;
            double rankDiscount = discountableAmount * (rankDiscountPercent / 100.0);

            // Calculate what customer needs to pay now
            double amountToPay = Math.max(0, discountableAmount - rankDiscount);

            // Total bill (including already paid services)
            double grandTotal = totalRoomPrice + totalServicePrice - rankDiscount;

            // Store all calculated values
            details.put("totalRoomPrice", totalRoomPrice);
            details.put("totalServicePrice", totalServicePrice);
            details.put("paidServicePrice", paidServicePrice);
            details.put("unpaidServicePrice", unpaidServicePrice);
            details.put("rankDiscountPercent", rankDiscountPercent);
            details.put("rankDiscount", rankDiscount);
            details.put("amountToPay", amountToPay);
            details.put("grandTotal", grandTotal);
            details.put("voucherDiscount", 0.0);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in calculateCheckoutDetails: " + e.getMessage());

            // Set default values in case of error
            details.put("totalRoomPrice", 0.0);
            details.put("totalServicePrice", 0.0);
            details.put("paidServicePrice", 0.0);
            details.put("unpaidServicePrice", 0.0);
            details.put("rankDiscountPercent", 0.0);
            details.put("rankDiscount", 0.0);
            details.put("amountToPay", 0.0);
            details.put("grandTotal", 0.0);
            details.put("voucherDiscount", 0.0);
        }

        return details;
    }

    private long calculateNumberOfNights(LocalDateTime checkIn, LocalDateTime checkOut) {
        final int STANDARD_CHECKIN_HOUR = 7;  // 7:00 AM
        final int STANDARD_CHECKOUT_HOUR = 14; // 2:00 PM

        LocalDate checkInDate = checkIn.toLocalDate();
        LocalDate checkOutDate = checkOut.toLocalDate();

        long daysBetween = ChronoUnit.DAYS.between(checkInDate, checkOutDate);

        if (daysBetween == 0) {
            return 1;
        } else if (daysBetween == 1) {
            if (checkOut.getHour() < STANDARD_CHECKOUT_HOUR) {
                long totalHours = ChronoUnit.HOURS.between(checkIn, checkOut);
                if (totalHours < 12) {
                    return 1;
                }
            }
            return 1;
        } else {
            return daysBetween;
        }
    }
}
