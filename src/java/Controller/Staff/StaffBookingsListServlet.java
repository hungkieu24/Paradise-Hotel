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
    
    public static final String BRANCH_NAME = "branchName";
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 15;
    
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

        // Lấy filter parameters
        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status");
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");
        
        // Lấy pagination parameters
        int currentPage = 1;
        int pageSize = DEFAULT_PAGE_SIZE;
        
        // Parse current page
        try {
            String pageParam = request.getParameter("page");
            if (pageParam != null && !pageParam.trim().isEmpty()) {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) currentPage = 1;
            }
        } catch (NumberFormatException e) {
            currentPage = 1;
        }
        
        // Parse page size - only allow 5, 10, 15
        try {
            String pageSizeParam = request.getParameter("pageSize");
            if (pageSizeParam != null && !pageSizeParam.trim().isEmpty()) {
                int requestedSize = Integer.parseInt(pageSizeParam);
                if (requestedSize == 5 || requestedSize == 10 || requestedSize == 15) {
                    pageSize = requestedSize;
                } else {
                    pageSize = DEFAULT_PAGE_SIZE;
                }
            }
        } catch (NumberFormatException e) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        
        BookingDAO bookingDAO = new BookingDAO();
        
        try {
            // Lấy total count trước
            int totalBookings = bookingDAO.countBookingsByBranchWithFilter(
                branchId, keyword, status, fromDate, toDate
            );
            
            // Calculate total pages
            int totalPages = (int) Math.ceil((double) totalBookings / pageSize);
            if (totalPages == 0) totalPages = 1;
            
            // Validate current page
            if (currentPage > totalPages) currentPage = totalPages;
            
            // Lấy bookings với pagination
            List<Booking> bookings = bookingDAO.searchBookingsByBranchWithFilterPaginated(
                branchId, keyword, status, fromDate, toDate, currentPage, pageSize
            );
            
            // Set pagination attributes
            request.setAttribute("bookings", bookings);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPage", totalPages);
            request.setAttribute("totalBookings", totalBookings);
            request.setAttribute("pageSize", pageSize);
            
            // Set navigation flags
            request.setAttribute("hasNext", currentPage < totalPages);
            request.setAttribute("hasPrevious", currentPage > 1);
            
        } catch (Exception e) {
            request.setAttribute("errorMessage", "An error occurred while loading bookings: " + e.getMessage());
            
            // Set empty data on error
            request.setAttribute("bookings", List.of());
            request.setAttribute("currentPage", 1);
            request.setAttribute("totalPage", 1);
            request.setAttribute("totalBookings", 0);
            request.setAttribute("pageSize", pageSize);
        }
        
        // Trả filter parameters lại cho view
        request.setAttribute("keyword", keyword != null ? keyword : "");
        request.setAttribute("status", status != null ? status : "");
        request.setAttribute("fromDate", fromDate != null ? fromDate : "");
        request.setAttribute("toDate", toDate != null ? toDate : "");

        // Flash messages
        Object checkinMsg = session.getAttribute("checkinMessage");
        if (checkinMsg != null) { 
            request.setAttribute("checkinMessage", checkinMsg); 
            session.removeAttribute("checkinMessage"); 
        }
        Object checkoutMsg = session.getAttribute("checkoutMessage");
        if (checkoutMsg != null) { 
            request.setAttribute("checkoutMessage", checkoutMsg); 
            session.removeAttribute("checkoutMessage"); 
        }
        Object errorMsg = session.getAttribute("errorMessage");
        if (errorMsg != null) { 
            request.setAttribute("errorMessage", errorMsg); 
            session.removeAttribute("errorMessage"); 
        }

        request.getRequestDispatcher("staff-bookings-list.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}