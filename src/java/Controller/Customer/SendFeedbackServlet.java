/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Customer;

import Dal.BookingDAO;
import Dal.FeedbackDAO;
import Dal.UserAccountDAO;
import Model.Booking;
import Model.Feedback;
import Model.UserAccount;
import Utility.UploadMultyImage;
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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author KTC
 */
@WebServlet(name = "SendFeedbackServlet", urlPatterns = {"/sendFeedback"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 MB threshold
        maxFileSize = 1024 * 1024 * 5, // 5 MB max file size
        maxRequestSize = 1024 * 1024 * 10 // 10 MB max request size
)
public class SendFeedbackServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        request.getRequestDispatcher("sendFeedback.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserAccountDAO uad = new UserAccountDAO();
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("./login.jsp");
            return;
        }

        String bookingIdRaw = request.getParameter("bookingId");
        int bookingId = Integer.parseInt(bookingIdRaw);

        FeedbackDAO feedbackDAO = new FeedbackDAO();

        if (user == null) {
            response.sendRedirect("./login.jsp");
            return;
        }

        // Get form parameters
        String ratingStr = request.getParameter("rating");
        int rating = 0;
        if (ratingStr != null && !ratingStr.isEmpty()) {
            rating = Integer.parseInt(ratingStr);
        } else {
            request.setAttribute("message", "Rating is required.");
            request.getRequestDispatcher("sendFeedback.jsp").forward(request, response);
            return;
        }
        String comment = request.getParameter("comment");
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());

        //Upload anh
        UploadMultyImage uploader = new UploadMultyImage();

        // ✅ SỬA: Tạo folder riêng cho mỗi booking
        long timestamp = System.currentTimeMillis();
        String UPLOAD_DIR = "/img/feedback/customer_id" + user.getId() + "/booking_" + bookingId + "/feedback_" + timestamp;

        String pathHost = getServletContext().getRealPath("");
        String uploadPath = pathHost.replace("build\\", "") + UPLOAD_DIR;
        String uploadPath2 = pathHost + UPLOAD_DIR;

        uploader.uploadImages(request, "images", uploadPath);
        uploader.uploadImages(request, "images", uploadPath2);

// Lưu path có booking_id vào database
        Feedback feedback = new Feedback(user.getId(), bookingId, rating, comment, UPLOAD_DIR, createdAt, "Visible", "None");
        try {
            feedbackDAO.addFeedback(feedback);
            setSessionMessage(session, "Send feedback successfully!", "success");
        } catch (Exception e) {
            setSessionMessage(session, "Send feedback failed!!", "error");
        }

        request.getRequestDispatcher("sendFeedback.jsp").forward(request, response);
    }

    private void setSessionMessage(HttpSession session, String message, String type) {
        session.setAttribute("message", message);
        session.setAttribute("messageType", type);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
