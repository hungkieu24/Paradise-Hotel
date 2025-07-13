/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Manager;

import Dal.FeedbackCommentDAO;
import Dal.FeedbackDAO;
import Model.Feedback;
import Model.FeedbackComment;
import Model.UserAccount;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author hungk
 */
@WebServlet(name = "FeedbackEventHandlerServlet", urlPatterns = {"/manager/FeedbackEventHandler"})
public class FeedbackEventHandlerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String type = request.getParameter("type");

        if ("comment-tree".equals(type)) {
            String feedbackIdParam = request.getParameter("feedbackId");
            if (feedbackIdParam == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Missing feedbackId\"}");
                return;
            }

            int feedbackId = Integer.parseInt(feedbackIdParam);
            FeedbackCommentDAO commentDAO = new FeedbackCommentDAO(); // hoặc dùng Singleton/service nếu có
            List<FeedbackComment> comments = commentDAO.getCommentsByFeedbackId(feedbackId);

            FeedbackDAO feedbackDAO = new FeedbackDAO();
            Feedback feedback = feedbackDAO.getFeedbackById(feedbackId);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("feedback", feedback);
            responseData.put("comments", comments);

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create();

            String json = gson.toJson(responseData);
            response.getWriter().write(json);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\":\"Invalid type parameter\"}");
        }
    }

    private void setSessionMessage(HttpSession session, String message, String type) {
        session.setAttribute("message", message);
        session.setAttribute("messageType", type);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BufferedReader reader = request.getReader();
        Gson gson = new Gson();
        Map<String, Object> requestData = gson.fromJson(reader, Map.class);

        String action = (String) requestData.get("action");
        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null) {
            setSessionMessage(session, "You need to login!", "error");
            response.sendRedirect("../login.jsp");
            return;
        }

        Map<String, Object> responseData = new HashMap<>();
        if ("reply".equals(action)) {
            handleReplyAction(requestData, responseData, user, response);
            return;
        } else if ("postRoot".equals(action)) {
            handlePostRootAction(requestData, responseData, user, response);
            return;
        } else if ("delete".equals(action)) {
            int commentId = ((Double) requestData.get("comment_id")).intValue();

            boolean success = new FeedbackCommentDAO().softDeleteComment(commentId);

            responseData.put("success", success);
            responseData.put("message", success ? "Comment deleted." : "You cannot delete this comment.");
            responseData.put("messageType", success ? "success" : "error");
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new Gson().toJson(responseData));

    }

    private void handleReplyAction(Map<String, Object> requestData, Map<String, Object> responseData, UserAccount user, HttpServletResponse response) throws IOException {
        int feedbackId = ((Double) requestData.get("feedback_id")).intValue();
        int parentCommentId = ((Double) requestData.get("parent_comment_id")).intValue();
        String content = (String) requestData.get("content");

        String userId = user.getId(); // lấy từ session thực tế

        FeedbackCommentDAO dao = new FeedbackCommentDAO();
        FeedbackComment comment = new FeedbackComment();
        comment.setFeedback_id(feedbackId);
        comment.setParent_comment_id(parentCommentId);
        comment.setUser_id(userId);
        comment.setContent(content);
        boolean success = dao.addComment(comment);

        responseData.put("success", success);
        responseData.put("message", success ? "Reply posted successfully!" : "Failed to post reply!");
        responseData.put("messageType", success ? "success" : "error");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new Gson().toJson(responseData));
    }

    private void handlePostRootAction(Map<String, Object> requestData, Map<String, Object> responseData, UserAccount user, HttpServletResponse response) throws IOException {
        int feedbackId = Integer.parseInt(requestData.get("feedback_id").toString());
        String content = (String) requestData.get("content");

        FeedbackComment comment = new FeedbackComment();
        comment.setFeedback_id(feedbackId);
        comment.setParent_comment_id(0); // root comment
        comment.setUser_id(user.getId());
        comment.setContent(content);

        boolean success = new FeedbackCommentDAO().addComment(comment);

        responseData.put("success", success);
        responseData.put("message", success ? "Reply posted successfully!" : "Unable to post comments.");
        responseData.put("messageType", success ? "success" : "error");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new Gson().toJson(responseData));
    }
}
