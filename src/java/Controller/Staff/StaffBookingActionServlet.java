package Controller.Staff;

import Dal.BookingDAO;
import Dal.RoomDAO;
import Dal.UserAccountDAO;
import Model.Booking;
import Model.UserAccount;
import Model.Room;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "StaffBookingActionServlet", urlPatterns = {"/staff-booking-action"})
public class StaffBookingActionServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String idParam = request.getParameter("bookingId");
        HttpSession session = request.getSession(false);

        // Authentication check
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        UserAccount staff = (UserAccount) session.getAttribute("user");
        Integer branchId = staff.getBranchId();

        // Validate branch ID
        if (branchId == null || branchId <= 0) {
            session.setAttribute("errorMessage", "Staff branch information is invalid.");
            response.sendRedirect(request.getContextPath() + "/staff-bookings-list");
            return;
        }

        BookingDAO bookingDAO = new BookingDAO();
        RoomDAO roomDAO = new RoomDAO();
        UserAccountDAO userAccountDAO = new UserAccountDAO();

        try {
            // Validate booking ID
            if (idParam == null || idParam.trim().isEmpty()) {
                session.setAttribute("errorMessage", "Booking ID is required.");
                response.sendRedirect(request.getContextPath() + "/staff-bookings-list");
                return;
            }
            
            int bookingId = Integer.parseInt(idParam);
            
            // Get booking and validate branch ownership
            Booking booking = bookingDAO.getBookingByIdAndBranch(bookingId, branchId);
            if (booking == null) {
                session.setAttribute("errorMessage", "The booking does not belong to your branch or does not exist.");
                response.sendRedirect(request.getContextPath() + "/staff-bookings-list");
                return;
            }

            String status = booking.getStatus();
            java.util.Date now = new java.util.Date();
            java.util.Date checkInTime = booking.getCheckIn();
           
            // ----------- CHECK-IN ACTION: Only update booking status to CheckedIn -----------
            if ("checkin".equalsIgnoreCase(action)) {
                // Validate booking status for check-in
                if (!"Paid".equalsIgnoreCase(status) && !"Pending".equalsIgnoreCase(status)) {
                    session.setAttribute("errorMessage", 
                        "Only bookings with status 'Paid' or 'Pending' can be checked in. Current status: " + status);
                    response.sendRedirect(request.getContextPath() + "/staff-bookings-list");
                    return;
                }
                
                // Validate check-in time
                if (checkInTime != null && now.before(checkInTime)) {
                    session.setAttribute("errorMessage", 
                        "Cannot check-in before scheduled check-in time: " + checkInTime);
                    response.sendRedirect(request.getContextPath() + "/staff-bookings-list");
                    return;
                }
                
                // Update booking status to CheckedIn
                boolean updated = bookingDAO.updateBookingStatus(bookingId, "CheckedIn");
                if (updated) {
                    // Check if there are assigned rooms and update their status to Occupied
                    List<Room> assignedRooms = roomDAO.getAssignedRoomsByBookingId(bookingId);
                    int roomsUpdated = 0;
                    
                    if (assignedRooms != null && !assignedRooms.isEmpty()) {
                        for (Room room : assignedRooms) {
                            boolean roomUpdated = roomDAO.updateRoomStatus(room.getId(), "Occupied");
                            if (roomUpdated) {
                                roomsUpdated++;
                            }
                        }
                        
                        String roomNumbers = getRoomNumbers(assignedRooms);
                        session.setAttribute("successMessage", 
                            "Check-in successful! Customer can now use rooms: " + roomNumbers);
                    } else {
                        session.setAttribute("successMessage", 
                            "Check-in completed successfully. No rooms were assigned yet.");
                    }
                    
                    System.out.println("Booking " + bookingId + " checked in by staff " + staff.getUsername() + 
                                 ". Updated " + roomsUpdated + " rooms to Occupied status.");
                } else {
                    session.setAttribute("errorMessage", "Failed to update booking status to CheckedIn.");
                }
                
                response.sendRedirect(request.getContextPath() + "/staff-bookings-list");
                return;
            }
            // ----------- CHECK-OUT ACTION -----------
            else if ("checkout".equalsIgnoreCase(action)) {
                // Validate booking status for check-out
                if (!"CheckedIn".equalsIgnoreCase(status)) {
                    session.setAttribute("errorMessage", 
                        "Only bookings with status 'CheckedIn' can be checked out. Current status: " + status);
                    response.sendRedirect(request.getContextPath() + "/staff-bookings-list");
                    return;
                }
                
                // Get customer and room information for checkout page
                UserAccount customer = userAccountDAO.getUserById(booking.getUserId());
                List<Room> bookingRoomList = bookingDAO.getRoomsByBookingIdAndBranch(bookingId, branchId);
                
                if (customer == null) {
                    session.setAttribute("errorMessage", "Customer information not found.");
                    response.sendRedirect(request.getContextPath() + "/staff-bookings-list");
                    return;
                }
                
                if (bookingRoomList == null || bookingRoomList.isEmpty()) {
                    session.setAttribute("errorMessage", "No rooms found for this booking.");
                    response.sendRedirect(request.getContextPath() + "/staff-bookings-list");
                    return;
                }
                
                // Forward to checkout page
                request.setAttribute("booking", booking);
                request.setAttribute("customer", customer);
                request.setAttribute("bookingRoomList", bookingRoomList);
                request.getRequestDispatcher("/staff-checkout.jsp").forward(request, response);
                return;
            }
            // ----------- CONFIRM CHECK-OUT ACTION -----------
            else if ("confirmcheckout".equalsIgnoreCase(action)) {
                // Validate booking status
                if (!"CheckedIn".equalsIgnoreCase(status)) {
                    session.setAttribute("errorMessage", "Only checked-in bookings can be completed.");
                    response.sendRedirect(request.getContextPath() + "/staff-bookings-list");
                    return;
                }
                
                // Update booking status to Completed
                boolean updated = bookingDAO.updateBookingStatus(bookingId, "Completed");
                if (updated) {
                    // Release all rooms assigned to this booking
                    List<Integer> roomIds = bookingDAO.getRoomIdsByBookingAndBranch(bookingId, branchId);
                    int releasedRooms = 0;
                    
                    for (int roomId : roomIds) {
                        boolean roomUpdated = roomDAO.updateRoomStatus(roomId, "Available");
                        if (roomUpdated) {
                            releasedRooms++;
                        }
                    }
                    
                    if (releasedRooms > 0) {
                        session.setAttribute("successMessage", 
                            "Check-out completed successfully! " + releasedRooms + " room(s) released and booking marked as Completed.");
                    } else {
                        session.setAttribute("successMessage", 
                            "Check-out completed and booking marked as Completed.");
                    }
                    
                    System.out.println("Booking " + bookingId + " checked out by staff " + staff.getUsername() + 
                                     ". Released " + releasedRooms + " rooms.");
                } else {
                    session.setAttribute("errorMessage", "Failed to complete check-out. Please try again.");
                }
                response.sendRedirect(request.getContextPath() + "/staff-bookings-list");
                return;
            }
            // ----------- INVALID ACTION -----------
            else {
                session.setAttribute("errorMessage", "Invalid action: " + action);
                response.sendRedirect(request.getContextPath() + "/staff-bookings-list");
                return;
            }
            
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid booking ID format: " + idParam);
            response.sendRedirect(request.getContextPath() + "/staff-bookings-list");
        } catch (Exception e) {
            System.err.println("Error in StaffBookingActionServlet: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/staff-bookings-list");
        }
    }
    
    /**
     * Helper method to get room numbers as a comma-separated string
     */
    private String getRoomNumbers(List<Room> rooms) {
        if (rooms == null || rooms.isEmpty()) {
            return "N/A";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rooms.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(rooms.get(i).getRoomNumber());
        }
        return sb.toString();
    }
}