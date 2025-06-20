/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package Controller;

import Dal.UserAccountDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author thien
 */
@WebServlet(name="ResetPasswordServlet", urlPatterns={"/resetPassword"})
public class ResetPasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        //mã hóa mật khẩu 
        String hashedPassWord = BCrypt.hashpw(password, BCrypt.gensalt(12));
        // Cap nhat DB 
        UserAccountDAO dao = new UserAccountDAO();
        boolean update = dao.updatePassword(email, hashedPassWord);
        if(update){
            request.setAttribute("success", "Change password successfully! Can you login again.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } else{
            request.setAttribute("error", "Have error when update password");
            request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
        }
    }
    
}
