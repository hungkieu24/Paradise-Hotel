/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Customer;

import Dal.BookingDAO;
import Dal.FeedbackDAO;
import Dal.LoyaltyPointDAO;
import Dal.UserAccountDAO;
import Model.Booking;
import Model.LoyaltyPoint;
import Model.UserAccount;
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
 * @author KTC
 */
@WebServlet(name = "BookingHistoryServlet", urlPatterns = {"/bookingHistory"})
public class BookingHistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int page = 1;
        int pageSize = 3;
        if (request.getParameter("page") != null) {
            try {
                page = Integer.parseInt(request.getParameter("page"));
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        BookingDAO bookingDao = new BookingDAO();
        List<Booking> allBookings = bookingDao.getBookingsByUserId1(user.getId());
        int totalBookings = allBookings.size();
        int totalPages = (int) Math.ceil((double) totalBookings / pageSize);

        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalBookings);
        List<Booking> bookings = allBookings.subList(startIndex, endIndex);

        request.setAttribute("bookings", bookings);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        LoyaltyPointDAO loyaltypointdao = new LoyaltyPointDAO();
        LoyaltyPoint loyaltypointlp = loyaltypointdao.getLoyaltyPointByUserId(user.getId());
        session.setAttribute("loyaltypointlp", loyaltypointlp);

        request.getRequestDispatcher("bookingHistory.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

}
