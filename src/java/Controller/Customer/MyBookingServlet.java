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
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

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

        String branchName = request.getParameter("branchName");
        String roomTypeName = request.getParameter("roomTypeName");
        String status = request.getParameter("status");

        List<Booking> filteredBookings = allBookings.stream()
                .filter(b -> branchName == null || branchName.isEmpty() || b.getBranchName().toLowerCase().contains(branchName.toLowerCase()))
                .filter(b -> roomTypeName == null || roomTypeName.isEmpty() || b.getRoomTypeName().toLowerCase().contains(roomTypeName.toLowerCase()))
                .filter(b -> status == null || status.isEmpty() || b.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
        Set<String> branchNames = allBookings.stream()
                .map(Booking::getBranchName)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(TreeSet::new));

        Set<String> roomTypeNames = allBookings.stream()
                .map(Booking::getRoomTypeName)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(TreeSet::new));

        int totalBookings = filteredBookings.size();
        int totalPages = (int) Math.ceil((double) totalBookings / pageSize);

        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalBookings);
        List<Booking> bookings = filteredBookings.subList(startIndex, endIndex);
        request.setAttribute("bookings", bookings);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("branchNames", branchNames);
        request.setAttribute("roomTypeNames", roomTypeNames);
        request.setAttribute("status", status);
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
                session.setAttribute("message", "Booking cancelled successfully.");
                session.setAttribute("messageType", "success");
            } else {
                session.setAttribute("message", "Reservation cannot be canceled. Please try again.");
                session.setAttribute("messageType", "error");
            }
        }

        if ("editSpecialRequest".equals(action)) {
            int bookingId = Integer.parseInt(request.getParameter("bookingId"));
            String specialRequest = request.getParameter("specialRequest");

            BookingDAO bd = new BookingDAO();
            boolean success = bd.updateSpecialRequest(bookingId, specialRequest);

            if (success) {
                session.setAttribute("message", "Special request updated successfully.");
                session.setAttribute("messageType", "success");
            } else {
                session.setAttribute("message", "Unable to update special request.");
                session.setAttribute("messageType", "error");
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
