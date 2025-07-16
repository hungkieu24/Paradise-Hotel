<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, java.util.Map" %>
<%@ page import="Model.Room" %>
<%@ page import="Model.UserAccount" %>
<%
    List<Room> rooms = (List<Room>) request.getAttribute("rooms");
    Map<Integer, String> roomTypeMap = (Map<Integer, String>) request.getAttribute("roomTypeMap");
    Map<Integer, Double> roomTypePriceMap = (Map<Integer, Double>) request.getAttribute("roomTypePriceMap");
    Map<Integer, Integer> roomTypeCountMap = (Map<Integer, Integer>) request.getAttribute("roomTypeCountMap");
    String keyword = request.getParameter("keyword");
    String selectedStatus = request.getParameter("status");
    int currentPage = (request.getAttribute("currentPage") != null) ? (Integer)request.getAttribute("currentPage") : 1;
    int totalPage = (request.getAttribute("totalPage") != null) ? (Integer)request.getAttribute("totalPage") : 1;
    String staffName = "";
    if (session.getAttribute("user") != null) {
        staffName = ((UserAccount)session.getAttribute("user")).getUsername();
    }
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Room List</title>
        <link rel="stylesheet" href="css/style_1.css">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <style>
            .room-img {
                width: 80px;
                height: 60px;
                border-radius: 6px;
                box-shadow: 0 2px 6px rgba(0,0,0,.09);
                object-fit: cover;
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
                color: #495057;
            }
            .pagination a:hover {
                background-color: #e9ecef;
                color: #495057;
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
                margin-left: 250px;
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
            .alert {
                margin-bottom: 20px;
            }
            .badge-count {
                font-size: 0.8em;
                padding: 0.25em 0.5em;
            }
            .table th {
                background-color: #f8f9fa;
                border-bottom: 2px solid #dee2e6;
                font-weight: 600;
            }
            .no-image-placeholder {
                width: 80px;
                height: 60px;
                background-color: #f8f9fa;
                border: 2px dashed #dee2e6;
                border-radius: 6px;
                display: flex;
                align-items: center;
                justify-content: center;
                color: #6c757d;
            }
        </style>
    </head>
    <body class="bg-light">
        <div class="app-container">
            <!-- Sidebar -->
            <%@ include file="sidebar.jsp" %>
            <!-- Main Content -->
            <main class="main-content">
                <header class="content-header">
                    <div class="header-left">
                        <h1 class="page-title">Room List</h1>
                    </div>
                    <div class="header-right">
                        <div class="user-info">
                            <i class="fas fa-user-circle"></i>
                            <span><%= staffName != null && !staffName.isEmpty() ? staffName : "staff" %></span>
                        </div>
                    </div>
                </header>
                <div class="content-body">
                    <div class="form-container">
                        <!-- Success/Error Messages -->
                        <%
                            String message = request.getParameter("message");
                            String error = (String) request.getAttribute("error");
                            if ("statusUpdated".equals(message)) {
                        %>
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="bi bi-check-circle"></i> Room status updated successfully!
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <%
                            }
                            if (error != null && !error.isEmpty()) {
                        %>
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="bi bi-exclamation-triangle"></i> <%= error %>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <%
                            }
                        %>

                        <div class="d-flex mb-3 justify-content-between align-items-center">

                            <!-- Search form -->
                            <form class="d-flex" id="room-search-form" method="get" action="staff-rooms">
                                <input type="text" name="keyword" class="form-control me-2" placeholder="Search by Room Type ..."
                                    value="<%= keyword != null ? keyword : "" %>" autocomplete="off">
                                <select name="status" class="form-select me-2" style="width: 160px;">
                                    <option value="">All Status</option>
                                    <option value="Available" <%= "Available".equals(selectedStatus) ? "selected" : "" %>>Available</option>
                                    <option value="Booked" <%= "Booked".equals(selectedStatus) ? "selected" : "" %>>Booked</option>
                                    <option value="Occupied" <%= "Occupied".equals(selectedStatus) ? "selected" : "" %>>Occupied</option>
                                    <option value="Maintenance" <%= "Maintenance".equals(selectedStatus) ? "selected" : "" %>>Maintenance</option>
                                </select>
                                <button type="submit" class="btn btn-warning">
                                    <i class="bi bi-search"></i> Search
                                </button>
                            </form>
                        </div>

                        <form method="post" action="staff-rooms" id="bulk-room-status-form">
                            <div class="card shadow-sm">
                                <div class="card-header bg-warning">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <h2 class="mb-0"><i class="bi bi-door-open"></i> Room List</h2>
                                     
                                    </div>
                                </div>
                                <div class="card-body p-0">
                                    <table class="table table-hover align-middle mb-0">
                                        <thead class="table-light">
                                            <tr>
                                                <th>ID</th>
                                                <th>Room Number</th>
                                                <th>Room Type</th>
                                                <th>Total Rooms</th>
                                                <th>Price</th>
                                                <th>Status</th>
                                                <th>Image</th>
                                                <th>Change Status</th>
                                            </tr>
                                        </thead>
                                        <tbody id="rooms-table-body">
                                            <%
                                            if (rooms != null && !rooms.isEmpty()) {
                                                for (Room r : rooms) {
                                                    String typeName = roomTypeMap != null ? roomTypeMap.get(r.getRoomTypeId()) : "Unknown";
                                                    Double price = roomTypePriceMap != null ? roomTypePriceMap.get(r.getRoomTypeId()) : null;
                                                    Integer roomTypeId = r.getRoomTypeId();
                                                    Integer totalRoomForType = roomTypeCountMap != null ? roomTypeCountMap.get(roomTypeId) : null;
                                            %>
                                            <tr>
                                                <td><strong>#<%= r.getId() %></strong></td>
                                                <td><i class="bi bi-door-closed text-primary"></i> <%= r.getRoomNumber() %></td>
                                                <td>
                                                    <span class="text-primary fw-bold"><%= typeName != null ? typeName : "Unknown" %></span>
                                                </td>
                                                <td>
                                                    <span class="badge bg-info badge-count">
                                                        <%= totalRoomForType != null ? totalRoomForType : "N/A" %>
                                                    </span>
                                                </td>
                                                <td>
                                                    <span class="fw-bold text-success">
                                                    <%
                                                        if (price != null) {
                                                            out.print(String.format("%,.0f VND", price));
                                                        } else {
                                                            out.print("N/A");
                                                        }
                                                    %>
                                                    </span>
                                                </td>
                                                <td>
                                                    <% String status = r.getStatus(); %>
                                                    <span class="badge
                                                          <% if("Available".equalsIgnoreCase(status)) { %>bg-success
                                                          <% } else if("Booked".equalsIgnoreCase(status)) { %>bg-primary
                                                          <% } else if("Occupied".equalsIgnoreCase(status)) { %>bg-warning text-dark
                                                          <% } else if("Maintenance".equalsIgnoreCase(status)) { %>bg-secondary
                                                          <% } else { %>bg-light text-dark<% } %>">
                                                        <i class="bi bi-circle-fill me-1"></i><%= status %>
                                                    </span>
                                                </td>
                                                <td>
                                                    <% if (r.getImageUrl() != null && !r.getImageUrl().isEmpty()) { %>
                                                    <img src="<%= r.getImageUrl() %>" alt="Room <%= r.getRoomNumber() %>" class="room-img"/>
                                                    <% } else { %>
                                                    <div class="no-image-placeholder">
                                                        <i class="bi bi-image"></i>
                                                    </div>
                                                    <% } %>
                                                </td>
                                                <td>
                                                    <select name="status_<%= r.getId() %>" class="form-select form-select-sm">
                                                        <option value="Available" <%= "Available".equals(r.getStatus()) ? "selected" : "" %>>Available</option>
                                                        <option value="Booked" <%= "Booked".equals(r.getStatus()) ? "selected" : "" %>>Booked</option>
                                                        <option value="Occupied" <%= "Occupied".equals(r.getStatus()) ? "selected" : "" %>>Occupied</option>
                                                        <option value="Maintenance" <%= "Maintenance".equals(r.getStatus()) ? "selected" : "" %>>Maintenance</option>
                                                    </select>
                                                    <input type="hidden" name="roomId" value="<%= r.getId() %>"/>
                                                </td>
                                            </tr>
                                            <%
                                                }
                                            } else {
                                            %>
                                            <tr>
                                                <td colspan="8" class="text-center text-muted py-5">
                                                    <div class="d-flex flex-column align-items-center">
                                                        <i class="bi bi-inbox fs-1 mb-3 text-secondary"></i>
                                                        <h5 class="text-muted">No rooms found</h5>
                                                        <%
                                                            if (selectedStatus != null && !selectedStatus.isEmpty() || 
                                                                keyword != null && !keyword.isEmpty()) {
                                                        %>
                                                        <p class="text-muted">Try adjusting your search filters or clear all filters.</p>
                                                        <a href="staff-rooms" class="btn btn-outline-secondary btn-sm">
                                                            <i class="bi bi-arrow-clockwise"></i> Clear Filters
                                                        </a>
                                                        <%
                                                            }
                                                        %>
                                                    </div>
                                                </td>
                                            </tr>
                                            <% } %>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            
                            <% if (rooms != null && !rooms.isEmpty()) { %>
                            <div class="mt-3 d-flex justify-content-between align-items-center">
                                <div>
                                    <small class="text-muted">
                                        Showing <%= rooms.size() %> room(s) on page <%= currentPage %> of <%= totalPage %>
                                    </small>
                                </div>
                                <button type="submit" class="btn btn-primary" onclick="return confirmUpdate()">
                                    <i class="bi bi-arrow-repeat"></i> Update All Status
                                </button>
                            </div>
                            <% } %>
                        </form>

                        <!-- Pagination Bar -->
                        <% if (totalPage > 1) { %>
                        <div class="pagination" id="pagination-bar">
                            <%
                                String queryStr = "";
                                if (keyword != null && !keyword.isEmpty()) {
                                    queryStr += "&keyword=" + java.net.URLEncoder.encode(keyword, "UTF-8");
                                }
                                if (selectedStatus != null && !selectedStatus.isEmpty()) {
                                    queryStr += "&status=" + java.net.URLEncoder.encode(selectedStatus, "UTF-8");
                                }
                                if (currentPage > 1) {
                            %>
                            <a href="staff-rooms?page=<%=currentPage-1%><%=queryStr%>" title="Previous Page">&laquo;</a>
                            <%
                                }
                                for (int i = 1; i <= totalPage; i++) {
                                    if (i == currentPage) {
                            %>
                            <span class="active" aria-current="page"><%=i%></span>
                            <%
                                    } else {
                            %>
                            <a href="staff-rooms?page=<%=i%><%=queryStr%>"><%=i%></a>
                            <%
                                    }
                                }
                                if (currentPage < totalPage) {
                            %>
                            <a href="staff-rooms?page=<%=currentPage+1%><%=queryStr%>" title="Next Page">&raquo;</a>
                            <%
                                }
                            %>
                        </div>
                        <% } %>
                    </div>
                </div>
            </main>
        </div>
        
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="js/main.js"></script>
        
        <script>
            // Auto-hide success/error alerts after 5 seconds
            setTimeout(function() {
                var alerts = document.querySelectorAll('.alert');
                alerts.forEach(function(alert) {
                    if (bootstrap.Alert) {
                        var bsAlert = new bootstrap.Alert(alert);
                        bsAlert.close();
                    }
                });
            }, 5000);
            
            // Confirm update function
            function confirmUpdate() {
                var changedCount = 0;
                var selects = document.querySelectorAll('select[name^="status_"]');
                var originalStatuses = {};
                
                // Get original statuses from the badge display
                var statusBadges = document.querySelectorAll('span.badge');
                statusBadges.forEach(function(badge, index) {
                    if (badge.textContent && badge.textContent.trim()) {
                        var statusText = badge.textContent.trim();
                        // Remove the icon text if present
                        statusText = statusText.replace(/^\s*\S+\s*/, '');
                        originalStatuses[index] = statusText;
                    }
                });
                
                // Count changes
                selects.forEach(function(select, index) {
                    var currentValue = select.value;
                    var roomRow = select.closest('tr');
                    var statusBadge = roomRow.querySelector('span.badge');
                    if (statusBadge) {
                        var originalStatus = statusBadge.textContent.trim().replace(/^\s*\S+\s*/, '');
                        if (originalStatus !== currentValue) {
                            changedCount++;
                        }
                    }
                });
                
                if (changedCount === 0) {
                    alert('No room status changes detected. Please modify at least one room status before updating.');
                    return false;
                }
                
                return confirm('You are about to update status for ' + changedCount + ' room(s). Do you want to continue?');
            }
            
            // Add visual feedback for changed selects
            document.querySelectorAll('select[name^="status_"]').forEach(function(select) {
                var originalValue = select.value;
                select.addEventListener('change', function() {
                    if (this.value !== originalValue) {
                        this.classList.add('border-warning', 'bg-warning-subtle');
                    } else {
                        this.classList.remove('border-warning', 'bg-warning-subtle');
                    }
                });
            });
        </script>
    </body>
</html>