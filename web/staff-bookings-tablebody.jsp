<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.Booking" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    // Lấy dữ liệu từ request (do servlet chuyển tiếp)
    List<Booking> bookings = (List<Booking>) request.getAttribute("bookings");
    String keyword = request.getAttribute("keyword") != null ? (String) request.getAttribute("keyword") : "";
    String fromDate = request.getAttribute("fromDate") != null ? (String) request.getAttribute("fromDate") : "";
    String toDate = request.getAttribute("toDate") != null ? (String) request.getAttribute("toDate") : "";
    String status = request.getAttribute("status") != null ? (String) request.getAttribute("status") : "";
    
    SimpleDateFormat sdfDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    java.util.Date now = new java.util.Date();
%>
<%-- Phần thân bảng này được thiết kế để tải bằng AJAX --%>
<%
    int displayCount = 0;
    if (bookings != null && !bookings.isEmpty()) {
        for (Booking b : bookings) {
            displayCount++;
            String bookingStatus = b.getStatus() != null ? b.getStatus().toLowerCase() : "";

            // --- Logic được đồng bộ hóa với staff-bookings-list.jsp ---
            // Logic cho phép Check-in
            boolean canCheckin = (
                "paid".equalsIgnoreCase(b.getStatus()) ||
                "pending".equalsIgnoreCase(b.getStatus())
            ) && b.getCheckIn() != null && !now.before(b.getCheckIn());

            // Logic cho trạng thái chờ Check-in
            boolean isWaitForCheckin = (
                "paid".equalsIgnoreCase(b.getStatus()) ||
                "pending".equalsIgnoreCase(b.getStatus())
            ) && b.getCheckIn() != null && now.before(b.getCheckIn());

            // Logic cho phép Check-out
            boolean canCheckout = "checkedin".equalsIgnoreCase(b.getStatus());
            
            // Logic cho phép Assign Room
            boolean canAssignRoom = (
                "paid".equalsIgnoreCase(b.getStatus()) ||
                "pending".equalsIgnoreCase(b.getStatus())
            );
            
            // Logic làm nổi bật hàng ưu tiên
            String rowClass = "";
            if (canCheckin) {
                rowClass = "priority-booking";
            } else if (isWaitForCheckin && b.getCheckIn() != null) {
                long timeDiff = b.getCheckIn().getTime() - now.getTime();
                long hoursDiff = timeDiff / (1000 * 60 * 60);
                if (hoursDiff < 24) {
                    rowClass = "priority-booking";
                }
            }
%>
<tr class="<%= rowClass %>">
    <td>
        <strong>#<%= b.getId() %></strong>
    </td>
    <td>
        <div class="d-flex align-items-center">
            <i class="bi bi-person-circle me-2"></i>
            <div class="booking-info">
                <div class="fw-bold">
                    <%= (b.getFullName() != null && !b.getFullName().isEmpty()) ? b.getFullName() : b.getUserName() %>
                </div>
                <small class="text-muted">ID: <%= b.getUserId() %></small>
            </div>
        </div>
    </td>
    <td>
        <span class="badge bg-secondary">
            <%= b.getRank() != null ? b.getRank() : "Member" %>
        </span>
    </td>
    <td>
        <div class="booking-info">
            <i class="bi bi-door-closed me-1"></i>
            <%= b.getRoomTypes() != null ? b.getRoomTypes() : "N/A" %>
        </div>
    </td>
    <td>
        <span class="badge bg-info text-dark">
            <%= b.getCheckIn() != null ? sdfDateTime.format(b.getCheckIn()) : "" %>
        </span>
    </td>
    <td>
        <span class="badge bg-info text-dark">
            <%= b.getCheckOut() != null ? sdfDateTime.format(b.getCheckOut()) : "" %>
        </span>
    </td>
    <td>
        <span class="badge bg-warning text-dark">
            <%= b.getTotalPrice() != 0 ? String.format("%,.0f", b.getTotalPrice()) : "0" %> đ
        </span>
    </td>
    <td>
        <span class="badge
              <% if(bookingStatus.contains("checkedin")) { %>bg-success
              <% } else if(bookingStatus.contains("checkedout")) { %>bg-secondary
              <% } else if(bookingStatus.contains("cancel")) { %>bg-danger
              <% } else if(bookingStatus.contains("paid")) { %>bg-primary
              <% } else if(bookingStatus.contains("pending")) { %>bg-warning text-dark
              <% } else if(bookingStatus.contains("completed")) { %>bg-success
              <% } else { %>bg-dark<% } %>">
            <%= b.getStatus() %>
        </span>
    </td>
    <td>
        <div class="action-buttons">
            <!-- Nút Assign Room -->
            <% if (canAssignRoom) { %>
                <a href="staff-assign-room?bookingId=<%= b.getId() %>" 
                   class="btn btn-outline-primary btn-sm" 
                   title="Assign rooms to this booking">
                    <i class="bi bi-house-door"></i> Assign Room
                </a>
            <% } %>
            
            <!-- Nút Check-in -->
            <% if (canCheckin) { %>
            <form method="post" action="staff-booking-action" style="display:inline;">
                <input type="hidden" name="action" value="checkin"/>
                <input type="hidden" name="bookingId" value="<%= b.getId() %>"/>
                <button class="btn btn-success btn-sm" type="submit" title="Check-in customer">
                    <i class="bi bi-person-check"></i> Check-in
                </button>
            </form>
            <% } else if (isWaitForCheckin) { %>
            <span class="text-muted small" title="Cannot check-in before scheduled time">
                <i class="bi bi-clock"></i> Wait
            </span>
            <% } %>
            
            <!-- Nút Check-out -->
            <% if (canCheckout) { %>
            <form method="post" action="staff-booking-action" style="display:inline;">
                <input type="hidden" name="action" value="checkout"/>
                <input type="hidden" name="bookingId" value="<%= b.getId() %>"/>
                <button class="btn btn-warning btn-sm" type="submit" title="Check-out customer">
                    <i class="bi bi-box-arrow-right"></i> Check-out
                </button>
            </form>
            <% } %>
            
            <!-- Nút View Customer -->
            <a href="view-user-info?userId=<%= b.getUserId() %>" 
               class="btn btn-info btn-sm" 
               title="View customer information">
                <i class="bi bi-eye"></i> View
            </a>
        </div>
    </td>
</tr>
<%
        }
    }
    if (displayCount == 0) {
%>
<tr>
    <td colspan="9" class="text-center text-muted py-5">
        <i class="bi bi-inbox display-1 text-muted"></i>
        <h5 class="mt-3">No bookings found</h5>
        <p class="mb-0">
            <% if((fromDate != null && !fromDate.isEmpty()) || (toDate != null && !toDate.isEmpty()) || (keyword != null && !keyword.isEmpty()) || (status != null && !status.isEmpty())) { %>
                Try adjusting your filters or clear all filters.
            <% } else { %>
                There are currently no bookings for your branch.
            <% } %>
        </p>
    </td>
</tr>
<% } %>