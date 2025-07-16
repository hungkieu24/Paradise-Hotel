package Controller.Staff;

import Dal.BookingDAO;
import Dal.BookingServiceDAO;
import Dal.RoomAssignmentDAO;
import Dal.UserAccountDAO;
import Dal.RoomDAO;
import Dal.RoomTypeDAO;
import Dal.ServiceDAO;
import Model.Booking;
import Model.BookingService;
import Model.UserAccount;
import Model.RoomType;
import Model.Room;
import Model.Service;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import DBcontext.DBContext;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@WebServlet(name="SearchGuestServlet", urlPatterns={"/searchGuest"})
public class SearchGuestServlet extends HttpServlet {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\-.]+@[a-zA-Z0-9\\-.]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(0\\d{9,10}|\\+84\\d{9,10})$");
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Lấy staff từ session để lấy branch
        UserAccount staff = (UserAccount) request.getSession().getAttribute("user");
        Integer branchId = (staff != null) ? staff.getBranchId() : null;
        System.out.println(branchId);
        
        // Load room types và rooms cho branch
        RoomDAO roomDAO = new RoomDAO();
        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
        
        List<RoomType> allSystemRoomTypes = roomTypeDAO.getAllRoomType();
        Map<Integer, List<Room>> availableRoomsByAllTypes = new HashMap<>();
        
        ServiceDAO serviceDAO = new ServiceDAO();
        List<Service> availableServices = serviceDAO.getActiveServicesByBranch(branchId);
        
        if (branchId != null) {
            for (RoomType rt : allSystemRoomTypes) {
                List<Room> available = roomDAO.getAvailableRoomsByBranchAndRoomType(branchId, rt.getRoomTypeID());
                if (!available.isEmpty()) {
                    availableRoomsByAllTypes.put(rt.getRoomTypeID(), available);
                }
            }
        }
        
        // Xử lý tìm kiếm nếu có parameters
        String keyword = request.getParameter("keyword");
        String fullName = request.getParameter("fullName");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String idNumber = request.getParameter("idNumber");
        String status = request.getParameter("status");
        
        List<UserAccount> searchResults = new ArrayList<>();
        String errorMsg = null;
        
        // Kiểm tra nếu có bất kỳ search parameter nào
        boolean hasSearchParams = (keyword != null && !keyword.trim().isEmpty()) ||
                                 (fullName != null && !fullName.trim().isEmpty()) ||
                                 (username != null && !username.trim().isEmpty()) ||
                                 (email != null && !email.trim().isEmpty()) ||
                                 (phone != null && !phone.trim().isEmpty()) ||
                                 (idNumber != null && !idNumber.trim().isEmpty()) ||
                                 (status != null && !status.trim().isEmpty());
        
        if (hasSearchParams) {
            UserAccountDAO userDao = new UserAccountDAO();
            
            // Tìm kiếm cơ bản với keyword
            if (keyword != null && !keyword.trim().isEmpty()) {
                keyword = keyword.trim();
                boolean isEmail = EMAIL_PATTERN.matcher(keyword).matches();
                boolean isPhone = PHONE_PATTERN.matcher(keyword).matches();
                
                if (!isEmail && !isPhone) {
                    errorMsg = "Invalid email or phone number format.";
                } else {
                    UserAccount guest = userDao.getUserByEmailOrPhone(keyword);
                    if (guest != null) {
                        searchResults.add(guest);
                    }
                }
            }
            // Tìm kiếm nâng cao
            else {
                searchResults = userDao.searchUserAccounts(keyword, 0, 10);
            }
            if (searchResults.isEmpty() && errorMsg == null) {
                errorMsg = "No users found matching your search criteria.";
            }
        }
        // Set attributes
        request.setAttribute("availableServices", availableServices);
        request.setAttribute("roomTypes", allSystemRoomTypes);
        request.setAttribute("availableRoomsByAllTypes", availableRoomsByAllTypes);
        request.setAttribute("searchResults", searchResults);
        request.setAttribute("errorMsg", errorMsg);
        request.getRequestDispatcher("searchGuest.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("create-user".equals(action)) {
            handleCreateUser(request, response);
        } else if ("create-booking".equals(action)) {
            handleCreateBooking(request, response);
        } else {
            response.sendRedirect("searchGuest");
        }
    }
    
    private void handleCreateUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String fullName = request.getParameter("fullName");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String assignRoomAfterCreation = request.getParameter("assignRoomAfterCreation");
        
        if (fullName == null || fullName.trim().isEmpty() ||
            username == null || username.trim().isEmpty() ||
            email == null || email.trim().isEmpty()
            ) {
            
            response.sendRedirect("searchGuest?error=missing-required-fields");
            return;
        }
        assignRoomAfterCreation = "on";
        // Create new user
        UserAccount newUser = new UserAccount();
        newUser.setFullname(fullName.trim());
        newUser.setUsername(username.trim());
        newUser.setPassword(password);
        newUser.setEmail(email.trim());
        newUser.setAvatar_url("");
        newUser.setStatus("Active");
        newUser.setRole("Customer");
        newUser.setCreate_at(LocalDateTime.now().toString());
        
        UserAccountDAO userDao = new UserAccountDAO();
        boolean created = userDao.insertUser(newUser);
        if (created) {
            if ("on".equals(assignRoomAfterCreation)) {
                UserAccount createdUser = userDao.getUserByUserName(username);
                // Redirect to booking creation with user info
                response.sendRedirect("searchGuest?success=user-created&userId=" + createdUser.getId() + "&assignRoom=true");
            } else {
                response.sendRedirect("searchGuest?success=user-created");
            }
        } else {
            response.sendRedirect("searchGuest?error=user-creation-failed");
        }
    }
    
