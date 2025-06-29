/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Manager;

import Dal.ServiceDAO;
import Model.UserAccount;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author thien
 */
@WebServlet(name = "DeleteServiceServletManage", urlPatterns = {"/deleteService"})
public class DeleteServiceServletManage extends HttpServlet {

    private ServiceDAO serviceDAO = new ServiceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("serviceManage");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("doPost called for DeleteServiceServletManage");
        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user != null) {
            String serviceIdStr = request.getParameter("serviceId");
            try {
                int serviceId = Integer.parseInt(serviceIdStr);
                if (serviceDAO.isServiceInUse(serviceId)) {
                    request.setAttribute("warning", "Cannot delete service because it is currently in use in bookings.");
                } else {
                    boolean success = serviceDAO.deleteService(serviceId);
                    if (success) {
                        request.setAttribute("success", "Service deleted successfully");
                    } else {
                        request.setAttribute("error", "Failed to delete service");
                    }
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid service ID");
            }
            request.getRequestDispatcher("serviceManage.jsp").forward(request, response);
            
        } else {
            request.setAttribute("error", "User not logged in");
            response.sendRedirect("login");
        }
    }

}
