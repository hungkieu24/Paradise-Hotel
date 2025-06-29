/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Manager;

import Dal.RoomDAO;
import Dal.ServiceDAO;
import Model.Service;
import Model.UserAccount;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
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
@WebServlet(name = "ServiceServletManage", urlPatterns = {"/serviceManage"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50 // 50MB
)
public class ServiceServletManage extends HttpServlet {

    private RoomDAO r = new RoomDAO();
    private ServiceDAO s = new ServiceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");

        if (user != null) {
            //lấy tên chi nhánh và tên manager
            String username = user.getUsername();
            String userId = user.getId();

            String branchName = r.getBranchNameById(userId);
            int branchId = r.getBranchId(userId);
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
            // total service
            int totalService = s.getTotalServicesByBranchId(branchId);
            int totalPages = (int) Math.ceil((double) totalService / pageSize);
            // lay list service
            List<Service> services = s.getServicesByBranchId(branchId, page, pageSize);
            // Image handling
            Map<Integer, List<String>> serviceImageMap = new HashMap<>();
            for (Service service : services) {
                String serviceId = String.valueOf(service.getId());
                String imgFolder = request.getServletContext().getRealPath("/img/services").replace("build\\", "") + File.separator + serviceId;
                List<String> imageUrls = new ArrayList<>();
                File folder = new File(imgFolder);
                if (folder.exists() && folder.isDirectory()) {
                    for (File file : folder.listFiles()) {
                        if (file.isFile()) {
                            imageUrls.add(request.getContextPath() + "/img/services/" + serviceId + "/" + file.getName());
                        }
                    }
                }
                serviceImageMap.put(service.getId(), imageUrls);
            }
            System.out.println("Services size: " + services.size());
            for (Service s : services) {
                System.out.println("Service: " + s.getName());
            }
            System.out.println("Branch ID from RoomDAO: " + branchId);

            //set thuoc tinh
            request.setAttribute("serviceImageMap", serviceImageMap);
            request.setAttribute("branchId", branchId);
            request.setAttribute("username", username);
            request.setAttribute("branchname", branchName);
            request.setAttribute("userId", userId);
            request.setAttribute("currentPage", page);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("services", services);
            request.getRequestDispatcher("serviceManage.jsp").forward(request, response);

        } else {
            request.setAttribute("error", "don't see user");
            response.sendRedirect("login");
        }

    }

}
