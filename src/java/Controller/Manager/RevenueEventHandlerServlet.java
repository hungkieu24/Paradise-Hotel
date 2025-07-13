/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Manager;

import Dal.RevenueDAO;
import Model.Revenue;
import Model.UserAccount;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.Date;

/**
 *
 * @author hungk
 */
@WebServlet(name = "RevenueEventHandlerServlet", urlPatterns = {"/manager/revenueEventHandler"})
public class RevenueEventHandlerServlet extends HttpServlet {

    private final RevenueDAO revenueDAO = new RevenueDAO();
    private final String REVENUE_PAGE = "./revenue";
    private static final String COL_Revenue_Type = "revenue_type";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int revenueID = Integer.parseInt(request.getParameter("revenueID"));
        Revenue revenue = revenueDAO.getRevenueById(revenueID);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();

        response.getWriter().write(gson.toJson(revenue));
    }

    private void setSessionMessage(HttpSession session, String message, String type) {
        session.setAttribute("message", message);
        session.setAttribute("messageType", type);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        if (action != null) {
            if (action.equals("add") || action.equals("edit")) {
                String revenueType = request.getParameter("revenueType");
                String revenueDateStr = request.getParameter("revenueDate");
                String amountStr = request.getParameter("amount").replace(".", "");
                String description = request.getParameter("description");

                int branchID = Integer.parseInt(request.getParameter("branchID").trim());
                double amount = Double.parseDouble(amountStr);
                Date revenueDate = Date.valueOf(revenueDateStr);
                Revenue revenue = new Revenue(branchID, revenueType, amount, revenueDate, description);

                if (action.equals("add")) {
                    handleAddRevenue(request, response, session, revenue);
                    response.sendRedirect(REVENUE_PAGE);
                    return;
                }
                if (action.equals("edit")) {
                    int revenueID = Integer.parseInt(request.getParameter("revenueID").trim());
                    revenue.setId(revenueID);
                    handleEditRevenue(request, session, revenue);
                    response.sendRedirect(REVENUE_PAGE);
                    return;
                }
            }

            if (action.equals("delete")) {
                int revenueID = Integer.parseInt(request.getParameter("IdDelete").trim());
                boolean success = revenueDAO.deleteRevenueById(revenueID);
                setSessionMessage(session, success ? "Delete revenue successful!" : "Failure to delete revenue!",
                        success ? "success" : "error");
                response.sendRedirect(REVENUE_PAGE);
                return;
            }
        }
    }

    private void handleAddRevenue(HttpServletRequest request, HttpServletResponse response, HttpSession session, Revenue revenue) throws ServletException, IOException {
        boolean isExistType = revenueDAO.isFieldExists(COL_Revenue_Type, revenue.getRevenue_type(), null);
        if (isExistType) {
            setSessionMessage(session, "Revenue type already exists", "error");
            return;
        }
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null) {
            setSessionMessage(session, "You need to login!", "error");
            response.sendRedirect("../login.jsp");

        }
        revenue.setSource("MANUAL");
        revenue.setCreated_by(user.getId());

        boolean success = revenueDAO.insertRevenue(revenue);

        // Đặt thông báo session
        setSessionMessage(session, success ? "Add revenue successful!" : "Failure to add revenue!",
                success ? "success" : "error");
    }

    private void handleEditRevenue(HttpServletRequest request, HttpSession session, Revenue revenue) throws ServletException, IOException {
        boolean isExistType = revenueDAO.isFieldExists(COL_Revenue_Type, revenue.getRevenue_type(), revenue.getId());
        if (isExistType) {
            setSessionMessage(session, "Revenue type already exists", "error");
            return;
        }

        boolean success = revenueDAO.updateRevenue(revenue);

        // Đặt thông báo session
        setSessionMessage(session, success ? "Update revenue successful!" : "Failure to update revenue!",
                success ? "success" : "error");
    }
}
