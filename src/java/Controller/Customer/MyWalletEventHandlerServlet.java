/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Customer;

import Dal.BankAccountDAO;
import Model.BankAccount;
import Model.UserAccount;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author hungk
 */
@WebServlet(name = "MyWalletEventHandlerServlet", urlPatterns = {"/myWalletEventHandler"})
public class MyWalletEventHandlerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
        UserAccount user = (UserAccount) session.getAttribute("user");
        BankAccountDAO bankAccountDAO = new BankAccountDAO();
        if (user == null) {
            setSessionMessage(session, "You need to login!", "error");
            response.sendRedirect("./login.jsp");
            return;
        }
        String userID = user.getId();

        if (action != null) {
            if (action.equals("editBankAccount")) {
                String selectedBankValue = request.getParameter("bankNumDefault");
                if (selectedBankValue != null) {
                    int bankAccountId = Integer.parseInt(selectedBankValue);
                    boolean success = bankAccountDAO.setDefaultBankAccount(bankAccountId, userID);
                    setSessionMessage(session,
                            success ? "Default bank account has been changed!" : "Fail to change default bank account",
                            success ? "success" : "error");
                    response.sendRedirect("./myWallet");
                    return;
                } else {
                    setSessionMessage(session, "You have not selected any bank accounts!", "error");
                }

            }

            if (action.equals("delete")) {
                int idDelete = Integer.parseInt(request.getParameter("idDelete"));
                boolean success = bankAccountDAO.deleteBankAccount(idDelete, userID);
                setSessionMessage(session,
                        success ? "Deleted successfully!" : "Fail to delete",
                        success ? "success" : "error"
                );
                response.sendRedirect("./myWallet");
                return;
            }

            if (action.equals("addBankAccount")) {
                String bankNumber = request.getParameter("bankNumber");
                String bankAccountHolder = request.getParameter("bankAccountHolder");
                String bankName = request.getParameter("bankName");
                BankAccount bankAccount = new BankAccount(0, userID, bankName, bankNumber, bankAccountHolder, false);

                boolean success = bankAccountDAO.addBankAccount(bankAccount);
                setSessionMessage(session,
                        success ? "Added successfully!" : "Fail to add",
                        success ? "success" : "error"
                );
                response.sendRedirect("./myWallet");
                return;
            }
        }
    }
}
