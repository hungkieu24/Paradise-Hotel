<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="Model.UserAccount" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.Booking" %>
<%
    List<Booking> pendingBookings = (List<Booking>) request.getAttribute("pendingBookings");
    java.util.Map<Integer, Boolean> fullyAssignedMap = (java.util.Map<Integer, Boolean>) request.getAttribute("fullyAssignedMap");
    
    String staffName = "";
    if (session.getAttribute("user") != null) {
        staffName = ((UserAccount)session.getAttribute("user")).getUsername();
    }
    
    // Success/error messages
    String successMessage = request.getParameter("success");
    String errorMessage = request.getParameter("error");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Room Assignment Management - Staff Panel</title>
    <link rel="stylesheet" href="css/style_1.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        .booking-card {
            border-radius: 8px;
            margin-bottom: 20px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
            transition: transform 0.3s;
        }
        .booking-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 6px 12px rgba(0,0,0,0.15);
        }
        .booking-header {
            padding: 15px;
            border-radius: 8px 8px 0 0;
            background-color: #f8f9fa;
        }
        .booking-body {
            padding: 15px;
        }
        .booking-footer {
            padding: 15px;
            border-top: 1px solid #eee;
            border-radius: 0 0 8px 8px;
        }
        .progress {
            height: 10px;
            border-radius: 5px;
        }
        .sidebar {
            height: 100vh;
            width: 220px;
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
        .stats-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 10px;
            padding: 15px;
            margin-bottom: 20px;
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
                        <i class="bi bi-door-open"></i> Room Assignment Management
                    </h1>
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
                    <% if (successMessage != null && successMessage.equals("completion")) { %>
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="bi bi-check-circle-fill me-2"></i>
                        Room assignment has been completed successfully.
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                    <% } %>

                    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="bi bi-exclamation-triangle-fill me-2"></i>
                        <% if (errorMessage.equals("incomplete")) { %>
                            Cannot complete assignment. Not all rooms have been assigned yet.
                        <% } else if (errorMessage.equals("failed")) { %>
                            Failed to assign room. The room might already be assigned or unavailable.
                        <% } else if (errorMessage.equals("remove-failed")) { %>
                            Failed to remove room assignment.
                        <% } else { %>
                            <%= errorMessage %>
                        <% } %>
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                    <% } %>

                    <!-- Navigation -->
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <a href="${pageContext.request.contextPath}/staff/dashboard" class="btn btn-outline-secondary">
                            <i class="bi bi-arrow-left"></i> Back to Dashboard
                        </a>
                        <div class="d-flex gap-2">
                            <button type="button" class="btn btn-outline-primary" onclick="window.location.reload()">
                                <i class="bi bi-arrow-clockwise"></i> Refresh
                            </button>
                            <button type="button" class="btn btn-outline-info" data-bs-toggle="modal" data-bs-target="#helpModal">
                                <i class="bi bi-question-circle"></i> Help
                            </button>
                        </div>
                    </div>

                    <!-- Statistics Cards -->
                    <%
                        int totalBookings = pendingBookings != null ? pendingBookings.size() : 0;
                        int fullyAssigned = 0, partiallyAssigned = 0, notAssigned = 0;
                        
                        if (pendingBookings != null && fullyAssignedMap != null) {
                            for (Booking b : pendingBookings) {
                                if (fullyAssignedMap.containsKey(b.getId()) && fullyAssignedMap.get(b.getId())) {
                                    fullyAssigned++;
                                } else {
                                    // Check if any rooms are assigned
                                    if (b.getRoomNumbers() != null && !b.getRoomNumbers().isEmpty()) {
                                        partiallyAssigned++;
                                    } else {
                                        notAssigned++;
                                    }
                                }
                            }
                        }
                    %>
                    <div class="row mb-4">
                        <div class="col-md-3">
                            <div class="stats-card">
                                <h4><%= totalBookings %></h4>
                                <p class="mb-0"><i class="bi bi-list-ul"></i> Total Bookings</p>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="stats-card">
                                <h4><%= fullyAssigned %></h4>
                                <p class="mb-0"><i class="bi bi-check2-all"></i> Fully Assigned</p>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="stats-card">
                                <h4><%= partiallyAssigned %></h4>
                                <p class="mb-0"><i class="bi bi-check"></i> Partially Assigned</p>
                            </div>
                        </div>
                        <div class="col-md-3">
                            <div class="stats-card">
                                <h4><%= notAssigned %></h4>
                                <p class="mb-0"><i class="bi bi-x-lg"></i> Not Assigned</p>
                            </div>
                        </div>
                    </div>

                    <!-- Bookings Cards -->
                    <div class="card shadow-sm">
                        <div class="card-header bg-primary text-white">
                            <h5 class="mb-0">
                                <i class="bi bi-clipboard-check"></i> Pending Bookings
                                <span class="badge bg-light text-primary ms-2"><%= totalBookings %></span>
                            </h5>
                        </div>
                        <div class="card-body">
                            <% if (pendingBookings == null || pendingBookings.isEmpty()) { %>
                            <div class="text-center text-muted py-5">
                                <i class="bi bi-inbox display-1 text-muted"></i>
                                <h5 class="mt-3">No pending bookings found</h5>
                                <p class="mb-0">There are no bookings that need room assignments at this time.</p>
                            </div>
                            <% } else { %>
                            <div class="row">
                                <% for (Booking booking : pendingBookings) { 
                                    boolean isFullyAssigned = fullyAssignedMap.containsKey(booking.getId()) && fullyAssignedMap.get(booking.getId());
                                %>
                                <div class="col-md-6 mb-4">
                                    <div class="booking-card">
                                        <div class="booking-header d-flex justify-content-between">
                                            <h5 class="mb-0">Booking #<%= booking.getId() %></h5>
                                            <span class="badge bg-<%= booking.getStatus().equalsIgnoreCase("Pending") ? "warning text-dark" : "primary" %>">
                                                <%= booking.getStatus() %>
                                            </span>
                                        </div>
                                        <div class="booking-body">
                                            <div class="mb-3">
                                                <p><strong><i class="bi bi-person-circle"></i> Customer:</strong> 
                                                    <%= (booking.getFullName() != null && !booking.getFullName().isEmpty()) ? booking.getFullName() : booking.getUserName() %>
                                                </p>
                                                <p><strong><i class="bi bi-calendar-check"></i> Check-in:</strong> 
                                                    <fmt:formatDate value="${booking.checkIn}" pattern="dd/MM/yyyy HH:mm"/>
                                                </p>
                                                <p><strong><i class="bi bi-calendar-x"></i> Check-out:</strong> 
                                                    <fmt:formatDate value="${booking.checkOut}" pattern="dd/MM/yyyy HH:mm"/>
                                                </p>
                                                <p><strong><i class="bi bi-door-closed"></i> Room Types:</strong> 
                                                    <%= booking.getRoomTypes() != null ? booking.getRoomTypes() : "N/A" %>
                                                </p>
                                            </div>
                                            
                                            <!-- Assignment Progress -->
                                            <div class="mt-3">
                                                <p><strong><i class="bi bi-sliders"></i> Assignment Progress:</strong></p>
                                                <div class="progress mb-2">
                                                    <div class="progress-bar bg-<%= isFullyAssigned ? "success" : "warning" %>" 
                                                         role="progressbar" 
                                                         style="width: <%= isFullyAssigned ? "100" : "50" %>%;" 
                                                         aria-valuenow="<%= isFullyAssigned ? "100" : "50" %>" 
                                                         aria-valuemin="0" 
                                                         aria-valuemax="100">
                                                    </div>
                                                </div>
                                                <p class="text-center mb-0">
                                                    <% if (isFullyAssigned) { %>
                                                        <span class="text-success"><i class="bi bi-check-circle-fill"></i> All rooms assigned</span>
                                                    <% } else { %>
                                                        <span class="text-warning"><i class="bi bi-exclamation-circle-fill"></i> Rooms need to be assigned</span>
                                                    <% } %>
                                                </p>
                                            </div>
                                        </div>
                                        <div class="booking-footer text-end">
                                            <a href="${pageContext.request.contextPath}/staff/room-assignment?action=assign&bookingId=<%= booking.getId() %>" 
                                               class="btn btn-<%= isFullyAssigned ? "outline-success" : "primary" %>">
                                                <i class="bi bi-<%= isFullyAssigned ? "eye" : "door-open" %>"></i> 
                                                <%= isFullyAssigned ? "View Assignments" : "Assign Rooms" %>
                                            </a>
                                        </div>
                                    </div>
                                </div>
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
                    <h5 class="modal-title"><i class="bi bi-question-circle"></i> Room Assignment Help</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <h6>Room Assignment Process:</h6>
                    <ul>
                        <li><strong>Assign Rooms:</strong> Allows you to assign specific rooms to a booking based on room type</li>
                        <li><strong>View Assignments:</strong> View and manage existing room assignments</li>
                        <li><strong>Complete Assignment:</strong> Finalize the room assignment process when all required rooms are assigned</li>
                    </ul>
                    <h6>Status Indicators:</h6>
                    <ul>
                        <li><span class="badge bg-warning text-dark">Pending</span> - Booking awaiting payment</li>
                        <li><span class="badge bg-primary">Paid</span> - Payment confirmed, ready for check-in</li>
                        <li><span class="badge bg-success">Fully Assigned</span> - All required rooms have been assigned</li>
                        <li><span class="badge bg-warning">Partially Assigned</span> - Some rooms still need to be assigned</li>
                    </ul>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Auto-dismiss alerts after 5 seconds
        window.addEventListener('DOMContentLoaded', function() {
            setTimeout(function() {
                var alerts = document.querySelectorAll('.alert');
                alerts.forEach(function(alert) {
                    var bsAlert = new bootstrap.Alert(alert);
                    bsAlert.close();
                });
            }, 5000);
        });
    </script>
</body>
</html>