<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.Booking" %>
<%@ page import="Model.UserAccount" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    List<Booking> bookings = (List<Booking>) request.getAttribute("bookings");
    String keyword = request.getAttribute("keyword") != null ? (String)request.getAttribute("keyword") : "";
    String fromDate = request.getAttribute("fromDate") != null ? (String)request.getAttribute("fromDate") : "";
    String toDate = request.getAttribute("toDate") != null ? (String)request.getAttribute("toDate") : "";
    String status = request.getAttribute("status") != null ? (String)request.getAttribute("status") : "";
    SimpleDateFormat sdfDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    int currentPage = request.getAttribute("currentPage") != null ? (Integer)request.getAttribute("currentPage") : 1;
    int totalPage = request.getAttribute("totalPage") != null ? (Integer)request.getAttribute("totalPage") : 1;
    int totalBookings = request.getAttribute("totalBookings") != null ? (Integer)request.getAttribute("totalBookings") : 0;
    int pageSize = request.getAttribute("pageSize") != null ? (Integer)request.getAttribute("pageSize") : 10;
   
    String staffName = "hieu1235";
    if (session.getAttribute("user") != null) {
        staffName = ((UserAccount)session.getAttribute("user")).getUsername();
    }
    java.util.Date now = new java.util.Date();
    
    // Messages from session
    String successMessage = (String) session.getAttribute("successMessage");
    String errorMessage = (String) session.getAttribute("errorMessage");
    
    // Clear messages after displaying
    if (successMessage != null) session.removeAttribute("successMessage");
    if (errorMessage != null) session.removeAttribute("errorMessage");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Booking List - Staff Panel</title>
        <link rel="stylesheet" href="css/style_1.css">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <style>
            .room-img {
                width: 80px;
                border-radius: 6px;
                box-shadow: 0 2px 6px rgba(0,0,0,.09);
            }
            .pagination {
                margin: 20px 0 0 0;
                display: flex;
                justify-content: center;
            }
            .pagination a, .pagination span {
                margin: 0 4px;
                padding: 6px 14px;
                border-radius: 5px;
                border: 1px solid #ddd;
                text-decoration: none;
            }
            .pagination .active, .pagination span[aria-current="page"] {
                background: #ffc107;
                color: #222;
                font-weight: bold;
                border-color: #ffc107;
            }
            .sidebar {
                height: 100vh;
                width: 250px;
                position: fixed;
                top: 0;
                left: 0;
                background: #22223b;
                color: #fff;
                z-index: 1000;
            }
            .sidebar-header {
                padding: 20px;
                font-size: 1.25rem;
                font-weight: bold;
            }
            .sidebar-menu {
                padding: 0 0 20px 0;
            }
            .sidebar-menu .menu-item {
                display: flex;
                align-items: center;
                gap: 10px;
                color: #fff;
                padding: 12px 20px;
                text-decoration: none;
                transition: background 0.2s;
            }
            .sidebar-menu .menu-item.active, .sidebar-menu .menu-item:hover {
                background: #4a4e69;
                border-left: 4px solid #ffc107;
            }
            .sidebar-menu .logout {
                color: #fa5252;
            }
            .main-content {
                margin-left: 220px;
                padding: 0;
                min-height: 100vh;
                background: #f4f5fa;
            }
            .content-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 28px 40px 0 40px;
            }
            .user-info {
                display: flex;
                align-items: center;
                gap: 7px;
                font-weight: 600;
            }
            .content-body {
                margin: 35px 40px 0 40px;
            }
            .form-container {
                max-width: 1200px;
                margin: 0 auto;
            }
            .action-buttons {
                display: flex;
                gap: 5px;
                flex-wrap: wrap;
            }
            .action-buttons .btn {
                white-space: nowrap;
            }
            .booking-info {
                max-width: 200px;
                overflow: hidden;
                text-overflow: ellipsis;
            }
            .priority-booking {
                background-color: #fff3cd !important;
                border-left: 4px solid #ffc107;
            }
            .overdue-booking {
                background-color: #f8d7da !important;
                border-left: 4px solid #dc3545;
            }
            .stats-card {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                border-radius: 10px;
                padding: 15px;
                margin-bottom: 20px;
            }
            
            /* Responsive CSS for filter section */
            @media (max-width: 992px) {
                .filter-section form {
                    flex-direction: column !important;
                    align-items: stretch !important;
                }
                
                .filter-section .d-flex[style*="gap"] {
                    justify-content: center;
                    margin-top: 0.5rem;
                }
                
                .filter-section .flex-fill {
                    min-width: 100% !important;
                    margin-bottom: 0.5rem;
                }
            }

            @media (max-width: 768px) {
                .filter-section {
                    padding: 12px 15px !important;
                }
                
                .filter-section .btn {
                    width: 100% !important;
                    margin-bottom: 0.25rem !important;
                }
            }
        </style>
    </head>
    <body class="bg-light">
        <div class="app-container">
            <%@ include file="sidebar.jsp" %>
            <main class="main-content">
                <header class="content-header">
                    <div class="header-left">
                        <h1 class="page-title">
                            <i class="bi bi-calendar-check"></i> Bookings Management
                        </h1>
                    </div>
                    <div class="header-right">
                        <div class="user-info">
                            <i class="fas fa-user-circle"></i>
                            <span><%= staffName %></span>
                        </div>
                    </div>
                </header>
                <div class="content-body">
                    <div class="form-container">
                        <!-- Success/Error Messages -->
                        <% if (successMessage != null && !successMessage.isEmpty()) { %>
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="bi bi-check-circle-fill me-2"></i>
                            <%= successMessage %>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <% } %>

                        <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="bi bi-exclamation-triangle-fill me-2"></i>
                            <%= errorMessage %>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <% } %>

                        <!-- Legacy Messages -->
                        <% if(request.getAttribute("error") != null) { %>
                        <div class="alert alert-danger alert-dismissible fade show">
                            <i class="bi bi-exclamation-triangle-fill me-2"></i>
                            <%= request.getAttribute("error") %>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <% } %>

                        <% if(request.getAttribute("checkinMessage") != null) { %>
                        <div class="alert alert-success alert-dismissible fade show">
                            <i class="bi bi-check-circle-fill me-2"></i>
                            <%= request.getAttribute("checkinMessage") %>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <% } %>

                        <% if(request.getAttribute("checkoutMessage") != null) { %>
                        <div class="alert alert-success alert-dismissible fade show">
                            <i class="bi bi-check-circle-fill me-2"></i>
                            <%= request.getAttribute("checkoutMessage") %>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <% } %>         

                        <!-- Statistics Cards -->
                        <div class="row mb-4">
                            <%
                                int displayedBookings = bookings != null ? bookings.size() : 0;
                                int paidBookings = 0, checkedInBookings = 0, pendingBookings = 0;
                                
                                if (bookings != null) {
                                    for (Booking b : bookings) {
                                        String bStatus = b.getStatus() != null ? b.getStatus().toLowerCase() : "";
                                        if (bStatus.contains("paid")) paidBookings++;
                                        else if (bStatus.contains("checkedin")) checkedInBookings++;
                                        else if (bStatus.contains("pending")) pendingBookings++;
                                    }
                                }
                            %>
                            <div class="col-md-3">
                                <div class="stats-card">
                                    <h4><%= totalBookings %></h4>
                                    <p class="mb-0"><i class="bi bi-list-ul"></i> Total Bookings</p>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="stats-card">
                                    <h4><%= paidBookings %></h4>
                                    <p class="mb-0"><i class="bi bi-credit-card"></i> Paid Bookings</p>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="stats-card">
                                    <h4><%= checkedInBookings %></h4>
                                    <p class="mb-0"><i class="bi bi-person-check"></i> Checked In</p>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="stats-card">
                                    <h4><%= pendingBookings %></h4>
                                    <p class="mb-0"><i class="bi bi-clock"></i> Pending</p>
                                </div>
                            </div>
                        </div>

                        <!-- Filter Section - Compact Layout with Inline Styles -->
                        <div class="filter-section" style="background: white; padding: 15px 20px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); margin-bottom: 20px; border: 1px solid #e9ecef;">
                            <form class="d-flex align-items-center gap-3 flex-wrap" id="filter-form" method="get" action="staff-bookings-list" style="gap: 1rem;">
                                <input type="hidden" name="pageSize" value="<%= pageSize %>">
                                
                                <!-- Search Customer -->
                                <div class="flex-fill" style="min-width: 200px;">
                                    <input class="form-control" type="search" name="keyword" 
                                           placeholder="🔍 Search customer..." value="<%= keyword %>"
                                           style="font-size: 0.875rem; padding: 0.375rem 0.75rem; border-radius: 4px;">
                                </div>
                                
                                <!-- Status -->
                                <div style="min-width: 140px;">
                                    <select class="form-select" name="status" style="font-size: 0.875rem; padding: 0.375rem 0.75rem; border-radius: 4px;">
                                        <option value="">📋 All Status</option>
                                        <option value="Pending" <%= "Pending".equalsIgnoreCase(status) ? "selected" : "" %>>⏳ Pending</option>
                                        <option value="Paid" <%= "Paid".equalsIgnoreCase(status) ? "selected" : "" %>>💳 Paid</option>
                                        <option value="CheckedIn" <%= "CheckedIn".equalsIgnoreCase(status) ? "selected" : "" %>>✅ CheckedIn</option>
                                        <option value="CheckedOut" <%= "CheckedOut".equalsIgnoreCase(status) ? "selected" : "" %>>🏃 CheckedOut</option>
                                        <option value="Completed" <%= "Completed".equalsIgnoreCase(status) ? "selected" : "" %>>🎉 Completed</option>
                                        <option value="Cancelled" <%= "Cancelled".equalsIgnoreCase(status) ? "selected" : "" %>>❌ Cancelled</option>
                                        <option value="NoShow" <%= "NoShow".equalsIgnoreCase(status) ? "selected" : "" %>>👻 NoShow</option>
                                    </select>
                                </div>
                                
                                <!-- From Date -->
                                <div style="min-width: 150px;">
                                    <input type="date" class="form-control" name="fromDate" 
                                           value="<%= fromDate %>" title="From Date"
                                           style="font-size: 0.875rem; padding: 0.375rem 0.75rem; border-radius: 4px;"/>
                                </div>
                                
                                <!-- To Date -->
                                <div style="min-width: 150px;">
                                    <input type="date" class="form-control" name="toDate" 
                                           value="<%= toDate %>" title="To Date"
                                           style="font-size: 0.875rem; padding: 0.375rem 0.75rem; border-radius: 4px;"/>
                                </div>
                                
                                <!-- Items per page -->
                                <div style="min-width: 80px;">
                                    <select class="form-select" id="pageSizeSelect" onchange="changePageSize(this.value)"
                                            style="font-size: 0.875rem; padding: 0.375rem 0.75rem; border-radius: 4px;">
                                        <option value="5" <%= pageSize == 5 ? "selected" : "" %>>5</option>
                                        <option value="10" <%= pageSize == 10 ? "selected" : "" %>>10</option>
                                        <option value="15" <%= pageSize == 15 ? "selected" : "" %>>15</option>
                                    </select>
                                </div>
                                
                                <!-- Action Buttons -->
                                <div class="d-flex" style="gap: 0.5rem;">
                                    <button type="submit" class="btn btn-primary" 
                                            style="padding: 0.375rem 0.75rem; font-size: 0.875rem; border-radius: 4px; font-weight: 500;">
                                        <i class="bi bi-search"></i> Search
                                    </button>
                                    <a href="staff-bookings-list" class="btn btn-outline-secondary" 
                                       style="padding: 0.375rem 0.75rem; font-size: 0.875rem; border-radius: 4px; font-weight: 500; text-decoration: none;">
                                        <i class="bi bi-arrow-clockwise"></i> Reset
                                    </a>
                                </div>
                            </form>
                        </div>

                        <!-- Bookings Table -->
                        <div class="card shadow-sm">
                            <div class="card-header bg-primary text-white">
                                <h5 class="mb-0">
                                    <i class="bi bi-table"></i> Bookings List
                                    <span class="badge bg-light text-primary ms-2"><%= displayedBookings %></span>
                                </h5>
                            </div>
                            <div class="card-body p-0">
                                <div class="table-responsive">
                                    <table class="table table-hover align-middle mb-0">
                                        <thead class="table-light">
                                            <tr>
                                                <th width="5%">ID</th>
                                                <th width="15%">Customer</th>
                                                <th width="8%">Rank</th>
                                                <th width="12%">Room Type</th>
                                                <th width="12%">Check-in</th>
                                                <th width="12%">Check-out</th>
                                                <th width="10%">Total Price</th>
                                                <th width="8%">Status</th>
                                                <th width="18%">Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody id="bookings-table-body">
                                            <%
                                                int displayCount = 0;
                                                if (bookings != null && !bookings.isEmpty()) {
                                                    for (Booking b : bookings) {
                                                        displayCount++;
                                                        String bookingStatus = b.getStatus() != null ? b.getStatus().toLowerCase() : "";
                                                        
                                                        // Check-in logic
                                                        boolean canCheckin = (
                                                            "paid".equalsIgnoreCase(b.getStatus()) ||
                                                            "pending".equalsIgnoreCase(b.getStatus())
                                                        ) && b.getCheckIn() != null && !now.before(b.getCheckIn());

                                                        boolean isWaitForCheckin = (
                                                            "paid".equalsIgnoreCase(b.getStatus()) ||
                                                            "pending".equalsIgnoreCase(b.getStatus())
                                                        ) && b.getCheckIn() != null && now.before(b.getCheckIn());

                                                        boolean canCheckout = "checkedin".equalsIgnoreCase(b.getStatus());
                                                        boolean completed =  "Completed".equalsIgnoreCase(b.getStatus());
                                                        // Assign room logic
                                                        boolean canAssignRoom = (
                                                            "paid".equalsIgnoreCase(b.getStatus()) ||
                                                            "pending".equalsIgnoreCase(b.getStatus())
                                                        );
                                                        
                                                        // Priority styling
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
                                                        <%= b.getRoomTypes() != null ? b.getRoomTypes() : "Not Assigned" %>
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
                                                        <% if (canCheckin) { %>
                                                        <a href="staff-room-assignment?action=assign&bookingId=<%= b.getId() %>" 
                                                           class="btn btn-success btn-sm" 
                                                           title="Assign rooms and check-in customer">
                                                            <i class="bi bi-person-check"></i> Check-in
                                                        </a>
                                                        <% } else if (!canCheckin && !canCheckout && !completed) { %>
                                                        <span class="text-muted">Waiting check-in</span>
                                                        <% } %>

                                                        <!-- Check-out Button -->
                                                        <% if (canCheckout) { %>
                                                        <form method="get" action="staff-checkout" style="display:inline;">
                                                            <input type="hidden" name="bookingId" value="<%= b.getId() %>"/>
                                                            <button class="btn btn-warning btn-sm" type="submit" title="Proceed to checkout payment">
                                                                <i class="bi bi-credit-card"></i> Checkout & Payment
                                                            </button>
                                                        </form>
                                                        <% } %>

                                                        <!-- View Customer Button -->
                                                        <a href="view-user-info?bookingId=<%= b.getId() %>" 
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
                                        </tbody>
                                    </table>
                                </div>

                                <!-- Pagination -->
                                <% if (totalPage > 1) { %>
                                <div class="pagination justify-content-center py-3">
                                    <% 
                                        String queryParams = "";
                                        if (keyword != null && !keyword.isEmpty()) queryParams += "&keyword=" + java.net.URLEncoder.encode(keyword, "UTF-8");
                                        if (status != null && !status.isEmpty()) queryParams += "&status=" + java.net.URLEncoder.encode(status, "UTF-8");
                                        if (fromDate != null && !fromDate.isEmpty()) queryParams += "&fromDate=" + java.net.URLEncoder.encode(fromDate, "UTF-8");
                                        if (toDate != null && !toDate.isEmpty()) queryParams += "&toDate=" + java.net.URLEncoder.encode(toDate, "UTF-8");
                                        queryParams += "&pageSize=" + pageSize;
                                    %>
                                    <% for (int i = 1; i <= totalPage; i++) { %>
                                    <% if (i == currentPage) { %>
                                    <span class="active"><%= i %></span>
                                    <% } else { %>
                                    <a href="staff-bookings-list?page=<%= i %><%= queryParams %>"><%= i %></a>
                                    <% } %>
                                    <% } %>
                                </div>
                                <% } %>
                            </div>
                        </div>
                    </div>
                </div>
            </main>
        </div>

        <!-- Help Modal -->
        <div class="modal fade" id="helpModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title"><i class="bi bi-question-circle"></i> Booking Management Help</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <h6>Action Buttons Explained:</h6>
                        <ul>
                            <li><strong>Assign Room:</strong> Assign specific rooms to paid/pending bookings before check-in</li>
                            <li><strong>Check-in:</strong> Available when booking is paid and check-in time has arrived</li>
                            <li><strong>Check-out:</strong> Available for checked-in customers</li>
                            <li><strong>View:</strong> View detailed customer information</li>
                        </ul>
                        <h6>Booking Status:</h6>
                        <ul>
                            <li><span class="badge bg-warning text-dark">Pending</span> - Awaiting payment</li>
                            <li><span class="badge bg-primary">Paid</span> - Payment confirmed, ready for room assignment</li>
                            <li><span class="badge bg-success">CheckedIn</span> - Customer has checked in</li>
                            <li><span class="badge bg-secondary">CheckedOut</span> - Customer has checked out</li>
                        </ul>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="js/main.js"></script>
        <script>
            setTimeout(function () {
                window.location.reload();
            }, 300000);

            document.querySelectorAll('form[action="staff-booking-action"]').forEach(form => {
                form.addEventListener('submit', function (e) {
                    const action = this.querySelector('input[name="action"]').value;
                    const bookingId = this.querySelector('input[name="bookingId"]').value;

                    if (action === 'checkin') {
                        if (!confirm(`Are you sure you want to check-in booking #${bookingId}?`)) {
                            e.preventDefault();
                        }
                    } else if (action === 'checkout') {
                        if (!confirm(`Are you sure you want to check-out booking #${bookingId}?`)) {
                            e.preventDefault();
                        }
                    }
                });
            });
            
            function changePageSize(newSize) {
                const currentUrl = new URL(window.location.href);
                currentUrl.searchParams.set('pageSize', newSize);
                currentUrl.searchParams.set('page', '1');
                window.location.href = currentUrl.toString();
            }
        </script>
    </body>
</html>