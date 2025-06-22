/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.admin;

import Dal.UserAccountDAO;
import Model.UserAccount;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 * @author hungk
 */
@WebServlet(name = "AccountServlet", urlPatterns = {"/admin/account"})
public class AccountServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserAccountDAO accountDAO = new UserAccountDAO();
        String action = request.getParameter("action");
        String keyword = request.getParameter("searchKeyword");
        String statusValue = request.getParameter("statusValue");

        int page = 1; // trang đầu tiên
        int pageSize = 5; // 1 trang có 5 row
        int totalPages = 0;
        int listSize = 0;

        if (request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }

        List<UserAccount> userAccountList = accountDAO.getAllUsersAccountByPage(page, pageSize);

        if (action != null && action.equals("search")) {

            if (keyword != null) {
                keyword = keyword.trim(); // Xóa dấu cách đầu và cuối
                keyword = keyword.replaceAll("\\s+", " ");
            }

            if (keyword.equalsIgnoreCase("all")) {
                List<UserAccount> listAll = accountDAO.getAllUsersAccount();
                listSize = listAll.size();
            } else {
                userAccountList = accountDAO.searchUserAccounts(keyword, page, pageSize);
                listSize = accountDAO.getTotalUserAccountAfterSearching(keyword);
            }
        } else if (action != null && action.equals("filerStatus")) {
            if (statusValue.equals("Deleted")) {
                userAccountList = accountDAO.getDeletedUserAccounts(page, pageSize);
                listSize = accountDAO.getTotalDeletedUserAccounts();
            } 
            else if (statusValue.equalsIgnoreCase("all")) {
                List<UserAccount> listAll = accountDAO.getAllUsersAccount();
                listSize = listAll.size();
            }
            else {
                userAccountList = accountDAO.getUserAccountsByStatus(statusValue,page, pageSize);
                listSize = accountDAO.getTotalUserAccountByStatus(statusValue);
            }
        } else {
            List<UserAccount> listAll = accountDAO.getAllUsersAccount();
            listSize = listAll.size();
        }
        
        totalPages = (int) Math.ceil((double) listSize / pageSize);
        List<String> roleList = accountDAO.getAllRoles();
        List<String> statusList = accountDAO.getAllStatuses();

        request.setAttribute("action", action);
        request.setAttribute("keyword", keyword);
        request.setAttribute("statusValue", statusValue);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("accountListSize", listSize);
        request.setAttribute("statusList", statusList);
        request.setAttribute("roleList", roleList);
        request.setAttribute("userAccountList", userAccountList);
        request.getRequestDispatcher("./account.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

}
