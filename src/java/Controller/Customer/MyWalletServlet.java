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
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 *
 * @author hungk
 */
@WebServlet(name = "MyWalletServlet", urlPatterns = {"/myWallet"})
public class MyWalletServlet extends HttpServlet {
    private final WalletDAO walletDAO = new WalletDAO();;
    private final BankAccountDAO bankAccountDAO = new BankAccountDAO();
    private final WalletTransactionDAO transactionDAO = new WalletTransactionDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");

        if (user == null) {
            setSessionMessage(session, "You need to login!", "error");
            response.sendRedirect("./login.jsp");
            return;
        }
        
        
        String userID = user.getId();
        Wallet wallet = walletDAO.getWalletByUserId(userID);
        List<BankAccount> bankAccounts = bankAccountDAO.getBankAccountsByUserId(userID);
        BankAccount defaultBankAccount = bankAccountDAO.getDefaultBankAccount(userID);
        List<WalletTransaction> transactions = transactionDAO.getTransactionsByUserId(wallet.getWalletID());

        request.setAttribute("transactions", transactions);
        request.setAttribute("defaultBankAccount", defaultBankAccount);
        request.setAttribute("bankAccounts", bankAccounts);
        request.setAttribute("wallet", wallet);
        request.getRequestDispatcher("myWallet.jsp").forward(request, response);
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
