/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.HotelOwner;

import Dal.BranchMonthlyReportDAO;
import Dal.HotelBranchDAO;
import Dal.InitialInvestmentDAO;
import Model.BranchMonthlyReport;
import Model.HotelBranch;
import Model.InitialInvestment;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author hungk
 */
@WebServlet(name = "financialDashboardServlet", urlPatterns = {"/hotelOwner/financialDashboard"})
public class financialDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        int page = parseIntSafe(request.getParameter("page"), 1);
        int pageSize = 10;
        int listSize = 0;

        BranchMonthlyReportDAO monthlyReportDAO = new BranchMonthlyReportDAO();
        InitialInvestmentDAO investmentDAO = new InitialInvestmentDAO();

        LocalDate today = LocalDate.now();
        int[] earliest = monthlyReportDAO.getEarliestMonthYearInBranchMonthlyReport();
        int currentMonth = earliest[0]; 
        int currentYear = earliest[1];
        int monthTo = today.getMonthValue(); // từ 1 đến 12
        int yearTo = today.getYear();

        List<BranchMonthlyReport> branchMonthlyReportList = monthlyReportDAO.getBranchMonthlyReportsByMonthRangeAllBranches(currentMonth, currentYear, monthTo, yearTo, page, pageSize);
        listSize = monthlyReportDAO.getBranchMonthlyReportCountAllBranches(currentMonth, currentYear, monthTo, yearTo);

        List<InitialInvestment> initialInvestmentList = investmentDAO.getInitialInvestmentsByMonthRange(currentMonth, currentYear, monthTo, yearTo);
        double totalInitialInvestment = investmentDAO.getTotalInitialInvestmentByMonthRange(currentMonth, currentYear, monthTo, yearTo);

        // Lấy total revenue theo tháng hiện tại
        double totalRevenue = monthlyReportDAO.getTotalRevenueAllBranches(currentMonth, currentYear, monthTo, yearTo);

        // Lấy total expense theo tháng hiện tại
        double totalExpense = monthlyReportDAO.getTotalExpensesAllBranches(currentMonth, currentYear, monthTo, yearTo);
        
        int branchId = 0;
        if (action != null && action.equals("filter")) {
            currentMonth = Integer.parseInt(request.getParameter("monthFrom"));
            currentYear = Integer.parseInt(request.getParameter("yearFrom"));
            monthTo = Integer.parseInt(request.getParameter("monthTo"));
            yearTo = Integer.parseInt(request.getParameter("yearTo"));
            branchId = Integer.parseInt(request.getParameter("branchId"));

            if (branchId != 0) {
                branchMonthlyReportList = monthlyReportDAO.getBranchMonthlyReportsByMonthRange(branchId, currentMonth, currentYear, monthTo, yearTo, page, pageSize);
                listSize = monthlyReportDAO.getBranchMonthlyReportCount(branchId, currentMonth, currentYear, monthTo, yearTo);
                initialInvestmentList = investmentDAO.getInitialInvestmentsByBranchAndMonthRange(branchId, currentMonth, currentYear, monthTo, yearTo);
                totalInitialInvestment = investmentDAO.getTotalInitialInvestmentByBranchAndMonthRange(branchId, currentMonth, currentYear, monthTo, yearTo);

                totalRevenue = monthlyReportDAO.getTotalRevenueByBranchAndMonthRange(branchId, currentMonth, currentYear, monthTo, yearTo);
                totalExpense = monthlyReportDAO.getTotalExpensesByBranchAndMonthRange(branchId, currentMonth, currentYear, monthTo, yearTo);
            } else {
                branchMonthlyReportList = monthlyReportDAO.getBranchMonthlyReportsByMonthRangeAllBranches(currentMonth, currentYear, monthTo, yearTo, page, pageSize);
                listSize = monthlyReportDAO.getBranchMonthlyReportCountAllBranches(currentMonth, currentYear, monthTo, yearTo);
                initialInvestmentList = investmentDAO.getInitialInvestmentsByMonthRange(currentMonth, currentYear, monthTo, yearTo);
                totalInitialInvestment = investmentDAO.getTotalInitialInvestmentByMonthRange(currentMonth, currentYear, monthTo, yearTo);

                totalRevenue = monthlyReportDAO.getTotalRevenueAllBranches(currentMonth, currentYear, monthTo, yearTo);
                totalExpense = monthlyReportDAO.getTotalExpensesAllBranches(currentMonth, currentYear, monthTo, yearTo);
            }
        }

        int totalPages = (int) Math.ceil((double) listSize / pageSize);

        double Profit = totalRevenue - totalExpense;
        double ProfitRate = 0;

        if (totalInitialInvestment != 0) {
            ProfitRate = (Profit / totalInitialInvestment) * 100;
        }

        HotelBranchDAO branchDAO = new HotelBranchDAO();
        List<HotelBranch> branchList = branchDAO.getAllHotelBranchesSimple();
        
        List<String> monthNames = prepareMonthNames();
        int totalMonths = (yearTo - currentYear) * 12 + (monthTo - currentMonth) + 1;

        // Set attribute để đổ dữ liệu về JSP
        request.setAttribute("totalMonths", totalMonths);
        request.setAttribute("branchId", branchId);
        request.setAttribute("monthNames", monthNames);
        request.setAttribute("monthFrom", currentMonth);
        request.setAttribute("yearFrom", currentYear);
        request.setAttribute("monthTo", monthTo);
        request.setAttribute("yearTo", yearTo);
        request.setAttribute("totalExpense", totalExpense);
        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("Profit", Profit);
        request.setAttribute("ProfitRate", ProfitRate);

        request.setAttribute("action", action);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalInitialInvestment", totalInitialInvestment);
        request.setAttribute("reportListSize", listSize);
        request.setAttribute("branchList", branchList);
        request.setAttribute("branchMonthlyReportList", branchMonthlyReportList);
        request.setAttribute("initialInvestmentList", initialInvestmentList);
        request.getRequestDispatcher("./financialDashboard.jsp").forward(request, response);
    }
    
    private List<String> prepareMonthNames() {
        List<String> monthNames = new ArrayList<>();
        DateFormatSymbols dfs = new DateFormatSymbols(Locale.ENGLISH);
        String[] months = dfs.getMonths();
        for (int i = 0; i < 12; i++) {
            monthNames.add(months[i]);
        }
        return monthNames;
    }

    private int parseIntSafe(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

}
