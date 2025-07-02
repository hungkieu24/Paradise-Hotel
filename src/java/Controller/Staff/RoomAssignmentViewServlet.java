package Controller.Staff;

import Dal.RoomAssignmentDAO;
import Model.UserAccount;
import Model.RoomAssignmentView;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@WebServlet(name = "RoomAssignmentViewServlet", urlPatterns = {"/staff-room-assignments-view"})
public class RoomAssignmentViewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        UserAccount user = (UserAccount) session.getAttribute("user");
        
        // Only Staff and Manager can access this view
        if (user == null || !("Staff".equals(user.getRole()) || "Manager".equals(user.getRole()))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        // Staff can only see their own branch
        if (user.getBranchId() == null || user.getBranchId() <= 0) {
            request.setAttribute("errorMessage", "You are not assigned to any branch. Please contact administrator.");
            request.getRequestDispatcher("/staff-room-assignments-view.jsp").forward(request, response);
            return;
        }
        
        try {
            // Get filter parameters (simplified for staff view)
            String statusFilter = request.getParameter("status");
            String dateFilter = request.getParameter("date");
            String searchQuery = request.getParameter("search");
            
            // Pagination parameters
            int page = 1;
            int pageSize = 20;
            try {
                if (request.getParameter("page") != null) {
                    page = Integer.parseInt(request.getParameter("page"));
                }
                if (request.getParameter("pageSize") != null) {
                    pageSize = Integer.parseInt(request.getParameter("pageSize"));
                }
            } catch (NumberFormatException e) {
                page = 1;
                pageSize = 20;
            }
            
            RoomAssignmentDAO roomAssignmentDAO = new RoomAssignmentDAO();
            
            // Get filtered room assignments for current user's branch only
            Map<String, Object> filters = new HashMap<>();
            filters.put("status", statusFilter);
            filters.put("date", dateFilter);
            filters.put("search", searchQuery);
            filters.put("branchId", user.getBranchId()); // Fixed to current user's branch
            
            List<RoomAssignmentView> roomAssignments = roomAssignmentDAO.getRoomAssignmentsByBranch(
                user.getBranchId(), filters, page, pageSize);
            int totalCount = roomAssignmentDAO.getRoomAssignmentCountByBranch(user.getBranchId(), filters);
            int totalPages = (int) Math.ceil((double) totalCount / pageSize);
            
            // Get summary statistics for current branch only
            Map<String, Integer> statistics = roomAssignmentDAO.getRoomAssignmentStatistics(user.getBranchId());
            
            // Get current branch info
            String branchName = roomAssignmentDAO.getBranchName(user.getBranchId());
            
            // Set attributes
            request.setAttribute("roomAssignments", roomAssignments);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("totalCount", totalCount);
            request.setAttribute("statistics", statistics);
            request.setAttribute("branchName", branchName);
            request.setAttribute("userBranchId", user.getBranchId());
            
            // Set filter values for form
            request.setAttribute("statusFilter", statusFilter);
            request.setAttribute("dateFilter", dateFilter);
            request.setAttribute("searchQuery", searchQuery);
            
            request.getRequestDispatcher("/staff-room-assignments-view.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while loading room assignments: " + e.getMessage());
            request.getRequestDispatcher("/staff-room-assignments-view.jsp").forward(request, response);
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
        
        try {
            switch (action) {
                case "refresh":
                    // Just redirect back to GET
                    response.sendRedirect(request.getContextPath() + "/staff-room-assignments-view");
                    break;
                default:
                    doGet(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/staff-room-assignments-view?error=operation_failed");
        }
    }
}