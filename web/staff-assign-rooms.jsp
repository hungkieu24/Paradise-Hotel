<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="Model.UserAccount, Model.Booking, Model.BookingRoomType, Model.Room, Model.BookingService, Model.Service, java.util.Map, java.util.List" %>
<%
    // Lấy dữ liệu từ request
    Booking booking = (Booking) request.getAttribute("booking");
    List<BookingRoomType> bookingRoomTypes = (List<BookingRoomType>) request.getAttribute("bookingRoomTypes");
    List<Room> assignedRooms = (List<Room>) request.getAttribute("assignedRooms");
    Map<Integer, Integer> assignmentCounts = (Map<Integer, Integer>) request.getAttribute("assignmentCounts");
    Map<Integer, List<Room>> availableRoomsByType = (Map<Integer, List<Room>>) request.getAttribute("availableRoomsByType");
    Boolean isFullyAssigned = (Boolean) request.getAttribute("isFullyAssigned");
    List<BookingService> bookingServices = (List<BookingService>) request.getAttribute("bookingServices");
    List<Service> availableServices = (List<Service>) request.getAttribute("availableServices");
    String contextPath = request.getContextPath();
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
            .filter-section {
                background: white;
                padding: 20px;
                border-radius: 10px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                margin-bottom: 20px;
            }
        </style>
    </head>
    <body class="bg-light">
        <div class="app-container">
            <jsp:include page="sidebar.jsp"/>
            <main class="main-content">
                <div class="container-fluid">
                    <!-- Messages -->
                    <c:if test="${not empty param.error}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="bi bi-exclamation-triangle-fill me-2"></i>
                            <c:choose>
                                <c:when test="${param.error == 'failed'}">Failed to assign room. The room might be unavailable or already assigned.</c:when>
                                <c:when test="${param.error == 'remove-failed'}">Failed to remove room assignment.</c:when>
                                <c:when test="${param.error == 'incomplete'}">Cannot complete. Not all rooms have been assigned.</c:when>
                                <c:when test="${param.error == 'service-add-failed'}">Failed to add service.</c:when>
                                <c:when test="${param.error == 'service-remove-failed'}">Failed to remove service.</c:when>
                                <c:otherwise>An error occurred: ${param.error}</c:otherwise>
                            </c:choose>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>
                    <c:if test="${not empty param.success}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="bi bi-check-circle-fill me-2"></i>
                            <c:choose>
                                <c:when test="${param.success == 'completion'}">Assignment completed! Booking is ready for check-in.</c:when>
                                <c:when test="${param.success == 'service-added'}">Service added successfully.</c:when>
                                <c:when test="${param.success == 'service-removed'}">Service removed successfully.</c:when>
                                <c:otherwise>Operation successful: ${param.success}</c:otherwise>
                            </c:choose>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <!-- Header -->
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <div>
                            <h2 class="mb-0">Room Assignment</h2>
                            <p class="text-muted">Booking #${booking.id}</p>
                        </div>
                        <a href="<%= contextPath %>/staff-room-assignments-view" class="btn btn-outline-secondary"><i class="bi bi-arrow-left"></i> Back to List</a>
                    </div>

                    <div class="row g-4">
                        <div class="col-lg-8">
                            <!-- Left Column: Assignment Details -->
                            <div class="card shadow-sm">
                                <div class="card-header bg-white">
                                    <h5 class="mb-0"><i class="bi bi-door-open-fill me-2 text-primary"></i>Available Rooms for Assignment</h5>
                                </div>
                                <div class="card-body">
                                    <c:forEach var="roomType" items="${allSystemRoomTypes}">
                                        
                                        <c:set var="required" value="${requiredQuantities.get(roomType.roomTypeID) != null ? requiredQuantities.get(roomType.roomTypeID) : 0}" />
                                        <c:set var="assigned" value="${assignmentCounts.get(roomType.roomTypeID) != null ? assignmentCounts.get(roomType.roomTypeID) : 0}" />
                                        <c:set var="needed" value="${required - assigned}" />

                                        <div class="room-type-section ${needed > 0 ? 'needs-assignment' : ''}">
                                            <div class="d-flex justify-content-between align-items-center mb-3">
                                              
                                                <h6 class="mb-0">${roomType.name}</h6>
                                                <c:if test="${required > 0}">
                                                    <span class="badge ${needed > 0 ? 'bg-warning text-dark' : 'bg-success'}">
                                                        Required: ${required} | Assigned: ${assigned}
                                                    </span>
                                                </c:if>
                                            </div>

                                            <c:set var="availableRooms" value="${availableRoomsByAllTypes.get(roomType.roomTypeID)}" />
                                            <c:choose>
                                                <c:when test="${not empty availableRooms}">
                                                    <div class="row g-3">
                                                        <c:forEach var="room" items="${availableRooms}">
                                                            <div class="col-md-4 col-sm-6">
                                                                <div class="card room-card text-center">
                                                                    <div class="card-body">
                                                                        <h5 class="card-title">
                                                                            <i class="bi bi-key-fill text-muted"></i> Room ${room.roomNumber}
                                                                        </h5>
                                                                        <c:choose>
                                                                            <c:when test="${room.assigned}">
                                                                                <%-- Nếu phòng đã được gán --%>
                                                                                <form action="<%= contextPath %>/staff-room-assignment" method="post" onsubmit="return confirm('Remove this room assignment?')">
                                                                                    <input type="hidden" name="action" value="remove-assignment">
                                                                                    <input type="hidden" name="bookingId" value="${booking.id}">
                                                                                    <input type="hidden" name="roomId" value="${room.id}">
                                                                                    <button type="submit" class="btn btn-sm btn-outline-danger btn-assign">Remove Assignment</button>
                                                                                </form>
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <%-- Nếu phòng chưa được gán --%>
                                                                                <form action="<%= contextPath %>/staff-room-assignment" method="post">
                                                                                    <input type="hidden" name="action" value="assign-room">
                                                                                    <input type="hidden" name="bookingId" value="${booking.id}">
                                                                                    <input type="hidden" name="roomId" value="${room.id}">
                                                                                    <button type="submit" class="btn btn-sm btn-outline-success btn-assign">Assign this Room</button>
                                                                                </form>
                                                                            </c:otherwise>
                                                                        </c:choose>

                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </c:forEach>

                                                    </div>
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="alert alert-light border-dashed text-center p-2">No available rooms of this type at this branch.</div>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>
                        </div>

                        <!-- Right Column: Booking & Service Details -->
                        <div class="col-lg-4">
                            <!-- Booking Details -->
                            <div class="card shadow-sm mb-4">
                                <div class="card-header bg-white">
                                    <h5 class="mb-0"><i class="bi bi-file-earmark-text-fill me-2 text-info"></i>Booking Details</h5>
                                </div>
                                <div class="card-body">
                                    <p><strong>Customer:</strong> ${booking.fullName != null && !booking.fullName.isEmpty() ? booking.fullName : booking.userName}</p>
                                    <p><strong>Check-in:</strong> <fmt:formatDate value="${booking.checkIn}" pattern="dd/MM/yyyy HH:mm"/></p>
                                    <p><strong>Check-out:</strong> <fmt:formatDate value="${booking.checkOut}" pattern="dd/MM/yyyy HH:mm"/></p>
                                    <p><strong>Payment:</strong> <span class="badge bg-${booking.paymentStatus.equalsIgnoreCase('Paid') ? 'success' : 'warning text-dark'}">${booking.paymentStatus}</span></p>
                                    <p><strong>Status:</strong> <span class="badge bg-primary">${booking.status}</span></p>
                                    <p><strong>Total:</strong> <strong class="text-danger"><fmt:formatNumber value="${booking.totalPrice}" type="currency" currencySymbol="" maxFractionDigits="0"/> đ</strong></p>
                                </div>
                            </div>

                            <!-- Services -->
                            <div class="services-section shadow-sm">
                                <div class="d-flex justify-content-between align-items-center mb-3">
                                    <h5 class="mb-0"><i class="bi bi-cart-fill me-2"></i>Booking Services</h5>
                                    <button type="button" class="btn btn-sm btn-info" data-bs-toggle="modal" data-bs-target="#addServiceModal"><i class="bi bi-plus-circle"></i> Add</button>
                                </div>
                                <c:if test="${empty bookingServices}">
                                    <div class="alert alert-light border-dashed text-center">No services added.</div>
                                </c:if>
                                <c:if test="${not empty bookingServices}">
                                    <div class="table-responsive">
                                        <table class="table table-sm">
                                            <tbody>
                                                <c:forEach var="bs" items="${bookingServices}">
                                                    <tr>
                                                        <td>${bs.serviceName} x${bs.quantity}</td>
                                                        <td class="text-end"><fmt:formatNumber value="${bs.servicePrice * bs.quantity}" type="currency" currencySymbol="" maxFractionDigits="0"/>đ</td>
                                                        <td>
                                                            <form action="<%= contextPath %>/staff-room-assignment" method="post" onsubmit="return confirm('Remove this service?')" class="d-inline">
                                                                <input type="hidden" name="action" value="remove-service">
                                                                <input type="hidden" name="bookingId" value="${booking.id}">
                                                                <input type="hidden" name="serviceId" value="${bs.serviceId}">
                                                                <button type="submit" class="btn btn-link text-danger p-0" style="line-height: 1;"><i class="bi bi-x-circle"></i></button>
                                                            </form>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </c:if>
                                <div class="d-flex justify-content-center mb-4">
                                    <form action="<%= contextPath %>/staff-room-assignment" method="post" class="text-center">
                                        <input type="hidden" name="action" value="complete-assignment">
                                        <input type="hidden" name="bookingId" value="${booking.id}">
                                        <button type="submit" class="btn btn-primary btn-lg">
                                            <i class="bi bi-plus-circle"></i>Complete Assign
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </main>
        </div>

        <!-- Add Service Modal -->
        <div class="modal fade" id="addServiceModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header bg-info text-white">
                        <h5 class="modal-title"><i class="bi bi-cart-plus"></i> Add Service to Booking</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <form id="serviceForm" action="<%= contextPath %>/staff-room-assignment" method="post">
                        <div class="modal-body">
                            <input type="hidden" name="action" value="add-service">
                            <input type="hidden" name="bookingId" value="${booking.id}">
                            <div class="mb-3">
                                <label for="serviceId" class="form-label">Select Service</label>
                                <select name="serviceId" id="serviceId" class="form-select" required>
                                    <option value="" disabled selected>-- Choose a service --</option>
                                    <c:forEach var="service" items="${availableServices}">
                                        <option value="${service.id}">${service.name} - <fmt:formatNumber value="${service.price}" type="currency" currencySymbol="" maxFractionDigits="0"/> đ</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label for="quantity" class="form-label">Quantity</label>
                                <input type="number" name="quantity" id="quantity" class="form-control" value="1" min="1" required>
                            </div>
                            <div class="mb-3">
                                <label for="paidStatus" class="form-label">Payment Status</label>
                                <select name="paidStatus" id="paidStatus" class="form-select">
                                    <option value="Unpaid" selected>Unpaid</option>
                                    <option value="Paid">Paid</option>
                                </select>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                            <button type="submit" class="btn btn-primary"><i class="bi bi-plus-circle"></i> Add Service</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>