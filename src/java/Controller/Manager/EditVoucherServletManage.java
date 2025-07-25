/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Manager;

import Dal.RoomDAO;
import Dal.VoucherDAO;
import Model.UserAccount;
import Model.Voucher;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.Date;
import java.time.LocalDate;

/**
 *
 * @author thien
 */
@WebServlet(name = "EditVoucherServletManage", urlPatterns = {"/editVoucher"})
public class EditVoucherServletManage extends HttpServlet {

    private VoucherDAO v = new VoucherDAO();
    private RoomDAO r = new RoomDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UserAccount user = (session != null) ? (UserAccount) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect("login");
            return;
        }

        try {
            int voucherId = Integer.parseInt(request.getParameter("voucher_id"));
            String code = request.getParameter("voucher_code");
            String description = request.getParameter("description");

            String discountPercentStr = request.getParameter("discount_percent");
            String discountAmountStr = request.getParameter("discount_amount");
            String minPriceStr = request.getParameter("min_price");
            String totalQuantityStr = request.getParameter("total_quantity");
            String fromDateStr = request.getParameter("from_date");
            String toDateStr = request.getParameter("to_date");
            String newStatus = request.getParameter("status");

            double discountPercent = (discountPercentStr != null && !discountPercentStr.isEmpty())
                    ? Double.parseDouble(discountPercentStr) : 0.0;
            double discountAmount = (discountAmountStr != null && !discountAmountStr.isEmpty())
                    ? Double.parseDouble(discountAmountStr) : 0.0;
            double minPrice = Double.parseDouble(minPriceStr);
            int totalQuantity = Integer.parseInt(totalQuantityStr);
            Date fromDate = Date.valueOf(fromDateStr);
            Date toDate = Date.valueOf(toDateStr);
            Voucher current = v.getVoucherById(voucherId);
            LocalDate from = fromDate.toLocalDate();
            LocalDate to = toDate.toLocalDate();
            LocalDate today = LocalDate.now();
            if(newStatus.equalsIgnoreCase("Inactive") && current.getStatus().equalsIgnoreCase("Active")){
                if(from.isAfter(to)){
                    session.setAttribute("error", "Start date must be before or equal to end date.");
                    response.sendRedirect("vouchers");
                    return;
                }
                if(to.isBefore(today)){
                    session.setAttribute("error", "End date must not be in the past when activating voucher.");
                    response.sendRedirect("vouchers");
                    return;
                }
            }

            Voucher voucher = new Voucher();
            voucher.setId(voucherId);
            voucher.setCode(code);
            voucher.setDescription(description);
            voucher.setDiscount_percent(discountPercent);
            voucher.setDiscount_amount(discountAmount);
            voucher.setMin_price(minPrice);
            voucher.setTotal_quantity(totalQuantity);
            voucher.setValid_from(fromDate);
            voucher.setValid_to(toDate);
            voucher.setStatus(newStatus);
            

            boolean success = v.updateVoucher(voucher);

            if (success) {
                // Redirect back with success message
                response.sendRedirect("vouchers?success=Update successful");
            } else {
                response.sendRedirect("vouchers?error=Update failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("vouchers?error=Exception occurred");
        }
    }

}
