/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Manager;

import Dal.FeedbackDAO;
import Dal.HotelBranchDAO;
import Model.Feedback;
import Model.HotelBranch;
import Model.UserAccount;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 *
 * @author hungk
 */
@WebServlet(name = "FeedbackServlet", urlPatterns = {"/manager/feedback"})
public class FeedbackServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");
        String action = request.getParameter("action");
        String keyword = request.getParameter("searchKeyword");
        String ratingStr = request.getParameter("rating");
        
        int page = 1; // trang đầu tiên
        int pageSize = 5; // 1 trang có 5 row
        int totalPages = 0;
        int listSize = 0;

        if (user != null) {
            String managerId = user.getId();
            HotelBranchDAO branchDAO = new HotelBranchDAO();

            HotelBranch branch = branchDAO.getBranchByManagerId(managerId);
            int branchId = branch.getId();

            FeedbackDAO feedbackDAO = new FeedbackDAO();
            List<Feedback> feedbackList = feedbackDAO.getFeedbackByBranchIdAndPage(branchId, page, pageSize);

            if (action != null) {
                if (action.equals("search")) {

                    if (keyword != null) {
                        keyword = keyword.trim(); // Xóa dấu cách đầu và cuối
                        keyword = keyword.replaceAll("\\s+", " ");
                    }
                    feedbackList = feedbackDAO.searchFeedbackByBranchAndKeyword(branchId, keyword, page, pageSize);
                    listSize = feedbackDAO.countSearchFeedbackByBranchAndKeyword(branchId, keyword);
                }

                if (action.equals("filter")) {
                    int rating = Integer.parseInt(ratingStr);
                    if (rating == 0) {
                        feedbackList = feedbackDAO.getFeedbackByBranchIdAndPage(branchId, page, pageSize);
                        listSize = feedbackDAO.getTotalFeedbackByBranchId(branchId);
                    } else {
                        feedbackList = feedbackDAO.filterFeedbackByBranchAndRating(branchId, rating, page, pageSize);
                        listSize = feedbackDAO.countFeedbackByBranchAndRating(branchId, rating);
                    }
                }
            } else {
                listSize = feedbackDAO.getTotalFeedbackByBranchId(branchId);
            }

            totalPages = (int) Math.ceil((double) listSize / pageSize);

            request.setAttribute("branch", branch);
            request.setAttribute("action", action);
            request.setAttribute("keyword", keyword);
            request.setAttribute("rating", ratingStr);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("feedbackListSize", listSize);
            request.setAttribute("feedbackList", feedbackList);
            request.getRequestDispatcher("./feedback.jsp").forward(request, response);

        } else {
            setSessionMessage(session, "You need to login!", "error");
            response.sendRedirect("../login.jsp");
        }
    }

    private void setSessionMessage(HttpSession session, String message, String type) {
        session.setAttribute("message", message);
        session.setAttribute("messageType", type);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        FeedbackDAO feedbackDAO = new FeedbackDAO();

        if (action != null) {
            if (action.equals("delete")) {
                int IdDelete = Integer.parseInt(request.getParameter("IdDelete"));
                boolean success = feedbackDAO.deleteFeedback(IdDelete);

                // Đặt thông báo session
                setSessionMessage(session, success ? "Delete feeback successful!" : "Failure to delete feeback!",
                        success ? "success" : "error");
                response.sendRedirect("./feedback");
                return;
            }
            if (action.equals("ban")) {
                int IdBan = Integer.parseInt(request.getParameter("IdBan"));
                Feedback feedback = feedbackDAO.getFeedbackById(IdBan);
                String userID = feedback.getUser_id();
                boolean success = feedbackDAO.banFeedback(IdBan);

                // Đặt thông báo session
                setSessionMessage(session, success ? "Ban feeback successful!" : "Failure to ban feeback!",
                        success ? "success" : "error");
                response.sendRedirect("./feedback");
                return;
            }
            if (action.equals("warning")) {
                int IdWarning = Integer.parseInt(request.getParameter("IdWarning"));
                boolean success = feedbackDAO.warnAndHideFeedbackById(IdWarning);

                // Đặt thông báo session
                setSessionMessage(session, success ? "Hide feeback successful!" : "Failure to hide feeback!",
                        success ? "success" : "error");
                response.sendRedirect("./feedback");
                return;
            }
            if (action.equals("restore")) {
                int Idrestore = Integer.parseInt(request.getParameter("Idrestore"));
                boolean success = feedbackDAO.restoreFeedback(Idrestore);

                // Đặt thông báo session
                setSessionMessage(session, success ? "Restore feeback successful!" : "Failure to restore feeback!",
                        success ? "success" : "error");
                response.sendRedirect("./feedback");
                return;
            }
        }
    }
}
