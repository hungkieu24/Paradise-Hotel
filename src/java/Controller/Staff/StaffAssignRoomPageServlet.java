package Controller.Staff;

import Dal.BookingDAO;
import Dal.RoomDAO;
import Dal.BookingRoomTypeDAO;
import Model.Booking;
import Model.Room;
import Model.BookingRoomType;
import Model.UserAccount;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "StaffAssignRoomPageServlet", urlPatterns = {"/staff-assign-room"})
public class StaffAssignRoomPageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        UserAccount staff = (UserAccount) session.getAttribute("user");
        int staffBranchId = staff.getBranchId();
        
        String bookingIdParam = request.getParameter("bookingId");  
        if (bookingIdParam == null || bookingIdParam.trim().isEmpty()) {
            response.sendRedirect("staff-bookings-list");
            return;
        }
        
        try {
            int bookingId = Integer.parseInt(bookingIdParam);
            
            BookingDAO bookingDAO = new BookingDAO();
            RoomDAO roomDAO = new RoomDAO();
            BookingRoomTypeDAO bookingRoomTypeDAO = new BookingRoomTypeDAO();
            
            // Lấy thông tin booking
            Booking booking = bookingDAO.getBookingById(bookingId);
            
            if (booking == null) {
                request.setAttribute("errorMessage", "Booking not found!");
                request.getRequestDispatcher("/staff-bookings-list").forward(request, response);
                return;
            }
            
            // Kiểm tra nếu booking thuộc chi nhánh của staff
            if (booking.getBranchId() != staffBranchId) {
                request.setAttribute("errorMessage", "This booking does not belong to your branch!");
                request.getRequestDispatcher("/staff-bookings-list").forward(request, response);
                return;
            }
            
            // Lấy thông tin room types và quantities từ BookingRoomType table
            List<BookingRoomType> bookingRoomTypes = bookingRoomTypeDAO.getBookingRoomTypesByBookingId(bookingId);
            
            if (bookingRoomTypes == null || bookingRoomTypes.isEmpty()) {
                request.setAttribute("errorMessage", "No room types found for this booking!");
                request.getRequestDispatcher("/staff-bookings-list").forward(request, response);
                return;
            }
            
            // Tạo map để theo dõi số lượng phòng yêu cầu cho mỗi loại
            Map<Integer, Integer> requiredQuantityByRoomType = new HashMap<>();
            Map<Integer, String> roomTypeNames = new HashMap<>();
            
            for (BookingRoomType brt : bookingRoomTypes) {
                requiredQuantityByRoomType.put(brt.getRoomTypeId(), brt.getQuantity());
                roomTypeNames.put(brt.getRoomTypeId(), brt.getRoomTypeName());
            }
            
            // Lấy danh sách phòng khả dụng cho từng loại phòng
            Map<Integer, List<Room>> availableRoomsByType = new HashMap<>();
            for (BookingRoomType brt : bookingRoomTypes) {
                List<Room> availableRooms = roomDAO.getSimpleAvailableRoomsByType(
                    brt.getRoomTypeId(), booking.getBranchId());
                
                if (availableRooms == null) {
                    availableRooms = new ArrayList<>();
                }
                
                availableRoomsByType.put(brt.getRoomTypeId(), availableRooms);
            }
            
            // Lấy danh sách phòng đã được gán (nếu có)
            List<Room> assignedRooms = roomDAO.getAssignedRoomsByBookingId(bookingId);
            if (assignedRooms == null) {
                assignedRooms = new ArrayList<>();
            }
            
            // Đếm số phòng đã được gán cho mỗi loại
            Map<Integer, Integer> assignedCountByRoomType = new HashMap<>();
            for (Room room : assignedRooms) {
                int roomTypeId = room.getRoomTypeId();
                assignedCountByRoomType.put(roomTypeId, 
                    assignedCountByRoomType.getOrDefault(roomTypeId, 0) + 1);
            }
            
            // Tính toán số phòng còn cần gán cho mỗi loại
            Map<Integer, Integer> remainingByRoomType = new HashMap<>();
            for (BookingRoomType brt : bookingRoomTypes) {
                int roomTypeId = brt.getRoomTypeId();
                int required = brt.getQuantity();
                int assigned = assignedCountByRoomType.getOrDefault(roomTypeId, 0);
                int remaining = Math.max(0, required - assigned);
                remainingByRoomType.put(roomTypeId, remaining);
            }
            
            // Chuẩn bị dữ liệu tổng hợp cho từng loại phòng
            Map<Integer, Map<String, Object>> roomTypeData = new HashMap<>();
            
            for (BookingRoomType brt : bookingRoomTypes) {
                int roomTypeId = brt.getRoomTypeId();
                
                Map<String, Object> typeInfo = new HashMap<>();
                typeInfo.put("roomType", brt);
                typeInfo.put("availableRooms", availableRoomsByType.get(roomTypeId));
                typeInfo.put("requiredQuantity", brt.getQuantity());
                typeInfo.put("assignedCount", assignedCountByRoomType.getOrDefault(roomTypeId, 0));
                typeInfo.put("remainingCount", remainingByRoomType.get(roomTypeId));
                typeInfo.put("availableCount", availableRoomsByType.get(roomTypeId).size());
                
                roomTypeData.put(roomTypeId, typeInfo);
            }
            
            // Kiểm tra xem có thể assign đủ phòng không
            boolean canAssignAll = true;
            StringBuilder warningMessage = new StringBuilder();
            
            for (BookingRoomType brt : bookingRoomTypes) {
                int roomTypeId = brt.getRoomTypeId();
                int remaining = remainingByRoomType.get(roomTypeId);
                int available = availableRoomsByType.get(roomTypeId).size();
                
                if (remaining > available) {
                    canAssignAll = false;
                    warningMessage.append("Room Type '").append(brt.getRoomTypeName())
                                 .append("': Need ").append(remaining)
                                 .append(" more rooms but only ").append(available)
                                 .append(" available. ");
                }
            }
            
            if (!canAssignAll) {
                request.setAttribute("warningMessage", warningMessage.toString());
            }
            
            // Set các thuộc tính cho request
            request.setAttribute("booking", booking);
            request.setAttribute("bookingRoomTypes", bookingRoomTypes);
            request.setAttribute("assignedRooms", assignedRooms);
            request.setAttribute("roomTypeData", roomTypeData);
            request.setAttribute("availableRoomsByType", availableRoomsByType);
            request.setAttribute("requiredQuantityByRoomType", requiredQuantityByRoomType);
            request.setAttribute("assignedCountByRoomType", assignedCountByRoomType);
            request.setAttribute("remainingByRoomType", remainingByRoomType);
            request.setAttribute("roomTypeNames", roomTypeNames);
            request.setAttribute("canAssignAll", canAssignAll);
            
            request.getRequestDispatcher("/staff-assign-room.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid booking ID format!");
            request.getRequestDispatcher("/staff-bookings-list").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            request.getRequestDispatcher("/staff-bookings-list").forward(request, response);
        }
    }
}