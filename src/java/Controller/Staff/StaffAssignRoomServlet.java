package Controller.Staff;

import Dal.BookingDAO;
import Dal.RoomDAO;
import Dal.BookingRoomTypeDAO;
import Model.Booking;
import Model.Room;
import Model.BookingRoomType;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "StaffAssignRoomServlet", urlPatterns = {"/staff-assign-room", "/staff-assign-room-action"})
public class StaffAssignRoomServlet extends HttpServlet {
    
    private final BookingDAO bookingDAO = new BookingDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final BookingRoomTypeDAO bookingRoomTypeDAO = new BookingRoomTypeDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String bookingIdParam = request.getParameter("bookingId");
        
        try {
            if (bookingIdParam == null || bookingIdParam.trim().isEmpty()) {
                session.setAttribute("errorMessage", "Invalid booking ID.");
                response.sendRedirect("staff-bookings-list");
                return;
            }
            
            int bookingId = Integer.parseInt(bookingIdParam);
            loadAssignRoomPage(request, response, bookingId, null, null);
            
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid booking ID format.");
            response.sendRedirect("staff-bookings-list");
        } catch (Exception e) {
            session.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            response.sendRedirect("staff-bookings-list");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String bookingIdParam = request.getParameter("bookingId");
        String[] roomIdsParam = request.getParameterValues("roomIds");

        try {
            int bookingId = Integer.parseInt(bookingIdParam);

            // Get booking information
            Booking booking = bookingDAO.getBookingById(bookingId);
            if (booking == null) {
                session.setAttribute("errorMessage", "Booking not found!");
                response.sendRedirect("staff-bookings-list");
                return;
            }
        
            // Check if rooms selected
            if (roomIdsParam == null || roomIdsParam.length == 0) {
                loadAssignRoomPage(request, response, bookingId, 
                    "Please select at least one room to assign.", null);
                return;
            }

            // Get booking room types
            List<BookingRoomType> bookingRoomTypes = bookingRoomTypeDAO.getBookingRoomTypesByBookingId(bookingId);
            if (bookingRoomTypes == null || bookingRoomTypes.isEmpty()) {
                loadAssignRoomPage(request, response, bookingId, 
                    "No room types found for this booking.", null);
                return;
            }

            // Get already assigned rooms and calculate remaining
            List<Room> assignedRooms = roomDAO.getAssignedRoomsByBookingId(bookingId);
            Map<Integer, Integer> assignedCountByRoomType = calculateAssignedCounts(assignedRooms);
            Map<Integer, Integer> remainingByRoomType = calculateRemainingCounts(bookingRoomTypes, assignedCountByRoomType);
            
            // Validate selected rooms
            ValidationResult validation = validateSelectedRooms(roomIdsParam, booking, bookingRoomTypes, remainingByRoomType);
            
            if (!validation.isValid()) {
                loadAssignRoomPage(request, response, bookingId, validation.getErrorMessage(), null);
                return;
            }

            // Assign rooms to booking
            boolean assignSuccess = bookingDAO.assignRoomsToBooking(bookingId, roomIdsParam);
            
            if (!assignSuccess) {
                session.setAttribute("errorMessage", "Failed to assign rooms to booking. Please try again.");
                response.sendRedirect("staff-bookings-list");
                return;
            }

            // Update room status to Occupied
            for (String rid : roomIdsParam) {
                try {
                    int roomId = Integer.parseInt(rid);
                    roomDAO.updateRoomStatus(roomId, "Occupied");
                } catch (NumberFormatException ignore) {}
            }

            // Check if all room requirements are met
            boolean allRoomTypesComplete = checkAllRoomTypesComplete(bookingRoomTypes, assignedCountByRoomType, validation.getSelectedCounts());

            // Update booking status if complete
            String successMessage;
            if (allRoomTypesComplete) {
                boolean statusUpdated = bookingDAO.updateBookingStatus(bookingId, "CheckedIn");
                if (statusUpdated) {
                    successMessage = "Rooms assigned and booking checked-in successfully!";
                } else {
                    successMessage = "Rooms assigned but failed to update booking status.";
                }
            } else {
                successMessage = "Rooms assigned successfully! Complete all room assignments to enable check-in.";
            }

            session.setAttribute("successMessage", successMessage);

        } catch (Exception e) {
            session.setAttribute("errorMessage", "An error occurred while assigning rooms: " + e.getMessage());
        }
        
        response.sendRedirect("staff-bookings-list");
    }
    
