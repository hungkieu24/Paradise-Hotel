/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Customer;

import Dal.FeedbackDAO;
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
import java.util.Collection;

/**
 *
 * @author KTC
 */
@WebServlet(name = "EditFeedbackServlet", urlPatterns = {"/EditFeedbackServlet"})
@MultipartConfig // ✅ Thêm để xử lý file upload
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
        HttpSession session = request.getSession();

        int id = Integer.parseInt(request.getParameter("feedbackId"));
        int rating = Integer.parseInt(request.getParameter("rating"));
        String comment = request.getParameter("comment");

        // ✅ Lấy feedback hiện tại để giữ lại image_url cũ
        FeedbackDAO dao = new FeedbackDAO();
        Feedback existingFeedback = dao.getFeedbackById(id);
        String currentImageUrl = existingFeedback.getImage_url();

        String finalImageUrl = currentImageUrl; // Giữ ảnh cũ mặc định

        // ✅ Kiểm tra content type trước khi xử lý multipart
        String contentType = request.getContentType();

        if (contentType != null && contentType.toLowerCase().startsWith("multipart/")) {
            // Có multipart data - kiểm tra upload ảnh mới
            Collection<Part> fileParts = request.getParts();
            boolean hasNewImages = fileParts.stream().anyMatch(part
                    -> "images".equals(part.getName()) && part.getSize() > 0);

            if (hasNewImages) {
                UserAccount user = (UserAccount) request.getSession().getAttribute("user");
                long timestamp = System.currentTimeMillis();
                String UPLOAD_DIR = "/img/feedback/customer_id" + user.getId() + "/feedback_edit_" + id + "_" + timestamp;

                String pathHost = getServletContext().getRealPath("");
                String uploadPath = pathHost.replace("build\\", "") + UPLOAD_DIR;
                String uploadPath2 = pathHost + UPLOAD_DIR;

                // ✅ Tạo folder nếu chưa tồn tại
                File uploadDir1 = new File(uploadPath);
                File uploadDir2 = new File(uploadPath2);

                if (!uploadDir1.exists()) {
                    uploadDir1.mkdirs();
                }
                if (!uploadDir2.exists()) {
                    uploadDir2.mkdirs();
                }

                UploadMultyImage uploader = new UploadMultyImage();
                uploader.uploadImages(request, "images", uploadPath);
                uploader.uploadImages(request, "images", uploadPath2);

                finalImageUrl = UPLOAD_DIR;

            }

            // ✅ Đảm bảo không bị null
            if (finalImageUrl == null) {
                finalImageUrl = currentImageUrl;
            }

            // Update feedback
            Feedback feedback = new Feedback();
            feedback.setId(id);
            feedback.setRating(rating);
            feedback.setComment(comment);
            boolean updateSuccess = false;

// ✅ Chỉ update image khi có ảnh mới
            feedback.setImage_url(finalImageUrl);
            updateSuccess = dao.updateFeedback(feedback);

// ✅ Set thông báo thành công/thất bại
            if (updateSuccess) {
                session.setAttribute("message", "Feedback updated successfully!");
                session.setAttribute("messageType", "success");
            } else {
                session.setAttribute("message", "Failed to update feedback. Please try again.");
                session.setAttribute("messageType", "error");
            }

            int roomTypeId = Integer.parseInt(request.getParameter("roomTypeId"));
            response.sendRedirect("viewRoomTypeDetail?roomTypeId=" + roomTypeId);
        }
    }
}
