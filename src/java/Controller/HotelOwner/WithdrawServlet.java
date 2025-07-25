/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.HotelOwner;

import Dal.WalletTransactionDAO;
import Model.UserAccount;
import Model.WalletTransaction;
import java.io.IOException;
import java.io.PrintWriter;
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
@WebServlet(name = "WithdrawServlet", urlPatterns = {"/hotelOwner/withdraw"})
public class WithdrawServlet extends HttpServlet {

    private final WalletTransactionDAO transactionDAO = new WalletTransactionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");

        if (checkLogin(user, session, response)) {
            response.sendRedirect("../login.jsp");
            return;
        }

        String action = request.getParameter("action");
        int page = parseIntSafe(request.getParameter("page"), 1);
        int pageSize = 10;
        int listSize = 0;

        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();
        int monthTo = currentMonth;
        int yearTo = currentYear;

        List<WalletTransaction> walletTransactions = transactionDAO.getWithdrawTransactionsByMonthRangeAndPage(currentMonth, currentYear, monthTo, yearTo, page, pageSize);
        listSize = transactionDAO.countWithdrawTransactionsByMonthRange(currentMonth, currentYear, monthTo, yearTo);

        String status = null;
        if (action != null && action.equals("filterByMonthYear")) {
            currentMonth = Integer.parseInt(request.getParameter("monthFrom"));
            currentYear = Integer.parseInt(request.getParameter("yearFrom"));
            monthTo = Integer.parseInt(request.getParameter("monthTo"));
            yearTo = Integer.parseInt(request.getParameter("yearTo"));
            status = request.getParameter("status");

            if (status.equalsIgnoreCase("all")) {
                walletTransactions = transactionDAO.getWithdrawTransactionsByMonthRangeAndPage(currentMonth, currentYear, monthTo, yearTo, page, pageSize);
                listSize = transactionDAO.countWithdrawTransactionsByMonthRange(currentMonth, currentYear, monthTo, yearTo);
            } else {
                walletTransactions = transactionDAO.getWithdrawTransactionsByStatusAndPage(status, currentMonth, currentYear, monthTo, yearTo, page, pageSize);
                listSize = transactionDAO.countWithdrawTransactionsByStatus(status, currentMonth, currentYear, monthTo, yearTo);
            }
        }

        int totalPages = (int) Math.ceil((double) listSize / pageSize);
        List<String> monthNames = prepareMonthNames();

        request.setAttribute("walletTransactions", walletTransactions);
        request.setAttribute("status", status);
        request.setAttribute("monthNames", monthNames);
        request.setAttribute("monthFrom", currentMonth);
        request.setAttribute("yearFrom", currentYear);
        request.setAttribute("monthTo", monthTo);
        request.setAttribute("yearTo", yearTo);
        request.setAttribute("action", action);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("listSize", listSize);
        request.getRequestDispatcher("./withdraw.jsp").forward(request, response);
    }

    private int parseIntSafe(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
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

    private boolean checkLogin(UserAccount user, HttpSession session, HttpServletResponse response) throws IOException {
        if (user == null) {
            setSessionMessage(session, "You need to login!", "error");
            return true;
        }
        return false;
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
