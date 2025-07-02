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
@WebServlet(name = "AddPromotionServletManage", urlPatterns = {"/addPromotion"})
public class AddPromotionServletManage extends HttpServlet {

    private RoomDAO r = new RoomDAO();
    private SeasonalPromotionDAO p = new SeasonalPromotionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user != null) {
            request.setAttribute("username", user.getUsername());
            request.setAttribute("branchname", r.getBranchNameById(user.getId()));
            request.setAttribute("branchId", r.getBranchId(user.getId()));
            request.getRequestDispatcher("serviceManage.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "User not logged in");
            response.sendRedirect("login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user != null) {
            int branchId = r.getBranchId(user.getId());
            String promotionName = request.getParameter("promotion_name");
            String discountPercentStr = request.getParameter("discount_percent");
            String discountAmountStr = request.getParameter("discount_amount");
            String startDateStr = request.getParameter("start_date");
            String endDateStr = request.getParameter("end_date");
            String description = request.getParameter("description");
            String status = request.getParameter("status");
            try {
                if(p.isPromotionNameExist(promotionName, branchId)){
                    request.setAttribute("error", "Promotion name already exist");
                    request.setAttribute("returnPage", "promotions");
                    request.getRequestDispatcher("promotionManage.jsp").forward(request, response);
                    return;
                }
                Double discountPercent = null;
                Double discountAmount = null;
                if (discountPercentStr != null && !discountPercentStr.trim().isEmpty()) {
                    discountPercent = Double.parseDouble(discountPercentStr);
                }
                if (discountAmountStr != null && !discountAmountStr.trim().isEmpty()) {
                    discountAmount = Double.parseDouble(discountAmountStr);
                }
                Date startDate = Date.valueOf(startDateStr);
                Date endDate = Date.valueOf(endDateStr);
                
                SeasonalPromotion promotion = new SeasonalPromotion();
                promotion.setName(promotionName);
                promotion.setDescription(description);
                promotion.setDiscount_percent(discountPercent != null ? discountPercent : 0.0);
                promotion.setDiscount_amount(discountAmount != null ? discountAmount : 0.0);
                promotion.setStartDate(startDate);
                promotion.setEndDate(endDate);
                promotion.setBranchId(branchId);
                promotion.setStatus(status != null && !status.isEmpty() ? status : "Active");
                promotion.setIs_deleted(false);
                // add vao DB
                p.addPromotion(promotion);
                response.sendRedirect("promotions");
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Failed to add promotion: " + e.getMessage());
                request.getRequestDispatcher("promotionManage.jsp").forward(request, response);
            }
        } else {
            request.setAttribute("error", "User not logged in");
            response.sendRedirect("login");
        }
    }
}
