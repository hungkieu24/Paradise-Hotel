<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="Model.UserAccount" %>
<%@ page import="Model.Booking" %>
<%@ page import="Model.BookingRoomType" %>
<%@ page import="Model.Room" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%
    Booking booking = (Booking) request.getAttribute("booking");
    List<BookingRoomType> bookingRoomTypes = (List<BookingRoomType>) request.getAttribute("bookingRoomTypes");
    List<Room> assignedRooms = (List<Room>) request.getAttribute("assignedRooms");
    Map<Integer, Integer> assignmentCounts = (Map<Integer, Integer>) request.getAttribute("assignmentCounts");
    Map<Integer, Integer> remainingRoomQuantities = (Map<Integer, Integer>) request.getAttribute("remainingRoomQuantities");
    Map<Integer, List<Room>> availableRoomsByType = (Map<Integer, List<Room>>) request.getAttribute("availableRoomsByType");
    Boolean isFullyAssigned = (Boolean) request.getAttribute("isFullyAssigned");
    
    String staffName = "";
    if (session.getAttribute("user") != null) {
        staffName = ((UserAccount)session.getAttribute("user")).getUsername();
    }
    
    // Error parameters
    String errorParam = request.getParameter("error");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Assign Rooms - Staff Panel</title>
    <link rel="stylesheet" href="css/style_1.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        .room-card {
            border-radius: 8px;
            box-shadow: 0 3px 6px rgba(0,0,0,0.1);
            transition: transform 0.3s;
            height: 100%;
            position: relative;
            overflow: hidden;
        }
        .room-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 5px 10px rgba(0,0,0,0.15);
        }
        .room-card-header {
            position: relative;
            height: 120px;
            overflow: hidden;
            background-color: #f8f9fa;
        }
        .room-card-header img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
        .room-card-body {
            padding: 15px;
        }
        .room-type-section {
            margin-bottom: 2rem;
            padding: 1.5rem;
            border-radius: 8px;
            background-color: #f9f9f9;
            box-shadow: 0 2px 4px rgba(0,0,0,0.05);
        }
        .assigned-rooms-section {
            margin-bottom: 2rem;
            padding: 1.5rem;
            border-radius: 8px;
            border: 1px solid #28a745;
            background-color: #f0f9f0;
        }
        .assigned-room-badge {
            display: inline-flex;
            align-items: center;
            margin: 0.25rem;
            padding: 0.5rem 1rem;
            border-radius: 20px;
            background-color: #28a745;
            color: white;
            font-weight: 500;
        }
        .assigned-room-badge form {
            display: inline;
        }
        .assigned-room-badge button {
            background: none;
            border: none;
            color: white;
            margin-left: 0.5rem;
            cursor: pointer;
        }
        .room-status-badge {
            position: absolute;
            top: 10px;
            right: 10px;
            padding: 0.35rem 0.75rem;
            border-radius: 20px;
            font-size: 0.8rem;
            z-index: 1;
        }
        .nav-tabs .nav-link.active {
            font-weight: 600;
            color: #0d6efd;
            border-color: #0d6efd #0d6efd #fff;
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
    </style>
</head>
<body class="bg-light">
    <div class="app-container">
        <%@ include file="sidebar.jsp" %>
        <main class="main-content">
            <header class="content-header">
                <div class="header-left">
                    <h1 class="page-title">
                        <i class="bi bi-door-open"></i> Assign Rooms
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
                    <% if (errorParam != null && !errorParam.isEmpty()) { %>
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="bi bi-exclamation-triangle-fill me-2"></i>
                        <% if (errorParam.equals("failed")) { %>
                            Failed to assign room. The room might already be assigned or unavailable.
                        <% } else if (errorParam.equals("remove-failed")) { %>
                            Failed to remove room assignment.
                        <% } else if (errorParam.equals("incomplete")) { %>
                            Cannot complete assignment. Not all rooms have been assigned yet.
                        <% } else { %>
                            An error occurred: <%= errorParam %>
                        <% } %>
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                    <% } %>

                    <!-- Navigation -->
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <a href="${pageContext.request.contextPath}/staff/room-assignment" class="btn btn-outline-secondary">
                            <i class="bi bi-arrow-left"></i> Back to Assignment List
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

                    <!-- Booking Details Card -->
                    <div class="card mb-4 shadow-sm">
                        <div class="card-header bg-primary text-white">
                            <h5 class="mb-0"><i class="bi bi-info-circle"></i> Booking Details #<%= booking.getId() %></h5>
                        </div>
                        <div class="card-body">
                            <div class="row">
                                <div class="col-md-6">
                                    <p><strong><i class="bi bi-person-circle"></i> Customer:</strong> 
                                        <%= (booking.getFullName() != null && !booking.getFullName().isEmpty()) ? booking.getFullName() : booking.getUserName() %>
                                    </p>
                                    <p><strong><i class="bi bi-calendar-check"></i> Check-in:</strong> 
                                        <fmt:formatDate value="${booking.checkIn}" pattern="dd/MM/yyyy HH:mm"/>
                                    </p>
                                    <p><strong><i class="bi bi-calendar-x"></i> Check-out:</strong> 
                                        <fmt:formatDate value="${booking.checkOut}" pattern="dd/MM/yyyy HH:mm"/>
                                    </p>
                                </div>
                                <div class="col-md-6">
                                    <p><strong><i class="bi bi-cash-coin"></i> Payment Status:</strong> 
                                        <span class="badge bg-<%= booking.getPaymentStatus().equalsIgnoreCase("Paid") ? "success" : "warning text-dark" %>">
                                            <%= booking.getPaymentStatus() %>
                                        </span>
                                    </p>
                                    <p><strong><i class="bi bi-bookmark"></i> Booking Status:</strong> 
                                        <span class="badge bg-<%= booking.getStatus().equalsIgnoreCase("Pending") ? "warning text-dark" : "primary" %>">
                                            <%= booking.getStatus() %>
                                        </span>
                                    </p>
                                    <p><strong><i class="bi bi-currency-dollar"></i> Total Price:</strong> 
                                        <%= String.format("%,.0f", booking.getTotalPrice()) %> đ
                                    </p>
                                </div>
                            </div>
                            <div class="mt-3">
                                <h6><i class="bi bi-door-closed"></i> Room Types Booked:</h6>
                                <div>
                                    <% if (bookingRoomTypes != null) { %>
                                        <% for (BookingRoomType brt : bookingRoomTypes) { %>
                                            <span class="badge bg-info text-dark me-2">
                                                <%= brt.getRoomTypeName() %> × <%= brt.getQuantity() %>
                                            </span>
                                        <% } %>
                                    <% } else { %>
                                        <span class="badge bg-secondary"><%= booking.getRoomTypes() %></span>
                                    <% } %>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Assigned Rooms Section -->
                    <div class="assigned-rooms-section">
                        <h4 class="mb-3"><i class="bi bi-check-circle"></i> Assigned Rooms</h4>
                        
                        <% if (assignedRooms == null || assignedRooms.isEmpty()) { %>
                            <div class="alert alert-info">
                                <i class="bi bi-info-circle"></i> No rooms have been assigned yet. Please select rooms from the available options below.
                            </div>
                        <% } else { %>
                            <div class="mb-3">
                                <% for (Room room : assignedRooms) { %>
                                    <div class="assigned-room-badge">
                                        <i class="bi bi-door-open me-2"></i>
                                        <%= room.getRoomNumber() %> (<%= room.getRoomTypeName() %>)
                                        <form action="${pageContext.request.contextPath}/staff/room-assignment" method="post">
                                            <input type="hidden" name="action" value="remove-assignment">
                                            <input type="hidden" name="bookingId" value="<%= booking.getId() %>">
                                            <input type="hidden" name="roomId" value="<%= room.getId() %>">
                                            <button type="submit" title="Remove" onclick="return confirm('Are you sure you want to remove this room assignment?');">
                                                <i class="bi bi-x-lg"></i>
                                            </button>
                                        </form>
                                    </div>
                                <% } %>
                            </div>
                            
                            <!-- Room Assignment Progress -->
                            <div class="mt-4">
                                <h5 class="mb-3"><i class="bi bi-sliders"></i> Assignment Progress by Room Type</h5>
                                <div class="row">
                                    <% if (bookingRoomTypes != null) { %>
                                        <% for (BookingRoomType brt : bookingRoomTypes) { %>
                                            <div class="col-md-6 mb-3">
                                                <h6><%= brt.getRoomTypeName() %></h6>
                                                <% 
                                                    int assigned = (assignmentCounts != null && assignmentCounts.containsKey(brt.getRoomTypeId())) 
                                                        ? assignmentCounts.get(brt.getRoomTypeId()) : 0;
                                                    int required = brt.getQuantity();
                                                    int percentage = (required > 0) ? (assigned * 100 / required) : 0;
                                                %>
                                                <div class="progress mb-2">
                                                    <div class="progress-bar <%= (assigned >= required) ? "bg-success" : "bg-warning" %>" 
                                                         role="progressbar" 
                                                         style="width: <%= percentage %>%;" 
                                                         aria-valuenow="<%= assigned %>" 
                                                         aria-valuemin="0" 
                                                         aria-valuemax="<%= required %>">
                                                    </div>
                                                </div>
                                                <p class="text-end mb-0"><%= assigned %> / <%= required %> assigned</p>
                                            </div>
                                        <% } %>
                                    <% } %>
                                </div>
                            </div>
                        <% } %>
                        
                        <% if (isFullyAssigned != null && isFullyAssigned) { %>
                            <div class="alert alert-success mt-4">
                                <i class="bi bi-check-circle-fill"></i> All required rooms have been assigned.
                            </div>
                            
                            <div class="text-center mt-3">
                                <form action="${pageContext.request.contextPath}/staff/room-assignment" method="post" onsubmit="return confirm('Are you sure you want to complete the room assignment?');">
                                    <input type="hidden" name="action" value="complete-assignment">
                                    <input type="hidden" name="bookingId" value="<%= booking.getId() %>">
                                    <button type="submit" class="btn btn-success btn-lg">
                                        <i class="bi bi-check2-all"></i> Complete Assignment
                                    </button>
                                </form>
                            </div>
                        <% } %>
                    </div>
                    
                    <!-- Available Rooms Section -->
                    <% if (isFullyAssigned == null || !isFullyAssigned) { %>
                        <div class="card shadow-sm">
                            <div class="card-header bg-primary text-white">
                                <h5 class="mb-0"><i class="bi bi-search"></i> Available Rooms</h5>
                            </div>
                            <div class="card-body">
                                <% if (availableRoomsByType == null || availableRoomsByType.isEmpty()) { %>
                                    <div class="alert alert-warning">
                                        <i class="bi bi-exclamation-triangle-fill"></i> No room types need assignment or no rooms are available.
                                    </div>
                                <% } else { %>
                                    <ul class="nav nav-tabs mb-4" id="roomTypeTabs" role="tablist">
                                        <% 
                                            boolean firstTab = true;
                                            for (Map.Entry<Integer, List<Room>> entry : availableRoomsByType.entrySet()) {
                                                Integer roomTypeId = entry.getKey();
                                                List<Room> rooms = entry.getValue();
                                                
                                                String roomTypeName = "";
                                                int remaining = 0;
                                                
                                                // Find room type name and remaining count
                                                if (bookingRoomTypes != null) {
                                                    for (BookingRoomType brt : bookingRoomTypes) {
                                                        if (brt.getRoomTypeId() == roomTypeId) {
                                                            roomTypeName = brt.getRoomTypeName();
                                                            remaining = remainingRoomQuantities.containsKey(roomTypeId) ? 
                                                                remainingRoomQuantities.get(roomTypeId) : brt.getQuantity();
                                                            break;
                                                        }
                                                    }
                                                }
                                        %>
                                        <li class="nav-item" role="presentation">
                                            <button class="nav-link <%= firstTab ? "active" : "" %>" 
                                                    id="tab-<%= roomTypeId %>" 
                                                    data-bs-toggle="tab" 
                                                    data-bs-target="#roomType-<%= roomTypeId %>" 
                                                    type="button" 
                                                    role="tab" 
                                                    aria-controls="roomType-<%= roomTypeId %>" 
                                                    aria-selected="<%= firstTab ? "true" : "false" %>">
                                                <%= roomTypeName %> 
                                                <span class="badge bg-<%= remaining > 0 ? "danger" : "success" %> ms-1">
                                                    <%= remaining %>
                                                </span>
                                            </button>
                                        </li>
                                        <% 
                                            firstTab = false;
                                        } 
                                        %>
                                    </ul>
                                    
                                    <div class="tab-content" id="roomTypeTabContent">
                                        <% 
                                            firstTab = true;
                                            for (Map.Entry<Integer, List<Room>> entry : availableRoomsByType.entrySet()) {
                                                Integer roomTypeId = entry.getKey();
                                                List<Room> rooms = entry.getValue();
                                                
                                                String roomTypeName = "";
                                                
                                                // Find room type name
                                                if (bookingRoomTypes != null) {
                                                    for (BookingRoomType brt : bookingRoomTypes) {
                                                        if (brt.getRoomTypeId() == roomTypeId) {
                                                            roomTypeName = brt.getRoomTypeName();
                                                            break;
                                                        }
                                                    }
                                                }
                                        %>
                                        <div class="tab-pane fade <%= firstTab ? "show active" : "" %>" 
                                             id="roomType-<%= roomTypeId %>" 
                                             role="tabpanel" 
                                             aria-labelledby="tab-<%= roomTypeId %>">
                                            
                                            <div class="room-type-section">
                                                <h5 class="mb-3"><i class="bi bi-door-closed"></i> <%= roomTypeName %> - Available Rooms</h5>
                                                
                                                <% if (rooms == null || rooms.isEmpty()) { %>
                                                    <div class="alert alert-warning">
                                                        <i class="bi bi-exclamation-triangle-fill"></i> No available rooms found for this room type during the selected dates.
                                                    </div>
                                                <% } else { %>
                                                    <div class="row row-cols-1 row-cols-md-3 row-cols-lg-4 g-4">
                                                        <% for (Room room : rooms) { %>
                                                            <div class="col">
                                                                <div class="card room-card">
                                                                    <span class="room-status-badge badge bg-success">Available</span>
                                                                    <div class="room-card-header">
                                                                        <% if (room.getImageUrl() != null && !room.getImageUrl().isEmpty()) { %>
                                                                            <img src="${pageContext.request.contextPath}/<%= room.getImageUrl() %>" alt="Room <%= room.getRoomNumber() %>">
                                                                        <% } else { %>
                                                                            <div class="d-flex align-items-center justify-content-center h-100">
                                                                                <i class="bi bi-door-closed fa-3x text-secondary"></i>
                                                                            </div>
                                                                        <% } %>
                                                                    </div>
                                                                    <div class="room-card-body">
                                                                        <h5 class="card-title">Room <%= room.getRoomNumber() %></h5>
                                                                        <p class="card-text text-muted"><%= room.getRoomTypeName() %></p>
                                                                        <form action="${pageContext.request.contextPath}/staff/room-assignment" method="post">
                                                                            <input type="hidden" name="action" value="assign-room">
                                                                            <input type="hidden" name="bookingId" value="<%= booking.getId() %>">
                                                                            <input type="hidden" name="roomId" value="<%= room.getId() %>">
                                                                            <button type="submit" class="btn btn-primary w-100">
                                                                                <i class="bi bi-plus-circle"></i> Assign
                                                                            </button>
                                                                        </form>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        <% } %>
                                                    </div>
                                                <% } %>
                                            </div>
                                        </div>
                                        <% 
                                            firstTab = false;
                                        } 
                                        %>
                                    </div>
                                <% } %>
                            </div>
                        </div>
                    <% } %>
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
                    <h6>How to Assign Rooms:</h6>
                    <ol>
                        <li>Select a room type tab from the available rooms section</li>
                        <li>Click the "Assign" button on any available room card</li>
                        <li>Repeat for all required room types until all rooms are assigned</li>
                        <li>When all rooms are assigned, click "Complete Assignment"</li>
                    </ol>
                    <h6>Notes:</h6>
                    <ul>
                        <li>You can remove a room assignment by clicking the X icon on the assigned room badge</li>
                        <li>The progress bars show how many rooms of each type have been assigned</li>
                        <li>The assignment cannot be completed until all required rooms are assigned</li>
                        <li>Once assignment is completed, the booking status will update automatically if needed</li>
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
                var alerts = document.querySelectorAll('.alert-dismissible');
                alerts.forEach(function(alert) {
                    var bsAlert = new bootstrap.Alert(alert);
                    bsAlert.close();
                });
            }, 5000);
        });
    </script>
</body>
</html>