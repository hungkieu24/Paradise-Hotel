<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="Model.*" %>
<%@ page import="Controller.Staff.StaffAssignRoomServlet.RoomTypeDisplayData" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    Booking booking = (Booking) request.getAttribute("booking");
    List<Room> assignedRooms = (List<Room>) request.getAttribute("assignedRooms");
    List<RoomTypeDisplayData> roomTypeDisplays = (List<RoomTypeDisplayData>) request.getAttribute("roomTypeDisplays");
    String errorMessage = (String) request.getAttribute("errorMessage");
    String warningMessage = (String) request.getAttribute("warningMessage");
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Assign Rooms</title>
    <link rel="stylesheet" href="css/style_1.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <style>
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
        .main-content {
            margin-left: 220px;
            padding: 0;
            min-height: 100vh;
            background: #f4f5fa;
        }
        .content-body {
            margin: 35px 40px 0 40px;
        }
        .room-card {
            border: 1px solid #ddd;
            border-radius: 8px;
            transition: all 0.3s;
            cursor: pointer;
        }
        .room-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        }
        .room-checkbox:checked + .room-card-content {
            background-color: #d4edda;
            border-color: #28a745;
        }
        .room-type-section {
            border: 1px solid #dee2e6;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 20px;
            background: white;
        }
        .room-type-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 15px 20px;
            border-radius: 8px;
            margin-bottom: 20px;
        }
        .quantity-info {
            background: #f8f9fa;
            border-left: 4px solid #007bff;
            padding: 15px 20px;
            margin-bottom: 15px;
            border-radius: 0 8px 8px 0;
        }
        .progress {
            height: 10px;
        }
        .assigned-room-card {
            background: linear-gradient(135deg, #28a745, #20c997);
            color: white;
            border: none;
        }
        .insufficient-rooms {
            border-color: #dc3545 !important;
            background-color: #f8d7da !important;
        }
        .completed-section {
            background-color: #d1f2eb !important;
            border-color: #00d4aa !important;
        }
    </style>
</head>
<body class="bg-light">
    <div class="app-container">
        <%@ include file="sidebar.jsp" %>
        <main class="main-content">
            <div class="content-body">
                <a href="staff-bookings-list" class="btn btn-outline-secondary mb-3">
                    <i class="bi bi-arrow-left"></i> Back to Booking List
                </a>

                <div class="card shadow-sm mb-4">
                    <div class="card-header bg-primary text-white">
                        <h3 class="mb-0"><i class="bi bi-door-open"></i> Assign Rooms for Booking</h3>
                    </div>
                    <div class="card-body">
                        <!-- Messages -->
                        <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
                        <div class="alert alert-danger alert-dismissible fade show">
                            <i class="bi bi-exclamation-triangle-fill me-2"></i> <%= errorMessage %>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <% } %>

                        <% if (warningMessage != null && !warningMessage.isEmpty()) { %>
                        <div class="alert alert-warning alert-dismissible fade show">
                            <i class="bi bi-exclamation-triangle-fill me-2"></i> <%= warningMessage %>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <% } %>

                        <% if (booking != null) { %>
                        <!-- Booking Information -->
                        <div class="card mb-4 bg-light">
                            <div class="card-body">
                                <h5 class="card-title"><i class="bi bi-info-circle"></i> Booking Information</h5>
                                <div class="row">
                                    <div class="col-md-6">
                                        <ul class="list-group list-group-flush">
                                            <li class="list-group-item bg-light"><strong>Booking ID:</strong> <%= booking.getId() %></li>
                                            <li class="list-group-item bg-light">
                                                <strong>Customer:</strong> 
                                                <%= booking.getFullName() != null && !booking.getFullName().isEmpty() 
                                                    ? booking.getFullName() 
                                                    : (booking.getUserName() != null ? booking.getUserName() : "N/A") %>
                                            </li>
                                        </ul>
                                    </div>
                                    <div class="col-md-6">
                                        <ul class="list-group list-group-flush">
                                            <li class="list-group-item bg-light">
                                                <strong>Check-in:</strong> 
                                                <%= booking.getCheckIn() != null ? dateFormat.format(booking.getCheckIn()) : "Not specified" %>
                                            </li>
                                            <li class="list-group-item bg-light">
                                                <strong>Check-out:</strong> 
                                                <%= booking.getCheckOut() != null ? dateFormat.format(booking.getCheckOut()) : "Not specified" %>
                                            </li>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Currently Assigned Rooms -->
                        <% if (assignedRooms != null && !assignedRooms.isEmpty()) { %>
                        <div class="card mb-4">
                            <div class="card-header bg-success text-white">
                                <h5 class="mb-0"><i class="bi bi-pin-map-fill"></i> Currently Assigned Rooms (<%= assignedRooms.size() %>)</h5>
                            </div>
                            <div class="card-body">
                                <div class="row">
                                    <% for (Room room : assignedRooms) { %>
                                    <div class="col-md-4 mb-3">
                                        <div class="room-card assigned-room-card p-3">
                                            <h5 class="mb-1"><%= room.getRoomNumber() %></h5>
                                            <p class="mb-1">
                                                <strong>Type:</strong> <%= room.getRoomTypeName() != null ? room.getRoomTypeName() : "Unknown" %>
                                            </p>
                                            <div class="d-flex justify-content-between align-items-center">
                                                <span class="badge bg-light text-dark">✓ Assigned</span>
                                                <small>ID: <%= room.getId() %></small>
                                            </div>
                                        </div>
                                    </div>
                                    <% } %>
                                </div>
                            </div>
                        </div>
                        <% } %>

                        <!-- Room Assignment Form -->
                        <form action="staff-assign-room-action" method="post" onsubmit="return validateForm()">
                            <input type="hidden" name="bookingId" value="<%= booking.getId() %>">

                            <!-- Room Types - Processed by Servlet -->
                            <% if (roomTypeDisplays != null && !roomTypeDisplays.isEmpty()) { %>
                            <% for (RoomTypeDisplayData display : roomTypeDisplays) { %>
                            <div class="room-type-section <%= display.isInsufficient() ? "insufficient-rooms" : "" %> <%= display.isCompleted() ? "completed-section" : "" %>">
                                <div class="room-type-header">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <h4 class="mb-0">
                                            <i class="bi bi-house-door"></i> <%= display.getRoomTypeName() %>
                                            <% if (display.isCompleted()) { %>
                                            <i class="bi bi-check-circle-fill text-success"></i>
                                            <% } %>
                                        </h4>
                                        <span class="badge bg-light text-dark fs-6">Type ID: <%= display.getRoomTypeId() %></span>
                                    </div>
                                </div>

                                <!-- Quantity Information -->
                                <div class="quantity-info">
                                    <div class="row">
                                        <div class="col-md-8">
                                            <div class="row">
                                                <div class="col-4">
                                                    <strong>Required:</strong> <span class="badge bg-primary"><%= display.getRequired() %></span>
                                                </div>
                                                <div class="col-4">
                                                    <strong>Assigned:</strong> <span class="badge bg-success"><%= display.getAssigned() %></span>
                                                </div>
                                                <div class="col-4">
                                                    <strong>Still Need:</strong> <span class="badge <%= display.getRemaining() > 0 ? "bg-warning text-dark" : "bg-success" %>"><%= display.getRemaining() %></span>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-md-4">
                                            <div class="progress">
                                                <div class="progress-bar <%= display.getProgressPercent() == 100 ? "bg-success" : "bg-info" %>" 
                                                     style="width: <%= display.getProgressPercent() %>%">
                                                    <%= display.getProgressPercent() %>%
                                                </div>
                                            </div>
                                            <small class="text-muted">Progress: <%= display.getAssigned() %>/<%= display.getRequired() %></small>
                                        </div>
                                    </div>
                                </div>

                                <% if (display.isInsufficient()) { %>
                                <div class="alert alert-danger">
                                    <i class="bi bi-exclamation-triangle-fill"></i> 
                                    <strong>Insufficient Rooms!</strong> Need <%= display.getRemaining() %> more but only <%= display.getAvailableRooms().size() %> available.
                                </div>
                                <% } %>

                                <!-- Available Rooms -->
                                <% if (display.isCompleted()) { %>
                                <div class="alert alert-success">
                                    <i class="bi bi-check-circle-fill"></i> 
                                    All required rooms for <strong><%= display.getRoomTypeName() %></strong> have been assigned!
                                </div>
                                <% } else if (display.getAvailableRooms() != null && !display.getAvailableRooms().isEmpty()) { %>
                                <h6 class="mb-3">
                                    <i class="bi bi-door-open"></i> Available Rooms 
                                    <span class="badge bg-info"><%= display.getAvailableRooms().size() %></span>
                                    <small class="text-muted">(Select up to <%= display.getRemaining() %> room<%= display.getRemaining() > 1 ? "s" : "" %>)</small>
                                </h6>

                                <div class="row">
                                    <% for (Room room : display.getAvailableRooms()) { %>
                                    <div class="col-md-4 mb-3">
                                        <label class="room-card p-3 d-block">
                                            <input type="checkbox" name="roomIds" value="<%= room.getId() %>" 
                                                   class="room-checkbox me-2" style="display: none;">
                                            <div class="room-card-content">
                                                <div class="d-flex justify-content-between align-items-center mb-2">
                                                    <h5 class="mb-0"><%= room.getRoomNumber() %></h5>
                                                    <span class="badge bg-success">Available</span>
                                                </div>
                                                <p class="mb-1 text-muted">
                                                    <strong>Type:</strong> <%= display.getRoomTypeName() %>
                                                </p>
                                                <small class="text-muted">Room ID: <%= room.getId() %></small>
                                            </div>
                                        </label>
                                    </div>
                                    <% } %>
                                </div>
                                <% } else { %>
                                <div class="alert alert-warning">
                                    <i class="bi bi-exclamation-triangle"></i> 
                                    No available rooms found for room type: <strong><%= display.getRoomTypeName() %></strong>
                                </div>
                                <% } %>
                            </div>
                            <% } %>
                            <% } else { %>
                            <div class="alert alert-danger">
                                <i class="bi bi-exclamation-circle-fill"></i> 
                                No room types found for this booking!
                            </div>
                            <% } %>

                            <!-- Submit Button -->
                            <div class="d-flex justify-content-between mt-4">
                                <div>
                                    <button type="submit" class="btn btn-success btn-lg me-2">
                                        <i class="bi bi-check2-square"></i> Confirm Assign & Check-in
                                    </button>
                                    <a href="staff-bookings-list" class="btn btn-outline-secondary btn-lg">
                                        <i class="bi bi-x-lg"></i> Cancel
                                    </a>
                                </div>
                                <div>
                                    <button type="button" class="btn btn-outline-info" onclick="window.location.reload()">
                                        <i class="bi bi-arrow-clockwise"></i> Refresh
                                    </button>
                                </div>
                            </div>
                        </form>
                        <% } else { %>
                        <div class="alert alert-danger">
                            <i class="bi bi-exclamation-circle-fill"></i> Booking not found!
                        </div>
                        <% } %>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Minimal JavaScript - chỉ validation và UI feedback
        function validateForm() {
            const checkboxes = document.querySelectorAll('input[name="roomIds"]:checked');
            if (checkboxes.length === 0) {
                alert('Please select at least one room to assign.');
                return false;
            }
            return confirm('Are you sure you want to assign the selected rooms and proceed with check-in?');
        }

        // Simple checkbox styling
        document.addEventListener('DOMContentLoaded', function() {
            const checkboxes = document.querySelectorAll('.room-checkbox');
            checkboxes.forEach(function(checkbox) {
                checkbox.addEventListener('change', function() {
                    const content = this.nextElementSibling;
                    if (this.checked) {
                        content.style.backgroundColor = '#d4edda';
                        content.style.borderColor = '#28a745';
                        content.style.borderWidth = '2px';
                    } else {
                        content.style.backgroundColor = '';
                        content.style.borderColor = '';
                        content.style.borderWidth = '';
                    }
                });
            });
        });
    </script>
</body>
</html>