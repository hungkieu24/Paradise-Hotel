<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List, java.util.Map" %>
<%@ page import="Model.Room" %>
<%@ page import="Model.UserAccount" %>
<%
    List<Room> rooms = (List<Room>) request.getAttribute("rooms");
    Map<Integer, String> roomTypeMap = (Map<Integer, String>) request.getAttribute("roomTypeMap");
    Map<Integer, Double> roomTypePriceMap = (Map<Integer, Double>) request.getAttribute("roomTypePriceMap");
    String keyword = request.getParameter("keyword");
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
                        <div class="d-flex mb-3 justify-content-between align-items-center">
                            <a href="staff-dashboard.jsp" class="btn btn-outline-secondary">
                                <i class="bi bi-arrow-left"></i> Back to Panel
                            </a>
                            <!-- AJAX live search form -->
                            <form class="d-flex" id="room-search-form" onsubmit="return false;">
                                <input type="text" name="keyword" class="form-control me-2" placeholder="Search by Room Type ..." id="room-search-input"
                                       value="<%= keyword != null ? keyword : "" %>" autocomplete="off">
                            </form>
                        </div>

                        <form method="post" action="staff-rooms" id="bulk-room-status-form">
                            <div class="card shadow-sm">
                                <div class="card-header bg-warning">
                                    <h2 class="mb-0"><i class="bi bi-door-open"></i> Room List</h2>
                                </div>
                                <div class="card-body p-0">
                                    <table class="table table-hover align-middle mb-0">
                                        <thead class="table-light">
                                            <tr>
                                                <th>ID</th>
                                                <th>Room Number</th>
                                                <th>Room Type</th>
                                                <th>Price</th> <!-- Thêm cột giá tiền ở đây -->
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
                                            %>
                                            <tr>
                                                <td><%= r.getId() %></td>
                                                <td><i class="bi bi-door-closed"></i> <%= r.getRoomNumber() %></td>
                                                <td><%= typeName != null ? typeName : "Unknown" %></td>
                                                <td>
                                                    <%
                                                        if (price != null) {
                                                            out.print(String.format("%,.0f VND", price));
                                                        } else {
                                                            out.print("N/A");
                                                        }
                                                    %>
                                                </td>
                                                <td>
                                                    <% String status = r.getStatus(); %>
                                                    <span class="badge
                                                          <% if("Available".equalsIgnoreCase(status)) { %>bg-success
                                                          <% } else if("Booked".equalsIgnoreCase(status)) { %>bg-primary
                                                          <% } else if("Occupied".equalsIgnoreCase(status)) { %>bg-warning text-dark
                                                          <% } else if("Maintenance".equalsIgnoreCase(status)) { %>bg-secondary
                                                          <% } else { %>bg-light text-dark<% } %>">
                                                        <%= status %>
                                                    </span>
                                                </td>
                                                <td>
                                                    <% if (r.getImageUrl() != null && !r.getImageUrl().isEmpty()) { %>
                                                    <img src="<%= r.getImageUrl() %>" alt="room image" class="room-img"/>
                                                    <% } %>
                                                </td>
                                                <td>
                                                    <select name="status_<%= r.getId() %>" class="form-select form-select-sm">
                                                        <option value="Available" <%= "Available".equals(status) ? "selected" : "" %>>Available</option>
                                                        <option value="Booked" <%= "Booked".equals(status) ? "selected" : "" %>>Booked</option>
                                                        <option value="Occupied" <%= "Occupied".equals(status) ? "selected" : "" %>>Occupied</option>
                                                        <option value="Maintenance" <%= "Maintenance".equals(status) ? "selected" : "" %>>Maintenance</option>
                                                    </select>
                                                    <input type="hidden" name="roomId" value="<%= r.getId() %>"/>
                                                </td>
                                            </tr>
                                            <%
                                                }
                                            } else {
                                            %>
                                            <tr>
                                                <td colspan="7" class="text-center text-muted">No rooms found.</td>
                                            </tr>
                                            <% } %>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                            <div class="mt-3 text-end">
                                <button type="submit" class="btn btn-primary">
                                    <i class="bi bi-arrow-repeat"></i> Update All
                                </button>
                            </div>
                        </form>

                        <!-- Pagination Bar -->
                        <div class="pagination" id="pagination-bar">
                            <%
                                String queryStr = "";
                                if (keyword != null && !keyword.isEmpty()) {
                                    queryStr += "&keyword=" + java.net.URLEncoder.encode(keyword, "UTF-8");
                                }
                                if (totalPage > 1) {
                                    if(currentPage > 1){
                            %>
                            <a href="staff-rooms?page=<%=currentPage-1%><%=queryStr%>">&laquo;</a>
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
                                    if(currentPage < totalPage){
                            %>
                            <a href="staff-rooms?page=<%=currentPage+1%><%=queryStr%>">&raquo;</a>
                            <%
                                    }
                                }
                            %>
                        </div>
                    </div>
                </div>
            </main>
        </div>
        <script src="js/main.js"></script>
        <script>
                                let typingTimer;
                                const doneTypingInterval = 350; // milliseconds
                                const searchInput = document.getElementById('room-search-input');

                                function fetchRooms() {
                                    const keyword = searchInput.value;
                                    let url = 'staff-rooms?ajax=1';
                                    if (keyword)
                                        url += '&keyword=' + encodeURIComponent(keyword);

                                    fetch(url)
                                            .then(response => response.text())
                                            .then(html => {
                                                document.getElementById('rooms-table-body').innerHTML = html;
                                            });
                                }

                                searchInput.addEventListener('input', function () {
                                    clearTimeout(typingTimer);
                                    typingTimer = setTimeout(fetchRooms, doneTypingInterval);
                                });
        </script>
    </body>
</html>