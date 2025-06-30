/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Manager;

import Dal.RoomDAO;
import Dal.VoucherDAO;
import Model.UserAccount;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author thien
 */
@WebServlet(name = "DeleteVoucherServletManage", urlPatterns = {"/deleteVoucher"})
public class DeleteVoucherServletManage extends HttpServlet {

    private VoucherDAO v = new VoucherDAO();
    private RoomDAO r = new RoomDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("promotions");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UserAccount user = (session != null) ? (UserAccount) session.getAttribute("user") : null;
        if(user != null){
             String idStr = request.getParameter("voucherId");
            boolean result = false;
            try{
                if(idStr != null && !idStr.isEmpty()){
                    int id = Integer.parseInt(idStr);
                    result=v.softDeleteVoucher(id);
                }
                if(result){
                    session.setAttribute("success", "Delete voucher successfully");
                }
                else{
                    session.setAttribute("error", "delete false");
                }
            }catch(Exception e){
                e.printStackTrace();
                session.setAttribute("error", "Error occured while deleting voucher");
            }
            response.sendRedirect("vouchers");
        }else{
            session.setAttribute("error", "Please login to continue");
            response.sendRedirect("login");
        }
    }

}
