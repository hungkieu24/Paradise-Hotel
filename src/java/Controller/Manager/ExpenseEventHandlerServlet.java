/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Manager;

import Dal.ExpenseDAO;
import Model.Expense;
import Model.UserAccount;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.Date;

/**
 *
 * @author hungk
 */
@WebServlet(name = "ExpenseEventHandlerServlet", urlPatterns = {"/manager/expenseEventHandler"})
public class ExpenseEventHandlerServlet extends HttpServlet {

    private final ExpenseDAO expenseDAO = new ExpenseDAO();
    private final String Expense_PAGE = "./expense";
    private static final String COL_Expense_Type = "expense_type";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int expenseID = Integer.parseInt(request.getParameter("expenseID"));
        Expense expense = expenseDAO.getExpenseById(expenseID);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();

        response.getWriter().write(gson.toJson(expense));
    }

    private void setSessionMessage(HttpSession session, String message, String type) {
        session.setAttribute("message", message);
        session.setAttribute("messageType", type);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        if (action != null) {
            if (action.equals("add") || action.equals("edit")) {
                String expenseType = request.getParameter("expenseType");
                String expenseDateStr = request.getParameter("expenseDate");
                String amountStr = request.getParameter("amount").replace(".", "");
                String description = request.getParameter("description");

                int branchID = Integer.parseInt(request.getParameter("branchID").trim());
                double amount = Double.parseDouble(amountStr);
                Date expenseDate = Date.valueOf(expenseDateStr);
                Expense expense = new Expense(branchID, expenseType, amount, description, expenseDate);

                if (action.equals("add")) {
                    handleAddExpense(response, session, expense);
                    response.sendRedirect(Expense_PAGE);
                    return;
                }

                if (action.equals("edit")) {
                    handleEditExpense(request, session, expense);
                    response.sendRedirect(Expense_PAGE);
                    return;
                }
            }
            if (action.equals("delete")) {
                int expenseID = Integer.parseInt(request.getParameter("IdDelete").trim());
                boolean success = expenseDAO.deleteExpenseById(expenseID);
                setSessionMessage(session, success ? "Delete expense successful!" : "Failure to delete expense!",
                        success ? "success" : "error");
                response.sendRedirect(Expense_PAGE);
                return;
            }
        }
    }

    private void handleAddExpense(HttpServletResponse response, HttpSession session, Expense expense) throws ServletException, IOException {
        boolean isExistType = expenseDAO.isFieldExists(COL_Expense_Type, expense.getExpense_type(), null);
        if (isExistType) {
            setSessionMessage(session, "Expense type already exists", "error");
            return;
        }
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null) {
            setSessionMessage(session, "You need to login!", "error");
            response.sendRedirect("../login.jsp");

        }
        expense.setCreated_by(user.getId());

        boolean success = expenseDAO.insertExpense(expense);

        // Đặt thông báo session
        setSessionMessage(session, success ? "Add expense successful!" : "Failure to add expense!",
                success ? "success" : "error");
    }

    private void handleEditExpense(HttpServletRequest request, HttpSession session, Expense expense) throws ServletException, IOException {
        int expenseID = Integer.parseInt(request.getParameter("expenseID").trim());
        expense.setId(expenseID);
        boolean success = expenseDAO.updateExpense(expense);

        // Đặt thông báo session
        setSessionMessage(session, success ? "Update expense successful!" : "Failure to update expense!",
                success ? "success" : "error");
    }

}
