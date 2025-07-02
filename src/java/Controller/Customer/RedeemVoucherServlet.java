/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Customer;

import Dal.LoyaltyPointDAO;
import Dal.PointRedeemVoucherDAO;
import Dal.VoucherDAO;
import Model.LoyaltyPoint;
import Model.UserAccount;
import Model.Voucher;
import Utility.TierUtil;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 *
 * @author KTC
 */
@WebServlet(name = "RedeemVoucherServlet", urlPatterns = {"/redeemVoucher"})
public class RedeemVoucherServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        LoyaltyPointDAO pointDAO = new LoyaltyPointDAO();
        LoyaltyPoint loyaltyPoint = pointDAO.getLoyaltyPointByUserId(user.getId());

        VoucherDAO voucherDAO = new VoucherDAO();
        List<Voucher> listVoucher = voucherDAO.getAllVouchers();

        request.setAttribute("listVoucher", listVoucher);

        request.setAttribute("tierRankHelper", new TierUtil());
        request.setAttribute("discountRateHelper", new TierUtil());

        session.setAttribute("loyaltyPoint", loyaltyPoint);

        String success = request.getParameter("success");

        request.getRequestDispatcher("redeemVoucher.jsp").forward(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int voucherId = Integer.parseInt(request.getParameter("voucherId"));
        double pointsDouble = Double.parseDouble(request.getParameter("pointsUsed"));
        int pointsUsed = (int) Math.ceil(pointsDouble);

        // DAO
        LoyaltyPointDAO pointDAO = new LoyaltyPointDAO();
        PointRedeemVoucherDAO redeemDAO = new PointRedeemVoucherDAO();

        int currentPoints = pointDAO.getPointsByUser(user.getId());
        if (currentPoints < pointsUsed) {
            request.setAttribute("error", "❌ You do not have enough points.");
            request.getRequestDispatcher("redeemVoucher").forward(request, response);
            return;
        }

        if (redeemDAO.hasAlreadyRedeemed(user.getId(), voucherId)) {
            request.setAttribute("error", "⚠️ You have already redeemed this voucher.");
             response.sendRedirect("redeemVoucher?success=false");
            return;
        }

        // 1. Trừ điểm
        boolean deducted = pointDAO.subtractPoints(user.getId(), pointsUsed);
        if (!deducted) {
            request.setAttribute("error", "❌ Failed to deduct points.");
            request.getRequestDispatcher("redeemVoucher.jsp").forward(request, response);
            return;
        }

        // 2. Ghi nhận đổi
        boolean success = redeemDAO.redeemVoucher(user.getId(), voucherId, pointsUsed);
        if (success) {
            response.sendRedirect("redeemVoucher?success=true");

        } else {
            request.setAttribute("error", "❌ Failed to redeem voucher.");
            request.getRequestDispatcher("redeemVoucher.jsp").forward(request, response);
        }
    }
}