    /**
     * Load assign room page with all data
     */
    private void loadAssignRoomPage(HttpServletRequest request, HttpServletResponse response, 
                                  int bookingId, String errorMessage, String warningMessage) 
                                  throws ServletException, IOException {
        
        // Get booking
        Booking booking = bookingDAO.getBookingById(bookingId);
        if (booking == null) {
            request.setAttribute("errorMessage", "Booking not found!");
            request.getRequestDispatcher("/staff-assign-room.jsp").forward(request, response);
            return;
        }
        
        // Get booking room types
        List<BookingRoomType> bookingRoomTypes = bookingRoomTypeDAO.getBookingRoomTypesByBookingId(bookingId);
        
        // Get assigned rooms
        List<Room> assignedRooms = roomDAO.getAssignedRoomsByBookingId(bookingId);
        
        // Calculate counts
        Map<Integer, Integer> assignedCountByRoomType = calculateAssignedCounts(assignedRooms);
        Map<Integer, Integer> requiredQuantityByRoomType = calculateRequiredCounts(bookingRoomTypes);
        Map<Integer, Integer> remainingByRoomType = calculateRemainingCounts(bookingRoomTypes, assignedCountByRoomType);
        
        // Get available rooms
        Map<Integer, List<Room>> availableRoomsByType = getAvailableRoomsByType(bookingRoomTypes, booking.getBranchId());
        
        // Check availability
        boolean canAssignAll = checkCanAssignAll(remainingByRoomType, availableRoomsByType);
        
        // Generate UI data
        List<RoomTypeDisplayData> roomTypeDisplays = generateRoomTypeDisplays(
            bookingRoomTypes, assignedCountByRoomType, remainingByRoomType, availableRoomsByType);
        
        // Set attributes
        request.setAttribute("booking", booking);
        request.setAttribute("bookingRoomTypes", bookingRoomTypes);
        request.setAttribute("assignedRooms", assignedRooms);
        request.setAttribute("assignedCountByRoomType", assignedCountByRoomType);
        request.setAttribute("requiredQuantityByRoomType", requiredQuantityByRoomType);
        request.setAttribute("remainingByRoomType", remainingByRoomType);
        request.setAttribute("availableRoomsByType", availableRoomsByType);
        request.setAttribute("canAssignAll", canAssignAll);
        request.setAttribute("roomTypeDisplays", roomTypeDisplays);
        
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
        }
        if (warningMessage != null) {
            request.setAttribute("warningMessage", warningMessage);
        }
        
