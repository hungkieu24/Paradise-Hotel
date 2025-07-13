/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Customer;

import Dal.BookingDAO;
import Dal.CartRoomTypeDAO;
import Dal.LoyaltyPointDAO;
import Dal.RoomTypeDAO;
import Dal.ServiceDAO;
import Model.Booking;
import Model.CartItem;
import Model.LoyaltyPoint;
import Model.RoomType;
import Model.Service;
import Model.UserAccount;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author KTC
 */
@WebServlet(name = "BookingServlet", urlPatterns = {"/booking"})
public class BookingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        ServiceDAO serviceDAO = new ServiceDAO();
        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
        CartRoomTypeDAO cartRoomTypeDAO = new CartRoomTypeDAO();

        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String singleIdStr = request.getParameter("roomTypeId");
        List<Service> listServices = new ArrayList<>();
        double totalRoom = 0;
        if (singleIdStr != null) {
            int roomTypeId = Integer.parseInt(singleIdStr);
            RoomType room = roomTypeDAO.getRoomTypeById(roomTypeId);
            listServices = serviceDAO.getServicesByBranchId(room.getBranchId());
            totalRoom = room.getBase_price();

            request.setAttribute("singleRoom", room);
            request.setAttribute("listServices", listServices);
            session.setAttribute("totalRoomQuantity", 1);
            request.setAttribute("totalRoom", totalRoom);
        } else {
            session.removeAttribute("singleRoom");

        }

        String selectedIds = request.getParameter("selectedRoomList");
        String strQuanlity = request.getParameter("quanlitySend");

        if (selectedIds != null && strQuanlity != null) {
            // Tách chuỗi "1,2,3" thành danh sách chuỗi
            List<RoomType> listRoomType = new ArrayList<>();

            List<Integer> listQuantity = new ArrayList<>();
            List<String> roomTypeIdList = new ArrayList<>();

            roomTypeIdList = Arrays.asList(selectedIds.split("\\s*,\\s*"));

            for (String strRoomTypeID : roomTypeIdList) {
                int roomTypeId = Integer.parseInt(strRoomTypeID);
                RoomType roomType = roomTypeDAO.getRoomTypeById(roomTypeId);
                listRoomType.add(roomType);
            }
            if (strQuanlity != null && !strQuanlity.isEmpty()) {
                String[] quantityArray = strQuanlity.split(",");

                for (String qtyStr : quantityArray) {
                    int qty = Integer.parseInt(qtyStr.trim());
                    listQuantity.add(qty);
                }
            }
            List<CartItem> listCartItem = new ArrayList<>();
            for (int i = 0; i < roomTypeIdList.size(); i++) {
                try {
                    RoomType roomtype = listRoomType.get(i);
                    int quantity = listQuantity.get(i);

                    CartItem item = new CartItem(roomtype, quantity);
                    listCartItem.add(item);
                } catch (NumberFormatException e) {
                    System.out.println("❌ Lỗi chuyển đổi ID hoặc quantity tại vị trí " + i);
                    e.printStackTrace();
                }
            }
            int totalQuantity = 0;
            for (int qty : listQuantity) {
                totalQuantity += qty;
            }
            session.setAttribute("totalRoomQuantity", totalQuantity);

            if (!listCartItem.isEmpty()) {
                int branchId = listCartItem.get(0).getRoomType().getBranchId();
                listServices = serviceDAO.getServicesByBranchId(branchId);
                session.setAttribute("listServices", listServices);
            }

            for (CartItem cartItem : listCartItem) {
                totalRoom += cartItem.getRoomType().getBase_price() * cartItem.getQuantity();
            }

            Map<Integer, Integer> selectedQuantityMap = new HashMap<>();
            for (CartItem item : listCartItem) {
                selectedQuantityMap.put(item.getRoomType().getRoomTypeID(), item.getQuantity());
            }
            request.setAttribute("selectedQuantityMap", selectedQuantityMap);

            session.setAttribute("totalRoom", totalRoom);
            session.setAttribute("listCartItem", listCartItem);
            session.setAttribute("services", listServices);
        } else {
            String isRebook = request.getParameter("rebook");

            if ((selectedIds == null || strQuanlity == null) && !"1".equals(isRebook)) {
                session.removeAttribute("listCartItem");
                session.removeAttribute("singleRoom");
                session.removeAttribute("listServices");
                session.removeAttribute("totalRoomQuantity");
                session.removeAttribute("totalRoom");
                session.removeAttribute("selectedServiceMap");
                session.removeAttribute("preNote");
            }
        }

        LoyaltyPointDAO loyaltyPointDAO = new LoyaltyPointDAO();
        LoyaltyPoint loyaltyPoint = loyaltyPointDAO.getLoyaltyPointByUserId(user.getId());
        session.setAttribute("loyaltyPoint", loyaltyPoint);

        request.getRequestDispatcher("booking.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        UserAccount user = (UserAccount) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        BookingDAO bookingDAO = new BookingDAO();

        //////////////////////////////////////////////////////////////// SERVICE
        String serviceIdList = request.getParameter("selectedServiceIds");
        String totalServiceCostStr = request.getParameter("totalServiceCost");
        double totalServiceCost = 0;

// Danh sách serviceId - quantity
        Map<Integer, Integer> serviceMap = new HashMap<>();

        if (serviceIdList != null && !serviceIdList.trim().isEmpty()) {
            String[] entries = serviceIdList.split(",");
            for (String entry : entries) {
                String[] parts = entry.trim().split(":");
                if (parts.length == 2) {
                    try {
                        int serviceId = Integer.parseInt(parts[0].trim());
                        int quantity = Integer.parseInt(parts[1].trim());
                        if (quantity < 0) {
                            quantity = 0;
                        }
                        serviceMap.put(serviceId, quantity);
                    } catch (NumberFormatException ex) {
                        response.getWriter().write("{\"status\":\"error\", \"message\":\"Invalid service ID or quantity\"}");
                        return;
                    }
                }
            }
        }

// Parse tổng tiền dịch vụ
        if (totalServiceCostStr != null && !totalServiceCostStr.isEmpty()) {
            try {
                totalServiceCost = Double.parseDouble(totalServiceCostStr);
            } catch (NumberFormatException e) {
                response.getWriter().write("{\"status\":\"error\", \"message\":\"Invalid total service cost\"}");
                return;
            }
        }

        ///////////////////////////////////////////////////// CHECKIN - CHECKOUT
        String checkInStr = request.getParameter("checkIn");
        String checkOutStr = request.getParameter("checkOut");
        Timestamp checkInTimestamp = null;
        Timestamp checkOutTimestamp = null;
        if (checkInStr == null || checkInStr.isEmpty() || checkOutStr == null || checkOutStr.isEmpty()) {
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Check-in and Check-out must not be empty\"}");
            return;
        }

        try {
            checkInTimestamp = Timestamp.valueOf(checkInStr.replace("T", " ") + ":00");
            checkOutTimestamp = Timestamp.valueOf(checkOutStr.replace("T", " ") + ":00");
        } catch (IllegalArgumentException e) {
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Invalid date format\"}");
            return;
        }

        Timestamp now = new Timestamp(System.currentTimeMillis());
        long durationMillis = checkOutTimestamp.getTime() - checkInTimestamp.getTime();

        if (checkInTimestamp.before(now) || checkOutTimestamp.before(now)) {
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Dates must be in the future\"}");
            return;
        } else if (!checkOutTimestamp.after(checkInTimestamp)) {
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Check-out must be after Check-in\"}");
            return;
        } else if (durationMillis < 3600 * 1000) {
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Booking duration must be at least 1 hour\"}");
            return;
        } else if (durationMillis > (365L * 24 * 3600 * 1000)) {
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Booking duration cannot exceed 1 year\"}");
            return;
        }

        //////////////////////////////////////////////////////////// TOTAL PRICE
        String totalPriceStr = request.getParameter("finalTotalPrice");
        double totalPrice = 0;
        if (totalPriceStr == null || totalPriceStr.isEmpty()) {
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Total price is required\"}");
            return;
        }

        try {
            totalPrice = Double.parseDouble(totalPriceStr);
        } catch (NumberFormatException e) {
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Invalid total price format\"}");
            return;
        }

        String note = request.getParameter("note");

        int branchId = 0;
        RoomType singleRoom = (RoomType) request.getSession().getAttribute("singleRoom");
        List<CartItem> cartItems = (List<CartItem>) request.getSession().getAttribute("listCartItem");

        if (singleRoom != null) {
            branchId = singleRoom.getBranchId();
        } else if (cartItems != null && !cartItems.isEmpty()) {
            branchId = cartItems.get(0).getRoomType().getBranchId();
        } else {
            response.getWriter().write("{\"status\":\"error\", \"message\":\"No room selected\"}");
            return;
        }

        Integer bookingId = bookingDAO.addBookingReturnId(user.getId(),
                checkInTimestamp, checkOutTimestamp, "Pending", totalPrice,
                "Unpaid", branchId, note, false);

        if (bookingId != null) {
            boolean allInserted = true;

            // Insert BookingRoomType
            if (singleRoom != null) {
                allInserted = bookingDAO.insertBookingRoomType(bookingId,
                        singleRoom.getRoomTypeID(), 1, singleRoom.getBase_price());
            } else if (cartItems != null) {
                for (CartItem item : cartItems) {
                    boolean inserted = bookingDAO.insertBookingRoomType(bookingId,
                            item.getRoomType().getRoomTypeID(), item.getQuantity(),
                            item.getRoomType().getBase_price());
                    if (!inserted) {
                        allInserted = false;
                        break;
                    }
                }
            }

            // Insert BookingService
            if (allInserted && !serviceMap.isEmpty()) {
                for (Map.Entry<Integer, Integer> entry : serviceMap.entrySet()) {
                    int serviceId = entry.getKey();
                    int quantity = entry.getValue();

                    boolean serviceInserted = bookingDAO.insertBookingService(bookingId, serviceId, quantity, "Unpaid");
                    if (!serviceInserted) {
                        allInserted = false;
                        break;
                    }
                }
            }

            if (allInserted) {
                HttpSession session = request.getSession();
                session.removeAttribute("listCartItem");
                session.removeAttribute("singleRoom");
                session.removeAttribute("listServices");
                session.removeAttribute("totalRoomQuantity");
                session.removeAttribute("totalRoom");
                session.removeAttribute("services");

                response.getWriter().write("{\"status\":\"success\"}");
            } else {
                response.getWriter().write("{\"status\":\"error\", \"message\":\"Failed to save booking details\"}");
            }

        } else {
            response.getWriter().write("{\"status\":\"fail\"}");
        }

    }

}
