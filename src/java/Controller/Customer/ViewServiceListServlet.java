/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Customer;

import Dal.ServiceDAO;
import Model.Branch;
import Model.Service;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 * @author KTC
 */
@WebServlet(name = "ViewServiceListServlet", urlPatterns = {"/viewServiceList"})
public class ViewServiceListServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServiceDAO serviceDAO = new ServiceDAO();
        List<Service> listService = serviceDAO.getAllServices1();

        List<Branch> listBranch = serviceDAO.getAllBranches();
        request.setAttribute("listBranch", listBranch);

        String branchIdParam = request.getParameter("branchId");
        String minPriceStr = request.getParameter("minPrice");
        String maxPriceStr = request.getParameter("maxPrice");
        String keyword = request.getParameter("keyword");

        ///////////////////////////////////////// loc theo chi nhanh
        if (branchIdParam != null && !branchIdParam.isEmpty() && !branchIdParam.equals("0")) {
            try {
                int branchId = Integer.parseInt(branchIdParam);
                listService = listService.stream()
                        .filter(s -> s.getBranchId() == branchId)
                        .toList();
            } catch (NumberFormatException e) {
                // Bỏ qua nếu lỗi
            }
        }

        ///////////////////////////////////////// loc theo key
        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            listService = listService.stream()
                    .filter(s -> s.getName().toLowerCase().contains(lowerKeyword)
                    || s.getDescription().toLowerCase().contains(lowerKeyword))
                    .toList();
        }

        ///////////////////////////////////////// loc theo gia
        try {
            if (minPriceStr != null && !minPriceStr.isEmpty()
                    && maxPriceStr != null && !maxPriceStr.isEmpty()) {

                double minPrice = Double.parseDouble(minPriceStr);
                double maxPrice = Double.parseDouble(maxPriceStr);

                listService = listService.stream()
                        .filter(s -> s.getPrice() >= minPrice && s.getPrice() <= maxPrice)
                        .toList();
            }
        } catch (NumberFormatException e) {
            // Bỏ qua nếu lỗi
        }

        request.setAttribute("listService", listService);
        request.setAttribute("listBranch", listBranch);
        request.setAttribute("selectedBranchId", branchIdParam);
        request.setAttribute("keyword", keyword);
        request.getRequestDispatcher("./viewServiceList.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

}