        request.getRequestDispatcher("/staff-assign-room.jsp").forward(request, response);
    }
    
    /**
     * Calculate assigned room counts by type
     */
    private Map<Integer, Integer> calculateAssignedCounts(List<Room> assignedRooms) {
        Map<Integer, Integer> counts = new HashMap<>();
        if (assignedRooms != null) {
            for (Room room : assignedRooms) {
                counts.put(room.getRoomTypeId(), 
                    counts.getOrDefault(room.getRoomTypeId(), 0) + 1);
            }
        }
        return counts;
    }
    
    /**
     * Calculate required room counts by type
     */
    private Map<Integer, Integer> calculateRequiredCounts(List<BookingRoomType> bookingRoomTypes) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (BookingRoomType brt : bookingRoomTypes) {
            counts.put(brt.getRoomTypeId(), brt.getQuantity());
        }
        return counts;
    }
    
    /**
     * Calculate remaining room counts by type
     */
    private Map<Integer, Integer> calculateRemainingCounts(List<BookingRoomType> bookingRoomTypes, 
                                                         Map<Integer, Integer> assignedCounts) {
        Map<Integer, Integer> remaining = new HashMap<>();
        for (BookingRoomType brt : bookingRoomTypes) {
            int required = brt.getQuantity();
            int assigned = assignedCounts.getOrDefault(brt.getRoomTypeId(), 0);
            remaining.put(brt.getRoomTypeId(), Math.max(0, required - assigned));
        }
        return remaining;
    }
    
    /**
     * Get available rooms by type
     */
    private Map<Integer, List<Room>> getAvailableRoomsByType(List<BookingRoomType> bookingRoomTypes, int branchId) {
        Map<Integer, List<Room>> availableRooms = new HashMap<>();
        for (BookingRoomType brt : bookingRoomTypes) {
            List<Room> rooms = roomDAO.getSimpleAvailableRoomsByType(brt.getRoomTypeId(), branchId);
            availableRooms.put(brt.getRoomTypeId(), rooms != null ? rooms : new ArrayList<>());
        }
        return availableRooms;
    }
    
    /**
     * Check if all room types can be satisfied
     */
    private boolean checkCanAssignAll(Map<Integer, Integer> remainingByRoomType, 
                                    Map<Integer, List<Room>> availableRoomsByType) {
        for (Map.Entry<Integer, Integer> entry : remainingByRoomType.entrySet()) {
            int roomTypeId = entry.getKey();
            int remaining = entry.getValue();
            List<Room> available = availableRoomsByType.get(roomTypeId);
            int availableCount = available != null ? available.size() : 0;
            
            if (remaining > availableCount) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Validate selected rooms
     */
    private ValidationResult validateSelectedRooms(String[] roomIds, Booking booking, 
                                                 List<BookingRoomType> bookingRoomTypes,
                                                 Map<Integer, Integer> remainingByRoomType) {
        
        ValidationResult result = new ValidationResult();
        Map<Integer, Integer> selectedCounts = new HashMap<>();
        
        // Initialize selected counts
        for (BookingRoomType brt : bookingRoomTypes) {
            selectedCounts.put(brt.getRoomTypeId(), 0);
        }
        
        List<String> errors = new ArrayList<>();
        
        for (String rid : roomIds) {
            try {
                int roomId = Integer.parseInt(rid);
                Room room = roomDAO.getRoomById(roomId);
                
                if (room == null) {
                    errors.add("Room ID " + roomId + " not found.");
                    continue;
                }
                
                if (!"Available".equals(room.getStatus())) {
                    errors.add("Room " + room.getRoomNumber() + " is not available.");
                    continue;
                }
                
                if (room.getBranchId() != booking.getBranchId()) {
                    errors.add("Room " + room.getRoomNumber() + " belongs to different branch.");
                    continue;
                }
                
                // Check room type validity
                boolean validType = false;
                for (BookingRoomType brt : bookingRoomTypes) {
                    if (brt.getRoomTypeId() == room.getRoomTypeId()) {
                        validType = true;
                        selectedCounts.put(room.getRoomTypeId(), 
                            selectedCounts.get(room.getRoomTypeId()) + 1);
                        break;
                    }
                }
                
                if (!validType) {
                    errors.add("Room " + room.getRoomNumber() + " is not correct type for this booking.");
                }
                
            } catch (NumberFormatException e) {
                errors.add("Invalid room ID: " + rid);
            }
        }
        
        // Check quantity limits
        for (Map.Entry<Integer, Integer> entry : selectedCounts.entrySet()) {
            int roomTypeId = entry.getKey();
            int selected = entry.getValue();
            int remaining = remainingByRoomType.getOrDefault(roomTypeId, 0);
            
            if (selected > remaining) {
                errors.add("Too many rooms selected for room type " + roomTypeId + ": need " + remaining + " but selected " + selected + ".");
            }
        }
        
        result.setValid(errors.isEmpty());
        result.setErrorMessage(String.join(" ", errors));
        result.setSelectedCounts(selectedCounts);
        
        return result;
    }
    
    /**
     * Check if all room types are complete
     */
    private boolean checkAllRoomTypesComplete(List<BookingRoomType> bookingRoomTypes,
                                            Map<Integer, Integer> assignedCounts,
                                            Map<Integer, Integer> newlySelectedCounts) {
        for (BookingRoomType brt : bookingRoomTypes) {
            int required = brt.getQuantity();
            int currentlyAssigned = assignedCounts.getOrDefault(brt.getRoomTypeId(), 0);
            int newlySelected = newlySelectedCounts.getOrDefault(brt.getRoomTypeId(), 0);
            int total = currentlyAssigned + newlySelected;
            
            if (total < required) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Generate display data for room types
     */
    private List<RoomTypeDisplayData> generateRoomTypeDisplays(List<BookingRoomType> bookingRoomTypes,
                                                             Map<Integer, Integer> assignedCounts,
                                                             Map<Integer, Integer> remainingCounts,
                                                             Map<Integer, List<Room>> availableRooms) {
        List<RoomTypeDisplayData> displays = new ArrayList<>();
        
        for (BookingRoomType brt : bookingRoomTypes) {
            int roomTypeId = brt.getRoomTypeId();
            int required = brt.getQuantity();
            int assigned = assignedCounts.getOrDefault(roomTypeId, 0);
            int remaining = remainingCounts.getOrDefault(roomTypeId, 0);
            List<Room> available = availableRooms.getOrDefault(roomTypeId, new ArrayList<>());
            
            RoomTypeDisplayData display = new RoomTypeDisplayData();
            display.setRoomTypeId(roomTypeId);
            display.setRoomTypeName(brt.getRoomTypeName());
            display.setRequired(required);
            display.setAssigned(assigned);
            display.setRemaining(remaining);
            display.setAvailableRooms(available);
            display.setInsufficient(remaining > available.size());
            display.setProgressPercent(required > 0 ? (assigned * 100) / required : 0);
            display.setCompleted(remaining == 0);
            
            displays.add(display);
        }
        
        return displays;
    }
    
    /**
     * Validation result class
     */
    private static class ValidationResult {
        private boolean valid;
        private String errorMessage;
        private Map<Integer, Integer> selectedCounts;
        
        // Getters and setters
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public Map<Integer, Integer> getSelectedCounts() { return selectedCounts; }
        public void setSelectedCounts(Map<Integer, Integer> selectedCounts) { this.selectedCounts = selectedCounts; }
    }
    
    /**
     * Room type display data class
     */
    public static class RoomTypeDisplayData {
        private int roomTypeId;
        private String roomTypeName;
        private int required;
        private int assigned;
        private int remaining;
        private List<Room> availableRooms;
        private boolean insufficient;
        private int progressPercent;
        private boolean completed;
        
        // Getters and setters
        public int getRoomTypeId() { return roomTypeId; }
        public void setRoomTypeId(int roomTypeId) { this.roomTypeId = roomTypeId; }
        public String getRoomTypeName() { return roomTypeName; }
        public void setRoomTypeName(String roomTypeName) { this.roomTypeName = roomTypeName; }
        public int getRequired() { return required; }
        public void setRequired(int required) { this.required = required; }
        public int getAssigned() { return assigned; }
        public void setAssigned(int assigned) { this.assigned = assigned; }
        public int getRemaining() { return remaining; }
        public void setRemaining(int remaining) { this.remaining = remaining; }
        public List<Room> getAvailableRooms() { return availableRooms; }
        public void setAvailableRooms(List<Room> availableRooms) { this.availableRooms = availableRooms; }
        public boolean isInsufficient() { return insufficient; }
        public void setInsufficient(boolean insufficient) { this.insufficient = insufficient; }
        public int getProgressPercent() { return progressPercent; }
        public void setProgressPercent(int progressPercent) { this.progressPercent = progressPercent; }
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
    }
}