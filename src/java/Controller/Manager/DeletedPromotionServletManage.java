/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Manager;

import Dal.RoomDAO;
import Dal.SeasonalPromotionDAO;
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
@WebServlet(name = "DeletedPromotionServletManage", urlPatterns = {"/deletePromotion"})
public class DeletedPromotionServletManage extends HttpServlet {

    private RoomDAO r = new RoomDAO();
    private SeasonalPromotionDAO p = new SeasonalPromotionDAO();

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
            String promotionIdStr = request.getParameter("promotionId");
            boolean result = false;
            try{
                if(promotionIdStr != null && !promotionIdStr.isEmpty()){
                    int promotionIs = Integer.parseInt(promotionIdStr);
                    result = p.deletePromotion(promotionIs);
                }
                if(result){
                    session.setAttribute("success", "Delete promotion successfully");
                    
                }else{
                    session.setAttribute("error", "delete false");
                }
            }catch(Exception e){
                e.printStackTrace();
                session.setAttribute("error", "Error occured while deleting promotion");
            }
            response.sendRedirect("promotions");
        }else{
            session.setAttribute("error", "Please login to continue");
            response.sendRedirect("login");
        }
    }

}
