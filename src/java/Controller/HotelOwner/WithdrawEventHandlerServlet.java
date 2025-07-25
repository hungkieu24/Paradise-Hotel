/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.HotelOwner;

import Dal.WalletDAO;
import Dal.WalletTransactionDAO;
import Model.Wallet;
import Model.WalletTransaction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
@WebServlet(name = "WithdrawEventHandlerServlet", urlPatterns = {"/hotelOwner/withdrawEventHandler"})
public class WithdrawEventHandlerServlet extends HttpServlet {

    private final WalletTransactionDAO transactionDAO = new WalletTransactionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int transactionID = Integer.parseInt(request.getParameter("transactionID"));
        WalletTransaction walletTransaction = transactionDAO.getTransactionById(transactionID);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
        response.getWriter().write(gson.toJson(walletTransaction));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        if (action != null) {
            if (action.equals("edit")) {
                String status = request.getParameter("statusChoose");
                String description = request.getParameter("description");
                int transactionID = Integer.parseInt(request.getParameter("transactionID"));

                if (status.equalsIgnoreCase("Success")) {
                    boolean success = transactionDAO.updateTransactionStatusAndDescription(transactionID, status, description);
                    setSessionMessage(session, success ? "Update successful!" : "Failure to update!",
                            success ? "success" : "error");
                    response.sendRedirect("./withdraw");
                }
                if (status.equalsIgnoreCase("Cancelled")) {
                    String amountStr = request.getParameter("amount").replace(".", "");
                    int amount = Integer.parseInt(amountStr);
                    WalletTransaction transaction = transactionDAO.getTransactionById(transactionID);
                    WalletDAO walletDAO = new WalletDAO();
                    Wallet wallet = walletDAO.getWalletById(transaction.getWalletID());
                    boolean success = walletDAO.updateWalletBalance(wallet.getUserID(), amount);
                    transactionDAO.updateTransactionStatusAndDescription(transactionID, status, description);
                    setSessionMessage(session, success ? "Cancel successful!" : "Failure to cancel!",
                            success ? "success" : "error");
                    response.sendRedirect("./withdraw");
                }
            }
        }
    }

    private void setSessionMessage(HttpSession session, String message, String type) {
        session.setAttribute("message", message);
        session.setAttribute("messageType", type);
    }
}
