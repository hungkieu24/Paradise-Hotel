package Controller.Staff;

import Dal.RoomDAO;
import Model.UserAccount;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name="AjaxBookedQuantityServlet", urlPatterns={"/ajaxBookedQuantity"})
public class AjaxBookedQuantityServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String roomTypeIdStr = request.getParameter("roomTypeId");
        String checkInStr = request.getParameter("checkIn");
        String checkOutStr = request.getParameter("checkOut");
        
        int bookedQuantity = 0;
        String errorMessage = null;
        
        try {
            // Validate parameters
            if (roomTypeIdStr == null || roomTypeIdStr.isEmpty()) {
                throw new IllegalArgumentException("Room type ID is required");
            }
            
            if (checkInStr == null || checkInStr.isEmpty() || 
                checkOutStr == null || checkOutStr.isEmpty()) {
                throw new IllegalArgumentException("Check-in and check-out dates are required");
            }
            
            // Parse room type ID
            int roomTypeId = Integer.parseInt(roomTypeIdStr);
            
            // Parse dates - expecting yyyy-MM-dd format from date inputs
            java.sql.Date checkInDate = java.sql.Date.valueOf(checkInStr);
            java.sql.Date checkOutDate = java.sql.Date.valueOf(checkOutStr);
            
            // Validate dates
            if (checkInDate.compareTo(checkOutDate) >= 0) {
                throw new IllegalArgumentException("Check-out date must be after check-in date");
            }
            
            // Get branch ID from session
            UserAccount staff = (UserAccount) request.getSession().getAttribute("user");
            if (staff == null || staff.getBranchId() == null) {
                throw new IllegalArgumentException("Staff session not found or no branch assigned");
            }
            
            Integer branchId = staff.getBranchId();
            
            // Get booked quantity
            RoomDAO roomDAO = new RoomDAO();
            bookedQuantity = roomDAO.getBookedQuantityByRoomTypeAndDateRange(
                branchId, 
                roomTypeId,
                checkInDate,
                checkOutDate
            );
            
            System.out.println("Ajax call - Branch: " + branchId + 
                             ", RoomType: " + roomTypeId + 
                             ", CheckIn: " + checkInDate + 
                             ", CheckOut: " + checkOutDate + 
                             ", Booked: " + bookedQuantity);
            
        } catch (IllegalArgumentException e) {
            errorMessage = e.getMessage();
            bookedQuantity = -1;
            System.err.println("Ajax validation error: " + e.getMessage());
        } catch (Exception e) {
            errorMessage = "Server error occurred";
            bookedQuantity = -1;
            e.printStackTrace();
        }
        
        // Return JSON response
        if (errorMessage != null) {
            response.getWriter().write(
                "{\"bookedQuantity\":" + bookedQuantity + 
                ",\"error\":\"" + errorMessage + "\"}"
            );
        } else {
            response.getWriter().write(
                "{\"bookedQuantity\":" + bookedQuantity + ",\"success\":true}"
            );
        }
    }
}