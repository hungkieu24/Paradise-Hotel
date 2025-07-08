/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.HotelOwner;

import Dal.BranchMonthlyReportDAO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.sql.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author hungk
 */
@WebServlet(name = "financialChartData", urlPatterns = {"/hotelOwner/chartData"})
public class financialChartData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String branchIdStr = request.getParameter("branchId");
        String fromDateStr = request.getParameter("fromDate");
        String toDateStr = request.getParameter("toDate");

        boolean hasFromDate = fromDateStr != null && !fromDateStr.isEmpty();
        boolean hasToDate = toDateStr != null && !toDateStr.isEmpty();
        boolean hasBranch = branchIdStr != null && !branchIdStr.isEmpty();

        BranchMonthlyReportDAO dao = new BranchMonthlyReportDAO();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // === 1. Lấy dữ liệu profit trend ===
            Map<String, Double> profitMap;
            if (hasBranch && !"0".equals(branchIdStr)) {
                int branchId = Integer.parseInt(branchIdStr);
                if (hasFromDate && hasToDate) {
                    Date fromDate = Date.valueOf(fromDateStr);
                    Date toDate = Date.valueOf(toDateStr);
                    profitMap = dao.getMonthlyProfitTrendByBranchAndDateRange(branchId, fromDate, toDate);
                } else {
                    profitMap = dao.getMonthlyProfitTrendByBranch(branchId);
                }
            } else {
                if (hasFromDate && hasToDate) {
                    Date fromDate = Date.valueOf(fromDateStr);
                    Date toDate = Date.valueOf(toDateStr);
                    profitMap = dao.getMonthlyProfitTrendByDateRange(fromDate, toDate);
                } else {
                    profitMap = dao.getMonthlyProfitTrend();
                }
            }

            List<String> profitLabels = new ArrayList<>();
            List<Double> profitData = new ArrayList<>();
            for (Map.Entry<String, Double> entry : profitMap.entrySet()) {
                profitLabels.add(entry.getKey());
                profitData.add(entry.getValue());
            }

            // === 2. Lấy dữ liệu branch comparison ===
            Map<String, Map<String, Double>> comparisonMap;
            if (hasFromDate && hasToDate) {
                Date fromDate = Date.valueOf(fromDateStr);
                Date toDate = Date.valueOf(toDateStr);
                comparisonMap = dao.getBranchComparisonDataByDateRange(fromDate, toDate);
            } else {
                comparisonMap = dao.getBranchComparisonData();
            }

            // === 3. Gộp vào JSON object chung ===
            Map<String, Object> responseData = new HashMap<>();

            responseData.put("profitChart", Map.of(
                    "labels", profitLabels,
                    "data", profitData
            ));

            responseData.put("branchChart", comparisonMap);

            String json = new Gson().toJson(responseData);
            response.getWriter().write(json);

        } catch (Exception ex) {
            ex.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi xử lý dữ liệu biểu đồ");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

}
