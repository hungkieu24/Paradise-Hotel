/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Customer;

import Dal.BookingDAO;
import Model.Booking;
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
@WebServlet(name = "MyBookingServlet", urlPatterns = {"/myBooking"})
public class MyBookingServlet extends HttpServlet {

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
        List<Booking> allBookings = bookingDao.getBookingsByUserId11(user.getId());
        int totalBookings = allBookings.size();
        int totalPages = (int) Math.ceil((double) totalBookings / pageSize);

        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalBookings);
        List<Booking> bookings = allBookings.subList(startIndex, endIndex);

        request.setAttribute("bookings", bookings);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        request.setAttribute("bookings", bookings);
        request.getRequestDispatcher("myBooking.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Xử lý huỷ booking
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        if ("cancel".equals(action)) {
            int bookingId = Integer.parseInt(request.getParameter("bookingId"));
            String cancelReason = request.getParameter("cancelReason");

            BookingDAO bd = new BookingDAO();
            boolean success = bd.cancelBooking(bookingId, cancelReason);

            if (success) {
                request.setAttribute("message", "Booking cancelled successfully.");
            } else {
                request.setAttribute("error", "Reservation cannot be canceled. Please try again.");
            }
        }

        // ----- Phân trang sau khi xử lý -----
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
        List<Booking> allBookings = bookingDao.getBookingsByUserId11(user.getId());
        int totalBookings = allBookings.size();
        int totalPages = (int) Math.ceil((double) totalBookings / pageSize);

        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalBookings);
        List<Booking> bookings = allBookings.subList(startIndex, endIndex);

        request.setAttribute("bookings", bookings);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        request.getRequestDispatcher("myBooking.jsp").forward(request, response);
    }

}
