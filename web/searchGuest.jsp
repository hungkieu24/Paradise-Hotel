<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="Model.UserAccount" %>
<%
    String staffName = "";
    if (session.getAttribute("user") != null) {
        staffName = ((UserAccount)session.getAttribute("user")).getUsername();
    }
     
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Guest Shearch</title>
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
            .form-container {
                max-width: 350px;
                margin: 0 auto;
            }
            .search-input {
                font-size: 0.98rem;
                padding: 6px 12px;
                height: 38px;
            }
            .search-btn {
                font-size: 1rem;
                padding: 7px 0;
                height: 38px;
            }
            @media (max-width: 576px) {
                .form-container {
                    max-width: 100%;
                    padding: 0 8px;
                }
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
                        <h1 class="page-title">Add New Booking</h1>
                    </div>
                    <div class="header-right">
                        <button class="theme-toggle" id="themeToggle" style="display:none;">
                            <i class="fas fa-moon"></i>
                        </button>
                        <div class="user-info">
                            <i class="fas fa-user-circle"></i>
                            <span><%= staffName != null && !staffName.isEmpty() ? staffName : "staff" %></span>
                        </div>
                    </div>
                </header>
                <div class="content-body">
                    <div class="form-container">
                        <h4 class="mb-4 text-center">Search Guest Account</h4>
                        <form method="post" action="searchGuest">
                            <div class="mb-3">
                                <label class="form-label">Email or Phone Number:</label>
                                <input
                                    type="text"
                                    class="form-control search-input"
                                    name="keyword"
                                    required
                                    value="${param.keyword != null ? param.keyword : ''}"
                                    pattern="^([a-zA-Z0-9_\\-.]+@[a-zA-Z0-9\\-.]+\\.[a-zA-Z]{2,}|0\\d{9,10}|\\+84\\d{9,10})$"
                                    title="Please enter a valid email or phone number."
                                    />
                            </div>
                            <button type="submit" class="btn btn-primary w-100 search-btn">Search</button>
                        </form>
                        <c:if test="${not empty errorMsg}">
                            <div class="alert alert-danger mt-3">${errorMsg}</div>
                            <c:if test="${errorMsg eq 'Account does not exist. Please register a new one.'}">
                                <a href="register.jsp" class="btn btn-success w-100 mt-3">Create New Account</a>
                            </c:if>
                        </c:if>
                    </div>
                </div>
            </main>
        </div>
        <script src="js/main.js"></script>
    </body>
</html>