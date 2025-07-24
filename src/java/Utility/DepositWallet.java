/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Utility;

import java.io.IOException;
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
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONObject;

/**
 *
 * @author hungk
 */
@WebServlet(name = "DepositWallet", urlPatterns = {"/deposit"})
public class DepositWallet extends HttpServlet {

    private final String API_KEY = PaymentConfig.API_KEY;
    private final String CLIENT_ID = PaymentConfig.CLIENT_ID;
    private final String CHECKSUM_KEY = PaymentConfig.CHECKSUM_KEY;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        String amountStr = request.getParameter("amountDeposit");
        if (amountStr == null || amountStr.trim().isEmpty()) {
            setSessionMessage(session, "Amount is required", "error");
            response.sendRedirect("./myWallet");
            return;
        }

        long amount;
        try {
            amount = Long.parseLong(amountStr.trim());
            if (amount <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            setSessionMessage(session, "Invalid amount", "error");
            response.sendRedirect("./myWallet");
            return;
        }

        long orderCode = System.currentTimeMillis();
        String description = "Deposit";
        String returnUrl = "http://localhost:8080/ParadiseHotel/success?orderCode=" + orderCode + "&amount=" + amount;
        String cancelUrl = "http://localhost:8080/ParadiseHotel/cancel-payment";

        String dataToSign = "amount=" + amount
                + "&cancelUrl=" + cancelUrl
                + "&description=" + description
                + "&orderCode=" + orderCode
                + "&returnUrl=" + returnUrl;

        String signature;
        try {
            signature = hmacSHA256(dataToSign, CHECKSUM_KEY);
        } catch (Exception e) {
            setSessionMessage(session, "Failed to sign request", "error");
            response.sendRedirect("./myWallet");
            return;
        }

        String jsonBody = "{"
                + "\"orderCode\":" + orderCode + ","
                + "\"amount\":" + amount + ","
                + "\"description\":\"" + description + "\","
                + "\"returnUrl\":\"" + returnUrl + "\","
                + "\"cancelUrl\":\"" + cancelUrl + "\","
                + "\"signature\":\"" + signature + "\","
                + "\"items\":["
                + " {"
                + "   \"name\":\"" + description + "\","
                + "   \"quantity\":1,"
                + "   \"price\":" + amount
                + " }"
                + "]"
                + "}";

        try {
            URL url = new URL("https://api-merchant.payos.vn/v2/payment-requests");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("x-client-id", CLIENT_ID);
            conn.setRequestProperty("x-api-key", API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            InputStream is = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
            System.out.println("PayOS response: " + result.toString());

            String checkoutUrl = extractCheckoutUrl(result.toString());
            if (checkoutUrl != null) {
                response.sendRedirect(checkoutUrl); // redirect đến PayOS
            } else {
                setSessionMessage(session, "Unable to get payment link", "error");
                response.sendRedirect("./myWallet");
            }
        } catch (IOException e) {
            setSessionMessage(session, "Error connecting to payment gateway", "error");
            response.sendRedirect("./myWallet");
        }
    }

    private String extractCheckoutUrl(String jsonResponse) {
        try {
            JSONObject json = new JSONObject(jsonResponse);
            return json.getJSONObject("data").getString("checkoutUrl");
        } catch (Exception e) {
            return null;
        }
    }

    // Hàm ký HMAC SHA256
    public static String hmacSHA256(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private void setSessionMessage(HttpSession session, String message, String type) {
        session.setAttribute("message", message);
        session.setAttribute("messageType", type);
    }
}
