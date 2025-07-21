/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Manager;

import Dal.RoomDAO;
import Dal.RoomTypeDAO;
import Model.Room;
import Model.RoomType;
import Model.UserAccount;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author thien
 */
@WebServlet(name = "DeleteRoomServlet", urlPatterns = {"/deleteRoom"})
public class DeleteRoomServlet extends HttpServlet {

    RoomDAO roomDao = new RoomDAO();
    RoomTypeDAO rtDao = new RoomTypeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("rooms");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Lấy session và kiểm tra đăng nhập
        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null) {
            request.setAttribute("error", "User not logged in.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }
        String username = user.getUsername();
        String userId = user.getId();
        String branchname = roomDao.getBranchNameById(userId);
        // Pagination parameters
        int page = 1;
        int pageSize = 5;
        String pageParam = request.getParameter("page");
        String sizeParam = request.getParameter("size");
        if (pageParam != null && !pageParam.isEmpty()) {
            page = Integer.parseInt(pageParam);
        }
        if (sizeParam != null && !sizeParam.isEmpty()) {
            pageSize = Integer.parseInt(sizeParam);
        }
        if (page < 1) {
            page = 1;
        }
        if (pageSize < 1) {
            pageSize = 5;
        }

        String action = request.getParameter("action");
        if ("delete".equals(action)) {
            String roomIdStr = request.getParameter("roomId");
            if (roomIdStr == null || roomIdStr.isEmpty()) {
                request.setAttribute("error", "Room ID is required.");
                prepareResponse(request, response, user, roomDao.getBranchId(userId), branchname, page, pageSize);
                return;
            }
            int roomId = Integer.parseInt(roomIdStr);
            String status = roomDao.getStatusById(roomId);
            if ("booked".equalsIgnoreCase(status) || "occupied".equalsIgnoreCase(status)) {
                request.setAttribute("error", "Cannot delete a room that is currently booked or in use.");
                int branchId = roomDao.getBranchId(user.getId());
                prepareResponse(request, response, user, branchId, branchname, page, pageSize);
                return;
            }
            boolean deleted = roomDao.softDeleteRoom(roomId);
            if (deleted) {
                String redirectUrl = "rooms?page=" + page + "&size=" + pageSize;
                response.sendRedirect(redirectUrl);
                return;
            } else {
                request.setAttribute("error", "Failed to delete room. Please try again.");
                prepareResponse(request, response, user, roomDao.getBranchId(userId), branchname, page, pageSize);
                return;
            }

        }
    }

    private void prepareResponse(HttpServletRequest request, HttpServletResponse response, UserAccount user, int branchId,
            String branchname, int page, int pageSize) throws ServletException, IOException {
        int totalRooms = roomDao.getTotalRoomForManager(branchId);
        int totalPages = (int) Math.ceil((double) totalRooms / pageSize);
        String startParam = request.getParameter("startDate");
        LocalDateTime checkIn;
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (startParam == null || startParam.isEmpty()) {
            checkIn = LocalDate.now().atStartOfDay();
            
        } else {
            checkIn = LocalDate.parse(startParam, formatter).atStartOfDay();
           
        }
        List<Room> rooms = roomDao.getAllRoomStatusForManager(branchId, page, pageSize, checkIn);
        List<RoomType> roomTypes = rtDao.getAllRoomType();
        Map<String, List<String>> roomImageMap = new HashMap<>();
        for (Room room : rooms) {
            String room_number = room.getRoomNumber();
            String imgFolder = request.getServletContext().getRealPath("/img/rooms").replace("build\\", "") + File.separator + room_number;
            List<String> imageUrls = new java.util.ArrayList<>();
            java.io.File folder = new java.io.File(imgFolder);
            if (folder.exists() && folder.isDirectory()) {
                for (java.io.File file : folder.listFiles()) {
                    if (file.isFile()) {
                        imageUrls.add(request.getContextPath() + room.getImageUrl() + "/" + room_number + "/" + file.getName());
                    }
                }
            }
            roomImageMap.put(room_number, imageUrls);
        }

        request.setAttribute("roomImageMap", roomImageMap);
        request.setAttribute("branchId", branchId);
        request.setAttribute("username", user.getUsername());
        request.setAttribute("userId", user.getId());
        request.setAttribute("branchname", branchname);
        request.setAttribute("rooms", rooms);
        request.setAttribute("roomtypes", roomTypes);
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("startDate", java.util.Date.from(checkIn.atZone(ZoneId.systemDefault()).toInstant()));
        request.getRequestDispatcher("roomManage.jsp").forward(request, response);
    }

}
