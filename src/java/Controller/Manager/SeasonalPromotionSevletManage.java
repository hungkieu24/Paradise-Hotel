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
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author thien
 */
@WebServlet(name = "SeasonalPromotionSevletManage", urlPatterns = {"/promotions"})
public class SeasonalPromotionSevletManage extends HttpServlet {

    private SeasonalPromotionDAO p = new SeasonalPromotionDAO();
    private RoomDAO r = new RoomDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user != null) {
            // lấy tên chi nhánh và tên manager
            String username = user.getUsername();
            String userId = user.getId();

            String branchName = r.getBranchNameById(userId);
            int branchId = r.getBranchId(userId);
            // phan trang
            int page = 1;
            int pageSize = 5;
            String pageParam = request.getParameter("page");
            String sizeParam = request.getParameter("size");
            if (pageParam != null && !pageParam.isEmpty()) {
                page = Integer.parseInt(pageParam);
            }
            if (sizeParam != null && !sizeParam.isEmpty()) {
                pageSize = Integer.parseInt(sizeParam);
            }
            if (page < 1) {
                page = 1;
            }
            if (pageSize < 1) {
                pageSize = 5;
            }
            // total promotion theo branch
            int totalPromotion = p.countPromotionByBranch(branchId);
            int totalPages = (int) Math.ceil((double) totalPromotion / pageSize);
            // lay list promotion
            List<SeasonalPromotion> promotions = p.getPromotionsByBranchId(branchId, page, pageSize);
            LocalDate today = LocalDate.now();
            for (SeasonalPromotion pro : promotions) {
                LocalDate endDa = pro.getEndDate().toLocalDate();

                if (endDa.isBefore(today) && pro.getStatus().equalsIgnoreCase("Active")) {
                    // Cập nhật trạng thái trong DB nếu đã hết hạn
                    pro.setStatus("Inactive");
                    p.updateStatus(pro.getId(), "Inactive");
                }
            }
            //set Attributes
            request.setAttribute("branchId", branchId);
            request.setAttribute("username", username);
            request.setAttribute("branchname", branchName);
            request.setAttribute("currentPage", page);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("promotions", promotions);
            request.getRequestDispatcher("promotionManage.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "don't see user");
            response.sendRedirect("login");
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

}
