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
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        BranchMonthlyReportDAO monthlyReportDAO = new BranchMonthlyReportDAO();
        InitialInvestmentDAO investmentDAO = new InitialInvestmentDAO();
        List<BranchMonthlyReport> branchMonthlyReportList;
        List<InitialInvestment> initialInvestmentList;
        Map<String, Double> systemTotals;
        int monthRange = 0;
        double totalInitialInvestment = 0;
        int listSize;

        if (action != null && action.equals("filter")) {
            branchMonthlyReportList = getFilteredReports(request, page, pageSize, monthlyReportDAO);
            listSize = (int) request.getAttribute("reportListSize");
            initialInvestmentList = getFilteredInitialInvestment(request, investmentDAO);
            totalInitialInvestment = getTotalInitialInvestment(request, investmentDAO);
            systemTotals = getSystemTotalMap(request, monthlyReportDAO);
            monthRange = getMonthRange(request, monthlyReportDAO);
        } else {
            branchMonthlyReportList = monthlyReportDAO.getListBranchMonthlyReportByPage(page, pageSize);
            List<BranchMonthlyReport> listAll = monthlyReportDAO.getBranchMonthlyReportSimple();
            listSize = listAll.size();
            initialInvestmentList = investmentDAO.getInitialInvestmentsWithHotelBranch();
            totalInitialInvestment = investmentDAO.getTotalInitialCapital();
            systemTotals = monthlyReportDAO.getSystemTotals();
            monthRange = monthlyReportDAO.getReportMonthRange();
        }

        int totalPages = (int) Math.ceil((double) listSize / pageSize);

        HotelBranchDAO branchDAO = new HotelBranchDAO();
        List<HotelBranch> branchList = branchDAO.getAllHotelBranchesSimple();
        
        // Set attribute để đổ dữ liệu về JSP
        request.setAttribute("monthRange", monthRange);
        request.setAttribute("action", action);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalInitialInvestment", totalInitialInvestment);
        request.setAttribute("reportListSize", listSize);
        request.setAttribute("branchList", branchList);
        request.setAttribute("branchMonthlyReportList", branchMonthlyReportList);
        request.setAttribute("initialInvestmentList", initialInvestmentList);
        request.setAttribute("systemTotals", systemTotals);
        request.getRequestDispatcher("./financialDashboard.jsp").forward(request, response);
    }

    private List<BranchMonthlyReport> getFilteredReports(HttpServletRequest request, int page, int pageSize, BranchMonthlyReportDAO dao) {
        String branchIdStr = request.getParameter("branchId");
        String fromDateStr = request.getParameter("fromDate");
        String toDateStr = request.getParameter("toDate");

        boolean hasFromDate = fromDateStr != null && !fromDateStr.isEmpty();
        boolean hasToDate = toDateStr != null && !toDateStr.isEmpty();

        int listSize = 0;
        List<BranchMonthlyReport> list = new ArrayList<>();

        if (branchIdStr != null) {
            int branchId = parseIntSafe(branchIdStr, 0);

            if (branchId == 0) {
                if (hasFromDate && hasToDate) {
                    Date fromDate = Date.valueOf(fromDateStr);
                    Date toDate = Date.valueOf(toDateStr);
                    list = dao.getBranchMonthlyReportsByMonthRange(branchId, fromDate, toDate, page, pageSize);
                    listSize = dao.getTotalBranchMonthlyReportByMonthRange(branchId, fromDate, toDate);
                } else {
                    list = dao.getListBranchMonthlyReportByPage(page, pageSize);
                    listSize = dao.getBranchMonthlyReportSimple().size();
                }
            } else {
                if (hasFromDate && hasToDate) {
                    Date fromDate = Date.valueOf(fromDateStr);
                    Date toDate = Date.valueOf(toDateStr);
                    list = dao.getBranchMonthlyReportsByBranchIdAndMonthRange(branchId, fromDate, toDate, page, pageSize);
                    listSize = dao.getTotalBranchMonthlyReportByBranchIdAndMonthRange(branchId, fromDate, toDate);
                } else {
                    list = dao.getBranchMonthlyReportsByBranchId(branchId, page, pageSize);
                    listSize = dao.getTotalBranchMonthlyReportByBranchId(branchId);
                }
            }
        }

        request.setAttribute("reportListSize", listSize);
        return list;
    }

    private int getMonthRange(HttpServletRequest request, BranchMonthlyReportDAO dao) {
        String branchIdStr = request.getParameter("branchId");
        String fromDateStr = request.getParameter("fromDate");
        String toDateStr = request.getParameter("toDate");

        boolean hasFromDate = fromDateStr != null && !fromDateStr.isEmpty();
        boolean hasToDate = toDateStr != null && !toDateStr.isEmpty();

        int monthRange = 0;

        if (branchIdStr != null) {
            int branchId = parseIntSafe(branchIdStr, 0);

            if (branchId == 0) {
                if (hasFromDate && hasToDate) {
                    Date fromDate = Date.valueOf(fromDateStr);
                    Date toDate = Date.valueOf(toDateStr);
                    monthRange = dao.getMonthDifference(fromDate, toDate);
                } else {
                    monthRange = dao.getReportMonthRange();
                }
            } else {
                if (hasFromDate && hasToDate) {
                    Date fromDate = Date.valueOf(fromDateStr);
                    Date toDate = Date.valueOf(toDateStr);
                    monthRange = dao.getReportMonthRangeByBranchAndDate(branchId, fromDate, toDate);
                } else {
                    monthRange = dao.getReportMonthRangeByBranch(branchId);
                }
            }
        }
        return monthRange;
    }
    
    private List<InitialInvestment> getFilteredInitialInvestment(HttpServletRequest request, InitialInvestmentDAO dao) {
        String branchIdStr = request.getParameter("branchId");
        String fromDateStr = request.getParameter("fromDate");
        String toDateStr = request.getParameter("toDate");

        boolean hasFromDate = fromDateStr != null && !fromDateStr.isEmpty();
        boolean hasToDate = toDateStr != null && !toDateStr.isEmpty();

        List<InitialInvestment> list = new ArrayList<>();

        if (branchIdStr != null) {
            int branchId = parseIntSafe(branchIdStr, 0);

            if (branchId == 0) {
                if (hasFromDate && hasToDate) {
                    Date fromDate = Date.valueOf(fromDateStr);
                    Date toDate = Date.valueOf(toDateStr);
                    list = dao.getInitialInvestmentsByDateRange(fromDate, toDate);
                } else {
                    list = dao.getInitialInvestmentsWithHotelBranch();
                }
            } else {
                if (hasFromDate && hasToDate) {
                    Date fromDate = Date.valueOf(fromDateStr);
                    Date toDate = Date.valueOf(toDateStr);
                    list = dao.getInitialInvestmentsByBranchAndDateRange(branchId, fromDate, toDate);
                } else {
                    list = dao.getInitialInvestmentsByBranchId(branchId);
                }
            }
        }
        return list;
    }

    private double getTotalInitialInvestment(HttpServletRequest request, InitialInvestmentDAO dao) {
        String branchIdStr = request.getParameter("branchId");
        String fromDateStr = request.getParameter("fromDate");
        String toDateStr = request.getParameter("toDate");

        boolean hasFromDate = fromDateStr != null && !fromDateStr.isEmpty();
        boolean hasToDate = toDateStr != null && !toDateStr.isEmpty();

        double total = 0;

        if (branchIdStr != null) {
            int branchId = parseIntSafe(branchIdStr, 0);

            if (branchId == 0) {
                if (hasFromDate && hasToDate) {
                    Date fromDate = Date.valueOf(fromDateStr);
                    Date toDate = Date.valueOf(toDateStr);
                    total = dao.getTotalCapitalByDateRange(fromDate, toDate);
                } else {
                    total = dao.getTotalInitialCapital();
                }
            } else {
                if (hasFromDate && hasToDate) {
                    Date fromDate = Date.valueOf(fromDateStr);
                    Date toDate = Date.valueOf(toDateStr);
                    total = dao.getTotalCapitalByBranchAndDateRange(branchId, fromDate, toDate);
                } else {
                    total = dao.getTotalCapitalByBranch(branchId);
                }
            }
        }
        return total;
    }
    
    private Map<String, Double> getSystemTotalMap (HttpServletRequest request, BranchMonthlyReportDAO dao) {
        String branchIdStr = request.getParameter("branchId");
        String fromDateStr = request.getParameter("fromDate");
        String toDateStr = request.getParameter("toDate");

        boolean hasFromDate = fromDateStr != null && !fromDateStr.isEmpty();
        boolean hasToDate = toDateStr != null && !toDateStr.isEmpty();

        Map<String, Double> result = new HashMap<>();

        if (branchIdStr != null) {
            int branchId = parseIntSafe(branchIdStr, 0);

            if (branchId == 0) {
                if (hasFromDate && hasToDate) {
                    Date fromDate = Date.valueOf(fromDateStr);
                    Date toDate = Date.valueOf(toDateStr);
                    result = dao.getSystemTotals(fromDate, toDate);
                } else {
                    result = dao.getSystemTotals();
                }
            } else {
                if (hasFromDate && hasToDate) {
                    Date fromDate = Date.valueOf(fromDateStr);
                    Date toDate = Date.valueOf(toDateStr);
                    result = dao.getTotalsByBranchAndDateRange(branchId, fromDate, toDate);
                } else {
                    result = dao.getTotalsByBranch(branchId);
                }
            }
        }
        return result;
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
