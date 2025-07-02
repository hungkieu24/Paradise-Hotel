<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="Model.UserAccount" %>
<%
    UserAccount user = (UserAccount) request.getAttribute("user");
    String staffName = "";
    if (session.getAttribute("user") != null) {
        staffName = ((UserAccount)session.getAttribute("user")).getUsername();
    }
       String branchName = (session.getAttribute("branchName") != null && !session.getAttribute("branchName").toString().isEmpty())
        ? session.getAttribute("branchName").toString()
        : "Branch";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Customer Information</title>
    <link rel="stylesheet" href="css/style_1.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        .room-img { width: 80px; border-radius: 6px; box-shadow: 0 2px 6px rgba(0,0,0,.09); }
        .pagination { margin: 20px 0 0 0; display: flex; justify-content: center; }
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
        .sidebar-header { padding: 20px; font-size: 1.25rem; font-weight: bold; }
        .sidebar-menu { padding: 0 0 20px 0; }
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
        .sidebar-menu .logout { color: #fa5252; }
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
    <!-- Header Bar -->
    <!-- Main Content -->
    <main class="main-content">
        <div class="content-body">
            <a href="javascript:history.back()" class="btn btn-outline-secondary mb-3">
                <i class="bi bi-arrow-left"></i> Back
            </a>
            <div class="card shadow-sm">
                <div class="card-header bg-info text-white">
                    <h2 class="mb-0">
                        <i class="bi bi-person"></i> Customer Information
                    </h2>
                </div>
                <div class="card-body">
                    <% if(user != null) { %>
                    <div class="row mb-3">
                        <div class="col-md-3 text-center">
                            <% if(user.getAvatar_url() != null && !user.getAvatar_url().isEmpty()) { %>
                                <img src="<%= user.getAvatar_url() %>" class="avatar-img img-thumbnail">
                            <% } else { %>
                                <i class="bi bi-person-circle" style="font-size: 4rem;"></i>
                            <% } %>
                        </div>
                        <div class="col-md-9">
                            <h4><i class="bi bi-person"></i> <%= user.getUsername() %></h4>
                            <p><i class="bi bi-envelope"></i> <strong>Email:</strong> <%= user.getEmail() %></p>
                            <p><i class="bi bi-phone"></i> <strong>Phone:</strong> <%= user.getPhonenumber() %></p>
                            <p><i class="bi bi-person-badge"></i> <strong>Role:</strong> <%= user.getRole() %></p>
                            <p><i class="bi bi-check-circle"></i> <strong>Status:</strong> <%= user.getStatus() %></p>
                        </div>
                    </div>
                    <% } else { %>
                        <div class="alert alert-warning">User not found.</div>
                    <% } %>
                </div>
            </div>
        </div>
    </main>
</div>
<script src="js/main.js"></script>
</body>
</html>