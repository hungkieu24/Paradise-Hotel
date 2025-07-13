/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Customer;

import Dal.FeedbackDAO;
import Dal.RoomTypeDAO;
import Model.Feedback;
import Model.RoomType;
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
@WebServlet(name = "viewFeedback", urlPatterns = {"/viewFeedback"})
public class ViewFeedbackServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String sroomTypeId = request.getParameter("roomTypeId");
        int roomTypeId = 0;
        if (sroomTypeId != null || !sroomTypeId.trim().isEmpty()) {
            roomTypeId = Integer.parseInt(sroomTypeId);
        } else {
            response.sendRedirect("./homepage");
            return;
        }
        
        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
        RoomType roomType = roomTypeDAO.getRoomTypeById(roomTypeId);
        
        int page = 1; // trang dau tien
        int pageSize = 5; // 1 trang co 5 row
        if (request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }
        FeedbackDAO feedbackDAO = new FeedbackDAO();
        int feedbackListSize = feedbackDAO.getAllFeedBack().size();
        int totalPages = (int) Math.ceil((double) feedbackListSize / pageSize);
        List<Feedback> listFeedback = feedbackDAO.getListFeedbackByPage1(page, pageSize,roomTypeId);

        request.setAttribute("listFeedback", listFeedback);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        request.getRequestDispatcher("viewFeedback.jsp").forward(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {        
    }
}
