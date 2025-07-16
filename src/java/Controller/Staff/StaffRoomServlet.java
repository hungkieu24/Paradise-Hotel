package Controller.Staff;

import Dal.HotelBranchDAO;
import Dal.RoomDAO;
import Dal.RoomTypeDAO;
import Model.Room;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "StaffRoomServlet", urlPatterns = {"/staff-rooms"})
public class StaffRoomServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // --- VALIDATION: Check staff session/role ---
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userRole") == null
                || !"staff".equalsIgnoreCase(String.valueOf(session.getAttribute("userRole")))) {
            response.sendRedirect("login.jsp");
            return;
        }

        // ---- Lấy branchId từ session của staff ----
        Object branchIdObj = session.getAttribute("branchId");
        Integer branchId = null;
        if (branchIdObj instanceof Integer) {
            branchId = (Integer) branchIdObj;
        } else if (branchIdObj instanceof String) {
            try {
                branchId = Integer.parseInt((String) branchIdObj);
            } catch (NumberFormatException e) {
                branchId = null;
            }
        }
        
        // Nếu không có branchId -> lỗi, không cho xem phòng
        if (branchId == null) {
            request.setAttribute("errorMessage", "Staff information is invalid (no branch found).");
            response.sendRedirect("login.jsp");
            return;
        }

        // Đảm bảo branchName có trong session
        if (session.getAttribute("branchName") == null) {
            HotelBranchDAO branchDAO = new HotelBranchDAO();
            String branchName = branchDAO.getBranchNameById(branchId);
            session.setAttribute("branchName", branchName);
        }
        // Đặt branchName lên request để sidebar lấy ra hiển thị
        request.setAttribute("branchName", session.getAttribute("branchName"));

        // Lấy các tham số search và pagination
        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status");
        int page = 1;
        int pageSize = 8;

        // Validate và parse page parameter
        try {
            if (request.getParameter("page") != null) {
                page = Integer.parseInt(request.getParameter("page"));
                if (page < 1) {
                    page = 1;
                }
            }
        } catch (NumberFormatException e) {
            page = 1;
        }

        // Khởi tạo DAO
        RoomDAO roomDAO = new RoomDAO();
        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();

        List<Room> rooms;
        int totalRoom = 0;
        
        try {
            // Xử lý search với keyword và status
            if (keyword != null && !keyword.trim().isEmpty()) {
                rooms = roomDAO.searchRoomsByRoomTypeNameAndBranchWithStatus(
                    keyword.trim(), branchId, status, page, pageSize);
                totalRoom = roomDAO.countRoomsByRoomTypeNameAndBranchWithStatus(
                    keyword.trim(), branchId, status);
            } else {
                rooms = roomDAO.pagingRoomByBranchWithStatus(branchId, status, page, pageSize);
                totalRoom = roomDAO.countRoomsByBranchWithStatus(branchId, status);
            }

            // Tính toán pagination
            int totalPage = (int) Math.ceil((double) totalRoom / pageSize);
            if (totalPage == 0) {
                totalPage = 1;
            }

            // Validate current page against total pages
            if (page > totalPage) {
                page = totalPage;
            }

            // Lấy các map cần thiết
            Map<Integer, String> roomTypeMap = roomTypeDAO.getRoomTypeMap();
            Map<Integer, Double> roomTypePriceMap = roomTypeDAO.getRoomTypePriceMap();
            Map<Integer, Integer> roomTypeCountMap = roomDAO.getRoomTypeCountByBranchAndStatus(branchId, status);

            // Set attributes cho JSP
            request.setAttribute("roomTypeMap", roomTypeMap);
            request.setAttribute("roomTypePriceMap", roomTypePriceMap);
            request.setAttribute("roomTypeCountMap", roomTypeCountMap);
            request.setAttribute("rooms", rooms);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPage", totalPage);
            request.setAttribute("totalRoom", totalRoom);

      
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred while loading room data: " + e.getMessage());
            
            // Set default values để tránh JSP lỗi
            request.setAttribute("rooms", new java.util.ArrayList<Room>());
            request.setAttribute("currentPage", 1);
            request.setAttribute("totalPage", 1);
            request.setAttribute("totalRoom", 0);
            request.setAttribute("roomTypeMap", new java.util.HashMap<Integer, String>());
            request.setAttribute("roomTypePriceMap", new java.util.HashMap<Integer, Double>());
            request.setAttribute("roomTypeCountMap", new java.util.HashMap<Integer, Integer>());
        }

        // Forward tới JSP
        request.getRequestDispatcher("staff-rooms.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // --- VALIDATION: Check staff session/role ---
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userRole") == null
                || !"staff".equalsIgnoreCase(String.valueOf(session.getAttribute("userRole")))) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            String[] roomIds = request.getParameterValues("roomId");
            
            if (roomIds == null || roomIds.length == 0) {
                request.setAttribute("error", "No rooms selected for update.");
                doGet(request, response);
                return;
            }

            RoomDAO roomDAO = new RoomDAO();
            int updatedCount = 0;
            int errorCount = 0;
            StringBuilder errorDetails = new StringBuilder();
         for (String roomIdStr : roomIds) {
                try {
                    int roomId = Integer.parseInt(roomIdStr);
                    String newStatus = request.getParameter("status_" + roomId);
                    
                    if (newStatus != null && !newStatus.trim().isEmpty()) {
                        // Validate status values
                        if (isValidStatus(newStatus)) {
                            boolean updateResult = roomDAO.updateRoomStatus(roomId, newStatus);
                            if (updateResult) {
                                updatedCount++;
                                System.out.println("- Updated room " + roomId + " to status: " + newStatus);
                            } else {
                                errorCount++;
                                errorDetails.append("Room ").append(roomId).append(" update failed. ");
                            }
                        } else {
                            errorCount++;
                            errorDetails.append("Invalid status '").append(newStatus)
                                      .append("' for room ").append(roomId).append(". ");
                        }
                    }
                } catch (NumberFormatException e) {
                    errorCount++;
                    errorDetails.append("Invalid room ID: ").append(roomIdStr).append(". ");
                } catch (Exception e) {
                    errorCount++;
                    errorDetails.append("Error updating room ").append(roomIdStr)
                              .append(": ").append(e.getMessage()).append(". ");
                }
            }

            // Chuẩn bị message để redirect
            String redirectUrl = "staff-rooms";
            
            if (updatedCount > 0) {
                redirectUrl += "?message=statusUpdated";
                System.out.println("- Successfully updated " + updatedCount + " room(s)");
            }
            
            if (errorCount > 0) {
                System.err.println("- Errors occurred for " + errorCount + " room(s): " + errorDetails.toString());
                if (updatedCount == 0) {
                    request.setAttribute("error", "Failed to update room status: " + errorDetails.toString());
                    doGet(request, response);
                    return;
                }
            }

            // Redirect to prevent re-submission
            response.sendRedirect(redirectUrl);

        } catch (Exception e) {
            System.err.println("Error in StaffRoomServlet doPost: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "An error occurred while updating room status: " + e.getMessage());
            doGet(request, response);
        }
    }

    /**
     * Validate room status values
     */
    private boolean isValidStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return false;
        }
        
        String[] validStatuses = {"Available", "Booked", "Occupied", "Maintenance"};
        for (String validStatus : validStatuses) {
            if (validStatus.equalsIgnoreCase(status.trim())) {
                return true;
            }
        }
        return false;
    }

    
}