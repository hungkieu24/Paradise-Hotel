/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller.Customer;

import Dal.BookingDAO;
import Dal.CartRoomTypeDAO;
import Dal.LoyaltyPointDAO;
import Dal.PointRedeemVoucherDAO;
import Dal.RoomTypeDAO;
import Dal.ServiceDAO;
import Dal.VoucherDAO;
import Dal.WalletDAO;
import Dal.WalletTransactionDAO;
import Model.Booking;
import Model.CartItem;
import Model.LoyaltyPoint;
import Model.RoomType;
import Model.Service;
import Model.UserAccount;
import Model.Voucher;
import Model.Wallet;
import Model.WalletTransaction;
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
import java.util.Date;
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

        // Load user's available vouchers
        PointRedeemVoucherDAO pointRedeemVoucherDAO = new PointRedeemVoucherDAO();
        List<Integer> availableVoucherIds = pointRedeemVoucherDAO.getAvailableVoucherIdsByUser(user.getId());

        // Debug: Print voucher info
        List<Integer> allRedeemedVouchers = pointRedeemVoucherDAO.getRedeemedVoucherIdsByUser(user.getId());

        VoucherDAO voucherDAO = new VoucherDAO();
        List<Voucher> availableVouchers = voucherDAO.getVouchersByIds(availableVoucherIds);

        for (Voucher v : availableVouchers) {
        }

        request.setAttribute("availableVouchers", availableVouchers);

        WalletDAO walletDAO = new WalletDAO();
        Wallet wallet = walletDAO.getWalletByUserId(user.getId());
        session.setAttribute("wallet", wallet);

        request.getRequestDispatcher("booking.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        String action = request.getParameter("action");
        String isRebook = request.getParameter("rebook");

        // ✅ Xử lý case rebook - kiểm tra cả action empty
//        if ("1".equals(isRebook) && ("book".equals(action) || action == null || action.trim().isEmpty())) {
//            handleRebookSubmission(request, response);
//            return;
//        }
        if ("1".equals(isRebook)) {
            handleRebookSubmission(request, response);
            return;
        }

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        UserAccount user = (UserAccount) request.getSession().getAttribute("user");
        if (user == null) {
            setSessionMessage(session, "You need to login!", "error");
            response.sendRedirect("./login.jsp");
            return;
        }

        BookingDAO bookingDAO = new BookingDAO();
        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();

        if (action == null) {
            setSessionMessage(session, "Action is required", "error");
            response.sendRedirect("./viewRoomTypeList");
            return;
        }

        // --- Parse các tham số ---
        String checkInStr = request.getParameter("checkIn");
        String checkOutStr = request.getParameter("checkOut");
        String totalPriceStr = request.getParameter("finalTotalPrice");
        String note = request.getParameter("note");
        String serviceIdList = request.getParameter("selectedServiceIds");
        String selectedVoucherId = request.getParameter("selectedVoucherId");

        if (checkInStr == null || checkOutStr == null || checkInStr.isEmpty() || checkOutStr.isEmpty()) {
            setSessionMessage(session, "Check-in and Check-out must not be empty", "error");
            response.sendRedirect("./viewRoomTypeList");
            return;
        }

        LocalDate checkInDate = LocalDate.parse(checkInStr);
        LocalDate checkOutDate = LocalDate.parse(checkOutStr);

// Convert to Timestamp at start of day
        Timestamp checkInTimestamp = Timestamp.valueOf(checkInDate.atStartOfDay());
        Timestamp checkOutTimestamp = Timestamp.valueOf(checkOutDate.atStartOfDay());

        Timestamp now = new Timestamp(System.currentTimeMillis());
        long durationMillis = checkOutTimestamp.getTime() - checkInTimestamp.getTime();
        if (checkInTimestamp.before(now) || checkOutTimestamp.before(now)) {
            setSessionMessage(session, "Dates must be in the future", "error");
            response.sendRedirect("./viewRoomTypeList");
            return;
        } else if (!checkOutTimestamp.after(checkInTimestamp)) {
            setSessionMessage(session, "Check-out must be after Check-in", "error");
            response.sendRedirect("./viewRoomTypeList");
            return;
        } else if (durationMillis < 3600 * 1000) {
            setSessionMessage(session, "Booking duration must be at least 1 hour", "error");
            response.sendRedirect("./viewRoomTypeList");
            return;
        } else if (durationMillis > (365L * 24 * 3600 * 1000)) {
            setSessionMessage(session, "Booking duration cannot exceed 1 year", "error");
            response.sendRedirect("./viewRoomTypeList");
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
                setSessionMessage(session, "Failed to create booking", "error");
                response.sendRedirect("./viewRoomTypeList");
                return;
            }

            allInserted = bookingDAO.insertBookingRoomType(bookingId, room.getRoomTypeID(), 1, room.getBase_price());
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

        if (action.equals("manyRoom")) {
            List<CartItem> cartItems = (List<CartItem>) request.getSession().getAttribute("listCartItem");

            branchId = cartItems.get(0).getRoomType().getBranchId();
            bookingId = bookingDAO.addBookingReturnId(user.getId(),
                    checkInTimestamp, checkOutTimestamp, "Pending", totalPrice,
                    "Unpaid", branchId, note, false);

            if (bookingId == null) {
                setSessionMessage(session, "Failed to create bookingy", "error");
                response.sendRedirect("./viewRoomTypeList");
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
            // Apply voucher if selected
            if (selectedVoucherId != null && !selectedVoucherId.trim().isEmpty()) {
                try {
                    int voucherId = Integer.parseInt(selectedVoucherId);
                    VoucherDAO voucherDAO = new VoucherDAO();
                    Voucher voucher = voucherDAO.getVoucherById(voucherId);

                    if (voucher != null && totalPrice < voucher.getMin_price()) {
                        setSessionMessage(session, "Order is not eligible to use this voucher", "error");
                        response.sendRedirect("./viewRoomTypeList");
                        return;
                    }

                    PointRedeemVoucherDAO pointRedeemVoucherDAO = new PointRedeemVoucherDAO();
                    pointRedeemVoucherDAO.useVoucher(user.getId(), voucherId, bookingId);
                } catch (NumberFormatException e) {
                    e.printStackTrace(); // Ghi log
                }
            }

            String bookingIdStr = request.getParameter("bookingId");
            String amountStr = request.getParameter("amountToPay");

            if (bookingIdStr == null || amountStr == null) {
                setSessionMessage(session, "Missing parameters", "error");
                response.sendRedirect("./viewRoomTypeList");
                return;
            }

            double amount = Double.parseDouble(amountStr);

            WalletDAO walletDAO = new WalletDAO();
            Wallet wallet = walletDAO.getWalletByUserId(user.getId());

            if (wallet == null) {
                setSessionMessage(session, "Wallet not found", "error");
                response.sendRedirect("./viewRoomTypeList");
                return;
            }

            if (wallet.getBalance() < amount) {
                setSessionMessage(session, "Insufficient wallet balance", "error");
                response.sendRedirect("./viewRoomTypeList");
                return;
            }

            // Deduct from wallet
            boolean walletUpdated = walletDAO.updateWalletBalance(user.getId(), -amount);

            if (walletUpdated) {
                // Update booking status
                bookingDAO.updateBookingStatus(bookingId, "Paid");
                bookingDAO.updateBookingPaymentStatus(bookingId, "Paid");
                bookingDAO.updateBookingServicePaidStatus(bookingId, "Paid");
                awardLoyaltyPointsForPayment(user.getId(), amount);

                // Create transaction record
                WalletTransactionDAO transactionDAO = new WalletTransactionDAO();
                WalletTransaction transaction = new WalletTransaction();
                transaction.setWalletID(wallet.getWalletID());
                transaction.setTransactionType("Payment");
                transaction.setAmount(amount);
                transaction.setDescription("Booking payment #" + bookingId);
                transaction.setStatus("Success");
                transaction.setBankAccountID(0);
                transaction.setCreatedAt(new Timestamp(new Date().getTime()));

                transactionDAO.addWalletTransaction(transaction);
                clearBookingSession(request.getSession());

                setSessionMessage(session, "Payment successful", "success");
                response.sendRedirect("./myBooking");
                return;
            } else {
                setSessionMessage(session, "Payment failed", "error");
                response.sendRedirect("./viewRoomTypeList");
                return;
            }

        } else {
            setSessionMessage(session, "Failed to save booking details", "error");
            response.sendRedirect("./viewRoomTypeList");
            return;
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

    private void handleRebookSubmission(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");

        // Lấy data từ session (đã set bởi RebookServlet)
        List<CartItem> listCartItem = (List<CartItem>) session.getAttribute("listCartItem");
        Map<Integer, Integer> selectedServiceMap = (Map<Integer, Integer>) session.getAttribute("selectedServiceMap");
        String note = (String) session.getAttribute("preNote");

        // ✅ Lấy thông tin giống doPost()
        String checkInStr = request.getParameter("checkIn");
        String checkOutStr = request.getParameter("checkOut");
        String totalPriceStr = request.getParameter("finalTotalPrice"); // Sử dụng finalTotalPrice như doPost()
        String selectedVoucherId = request.getParameter("selectedVoucherId");

        if (checkInStr == null || checkOutStr == null || checkInStr.isEmpty() || checkOutStr.isEmpty()) {
            setSessionMessage(session, "Check-in and Check-out must not be empty", "error");
            response.sendRedirect("./viewRoomTypeList");
            return;
        }

        LocalDate checkInDate = LocalDate.parse(checkInStr);
        LocalDate checkOutDate = LocalDate.parse(checkOutStr);

// Convert to Timestamp at start of day
        Timestamp checkInTimestamp = Timestamp.valueOf(checkInDate.atStartOfDay());
        Timestamp checkOutTimestamp = Timestamp.valueOf(checkOutDate.atStartOfDay());

        Timestamp now = new Timestamp(System.currentTimeMillis());
        long durationMillis = checkOutTimestamp.getTime() - checkInTimestamp.getTime();
        if (checkInTimestamp.before(now) || checkOutTimestamp.before(now)) {
            response.getWriter().write("{\"status\":\"error\", \"message\":\"Dates must be in the future\"}");
            setSessionMessage(session, "Dates must be in the future", "error");
            response.sendRedirect("./viewRoomTypeList");
            return;
        } else if (!checkOutTimestamp.after(checkInTimestamp)) {
            setSessionMessage(session, "Check-out must be after Check-in", "error");
            response.sendRedirect("./viewRoomTypeList");
            return;
        } else if (durationMillis < 3600 * 1000) {
            setSessionMessage(session, "Booking duration must be at least 1 hour", "error");
            response.sendRedirect("./viewRoomTypeList");
            return;
        } else if (durationMillis > (365L * 24 * 3600 * 1000)) {
            setSessionMessage(session, "Booking duration cannot exceed 1 year", "error");
            response.sendRedirect("./viewRoomTypeList");
            return;
        }
        double totalPrice = Double.parseDouble(totalPriceStr);

        BookingDAO bookingDAO = new BookingDAO();
        Integer bookingId = null;
        boolean allInserted = true;
        int branchId = listCartItem.get(0).getRoomType().getBranchId();

        // Tạo booking mới
        bookingId = bookingDAO.addBookingReturnId(user.getId(),
                checkInTimestamp, checkOutTimestamp, "Pending", totalPrice,
                "Unpaid", branchId, note, false);

        if (bookingId == null) {
            setSessionMessage(session, "Failed to create rebook", "error");
            response.sendRedirect("./viewRoomTypeList");
            return;
        }

        // Lưu room types
        for (CartItem item : listCartItem) {
            boolean inserted = bookingDAO.insertBookingRoomType(bookingId,
                    item.getRoomType().getRoomTypeID(), item.getQuantity(),
                    item.getRoomType().getBase_price());
            if (!inserted) {
                allInserted = false;
                break;
            }
        }

        // Lưu services
        if (allInserted && selectedServiceMap != null && !selectedServiceMap.isEmpty()) {
            for (Map.Entry<Integer, Integer> entry : selectedServiceMap.entrySet()) {
                boolean inserted = bookingDAO.insertBookingService(bookingId,
                        entry.getKey(), entry.getValue(), "Unpaid");
                if (!inserted) {
                    allInserted = false;
                    break;
                }
            }
        }

        if (allInserted) {
            // Apply voucher if selected
            if (selectedVoucherId != null && !selectedVoucherId.trim().isEmpty()) {
                try {
                    int voucherId = Integer.parseInt(selectedVoucherId);
                    VoucherDAO voucherDAO = new VoucherDAO();
                    Voucher voucher = voucherDAO.getVoucherById(voucherId);

                    if (voucher != null && totalPrice < voucher.getMin_price()) {
                        setSessionMessage(session, "Order is not eligible to use this voucher", "error");
                        response.sendRedirect("./viewRoomTypeList");
                        return;
                    }

                    PointRedeemVoucherDAO pointRedeemVoucherDAO = new PointRedeemVoucherDAO();
                    pointRedeemVoucherDAO.useVoucher(user.getId(), voucherId, bookingId);
                } catch (NumberFormatException e) {
                    e.printStackTrace(); // Ghi log
                }
            }

            String bookingIdStr = request.getParameter("bookingId");
            String amountStr = request.getParameter("amountToPay");

            if (bookingIdStr == null || amountStr == null) {
                setSessionMessage(session, "Missing parameters", "error");
                response.sendRedirect("./viewRoomTypeList");
                return;
            }

            double amount = Double.parseDouble(amountStr);

            WalletDAO walletDAO = new WalletDAO();
            Wallet wallet = walletDAO.getWalletByUserId(user.getId());

            if (wallet == null) {
                setSessionMessage(session, "Wallet not found", "error");
                response.sendRedirect("./viewRoomTypeList");
                return;
            }

            if (wallet.getBalance() < amount) {
                setSessionMessage(session, "Insufficient wallet balance", "error");
                response.sendRedirect("./viewRoomTypeList");
                return;
            }

            // Deduct from wallet
            boolean walletUpdated = walletDAO.updateWalletBalance(user.getId(), -amount);

            if (walletUpdated) {
                // Update booking status
                bookingDAO.updateBookingStatus(bookingId, "Paid");
                bookingDAO.updateBookingPaymentStatus(bookingId, "Paid");
                bookingDAO.updateBookingServicePaidStatus(bookingId, "Paid");
                awardLoyaltyPointsForPayment(user.getId(), amount);

                // Create transaction record
                WalletTransactionDAO transactionDAO = new WalletTransactionDAO();
                WalletTransaction transaction = new WalletTransaction();
                transaction.setWalletID(wallet.getWalletID());
                transaction.setTransactionType("Payment");
                transaction.setAmount(amount);
                transaction.setDescription("Booking payment #" + bookingId);
                transaction.setStatus("Success");
                transaction.setBankAccountID(0);
                transaction.setCreatedAt(new Timestamp(new Date().getTime()));

                transactionDAO.addWalletTransaction(transaction);
                clearBookingSession(request.getSession());

                setSessionMessage(session, "Payment successful", "success");
                response.sendRedirect("./myBooking");
                return;
            } else {
                setSessionMessage(session, "Payment failed", "error");
                response.sendRedirect("./viewRoomTypeList");
                return;
            }

        } else {
            setSessionMessage(session, "Failed to save booking details", "error");
            response.sendRedirect("./viewRoomTypeList");
            return;
        }
    }

    private void awardLoyaltyPointsForPayment(String userId, double amount) {
        try {
            LoyaltyPointDAO loyaltyPointDAO = new LoyaltyPointDAO();
            int pointsToAward = (int) (amount / 100000); // 100,000 VND = 1 point

            if (pointsToAward > 0) {
                // Sử dụng method mới để cập nhật cả points và total_spending
                boolean success = loyaltyPointDAO.addPointsWithSpending(userId, pointsToAward,
                        "Online booking payment reward", amount);

                if (success) {
                    loyaltyPointDAO.checkAndUpdateTier(userId);
                } else {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSessionMessage(HttpSession session, String message, String type) {
        session.setAttribute("message", message);
        session.setAttribute("messageType", type);
    }
}
