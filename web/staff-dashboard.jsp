<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="Model.UserAccount" %>
<%
   
    // Lấy tên staff từ session (nếu có)
    String staffName = "staff";
    if (session.getAttribute("user") != null) {
        UserAccount user = (UserAccount) session.getAttribute("user");
        if (user.getUsername() != null && !user.getUsername().isEmpty()) {
            staffName = user.getUsername();
        }
    }
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Staff Dashboard</title>
        <link rel="stylesheet" href="css/style_1.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
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
    <body>
        <div class="app-container">
            <!-- Sidebar -->
            <%@ include file="sidebar.jsp" %>
            <!-- Main Content -->
            <main class="main-content">
                <header class="content-header">
                    <div class="header-left">
                        <h1 class="page-title">Staff Panel</h1>
                    </div>
                    <div class="header-right">
                        <button class="theme-toggle" id="themeToggle">
                            <i class="fas fa-moon"></i>
                        </button>
                        <div class="user-info">
                            <i class="fas fa-user-circle"></i>
                            <span><%= staffName %></span>
                        </div>
                    </div>
                </header>

                <div class="content-body">
                    <div class="dashboard-grid">
                        <div class="stats-grid">
                            <!-- Today's Bookings -->
                            <div class="stat-card">
                                <div class="stat-icon occupied">
                                    <i class="fas fa-calendar-check"></i>
                                </div>
                                <div class="stat-info">
                                    <h3>Today's Booking</h3>
                                    <p>Bookings & Check-in/out</p>
                                    <a href="staff-bookings-list" class="btn btn-primary btn-sm mt-2">
                                        Manage <i class="fas fa-arrow-right"></i>
                                    </a>
                                </div>
                            </div>

                            <!-- Room Management -->
                            <div class="stat-card">
                                <div class="stat-icon available">
                                    <i class="fas fa-door-closed"></i>
                                </div>
                                <div class="stat-info">
                                    <h3>Rooms list</h3>
                                    <p>Manage room status</p>
                                    <a href="staff-rooms" class="btn btn-secondary btn-sm mt-2">
                                        View Rooms <i class="fas fa-arrow-right"></i>
                                    </a>
                                </div>
                            </div>

                            <!-- Add New Booking -->
                            <div class="stat-card">                       
                                <div class="stat-icon add-booking d-flex align-items-center justify-content-center"
                                     style="width:48px;height:48px;background:#22a366;border-radius:12px;">
                                    <i class="fas fa-plus" style="color:white;font-size:2rem;"></i>
                                </div>
                                <div class="stat-info">
                                    <h3 style="font-weight:700;">Add New Booking</h3>
                                    <p class="text-secondary">Create walk-in or guest booking</p>
                                    <a href="searchGuest" class="btn btn-success btn-sm mt-2 d-inline-flex align-items-center"
                                       style="background:#22a366;border:none;font-weight:500;">
                                        Add new Booking <i class="fas fa-arrow-right ms-1"></i>
                                    </a>
                                </div>
                            </div>
                            
                            <div class="stat-card">                       
                                <div class="stat-icon add-booking d-flex align-items-center justify-content-center"
                                     style="width:48px;height:48px;background:#22a366;border-radius:12px;">
                                    <i class="fas fa-plus" style="color:white;font-size:2rem;"></i>
                                </div>
                                <div class="stat-info">
                                    <h3 style="font-weight:700;">Check out</h3>
                                    <p class="text-secondary">Check out</p>
                                    <a href="staff-checkout" class="btn btn-success btn-sm mt-2 d-inline-flex align-items-center"
                                       style="background:#22a366;border:none;font-weight:500;">
                                        Check out<i class="fas fa-arrow-right ms-1"></i>
                                    </a>
                                </div>
                            </div>
                            <!-- End Add New Booking -->
                        </div>
                    </div>
                </div>
            </main>
        </div>

        <script src="js/main.js"></script>
    </body>
</html>