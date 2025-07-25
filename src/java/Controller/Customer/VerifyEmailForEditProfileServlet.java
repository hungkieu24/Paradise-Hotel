/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Customer;

import Dal.UserAccountDAO;
import Model.UserAccount;
import Utility.EmailUtility;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
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
@WebServlet(name = "VerifyEmailForEditProfileServlet", urlPatterns = {"/verifyEmailForEditProfile"})
public class VerifyEmailForEditProfileServlet extends HttpServlet {

  

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       
    }

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String inputCode = request.getParameter("code");
        String action = request.getParameter("action");
        if (action != null && action.equals("resend")) {
            sendVerificationCode(request, response);
            return;
        }
        HttpSession session = request.getSession(false);

        // Kiểm tra sessionCode và expiryTime
        String sessionCode = (String) session.getAttribute("authCode");
        Long expiryTime = (Long) session.getAttribute("expiryTime");
        long currentTime = System.currentTimeMillis();

        if (expiryTime == null || sessionCode == null) {
            setSessionMessage(session, "You need to enter a new email to change", "error");
            response.sendRedirect("./editProfile");
            return;
        }

        if (currentTime > expiryTime) {
            setSessionMessage(session, "The verification code has expired. Please request a new code.", "error");
            response.sendRedirect("./verifyEmailForEditProfile.jsp");
            return;
        }
        if (sessionCode.equals(inputCode)) {
            UserAccountDAO uadao = new UserAccountDAO();
            String email = (String) session.getAttribute("email");
            String userId = (String) session.getAttribute("userId");
            String fullname = (String) session.getAttribute("fullname");
            String phonenumber = (String) session.getAttribute("phonenumber");
            String avatarUrl = (String) session.getAttribute("avatarUrl");
            boolean updated = uadao.updateUserInfo(userId, fullname, email, phonenumber, avatarUrl);

            if (updated) {
                UserAccount useraccount = uadao.getUserById(userId);
                session.setAttribute("user", useraccount);
                setSessionMessage(session, "Information updated successfully!", "success");
            } else {
                setSessionMessage(session, "Update information failed!!", "error");
            }

            request.getRequestDispatcher("editProfile.jsp").forward(request, response);

        } else {
            setSessionMessage(session, "The verification code is incorrect!", "error");
            response.sendRedirect("./verifyEmailForEditProfile.jsp");
        }

    }

    private void setSessionMessage(HttpSession session, String message, String type) {
        session.setAttribute("message", message);
        session.setAttribute("messageType", type);
    }

    public void sendVerificationCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        String email = (String) session.getAttribute("email");

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
