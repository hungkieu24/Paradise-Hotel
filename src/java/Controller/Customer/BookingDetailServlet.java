/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Customer;

import Dal.BookingDAO;
import Dal.BookingRoomTypeDAO;
import Dal.BookingServiceDAO;
import Dal.LoyaltyPointDAO;
import Model.Booking;
import Model.BookingRoomType;
import Model.BookingService;
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
@WebServlet(name = "BookingDetailServlet", urlPatterns = {"/bookingDetail"})
public class BookingDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int bookingId = Integer.parseInt(request.getParameter("id"));

        BookingDAO bookingDAO = new BookingDAO();
        BookingRoomTypeDAO roomDAO = new BookingRoomTypeDAO();
        BookingServiceDAO serviceDAO = new BookingServiceDAO();

        Booking booking = bookingDAO.getBookingById1(bookingId);
        List<BookingRoomType> roomList = roomDAO.getBookingRoomTypesByBookingId(bookingId);
        List<BookingService> serviceList = serviceDAO.getBookingServicesByBookingId(bookingId);

        request.setAttribute("booking", booking);
        request.setAttribute("roomList", roomList);
        request.setAttribute("serviceList", serviceList);

        LoyaltyPointDAO loyaltyPointDAO = new LoyaltyPointDAO();
        LoyaltyPoint loyaltyPoint = loyaltyPointDAO.getLoyaltyPointByUserId(user.getId());
        session.setAttribute("loyaltyPoint", loyaltyPoint);

        // Forward sang trang popup (chỉ chứa phần modal)
        request.getRequestDispatcher("bookingDetailContent.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }
}
