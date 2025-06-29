package Controller;

import Dal.BookingDAO;
import Dal.BookingRoomTypeDAO;
import Dal.BookingServiceDAO;
import Dal.RoomDAO;
import Dal.ServiceDAO;
import Model.Booking;
import Model.BookingRoomType;
import Model.BookingService;
import Model.Room;
import Model.Service;
import Model.UserAccount;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "RoomAssignmentServlet", urlPatterns = {"/staff-room-assignment"})
public class RoomAssignmentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verify user is logged in and is staff/manager
        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");
        
        if (user == null || !("Staff".equals(user.getRole()) || "Manager".equals(user.getRole()))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        
        switch (action) {
            case "list":
                listPendingBookings(request, response, user.getBranchId());
                break;
            case "assign":
                showAssignRoomPage(request, response, user.getBranchId());
                break;
            case "services":
                showServicesPage(request, response, user.getBranchId());
                break;
            default:
                listPendingBookings(request, response, user.getBranchId());
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verify user is logged in and is staff/manager
        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");
        
        if (user == null || !("Staff".equals(user.getRole()) || "Manager".equals(user.getRole()))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        String action = request.getParameter("action");
        
        switch (action) {
            case "assign-room":
                assignRoom(request, response);
                break;
            case "remove-assignment":
                removeRoomAssignment(request, response);
                break;
            case "complete-assignment":
                completeAssignment(request, response);
                break;
            case "add-service":
                addServiceToBooking(request, response);
                break;
            case "remove-service":
                removeServiceFromBooking(request, response);
                break;
            default:
                listPendingBookings(request, response, user.getBranchId());
                break;
        }
    }
    
    private void listPendingBookings(HttpServletRequest request, HttpServletResponse response, int branchId)
            throws ServletException, IOException {
        
        // Get bookings with 'Pending' or 'Paid' status
        BookingDAO bookingDAO = new BookingDAO();
        List<Booking> pendingBookings = bookingDAO.getPendingBookingsForBranch(branchId);
        
        // DEBUG INFO
        System.out.println("Branch ID: " + branchId);
        System.out.println("Total bookings found: " + (pendingBookings != null ? pendingBookings.size() : "null"));
        if (pendingBookings != null && !pendingBookings.isEmpty()) {
            System.out.println("First booking ID: " + pendingBookings.get(0).getId());
            System.out.println("First booking status: " + pendingBookings.get(0).getStatus());
        }
        
        // For each booking, check if all rooms are assigned
        Map<Integer, Boolean> fullyAssignedMap = new HashMap<>();
        
        if (pendingBookings != null) {
            for (Booking booking : pendingBookings) {
                int bookingId = booking.getId();
                boolean isFullyAssigned = bookingDAO.areAllRoomsAssigned(bookingId);
                fullyAssignedMap.put(bookingId, isFullyAssigned);
            }
        }
        
        // Pass data to JSP
        request.setAttribute("pendingBookings", pendingBookings);
        request.setAttribute("fullyAssignedMap", fullyAssignedMap);
        
        // Check if there's an info message in the session
        HttpSession session = request.getSession();
        if (session.getAttribute("infoMessage") != null) {
            // Pass the info message to the request attributes
            request.setAttribute("infoMessage", session.getAttribute("infoMessage"));
            // Clear it from the session
            session.removeAttribute("infoMessage");
        }
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/staff-room-assignment-list.jsp");
        dispatcher.forward(request, response);
    }
    
    private void showAssignRoomPage(HttpServletRequest request, HttpServletResponse response, int branchId)
            throws ServletException, IOException {
        
        int bookingId = Integer.parseInt(request.getParameter("bookingId"));
        
        // Get booking details
        BookingDAO bookingDAO = new BookingDAO();
        Booking booking = bookingDAO.getBookingById(bookingId);
        
        if (booking == null || booking.getBranchId() != branchId) {
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "Booking not found or you don't have permission to access it");
            response.sendRedirect(request.getContextPath() + "/staff-room-assignment");
            return;
        }
        
        // Get room types booked
        BookingRoomTypeDAO brtDAO = new BookingRoomTypeDAO();
        List<BookingRoomType> bookingRoomTypes = brtDAO.getBookingRoomTypesByBookingId(bookingId);
        
        // Get assigned rooms
        RoomDAO roomDAO = new RoomDAO();
        List<Room> assignedRooms = roomDAO.getAssignedRoomsByBookingId(bookingId);
        
        // Get room assignment counts by room type
        Map<Integer, Integer> assignmentCounts = new HashMap<>();
        
        if (assignedRooms != null) {
            for (Room room : assignedRooms) {
                int roomTypeId = room.getRoomTypeId();
                assignmentCounts.put(roomTypeId, assignmentCounts.getOrDefault(roomTypeId, 0) + 1);
            }
        }
        
        // Get remaining rooms to assign
        Map<Integer, Integer> remainingRoomQuantities = brtDAO.getRemainingRoomQuantities(bookingId);
        
        // Get available rooms for each room type
        Map<Integer, List<Room>> availableRoomsByType = new HashMap<>();
        
        if (bookingRoomTypes != null) {
            for (BookingRoomType brt : bookingRoomTypes) {
                int roomTypeId = brt.getRoomTypeId();
                int remaining = remainingRoomQuantities.getOrDefault(roomTypeId, brt.getQuantity());
                
                if (remaining > 0) {
                    // Get available rooms for this room type
                    List<Room> availableRooms = roomDAO.getAvailableRoomsForAssignment(
                        roomTypeId, 
                        booking.getBranchId(),
                        bookingId
                    );
                    
                    availableRoomsByType.put(roomTypeId, availableRooms);
                }
            }
        }
        
        // Check if booking is fully assigned
        boolean isFullyAssigned = bookingDAO.areAllRoomsAssigned(bookingId);
        
        // Get booking services
        BookingServiceDAO bookingServiceDAO = new BookingServiceDAO();
        List<BookingService> bookingServices = bookingServiceDAO.getBookingServicesByBookingId(bookingId);
        
        // Pass data to JSP
        request.setAttribute("booking", booking);
        request.setAttribute("bookingRoomTypes", bookingRoomTypes);
        request.setAttribute("assignedRooms", assignedRooms);
        request.setAttribute("assignmentCounts", assignmentCounts);
        request.setAttribute("remainingRoomQuantities", remainingRoomQuantities);
        request.setAttribute("availableRoomsByType", availableRoomsByType);
        request.setAttribute("isFullyAssigned", isFullyAssigned);
        request.setAttribute("bookingServices", bookingServices);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/staff-assign-rooms.jsp");
        dispatcher.forward(request, response);
    }
    
    private void showServicesPage(HttpServletRequest request, HttpServletResponse response, int branchId)
            throws ServletException, IOException {
        
        int bookingId = Integer.parseInt(request.getParameter("bookingId"));
        
        // Get booking details
        BookingDAO bookingDAO = new BookingDAO();
        Booking booking = bookingDAO.getBookingById(bookingId);
        
        if (booking == null || booking.getBranchId() != branchId) {
            HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "Booking not found or you don't have permission to access it");
            response.sendRedirect(request.getContextPath() + "/staff-room-assignment");
            return;
        }
        
        // Get available services for this branch
        ServiceDAO serviceDAO = new ServiceDAO();
        List<Service> availableServices = serviceDAO.getActiveServicesByBranch(branchId);
        
        // Get services already added to booking
        BookingServiceDAO bookingServiceDAO = new BookingServiceDAO();
        List<BookingService> bookingServices = bookingServiceDAO.getBookingServicesByBookingId(bookingId);
        
        // Pass data to JSP
        request.setAttribute("booking", booking);
        request.setAttribute("availableServices", availableServices);
        request.setAttribute("bookingServices", bookingServices);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/staff-booking-services.jsp");
        dispatcher.forward(request, response);
    }
    
    private void assignRoom(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        int bookingId = Integer.parseInt(request.getParameter("bookingId"));
        int roomId = Integer.parseInt(request.getParameter("roomId"));
        
        BookingDAO bookingDAO = new BookingDAO();
        boolean success = bookingDAO.assignRoomsToBooking(bookingId, new String[]{String.valueOf(roomId)});
        
        System.out.println("Assigning room ID " + roomId + " to booking " + bookingId + ": " + (success ? "SUCCESS" : "FAILED"));
        
        response.sendRedirect(request.getContextPath() + 
            "/staff-room-assignment?action=assign&bookingId=" + bookingId + 
            (success ? "" : "&error=failed"));
    }
    
    private void removeRoomAssignment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        int bookingId = Integer.parseInt(request.getParameter("bookingId"));
        int roomId = Integer.parseInt(request.getParameter("roomId"));
        
        RoomDAO roomDAO = new RoomDAO();
        boolean success = roomDAO.removeRoomAssignment(bookingId, roomId);
        
        System.out.println("Removing room ID " + roomId + " from booking " + bookingId + ": " + (success ? "SUCCESS" : "FAILED"));
        
        response.sendRedirect(request.getContextPath() + 
            "/staff-room-assignment?action=assign&bookingId=" + bookingId + 
            (success ? "" : "&error=remove-failed"));
    }
    
    private void completeAssignment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        int bookingId = Integer.parseInt(request.getParameter("bookingId"));
        HttpSession session = request.getSession();
        
        BookingDAO bookingDAO = new BookingDAO();
        
        // Verify all rooms are assigned
        boolean isFullyAssigned = bookingDAO.areAllRoomsAssigned(bookingId);
        
        System.out.println("Completing assignment for booking " + bookingId + ". Is fully assigned: " + isFullyAssigned);
        
        if (isFullyAssigned) {
            // Get current booking details
            Booking booking = bookingDAO.getBookingById(bookingId);
            
            // Update booking status if it's currently 'Paid' (if it's already CheckedIn, keep it that way)
            if ("Paid".equals(booking.getStatus())) {
                boolean updated = bookingDAO.updateBookingStatus(bookingId, "CheckedIn");
                System.out.println("Updating booking status to CheckedIn: " + (updated ? "SUCCESS" : "FAILED"));
            }
            
            // Add success message to session for redirect
            session.setAttribute("successMessage", "Room assignment completed successfully. The booking is ready for check-in.");
            
            response.sendRedirect(request.getContextPath() + "/staff-room-assignment?success=completion");
        } else {
            session.setAttribute("errorMessage", "Cannot complete assignment. Not all rooms have been assigned yet.");
            response.sendRedirect(request.getContextPath() + 
                "/staff-room-assignment?action=assign&bookingId=" + bookingId);
        }
    }
    
    private void addServiceToBooking(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        int bookingId = Integer.parseInt(request.getParameter("bookingId"));
        int serviceId = Integer.parseInt(request.getParameter("serviceId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        String paidStatus = request.getParameter("paidStatus") != null ? 
                request.getParameter("paidStatus") : "Unpaid";
        
        BookingService bookingService = new BookingService();
        bookingService.setBookingId(bookingId);
        bookingService.setServiceId(serviceId);
        bookingService.setQuantity(quantity);
        bookingService.setPaidStatus(paidStatus);
        
        BookingServiceDAO bookingServiceDAO = new BookingServiceDAO();
        boolean success = bookingServiceDAO.addOrUpdateServiceToBooking(bookingService);
        
        System.out.println("Adding service ID " + serviceId + " to booking " + bookingId + ": " + (success ? "SUCCESS" : "FAILED"));
        
        // Check if redirect back to services page or room assignment page
        String redirectAction = request.getParameter("redirectTo");
        if ("services".equals(redirectAction)) {
            response.sendRedirect(request.getContextPath() + 
                "/staff-room-assignment?action=services&bookingId=" + bookingId + 
                (success ? "&success=service-added" : "&error=service-add-failed"));
        } else {
            response.sendRedirect(request.getContextPath() + 
                "/staff-room-assignment?action=assign&bookingId=" + bookingId + 
                (success ? "&success=service-added" : "&error=service-add-failed"));
        }
    }
    
    private void removeServiceFromBooking(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        int bookingId = Integer.parseInt(request.getParameter("bookingId"));
        int serviceId = Integer.parseInt(request.getParameter("serviceId"));
        
        BookingServiceDAO bookingServiceDAO = new BookingServiceDAO();
        boolean success = bookingServiceDAO.removeServiceFromBooking(bookingId, serviceId);
        
        System.out.println("Removing service ID " + serviceId + " from booking " + bookingId + ": " + (success ? "SUCCESS" : "FAILED"));
        
        // Check if redirect back to services page or room assignment page
        String redirectAction = request.getParameter("redirectTo");
        if ("services".equals(redirectAction)) {
            response.sendRedirect(request.getContextPath() + 
                "/staff-room-assignment?action=services&bookingId=" + bookingId + 
                (success ? "&success=service-removed" : "&error=service-remove-failed"));
        } else {
            response.sendRedirect(request.getContextPath() + 
                "/staff-room-assignment?action=assign&bookingId=" + bookingId + 
                (success ? "&success=service-removed" : "&error=service-remove-failed"));
        }
    }
}