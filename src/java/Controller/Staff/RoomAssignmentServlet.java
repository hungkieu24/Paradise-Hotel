package Controller;

import Dal.BookingDAO;
import Dal.BookingRoomTypeDAO;

import Dal.RoomDAO;
import Model.Booking;
import Model.BookingRoomType;
import Model.Room;
import Model.UserAccount;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "RoomAssignmentServlet", urlPatterns = {"/staff/room-assignment"})
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
        
        // For each booking, check if all rooms are assigned
        Map<Integer, Boolean> fullyAssignedMap = new HashMap<>();
        
        for (Booking booking : pendingBookings) {
            int bookingId = booking.getId();
            boolean isFullyAssigned = bookingDAO.areAllRoomsAssigned(bookingId);
            fullyAssignedMap.put(bookingId, isFullyAssigned);
        }
        
        // Pass data to JSP
        request.setAttribute("pendingBookings", pendingBookings);
        request.setAttribute("fullyAssignedMap", fullyAssignedMap);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/staff/room-assignment-list.jsp");
        dispatcher.forward(request, response);
    }
    
    private void showAssignRoomPage(HttpServletRequest request, HttpServletResponse response, int branchId)
            throws ServletException, IOException {
        
        int bookingId = Integer.parseInt(request.getParameter("bookingId"));
        
        // Get booking details
        BookingDAO bookingDAO = new BookingDAO();
        Booking booking = bookingDAO.getBookingById(bookingId);
        
        if (booking == null || booking.getBranchId() != branchId) {
            request.setAttribute("errorMessage", "Booking not found or you don't have permission to access it");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/staff/room-assignment-list.jsp");
            dispatcher.forward(request, response);
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
        
        for (Room room : assignedRooms) {
            int roomTypeId = room.getRoomTypeId();
            assignmentCounts.put(roomTypeId, assignmentCounts.getOrDefault(roomTypeId, 0) + 1);
        }
        
        // Get remaining rooms to assign
        Map<Integer, Integer> remainingRoomQuantities = brtDAO.getRemainingRoomQuantities(bookingId);
        
        // Get available rooms for each room type
        Map<Integer, List<Room>> availableRoomsByType = new HashMap<>();
        
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
        
        // Check if booking is fully assigned
        boolean isFullyAssigned = bookingDAO.areAllRoomsAssigned(bookingId);
        
        // Pass data to JSP
        request.setAttribute("booking", booking);
        request.setAttribute("bookingRoomTypes", bookingRoomTypes);
        request.setAttribute("assignedRooms", assignedRooms);
        request.setAttribute("assignmentCounts", assignmentCounts);
        request.setAttribute("remainingRoomQuantities", remainingRoomQuantities);
        request.setAttribute("availableRoomsByType", availableRoomsByType);
        request.setAttribute("isFullyAssigned", isFullyAssigned);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/staff/assign-rooms.jsp");
        dispatcher.forward(request, response);
    }
    
    private void assignRoom(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        int bookingId = Integer.parseInt(request.getParameter("bookingId"));
        int roomId = Integer.parseInt(request.getParameter("roomId"));
        
        BookingDAO bookingDAO = new BookingDAO();
        boolean success = bookingDAO.assignRoomsToBooking(bookingId, new String[]{String.valueOf(roomId)});
        
        response.sendRedirect(request.getContextPath() + 
            "/staff/room-assignment?action=assign&bookingId=" + bookingId + 
            (success ? "" : "&error=failed"));
    }
    
    private void removeRoomAssignment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        int bookingId = Integer.parseInt(request.getParameter("bookingId"));
        int roomId = Integer.parseInt(request.getParameter("roomId"));
        
        RoomDAO roomDAO = new RoomDAO();
        boolean success = roomDAO.removeRoomAssignment(bookingId, roomId);
        
        response.sendRedirect(request.getContextPath() + 
            "/staff/room-assignment?action=assign&bookingId=" + bookingId + 
            (success ? "" : "&error=remove-failed"));
    }
    
    private void completeAssignment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        int bookingId = Integer.parseInt(request.getParameter("bookingId"));
        
        BookingDAO bookingDAO = new BookingDAO();
        
        // Verify all rooms are assigned
        boolean isFullyAssigned = bookingDAO.areAllRoomsAssigned(bookingId);
        
        if (isFullyAssigned) {
            // Get current booking details
            Booking booking = bookingDAO.getBookingById(bookingId);
            
            // Update booking status if it's currently 'Paid' (if it's already CheckedIn, keep it that way)
            if ("Paid".equals(booking.getStatus())) {
                bookingDAO.updateBookingStatus(bookingId, "CheckedIn");
            }
            
            // Add success message to session for redirect
            HttpSession session = request.getSession();
            session.setAttribute("successMessage", "Room assignment completed successfully. The booking is ready for check-in.");
            
            response.sendRedirect(request.getContextPath() + "/staff/room-assignment?success=completion");
        } else {
            response.sendRedirect(request.getContextPath() + 
                "/staff/room-assignment?action=assign&bookingId=" + bookingId + "&error=incomplete");
        }
    }
}