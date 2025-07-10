/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Manager;

import Dal.AmenityDAO;
import Dal.HotelBranchDAO;
import Model.Amenity;
import Model.HotelBranch;
import Model.UserAccount;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 *
 * @author hungk
 */
@WebServlet(name = "AmenityServlet", urlPatterns = {"/manager/amenity"})
public class AmenityServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");

        if (user != null) {
            String managerId = user.getId();
            HotelBranchDAO branchDAO = new HotelBranchDAO();

            HotelBranch branch = branchDAO.getBranchByManagerId(managerId);
            int branchId = branch.getId();

            AmenityDAO amenityDAO = new AmenityDAO();
            List<Amenity> allAmenities = amenityDAO.getAllAmenityByBranchId(branchId);
            int amenitySize = allAmenities.size();
            
            String action = request.getParameter("action");
            if (action != null && action.equals("search")) {
                String keyword = request.getParameter("searchKeyword");

                if (keyword != null) {
                    keyword = keyword.trim(); // Xóa dấu cách đầu và cuối
                    keyword = keyword.replaceAll("\\s+", " ");
                    allAmenities = amenityDAO.searchAmenity(keyword, branchId);
                }
                else {
                    allAmenities = amenityDAO.getAllAmenityByBranchId(branchId);
                }
            }
            
            request.setAttribute("allAmenities", allAmenities);
            request.setAttribute("amenitySize", amenitySize);
            request.setAttribute("branch", branch);
            request.getRequestDispatcher("./amenity.jsp").forward(request, response);
        } else {
            setSessionMessage(session, "You need to login!", "error");
            response.sendRedirect("../login");
        }
    }

    private void setSessionMessage(HttpSession session, String message, String type) {
        session.setAttribute("message", message);
        session.setAttribute("messageType", type);
    }
}
