import Dal.BookingDAO;
import Dal.BookingRoomTypeDAO;
import Dal.BookingServiceDAO;
import Dal.RoomDAO;
import Dal.RoomTypeDAO;
import Dal.ServiceDAO;
import Model.Booking;
import Model.BookingRoomType;
import Model.BookingService;
import Model.Room;
import Model.RoomType;
import Model.Service;
import Model.UserAccount;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@WebServlet(name = "RoomAssignmentServlet", urlPatterns = {"/staff-room-assignment"})
public class RoomAssignmentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");
        
        if (user == null || !("Staff".equals(user.getRole()) || "Manager".equals(user.getRole()))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        
        switch (action) {
            case "list":
                listPendingBookings(request, response, user.getBranchId());
                break;
            case "assign":
                showAssignRoomPage(request, response, user.getBranchId());
                break;
            case "services":
                showAssignRoomPage(request, response, user.getBranchId());
                break;
            default:
                listPendingBookings(request, response, user.getBranchId());
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");
        
        if (user == null || !("Staff".equals(user.getRole()) || "Manager".equals(user.getRole()))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        String action = request.getParameter("action");
        
        switch (action) {
            case "assign-room":
                assignRoom(request, response);
                break;
            case "remove-assignment":
                removeRoomAssignment(request, response);
                break;
            case "complete-assignment":
                completeAssignment(request, response);
                break;
            case "add-service":
                addServiceToBooking(request, response);
                break;
            case "remove-service":
                removeServiceFromBooking(request, response);
                break;
            default:
                listPendingBookings(request, response, user.getBranchId());
                break;
        }
    }
    
    private void listPendingBookings(HttpServletRequest request, HttpServletResponse response, int branchId)
            throws ServletException, IOException {
        
        BookingDAO bookingDAO = new BookingDAO();
        List<Booking> pendingBookings = bookingDAO.getPendingBookingsForBranch(branchId);
        
        Map<Integer, Boolean> fullyAssignedMap = new HashMap<>();
        if (pendingBookings != null) {
            for (Booking booking : pendingBookings) {
                fullyAssignedMap.put(booking.getId(), bookingDAO.areAllRoomsAssigned(booking.getId()));
            }
        }
        
        request.setAttribute("pendingBookings", pendingBookings);
        request.setAttribute("fullyAssignedMap", fullyAssignedMap);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/staff-room-assignment-list.jsp");
        dispatcher.forward(request, response);
    }
    
    private void showAssignRoomPage(HttpServletRequest request, HttpServletResponse response, Integer branchID)
            throws ServletException, IOException {
        try {
            int bookingId = Integer.parseInt(request.getParameter("bookingId"));

            BookingDAO bookingDAO = new BookingDAO();
            Booking booking = bookingDAO.getBookingById(bookingId);

            if (booking == null) {
                response.sendRedirect(request.getContextPath() + "/staff-room-assignment?error=booking_not_found");
                return;
            }

            int branchId = booking.getBranchId();

            // Lấy các phòng đã được gán cho booking này
            RoomDAO roomDAO = new RoomDAO();
            List<Room> assignedRooms = roomDAO.getAssignedRoomsByBookingId(bookingId);
              Set<Integer> assignedRoomIds = assignedRooms.stream()
                                        .map(Room::getId)
                                        .collect(Collectors.toSet());

            // Lấy thông tin yêu cầu của booking
            BookingRoomTypeDAO brtDAO = new BookingRoomTypeDAO();
            List<BookingRoomType> bookingRoomTypes = brtDAO.getBookingRoomTypesByBookingId(bookingId);

            Map<Integer, Integer> requiredQuantities = new HashMap<>();
            for (BookingRoomType brt : bookingRoomTypes) {
                requiredQuantities.put(brt.getRoomTypeId(), brt.getQuantity());
            }

            // Đếm số lượng phòng đã gán cho mỗi loại
            Map<Integer, Integer> assignmentCounts = new HashMap<>();
            for (Room room : assignedRooms) {
                assignmentCounts.put(room.getRoomTypeId(), assignmentCounts.getOrDefault(room.getRoomTypeId(), 0) + 1);
            }

            // Lấy TẤT CẢ các loại phòng trong toàn bộ hệ thống
            RoomTypeDAO roomTypeDAO = new RoomTypeDAO(); 
            List<RoomType> allSystemRoomTypes = roomTypeDAO.getAllRoomType(); // Sử dụng findAll

            // Lấy các phòng có sẵn cho TỪNG loại phòng, NHƯNG chỉ ở chi nhánh hiện tại
            Map<Integer, List<Room>> availableRoomsByAllTypes = new HashMap<>();
            for (RoomType rt : allSystemRoomTypes) {
                // Quan trọng: Vẫn truyền branchId để đảm bảo chỉ lấy phòng ở đúng chi nhánh
                List<Room> available = roomDAO.getAvailableRoomsForAssignment(rt.getRoomTypeID(), branchId, bookingId);
                 // Gán flag assigned cho từng phòng
                for (Room room : available) {
                    room.setAssigned(assignedRoomIds.contains(room.getId()));
                }

                if (!available.isEmpty()) {
                    availableRoomsByAllTypes.put(rt.getRoomTypeID(), available);
                }
            }

            boolean isFullyAssigned = bookingDAO.areAllRoomsAssigned(bookingId);

            // Phần dịch vụ giữ nguyên
            BookingServiceDAO bookingServiceDAO = new BookingServiceDAO();
            List<BookingService> bookingServices = bookingServiceDAO.getBookingServicesByBookingId(bookingId);
            ServiceDAO serviceDAO = new ServiceDAO();
            List<Service> availableServices = serviceDAO.getActiveServicesByBranch(branchId);

            
          
            request.setAttribute("assignedRoomIds", assignedRoomIds);    
            request.setAttribute("booking", booking);
            request.setAttribute("assignedRooms", assignedRooms);
            request.setAttribute("allSystemRoomTypes", allSystemRoomTypes); // <-- Dữ liệu mới
            request.setAttribute("availableRoomsByAllTypes", availableRoomsByAllTypes);
            request.setAttribute("requiredQuantities", requiredQuantities);
            request.setAttribute("assignmentCounts", assignmentCounts);
            request.setAttribute("isFullyAssigned", isFullyAssigned);
            request.setAttribute("bookingServices", bookingServices);
            request.setAttribute("availableServices", availableServices);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/staff-assign-rooms.jsp");
            dispatcher.forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/staff-room-assignment?error=invalid_booking_id");
        }
    }
    
    private void assignRoom(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();

        int bookingId = Integer.parseInt(request.getParameter("bookingId"));
        int roomId = Integer.parseInt(request.getParameter("roomId"));
        
        BookingDAO bookingDAO = new BookingDAO();
        // Phương thức này bây giờ sẽ gán một phòng duy nhất
       boolean success = bookingDAO.assignRoomsToBooking(bookingId, new String[]{String.valueOf(roomId)});
        
        response.sendRedirect(request.getContextPath() + 
            "/staff-room-assignment?action=assign&bookingId=" + bookingId + 
            (success ? "" : "&error=failed"));
    }
    
    private void removeRoomAssignment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        int bookingId = Integer.parseInt(request.getParameter("bookingId"));
        int roomId = Integer.parseInt(request.getParameter("roomId"));
        
        RoomDAO roomDAO = new RoomDAO();
        boolean success = roomDAO.removeRoomAssignment(bookingId, roomId);
        
        response.sendRedirect(request.getContextPath() + 
            "/staff-room-assignment?action=assign&bookingId=" + bookingId + 
            (success ? "" : "&error=remove-failed"));
    }
    
    private void completeAssignment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        int bookingId = Integer.parseInt(request.getParameter("bookingId"));
        BookingDAO bookingDAO = new BookingDAO();
        
            Booking booking = bookingDAO.getBookingById(bookingId);
            
            if ("Pending".equalsIgnoreCase(booking.getStatus())) {
                bookingDAO.updateBookingStatus(bookingId, "CheckedIn");
            } else {
                 bookingDAO.updateBookingStatus(bookingId, "Assigned");
            }
            response.sendRedirect(request.getContextPath() + "/staff-bookings-list");
       
    }
    
    private void addServiceToBooking(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        int bookingId = Integer.parseInt(request.getParameter("bookingId"));
        int serviceId = Integer.parseInt(request.getParameter("serviceId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        String paidStatus = request.getParameter("paidStatus") != null ? request.getParameter("paidStatus") : "Unpaid";
        
        BookingService bookingService = new BookingService();
        bookingService.setBookingId(bookingId);
        bookingService.setServiceId(serviceId);
        bookingService.setQuantity(quantity);
        bookingService.setPaidStatus(paidStatus);
        
        BookingServiceDAO bookingServiceDAO = new BookingServiceDAO();
        boolean success = bookingServiceDAO.addOrUpdateServiceToBooking(bookingService);
        
        response.sendRedirect(request.getContextPath() + 
            "/staff-room-assignment?action=assign&bookingId=" + bookingId + 
            (success ? "&success=service-added" : "&error=service-add-failed"));
    }
    
    private void removeServiceFromBooking(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        int bookingId = Integer.parseInt(request.getParameter("bookingId"));
        int serviceId = Integer.parseInt(request.getParameter("serviceId"));
        
        BookingServiceDAO bookingServiceDAO = new BookingServiceDAO();
        boolean success = bookingServiceDAO.removeServiceFromBooking(bookingId, serviceId);
        
        response.sendRedirect(request.getContextPath() + 
            "/staff-room-assignment?action=assign&bookingId=" + bookingId + 
            (success ? "&success=service-removed" : "&error=service-remove-failed"));
    }
}


