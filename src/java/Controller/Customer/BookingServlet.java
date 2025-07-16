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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        UserAccount user = (UserAccount) request.getSession().getAttribute("user");
        if (user == null) {
            out.write("{\"status\":\"unauthenticated\", \"message\":\"Please login\"}");
            return;
        }

        BookingDAO bookingDAO = new BookingDAO();
        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();

        String action = request.getParameter("action");
        if (action == null) {
            out.write("{\"status\":\"error\", \"message\":\"Action is required\"}");
            return;
        }

        try {
            // --- Parse các tham số ---
            String checkInStr = request.getParameter("checkIn");
            String checkOutStr = request.getParameter("checkOut");
            String totalPriceStr = request.getParameter("finalTotalPrice");
            String note = request.getParameter("note");
            String serviceIdList = request.getParameter("selectedServiceIds");

            if (checkInStr == null || checkOutStr == null || checkInStr.isEmpty() || checkOutStr.isEmpty()) {
                out.write("{\"status\":\"error\", \"message\":\"Check-in and Check-out must not be empty\"}");
                return;
            }

            Timestamp checkInTimestamp = Timestamp.valueOf(checkInStr.replace("T", " ") + ":00");
            Timestamp checkOutTimestamp = Timestamp.valueOf(checkOutStr.replace("T", " ") + ":00");

            if (!checkOutTimestamp.after(checkInTimestamp)) {
                out.write("{\"status\":\"error\", \"message\":\"Check-out must be after Check-in\"}");
                return;
            }

            double totalPrice = Double.parseDouble(totalPriceStr);

            Map<Integer, Integer> serviceMap = new HashMap<>();
            if (serviceIdList != null && !serviceIdList.trim().isEmpty()) {
                String[] entries = serviceIdList.split(",");
                for (String entry : entries) {
                    String[] parts = entry.trim().split(":");
                    int serviceId = Integer.parseInt(parts[0].trim());
                    int quantity = Math.max(0, Integer.parseInt(parts[1].trim()));
                    serviceMap.put(serviceId, quantity);
                }
            }

            int branchId = 0; // chưa xử lý branchId cụ thể
            Integer bookingId = null; // Khai báo ở đây

            boolean allInserted = true;

            if (action.equals("oneRoom")) {
                int roomTypeId = Integer.parseInt(request.getParameter("roomTypeId"));
                RoomType room = roomTypeDAO.getRoomTypeById(roomTypeId);
                branchId = room.getBranchId();

                bookingId = bookingDAO.addBookingReturnId(user.getId(),
                        checkInTimestamp, checkOutTimestamp, "Pending", totalPrice,
                        "Unpaid", branchId, note, false);

                if (bookingId == null) {
                    out.write("{\"status\":\"error\", \"message\":\"Failed to create booking\"}");
                    return;
                }

                allInserted = bookingDAO.insertBookingRoomType(bookingId, room.getRoomTypeID(), 1, room.getBase_price());
            }

            if (action.equals("manyRoom")) {
                List<CartItem> cartItems = (List<CartItem>) request.getSession().getAttribute("listCartItem");

                branchId = cartItems.get(0).getRoomType().getBranchId();
                bookingId = bookingDAO.addBookingReturnId(user.getId(),
                        checkInTimestamp, checkOutTimestamp, "Pending", totalPrice,
                        "Unpaid", branchId, note, false);

                if (bookingId == null) {
                    out.write("{\"status\":\"error\", \"message\":\"Failed to create booking\"}");
                    return;
                }

                for (CartItem item : cartItems) {
                    boolean inserted = bookingDAO.insertBookingRoomType(bookingId,
                            item.getRoomType().getRoomTypeID(), item.getQuantity(),
                            item.getRoomType().getBase_price());
                    if (!inserted) {
                        allInserted = false;
                        break;
                    }
                }

                if (allInserted && !serviceMap.isEmpty()) {
                    for (Map.Entry<Integer, Integer> entry : serviceMap.entrySet()) {
                        boolean inserted = bookingDAO.insertBookingService(bookingId, entry.getKey(), entry.getValue(), "Unpaid");
                        if (!inserted) {
                            allInserted = false;
                            break;
                        }
                    }
                }
            }

            if (allInserted) {
                clearBookingSession(request.getSession());
                out.write("{\"status\":\"success\", \"bookingId\":" + bookingId + "}");
            } else {
                out.write("{\"status\":\"error\", \"message\":\"Failed to save booking details\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.write("{\"status\":\"error\", \"message\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }

// Hàm xóa session dùng chung
    private void clearBookingSession(HttpSession session) {
        session.removeAttribute("listCartItem");
        session.removeAttribute("listServices");
        session.removeAttribute("totalRoomQuantity");
        session.removeAttribute("totalRoom");
        session.removeAttribute("services");
    }

}
