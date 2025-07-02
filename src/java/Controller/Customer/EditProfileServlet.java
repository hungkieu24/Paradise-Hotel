/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Customer;

import Dal.LoyaltyPointDAO;
import Dal.UserAccountDAO;
import Model.LoyaltyPoint;
import Model.UserAccount;
import Utility.EmailUtility;
import Utility.UploadImage;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Random;

/**
 *
 * @author KTC
 */
@WebServlet(name = "EditProfileServlet", urlPatterns = {"/editProfile"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50 // 50MB
)
public class EditProfileServlet extends HttpServlet {

    private final UserAccountDAO useraccountdao = new UserAccountDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        LoyaltyPointDAO loyaltypointdao = new LoyaltyPointDAO();
        LoyaltyPoint loyaltypointlp = loyaltypointdao.getLoyaltyPointByUserId(user.getId());

        session.setAttribute("loyaltypointlp", loyaltypointlp);
        request.getRequestDispatcher("editProfile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        UserAccountDAO useraccountdao = new UserAccountDAO();
        UserAccount user = (UserAccount) session.getAttribute("user");

        UploadImage up = new UploadImage();
        String UPLOAD_DIR = "/img/avatar";// duong dan luu tru trong du an

        String image = user.getAvatar_url();
        String pathHost = getServletContext().getRealPath("");   // lay thu muc goc cua project tren server
        //lay duong dan thuc te noi project đang chạy.
        String finalPath = pathHost.replace("build\\", ""); // xoa build\ khi NetBeans
        String uploadPath = finalPath + UPLOAD_DIR;

        // upload anh
        String fileName = up.uploadImage(request, "avatar", uploadPath);
        String avatarUrl = "." + UPLOAD_DIR + "/" + fileName;
        if (fileName == null) {
            avatarUrl = image;

        }

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String phonenumber = request.getParameter("phonenumber");

        if (!validateForUpdate(user.getId(), username, email, phonenumber, request)) {
            response.sendRedirect("./editProfile");
            return;
        }
        if (!email.equals(user.getEmail())) {

            sendVerificationCode(request, response, email);
            session.setAttribute("username", username);
            session.setAttribute("phonenumber", phonenumber);
            session.setAttribute("userId", user.getId());
            session.setAttribute("avatarUrl", avatarUrl);
            session.setAttribute("email", email);
            return;
        }

        session.setAttribute("username", username);
        session.setAttribute("email", email);
        session.setAttribute("phonenumber", phonenumber);

        boolean updated = useraccountdao.updateUserInfo(user.getId(), username, email, phonenumber, avatarUrl);

        if (updated) {
            UserAccount useraccount = useraccountdao.getUserById(user.getId());
            session.setAttribute("user", useraccount);
            setSessionMessage(session, "Information updated successfully!", "success");
        } else {
            setSessionMessage(session, "Update information failed!", "error");
        }

        request.getRequestDispatcher("editProfile.jsp").forward(request, response);

    }

    private void setSessionMessage(HttpSession session, String message, String type) {
        session.setAttribute("message", message);
        session.setAttribute("messageType", type);
    }

    public boolean validateForUpdate(String id, String username, String email, String phonenumber, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (useraccountdao.isFieldExists("username", username, id)) {
            setSessionMessage(session, "Username already exists!", "error");
            return false;
        }

        if (useraccountdao.isFieldExists("email", email, id)) {
            setSessionMessage(session, "Email already exists!", "error");
            return false;
        }

        if (useraccountdao.isFieldExists("phonenumber", phonenumber, id)) {
            setSessionMessage(session, "Phone already exists!", "error");
            return false;
        }

        return true;
    }

    public void sendVerificationCode(HttpServletRequest request, HttpServletResponse response, String email) throws IOException {
        HttpSession session = request.getSession();

        // Sinh mã xác nhận 6 chữ số
        String verificationCode = String.format("%06d", new Random().nextInt(1000000));
        int duration = 1 * 60; // 1 phút (60 giây)
        long expiryTime = System.currentTimeMillis() + duration * 1000;

        try {
            // Gửi email xác nhận
            EmailUtility.sendEmail(email, "Verify your email to edit profile", verificationCode);
        } catch (Exception e) {
            e.printStackTrace();
            setSessionMessage(session, "Unable to send email, please check your email", "error");
            response.sendRedirect("./editProfile");
            return;
        }
        // Lưu thông tin xác nhận vào session
        session.setAttribute("duration", duration);
        session.setAttribute("expiryTime", expiryTime);
        session.setAttribute("authCode", verificationCode);

        // Điều hướng đến trang xác minh
        response.sendRedirect("verifyEmailForEditProfile.jsp");
    }

}
