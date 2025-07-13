    package Controller.Manager;

import Dal.MembershipDAO;
import Dal.BranchDAO;
import Model.Branch;
import Model.UserAccount;
import Model.PointTransaction;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "ManagerMembershipServlet", urlPatterns = {"/manager-membership"})
public class ManagerMembershipServlet extends HttpServlet {

    private final MembershipDAO membershipDAO = new MembershipDAO();
    private final BranchDAO branchDAO = new BranchDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserAccount manager = (UserAccount) session.getAttribute("user");

        if (manager == null || !"Manager".equals(manager.getRole()) || manager.getBranchId() == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            setCommonAttributes(request);

            String action = request.getParameter("action");
            if ("view".equals(action)) {
                viewCustomerDetails(request, response);
            } else {
                // Mặc định hiển thị tất cả customer khi vào trang
                List<UserAccount> allCustomers = membershipDAO.getAllCustomers();
                request.setAttribute("allCustomers", allCustomers);
                request.getRequestDispatcher("/manage_membership_list.jsp").forward(request, response);
            }
        } catch (Exception e) {
            handleException(request, response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        UserAccount manager = (UserAccount) session.getAttribute("user");

        if (manager == null || !"Manager".equals(manager.getRole())) {
            session.setAttribute("error", "Your session has expired. Please log in again.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/manager-membership");
            return;
        }

        try {
            switch (action) {
                case "search":
                    searchCustomers(request, response, manager.getBranchId());
                    break;
                case "adjustPoints":
                    adjustPoints(request, response, manager.getId());
                    break;
                // ĐÃ BỎ: case "changeTier"
                default:
                    response.sendRedirect(request.getContextPath() + "/manager-membership");
            }
        } catch (Exception e) {
            session.setAttribute("error", "A critical error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/manager-membership");
            e.printStackTrace();
        }
    }

    /**
     * Search customers by name/email/phone and rank (tier)
     */
    private void searchCustomers(HttpServletRequest request, HttpServletResponse response, int branchId)
            throws ServletException, IOException, SQLException {
        String searchTerm = request.getParameter("searchTerm");
        String rankFilter = request.getParameter("rankFilter");

        List<UserAccount> searchResults;
        if ((searchTerm == null || searchTerm.trim().isEmpty()) &&
            (rankFilter == null || rankFilter.trim().isEmpty())) {
            // Nếu không nhập gì thì show all
            searchResults = membershipDAO.getAllCustomers();
        } else {
            List<UserAccount> baseResults = membershipDAO.searchCustomers(
                searchTerm != null ? searchTerm.trim() : "",
                branchId
            );
            if (rankFilter != null && !rankFilter.trim().isEmpty()) {
                searchResults = baseResults.stream()
                    .filter(c -> c.getLoyaltyPoint() != null && rankFilter.equalsIgnoreCase(c.getLoyaltyPoint().getLevel()))
                    .collect(Collectors.toList());
            } else {
                searchResults = baseResults;
            }
        }
        request.setAttribute("searchResults", searchResults);
        request.setAttribute("searchTerm", searchTerm);
        request.setAttribute("rankFilter", rankFilter);

        setCommonAttributes(request);
        request.getRequestDispatcher("/manage_membership_list.jsp").forward(request, response);
    }

    /**
     * Xem chi tiết khách hàng
     */
    private void viewCustomerDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        String userIdStr = request.getParameter("userId");

        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/manager-membership");
            return;
        }

        try {
            String userId = userIdStr.trim();

            membershipDAO.createLoyaltyPointIfNotExists(userId);

            UserAccount customer = membershipDAO.getCustomerById(userId);

            if (customer != null) {
                List<PointTransaction> pointHistory = membershipDAO.getPointHistory(userId);
                customer.setPointHistory(pointHistory);

                request.setAttribute("selectedCustomer", customer);
            } else {
                request.getSession().setAttribute("error", "Customer not found.");
            }

            setCommonAttributes(request);
            request.getRequestDispatcher("/customer_detail.jsp").forward(request, response);
        } catch (SQLException e) {
            request.getSession().setAttribute("error", "Error loading customer details: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/manager-membership");
        }
    }

    /**
     * Điều chỉnh điểm thưởng
     */
    private void adjustPoints(HttpServletRequest request, HttpServletResponse response, String managerId)
            throws IOException, SQLException {
        HttpSession session = request.getSession();
        String userId = request.getParameter("userId");
        String pointsStr = request.getParameter("points");
        String reason = request.getParameter("reason");

        if (userId == null || pointsStr == null || reason == null ||
            userId.trim().isEmpty() || pointsStr.trim().isEmpty() || reason.trim().isEmpty()) {
            session.setAttribute("error", "All fields are required.");
            response.sendRedirect(request.getContextPath() + "/manager-membership?action=view&userId=" + userId);
            return;
        }

        try {
            int points = Integer.parseInt(pointsStr.trim());
            boolean success = membershipDAO.adjustPoints(userId, points, reason.trim(), managerId);

            if (success) {
                session.setAttribute("success", "Points adjusted successfully.");
            } else {
                session.setAttribute("error", "Failed to adjust points.");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("error", "Invalid points value.");
        } catch (SQLException e) {
            session.setAttribute("error", "Database error: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/manager-membership?action=view&userId=" + userId);
    }

    // ĐÃ BỎ: Hàm changeTier và isValidTierLevel

    private void setCommonAttributes(HttpServletRequest request) throws SQLException {
        UserAccount manager = (UserAccount) request.getSession().getAttribute("user");
        if (manager != null) {
            String displayName = manager.getFullname();
            if (displayName == null || displayName.trim().isEmpty()) {
                displayName = manager.getUsername();
            }
            request.setAttribute("username", displayName);

            Branch currentBranch = branchDAO.getBranchById(manager.getBranchId());
            if (currentBranch != null) {
                request.setAttribute("branchname", currentBranch.getName());
            } else {
                request.setAttribute("branchname", "Unknown Branch");
            }
        }
    }

    private void handleException(HttpServletRequest request, HttpServletResponse response, Exception e)
            throws ServletException, IOException {
        e.printStackTrace();
        request.getSession().setAttribute("error", "An unexpected error occurred: " + e.getMessage());
        response.sendRedirect(request.getContextPath() + "/manager-membership");
    }
}