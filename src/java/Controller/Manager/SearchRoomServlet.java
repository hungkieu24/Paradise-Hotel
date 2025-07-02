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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author thien
 */
@WebServlet(name = "SearchRoomServlet", urlPatterns = {"/searchRooms"})
public class SearchRoomServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null || session == null) {
            request.setAttribute("error", "can you login");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }
        if (session.getAttribute("username") == null) {
            session.setAttribute("username", user.getUsername());
        }

        RoomTypeDAO rtDao = new RoomTypeDAO();
        RoomDAO rDao = new RoomDAO();
        if (session.getAttribute("branchname") == null) {
            session.setAttribute("branchname", rDao.getBranchNameById(user.getId()));
        }
        int branchId = rDao.getBranchId(user.getId());
        List<RoomType> roomTypes = rtDao.getAllRoomType();
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

        // lay phong cho bo loc
        String status = request.getParameter("status");
        String roomType = request.getParameter("roomType");
        String search = request.getParameter("search");
        // Get total matching rooms for pagination
        int totalRooms = rDao.getTotalRoomsByBranch(branchId, status, roomType, search);
        int totalPages = (int) Math.ceil((double) totalRooms / pageSize);

        List<Room> rooms = rDao.getRoomsByBranch(branchId, status, roomType, search, page, pageSize);
        if(rooms.isEmpty()){
            request.setAttribute("warning", "No rooms found matching filter criteria.");
        }
        // Build roomImageMap
        Map<String, List<String>> roomImageMap = new HashMap<>();
        for (Room room : rooms) {
            String room_number = room.getRoomNumber();
            String imgFolder = request.getServletContext().getRealPath("/img/rooms").replace("build\\", "") + File.separator + room_number;
            List<String> imageUrls = new ArrayList<>();
            File folder = new File(imgFolder);
            if (folder.exists() && folder.isDirectory()) {
                for (File file : folder.listFiles()) {
                    if (file.isFile()) {
                        imageUrls.add(request.getContextPath() + room.getImageUrl() + "/" + room_number + "/" + file.getName());
                    }
                }
            }
            roomImageMap.put(room_number, imageUrls);
        }
        request.setAttribute("roomImageMap", roomImageMap);
        request.setAttribute("rooms", rooms);
        request.setAttribute("roomtypes", roomTypes);
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalPages", totalPages);
        request.getRequestDispatcher("roomManage.jsp").forward(request, response);
    }

}
