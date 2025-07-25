/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package Controller;

import Dal.FeedbackDAO;
import Dal.HotelBranchDAO;
import Dal.RoomTypeDAO;
import Model.Feedback;
import Model.HotelBranch;
import Model.RoomType;
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
 * @author hungk
 */
@WebServlet(name="HomeServlet", urlPatterns={"/homepage"})
public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String action = request.getParameter("action");
        if(action != null ) {
            if(action.equals("logout")) {
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }
                response.sendRedirect("./homepage");
                return;
            }
        }
        
        
        RoomTypeDAO roomTypeDao = new RoomTypeDAO();
        FeedbackDAO feedbackDAO = new FeedbackDAO();
        HotelBranchDAO hotelBranch = new HotelBranchDAO();
        List<HotelBranch> branchList = hotelBranch.getAllHotelBranchesSimple();
        
        List<RoomType> roomTypeList = roomTypeDao.getAllRoomType();
        List<Feedback> feedbackList = feedbackDAO.getUniqueFiveStarFeedbacksFromCustomers();
        
        request.setAttribute("feedbackList", feedbackList);
        request.setAttribute("roomTypeList", roomTypeList);
        request.setAttribute("branchList", branchList);
        request.getRequestDispatcher("./homepage.jsp").forward(request, response);
    } 
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
