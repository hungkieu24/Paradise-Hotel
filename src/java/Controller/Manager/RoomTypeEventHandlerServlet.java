/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Manager;

import Dal.AmenityDAO;
import Dal.RoomTypeDAO;
import Model.Amenity;
import Model.RoomType;
import Utility.UploadMultyImage;
import com.google.gson.Gson;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author hungk
 */
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
        maxFileSize = 1024 * 1024 * 10, // 10 MB
        maxRequestSize = 1024 * 1024 * 50 // 50 MB
)
@WebServlet(name = "RoomTypeEventHandlerServlet", urlPatterns = {"/manager/roomTypeEventHandler"})
public class RoomTypeEventHandlerServlet extends HttpServlet {

    private final UploadMultyImage uploader = new UploadMultyImage();
    private final RoomTypeDAO roomTypeDAO = new RoomTypeDAO();

    private final String ROOMTYPE_PAGE = "./roomType";
    private final String HOTEL_BRANCH_IMAGE_FOLDER_PREFIX = "/HotelBranch_";
    private final String ROOM_TYPE_IMAGE_FOLDER_PREFIX = "/RoomType_";
    private static final String COL_ROOM_TYPE_NAME = "name";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int roomTypeID = Integer.parseInt(request.getParameter("roomTypeID"));
        RoomType roomType = roomTypeDAO.getRoomTypeById(roomTypeID);
        List<String> imagePaths = null;

        String folderPath = getServletContext().getRealPath(roomType.getImage_url());
        if (folderPath != null) {
            File folder = new File(folderPath);
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif)$"));

