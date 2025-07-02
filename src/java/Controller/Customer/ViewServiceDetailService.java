/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Customer;

import Dal.BranchDAO;
import Dal.HotelBranchDAO;
import Dal.ServiceDAO;
import Model.Branch;
import Model.Service;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 * @author KTC
 */
@WebServlet(name = "ViewServiceDetailService", urlPatterns = {"/viewServiceDetail"})
public class ViewServiceDetailService extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String serviceIdStr = request.getParameter("serviceId");
        int serviceId = 0;
        if (serviceIdStr != null && !serviceIdStr.trim().isEmpty()) {
            serviceId = Integer.parseInt(serviceIdStr);
        } else {
            response.sendRedirect("./homepage");
            return;
        }

        ServiceDAO serviceDAO = new ServiceDAO();
        Service service = serviceDAO.getServiceById1(serviceId);

        if (service == null) {
            response.sendRedirect("./homepage");
            return;
        }

        BranchDAO branchDAO = new BranchDAO();
        Branch branch = branchDAO.getBranchById(service.getBranchId());

        List<Service> relatedServices = serviceDAO.getServicesByBranchIdExcept(service.getBranchId(), service.getId());

        request.setAttribute("service", service);
        request.setAttribute("branch", branch);
        request.setAttribute("relatedServices", relatedServices);
        request.getRequestDispatcher("viewServiceDetail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

}
