/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Customer;

import Dal.FeedbackDAO;
import Model.Feedback;
import Model.UserAccount;
import jakarta.mail.Session;
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
 * @author KTC
 */
@WebServlet(name = "DeleteFeedbackServlet", urlPatterns = {"/DeleteFeedbackServlet"})
public class DeleteFeedbackServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        int feedbackId = Integer.parseInt(request.getParameter("feedbackId"));
        int roomTypeId = Integer.parseInt(request.getParameter("roomTypeId"));

        UserAccount user = (UserAccount) request.getSession().getAttribute("user");

        FeedbackDAO dao = new FeedbackDAO();
        Feedback feedback = dao.getFeedbackById(feedbackId);

        if (feedback != null && user != null && feedback.getUser_id().equals(user.getId())) {
            boolean deleteSuccess = dao.deleteFeedbackById(feedbackId);

            // ✅ Set thông báo
            if (deleteSuccess) {
                session.setAttribute("message", "Feedback deleted successfully!");
                session.setAttribute("messageType", "success");
            } else {
                session.setAttribute("message", "Failed to delete feedback. Please try again.");
                session.setAttribute("messageType", "error");
            }
        } else {
            // ✅ Thông báo khi không có quyền xóa
            session.setAttribute("message", "You don't have permission to delete this feedback.");
            session.setAttribute("messageType", "error");
        }

        response.sendRedirect("viewRoomTypeDetail?roomTypeId=" + roomTypeId);
    }
}
