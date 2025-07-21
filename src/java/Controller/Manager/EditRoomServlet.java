package Controller.Manager;

import Dal.RoomDAO;
import Dal.RoomTypeDAO;
import Model.Room;
import Model.RoomType;
import Model.UserAccount;

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
import java.nio.file.Paths;
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
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50 // 50MB
)
@WebServlet(name = "EditRoomServlet", urlPatterns = {"/editRoom"})
public class EditRoomServlet extends HttpServlet {

    RoomDAO rDao = new RoomDAO();
    RoomTypeDAO rt = new RoomTypeDAO();
    private static final String UPLOAD_DIR = "/img/rooms";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("rooms");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UserAccount user = (session != null) ? (UserAccount) session.getAttribute("user") : null;
        if (user == null) {
            request.setAttribute("error", "Please login to edit room.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }
        int branchId = rDao.getBranchId(user.getId());
        // Pagination parameters
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

        String roomIdStr = request.getParameter("room_id");
        String roomNumber = request.getParameter("room_number");
        String roomTypeIdStr = request.getParameter("room_type");
        String status = request.getParameter("status");
        String capacityStr = request.getParameter("capacity");
        String priceStr = request.getParameter("price");
        String description = request.getParameter("description");

        StringBuilder errors = new StringBuilder();

        int roomId = 0;
        int roomTypeId = 0;
        int capacity = 0;
        double price = 0;

        // Validate dữ liệu đầu vào
        try {
            if (roomIdStr == null || roomIdStr.trim().isEmpty()) {
                errors.append("Room ID is missing. ");
            } else {
                roomId = Integer.parseInt(roomIdStr);
            }
            if (roomNumber == null || roomNumber.trim().isEmpty()) {
                errors.append("Room number cannot be empty. ");
            }
            if (roomTypeIdStr == null || roomTypeIdStr.trim().isEmpty()) {
                errors.append("Please select a room type. ");
            } else {
                roomTypeId = Integer.parseInt(roomTypeIdStr);
            }
            if (status == null || !List.of("Available", "Occupied", "Booked", "Maintenance").contains(status)) {
                errors.append("Invalid status. ");
            }
            if (capacityStr == null || capacityStr.trim().isEmpty()) {
                errors.append("Capacity cannot be empty. ");
            } else {
                capacity = Integer.parseInt(capacityStr);
                if (capacity <= 0) {
                    errors.append("Capacity must be greater than 0. ");
                }
            }
            if (priceStr == null || priceStr.trim().isEmpty()) {
                errors.append("Price cannot be empty. ");
            } else {
                price = Double.parseDouble(priceStr);
                if (price <= 0) {
                    errors.append("Price must be greater than 0. ");
                }
            }
        } catch (NumberFormatException e) {
            errors.append("Invalid number format for ID, capacity, or price. ");
        }

        if (errors.length() > 0) {
            request.setAttribute("error", errors.toString());
            Room room = rDao.getRoomById(roomId);
            List<RoomType> roomTypes = rt.getAllRoomType();
            request.setAttribute("room", room);
            request.setAttribute("roomtypes", roomTypes);
            request.getRequestDispatcher("roomManage.jsp").forward(request, response);
            return;
        }

        // Xác định thư mục chứa ảnh phòng
        String uploadDir = request.getServletContext().getRealPath(UPLOAD_DIR).replace("build\\", "") + File.separator + roomNumber;
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Kiểm tra có upload ảnh mới không
        boolean hasNewImages = false;
        for (Part part : request.getParts()) {
            if (part.getName().equals("images") && part.getSize() > 0) {
                hasNewImages = true;
                break;
            }
        }

        // Nếu có upload ảnh mới thì xóa toàn bộ ảnh cũ
        if (hasNewImages && dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isFile()) {
                    file.delete();
                }
            }
        }

        // Nếu có ảnh mới thì lưu
        if (hasNewImages) {
            for (Part part : request.getParts()) {
                if (part.getName().equals("images") && part.getSize() > 0) {
                    String fileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
                    String filePath = uploadDir + File.separator + fileName;
                    part.write(filePath);
                }
            }
        }

        // Tạo đối tượng Room
        Room room = new Room();
        room.setId(roomId);
        room.setRoomNumber(roomNumber);
        room.setRoomTypeId(roomTypeId);
        room.setBranchId(branchId);
        room.setStatus(status);
        room.setImageUrl(UPLOAD_DIR);
        System.out.println("Update Room: id=" + room.getId()
                + ", number=" + roomNumber
                + ", typeId=" + roomTypeId
                + ", branchId=" + branchId
                + ", status=" + status
                + ", imageUrl=" + UPLOAD_DIR);
        // update trong database
        boolean update = rDao.updateRoom(room);
        if (update) {
            // Cập nhật RoomType (nếu cần)
            rt.updateRoomType(roomTypeId, price, capacity, description);
            String redirectUrl = "rooms?page=" + page + "&size=" + pageSize;
            response.sendRedirect(redirectUrl);
        } else {
            request.setAttribute("error", "Failed to update room.");
            int totalRooms = rDao.getTotalRoomForManager(branchId);
            int totalPages = (int) Math.ceil((double) totalRooms / pageSize);
            List<Room> rooms = rDao.getAllRoomStatusForManager(branchId, page, pageSize, checkIn);
            Room roomObj = rDao.getRoomById(roomId);
            List<RoomType> roomTypes = rt.getAllRoomType();
            Map<String, List<String>> roomImageMap = new HashMap<>();
            for (Room r : rooms) {
                String room_number = r.getRoomNumber();
                String imgFolder = request.getServletContext().getRealPath(UPLOAD_DIR).replace("build\\", "") + File.separator + room_number;
                List<String> imageUrls = new ArrayList<>();
                File folder = new File(imgFolder);
                if (folder.exists() && folder.isDirectory()) {
                    for (File file : folder.listFiles()) {
                        if (file.isFile()) {
                            imageUrls.add(request.getContextPath() + r.getImageUrl() + "/" + room_number + "/" + file.getName());
                        }
                    }
                }
                roomImageMap.put(room_number, imageUrls);
            }

            request.setAttribute("roomImageMap", roomImageMap);
            request.setAttribute("branchId", branchId);
            request.setAttribute("username", user.getUsername());
            request.setAttribute("userId", user.getId());
            request.setAttribute("branchname", rDao.getBranchNameById(user.getId()));
            request.setAttribute("rooms", rooms);
            request.setAttribute("room", roomObj);
            request.setAttribute("roomtypes", roomTypes);
            request.setAttribute("currentPage", page);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("startDate", java.util.Date.from(checkIn.atZone(ZoneId.systemDefault()).toInstant()));
            request.getRequestDispatcher("roomManage.jsp").forward(request, response);
        }
    }

}
