/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Customer;

import Dal.UserAccountDAO;
import Model.UserAccount;
import Utility.EmailUtility;
import Utility.EmailUtilityVerifyCode;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Random;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author KTC
 */
@WebServlet(name = "ChangePasswordServlet", urlPatterns = {"/changePassword"})
public class ChangePasswordServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("changePassword.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        UserAccountDAO useraccountdao = new UserAccountDAO();
        UserAccount user = (UserAccount) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");

        boolean validCurrentPassword = BCrypt.checkpw(currentPassword, user.getPassword());

        if (!validCurrentPassword) {
            request.setAttribute("currentPasswordError", "Current password is incorrect.");
            setSessionMessage(session, "Current password is incorrect.", "error");
            request.getRequestDispatcher("changePassword.jsp").forward(request, response);
            return;
        }

        String code = String.format("%06d", new Random().nextInt(999999));
        long expiryTime = System.currentTimeMillis() + 1 * 60 * 1000;// 1 phut ke tu luc send
        session.setAttribute("resetCode", code);
        session.setAttribute("resetExpiry", expiryTime);
        // Gửi email
        try {
            EmailUtilityVerifyCode.sendEmail(user.getEmail(), "Change password", code);
            session.setAttribute("newPassword", newPassword);
            request.getRequestDispatcher("verifyCodeForChangePassword.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Unable to send email");
            request.getRequestDispatcher("changePassword.jsp").forward(request, response);
        }
     
    }

    private void setSessionMessage(HttpSession session, String message, String type) {
        session.setAttribute("message", message);
        session.setAttribute("messageType", type);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
