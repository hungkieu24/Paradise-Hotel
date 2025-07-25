/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Customer;

import Dal.BankAccountDAO;
import Dal.WalletDAO;
import Dal.WalletTransactionDAO;
import Model.BankAccount;
import Model.UserAccount;
import Model.Wallet;
import Model.WalletTransaction;
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

            if (action.equals("withdraw")) {
                double amountWithdraw = Double.parseDouble(request.getParameter("amountWithdraw"));
                WalletDAO walletDAO = new WalletDAO();
                WalletTransactionDAO transactionDAO = new WalletTransactionDAO();
                BankAccount bankAccount = bankAccountDAO.getDefaultBankAccount(userID);

                Wallet wallet = walletDAO.getWalletByUserId(userID);
                WalletTransaction transaction = new WalletTransaction();
                transaction.setWalletID(wallet.getWalletID());
                transaction.setAmount(amountWithdraw);
                transaction.setTransactionType("Withdraw"); // hoặc "Refund", "Withdraw", "Payment"
                transaction.setDescription("Withdraw to bank account");
                transaction.setBookingID(0);
                transaction.setBranchID(0);
                transaction.setCreatedBy(userID);
                transaction.setStatus("Pending"); // hoặc "Pending", "Failed"
                transaction.setBankAccountID(bankAccount.getBankAccountID());
                transactionDAO.addWalletTransaction(transaction);
                boolean success = walletDAO.updateWalletBalance(userID, -amountWithdraw);
                setSessionMessage(session,
                        success ? "Your withdrawal request has been submitted.!" : "Your withdrawal request failed.",
                        success ? "success" : "error");
                response.sendRedirect("./myWallet");
                return;
            }
        }
    }
}
