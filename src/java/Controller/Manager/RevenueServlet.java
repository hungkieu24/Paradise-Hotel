/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Manager;

import Dal.HotelBranchDAO;
import Dal.RevenueDAO;
import Model.HotelBranch;
import Model.Revenue;
import Model.UserAccount;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author hungk
 */
@WebServlet(name = "RevenueServlet", urlPatterns = {"/manager/revenue"})
public class RevenueServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");

        if (user != null) {
            String managerId = user.getId();
            HotelBranchDAO branchDAO = new HotelBranchDAO();

            HotelBranch branch = branchDAO.getBranchByManagerId(managerId);
            int branchId = branch.getId();

            RevenueDAO revenueDAO = new RevenueDAO();

            LocalDate today = LocalDate.now();
            int currentMonth = today.getMonthValue(); // từ 1 đến 12
            int currentYear = today.getYear();

            List<Revenue> revenueList = revenueDAO.getRevenueByBranchAndMonthYear(branchId, currentMonth, currentYear);
            double totalRevenue = revenueDAO.getTotalRevenueByBranchAndMonthYear(branchId, currentMonth, currentYear);

            List<String> monthNames = new ArrayList<>();
            DateFormatSymbols dfs = new DateFormatSymbols(Locale.ENGLISH);
            String[] months = dfs.getMonths();
            for (int i = 0; i < 12; i++) {
                monthNames.add(months[i]); // January → December
            }
            List<String> sourceList = revenueDAO.getAllRevenueSourcesByBranchId(branchId);

            String action = request.getParameter("action");
            if (action != null) {
                if (action.equals("filterByMonthYear")) {
                    currentMonth = Integer.parseInt(request.getParameter("month"));
                    currentYear = Integer.parseInt(request.getParameter("year"));
                    String sourceType = request.getParameter("sourceType");
                    
                    if(sourceType.equals("all")) {
                        revenueList = revenueDAO.getRevenueByBranchAndMonthYear(branchId, currentMonth, currentYear);
                        totalRevenue = revenueDAO.getTotalRevenueByBranchAndMonthYear(branchId, currentMonth, currentYear);
                    } else {
                        revenueList = revenueDAO.getRevenueBySourceAndMonthYear(branchId, sourceType, currentMonth, currentYear);
                        totalRevenue = revenueDAO.getTotalRevenueBySourceAndMonthYear(branchId, sourceType, currentMonth, currentYear);
                    }
                }
            }

            request.setAttribute("sourceList", sourceList);
            request.setAttribute("monthNames", monthNames);
            request.setAttribute("month", currentMonth);
            request.setAttribute("year", currentYear);
            request.setAttribute("totalRevenue", totalRevenue);
            request.setAttribute("revenueList", revenueList);
            request.setAttribute("branch", branch);
            request.getRequestDispatcher("./revenue.jsp").forward(request, response);
        } else {
            setSessionMessage(session, "You need to login!", "error");
            response.sendRedirect("../login.jsp");
        }
    }

    private void setSessionMessage(HttpSession session, String message, String type) {
        session.setAttribute("message", message);
        session.setAttribute("messageType", type);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }
}
