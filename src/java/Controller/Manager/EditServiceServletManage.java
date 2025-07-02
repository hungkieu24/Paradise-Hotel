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
import jakarta.servlet.http.Part;
import java.io.File;

/**
 *
 * @author thien
 */
@WebServlet(name = "EditServiceServletManage", urlPatterns = {"/editService"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50 // 50MB
)
public class EditServiceServletManage extends HttpServlet {

    private ServiceDAO serviceDAO = new ServiceDAO();
    private RoomDAO r = new RoomDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user != null) {
            String serviceIdStr = request.getParameter("serviceId");
            try {
                int serviceId = Integer.parseInt(serviceIdStr);
                Service service = serviceDAO.getServiceById(serviceId);
                if (service != null) {
                    request.setAttribute("service", service);
                    request.setAttribute("username", user.getUsername());
                    request.setAttribute("branchname", r.getBranchNameById(user.getId()));
                    request.setAttribute("branchId", r.getBranchId(user.getId()));
                    request.getRequestDispatcher("serviceManage.jsp").forward(request, response);
                } else {
                    request.setAttribute("error", "Service not found");
                    response.sendRedirect("services");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid service ID");
                request.getRequestDispatcher("serviceManage.jsp").forward(request, response);
            }
        } else {
            request.setAttribute("error", "User not logged in");
            response.sendRedirect("login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user != null) {
            String serviceIdStr = request.getParameter("service_id");
            String serviceName = request.getParameter("service_name");
            String status = request.getParameter("status");
            String priceStr = request.getParameter("price");
            String description = request.getParameter("description");

            // Validate inputs
            int serviceId;
            try {
                serviceId = Integer.parseInt(serviceIdStr);
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid service ID");
                request.getRequestDispatcher("serviceManage.jsp").forward(request, response);
                return;
            }

            if (serviceName == null || serviceName.trim().isEmpty()) {
                request.setAttribute("error", "Service name cannot be empty");
                request.getRequestDispatcher("serviceManage.jsp").forward(request, response);
                return;
            }
            if (status == null || (!status.equals("Active") && !status.equals("Inactive"))) {
                request.setAttribute("error", "Invalid status");
                request.getRequestDispatcher("serviceManage.jsp").forward(request, response);
                return;
            }
            double price;
            try {
                price = Double.parseDouble(priceStr);
                if (price <= 0) {
                    request.setAttribute("error", "Price must be greater than 0");
                    request.getRequestDispatcher("serviceManage.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid price format");
                request.getRequestDispatcher("serviceManage.jsp").forward(request, response);
                return;
            }

            // Handle file upload
            String imageUrl = null;
            Part filePart = request.getPart("image");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = filePart.getSubmittedFileName();
                String uploadPath = getServletContext().getRealPath("/img/services").replace("build\\", "") + File.separator + serviceId;
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                String filePath = uploadPath + File.separator + fileName;
                filePart.write(filePath);
                imageUrl = "/img/services/" + serviceId;
            }

            // Update service
            Service service = new Service();
            service.setId(serviceId);
            service.setName(serviceName);
            service.setDescription(description);
            service.setPrice(price);
            service.setBranchId(r.getBranchId(user.getId()));
            service.setStatus(status);
            if (imageUrl != null) {
                service.setImageUrl(imageUrl);
            } else {
                Service existingService = serviceDAO.getServiceById(serviceId);
                service.setImageUrl(existingService.getImageUrl());
            }
            service.setDeleted(false);

            // Save to database
            boolean success = serviceDAO.updateService(service);
            if (success) {
                request.setAttribute("success", "Service updated successfully");
            } else {
                request.setAttribute("error", "Failed to update service");
            }

            request.getRequestDispatcher("serviceManage.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "User not logged in");
            response.sendRedirect("login");
        }
    }

}
