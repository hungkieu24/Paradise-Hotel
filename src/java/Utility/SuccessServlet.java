/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Utility;

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
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONObject;

/**
 *
 * @author hungk
 */
@WebServlet(name = "SuccessServlet", urlPatterns = {"/success"})
public class SuccessServlet extends HttpServlet {

    private final String API_KEY = "";
    private final String CLIENT_ID = "";
    private final String CHECKSUM_KEY = "";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String orderCode = request.getParameter("orderCode");
        String amountStr = request.getParameter("amount");
        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");
        BankAccountDAO bankAccountDAO = new BankAccountDAO();
        WalletTransactionDAO transactionDAO = new WalletTransactionDAO();
        if (user == null) {
            setSessionMessage(session, "You need to login!", "error");
            response.sendRedirect("./login.jsp");
            return;
        }

        if (orderCode == null || orderCode.isEmpty()) {
            setSessionMessage(session, "Missing orderCode", "error");
            response.sendRedirect("./myWallet");
            return;
        }

        try {
            // Gọi API PayOS để lấy thông tin thanh toán
            URL url = new URL("https://api-merchant.payos.vn/v2/payment-requests/" + orderCode);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("x-client-id", CLIENT_ID);
            conn.setRequestProperty("x-api-key", API_KEY);

            int responseCode = conn.getResponseCode();
            InputStream is = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line);
            }

            JSONObject json = new JSONObject(result.toString());
            JSONObject data = json.getJSONObject("data");
            String status = data.getString("status");

            if ("PAID".equalsIgnoreCase(status)) {
                // TODO: update đơn hàng, ví dụ nạp tiền vào Wallet, lưu vào DB...
                double amount = Double.parseDouble(amountStr);
                String userId = user.getId();
                WalletDAO walletDAO = new WalletDAO();

                Wallet wallet = walletDAO.getWalletByUserId(userId);
                boolean success = walletDAO.updateWalletBalance(userId, amount);
                setSessionMessage(session, success ? "Added successfully!" : "Fail to update wallet", success ? "success" : "error");
                
                WalletTransaction transaction = new WalletTransaction();
                transaction.setWalletID(wallet.getWalletID());
                transaction.setAmount(amount);
                transaction.setTransactionType("Deposit"); // hoặc "Refund", "Withdraw", "Payment"
                transaction.setDescription("Deposit From PayOS");
                transaction.setBookingID(0);
                transaction.setBranchID(0);
                transaction.setCreatedBy(userId);
                transaction.setStatus("Success"); // hoặc "Pending", "Failed"
                transaction.setBankAccountNumber(null);
                transactionDAO.addWalletTransaction(transaction);
                
            } else {
                setSessionMessage(session, "Payment not completed", "error");
            }

        } catch (Exception e) {
            e.printStackTrace();
            setSessionMessage(session, "Lỗi hệ thống khi xác nhận thanh toán", "error");
        }

        // Redirect về myWallet
        response.sendRedirect("./myWallet");
    }

    private void setSessionMessage(HttpSession session, String message, String type) {
        session.setAttribute("message", message);
        session.setAttribute("messageType", type);
    }
}
