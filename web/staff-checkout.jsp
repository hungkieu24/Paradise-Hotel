<%@ page contentType="text/html;charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
        <title>Staff Checkout</title>
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
            /* Checkout styles from mookup */
            .checkout-section {
                background:#fff;
                border:1.5px solid #ccc;
                border-radius:8px;
                margin-top:30px;
                padding:32px 24px;
                max-width:800px;
            }
            .box {
                border:1.5px solid #bbb;
                border-radius:6px;
                padding:22px 18px 18px 18px;
                margin-bottom:26px;
            }
            .box-title {
                font-weight:bold;
                margin-bottom:10px;
                background:#fff;
                display:inline-block;
                padding:2px 16px;
                border:1px solid #bbb;
                border-radius:5px;
            }
            .flex-row {
                display:flex;
                gap:24px;
            }
            .flex-row .form-item {
                flex:1;
            }
            .form-item {
                margin-bottom:13px;
            }
            .form-label {
                font-weight:bold;
                display:block;
                margin-bottom:4px;
            }
            .form-value {
                padding:7px 12px;
                border:1px solid #ccc;
                border-radius:4px;
                background:#f7f7f7;
            }
            .booking-thumb {
                width:110px;
                height:82px;
                background:#eee;
                display:block;
                border:1px solid #bbb;
                margin-bottom:12px;
            }
            .details-section {
                display:flex;
                align-items:center;
                gap:40px;
                margin-top:12px;
            }
            .detail-btn {
                border:1px solid #bbb;
                background:#fff;
                border-radius:5px;
                padding:3px 16px;
                font-weight:bold;
            }
            .summary-table {
                margin-left:24px;
            }
            .summary-table td {
                padding:4px 12px;
            }
            .summary-table .total {
                font-weight:bold;
                font-size:1.2em;
            }
            .checkout-footer {
                text-align:right;
                margin-top:18px;
            }
            .primary-btn {
                background:#007bff;
                color:#fff;
                border:none;
                border-radius:5px;
                font-size:1.1em;
                padding:10px 30px;
                cursor:pointer;
            }
            .primary-btn:hover {
                background:#0056b3;
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
                        <h1 class="page-title">Staff Checkout</h1>
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
                    <div class="form-container">
                        <form action="confirm-payment" method="post" autocomplete="off">
                            <input type="hidden" name="bookingId" value="${booking.id}" />
                            <div class="checkout-section">
                                <!-- Customer Informations -->
                                <div class="box">
                                    <span class="box-title">Customer informations</span>
                                    <div class="flex-row">
                                        <div class="form-item">
                                            <label class="form-label">Full name</label>
                                            <div class="form-control-plaintext" readonly>${customer.fullname}</div>
                                        </div>
                                        <div class="form-item">
                                            <label class="form-label">Phone Number</label>
                                            <div class="form-control-plaintext" readonly>${customer.phonenumber}</div>
                                        </div>
                                    </div>
                                    <div class="flex-row">
                                        <div class="form-item">
                                            <label class="form-label">Email</label>
                                            <div class="form-control-plaintext" readonly>${customer.email}</div>
                                        </div>
                                        <div class="form-item">
                                            <label class="form-label">Rank</label>
                                            <div class="form-control-plaintext" readonly>${customer.rank}</div>
                                        </div>
                                    </div>
                                </div>
                                 <!-- Booking Informations: one box per room -->
                                <c:forEach var="room" items="${bookingRoomList}">
                                    <div class="box">
                                        <span class="box-title">
                                            Room: ${room.room_number} (${room.type_name})
                                        </span>
                                        <div class="flex-row" style="align-items: flex-start; margin-bottom: 12px;">
                                            <div>
                                                <img src="${room.image_url}" class="booking-thumb" alt="Room image"/>
                                            </div>
                                            <div style="flex:1;">
                                                <div class="flex-row">
                                                    <div class="form-item">
                                                        <label class="form-label">Room number</label>
                                                        <div class="form-control-plaintext" readonly>${room.room_number}</div>
                                                    </div>
                                                    <div class="form-item">
                                                        <label class="form-label">Room type</label>
                                                        <div class="form-control-plaintext" readonly>${room.type_name}</div>
                                                    </div>
                                                    <div class="form-item">
                                                        <label class="form-label">Capacity</label>
                                                        <div class="form-control-plaintext" readonly>
                                                            ${room.capacity_adult} adults, ${room.capacity_child} children
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="flex-row">
                                                    <div class="form-item">
                                                        <label class="form-label">Check-in</label>
                                                        <div class="form-control-plaintext" readonly>${room.checkin}</div>
                                                    </div>
                                                    <div class="form-item">
                                                        <label class="form-label">Check-out</label>
                                                        <div class="form-control-plaintext" readonly>${room.checkout}</div>
                                                    </div>
                                                    <div class="form-item">
                                                        <label class="form-label">Price</label>
                                                        <div class="form-control-plaintext" readonly>
                                                            ${room.price} VND
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                                <!-- Voucher Input -->
                                <div class="voucher-row">
                                    <label for="voucherCode" style="font-weight:bold;">Voucher code:</label>
                                    <input type="text" id="voucherCode" name="voucherCode" class="voucher-input" placeholder="Enter voucher code">
                                    <button type="button" class="voucher-btn" onclick="alert('Demo: Chưa xử lý áp dụng voucher!')">Apply</button>
                                </div>
                                <!-- Detail/Total -->
                                <div class="box details-section">
                                    <button type="button" class="detail-btn">Detail</button>
                                    <table class="summary-table">
                                        <tr>
                                            <td>Total room price</td>
                                            <td style="text-align:right;">${booking.totalRoomPrice}</td>
                                        </tr>
                                        <tr>
                                            <td>Total service</td>
                                            <td style="text-align:right;">${booking.totalServicePrice}</td>
                                        </tr>
                                                                                <tr>
                                            <td>Rank</td>
                                            <td style="text-align:right;">${booking.totalServicePrice}</td>
                                        </tr>
                                        <tr>
                                            <td>Voucher discount</td>
                                            <td style="text-align:right;">-${booking.voucherDiscount}</td>
                                        </tr>
                                        <tr>
                                            <td colspan="2" class="total">Total 
                                                <span style="float:right;">${booking.totalPrice}</span>
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                                <div class="checkout-footer">
                                    <button type="submit" class="primary-btn">Confirm Payment</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </main>
        </div>
        <script src="js/main.js"></script>
    </body>
</html>