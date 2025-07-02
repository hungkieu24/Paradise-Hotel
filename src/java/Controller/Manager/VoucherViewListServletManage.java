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
import java.util.List;

/**
 *
 * @author thien
 */
@WebServlet(name="VoucherViewListServletManage", urlPatterns={"/vouchers"})
public class VoucherViewListServletManage extends HttpServlet {
    private RoomDAO r = new RoomDAO();
    private VoucherDAO v = new VoucherDAO();
    
   
    

  
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        UserAccount user = (session != null) ? (UserAccount) session.getAttribute("user"):null;
        if(user != null){
            //lấy tên chi nhánh và tên manager
            String username = user.getUsername();
            String userId = user.getId();
            String branchName = r.getBranchNameById(userId);
            int branchId = r.getBranchId(userId);
            // Lấy tham số để tìm kiếm 
            String search = request.getParameter("search");
            String status = request.getParameter("status");
            String fromDateStr = request.getParameter("fromDate");
            String toDateStr = request.getParameter("toDate");
            // phân trang
            int page = 1;
            int pageSize = 5;
            String pageParam = request.getParameter("page");
            String sizeParam = request.getParameter("size");
            if (pageParam != null && !pageParam.isEmpty()) {
                page = Integer.parseInt(pageParam);
            }
            if (sizeParam != null && !sizeParam.isEmpty()) {
                pageSize = Integer.parseInt(sizeParam);
            }
            if (page < 1) {
                page = 1;
            }
            if (pageSize < 1) {
                pageSize = 5;
            }
            int totalVoucher = v.countSearchVoucher(branchId, search, status, fromDateStr, toDateStr);
            int totalPage = (int) Math.ceil((double) totalVoucher / pageSize);

            List<Voucher> vouchers = v.getSearchVoucherList(branchId, search, status, fromDateStr, toDateStr, page, pageSize);
            // set attribute
            request.setAttribute("branchId", branchId);
            request.setAttribute("username", username);
            request.setAttribute("branchname", branchName);
            request.setAttribute("currentPage", page);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("totalPages", totalPage);
            request.setAttribute("vouchers", vouchers);
            request.getRequestDispatcher("promotionManage.jsp").forward(request, response);

        }else{
            request.setAttribute("error", "don't see user");
            response.sendRedirect("login");
        }
    } 

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
     
    }

    

}
