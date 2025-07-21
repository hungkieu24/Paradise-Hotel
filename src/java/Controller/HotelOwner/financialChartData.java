/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.HotelOwner;

import Dal.BranchMonthlyReportDAO;
import com.google.gson.Gson;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
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

        int currentMonth = Integer.parseInt(request.getParameter("monthFrom"));
        int currentYear = Integer.parseInt(request.getParameter("yearFrom"));
        int monthTo = Integer.parseInt(request.getParameter("monthTo"));
        int yearTo = Integer.parseInt(request.getParameter("yearTo"));
        int branchId = Integer.parseInt(request.getParameter("branchId"));

        BranchMonthlyReportDAO dao = new BranchMonthlyReportDAO();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // === 1. Lấy dữ liệu profit trend ===
            Map<String, Double> profitMap;
            // === 2. Lấy dữ liệu branch comparison ===
            Map<String, Map<String, Double>> comparisonMap;

            if (branchId != 0) {
                profitMap = dao.getMonthlyProfitTrendByBranchAndDateRange(branchId, currentMonth, currentYear, monthTo, yearTo);
                comparisonMap = dao.getBranchIndicatorsByMonthRange(branchId, currentMonth, currentYear, monthTo, yearTo);
            } else {
                profitMap = dao.getMonthlyProfitTrendAllBranches(currentMonth, currentYear, monthTo, yearTo);
                comparisonMap = dao.getBranchComparisonDataByDateRange(currentMonth, currentYear, monthTo, yearTo);
            }

            List<String> profitLabels = new ArrayList<>();
            List<Double> profitData = new ArrayList<>();
            for (Map.Entry<String, Double> entry : profitMap.entrySet()) {
                profitLabels.add(entry.getKey());
                profitData.add(entry.getValue());
            }

            // === 3. Gộp vào JSON object chung ===
            Map<String, Object> responseData = new HashMap<>();

            responseData.put("profitChart", Map.of(
                    "labels", profitLabels,
                    "data", profitData
            ));
            responseData.put("branchId", branchId);
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
