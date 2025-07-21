<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="Model.UserAccount, Model.Room, Model.RoomType, java.util.List, java.util.Map" %>
<%
    String staffName = "";
    if (session.getAttribute("user") != null) {
        staffName = ((UserAccount)session.getAttribute("user")).getUsername();
    }
    
    // Lấy dữ liệu từ request
    List<UserAccount> searchResults = (List<UserAccount>) request.getAttribute("searchResults");
    List<RoomType> roomTypes = (List<RoomType>) request.getAttribute("roomTypes");
    Map<Integer, List<Room>> availableRoomsByAllTypes = (Map<Integer, List<Room>>) request.getAttribute("availableRoomsByAllTypes");
    String contextPath = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Advanced Guest Search & Room Assignment</title>
        <link rel="stylesheet" href="css/style_1.css">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <style>
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
            /* Make logout button red */
            .sidebar-menu .menu-item.logout {
                color: #fa5252 !important;
                font-weight: 600;
            }
            .sidebar-menu .menu-item.logout i {
                color: #fa5252 !important;
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
            .search-section {
                background: white;
                padding: 25px;
                border-radius: 10px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                margin-bottom: 25px;
            }
            .results-section {
                background: white;
                padding: 25px;
                border-radius: 10px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                margin-bottom: 25px;
            }
            .user-card {
                border: 1px solid #e0e0e0;
                border-radius: 8px;
                padding: 15px;
                margin-bottom: 15px;
                transition: all 0.3s;
            }
            .user-card:hover {
                border-color: #007bff;
                box-shadow: 0 4px 12px rgba(0,123,255,0.15);
            }
            .user-card.selected {
                border-color: #28a745;
                background-color: #f8fff9;
            }
            .room-assignment-section {
                background: white;
                padding: 25px;
                border-radius: 10px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                display: none;
            }
            .room-card {
                border: 2px solid #dee2e6;
                border-radius: 8px;
                padding: 15px;
                margin: 8px;
                cursor: pointer;
                transition: all 0.3s;
                text-align: center;
                min-width: 120px;
                position: relative;
            }
            .room-card:hover {
                border-color: #007bff;
                background-color: #f8f9fa;
                transform: translateY(-2px);
                box-shadow: 0 4px 8px rgba(0,0,0,0.1);
            }
            .room-card.selected {
                border-color: #28a745;
                background-color: #d4edda;
                transform: translateY(-2px);
                box-shadow: 0 4px 12px rgba(40,167,69,0.3);
            }
            .room-card.selected::after {
                content: '✓';
                position: absolute;
                top: 5px;
                right: 8px;
                color: #28a745;
                font-weight: bold;
                font-size: 16px;
            }
            .no-results {
                text-align: center;
                padding: 40px;
                color: #6c757d;
            }
            .advanced-search {
                display: none;
            }
            .search-toggle {
                color: #007bff;
                cursor: pointer;
                text-decoration: none;
            }
            .search-toggle:hover {
                text-decoration: underline;
            }
            .form-check-input:checked {
                background-color: #28a745;
                border-color: #28a745;
            }
            .room-type-checkbox {
                transform: scale(1.2);
            }
            #selectedRoomsList .badge {
                position: relative;
            }
            #confirmAssignmentBtn:disabled {
                opacity: 0.6;
                cursor: not-allowed;
            }
            .room-type-section {
                border: 1px solid #dee2e6;
                border-radius: 8px;
                padding: 20px;
                margin-bottom: 20px;
                background-color: #f8f9fa;
            }
        </style>
    </head>
    <body>
        <div class="app-container">
            <!-- Sidebar -->
            <%@ include file="sidebar.jsp" %>

            <!-- Main Content -->
            <main class="main-content">
                <header class="content-header">
                    <div class="header-left">
                        <h1 class="page-title">Advanced Guest Search & Room Assignment</h1>
                    </div>
                    <div class="header-right">
                        <div class="user-info">
                            <i class="fas fa-user-circle"></i>
                            <span><%= staffName != null && !staffName.isEmpty() ? staffName : "staff" %></span>
                        </div>
                    </div>
                </header>

                <div class="content-body">
                    <!-- Messages -->
                    <c:if test="${not empty param.success}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="bi bi-check-circle-fill me-2"></i>
                            <c:choose>
                                <c:when test="${param.success == 'user-created'}">New user created successfully!</c:when>
                                <c:when test="${param.success == 'booking-created'}">Booking created successfully!</c:when>
                                <c:otherwise>Operation completed successfully!</c:otherwise>
                            </c:choose>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <c:if test="${not empty param.error}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="bi bi-exclamation-triangle-fill me-2"></i>
                            <c:choose>
                                <c:when test="${param.error == 'user-creation-failed'}">Failed to create new user. Please try again.</c:when>
                                <c:when test="${param.error == 'missing-required-fields'}">Please fill in all required fields.</c:when>
                                <c:when test="${param.error == 'booking-creation-failed'}">Failed to create booking. Please try again.</c:when>
                                <c:otherwise>An error occurred: ${param.error}</c:otherwise>
                            </c:choose>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <!-- Search Section -->
                    <div class="search-section">
                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <h4 class="mb-0"><i class="bi bi-search me-2"></i>Search Guest</h4>
                            <a href="#" class="search-toggle" onclick="toggleAdvancedSearch()">
                                <i class="bi bi-gear-fill me-1"></i>Advanced Search
                            </a>
                        </div>

                        <form method="get" action="<%= contextPath %>/searchGuest" id="searchForm">
                            <!-- Basic Search -->
                            <div class="basic-search">
                                <div class="row">
                                    <div class="col-md-8">
                                        <label class="form-label">Email or Phone Number:</label>
                                        <input type="text" class="form-control" name="keyword"
                                               value="${param.keyword != null ? param.keyword : ''}"
                                               placeholder="Enter email or phone number">
                                    </div>
                                    <div class="col-md-4 d-flex align-items-end">
                                        <button type="submit" class="btn btn-primary w-100">
                                            <i class="bi bi-search me-1"></i>Search
                                        </button>
                                    </div>
                                </div>
                            </div>

                            <!-- Advanced Search -->
                            <div class="advanced-search" id="advancedSearch">
                                <hr class="my-4">
                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <label class="form-label">Full Name:</label>
                                        <input type="text" class="form-control" name="fullName"
                                               value="${param.fullName != null ? param.fullName : ''}"
                                               placeholder="Enter full name">
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label">Username:</label>
                                        <input type="text" class="form-control" name="username"
                                               value="${param.username != null ? param.username : ''}"
                                               placeholder="Enter username">
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label">Email:</label>
                                        <input type="email" class="form-control" name="email"
                                               value="${param.email != null ? param.email : ''}"
                                               placeholder="Enter email address">
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label">Account Status:</label>
                                        <select class="form-select" name="status">
                                            <option value="">All Status</option>
                                            <option value="Active" ${param.status == 'Active' ? 'selected' : ''}>Active</option>
                                            <option value="Inactive" ${param.status == 'Inactive' ? 'selected' : ''}>Inactive</option>
                                            <option value="Blocked" ${param.status == 'Blocked' ? 'selected' : ''}>Blocked</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>

                    <!-- Search Results Section -->
                    <div class="results-section">
                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <h4 class="mb-0"><i class="bi bi-people-fill me-2"></i>Search Results</h4>
                            <c:if test="${(empty searchResults || searchResults.size() == 0) && (not empty param.keyword || not empty param.fullName || not empty param.email || not empty param.phone)}">
                                <button type="button" class="btn btn-success" data-bs-toggle="modal" data-bs-target="#createUserModal">
                                    <i class="bi bi-person-plus-fill me-1"></i>Create New User
                                </button>
                            </c:if>
                        </div>

                        <c:choose>
                            <c:when test="${not empty searchResults && searchResults.size() > 0}">
                                <div class="row">
                                    <c:forEach var="user" items="${searchResults}" varStatus="status">
                                        <div class="col-md-6 col-lg-4">
                                            <div class="user-card" onclick="selectUser(${user.id}, '${user.email}', '${user.phonenumber}')">
                                                <div class="d-flex justify-content-between align-items-start mb-2">
                                                    <h6 class="mb-0">${user.username}</h6>
                                                    <span class="badge bg-${user.status == 'Active' ? 'success' : user.status == 'Inactive' ? 'warning' : 'danger'}">${user.status}</span>
                                                </div>
                                                <p class="text-muted mb-1"><i class="bi bi-envelope me-1"></i>${user.email}</p>
                                                <p class="text-muted mb-1"><i class="bi bi-telephone me-1"></i>${user.phonenumber}</p>
                                                    <c:if test="${not empty user.id}">
                                                    <p class="text-muted mb-0"><i class="bi bi-card-text me-1"></i>ID: ${user.id}</p>
                                                </c:if>
                                                <div class="mt-2">
                                                    <button type="button" class="btn btn-sm btn-outline-primary" onclick="event.stopPropagation(); selectUserForAssignment('${user.id}', '${user.email}')">
                                                        <i class="bi bi-key-fill me-1"></i>Assign Room
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:when test="${not empty param.keyword || not empty param.fullName || not empty param.email || not empty param.phone}">
                                <div class="no-results">
                                    <i class="bi bi-search display-1 text-muted"></i>
                                    <h5 class="mt-3">No users found</h5>
                                    <p class="text-muted">No users match your search criteria. Would you like to create a new user?</p>
                                    <button type="button" class="btn btn-success" data-bs-toggle="modal" data-bs-target="#createUserModal">
                                        <i class="bi bi-person-plus-fill me-1"></i>Create New User
                                    </button>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="no-results">
                                    <i class="bi bi-people display-1 text-muted"></i>
                                    <h5 class="mt-3">Search for guests</h5>
                                    <p class="text-muted">Use the search form above to find existing guests or create new ones.</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Room Assignment Section -->
                    <div class="room-assignment-section" id="roomAssignmentSection">
                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <div>
                                <h4 class="mb-0"><i class="bi bi-key-fill me-2"></i>Room Assignment</h4>
                                <p class="text-muted mb-0">Assigning rooms for: <span id="selectedUserName"></span></p>
                            </div>
                            <button type="button" class="btn btn-outline-secondary" onclick="hideRoomAssignment()">
                                <i class="bi bi-x-lg"></i> Cancel
                            </button>
                        </div>

                        <form id="roomAssignmentForm" method="post" action="<%= contextPath %>/searchGuest">
                            <input type="hidden" name="action" value="create-booking">
                            <input type="hidden" name="userId" id="selectedUserId">

                            <div class="row">
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label class="form-label">Check-in Date & Time:</label>
                                        <input type="datetime-local" class="form-control" name="checkIn" required>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label class="form-label">Check-out Date & Time:</label>
                                        <input type="datetime-local" class="form-control" name="checkOut" required>
                                    </div>
                                </div>
                            </div>

                           <!-- Room Type Selection -->
