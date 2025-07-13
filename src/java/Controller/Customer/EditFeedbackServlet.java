/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Customer;

import Dal.FeedbackDAO;
import Model.Feedback;
import Model.UserAccount;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author KTC
 */
@WebServlet(name = "EditFeedbackServlet", urlPatterns = {"/EditFeedbackServlet"})
public class EditFeedbackServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int feedbackId = Integer.parseInt(request.getParameter("feedbackId"));
        FeedbackDAO dao = new FeedbackDAO();
        Feedback feedback = dao.getFeedbackById(feedbackId);

        UserAccount user = (UserAccount) request.getSession().getAttribute("user");

        if (feedback != null && user != null && feedback.getUser_id().equals(user.getId())) {
            request.setAttribute("feedback", feedback);
            request.getRequestDispatcher("/editFeedbackForm.jsp").forward(request, response);
        } else {
            response.sendRedirect("homepage");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("feedbackId"));
        int rating = Integer.parseInt(request.getParameter("rating"));
        String comment = request.getParameter("comment");
        String imageUrl = request.getParameter("imageUrl"); // optional

        Feedback feedback = new Feedback();
        feedback.setId(id);
        feedback.setRating(rating);
        feedback.setComment(comment);
        feedback.setImage_url(imageUrl);

        FeedbackDAO dao = new FeedbackDAO();
        dao.updateFeedback(feedback);

        int roomTypeId = Integer.parseInt(request.getParameter("roomTypeId"));
        response.sendRedirect("viewRoomTypeDetail?roomTypeId=" + roomTypeId);
    }

}