private void handleCreateBooking(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    String userIdStr = request.getParameter("userId");
    String checkIn = request.getParameter("checkIn");
    String checkOut = request.getParameter("checkOut");
    String[] roomIds = request.getParameterValues("roomIds");
    String note = request.getParameter("specialRequests");

    // Kiểm tra serviceCount
    int serviceCount = 0;
    String serviceCountStr = request.getParameter("serviceCount");
    if (serviceCountStr != null && !serviceCountStr.trim().isEmpty()) {
        try {
            serviceCount = Integer.parseInt(serviceCountStr);
        } catch (NumberFormatException ex) {
            serviceCount = 0;
        }
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    if (userIdStr == null || checkIn == null || checkOut == null || roomIds == null || roomIds.length == 0) {
        response.sendRedirect("searchGuest?error=missing-booking-info");
        return;
    }

    UserAccount staff = (UserAccount) request.getSession().getAttribute("user");
    Integer branchId = (staff != null) ? staff.getBranchId() : null;
    LocalDateTime checkInLdt = LocalDateTime.parse(checkIn, formatter);
    LocalDateTime checkOutLdt = LocalDateTime.parse(checkOut, formatter);

    Timestamp checkInTimestamp = Timestamp.valueOf(checkInLdt);
    Timestamp checkOutTimestamp = Timestamp.valueOf(checkOutLdt);

    // Tính số đêm
    long numberOfNights = calculateNumberOfNights(checkInLdt, checkOutLdt);

    // Khởi tạo DAO
    RoomDAO roomDAO = new RoomDAO();
    RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
    ServiceDAO serviceDAO = new ServiceDAO();

    // Tính giá phòng trước - sử dụng List<Room>
    double totalRoomPrice = 0;
    List<Room> selectedRooms = new ArrayList<>();
    
    for (String roomIdStr : roomIds) {
        try {
            if (roomIdStr != null && !roomIdStr.trim().isEmpty()) {
                int roomId = Integer.parseInt(roomIdStr);
                Room room = roomDAO.getRoomById(roomId);
                RoomType roomType = roomTypeDAO.getRoomTypeById(room.getRoomTypeId());
                
                // Set roomType vào room để sử dụng sau
                room.setRoomType(roomType);
                
                double roomPrice = roomType.getBase_price() * numberOfNights;
                totalRoomPrice += roomPrice;
                
                selectedRooms.add(room);
            }
        } catch (NumberFormatException ex) {
            System.err.println("Invalid room ID: " + roomIdStr);
        }
    }

    // Tính giá dịch vụ trước - sử dụng List<Service>
    double totalServicePrice = 0;
    List<Service> selectedServices = new ArrayList<>();
    
    for (int i = 0; i < serviceCount; i++) {
        String serviceIdParam = request.getParameter("serviceId" + i);
        String quantityParam = request.getParameter("quantity" + i);
        String paymentStatus = request.getParameter("paymentStatus" + i);
        
        if (serviceIdParam != null && !serviceIdParam.trim().isEmpty() &&
            quantityParam != null && !quantityParam.trim().isEmpty() &&
            paymentStatus != null && !paymentStatus.trim().isEmpty()) {
            try {
                int serviceId = Integer.parseInt(serviceIdParam);
                int quantity = Integer.parseInt(quantityParam);
                Service service = serviceDAO.getServiceById(serviceId);
                
                // Set thông tin booking vào service
                service.setQuantity(quantity);
                service.setPaidStatus(paymentStatus);
                
                double servicePrice = service.getPrice() * quantity;
                totalServicePrice += servicePrice;
                
                selectedServices.add(service);
            } catch (NumberFormatException ex) {
                System.err.println("Invalid serviceId or quantity: " + serviceIdParam + ", " + quantityParam);
            }
        }
    }

    // Tính tổng giá cuối cùng
    double finalTotalPrice = totalRoomPrice + totalServicePrice;

    // Tạo booking với giá đúng
    BookingDAO bookingDAO = new BookingDAO();
    int id = bookingDAO.addBooking2(userIdStr, checkInTimestamp, checkOutTimestamp, 
            "CheckedIn", finalTotalPrice, "Unpaid", branchId, note, false);
    
    // Xử lý room assignments
    RoomAssignmentDAO roomAssignmentDAO = new RoomAssignmentDAO();
    
    for (Room room : selectedRooms) {
        try {
            roomDAO.updateRoomStatus(room.getId(), "Occupied");
            roomAssignmentDAO.createRoomAssignment(id, room.getId());
            
            double roomTotalPrice = room.getRoomType().getBase_price() * numberOfNights;
            roomAssignmentDAO.insertBookingRoomType(id, room.getRoomTypeId(), 
                                                  (int)numberOfNights, roomTotalPrice);
        } catch (Exception ex) {
            System.err.println("Error processing room " + room.getId() + ": " + ex.getMessage());
        }
    }

    // Xử lý services
    BookingServiceDAO bookingServiceDAO = new BookingServiceDAO();
    
    for (Service service : selectedServices) {
        try {
            BookingService bookingService = new BookingService();
            bookingService.setServiceId(service.getId());
            bookingService.setQuantity(service.getQuantity());
            bookingService.setPaidStatus(service.getPaidStatus());
            bookingService.setBookingId(id);
            
            bookingServiceDAO.addOrUpdateServiceToBooking(bookingService);
        } catch (Exception ex) {
            System.err.println("Error processing service " + service.getId() + ": " + ex.getMessage());
        }
    }
    
    response.sendRedirect("searchGuest?success=booking-created");
}

// Phương thức tính số đêm
private long calculateNumberOfNights(LocalDateTime checkIn, LocalDateTime checkOut) {
    final int STANDARD_CHECKIN_HOUR = 7;  // 7:00 AM
    final int STANDARD_CHECKOUT_HOUR = 14; // 2:00 PM
    
    LocalDate checkInDate = checkIn.toLocalDate();
    LocalDate checkOutDate = checkOut.toLocalDate();
    
    long daysBetween = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    
    if (daysBetween == 0) {
        return 1;
    } else if (daysBetween == 1) {
        if (checkOut.getHour() < STANDARD_CHECKOUT_HOUR) {
            long totalHours = ChronoUnit.HOURS.between(checkIn, checkOut);
            if (totalHours < 12) {
                return 1;
            }
        }
        return 1;
    } else {
        return daysBetween;
    }
}
}