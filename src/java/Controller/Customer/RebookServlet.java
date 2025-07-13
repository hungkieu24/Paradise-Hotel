/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Customer;

import Dal.BookingDAO;
import Dal.BookingRoomTypeDAO;
import Dal.BookingServiceDAO;
import Dal.LoyaltyPointDAO;
import Dal.RoomTypeDAO;
import Dal.ServiceDAO;
import Model.Booking;
import Model.BookingRoomType;
import Model.BookingService;
import Model.CartItem;
import Model.LoyaltyPoint;
import Model.Service;
import Model.UserAccount;
import Model.RoomType;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author KTC
 */
@WebServlet(name = "RebookServlet", urlPatterns = {"/rebook"})
public class RebookServlet extends HttpServlet {

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
        ServiceDAO serviceDAO = new ServiceDAO();
        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
        BookingDAO bookingDAO = new BookingDAO();
        BookingRoomTypeDAO roomDAO = new BookingRoomTypeDAO();
        BookingServiceDAO bookingServiceDAO = new BookingServiceDAO();

        Booking booking = bookingDAO.getBookingById1(bookingId);
        List<BookingRoomType> roomList = roomDAO.getBookingRoomTypesByBookingId(bookingId);
        List<BookingService> serviceList = bookingServiceDAO.getBookingServicesByBookingId(bookingId);

        // Phòng
        List<CartItem> listCartItem = new ArrayList<>();
        double totalRoom = 0;
        int totalQuantity = 0;
        for (BookingRoomType br : roomList) {
            RoomType rt = roomTypeDAO.getRoomTypeById(br.getRoomTypeId());
            listCartItem.add(new CartItem(rt, br.getQuantity()));
            totalRoom += br.getBase_price() * br.getQuantity(); // ✔ Đúng: cộng tổng giá theo số lượng
            totalQuantity += br.getQuantity();
        }

        // Dịch vụ theo branch
        List<Service> listServices = serviceDAO.getServicesByBranchId(booking.getBranchId());

        // Map các dịch vụ đã chọn để set số lượng mặc định (dùng sau)
        Map<Integer, Integer> selectedServiceMap = new HashMap<>();
        for (BookingService s : serviceList) {
            selectedServiceMap.put(s.getServiceId(), s.getQuantity());
        }

        // Set session
        session.setAttribute("listCartItem", listCartItem);
        session.setAttribute("totalRoom", totalRoom);
        session.setAttribute("totalRoomQuantity", totalQuantity);
        session.setAttribute("listServices", listServices);
        session.setAttribute("selectedServiceMap", selectedServiceMap);
        session.setAttribute("preNote", booking.getNote());

        // Lấy loyalty point
        LoyaltyPointDAO loyaltyDAO = new LoyaltyPointDAO();
        LoyaltyPoint point = loyaltyDAO.getLoyaltyPointByUserId(user.getId());
        session.setAttribute("loyaltyPoint", point);
        response.sendRedirect("booking?rebook=1");

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

}
