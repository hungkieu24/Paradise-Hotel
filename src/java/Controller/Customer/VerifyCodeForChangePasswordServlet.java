/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Customer;

import Dal.UserAccountDAO;
import Model.UserAccount;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author KTC
 */
@WebServlet(name = "VerifyCodeForChangePasswordServlet", urlPatterns = {"/verifyCodeForChangePassword"})
public class VerifyCodeForChangePasswordServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String code = request.getParameter("code");

        HttpSession session = request.getSession();
        String sessionCode = (String) session.getAttribute("resetCode");
        Long expiryTime = (Long) session.getAttribute("resetExpiry");
        long currentTime = System.currentTimeMillis();

        if (currentTime > expiryTime) {
            request.setAttribute("error", "The verification code has expired. Please request a new code.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("verifyCodeForChangePassword.jsp").forward(request, response);
            return;
        }

        if (!sessionCode.equals(code)) {
            request.setAttribute("error", "Incorrect verification code.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("verifyCodeForChangePassword.jsp").forward(request, response);
            return;
        }
        UserAccountDAO useraccountdao = new UserAccountDAO();
        UserAccount user = (UserAccount) session.getAttribute("user");
        String newPassword = (String) session.getAttribute("newPassword");
        String hashedNewPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

        boolean updated = useraccountdao.updatePassword1(user.getUsername(), hashedNewPassword);

        if (updated) {
            user.setPassword(hashedNewPassword);
            setSessionMessage(session, "Your password has been updated.", "success");
        } else {
            setSessionMessage(session, "Password updated failed!", "error");
        }
       
        session.setAttribute("email", email); // Giữ lại email để reset
        response.sendRedirect("changePassword.jsp");
    }
    
    private void setSessionMessage(HttpSession session, String message, String type) {
        session.setAttribute("message", message);
        session.setAttribute("messageType", type);
    }
    

}
