package Controller.Staff;

import Dal.BookingDAO;
import Model.Booking;
import Model.UserAccount;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "StaffBookingsListServlet", urlPatterns = {"/staff-bookings-list"})
public class StaffBookingsListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        UserAccount staff = (UserAccount) session.getAttribute("user");
        Integer branchId = staff.getBranchId();


        if (session.getAttribute("branchName") == null && branchId != null) {
            Dal.HotelBranchDAO branchDAO = new Dal.HotelBranchDAO();
            String branchName = branchDAO.getBranchNameById(branchId);
            session.setAttribute("branchName", branchName);
        }
        request.setAttribute("branchName", session.getAttribute("branchName"));

        // Lấy filter từ request (search, status, từ ngày, đến ngày)
        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status");
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");

     
        BookingDAO bookingDAO = new BookingDAO();

        // Lấy toàn bộ bookings theo filter, KHÔNG phân trang
        List<Booking> bookings = bookingDAO.searchBookingsByBranchWithFilter(
                branchId, keyword, status, fromDate, toDate
        );

     
        // Trả filter lại cho view
        request.setAttribute("keyword", keyword != null ? keyword : "");
        request.setAttribute("status", status != null ? status : "");
        request.setAttribute("fromDate", fromDate != null ? fromDate : "");
        request.setAttribute("toDate", toDate != null ? toDate : "");

        // Flash message
        Object checkinMsg = session.getAttribute("checkinMessage");
        if (checkinMsg != null) { request.setAttribute("checkinMessage", checkinMsg); session.removeAttribute("checkinMessage"); }
        Object checkoutMsg = session.getAttribute("checkoutMessage");
        if (checkoutMsg != null) { request.setAttribute("checkoutMessage", checkoutMsg); session.removeAttribute("checkoutMessage"); }
        Object errorMsg = session.getAttribute("errorMessage");
        if (errorMsg != null) { request.setAttribute("errorMessage", errorMsg); session.removeAttribute("errorMessage"); }

        request.setAttribute("bookings", bookings);

        // AJAX support cho live search/filter
        if ("1".equals(request.getParameter("ajax"))) {
            request.getRequestDispatcher("staff-bookings-tablebody.jsp").forward(request, response);
            return;
        }

        request.getRequestDispatcher("staff-bookings-list.jsp").forward(request, response);
    }
}