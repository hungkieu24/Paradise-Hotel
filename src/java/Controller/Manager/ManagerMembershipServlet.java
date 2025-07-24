package Controller.Manager;

import Dal.MembershipDAO;
import Dal.BranchDAO;
import Dal.HotelBranchDAO;
import Model.Branch;
import Model.HotelBranch;
import Model.UserAccount;
import Model.PointTransaction;
import Utility.EmailUtility;

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
    private final HotelBranchDAO branchDAO = new HotelBranchDAO();

    private static final int DEFAULT_ITEMS_PER_PAGE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserAccount manager = (UserAccount) session.getAttribute("user");

        if (manager == null) {
            response.sendRedirect("./login.jsp");
            return;
        }

        try {
            setCommonAttributes(request);

            String action = request.getParameter("action");
            if ("view".equals(action)) {
                viewCustomerDetails(request, response);
            } else {
                // Pagination params
                int currentPage = 1;
                int itemsPerPage = DEFAULT_ITEMS_PER_PAGE;
                try {
                    if (request.getParameter("page") != null) {
                        currentPage = Integer.parseInt(request.getParameter("page"));
                        if (currentPage < 1) currentPage = 1;
                    }
                    if (request.getParameter("itemsPerPage") != null) {
                        itemsPerPage = Integer.parseInt(request.getParameter("itemsPerPage"));
                        if (itemsPerPage <= 0) itemsPerPage = DEFAULT_ITEMS_PER_PAGE;
                    }
                } catch (NumberFormatException ignored) {}

                // Show all customers with pagination
                List<UserAccount> allCustomers = membershipDAO.getAllCustomers();
                int totalCustomer = allCustomers.size();
                int totalPages = (int) Math.ceil((double) totalCustomer / itemsPerPage);

                int fromIndex = (currentPage - 1) * itemsPerPage;
                int toIndex = Math.min(fromIndex + itemsPerPage, totalCustomer);

                List<UserAccount> pagedCustomers = (fromIndex < toIndex && fromIndex < allCustomers.size())
                        ? allCustomers.subList(fromIndex, toIndex)
                        : java.util.Collections.emptyList();

                request.setAttribute("allCustomers", pagedCustomers);
                request.setAttribute("currentPage", currentPage);
                request.setAttribute("itemsPerPage", itemsPerPage);
                request.setAttribute("totalPages", totalPages);
                request.setAttribute("totalCustomer", totalCustomer);

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
                    HotelBranch branch = branchDAO.getBranchByManagerId(manager.getId());
                    int branchId = branch.getId();
                    searchCustomers(request, response, branchId);
                    break;
                case "adjustPoints":
                    adjustPoints(request, response, manager.getId());
                    break;
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
     * Search customers by name/email/phone and rank (tier) with pagination
     */
    private void searchCustomers(HttpServletRequest request, HttpServletResponse response, int branchId)
            throws ServletException, IOException, SQLException {
        String searchTerm = request.getParameter("searchTerm");
        String rankFilter = request.getParameter("rankFilter");

        // Phân trang
        int currentPage = 1;
        int itemsPerPage = DEFAULT_ITEMS_PER_PAGE;
        try {
            if (request.getParameter("page") != null) {
                currentPage = Integer.parseInt(request.getParameter("page"));
                if (currentPage < 1) currentPage = 1;
            }
            if (request.getParameter("itemsPerPage") != null) {
                itemsPerPage = Integer.parseInt(request.getParameter("itemsPerPage"));
                if (itemsPerPage <= 0) itemsPerPage = DEFAULT_ITEMS_PER_PAGE;
            }
        } catch (NumberFormatException ignored) {}

        List<UserAccount> searchResults;
        if ((searchTerm == null || searchTerm.trim().isEmpty())
                && (rankFilter == null || rankFilter.trim().isEmpty())) {
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

        int totalCustomer = searchResults.size();
        int totalPages = (int) Math.ceil((double) totalCustomer / itemsPerPage);
        int fromIndex = (currentPage - 1) * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, totalCustomer);

        List<UserAccount> pagedResults = (fromIndex < toIndex && fromIndex < searchResults.size())
                ? searchResults.subList(fromIndex, toIndex)
                : java.util.Collections.emptyList();

        request.setAttribute("searchResults", pagedResults);
        request.setAttribute("searchTerm", searchTerm);
        request.setAttribute("rankFilter", rankFilter);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("itemsPerPage", itemsPerPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalCustomer", totalCustomer);

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
     * Điều chỉnh điểm cho khách hàng và gửi email thông báo.
     */
    private void adjustPoints(HttpServletRequest request, HttpServletResponse response, String managerId)
            throws IOException, SQLException {
        HttpSession session = request.getSession();
        String userId = request.getParameter("userId");
        String pointsStr = request.getParameter("points");
        String reason = request.getParameter("reason");

        if (userId == null || pointsStr == null || reason == null
                || userId.trim().isEmpty() || pointsStr.trim().isEmpty() || reason.trim().isEmpty()) {
            session.setAttribute("error", "All fields are required.");
            response.sendRedirect(request.getContextPath() + "/manager-membership?action=view&userId=" + userId);
            return;
        }

        try {
            int points = Integer.parseInt(pointsStr.trim());
            boolean success = membershipDAO.adjustPoints(userId, points, reason.trim(), managerId);

            if (success) {
                // Lấy lại thông tin khách hàng sau khi cộng/trừ điểm
                UserAccount customer = membershipDAO.getCustomerById(userId);
                int newTotalPoints = customer.getLoyaltyPoint() != null ? customer.getLoyaltyPoint().getPoints() : 0;
                String customerEmail = customer.getEmail();
                String customerName = customer.getFullname();

                // Gửi email thông báo
                try {
                    EmailUtility.sendPointAdjustmentEmail(
                        customerEmail,
                        customerName,
                        points,         // số điểm thay đổi (+/-)
                        reason.trim(),  // lý do điều chỉnh
                        newTotalPoints  // tổng điểm mới
                    );
                    session.setAttribute("success", "Points adjusted successfully and notification sent to customer.");
                } catch (Exception mailEx) {
                    mailEx.printStackTrace();
                    session.setAttribute("success", "Points adjusted successfully, but failed to send notification email.");
                }
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

    private void setCommonAttributes(HttpServletRequest request) throws SQLException {
        UserAccount manager = (UserAccount) request.getSession().getAttribute("user");
        if (manager != null) {
            String displayName = manager.getFullname();
            if (displayName == null || displayName.trim().isEmpty()) {
                displayName = manager.getUsername();
            }
            request.setAttribute("username", displayName);

            HotelBranch currentBranch = branchDAO.getBranchByManagerId(manager.getId());
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