            if (files != null) {
                imagePaths = new ArrayList<>();
                for (File file : files) {
                    imagePaths.add("../" + roomType.getImage_url() + "/" + file.getName());
                }
            }
        }

        // Lấy tiện ích
        AmenityDAO amenityDAO = new AmenityDAO();
        List<Amenity> allAmenities = amenityDAO.getAllAmenityByBranchId(roomType.getBranchId());
        List<Integer> selectedAmenityIds = amenityDAO.getAmenityIdsByRoomTypeId(roomTypeID);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("roomType", roomType);
        responseData.put("images", imagePaths);
        responseData.put("allAmenities", allAmenities);
        responseData.put("selectedAmenityIds", selectedAmenityIds);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Gson gson = new Gson();
        response.getWriter().write(gson.toJson(responseData));

    }

    private void setSessionMessage(HttpSession session, String message, String type) {
        session.setAttribute("message", message);
        session.setAttribute("messageType", type);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        String branchIDStr = request.getParameter("branchID");
        String roomTypeName = request.getParameter("roomTypeName");
        String basePriceStr = request.getParameter("basePrice");
        String capacityAdultStr = request.getParameter("capacityAdult");
        String capacityChildStr = request.getParameter("capacityChild");
        String description = request.getParameter("description");
        String uploadParamName = "roomTypeImgs";
        String[] amenityIds = request.getParameterValues("amenityIds");

        int branchID = 0;
        if (branchIDStr != null) {
            branchID = Integer.parseInt(branchIDStr.trim());
        }
        
        double basePrice = 0;
        if (basePriceStr != null) {
            basePrice = Double.parseDouble(basePriceStr.trim());
        }
        
        int capacityAdult = 0;
        if (capacityAdultStr != null) {
           capacityAdult = Integer.parseInt(capacityAdultStr.trim()); 
        }
        
        int capacityChild = 0;
        if (capacityChildStr != null) {
            capacityChild = Integer.parseInt(capacityChildStr.trim());
        }
         

        List<Integer> amenityIdList = new ArrayList<>();
        if (amenityIds != null) {
            for (String idStr : amenityIds) {
                try {
                    amenityIdList.add(Integer.parseInt(idStr.trim()));
                } catch (NumberFormatException e) {
                    // Bỏ qua ID không hợp lệ
                    e.printStackTrace(); // hoặc log nếu dùng logger
                }
            }
        }
        RoomType roomType = new RoomType(roomTypeName, description, basePrice,
                capacityAdult, capacityChild, null, branchID);

        if (action != null && action.equals("add")) {
            handleAddRoomType(request, session, roomType, amenityIdList, uploadParamName, branchID);
            response.sendRedirect(ROOMTYPE_PAGE);
            return;
        }

        if (action != null && action.equals("edit")) {
            int roomTypeID = Integer.parseInt(request.getParameter("roomTypeID").trim());
            roomType.setRoomTypeID(roomTypeID);
            handleEditRoomType(request, session, roomType, amenityIdList, uploadParamName, branchID);
            response.sendRedirect(ROOMTYPE_PAGE);
            return;
        }

        if (action != null && action.equals("delete")) {
            int roomTypeID = Integer.parseInt(request.getParameter("IdDelete").trim());
            boolean success = roomTypeDAO.deleteRoomType(roomTypeID);
            setSessionMessage(session, success ? "Delete room type successful!" : "Failure to delete room type!",
                    success ? "success" : "error");
            response.sendRedirect(ROOMTYPE_PAGE);
            return;
        }
    }

    private void handleAddRoomType(HttpServletRequest request, HttpSession session, RoomType roomType,
            List<Integer> amenityIdList, String uploadParamName, int branchID) throws ServletException, IOException {
        boolean isExistRoomTypeName = roomTypeDAO.isFieldExists(COL_ROOM_TYPE_NAME, roomType.getName(), null);
        if (isExistRoomTypeName) {
            setSessionMessage(session, "Room type name already exists", "error");
            return;
        }

        // Tạo folder lưu ảnh
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        String folderName = HOTEL_BRANCH_IMAGE_FOLDER_PREFIX + branchID + ROOM_TYPE_IMAGE_FOLDER_PREFIX + timestamp;

        String UPLOAD_DIR = "/img" + folderName;
        String pathHost = getServletContext().getRealPath("");
        String serverUploadPath = pathHost.replace("build\\", "") + UPLOAD_DIR;
        String buildUploadPath = pathHost + UPLOAD_DIR;

        // Upload ảnh vào cả folder gốc và build
        uploader.uploadImages(request, uploadParamName, serverUploadPath);
        uploader.uploadImages(request, uploadParamName, buildUploadPath);

        // Set path ảnh vào roomType
        roomType.setImage_url(UPLOAD_DIR);

        // Insert DB
        boolean success = false;
        if (amenityIdList != null && !amenityIdList.isEmpty()) {
            success = roomTypeDAO.insertRoomTypeWithAmenities(roomType, amenityIdList);
        } else {
            success = roomTypeDAO.insertRoomType(roomType);
        }

        // Đặt thông báo session
        setSessionMessage(session, success ? "Add room type successful!" : "Failure to add room type!",
                success ? "success" : "error");
    }

    private void handleEditRoomType(HttpServletRequest request, HttpSession session, RoomType roomType,
            List<Integer> amenityIdList, String uploadParamName, int branchID) throws ServletException, IOException {
        boolean isExistRoomTypeName = roomTypeDAO.isFieldExists(COL_ROOM_TYPE_NAME, roomType.getName(), roomType.getRoomTypeID());
        if (isExistRoomTypeName) {
            setSessionMessage(session, "Room type name already exists", "error");
            return;
        }
        // Tạo folder lưu ảnh
        String folderName = HOTEL_BRANCH_IMAGE_FOLDER_PREFIX + branchID + ROOM_TYPE_IMAGE_FOLDER_PREFIX + roomType.getName();

        String UPLOAD_DIR = "/img" + folderName;
        String pathHost = getServletContext().getRealPath("");
        String serverUploadPath = pathHost.replace("build\\", "") + UPLOAD_DIR;
        String buildUploadPath = pathHost + UPLOAD_DIR;

        // Upload ảnh vào cả folder gốc và build
        uploader.uploadImages(request, uploadParamName, serverUploadPath);
        uploader.uploadImages(request, uploadParamName, buildUploadPath);

        // Set path ảnh vào roomType
        roomType.setImage_url(UPLOAD_DIR);

        // Insert DB
        boolean success = false;
        if (amenityIdList != null && !amenityIdList.isEmpty()) {
            success = roomTypeDAO.updateRoomTypeWithAmenities(roomType, amenityIdList);
        } else {
            success = roomTypeDAO.updateRoomType(roomType);
        }

        // Đặt thông báo session
        setSessionMessage(session, success ? "Update room type successful!" : "Failure to update room type!",
                success ? "success" : "error");
    }

}
