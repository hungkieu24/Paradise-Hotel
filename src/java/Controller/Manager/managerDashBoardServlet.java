/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Manager;

import Dal.BookingDAO;
import Dal.BranchMonthlyReportDAO;
import Dal.ExpenseDAO;
import Dal.FeedbackDAO;
import Dal.HotelBranchDAO;
import Dal.InitialInvestmentDAO;
import Dal.RevenueDAO;
import Dal.RoomDAO;
import Model.HotelBranch;
import Model.InitialInvestment;
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
@WebServlet(name = "managerDashBoardServlet", urlPatterns = {"/manager/dashboard"})
public class managerDashBoardServlet extends HttpServlet {

    private final HotelBranchDAO branchDAO = new HotelBranchDAO();
    private final RevenueDAO revenueDAO = new RevenueDAO();
    private final ExpenseDAO expenseDAO = new ExpenseDAO();
    private final InitialInvestmentDAO investmentDAO = new InitialInvestmentDAO();
    private final BranchMonthlyReportDAO branchMonthlyReportDAO = new BranchMonthlyReportDAO();
    private final FeedbackDAO feedbackDAO = new FeedbackDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");
        String action = request.getParameter("action");

        if (checkLogin(user, session, response)) {
            response.sendRedirect("../login.jsp");
            return;
        }

        String managerId = user.getId();

        HotelBranch branch = branchDAO.getBranchByManagerId(managerId);
        int branchId = branch.getId();

        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue(); // từ 1 đến 12
        int currentYear = today.getYear();
        int monthTo = currentMonth;
        int yearTo = currentYear;

        // Lấy total revenue theo tháng hiện tại
        double totalRevenue = revenueDAO.getTotalRevenueByBranchAndMonthRange(branchId, currentMonth, currentYear, monthTo, yearTo);
        // Lấy total expense theo tháng hiện tại
        double totalExpense = expenseDAO.getTotalExpenseByBranchAndMonthRange(branchId, currentMonth, currentYear, monthTo, yearTo);

        if (action != null) {
            if (action.equals("filterByMonthRange")) {
                currentMonth = Integer.parseInt(request.getParameter("monthFrom"));
                currentYear = Integer.parseInt(request.getParameter("yearFrom"));
                monthTo = Integer.parseInt(request.getParameter("monthTo"));
                yearTo = Integer.parseInt(request.getParameter("yearTo"));

                totalRevenue = revenueDAO.getTotalRevenueByBranchAndMonthRange(branchId, currentMonth, currentYear, monthTo, yearTo);
                totalExpense = expenseDAO.getTotalExpenseByBranchAndMonthRange(branchId, currentMonth, currentYear, monthTo, yearTo);
            }
        }

        double totalCapital = investmentDAO.getTotalCapitalByBranchId(branchId);
        double Profit = totalRevenue - totalExpense;
        double ProfitRate = 0;

        if (totalCapital != 0) {
            ProfitRate = (Profit / totalCapital) * 100;
        }

        List<String> monthNames = prepareMonthNames();
        List<InitialInvestment> initialInvestmentList = investmentDAO.getInitialInvestmentsByBranchId(branchId);
        double totalInital = investmentDAO.getTotalCapitalByBranchId(branchId);
        double averageFeedbackRating = feedbackDAO.getAverageRatingByBranch(branchId);
        int totalGuest = bookingDAO.getTotalGuestsCompletedBookingByBranchAndMonthRange(branchId, currentMonth, currentYear, monthTo, yearTo);
        double occupancyRate = roomDAO.getOccupancyRate(branchId, currentMonth, currentYear, monthTo, yearTo);
        int totalBooking = bookingDAO.getTotalBookingByBranchAndMonthRange(branchId, currentMonth, currentYear, monthTo, yearTo);

        if (action == null) {
            LocalDate reportMonth = LocalDate.of(currentYear, currentMonth, 1);
            branchMonthlyReportDAO.upsertMonthlyReport(branchId, reportMonth, totalRevenue, totalExpense, Profit, ProfitRate);
        }

        request.setAttribute("totalBooking", totalBooking);
        request.setAttribute("occupancyRate", occupancyRate);
        request.setAttribute("totalGuest", totalGuest);
        request.setAttribute("averageFeedbackRating", averageFeedbackRating);
        request.setAttribute("totalInital", totalInital);
        request.setAttribute("initialInvestmentList", initialInvestmentList);
        request.setAttribute("totalExpense", totalExpense);
        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("Profit", Profit);
        request.setAttribute("ProfitRate", ProfitRate);
        request.setAttribute("monthNames", monthNames);
        request.setAttribute("monthFrom", currentMonth);
        request.setAttribute("yearFrom", currentYear);
        request.setAttribute("monthTo", monthTo);
        request.setAttribute("yearTo", yearTo);
        request.setAttribute("branch", branch);
        request.getRequestDispatcher("./dashboard.jsp").forward(request, response);
    }

    private boolean checkLogin(UserAccount user, HttpSession session, HttpServletResponse response) throws IOException {
        if (user == null) {
            setSessionMessage(session, "You need to login!", "error");
            return true;
        }
        return false;
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

    private void setSessionMessage(HttpSession session, String message, String type) {
        session.setAttribute("message", message);
        session.setAttribute("messageType", type);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

}
