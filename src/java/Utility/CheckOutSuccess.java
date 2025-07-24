/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Utility;

import Dal.BankAccountDAO;
import Dal.BookingDAO;
import Dal.BookingRoomTypeDAO;
import Dal.InvoiceDAO;
import Dal.LoyaltyPointDAO;
import Dal.RoomDAO;
import Dal.UserAccountDAO;
import Dal.WalletDAO;
import Dal.WalletTransactionDAO;
import Model.Booking;
import Model.BookingRoomType;
import Model.Invoice;
import Model.Room;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.json.JSONObject;

/**
 *
 * @author hungk
 */
@WebServlet(name = "CheckOutSuccess", urlPatterns = {"/checkOutSuccess"})
public class CheckOutSuccess extends HttpServlet {

    private final String API_KEY = PaymentConfig.API_KEY;
    private final String CLIENT_ID = PaymentConfig.CLIENT_ID;
    private final String RETURN_PAGE = "./staff-bookings-list";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String orderCode = request.getParameter("orderCode");
        String amountStr = request.getParameter("amount");
        String bookingIdStr = request.getParameter("bookingId");

        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");

        if (user == null) {
            setSessionMessage(session, "You need to login!", "error");
            response.sendRedirect("./login.jsp");
            return;
        }

        if (orderCode == null || orderCode.isEmpty()) {
            setSessionMessage(session, "Missing orderCode", "error");
            response.sendRedirect(RETURN_PAGE);
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
                String staffId = user.getId();

                // Update Booking
                int bookingId = Integer.parseInt(bookingIdStr);
                BookingDAO bookingDAO = new BookingDAO();
                Booking bookingCurrent = bookingDAO.getBookingById(bookingId);
                bookingDAO.updateBookingStatus(bookingId, "Completed");
                bookingDAO.updateBookingPaymentStatus(bookingId, "Paid");

                // Set invoice
                InvoiceDAO invoiceDAO = new InvoiceDAO();
                Invoice invoice = new Invoice();
                invoice.setBookingId(bookingId);
                invoice.setTotalAmount(amount);
                invoiceDAO.createInvoice(invoice);

                //Plus point
                LoyaltyPointDAO loyaltyPointDAO = new LoyaltyPointDAO();
                loyaltyPointDAO.addPoints(bookingCurrent.getUserId(), (int) amount / 100000, "Payment success " + amount);
                boolean loyaltyUpdated = loyaltyPointDAO.updateTotalSpending(bookingCurrent.getUserId(), amount);

                //Set roomtype to Available
                RoomDAO roomDAO = new RoomDAO();
                roomDAO.updateRoomStatusAfterCheckout(bookingId, "Available");

                UserAccountDAO userDAO = new UserAccountDAO();
                UserAccount customer = userDAO.findById(bookingCurrent.getUserId());
                Integer branchId = user.getBranchId();

                List<Room> bookingRoomList = bookingDAO.getRoomsByBookingIdAndBranch(bookingId, branchId != null ? branchId : 1);
                BookingRoomTypeDAO bookingRoomTypeDAO = new BookingRoomTypeDAO();
                List<BookingRoomType> bookingRoomTypes = bookingRoomTypeDAO.getBookingRoomTypesByBookingId(bookingId);

                try {
                    EmailUtility.sendInvoice(customer.getEmail(), "Invoice", amountStr, amountStr,
                            bookingRoomList, customer, bookingCurrent, bookingRoomTypes);
                } catch (Exception e) {
                }
                setSessionMessage(session, "Payment completed", "success");
            } else {
                setSessionMessage(session, "Payment not completed", "error");
            }

        } catch (Exception e) {
            e.printStackTrace();
            setSessionMessage(session, "Lỗi hệ thống khi xác nhận thanh toán", "error");
        }

        // Redirect về myWallet
        response.sendRedirect(RETURN_PAGE);
    }

    private void setSessionMessage(HttpSession session, String message, String type) {
        session.setAttribute("message", message);
        session.setAttribute("messageType", type);
    }

}
