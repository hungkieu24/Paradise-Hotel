<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="Model.UserAccount" %>
<%@ page import="Model.Booking" %>
<%@ page import="Model.BookingRoomType" %>
<%@ page import="Model.Room" %>
<%@ page import="Model.Service" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.math.BigDecimal" %>
<%
    Booking booking = (Booking) request.getAttribute("booking");
    UserAccount customer = (UserAccount) request.getAttribute("customer");
    List<BookingRoomType> bookingRoomTypes = (List<BookingRoomType>) request.getAttribute("bookingRoomTypes");
    List<Room> assignedRooms = (List<Room>) request.getAttribute("assignedRooms");
    List<Service> allServices = (List<Service>) request.getAttribute("allServices");
    Map<String, Object> checkoutDetails = (Map<String, Object>) request.getAttribute("checkoutDetails");
    String errorMessage = (String) request.getAttribute("errorMessage");
    
    NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    String staffName = "hieu1235";
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
    <title>Checkout Payment - Staff Panel</title>
    <link rel="stylesheet" href="css/style_1.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        /* Main Layout */
        .app-container {
            display: flex;
            min-height: 100vh;
            background: #f8f9fa;
        }
        
        .sidebar {
            width: 220px;
            height: 100vh;
            position: fixed;
            top: 0;
            left: 0;
            background: linear-gradient(180deg, #2c3e50 0%, #34495e 100%);
            color: #ecf0f1;
            z-index: 1000;
            overflow-y: auto;
        }
        
        .sidebar-header {
            padding: 20px;
            border-bottom: 1px solid #34495e;
            text-align: center;
        }
        
        .sidebar-brand {
            color: #3498db;
            font-size: 1.2rem;
            font-weight: bold;
            text-decoration: none;
        }
        
        .sidebar-menu {
            padding: 20px 0;
        }
        
        .sidebar-menu .menu-item {
            display: flex;
            align-items: center;
            gap: 12px;
            color: #bdc3c7;
            padding: 15px 20px;
            text-decoration: none;
            transition: all 0.3s ease;
            border-left: 3px solid transparent;
        }
        
        .sidebar-menu .menu-item:hover,
        .sidebar-menu .menu-item.active {
            background: rgba(52, 152, 219, 0.1);
            color: #3498db;
            border-left-color: #3498db;
        }
        
        .sidebar-menu .logout {
            color: #e74c3c;
            margin-top: auto;
        }
        
        .main-content {
            margin-left: 220px;
            flex: 1;
            background: #fff;
            min-height: 100vh;
        }
        
        /* Header */
        .content-header {
            background: #fff;
            border-bottom: 1px solid #e9ecef;
            padding: 20px 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .page-title {
            font-size: 1.5rem;
            font-weight: 600;
            color: #2c3e50;
            margin: 0;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .page-subtitle {
            color: #6c757d;
            margin: 0;
            font-size: 0.95rem;
        }
        
        .user-info {
            display: flex;
            align-items: center;
            gap: 10px;
            color: #6c757d;
            font-size: 0.9rem;
        }
        
        .user-info .staff-badge {
            background: #e3f2fd;
            color: #1976d2;
            padding: 4px 12px;
            border-radius: 15px;
            font-weight: 500;
        }
        
        /* Content Body */
        .content-body {
            padding: 30px;
            max-width: 1200px;
            margin: 0 auto;
        }
        
        /* Booking Header Card */
        .booking-header {
            background: #fff;
            border: 1px solid #e9ecef;
            border-radius: 8px;
            padding: 25px;
            margin-bottom: 25px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.05);
        }
        
        .booking-title {
            font-size: 1.3rem;
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 15px;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .booking-details {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }
        
        .detail-item {
            display: flex;
            align-items: center;
            gap: 8px;
            color: #495057;
            margin-bottom: 8px;
        }
        
        .detail-label {
            font-weight: 500;
            min-width: 100px;
        }
        
        .detail-value {
            color: #2c3e50;
            font-weight: 600;
        }
        
        .status-badge {
            background: #d4edda;
            color: #155724;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: 500;
        }
        
        /* Customer Info Section */
        .customer-section {
            background: #fff;
            border: 1px solid #e9ecef;
            border-radius: 8px;
            padding: 25px;
            margin-bottom: 25px;
        }
        
        .section-title {
            font-size: 1.1rem;
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .customer-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }
        
        .membership-badge {
            padding: 6px 16px;
            border-radius: 20px;
            font-size: 0.85rem;
            font-weight: 600;
        }
        
        .membership-member { background: #6c757d; color: white; }
        .membership-silver { background: #6c757d; color: white; }
        .membership-gold { background: #ffc107; color: #212529; }
        .membership-vip { background: #dc3545; color: white; }
        
        /* Room Types Section */
        .room-types-section {
            background: #fff;
            border: 1px solid #e9ecef;
            border-radius: 8px;
            padding: 25px;
            margin-bottom: 25px;
        }
        
        .room-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 15px;
        }
        
        .room-table th {
            background: #f8f9fa;
            padding: 12px 15px;
            text-align: left;
            font-weight: 600;
            color: #495057;
            border-bottom: 2px solid #e9ecef;
        }
        
        .room-table td {
            padding: 15px;
            border-bottom: 1px solid #f1f3f4;
        }
        
        .room-type-info {
            display: flex;
            align-items: center;
            gap: 15px;
        }
        
        .room-type-image {
            width: 60px;
            height: 45px;
            border-radius: 6px;
            object-fit: cover;
            border: 1px solid #e9ecef;
        }
        
        .room-type-placeholder {
            width: 60px;
            height: 45px;
            background: #f8f9fa;
            border: 1px solid #e9ecef;
            border-radius: 6px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #6c757d;
        }
        
        .quantity-badge {
            background: #007bff;
            color: white;
            padding: 6px 12px;
            border-radius: 20px;
            font-weight: 500;
            font-size: 0.9rem;
        }
        
        .price-text {
            font-weight: 600;
            color: #2c3e50;
        }
        
        .subtotal-text {
            font-weight: 700;
            color: #28a745;
            font-size: 1.05rem;
        }
        
        /* Payment Summary Section */
        .payment-summary {
            background: #fff;
            border: 1px solid #e9ecef;
            border-radius: 8px;
            padding: 25px;
            margin-bottom: 25px;
        }
        
        .summary-grid {
            display: grid;
            grid-template-columns: 2fr 1fr;
            gap: 30px;
            align-items: start;
        }
        
        .summary-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 12px 0;
            border-bottom: 1px solid #f1f3f4;
        }
        
        .summary-item:last-child {
            border-bottom: none;
        }
        
        .summary-label {
            display: flex;
            align-items: center;
            gap: 8px;
            color: #495057;
            font-weight: 500;
        }
        
        .summary-value {
            font-weight: 600;
            color: #2c3e50;
        }
        
        .summary-positive {
            color: #28a745;
        }
        
        .amount-to-pay {
            background: linear-gradient(135deg, #e3f2fd, #f3e5f5);
            border: 2px solid #1976d2;
            border-radius: 10px;
            padding: 20px;
            text-align: center;
        }
        
        .amount-title {
            font-size: 1.1rem;
            color: #1976d2;
            margin-bottom: 10px;
            font-weight: 600;
        }
        
        .amount-value {
            font-size: 1.8rem;
            font-weight: 700;
            color: #1976d2;
            margin-bottom: 5px;
        }
        
        .amount-subtitle {
            color: #6c757d;
            font-size: 0.9rem;
        }
        
        /* Payment Methods */
        .payment-methods {
            margin: 25px 0;
        }
        
        .methods-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 15px;
            margin-top: 15px;
        }
        
        .payment-option {
            border: 2px solid #e9ecef;
            border-radius: 10px;
            padding: 20px;
            text-align: center;
            cursor: pointer;
            transition: all 0.3s ease;
            background: #fff;
        }
        
        .payment-option:hover {
            border-color: #007bff;
            background: #f8f9ff;
        }
        
        .payment-option.selected {
            border-color: #007bff;
            background: #e3f2fd;
            color: #1976d2;
        }
        
        .payment-icon {
            font-size: 2rem;
            margin-bottom: 10px;
            color: #6c757d;
        }
        
        .payment-option.selected .payment-icon {
            color: #1976d2;
        }
        
        .payment-title {
            font-weight: 600;
            margin-bottom: 5px;
        }
        
        .payment-desc {
            font-size: 0.85rem;
            color: #6c757d;
        }
        
        /* Action Buttons */
        .checkout-actions {
            display: flex;
            justify-content: center;
            gap: 15px;
            margin-top: 30px;
        }
        
        .btn-back {
            background: #6c757d;
            color: white;
            border: none;
            padding: 12px 25px;
            border-radius: 8px;
            font-weight: 500;
            text-decoration: none;
            display: flex;
            align-items: center;
            gap: 8px;
            transition: all 0.3s ease;
        }
        
        .btn-back:hover {
            background: #5a6268;
            color: white;
            transform: translateY(-2px);
        }
        
        .btn-checkout {
            background: linear-gradient(135deg, #28a745, #20c997);
            color: white;
            border: none;
            padding: 12px 30px;
            border-radius: 8px;
            font-weight: 600;
            font-size: 1.05rem;
            display: flex;
            align-items: center;
            gap: 8px;
            transition: all 0.3s ease;
            cursor: pointer;
        }
        
        .btn-checkout:hover {
            background: linear-gradient(135deg, #218838, #1ea084);
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(40,167,69,0.3);
        }
        
        /* Error Alert */
        .error-alert {
            background: #f8d7da;
            border: 1px solid #f5c6cb;
            color: #721c24;
            padding: 15px 20px;
            border-radius: 8px;
            margin-bottom: 25px;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        /* Responsive */
        @media (max-width: 768px) {
            .main-content {
                margin-left: 0;
            }
            
            .sidebar {
                transform: translateX(-100%);
            }
            
            .booking-details,
            .customer-grid {
                grid-template-columns: 1fr;
            }
            
            .summary-grid {
                grid-template-columns: 1fr;
                gap: 20px;
            }
            
            .methods-grid {
                grid-template-columns: 1fr;
            }
            
            .checkout-actions {
                flex-direction: column;
                align-items: center;
            }
        }
    </style>
</head>
<body>
    <div class="app-container">
        <%@ include file="sidebar.jsp" %>
        
        <main class="main-content">
            <!-- Header -->
            <header class="content-header">
                <div>
                    <h1 class="page-title">
                        <i class="bi bi-credit-card"></i>
                        Checkout Payment
                    </h1>
                    <p class="page-subtitle">Process final payment and complete booking</p>
                </div>
                <div class="user-info">
                    <i class="bi bi-person-circle"></i>
                    <span>Staff: <span class="staff-badge"><%= staffName %></span></span>

                </div>
            </header>
            
            <div class="content-body">
                <!-- Error Message -->
                <% if (errorMessage != null) { %>
                <div class="error-alert">
                    <i class="bi bi-exclamation-triangle"></i>
                    <strong>Error!</strong> <%= errorMessage %>
                </div>
                <% } %>
                
                <% if (booking != null) { %>
                    <!-- Booking Header -->
                    <div class="booking-header">
                        <h2 class="booking-title">
                            <i class="bi bi-bookmark-fill"></i>
                            Booking #<%= booking.getId() %> - Checkout Payment
                        </h2>
                        <div class="booking-details">
                            <div>
                                <div class="detail-item">
                                    <i class="bi bi-calendar-check"></i>
                                    <span class="detail-label">Check-in:</span>
                                    <span class="detail-value"><%= dateFormat.format(booking.getCheckIn()) %></span>
                                </div>
                                <div class="detail-item">
                                    <i class="bi bi-calendar-x"></i>
                                    <span class="detail-label">Check-out:</span>
                                    <span class="detail-value"><%= dateFormat.format(booking.getCheckOut()) %></span>
                                </div>
                            </div>
                            <div>
                                <div class="detail-item">
                                    <i class="bi bi-flag"></i>
                                    <span class="detail-label">Status:</span>
                                    <span class="status-badge">
                                        <i class="bi bi-check-circle"></i>
                                        <%= booking.getStatus() %>
                                    </span>
                                </div>
                                <div class="detail-item">
                                    <i class="bi bi-clock"></i>
                                    <span class="detail-label">Current Time:</span>
                                    <span class="detail-value"><%= dateFormat.format(new java.util.Date()) %></span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Customer Information -->
                    <% if (customer != null) { %>
                    <div class="customer-section">
                        <h3 class="section-title">
                            <i class="bi bi-person-badge"></i>
                            Customer Information
                        </h3>
                        <div class="customer-grid">
                            <div>
                                <div class="detail-item">
                                    <i class="bi bi-person"></i>
                                    <span class="detail-label">Name:</span>
                                    <span class="detail-value">
                                        <%= customer.getFullName() != null ? customer.getFullName() : customer.getUsername() %>
                                    </span>
                                </div>
                                <div class="detail-item">
                                    <i class="bi bi-envelope"></i>
                                    <span class="detail-label">Email:</span>
                                    <span class="detail-value"><%= customer.getEmail() %></span>
                                </div>
                            </div>
                            <div>
                                <div class="detail-item">
                                    <i class="bi bi-telephone"></i>
                                    <span class="detail-label">Phone:</span>
                                    <span class="detail-value">
                                        <%= customer.getPhonenumber() != null ? customer.getPhonenumber() : "N/A" %>
                                    </span>
                                </div>
                                <div class="detail-item">
                                    <i class="bi bi-star"></i>
                                    <span class="detail-label">Membership:</span>
                                    <% 
                                        String rank = customer.getRank() != null ? customer.getRank() : "Member";
                                        String membershipClass = "membership-member";
                                        if ("Silver".equalsIgnoreCase(rank)) membershipClass = "membership-silver";
                                        else if ("Gold".equalsIgnoreCase(rank)) membershipClass = "membership-gold";
                                        else if ("VIP".equalsIgnoreCase(rank)) membershipClass = "membership-vip";
                                    %>
                                    <span class="membership-badge <%= membershipClass %>">
                                        <i class="bi bi-gem"></i> <%= rank %>
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <% } %>

                    <!-- Room Types -->
                    <% if (bookingRoomTypes != null && !bookingRoomTypes.isEmpty()) { %>
                    <div class="room-types-section">
                        <h3 class="section-title">
                            <i class="bi bi-house-door"></i>
                            Room Types
                        </h3>
                        <table class="room-table">
                            <thead>
                                <tr>
                                    <th><i class="bi bi-house"></i> Room Type</th>
                                    <th><i class="bi bi-123"></i> Quantity</th>
                                    <th><i class="bi bi-currency-dollar"></i> Price per Room</th>
                                    <th><i class="bi bi-calculator"></i> Subtotal</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (BookingRoomType brt : bookingRoomTypes) { %>
                                <tr>
                                    <td>
                                        <div class="room-type-info">
                                            <% if (brt.getRoomTypeImageUrl() != null && !brt.getRoomTypeImageUrl().isEmpty()) { %>
                                                <img src="<%= brt.getRoomTypeImageUrl() %>" 
                                                     alt="<%= brt.getRoomTypeName() %>" 
                                                     class="room-type-image">
                                            <% } else { %>
                                                <div class="room-type-placeholder">
                                                    <i class="bi bi-image"></i>
                                                </div>
                                            <% } %>
                                            <div>
                                                <div class="detail-value"><%= brt.getRoomTypeName() %></div>
                                                <% if (brt.getRoomTypeDescription() != null && !brt.getRoomTypeDescription().isEmpty()) { %>
                                                    <small class="text-muted"><%= brt.getRoomTypeDescription() %></small>
                                                <% } %>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <span class="quantity-badge"><%= brt.getQuantity() %></span>
                                    </td>
                                    <td class="price-text">
                                        <%= currencyFormat.format(brt.getPricePerRoom()) %> VND
                                    </td>
                                    <td class="subtotal-text">
                                        <%= currencyFormat.format(brt.getTotalPrice()) %> VND
                                    </td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                    <% } %>

                    <!-- Payment Summary -->
                    <% if (checkoutDetails != null) { %>
                    <div class="payment-summary">
                        <h3 class="section-title">
                            <i class="bi bi-calculator"></i>
                            Payment Summary
                        </h3>
                        
                        <div class="summary-grid">
                            <div>
                                <div class="summary-item">
                                    <span class="summary-label">
                                        <i class="bi bi-house"></i>
                                        Room Total:
                                    </span>
                                    <span class="summary-value">
                                        <%= currencyFormat.format((Double) checkoutDetails.get("totalRoomPrice")) %> VND
                                    </span>
                                </div>
                                <div class="summary-item">
                                    <span class="summary-label">
                                        <i class="bi bi-gear"></i>
                                        Services Total:
                                    </span>
                                    <span class="summary-value">
                                        <%= currencyFormat.format((Double) checkoutDetails.get("totalServicePrice")) %> VND
                                    </span>
                                </div>
                                <div class="summary-item">
                                    <span class="summary-label summary-positive">
                                        <i class="bi bi-check-circle"></i>
                                        Already Paid Services:
                                    </span>
                                    <span class="summary-value summary-positive">
                                        -<%= currencyFormat.format((Double) checkoutDetails.get("paidServicePrice")) %> VND
                                    </span>
                                </div>
                                <div class="summary-item">
                                    <span class="summary-label summary-positive">
                                        <i class="bi bi-star"></i>
                                        Membership Discount (<%= customer != null ? customer.getRank() : "Member" %> - <%= checkoutDetails.get("rankDiscountPercent") %>%):
                                    </span>
                                    <span class="summary-value summary-positive">
                                        -<%= currencyFormat.format((Double) checkoutDetails.get("rankDiscount")) %> VND
                                    </span>
                                </div>
                            </div>
                            
                            <div class="amount-to-pay">
                                <div class="amount-title">
                                    <i class="bi bi-credit-card"></i>
                                    Amount to Pay:
                                </div>
                                <div class="amount-value">
                                    <%= currencyFormat.format((Double) checkoutDetails.get("amountToPay")) %> VND
                                </div>
                                <div class="amount-subtitle">Final checkout amount</div>
                            </div>
                        </div>
                        
                        <!-- Payment Methods -->
                        <div class="payment-methods">
                            <h4 class="section-title">
                                <i class="bi bi-credit-card"></i>
                                Select Payment Method:
                            </h4>
                            
                            <form action="process-checkout" method="post" id="checkoutForm">
                                <input type="hidden" name="bookingId" value="<%= booking.getId() %>">
                                <input type="hidden" name="amountToPay" value="<%= checkoutDetails.get("amountToPay") %>">
                                
                                <div class="methods-grid">
                                    <label>
                                        <input type="radio" name="paymentMethod" value="cash" checked style="display: none;">
                                        <div class="payment-option selected" onclick="selectPayment(this, 'cash')">
                                            <div class="payment-icon">
                                                <i class="bi bi-cash-stack"></i>
                                            </div>
                                            <div class="payment-title">Cash Payment</div>
                                            <div class="payment-desc">Pay with cash</div>
                                        </div>
                                    </label>
                                    
                                    <label>
                                        <input type="radio" name="paymentMethod" value="card" style="display: none;">
                                        <div class="payment-option" onclick="selectPayment(this, 'card')">
                                            <div class="payment-icon">
                                                <i class="bi bi-credit-card"></i>
                                            </div>
                                            <div class="payment-title">Card Payment</div>
                                            <div class="payment-desc">Credit/Debit card</div>
                                        </div>
                                    </label>
                                    
                                    <label>
                                        <input type="radio" name="paymentMethod" value="transfer" style="display: none;">
                                        <div class="payment-option" onclick="selectPayment(this, 'transfer')">
                                            <div class="payment-icon">
                                                <i class="bi bi-bank"></i>
                                            </div>
                                            <div class="payment-title">Bank Transfer</div>
                                            <div class="payment-desc">Wire transfer</div>
                                        </div>
                                    </label>
                                </div>
                                
                                <div class="checkout-actions">
                                    <a href="view-booking-detail?bookingId=<%= booking.getId() %>" class="btn-back">
                                        <i class="bi bi-arrow-left"></i>
                                        Back to Details
                                    </a>
                                    <button type="button" class="btn-checkout" onclick="confirmCheckout()">
                                        <i class="bi bi-check-circle"></i>
                                        Complete Checkout
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                    <% } %>
                <% } else { %>
                    <div class="booking-header text-center">
                        <i class="bi bi-exclamation-triangle text-warning" style="font-size: 4rem;"></i>
                        <h3 class="mt-3">No Booking Found</h3>
                        <p class="text-muted">The requested booking could not be found or you don't have permission to access it.</p>
                        <a href="staff-bookings-list" class="btn-back">
                            <i class="bi bi-list"></i>
                            Back to Bookings List
                        </a>
                    </div>
                <% } %>
            </div>
        </main>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function selectPayment(element, method) {
            // Remove selected class from all options
            document.querySelectorAll('.payment-option').forEach(function(option) {
                option.classList.remove('selected');
            });
            
            // Add selected class to clicked option
            element.classList.add('selected');
            
            // Update radio button
            document.querySelector('input[value="' + method + '"]').checked = true;
        }
        
        function confirmCheckout() {
            const amount = '<%= checkoutDetails != null ? currencyFormat.format((Double) checkoutDetails.get("amountToPay")) : "0" %>';
            const paymentMethod = document.querySelector('input[name="paymentMethod"]:checked').value;
            
            if (confirm('Confirm checkout payment of ' + amount + ' VND via ' + paymentMethod.toUpperCase() + '?\n\nThis action cannot be undone.')) {
                document.getElementById('checkoutForm').submit();
            }
        }
        
        // Auto-hide alerts after 5 seconds
        setTimeout(function() {
            const alerts = document.querySelectorAll('.error-alert');
            alerts.forEach(function(alert) {
                alert.style.opacity = '0';
                setTimeout(() => alert.remove(), 300);
            });
        }, 5000);
    </script>
</body>
</html>