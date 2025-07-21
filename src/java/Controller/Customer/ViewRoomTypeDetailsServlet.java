/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Customer;

import Dal.FeedbackDAO;
import Dal.RoomTypeDAO;
import Dal.ServiceDAO;
import Model.Feedback;
import Model.RoomType;
import Model.Service;
import Model.UserAccount;
import jakarta.servlet.ServletContext;
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
import java.util.List;

/**
 *
 * @author KTC
 */
@WebServlet(name = "ViewRoomTypeDetailsServlet", urlPatterns = {"/viewRoomTypeDetail"})
public class ViewRoomTypeDetailsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String sroomTypeId = request.getParameter("roomTypeId");
        int roomTypeId = 0;
        if (sroomTypeId != null && !sroomTypeId.trim().isEmpty()) {
            roomTypeId = Integer.parseInt(sroomTypeId);
        } else {
            response.sendRedirect("./homepage");
            return;
        }

        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
        RoomType roomType = roomTypeDAO.getRoomTypeById(roomTypeId);
        List<RoomType> listSimilarRoom = roomTypeDAO.getSimilarRoomTypes(roomTypeId);
        request.setAttribute("roomType", roomType);
        request.setAttribute("listSimilarRoom", listSimilarRoom);

        //phan view feedback
        int page = 1; // trang dau tien
        int pageSize = 5; // 1 trang co 5 row
        if (request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }
        FeedbackDAO feedbackDAO = new FeedbackDAO();
        int feedbackListSize = feedbackDAO.getListFeedbackByRoomTypeId(roomTypeId).size();
        int totalPages = (int) Math.ceil((double) feedbackListSize / pageSize);
        List<Feedback> listFeedback = feedbackDAO.getListFeedbackByPage1(page, pageSize, roomTypeId);

        for (Feedback feedback : listFeedback) {
            String imgFolder = feedback.getImage_url();
           

            if (imgFolder != null && !imgFolder.trim().isEmpty()) {
                List<String> imageList = getImagesFromFolder(imgFolder, getServletContext());
                feedback.setImageList(imageList);
                
            } 
        }

        ////////////////////////////////////////////////////////////////////////
        UserAccount user = (UserAccount) request.getSession().getAttribute("user");
        request.setAttribute("user", user);

        ////////////////////////////////////////////////////////////////////////
        ServiceDAO serviceDAO = new ServiceDAO();
        List<Service> listServices = new ArrayList<>();

        RoomType room = roomTypeDAO.getRoomTypeById(roomTypeId);
        listServices = serviceDAO.getServicesByBranchId(room.getBranchId());

        request.setAttribute("listServices", listServices);

        /////////////////////////////////////////////////////////////////Service
        request.setAttribute("listFeedback", listFeedback);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("roomTypeId", roomTypeId);
        request.getRequestDispatcher("./viewRoomTypeDetail.jsp").forward(request, response);
    }

    private List<String> getImagesFromFolder(String folderPath, ServletContext context) {
        List<String> imageList = new ArrayList<>();


        // ✅ Kiểm tra cả 2 đường dẫn: web và build/web
        String realPath1 = context.getRealPath(folderPath);
        String realPath2 = context.getRealPath("").replace("build\\web\\", "web\\") + folderPath;


        File folder1 = new File(realPath1);
        File folder2 = new File(realPath2);


        // ✅ Thử cả 2 folder
        File targetFolder = null;
        if (folder1.exists() && folder1.isDirectory()) {
            targetFolder = folder1;
        } else if (folder2.exists() && folder2.isDirectory()) {
            targetFolder = folder2;
        }

        if (targetFolder != null) {
            File[] files = targetFolder.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && isImageFile(file.getName())) {
                        String imagePath = folderPath + "/" + file.getName();
                        imageList.add(imagePath);
                    }
                }
            }
        } 

        return imageList;
    }

    private boolean isImageFile(String fileName) {
        String[] imageExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};
        String lowerFileName = fileName.toLowerCase();
        for (String ext : imageExtensions) {
            if (lowerFileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

}
