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

/**
 *
 * @author thien
 */
@WebServlet(name = "AddVoucherServletManage", urlPatterns = {"/addVoucher"})
public class AddVoucherServletManage extends HttpServlet {

    private RoomDAO r = new RoomDAO();
    private VoucherDAO v = new VoucherDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UserAccount user = (session != null) ? (UserAccount) session.getAttribute("user") : null;
        if (user != null) {
            //lấy tên chi nhánh và tên manager
            String username = user.getUsername();
            String userId = user.getId();
            String branchName = r.getBranchNameById(userId);
            int branchId = r.getBranchId(userId);
            // Lấy dữ liệu từ form
            String code = request.getParameter("voucher_code");
            String description = request.getParameter("description");
            String discountPercentStr = request.getParameter("discount_percent");
            String discountAmountStr = request.getParameter("discount_amount");
            String minPriceStr = request.getParameter("min_price");
            String totalQuantityStr = request.getParameter("total_quantity");
            String fromDateStr = request.getParameter("from_date");
            String toDateStr = request.getParameter("to_date");
            String status = request.getParameter("status");
            if (v.isVoucherCodeExist(code, branchId)) {
                session.setAttribute("error", "Voucher code already exists");
                session.setAttribute("returnPage", "vouchers");
                response.sendRedirect("vouchers");
                return;
            }

            // Parse dữ liệu
            double discountPercent = (discountPercentStr != null && !discountPercentStr.isEmpty())
                    ? Double.parseDouble(discountPercentStr) : 0.0;
            double discountAmount = (discountAmountStr != null && !discountAmountStr.isEmpty())
                    ? Double.parseDouble(discountAmountStr) : 0.0;
            double minPrice = Double.parseDouble(minPriceStr);
            int totalQuantity = Integer.parseInt(totalQuantityStr);
            Date fromDate = Date.valueOf(fromDateStr);
            Date toDate = Date.valueOf(toDateStr);
            // Tạo đối tượng Voucher mới
            Voucher voucher = new Voucher();
            voucher.setCode(code);
            voucher.setDescription(description);
            voucher.setDiscount_percent(discountPercent);
            voucher.setDiscount_amount(discountAmount);
            voucher.setMin_price(minPrice);
            voucher.setTotal_quantity(totalQuantity);
            voucher.setUsed_quantity(0);
            voucher.setValid_from(fromDate);
            voucher.setValid_to(toDate);
            voucher.setBranchId(branchId);
            voucher.setStatus(status != null && !status.isEmpty() ? status : "Active");
            voucher.setIs_deleted(false);
            boolean check = v.addVoucher(voucher);
            if (check) {
                session.setAttribute("success", "update success");
               
            } else {
                session.setAttribute("error", "update false");
                
            }
            session.setAttribute("returnPage", "vouchers");
            response.sendRedirect("vouchers");

        } else {
            request.setAttribute("error", "don't see user");
            response.sendRedirect("login");
        }
    }

}
