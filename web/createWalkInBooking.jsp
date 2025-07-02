<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.UserAccount" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.RoomType" %>
<%@ page import="Model.Room" %>
<%
   
      
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Guest Search</title>
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
        .content-body {
            margin: 35px 40px 0 40px;
        }
        .form-container {
            max-width: 1000px;
            margin: 0 auto;
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
            <h1 class="page-title">Create Walk-in Booking</h1>
        </header>

        <div class="d-flex justify-content-start mt-3 ms-4">
            <a href="javascript:history.back()" class="btn btn-outline-secondary mb-3">
                <i class="bi bi-arrow-left"></i> Back
            </a>
        </div>

        <div class="content-body">
            <div class="form-container">
                <h4 class="mb-4 text-center">Create Walk-in Booking</h4>

                <% if (request.getAttribute("errorMsg") != null) { %>
                <div class="alert alert-danger"><%= request.getAttribute("errorMsg") %></div>
                <% } %>
                <% if (request.getAttribute("successMsg") != null) { %>
                <div class="alert alert-success"><%= request.getAttribute("successMsg") %></div>
                <% } %>

                <%
                    UserAccount guest = (UserAccount) request.getAttribute("guest");
                %>
                <h5>Guest Account Information</h5>
                <table class="table table-bordered mb-4">
                    <tr>
                        <th>Name</th>
                        <td><%= guest != null ? guest.getUsername() : "" %></td>
                    </tr>
                    <tr>
                        <th>Email</th>
                        <td><%= guest != null ? guest.getEmail() : "" %></td>
                    </tr>
                    <tr>
                        <th>Phone Number</th>
                        <td><%= guest != null ? guest.getPhonenumber() : "" %></td>
                    </tr>
                </table>

                <!-- FORM -->
                <form method="post" action="createWalkInBooking">
                    <input type="hidden" name="guestId" value="<%= guest != null ? guest.getId() : "" %>"/>

                    <div class="row mb-3">
                        <label class="col-sm-2 col-form-label">Room Type:</label>
                        <div class="col-sm-10">
                            <select name="roomTypeId" id="roomTypeId" class="form-select" required onchange="filterRooms()">
                                <option value="">-- Select room type --</option>
                                <%
                                    List<RoomType> roomTypes = (List<RoomType>) request.getAttribute("roomTypes");
                                    String selectedRoomTypeId = request.getParameter("roomTypeId");
                                    if (roomTypes != null)
                                    for (RoomType rt : roomTypes) {
                                %>
                                <option value="<%= rt.getRoomTypeID() %>" <%= (selectedRoomTypeId != null && selectedRoomTypeId.equals(String.valueOf(rt.getRoomTypeID()))) ? "selected" : "" %>>
                                    <%= rt.getName() %>
                                </option>
                                <% } %>
                            </select>
                        </div>
                    </div>

                    <div class="row mb-3">
                        <label class="col-sm-2 col-form-label">Room:</label>
                        <div class="col-sm-10">
                            <select name="roomId" id="roomId" class="form-select" required>
                                <option value="">-- Select room --</option>
                                <%
                                    List<Room> rooms = (List<Room>) request.getAttribute("rooms");
                                    String selectedRoomId = request.getParameter("roomId");
                                    if (rooms != null)
                                    for (Room r : rooms) {
                                %>
                                <option value="<%= r.getId() %>" data-roomtype="<%= r.getRoomTypeId() %>" <%= (selectedRoomId != null && selectedRoomId.equals(String.valueOf(r.getId()))) ? "selected" : "" %>>
                                    <%= r.getRoomNumber() %> (<%= r.getRoomTypeName() != null ? r.getRoomTypeName() : "" %>)
                                </option>
                                <% } %>
                            </select>
                        </div>
                    </div>

                    <div class="row mb-3">
                        <label class="col-sm-2 col-form-label">Check-in:</label>
                        <div class="col-sm-10">
                            <input type="datetime-local" name="checkIn" class="form-control" required
                                   value="<%= request.getParameter("checkIn") != null ? request.getParameter("checkIn") : "" %>"/>
                        </div>
                    </div>

                    <div class="row mb-4">
                        <label class="col-sm-2 col-form-label">Check-out:</label>
                        <div class="col-sm-10">
                            <input type="datetime-local" name="checkOut" class="form-control" required
                                   value="<%= request.getParameter("checkOut") != null ? request.getParameter("checkOut") : "" %>"/>
                        </div>
                    </div>

                    <button type="submit" class="btn btn-success w-100">Create Booking</button>
                </form>
            </div>
        </div>
    </main>
</div>

<!-- JS -->
<script>
    function filterRooms() {
        var typeId = document.getElementById("roomTypeId").value;
        var roomSelect = document.getElementById("roomId");
        for (var i = 0; i < roomSelect.options.length; i++) {
            var opt = roomSelect.options[i];
            if (opt.value === "") {
                opt.style.display = "";
                continue;
            }
            opt.style.display = (typeId === "" || opt.getAttribute("data-roomtype") === typeId) ? "" : "none";
        }
        roomSelect.value = "";
    }
    window.onload = filterRooms;
</script>
<script src="js/main.js"></script>
</body>
</html>
