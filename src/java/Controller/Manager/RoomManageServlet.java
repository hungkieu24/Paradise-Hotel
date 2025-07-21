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
import Utility.UploadImage;
import Utility.UploadMultyImage;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author thien
 */
@WebServlet(name = "RoomManageServlet", urlPatterns = {"/rooms"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50 // 50MB
)
public class RoomManageServlet extends HttpServlet {

    private RoomDAO r = new RoomDAO();
    private RoomTypeDAO rt = new RoomTypeDAO();
    private UploadImage uploadImage = new UploadImage();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");

        if (user == null) {
            request.setAttribute("error", "don't see user");
            response.sendRedirect("login.jsp");
            return;
        }

        // Lấy tên chi nhánh và tên manager
        String username = user.getUsername();
        String userId = user.getId();

        String branchname = r.getBranchNameById(userId);
        int branchId = r.getBranchId(userId);
        String startParam = request.getParameter("startDate");
        LocalDateTime checkIn;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (startParam == null || startParam.isEmpty()) {
            checkIn = LocalDate.now().atStartOfDay();

        } else {
            checkIn = LocalDate.parse(startParam, formatter).atStartOfDay();

        }
        // phan trang
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
        // total room
        int totalRooms = r.getTotalRoomForManager(branchId);
        int totalPages = (int) Math.ceil((double) totalRooms / pageSize);
        //lấy list room và roomtype

        List<Room> rooms = r.getAllRoomStatusForManager(branchId, page, pageSize, checkIn);
        List<RoomType> roomtypes = rt.getAllRoomType();
        // Dùng map để lấy list ảnh của từng phòng
        Map<String, List<String>> roomImageMap = new HashMap<>();
        for (Room room : rooms) {
            String room_number = room.getRoomNumber();
            String imgFolder = request.getServletContext().getRealPath("/img/rooms").replace("build\\", "") + File.separator + room_number;// chu ys real paths
            List<String> imageUrls = new ArrayList();
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

        // set thuộc tính
        request.setAttribute("roomImageMap", roomImageMap);
        request.setAttribute("branchId", branchId);
        request.setAttribute("username", username);
        request.setAttribute("userId", userId);
        request.setAttribute("branchname", branchname);
        request.setAttribute("rooms", rooms);
        request.setAttribute("roomtypes", roomtypes);
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("startDate", java.util.Date.from(checkIn.atZone(ZoneId.systemDefault()).toInstant()));
        request.getRequestDispatcher("roomManage.jsp").forward(request, response);

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

        // Lấy branchId từ user (hoặc context)
        String userId = user.getId();
        int branchId = r.getBranchId(userId);

        // Lấy dữ liệu từ form
        String roomNumber = request.getParameter("room_number");
        String roomTypeParam = request.getParameter("room_type");
        String status = request.getParameter("status");
        String capacityParam = request.getParameter("capacity");
        String priceParam = request.getParameter("price");
        String description = request.getParameter("description");

        StringBuilder errors = new StringBuilder();
//        boolean[] roomExist = r.isRoomnumberExist(roomNumber, branchId);
//        if (roomExist[0]) {
//            errors.append("Room number already exists and is active. ");
//        } else if (roomExist[1]) {
//            request.setAttribute("warning", "Reusing a soft-deleted room number. Proceed with caution.");
//        }
        boolean checkExist = r.isRoomNumberExist(roomNumber, branchId);
        if (checkExist) {
            errors.append("room number exist.");
        }
        if (branchId <= 0) {
            errors.append("Invalid branch ID. ");
        }
        // Parse các giá trị số
        int roomTypeId = 0;
        int capacity = 0;
        double price = 0;

        try {
            if (roomTypeParam != null && !roomTypeParam.trim().isEmpty()) {
                roomTypeId = Integer.parseInt(roomTypeParam);
            }
            if (capacityParam != null && !capacityParam.trim().isEmpty()) {
                capacity = Integer.parseInt(capacityParam);
            }
            if (priceParam != null && !priceParam.trim().isEmpty()) {
                price = Double.parseDouble(priceParam);
            }
        } catch (NumberFormatException e) {
            errors.append("Invalid number format for room type, capacity, or price. ");
        }

        // Validate sau khi parse
        if (roomTypeId <= 0) {
            errors.append("Invalid room type. ");
        }
        if (capacity <= 0) {
            errors.append("Capacity must be greater than 0. ");
        }
        if (price <= 0) {
            errors.append("Price must be greater than 0. ");
        }

        if (errors.length() > 0) {
            request.setAttribute("error", errors.toString());
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
            String startParam = request.getParameter("startDate");
            LocalDateTime checkIn;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            if (startParam == null || startParam.isEmpty()) {
                checkIn = LocalDate.now().atStartOfDay();

            } else {
                checkIn = LocalDate.parse(startParam, formatter).atStartOfDay();

            }

            int totalRooms = r.getTotalRoomForManager(branchId);
            int totalPages = (int) Math.ceil((double) totalRooms / pageSize);
            List<Room> rooms = r.getAllRoomStatusForManager(branchId, page, pageSize, checkIn);
            List<RoomType> roomTypes = rt.getAllRoomType();
            Map<String, List<String>> roomImageMap = new HashMap<>();
            for (Room room : rooms) {
                String room_number = room.getRoomNumber();
                String imgFolder = request.getServletContext().getRealPath("/img/rooms").replace("build\\", "") + File.separator + room_number;
                List<String> imageUrls = new ArrayList();
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
            request.setAttribute("branchId", branchId);
            request.setAttribute("username", user.getUsername());
            request.setAttribute("userId", user.getId());
            request.setAttribute("branchname", r.getBranchNameById(user.getId()));
            request.setAttribute("rooms", rooms);
            request.setAttribute("roomtypes", roomTypes);
            request.setAttribute("currentPage", page);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("startDate", java.util.Date.from(checkIn.atZone(ZoneId.systemDefault()).toInstant()));
            request.getRequestDispatcher("roomManage.jsp").forward(request, response);
            return;
        }

        // Tạo đối tượng Room
        Room room = new Room();
        room.setRoomNumber(roomNumber);
        room.setRoomTypeId(roomTypeId);
        room.setBranchId(branchId);
        room.setStatus(status);
        room.setImageUrl("/img/rooms");
        // Thêm Room vào cơ sở dữ liệu
        boolean add = r.addRoom(room);
        int roomId = r.getRoomIdByRoomNumberAndBranchId(roomNumber, branchId);
        if (add && roomId > 0) {
            // tao thu muc cho phong nay
            String uploadDir = request.getServletContext().getRealPath(room.getImageUrl()).replace("build\\", "") + File.separator + room.getRoomNumber();
            System.out.println("UPLOAD DIR: " + uploadDir);  // debug
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // upload tat car anh vao thu muc
            for (Part part : request.getParts()) {
                if (part.getName().equals("images") && part.getSize() > 0) {
                    String fileName = java.nio.file.Paths.get(part.getSubmittedFileName()).getFileName().toString();
                    String filePath = uploadDir + File.separator + fileName;
                    part.write(filePath);
                }
            }
            // Cập nhật RoomType (nếu cần)
            rt.updateRoomType(roomTypeId, price, capacity, description);
            request.setAttribute("success", "Add new room successfully!");
            response.sendRedirect("rooms");
        } else {
            request.setAttribute("error", "Failed to add new room.");
            int page = 1;
            int pageSize = 5;
            String pageParam = request.getParameter("page");
            String sizeParam = request.getParameter("size");
            String startParam = request.getParameter("startDate");
            LocalDateTime checkIn;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            if (startParam == null || startParam.isEmpty()) {
                checkIn = LocalDate.now().atStartOfDay();

            } else {
                checkIn = LocalDate.parse(startParam, formatter).atStartOfDay();

            }
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

            int totalRooms = r.getTotalRoomForManager(branchId);
            int totalPages = (int) Math.ceil((double) totalRooms / pageSize);
            List<Room> rooms = r.getAllRoomStatusForManager(branchId, page, pageSize, checkIn);
            List<RoomType> roomTypes = rt.getAllRoomType();
            Map<String, List<String>> roomImageMap = new HashMap<>();
            for (Room r : rooms) {
                String room_number = r.getRoomNumber();
                String imgFolder = request.getServletContext().getRealPath("/img/rooms").replace("build\\", "") + File.separator + room_number;
                List<String> imageUrls = new ArrayList();
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
            request.setAttribute("branchId", branchId);
            request.setAttribute("username", user.getUsername());
            request.setAttribute("userId", user.getId());
            request.setAttribute("branchname", r.getBranchNameById(user.getId()));
            request.setAttribute("rooms", rooms);
            request.setAttribute("roomtypes", roomTypes);
            request.setAttribute("currentPage", page);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("startDate", java.util.Date.from(checkIn.atZone(ZoneId.systemDefault()).toInstant()));
            request.getRequestDispatcher("roomManage.jsp").forward(request, response);
        }
    }

}
