/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.HotelOwner;

import Dal.InitialInvestmentDAO;
import Model.InitialInvestment;
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
@WebServlet(name = "InvestmentEventHandlerServlet", urlPatterns = {"/hotelOwner/investmentEventHandler"})
public class InvestmentEventHandlerServlet extends HttpServlet {

    private final InitialInvestmentDAO investmentDAO = new InitialInvestmentDAO();

    private final String Investment_PAGE = "./investment";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int investmentID = Integer.parseInt(request.getParameter("investmentID"));
        InitialInvestment investment = investmentDAO.getInitialInvestmentById(investmentID);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
        response.getWriter().write(gson.toJson(investment));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        if (action != null) {
            if (action.equals("add") || action.equals("edit")) {
                String investedDateStr = request.getParameter("investedDate");
                String capitalStr = request.getParameter("capital").replace(".", "");
                String branchIDStr = request.getParameter("branchID");
                double capital = Double.parseDouble(capitalStr);
                Date investedDate = Date.valueOf(investedDateStr);
                int branchID = Integer.parseInt(branchIDStr);
                InitialInvestment investment = new InitialInvestment(branchID, capital, investedDate);

                if (action.equals("add")) {
                    handleAddInvestment(request, response, session, investment);
                    response.sendRedirect(Investment_PAGE);
                    return;
                }
                if (action.equals("edit")) {
                    handleEditInvestment(request, response, session, investment);
                    response.sendRedirect(Investment_PAGE);
                    return;
                }
            }

            if (action.equals("delete")) {
                int investmentID = Integer.parseInt(request.getParameter("IdDelete").trim());
                boolean success = investmentDAO.deleteInitialInvestment(investmentID);
                setSessionMessage(session, success ? "Delete revenue successful!" : "Failure to delete revenue!",
                        success ? "success" : "error");
                response.sendRedirect(Investment_PAGE);
                return;
            }

        }
    }

    private void setSessionMessage(HttpSession session, String message, String type) {
        session.setAttribute("message", message);
        session.setAttribute("messageType", type);
    }

    private void handleAddInvestment(HttpServletRequest request, HttpServletResponse response, HttpSession session, InitialInvestment investment) throws ServletException, IOException {
        boolean success = investmentDAO.insertInitialInvestment(investment);

        // Đặt thông báo session
        setSessionMessage(session, success ? "Add investment successful!" : "Failure to add investment!",
                success ? "success" : "error");
    }

    private void handleEditInvestment(HttpServletRequest request, HttpServletResponse response, HttpSession session, InitialInvestment investment) throws ServletException, IOException {
        int investmentID = Integer.parseInt(request.getParameter("investmentID").trim());
        investment.setId(investmentID);
        boolean success = investmentDAO.updateInitialInvestment(investment);

        // Đặt thông báo session
        setSessionMessage(session, success ? "Update investment successful!" : "Failure to Update investment!",
                success ? "success" : "error");
    }

}