<div class="mb-4">
    <label class="form-label">Select Room Types:</label>
    <div class="row">
        <c:forEach var="roomType" items="${roomTypes}">
            <div class="col-md-4 mb-2">
                <div class="form-check">
                    <input class="form-check-input room-type-checkbox" type="checkbox" 
                           value="${roomType.roomTypeID}" id="roomType${roomType.roomTypeID}"
                           onchange="toggleRoomTypeSelection(${roomType.roomTypeID});fetchBookedQuantity(${roomType.roomTypeID});">
                    <label class="form-check-label" for="roomType${roomType.roomTypeID}">
                        ${roomType.name}
                        - <i class="bi bi-people-fill"></i> ${roomType.capacity_adult} Adults, 
                        <i class="bi bi-person-fill"></i> ${roomType.capacity_child} Children, 
                        <i class="bi bi-currency-dollar"></i> 
                        <br/>
                        <span>
                            <fmt:formatNumber value="${roomType.base_price} " pattern="#,##0" />
                        </span>
                        <br/>
                        <span>Booked: <span id="bookedQty${roomType.roomTypeID}"></span></span>
                    </label>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

                            <!-- Selected Rooms Display -->
                            <div class="mb-4" id="selectedRoomsDisplay" style="display: none;">
                                <label class="form-label">Selected Rooms:</label>
                                <div id="selectedRoomsList" class="border rounded p-3 bg-light">
                                    <!-- Selected rooms will be displayed here -->
                                </div>
                            </div>

                            <!-- Room Selection by Type -->
                            <div id="roomSelectionContainer">
                                <!-- Room selection sections will be dynamically added here -->
                            </div>

                            <!-- Hidden inputs for selected rooms -->
                            <div id="hiddenRoomInputs">
                                <!-- Hidden inputs for selected room IDs will be added here -->
                            </div>

                            <div class="row">
                                <!-- Note -->
                                <div class="col-md-12">
                                    <div class="mb-3">
                                        <label class="form-label">Note</label>
                                        <textarea class="form-control" name="specialRequests" rows="2" placeholder="Any special requests..."></textarea>
                                    </div>
                                </div>
                            </div>

                            <div class="mb-4">
                                <label class="form-label">Add Booking Services:</label>
                                <div id="servicesContainer"></div>
                                <button type="button" class="btn btn-outline-primary btn-sm mt-2" onclick="addServiceRow()">+ Add Service</button>
                            </div>

                            <input type="hidden" name="serviceCount" id="serviceCount">

                            <div class="text-center">
                                <button type="submit" class="btn btn-success btn-lg" id="confirmAssignmentBtn" disabled>
                                    <i class="bi bi-check-circle-fill me-1"></i>Confirm Room Assignment
                                    <span id="roomCountBadge" class="badge bg-white text-success ms-2" style="display: none;">0</span>
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </main>
        </div>

        <!-- Create User Modal -->
        <div class="modal fade" id="createUserModal" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header bg-success text-white">
                        <h5 class="modal-title"><i class="bi bi-person-plus-fill me-2"></i>Create New User</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <form method="post" action="<%= contextPath %>/searchGuest" id="createUserForm">
                        <input type="hidden" name="action" value="create-user">
                        <div class="modal-body">
                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label class="form-label">Full Name *</label>
                                    <input type="text" class="form-control" name="fullName" required>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label">Username *</label>
                                    <input type="text" class="form-control" name="username" required>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label">Email *</label>
                                    <input type="email" class="form-control" name="email" required>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label">Password *</label>
                                    <input type="password" class="form-control" name="password" required>
                                </div>
                                <div class="col-12">
                                    <div class="form-check">
                                        <input class="form-check-input" type="checkbox" name="assignRoomAfterCreation" id="assignRoomAfterCreation">
                                        <label class="form-check-label" for="assignRoomAfterCreation">
                                            Assign room immediately after creating user
                                        </label>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            <button type="submit" class="btn btn-success">
                                <i class="bi bi-person-plus-fill me-1"></i>Create User
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script>
                                    let serviceIndex = 0;
                                    function addServiceRow() {
                                    const container = document.getElementById('servicesContainer');
                                    const row = document.createElement('div');
                                    row.className = 'row g-2 mb-2 align-items-end';
                                    row.id = 'serviceRow' + serviceIndex;
                                    var html = ''
                                            + '<div class="col-md-4">'
                                            + '<label class="form-label">Service</label>'
                                            + '<select name="serviceId' + serviceIndex + '" class="form-select" required>'
                                            + '<c:forEach var="service" items="${availableServices}">'
                                            + '<option value="${service.id}">${service.name}</option>'
                                            + '</c:forEach>'
                                            + '</select>'
                                            + '</div>'
                                            + '<div class="col-md-3">'
                                            + '<label class="form-label">Quantity</label>'
                                            + '<input type="number" name="quantity' + serviceIndex + '" class="form-control" min="1" value="1" required>'
                                            + '</div>'
                                            + '<div class="col-md-3">'
                                            + '<label class="form-label">Payment Status</label>'
                                            + '<select name="paymentStatus' + serviceIndex + '" class="form-select" required>'
                                            + '<option value="Paid">Paid</option>'
                                            + '<option value="Unpaid">Unpaid</option>'
                                            + '</select>'
                                            + '</div>'
                                            + '<div class="col-md-2">'
                                            + '<button type="button" class="btn btn-danger" onclick="removeServiceRow(' + serviceIndex + ')">Remove</button>'
                                            + '</div>';
                                    row.innerHTML = html;
                                    container.appendChild(row);
                                    serviceIndex++;
                                    document.getElementById('serviceCount').value = serviceIndex;
                                    }

                                    function removeServiceRow(index) {
                                    const row = document.getElementById('serviceRow' + index);
                                    if (row) row.remove();
                                    }

                                    const availableRoomsByType = {
            <c:forEach var="entry" items="${availableRoomsByAllTypes}" varStatus="status">
                ${entry.key}: [
                <c:forEach var="room" items="${entry.value}" varStatus="roomStatus">
                                    {
                                    id: ${room.id},
                                            number: '${room.roomNumber}',
                                            status: '${room.status}'
                                    }<c:if test="${!roomStatus.last}">,</c:if>
                </c:forEach>
                                    ]<c:if test="${!status.last}">,</c:if>
            </c:forEach>
                                    };
                                    const selectedRooms = new Map();
                                    const selectedRoomTypes = new Set();
                                    function toggleAdvancedSearch() {
                                    const advancedSearch = document.getElementById('advancedSearch');
                                    const isVisible = advancedSearch.style.display !== 'none';
                                    advancedSearch.style.display = isVisible ? 'none' : 'block';
                                    }

                                    function selectUser(userId, email, phone) {
                                    document.querySelectorAll('.user-card').forEach(card => {
                                    card.classList.remove('selected');
                                    });
                                    event.currentTarget.classList.add('selected');
                                    console.log('Selected user:', userId, email, phone);
                                    }

                                    function selectUserForAssignment(userId, email) {
                                    document.getElementById('selectedUserId').value = userId;
                                    document.getElementById('selectedUserName').textContent = '(' + email + ')';
                                    document.getElementById('roomAssignmentSection').style.display = 'block';
                                    document.getElementById('roomAssignmentSection').scrollIntoView({ behavior: 'smooth' });
                                    }

                                    function toggleRoomTypeSelection(roomTypeId) {
                                    const checkbox = document.getElementById('roomType' + roomTypeId);
                                    const container = document.getElementById('roomSelectionContainer');
                                    if (checkbox.checked) {
                                    selectedRoomTypes.add(roomTypeId);
                                    showRoomsForType(roomTypeId);
                                    } else {
                                    selectedRoomTypes.delete(roomTypeId);
                                    hideRoomsForType(roomTypeId);
                                    removeRoomsOfType(roomTypeId);
                                    }

                                    updateSelectedRoomsDisplay();
                                    }

                                    function showRoomsForType(roomTypeId) {
                                    const container = document.getElementById('roomSelectionContainer');
                                    const rooms = availableRoomsByType[roomTypeId] || [];
                                    const roomTypeName = document.querySelector('label[for="roomType' + roomTypeId + '"]').textContent;
                                    const roomSection = document.createElement('div');
                                    roomSection.id = 'roomSection' + roomTypeId;
                                    roomSection.className = 'room-type-section';
                                    let roomHtml = '<h6 class="mb-3"> <i class="bi bi-building me-2"></i>' + roomTypeName + '</h6>' +
                                            '<div class="d-flex flex-wrap gap-2" id="roomCards' + roomTypeId + '">';
                                    if (rooms.length === 0) {
                                    roomHtml += '<div class="text-center p-3 text-muted w-100">No available rooms of this type</div>';
                                    } else {
                                    rooms.forEach(room => {
                                    roomHtml += '<div class="room-card" id="roomCard' + room.id + '" onclick="toggleRoomSelection(' + room.id + ', \'' + room.number + '\', ' + roomTypeId + ')">' +
                                            '<div class="text-center">' +
                                            '<i class="bi bi-door-open-fill fs-4 text-primary"></i>' +
                                            '<div class="fw-bold mt-2">Room ' + room.number + '</div>' +
                                            '<small class="text-success">' + room.status + '</small>' +
                                            '</div>' +
                                            '</div>';
                                    });
                                    }

                                    roomHtml += '</div>';
                                    roomSection.innerHTML = roomHtml;
                                    container.appendChild(roomSection);
                                    }

                                    function hideRoomsForType(roomTypeId) {
                                    const roomSection = document.getElementById('roomSection' + roomTypeId);
                                    if (roomSection) {
                                    roomSection.remove();
                                    }
                                    }

                                    function toggleRoomSelection(roomId, roomNumber, roomTypeId) {
                                    const roomCard = document.getElementById('roomCard' + roomId);
                                    if (selectedRooms.has(roomId)) {
                                    selectedRooms.delete(roomId);
                                    roomCard.classList.remove('selected');
                                    } else {
                                    const roomData = {
                                    id: roomId,
                                            number: roomNumber,
                                            typeId: roomTypeId
                                    };
                                    selectedRooms.set(roomId, roomData);
                                    roomCard.classList.add('selected');
                                    }

                                    updateSelectedRoomsDisplay();
                                    updateHiddenInputs();
                                    updateConfirmButton();
                                    }

                                    function removeRoomsOfType(roomTypeId) {
                                    for (const [roomId, roomData] of selectedRooms) {
                                    if (roomData.typeId === roomTypeId) {
                                    selectedRooms.delete(roomId);
                                    }
                                    }
                                    updateHiddenInputs();
                                    }

                                    function updateSelectedRoomsDisplay() {
                                    const display = document.getElementById('selectedRoomsDisplay');
                                    const list = document.getElementById('selectedRoomsList');
                                    if (selectedRooms.size === 0) {
                                    display.style.display = 'none';
                                    return;
                                    }

                                    display.style.display = 'block';
                                    let html = '<div class="row">';
                                    for (const [roomId, roomData] of selectedRooms) {
                                    html += '<div class="col-auto mb-2">' +
                                            '<span class="badge bg-primary fs-6 p-2">' +
                                            'Room ' + roomData.number +
                                            '<button type="button" class="btn-close btn-close-white ms-2" ' +
                                            'onclick="removeSelectedRoom(' + roomId + ')" style="font-size: 0.7em;"></button>' +
                                            '</span>' +
                                            '</div>';
                                    }
                                    html += '</div>';
                                    list.innerHTML = html;
                                    }

                                    function removeSelectedRoom(roomId) {
                                    selectedRooms.delete(roomId);
                                    const roomCard = document.getElementById('roomCard' + roomId);
                                    if (roomCard) {
                                    roomCard.classList.remove('selected');
                                    }

                                    updateSelectedRoomsDisplay();
                                    updateHiddenInputs();
                                    updateConfirmButton();
                                    }

                                    function updateHiddenInputs() {
                                    const container = document.getElementById('hiddenRoomInputs');
                                    container.innerHTML = '';
                                    for (const [roomId, roomData] of selectedRooms) {
                                    const input = document.createElement('input');
                                    input.type = 'hidden';
                                    input.name = 'roomIds';
                                    input.value = roomId;
                                    container.appendChild(input);
                                    }
                                    }

                                    function updateConfirmButton() {
                                    const button = document.getElementById('confirmAssignmentBtn');
                                    const badge = document.getElementById('roomCountBadge');
                                    const roomCount = selectedRooms.size;
                                    if (roomCount > 0) {
                                    button.disabled = false;
                                    badge.style.display = 'inline';
                                    badge.textContent = roomCount;
                                    } else {
                                    button.disabled = true;
                                    badge.style.display = 'none';
                                    }
                                    }

                                    function hideRoomAssignment() {
                                    document.getElementById('roomAssignmentSection').style.display = 'none';
                                    selectedRooms.clear();
                                    selectedRoomTypes.clear();
                                    document.getElementById('roomAssignmentForm').reset();
                                    document.getElementById('selectedUserId').value = '';
                                    document.getElementById('roomSelectionContainer').innerHTML = '';
                                    document.getElementById('selectedRoomsDisplay').style.display = 'none';
                                    document.getElementById('hiddenRoomInputs').innerHTML = '';
                                    document.querySelectorAll('.room-type-checkbox').forEach(checkbox => {
                                    checkbox.checked = false;
                                    });
                                    updateConfirmButton();
                                    }

                                    document.addEventListener('DOMContentLoaded', function() {
                                    const now = new Date();
                                    const dateString = now.toISOString().slice(0, 16);
                                    const checkInInput = document.querySelector('input[name="checkIn"]');
                                    const checkOutInput = document.querySelector('input[name="checkOut"]');
                                    if (checkInInput) checkInInput.min = dateString;
                                    if (checkOutInput) checkOutInput.min = dateString;
                                    const urlParams = new URLSearchParams(window.location.search);
                                    if (urlParams.get('assignRoom') === 'true' && urlParams.get('userId')) {
                                    // Auto-select user logic can be implemented here
                                    }
                                    });
                                    function fetchBookedQuantity(roomTypeId) {
                                    const checkIn = document.querySelector('input[name="checkIn"]').value;
                                    const checkOut = document.querySelector('input[name="checkOut"]').value;
                                    if (!checkIn || !checkOut) {
                                    document.getElementById('bookedQty' + roomTypeId).innerText = "";
                                    return;
                                    }

                                    fetch('<%=request.getContextPath()%>/ajaxBookedQuantity?roomTypeId=' + roomTypeId + '&checkIn=' + encodeURIComponent(checkIn) + '&checkOut=' + encodeURIComponent(checkOut))
                                            .then(resp => resp.json())
                                            .then(data => {
                                            document.getElementById('bookedQty' + roomTypeId).innerText = data.bookedQuantity;
                                            })
                                            .catch(err => {
                                            document.getElementById('bookedQty' + roomTypeId).innerText = "Error";
                                            });
                                    }

                                    // Gắn sự kiện onchange cho input ngày
                                    document.addEventListener('DOMContentLoaded', function() {
                                    const checkInInput = document.querySelector('input[name="checkIn"]');
                                    const checkOutInput = document.querySelector('input[name="checkOut"]');
                                    [checkInInput, checkOutInput].forEach(input => {
                                    if (input) {
                                    input.addEventListener('change', function() {
                                    document.querySelectorAll('.room-type-checkbox').forEach(cb => {
                                    fetchBookedQuantity(cb.value);
                                    });
                                    });
                                    }
                                    });
                                    });
        </script>

    </body>
</html>  