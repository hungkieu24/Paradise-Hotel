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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
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
        System.out.println("Branch ID: " + branchId);

        // Lấy check-in và check-out dates từ parameters (chỉ ngày)
        String checkInStr = request.getParameter("checkInDate");
        String checkOutStr = request.getParameter("checkOutDate");
        Date checkInDate = null;
        Date checkOutDate = null;

        // Thay đổi format để chỉ parse ngày
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (checkInStr != null && !checkInStr.isEmpty()) {
                checkInDate = dateFormat.parse(checkInStr);
            }
            if (checkOutStr != null && !checkOutStr.isEmpty()) {
                checkOutDate = dateFormat.parse(checkOutStr);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("Error parsing dates: " + e.getMessage());
        }
        
        // Load room types và rooms cho branch
        RoomDAO roomDAO = new RoomDAO();
        RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
        
        List<RoomType> allSystemRoomTypes = roomTypeDAO.getAllRoomType();
        Map<Integer, List<Room>> availableRoomsByAllTypes = new HashMap<>();
        Map<Integer, Integer> bookedQuantitiesByRoomType = new HashMap<>();
        Map<Integer, Integer> totalRoomsByRoomType = new HashMap<>();
        Map<Integer, Integer> availableQuantitiesByRoomType = new HashMap<>();
        
        ServiceDAO serviceDAO = new ServiceDAO();
        List<Service> availableServices = serviceDAO.getActiveServicesByBranch(branchId);
        
        if (branchId != null) {
            for (RoomType rt : allSystemRoomTypes) {
                int roomTypeId = rt.getRoomTypeID();

                // Lấy tổng số phòng theo room type và branch
                int totalRooms = roomDAO.getTotalRoomsByRoomTypeAndBranch(branchId, roomTypeId);
                totalRoomsByRoomType.put(roomTypeId, totalRooms);

                if (totalRooms > 0) {
                    // Lấy số phòng đã book trong khoảng thời gian
                    int bookedQuantity = 0;
                    if (checkInDate != null && checkOutDate != null) {
                        bookedQuantity = roomDAO.getBookedQuantityByRoomTypeAndDateRange(
                            branchId, 
                            roomTypeId, 
                            new java.sql.Date(checkInDate.getTime()), 
                            new java.sql.Date(checkOutDate.getTime())
                        );
                    } else {
                        // Nếu không có dates thì lấy tất cả bookings đang active (CheckedIn)
                        bookedQuantity = roomDAO.getActiveBookedQuantityByRoomType(branchId, roomTypeId);
                    }
                    bookedQuantitiesByRoomType.put(roomTypeId, bookedQuantity);

                    // Tính số phòng available
                    int availableQuantity = totalRooms - bookedQuantity;
                    availableQuantitiesByRoomType.put(roomTypeId, Math.max(0, availableQuantity));

                    // Lấy danh sách phòng available cho việc assign rooms
                    List<Room> availableRooms;
                    if (checkInDate != null && checkOutDate != null) {
                        availableRooms = roomDAO.getAvailableRoomsByBranchRoomTypeAndDateRange(
                            branchId, 
                            roomTypeId, 
                            new java.sql.Date(checkInDate.getTime()), 
                            new java.sql.Date(checkOutDate.getTime())
                        );
                    } else {
                        availableRooms = roomDAO.getAvailableRoomsByBranchAndRoomType(branchId, roomTypeId);
                    }

                    // Chỉ add vào map nếu có rooms available hoặc để hiển thị thông tin
                    availableRoomsByAllTypes.put(roomTypeId, availableRooms);
                } else {
                    // Nếu không có phòng nào, set tất cả về 0
                    bookedQuantitiesByRoomType.put(roomTypeId, 0);
                    availableQuantitiesByRoomType.put(roomTypeId, 0);
                    availableRoomsByAllTypes.put(roomTypeId, new ArrayList<>());
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
        request.setAttribute("bookedQuantitiesByRoomType", bookedQuantitiesByRoomType);
        request.setAttribute("totalRoomsByRoomType", totalRoomsByRoomType);
        request.setAttribute("availableQuantitiesByRoomType", availableQuantitiesByRoomType);
        request.setAttribute("checkInDate", checkInStr);
        request.setAttribute("checkOutDate", checkOutStr);
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
    
   public void handleCreateBooking(HttpServletRequest request, HttpServletResponse response)
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

    // Thay đổi cách parse ngày tháng - chỉ parse ngày, sau đó thêm giờ default
    if (userIdStr == null || checkIn == null || checkOut == null || roomIds == null || roomIds.length == 0) {
        response.sendRedirect("searchGuest?error=missing-booking-info");
        return;
    }

    UserAccount staff = (UserAccount) request.getSession().getAttribute("user");
    Integer branchId = (staff != null) ? staff.getBranchId() : null;
    
    // Parse ngày và thêm giờ default
    LocalDate checkInDate = LocalDate.parse(checkIn);
    LocalDate checkOutDate = LocalDate.parse(checkOut);
    
    // Thêm giờ default: check-in lúc 07:00, check-out lúc 14:00
    LocalDateTime checkInLdt = checkInDate.atTime(7, 0); // 7:00 AM
    LocalDateTime checkOutLdt = checkOutDate.atTime(14, 0); // 2:00 PM

    Timestamp checkInTimestamp = Timestamp.valueOf(checkInLdt);
    Timestamp checkOutTimestamp = Timestamp.valueOf(checkOutLdt);

    // Tính số đêm - sử dụng ngày thay vì LocalDateTime
    long numberOfNights = calculateNumberOfNights(checkInDate, checkOutDate);

    // Khởi tạo DAO
    RoomDAO roomDAO = new RoomDAO();
    RoomTypeDAO roomTypeDAO = new RoomTypeDAO();
    ServiceDAO serviceDAO = new ServiceDAO();

    // Tính giá phòng trước - sử dụng List<Room>
    double totalRoomPrice = 0;
    List<Room> selectedRooms = new ArrayList<>();

    // Map để đếm số phòng từng loại
    Map<Integer, Integer> roomTypeCount = new HashMap<>();
    Map<Integer, RoomType> roomTypeMap = new HashMap<>();

    for (String roomIdStr : roomIds) {
        try {
            if (roomIdStr != null && !roomIdStr.trim().isEmpty()) {
                int roomId = Integer.parseInt(roomIdStr);
                Room room = roomDAO.getRoomById(roomId);
                RoomType roomType = roomTypeDAO.getRoomTypeById(room.getRoomTypeId());
                room.setRoomType(roomType);

                // Tính giá cho từng phòng
                double roomPrice = roomType.getBase_price() * numberOfNights;
                totalRoomPrice += roomPrice;
                selectedRooms.add(room);

                // Đếm số phòng từng loại
                int typeId = room.getRoomTypeId();
                roomTypeCount.put(typeId, roomTypeCount.getOrDefault(typeId, 0) + 1);
                roomTypeMap.put(typeId, roomType);
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

    // Xử lý room assignments: cập nhật trạng thái phòng và gán booking_id cho từng phòng
    RoomAssignmentDAO roomAssignmentDAO = new RoomAssignmentDAO();
    for (Room room : selectedRooms) {
        try {
            roomDAO.updateRoomStatus(room.getId(), "Occupied");
            roomAssignmentDAO.createRoomAssignment(id, room.getId());
        } catch (Exception ex) {
            System.err.println("Error processing room " + room.getId() + ": " + ex.getMessage());
        }
    }

    for (Map.Entry<Integer, Integer> entry : roomTypeCount.entrySet()) {
        int roomTypeId = entry.getKey();
        int quantity = entry.getValue(); 
        RoomType roomType = roomTypeMap.get(roomTypeId);
        double pricePerRoom = roomType.getBase_price();
        roomAssignmentDAO.insertBookingRoomType(id, roomTypeId, quantity, pricePerRoom);
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
    
    // Phương thức tính số đêm - cập nhật để sử dụng LocalDate
    private long calculateNumberOfNights(LocalDate checkInDate, LocalDate checkOutDate) {
        long daysBetween = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        
        // Nếu cùng ngày hoặc chỉ 1 ngày thì tính là 1 đêm
        if (daysBetween <= 0) {
            return 1;
        } else {
            return daysBetween;
        }
    }
}