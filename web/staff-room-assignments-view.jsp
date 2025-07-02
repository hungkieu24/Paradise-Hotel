<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="Model.UserAccount, Model.RoomAssignmentView, java.util.List, java.util.Map" %>
<%
    List<RoomAssignmentView> roomAssignments = (List<RoomAssignmentView>) request.getAttribute("roomAssignments");
    Map<String, Integer> statistics = (Map<String, Integer>) request.getAttribute("statistics");
    String branchName = (String) request.getAttribute("branchName");
    Integer userBranchId = (Integer) request.getAttribute("userBranchId");
    
    String staffName = "";
    if (session.getAttribute("user") != null) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user.getUsername() != null && !user.getUsername().isEmpty()) {
            staffName = user.getUsername();
        }
    }
    
    String contextPath = request.getContextPath();
    
    // Pagination
    Integer currentPage = (Integer) request.getAttribute("currentPage");
    Integer totalPages = (Integer) request.getAttribute("totalPages");
    Integer totalCount = (Integer) request.getAttribute("totalCount");
    Integer pageSize = (Integer) request.getAttribute("pageSize");
    
    if (currentPage == null) currentPage = 1;
    if (totalPages == null) totalPages = 1;
    if (totalCount == null) totalCount = 0;
    if (pageSize == null) pageSize = 20;
    
    // Filter values
    String statusFilter = (String) request.getAttribute("statusFilter");
    String dateFilter = (String) request.getAttribute("dateFilter");
    String searchQuery = (String) request.getAttribute("searchQuery");
    
    // Error and success messages
    String errorParam = request.getParameter("error");
    String successParam = request.getParameter("success");
    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Room Assignments - <%= branchName != null ? branchName : "Staff Panel" %></title>
    <link rel="stylesheet" href="<%= contextPath %>/css/style_1.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        /* Main Layout */
        .app-container {
            display: flex;
            min-height: 100vh;
            background: #f8f9fa;
        }
        
        .sidebar {
            width: 220px;
            height: 100vh;
            position: fixed;
            top: 0;
            left: 0;
            background: linear-gradient(180deg, #2c3e50 0%, #34495e 100%);
            color: #ecf0f1;
            z-index: 1000;
            overflow-y: auto;
        }
        
        .main-content {
            margin-left: 220px;
            flex: 1;
            background: #fff;
            min-height: 100vh;
        }
        
        /* Header */
        .content-header {
            background: linear-gradient(135deg, #007bff, #0056b3);
            color: white;
            padding: 25px 40px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        .header-top {
            display: flex;
            justify-content: between;
            align-items: center;
            margin-bottom: 15px;
        }
        
        .page-title {
            font-size: 1.75rem;
            font-weight: 600;
            margin: 0;
            display: flex;
            align-items: center;
            gap: 12px;
        }
        
        .user-info {
            display: flex;
            align-items: center;
            gap: 10px;
            background: rgba(255,255,255,0.2);
            padding: 8px 16px;
            border-radius: 20px;
            backdrop-filter: blur(10px);
            margin-left: auto;
        }
        
        .branch-info {
            background: rgba(255,255,255,0.1);
            padding: 15px 20px;
            border-radius: 10px;
            border-left: 4px solid rgba(255,255,255,0.5);
        }
        
        .branch-title {
            font-size: 1.1rem;
            font-weight: 600;
            margin-bottom: 5px;
        }
        
        .branch-subtitle {
            font-size: 0.9rem;
            opacity: 0.9;
            margin: 0;
        }
        
        /* Content */
        .content-body {
            padding: 30px 40px;
            max-width: 1400px;
            margin: 0 auto;
        }
        
        /* Statistics Cards */
        .stats-row {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        
        .stats-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 12px;
            padding: 20px;
            text-align: center;
            box-shadow: 0 4px 15px rgba(102,126,234,0.3);
            transition: transform 0.3s ease;
        }
        
        .stats-card:hover {
            transform: translateY(-5px);
        }
        
        .stats-number {
            font-size: 2rem;
            font-weight: 700;
            margin-bottom: 5px;
        }
        
        .stats-label {
            font-size: 0.85rem;
            opacity: 0.9;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 6px;
        }
        
        /* Filter Section */
        .filter-section {
            background: white;
            border-radius: 12px;
            padding: 25px;
            margin-bottom: 25px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.05);
            border: 1px solid #e3e6f0;
        }
        
        .filter-row {
            display: grid;
            grid-template-columns: 2fr 1fr 1fr 1fr auto;
            gap: 15px;
            align-items: end;
        }
        
        /* Table */
        .assignments-table {
            background: white;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }
        
        .table-header {
            background: linear-gradient(135deg, #f8f9fa, #e9ecef);
            padding: 20px 25px;
            border-bottom: 1px solid #e9ecef;
        }
        
        .table-title {
            font-size: 1.2rem;
            font-weight: 600;
            color: #2c3e50;
            margin: 0;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .table-responsive {
            max-height: 700px;
            overflow-y: auto;
        }
        
        .table th {
            background: #f8f9fa;
            font-weight: 600;
            border: none;
            padding: 15px;
            position: sticky;
            top: 0;
            z-index: 10;
        }
        
        .table td {
            padding: 15px;
            vertical-align: middle;
            border-color: #f1f3f4;
        }
        
        .table tbody tr:hover {
            background: rgba(13,110,253,0.05);
        }
        
        /* Status Badges */
        .status-badge {
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: 500;
        }
        
        /* Customer Info */
        .customer-info {
            display: flex;
            flex-direction: column;
            gap: 3px;
        }
        
        .customer-name {
            font-weight: 600;
            color: #2c3e50;
        }
        
        .customer-contact {
            font-size: 0.8rem;
            color: #6c757d;
        }
        
        /* Room Info */
        .room-info {
            display: flex;
            flex-direction: column;
            gap: 3px;
        }
        
        .room-number {
            font-weight: 600;
            color: #007bff;
            font-size: 1.1rem;
        }
        
        .room-type {
            font-size: 0.85rem;
            color: #6c757d;
        }
        
        /* Date Info */
        .date-info {
            display: flex;
            flex-direction: column;
            gap: 2px;
        }
        
        .date-main {
            font-weight: 500;
            color: #2c3e50;
            font-size: 0.9rem;
        }
        
        .date-sub {
            font-size: 0.75rem;
            color: #6c757d;
        }
        
        /* Actions */
        .action-buttons {
            display: flex;
            gap: 5px;
            flex-wrap: wrap;
        }
        
        .btn-sm {
            padding: 6px 12px;
            font-size: 0.8rem;
        }
        
        /* Navigation */
        .nav-buttons {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 25px;
        }
        
        .btn-back {
            background: #6c757d;
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 8px;
            text-decoration: none;
            display: flex;
            align-items: center;
            gap: 8px;
            transition: all 0.3s ease;
        }
        
        .btn-back:hover {
            background: #5a6268;
            color: white;
            transform: translateY(-2px);
        }
        
        .btn-refresh {
            background: #007bff;
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 8px;
            display: flex;
            align-items: center;
            gap: 8px;
            transition: all 0.3s ease;
        }
        
        .btn-refresh:hover {
            background: #0056b3;
            transform: translateY(-2px);
        }
        
        /* Pagination */
        .pagination-container {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 20px 25px;
            background: #f8f9fa;
            border-top: 1px solid #e9ecef;
        }
        
        .pagination-info {
            color: #6c757d;
            font-size: 0.9rem;
        }
        
        .pagination {
            margin: 0;
        }
        
        .page-link {
            border-radius: 6px;
            margin: 0 2px;
            border-color: #e9ecef;
        }
        
        /* Empty State */
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #6c757d;
        }
        
        .empty-icon {
            font-size: 4rem;
            color: #dee2e6;
            margin-bottom: 20px;
        }
        
        /* Alerts */
        .alert {
            border: none;
            border-radius: 10px;
            padding: 16px 20px;
            border-left: 4px solid;
            margin-bottom: 20px;
        }
        
        .alert-success {
            background: linear-gradient(135deg, #d4edda, #c3e6cb);
            border-left-color: #28a745;
            color: #155724;
        }
        
        .alert-danger {
            background: linear-gradient(135deg, #f8d7da, #f1aeb5);
            border-left-color: #dc3545;
            color: #721c24;
        }
        
        /* Responsive */
        @media (max-width: 768px) {
            .main-content {
                margin-left: 0;
            }
            
            .sidebar {
                transform: translateX(-100%);
            }
            
            .content-header {
                padding: 20px;
            }
            
            .content-body {
                padding: 20px;
            }
            
            .filter-row {
                grid-template-columns: 1fr;
            }
            
            .stats-row {
                grid-template-columns: repeat(2, 1fr);
            }
            
            .nav-buttons {
                flex-direction: column;
                gap: 10px;
                align-items: stretch;
            }
        }
    </style>
</head>
<body>
    <div class="app-container">
        <nav class="sidebar" id="sidebar">
    <div class="sidebar-header">
        <div class="brand">
            <i class="fas fa-building"></i>
            <span class="brand-text"><%= branchName %></span>
        </div>
        <button class="sidebar-toggle" id="sidebarToggle">
            <i class="fas fa-bars"></i>
        </button>
    </div>
    <div class="sidebar-menu">
        <a href="staff-dashboard.jsp" class="menu-item active">
            <i class="fas fa-user-cog"></i>
            <span class="menu-text">Panel</span>
        </a>
        <a href="staff-bookings-list" class="menu-item">
            <i class="fas fa-calendar-check"></i>
            <span class="menu-text">Today's Bookings</span>
        </a>
        <a href="staff-rooms" class="menu-item">
            <i class="fas fa-door-closed"></i>
            <span class="menu-text">Room List</span>
        </a>
        <a href="searchGuest" class="menu-item">
            <i class="fas fa-plus-circle"></i>
            <span class="menu-text">Add New Booking</span>
        </a>
        <a href="logout.jsp" class="menu-item logout">
            <i class="fas fa-sign-out-alt"></i>
            <span class="menu-text">Logout</span>
        </a>
    </div>
</nav>
        
        <main class="main-content">
            <!-- Header -->
            <header class="content-header">
                <div class="header-top">
                    <h1 class="page-title">
                        <i class="bi bi-table"></i>
                        Room Assignments
                    </h1>
                    <div class="user-info">
                        <i class="bi bi-person-circle"></i>
                        <span>Staff: <strong><%= staffName %></strong></span>

                    </div>
                </div>
             
            </header>
            
            <div class="content-body">
                <!-- Success/Error Messages -->
                <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    <strong>Error!</strong> <%= errorMessage %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>
                
                <% if (errorParam != null && !errorParam.isEmpty()) { %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    <strong>Error!</strong>
                    <% if ("operation_failed".equals(errorParam)) { %>
                        Operation failed. Please try again.
                    <% } else { %>
                        <%= errorParam %>
                    <% } %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>
                
                <% if (successParam != null && !successParam.isEmpty()) { %>
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="bi bi-check-circle me-2"></i>
                    <strong>Success!</strong> Operation completed successfully.
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <% } %>

                <!-- Navigation -->
                <div class="nav-buttons">
                    <a href="<%= contextPath %>/staff-room-assignment" class="btn-back">
                        <i class="bi bi-arrow-left"></i>
                        Back to Assignment Management
                    </a>
                    <button type="button" class="btn-refresh" onclick="window.location.reload()">
                        <i class="bi bi-arrow-clockwise"></i>
                        Refresh Data
                    </button>
                </div>

                <!-- Statistics -->
                <% if (statistics != null) { %>
                <div class="stats-row">
                    <div class="stats-card">
                        <div class="stats-number"><%= statistics.get("total") != null ? statistics.get("total") : 0 %></div>
                        <div class="stats-label">
                            <i class="bi bi-list-ul"></i>Total Assignments
                        </div>
                    </div>
                    <div class="stats-card">
                        <div class="stats-number"><%= statistics.get("checkedIn") != null ? statistics.get("checkedIn") : 0 %></div>
                        <div class="stats-label">
                            <i class="bi bi-check-circle"></i>Checked In
                        </div>
                    </div>
                    <div class="stats-card">
                        <div class="stats-number"><%= statistics.get("checkedOut") != null ? statistics.get("checkedOut") : 0 %></div>
                        <div class="stats-label">
                            <i class="bi bi-box-arrow-right"></i>Checked Out
                        </div>
                    </div>
                    <div class="stats-card">
                        <div class="stats-number"><%= statistics.get("pending") != null ? statistics.get("pending") : 0 %></div>
                        <div class="stats-label">
                            <i class="bi bi-clock"></i>Pending
                        </div>
                    </div>
                    <div class="stats-card">
                        <div class="stats-number"><%= statistics.get("paid") != null ? statistics.get("paid") : 0 %></div>
                        <div class="stats-label">
                            <i class="bi bi-credit-card"></i>Paid
                        </div>
                    </div>
                    <div class="stats-card">
                        <div class="stats-number"><%= statistics.get("completed") != null ? statistics.get("completed") : 0 %></div>
                        <div class="stats-label">
                            <i class="bi bi-check-all"></i>Completed
                        </div>
                    </div>
                </div>
                <% } %>

                <!-- Filter Section -->
                <div class="filter-section">
                    <form method="get" action="<%= contextPath %>/staff-room-assignments-view">
                        <div class="filter-row">
                            <div>
                                <label class="form-label">Search</label>
                                <input type="text" class="form-control" name="search" 
                                       value="<%= searchQuery != null ? searchQuery : "" %>"
                                       placeholder="Customer name, room number, email, booking ID...">
                            </div>
                            <div>
                                <label class="form-label">Status</label>
                                <select class="form-select" name="status">
                                    <option value="">All Statuses</option>
                                    <option value="Pending" <%= "Pending".equals(statusFilter) ? "selected" : "" %>>Pending</option>
                                    <option value="Paid" <%= "Paid".equals(statusFilter) ? "selected" : "" %>>Paid</option>
                                    <option value="CheckedIn" <%= "CheckedIn".equals(statusFilter) ? "selected" : "" %>>Checked In</option>
                                    <option value="CheckedOut" <%= "CheckedOut".equals(statusFilter) ? "selected" : "" %>>Checked Out</option>
                                    <option value="Completed" <%= "Completed".equals(statusFilter) ? "selected" : "" %>>Completed</option>
                                </select>
                            </div>
                            <div>
                                <label class="form-label">Check-in Date</label>
                                <input type="date" class="form-control" name="date" 
                                       value="<%= dateFilter != null ? dateFilter : "" %>">
                            </div>
                            <div>
                                <label class="form-label">Items per page</label>
                                <select class="form-select" name="pageSize">
                                    <option value="20" <%= pageSize == 20 ? "selected" : "" %>>20</option>
                                    <option value="50" <%= pageSize == 50 ? "selected" : "" %>>50</option>
                                    <option value="100" <%= pageSize == 100 ? "selected" : "" %>>100</option>
                                </select>
                            </div>
                            <div>
                                <button type="submit" class="btn btn-primary">
                                    <i class="bi bi-search me-1"></i>Search
                                </button>
                            </div>
                        </div>
                    </form>
                </div>

                <!-- Table -->
                <div class="assignments-table">
                    <div class="table-header">
                        <h3 class="table-title">
                            <i class="bi bi-table"></i>
                            Room Assignments
                            <span class="badge bg-primary ms-2"><%= totalCount %></span>
                        </h3>
                    </div>
                    
                    <% if (roomAssignments == null || roomAssignments.isEmpty()) { %>
                    <div class="empty-state">
                        <div class="empty-icon">
                            <i class="bi bi-inbox"></i>
                        </div>
                        <h4>No Room Assignments Found</h4>
                        <p>No room assignments match your current filters for this branch.</p>
                    </div>
                    <% } else { %>
                    <div class="table-responsive">
                        <table class="table table-hover mb-0">
                            <thead>
                                <tr>
                                    <th>Booking #</th>
                                    <th>Customer</th>
                                    <th>Room</th>
                                    <th>Check-in</th>
                                    <th>Check-out</th>
                                    <th>Status</th>
                                    <th>Payment</th>
                                    <th>Total</th>
                                    <th>Assigned</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (RoomAssignmentView assignment : roomAssignments) { %>
                                <tr>
                                    <td>
                                        <strong class="text-primary">#<%= assignment.getBookingId() %></strong>
                                    </td>
                                    <td>
                                        <div class="customer-info">
                                            <div class="customer-name"><%= assignment.getCustomerName() %></div>
                                            <% if (assignment.getCustomerEmail() != null) { %>
                                                <div class="customer-contact">
                                                    <i class="bi bi-envelope me-1"></i><%= assignment.getCustomerEmail() %>
                                                </div>
                                            <% } %>
                                            <% if (assignment.getCustomerPhone() != null) { %>
                                                <div class="customer-contact">
                                                    <i class="bi bi-telephone me-1"></i><%= assignment.getCustomerPhone() %>
                                                </div>
                                            <% } %>
                                        </div>
                                    </td>
                                    <td>
                                        <div class="room-info">
                                            <div class="room-number">
                                                <i class="bi bi-door-closed me-1"></i><%= assignment.getRoomNumber() %>
                                            </div>
                                            <div class="room-type"><%= assignment.getRoomTypeName() %></div>
                                        </div>
                                    </td>
                                    <td>
                                        <div class="date-info">
                                            <div class="date-main">
                                                <fmt:formatDate value="<%= assignment.getCheckIn() %>" pattern="dd/MM/yyyy"/>
                                            </div>
                                            <div class="date-sub">
                                                <fmt:formatDate value="<%= assignment.getCheckIn() %>" pattern="HH:mm"/>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <div class="date-info">
                                            <div class="date-main">
                                                <fmt:formatDate value="<%= assignment.getCheckOut() %>" pattern="dd/MM/yyyy"/>
                                            </div>
                                            <div class="date-sub">
                                                <fmt:formatDate value="<%= assignment.getCheckOut() %>" pattern="HH:mm"/>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <span class="status-badge <%= assignment.getStatusBadgeClass() %>">
                                            <%= assignment.getBookingStatus() %>
                                        </span>
                                    </td>
                                    <td>
                                        <span class="status-badge <%= assignment.getPaymentBadgeClass() %>">
                                            <%= assignment.getPaymentStatus() %>
                                        </span>
                                    </td>
                                    <td>
                                        <strong><%= assignment.getFormattedTotalPrice() %></strong>
                                        <% if (assignment.getNights() > 0) { %>
                                            <br><small class="text-muted"><%= assignment.getNights() %> nights</small>
                                        <% } %>
                                        <% if (assignment.getMembershipLevel() != null && !"Member".equals(assignment.getMembershipLevel())) { %>
                                            <br><span class="badge bg-warning text-dark">
                                                <i class="bi bi-star me-1"></i><%= assignment.getMembershipLevel() %>
                                            </span>
                                        <% } %>
                                    </td>
                                    <td>
                                        <div class="date-info">
                                            <div class="date-main">
                                                <fmt:formatDate value="<%= assignment.getAssignedAt() %>" pattern="dd/MM/yyyy"/>
                                            </div>
                                            <div class="date-sub">
                                                <fmt:formatDate value="<%= assignment.getAssignedAt() %>" pattern="HH:mm"/>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <div class="action-buttons">
                                            <a href="<%= contextPath %>/staff-room-assignment?action=assign&bookingId=<%= assignment.getBookingId() %>" 
                                               class="btn btn-outline-primary btn-sm" title="Manage Assignment">
                                                <i class="bi bi-gear"></i>
                                            </a>
                                            <a href="<%= contextPath %>/view-booking-detail?bookingId=<%= assignment.getBookingId() %>" 
                                               class="btn btn-outline-info btn-sm" title="View Details">
                                                <i class="bi bi-eye"></i>
                                            </a>
                                        </div>
                                    </td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                    
                    <!-- Pagination -->
                    <div class="pagination-container">
                        <div class="pagination-info">
                            Showing <%= ((currentPage - 1) * pageSize) + 1 %> to 
                            <%= Math.min(currentPage * pageSize, totalCount) %> of 
                            <%= totalCount %> entries
                        </div>
                        
                        <% if (totalPages > 1) { %>
                        <nav>
                            <ul class="pagination">
                                <!-- Previous -->
                                <% if (currentPage > 1) { %>
                                <li class="page-item">
                                    <a class="page-link" href="?page=<%= currentPage - 1 %>&pageSize=<%= pageSize %>&status=<%= statusFilter != null ? statusFilter : "" %>&date=<%= dateFilter != null ? dateFilter : "" %>&search=<%= searchQuery != null ? searchQuery : "" %>">
                                        <i class="bi bi-chevron-left"></i>
                                    </a>
                                </li>
                                <% } %>
                                
                                <!-- Page Numbers -->
                                <%
                                    int startPage = Math.max(1, currentPage - 2);
                                    int endPage = Math.min(totalPages, currentPage + 2);
                                    
                                    for (int i = startPage; i <= endPage; i++) {
                                %>
                                <li class="page-item <%= i == currentPage ? "active" : "" %>">
                                    <a class="page-link" href="?page=<%= i %>&pageSize=<%= pageSize %>&status=<%= statusFilter != null ? statusFilter : "" %>&date=<%= dateFilter != null ? dateFilter : "" %>&search=<%= searchQuery != null ? searchQuery : "" %>">
                                        <%= i %>
                                    </a>
                                </li>
                                <% } %>
                                
                                <!-- Next -->
                                <% if (currentPage < totalPages) { %>
                                <li class="page-item">
                                    <a class="page-link" href="?page=<%= currentPage + 1 %>&pageSize=<%= pageSize %>&status=<%= statusFilter != null ? statusFilter : "" %>&date=<%= dateFilter != null ? dateFilter : "" %>&search=<%= searchQuery != null ? searchQuery : "" %>">
                                        <i class="bi bi-chevron-right"></i>
                                    </a>
                                </li>
                                <% } %>
                            </ul>
                        </nav>
                        <% } %>
                    </div>
                    <% } %>
                </div>
            </div>
        </main>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Auto-dismiss alerts after 5 seconds
        window.addEventListener('DOMContentLoaded', function() {
            setTimeout(function() {
                const alerts = document.querySelectorAll('.alert');
                alerts.forEach(function(alert) {
                    const bsAlert = new bootstrap.Alert(alert);
                    bsAlert.close();
                });
            }, 5000);
        });
        
        // Add current date to date filter if empty
        document.addEventListener('DOMContentLoaded', function() {
            const dateInput = document.querySelector('input[name="date"]');
            if (dateInput && !dateInput.value) {
                // You can uncomment this to auto-set today's date
                // dateInput.value = new Date().toISOString().split('T')[0];
            }
        });
    </script>
</body>
</html>