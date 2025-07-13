package com.vnpay.common;


import Dal.BookingDAO;
import Dal.VNPayPaymentDAO;
import Model.VNPayPayment;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/payment-result")
public class PaymentResultServlet extends HttpServlet {
    
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {        
        // Capture payment response
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");

        // Verify signature
        String signValue = Config.hashAllFields(fields);
        boolean isValidSignature = signValue.equals(vnp_SecureHash);

        // Get parameters from request
        String vnp_TxnRef = request.getParameter("vnp_TxnRef");
        String vnp_TransactionNo = request.getParameter("vnp_TransactionNo");
        String vnp_BankCode = request.getParameter("vnp_BankCode");
        String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
        String vnp_PayDate = request.getParameter("vnp_PayDate");
        String vnp_Amount = request.getParameter("vnp_Amount");
        String vnp_OrderInfo = request.getParameter("vnp_OrderInfo");
        
        
        int bookingId = Integer.parseInt(vnp_TxnRef.split("-")[1]);
        double amount = Double.parseDouble(vnp_Amount) / 100.0;

        VNPayPayment payment = new VNPayPayment();
                payment.setBookingId(bookingId);
                payment.setAmount(amount);
                payment.setStatus("Completed");
                payment.setPaidAt(new Timestamp(System.currentTimeMillis()));
                
                
        VNPayPaymentDAO dao = new VNPayPaymentDAO();
        int paymentId = dao.createPayment(payment);
        BookingDAO bookingDAO = new BookingDAO();   
        bookingDAO.updateBookingStatus(bookingId, "Completed");    
        request.setAttribute("vnp_TxnRef", vnp_TxnRef);
        request.setAttribute("vnp_TransactionNo", vnp_TransactionNo);
        request.setAttribute("vnp_BankCode", vnp_BankCode);
        request.setAttribute("vnp_ResponseCode", vnp_ResponseCode);
        request.setAttribute("vnp_PayDate", vnp_PayDate);
        request.setAttribute("vnp_Amount", vnp_Amount);
        request.setAttribute("vnp_OrderInfo", vnp_OrderInfo);
        request.setAttribute("status", "SUCCESS");

        request.getRequestDispatcher("vnpay_return.jsp").forward(request, response);
        
    }
    
    
    
}
