/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.HotelOwner;

import Dal.HotelBranchDAO;
import Dal.InitialInvestmentDAO;
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
@WebServlet(name = "InvestmentServlet", urlPatterns = {"/hotelOwner/investment"})
public class InvestmentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        InitialInvestmentDAO investmentDAO = new InitialInvestmentDAO();
        HotelBranchDAO branchDAO = new HotelBranchDAO();

        List<HotelBranch> branchList = branchDAO.getAllHotelBranchesSimple();
        LocalDate today = LocalDate.now();
        int currentMonth = investmentDAO.getLatestInvestmentMonthYear().getMonthValue(); // từ 1 đến 12
        int currentYear = investmentDAO.getLatestInvestmentMonthYear().getYear();
        double totalInitialCapital = investmentDAO.getTotalInitialCapital();
        
        List<InitialInvestment> investmentList = investmentDAO.getInitialInvestmentsByMonthYear(currentMonth, currentYear);

        if (action != null) {
            if (action.equals("filterByMonthYear")) {
                int branchID = Integer.parseInt(request.getParameter("branchID"));
                currentMonth = Integer.parseInt(request.getParameter("month"));
                currentYear = Integer.parseInt(request.getParameter("year"));
                investmentList = investmentDAO.getInitialInvestmentsByBranchAndMonthYear(branchID, currentMonth, currentYear);
                totalInitialCapital = investmentDAO.getTotalCapitalByBranchAndMonthYear(branchID, currentMonth, currentYear);
            }
        }

        List<String> monthNames = new ArrayList<>();
        DateFormatSymbols dfs = new DateFormatSymbols(Locale.ENGLISH);
        String[] months = dfs.getMonths();
        for (int i = 0; i < 12; i++) {
            monthNames.add(months[i]); // January → December
        }

        request.setAttribute("totalInitialCapital", totalInitialCapital);
        request.setAttribute("monthNames", monthNames);
        request.setAttribute("month", currentMonth);
        request.setAttribute("year", currentYear);
        request.setAttribute("branchList", branchList);
        request.setAttribute("investmentList", investmentList);
        request.getRequestDispatcher("./investment.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

}
