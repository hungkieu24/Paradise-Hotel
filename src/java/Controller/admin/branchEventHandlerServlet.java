/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.admin;

import Dal.HotelBranchDAO;
import Dal.UserAccountDAO;
import Model.HotelBranch;
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
@WebServlet(name = "branchEventHandlerServlet", urlPatterns = {"/admin/branchEventHandler"})
public class branchEventHandlerServlet extends HttpServlet {

    private final UploadMultyImage uploader = new UploadMultyImage();
    private final UserAccountDAO accountDAO = new UserAccountDAO();
    private final HotelBranchDAO branchDAO = new HotelBranchDAO();

    private final String BRANCH_PAGE = "./branch";
    private final String HOTEL_BRANCH_IMAGE_FOLDER_PREFIX = "HotelBranch_";
    private static final String COL_EMAIL = "email";
    private static final String COL_PHONE = "phone";
    private static final String COL_ADDRESS = "address";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int branchID = Integer.parseInt(request.getParameter("branchID"));
        HotelBranch branch = branchDAO.getHotelBranchById(branchID);

        List<String> imagePaths = null;

        String folderPath = getServletContext().getRealPath(branch.getImage_url());
        if (folderPath != null) {
            File folder = new File(folderPath);
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif)$"));

            if (files != null) {
                imagePaths = new ArrayList<>();
                for (File file : files) {
                    imagePaths.add("../" + branch.getImage_url() + "/" + file.getName());
                }
            }
        }

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("branch", branch);
        responseData.put("images", imagePaths);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Gson gson = new Gson();
        response.getWriter().write(gson.toJson(responseData));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        String branchIDString = request.getParameter("branchID");
        String staffID = request.getParameter("staffID");
        String branchName = request.getParameter("branchName");
        String branchPhone = request.getParameter("branchPhone");
        String branchEmail = request.getParameter("branchEmail");
        String branchAddress = request.getParameter("branchAddress");
        String uploadParamName = "branchImgs";

        if (branchIDString == null && action.equals("edit")) {
            setSessionMessage(session, "BranchID is null.", "error");
            response.sendRedirect(BRANCH_PAGE);
            return;
        }

        if (branchIDString != null && action.equals("edit")) {
            int branchID = Integer.parseInt(branchIDString);
            HotelBranch existingBranch = branchDAO.getHotelBranchByIdSimple(branchID);

            boolean isValidInformation = validateForUpdate(branchID ,branchEmail, branchAddress, branchPhone, request);
            if (!isValidInformation) {
                response.sendRedirect(BRANCH_PAGE);
                return;
            }
            
            updateBranch(request, session, branchDAO, existingBranch,
                    branchName, branchAddress, branchPhone,
                    branchEmail, staffID, uploadParamName);
            response.sendRedirect(BRANCH_PAGE);
            return;
        }

        if (action.equals("add")) {
            String ownerId = request.getParameter("ownerId");
            String specificAddress = request.getParameter("specificAddress");
            String address = specificAddress + ", " + branchAddress;

            boolean isValidInformation = validateForAdd(branchEmail, branchAddress, branchPhone, request);
            if (!isValidInformation) {
                response.sendRedirect(BRANCH_PAGE);
                return;
            }

            addBranch(request, session, branchDAO, branchName,
                    address, branchPhone, branchEmail,
                    ownerId, staffID, uploadParamName);
            response.sendRedirect(BRANCH_PAGE);
            return;
        }

        if (action.equals("delete")) {
            String branchIDDeleteString = request.getParameter("IdDelete");
            if (branchIDDeleteString == null || branchIDDeleteString.isEmpty()) {
                setSessionMessage(session, "BranchID is null.", "error");
                response.sendRedirect(BRANCH_PAGE);
                return;
            }
            int branchIDDelete = Integer.parseInt(branchIDDeleteString);
            boolean deleted = branchDAO.deleteHotelBranch(branchIDDelete);
            setSessionMessage(session, deleted ? "Delete successful!" : "Cannot delete, there may be rooms booked here!",
                    deleted ? "success" : "error");
            response.sendRedirect(BRANCH_PAGE);
            return;
        }
    }

    public boolean validateForAdd(String email, String address, String phone, HttpServletRequest request) {
        HttpSession session = request.getSession();

        if (branchDAO.isFieldExists(COL_EMAIL, email, null)) {
            setSessionMessage(session, "Email already exists!", "error");
            return false;
        }

        if (branchDAO.isFieldExists(COL_ADDRESS, address, null)) {
            setSessionMessage(session, "Address already exists!", "error");
            return false;
        }

        if (branchDAO.isFieldExists(COL_PHONE, phone, null)) {
            setSessionMessage(session, "Phone already exists!", "error");
            return false;
        }

        return true;
    }

    public boolean validateForUpdate(int id, String email, String address, String phone, HttpServletRequest request) {
        HttpSession session = request.getSession();

        if (branchDAO.isFieldExists(COL_EMAIL, email, id)) {
            setSessionMessage(session, "Email already exists!", "error");
            return false;
        }

        if (branchDAO.isFieldExists(COL_ADDRESS, address, id)) {
            setSessionMessage(session, "Address already exists!", "error");
            return false;
        }

        if (branchDAO.isFieldExists(COL_PHONE, phone, id)) {
            setSessionMessage(session, "Phone already exists!", "error");
            return false;
        }

        return true;
    }

    private void updateBranch(HttpServletRequest request, HttpSession session, HotelBranchDAO dao, HotelBranch oldBranch,
            String name, String address, String phone, String email, String newManagerID, String uploadParamName) throws ServletException, IOException {

        // Nếu có newManagerID và nó khác với manager hiện tại, thì cập nhật vai trò
        if (newManagerID != null && !newManagerID.isEmpty() && !newManagerID.equals(oldBranch.getManager_id())) {
            if (!accountDAO.updateUserRoleToManager(newManagerID)) {
                setSessionMessage(session, "Failure to update role!", "error");
                return;
            }
        } else {
            newManagerID = oldBranch.getManager_id(); // giữ nguyên nếu không thay đổi
        }

        String UPLOAD_DIR = HOTEL_BRANCH_IMAGE_FOLDER_PREFIX + oldBranch.getId();
        String pathHost = getServletContext().getRealPath("");
        String serverUploadPath = pathHost.replace("build\\", "") + UPLOAD_DIR;
        String buildUploadPath = pathHost + UPLOAD_DIR;
        uploader.uploadImages(request, uploadParamName, serverUploadPath);
        uploader.uploadImages(request, uploadParamName, buildUploadPath);

        HotelBranch branch = new HotelBranch(oldBranch.getId(), name, address, phone, email,
                UPLOAD_DIR, oldBranch.getOwner_id(), newManagerID);

        boolean updated = dao.updateHotelBranch(branch);
        setSessionMessage(session, updated ? "Update successful!" : "Failure to update!",
                updated ? "success" : "error");
    }

    private void addBranch(HttpServletRequest request, HttpSession session, HotelBranchDAO dao,
            String name, String address, String phone, String email,
            String ownerID, String managerID, String uploadParamName) throws ServletException, IOException {

        // Nếu có managerID thì cập nhật role trước
        if (managerID != null && !managerID.isEmpty()) {
            if (!accountDAO.updateUserRoleToManager(managerID)) {
                setSessionMessage(session, "Failure to update role!", "error");
                return;
            }
        } else {
            managerID = null;
        }

        // Tạo folder mới theo timestamp để upload ảnh
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        String folderName = HOTEL_BRANCH_IMAGE_FOLDER_PREFIX + timestamp;

        String UPLOAD_DIR = "/img/" + folderName;
        String pathHost = getServletContext().getRealPath("");
        String serverUploadPath = pathHost.replace("build\\", "") + UPLOAD_DIR;
        String buildUploadPath = pathHost + UPLOAD_DIR;
        uploader.uploadImages(request, uploadParamName, serverUploadPath);
        uploader.uploadImages(request, uploadParamName, buildUploadPath);

        HotelBranch newBranch = new HotelBranch(0, name, address, phone, email,
                UPLOAD_DIR, ownerID, managerID);

        boolean success = dao.addHotelBranch(newBranch);
        setSessionMessage(session, success ? "Add branch successful!" : "Failure to add branch!",
                success ? "success" : "error");
    }

    private void setSessionMessage(HttpSession session, String message, String type) {
        session.setAttribute("message", message);
        session.setAttribute("messageType", type);
    }
}
