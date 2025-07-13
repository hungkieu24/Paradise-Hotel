/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Manager;

import Dal.BookingDAO;
import Dal.BranchMonthlyReportDAO;
import Dal.HotelBranchDAO;
import Dal.ServiceDAO;
import Model.HotelBranch;
import Model.UserAccount;
import com.google.gson.Gson;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author hungk
 */
@WebServlet(name = "managerDashBoardChartServlet", urlPatterns = {"/manager/chartData"})
public class managerDashBoardChartServlet extends HttpServlet {

    private final HotelBranchDAO branchDAO = new HotelBranchDAO();
    private final BranchMonthlyReportDAO branchMonthlyReportDAO = new BranchMonthlyReportDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final ServiceDAO serviceDAO = new ServiceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");

        if (user == null) {
            setSessionMessage(session, "You need to login!", "error");
            response.sendRedirect("../login.jsp");
        }

        String managerId = user.getId();

        HotelBranch branch = branchDAO.getBranchByManagerId(managerId);
        int branchId = branch.getId();

        int monthFrom = Integer.parseInt(request.getParameter("monthFrom"));
        int yearFrom = Integer.parseInt(request.getParameter("yearFrom"));
        int monthTo = Integer.parseInt(request.getParameter("monthTo"));
        int yearTo = Integer.parseInt(request.getParameter("yearTo"));

        // Lấy Profit Trend
        Map<String, Double> profitTrend = branchMonthlyReportDAO.getMonthlyProfitTrendByBranchAndMonthYear(branchId, monthFrom, yearFrom, monthTo, yearTo);
        Map<String, Integer> statusCounts = bookingDAO.getBookingStatusCountsByBranchAndMonthYearRange(branchId, monthFrom, yearFrom, monthTo, yearTo);
        Map<String, Integer> serviceUsage = serviceDAO.getServiceUsageByBranchAndMonthRange(branchId, monthFrom, yearFrom, monthTo, yearTo);

        // Chuẩn bị response
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("profitTrend", profitTrend);
        responseData.put("bookingStatusCounts", statusCounts);
        responseData.put("serviceUsage", serviceUsage);
        response.setContentType("application/json");
        Gson gson = new Gson();
        String json = gson.toJson(responseData);
        response.getWriter().write(json);
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
