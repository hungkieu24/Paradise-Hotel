/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Manager;

import Dal.RoomDAO;
import Dal.SeasonalPromotionDAO;
import Model.SeasonalPromotion;
import Model.UserAccount;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.Date;

/**
 *
 * @author thien
 */
@WebServlet(name = "EditPromotionServletManage", urlPatterns = {"/editPromotion"})
public class EditPromotionServletManage extends HttpServlet {

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
        if (user != null) {
            int branchId = r.getBranchId(user.getId());

            int promotionId = Integer.parseInt(request.getParameter("promotion_id"));
            String name = request.getParameter("promotion_name");
            String description = request.getParameter("description");
            String discountPercentStr = request.getParameter("discount_percent");
            String discountAmountStr = request.getParameter("discount_amount");
            String startDateStr = request.getParameter("start_date");
            String endDateStr = request.getParameter("end_date");

            Double discountPercent = null;
            Double discountAmount = null;

            // Only one of discount_percent or discount_amount will be set
            if (discountPercentStr != null && !discountPercentStr.isEmpty()) {
                discountPercent = Double.parseDouble(discountPercentStr);
            }
            if (discountAmountStr != null && !discountAmountStr.isEmpty()) {
                discountAmount = Double.parseDouble(discountAmountStr);
            }
            Date startDate = Date.valueOf(startDateStr);
            Date endDate = Date.valueOf(endDateStr);

            SeasonalPromotion promotion = new SeasonalPromotion();
            promotion.setId(promotionId);
            promotion.setName(name);
            promotion.setDescription(description);
            promotion.setDiscount_percent(discountPercent != null ? discountPercent : 0.0);
            promotion.setDiscount_amount(discountAmount != null ? discountAmount : 0.0);
            promotion.setStartDate(startDate);
            promotion.setEndDate(endDate);
            promotion.setBranchId(branchId);
            promotion.setStatus("Active");// mac dinh
            promotion.setIs_deleted(false);
            // data base
            boolean check = p.updatePromotion(promotion);
           if(check){
               session.setAttribute("success", "update successfully!");
               response.sendRedirect("promotions");
           }else{
               session.setAttribute("error", "update false");
               response.sendRedirect("promotions");
            
           }

        } else {
            request.setAttribute("error", "Please login to edit room.");
            request.getRequestDispatcher("login.jsp").forward(request, response);

        }
    }

}
