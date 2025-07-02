<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="Model.UserAccount" %>
<%@ page import="Model.Booking" %>
<%@ page import="Model.RoomTypeQuantity" %>
<%@ page import="Model.Room" %>
<%@ page import="Model.Service" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Locale" %>
<%
    Booking booking = (Booking) request.getAttribute("booking");
    UserAccount customer = (UserAccount) request.getAttribute("customer");
    List<RoomTypeQuantity> roomTypeQuantities = (List<RoomTypeQuantity>) request.getAttribute("roomTypeQuantities");
    List<Room> assignedRooms = (List<Room>) request.getAttribute("assignedRooms");
    List<Service> allServices = (List<Service>) request.getAttribute("allServices");
    Map<String, Object> checkoutDetails = (Map<String, Object>) request.getAttribute("checkoutDetails");
    String errorMessage = (String) request.getAttribute("errorMessage");
    
    NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    String staffName = "Staff";
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
    <title>Checkout - Booking #<%= booking != null ? booking.getId() : "N/A" %></title>
    <link rel="stylesheet" href="css/style_1.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <style>
        .main-content { margin-left: 220px; padding: 0; min-height: 100vh; background: #f4f5fa; }
        .content-body { margin: 35px 40px 0 40px; }
        .checkout-container { max-width: 1000px; margin: 0 auto; }
        .section-card { background: #fff; border: 1px solid #ddd; border-radius: 8px; margin-bottom: 20px; }
        .section-header { background: #f8f9fa; padding: 15px 20px; border-bottom: 1px solid #ddd; border-radius: 8px 8px 0 0; }
        .section-body { padding: 20px; }
        .info-row { display: flex; justify-content: space-between; margin-bottom: 10px; padding: 8px 0; border-bottom: 1px solid #f0f0f0; }
        .info-label { font-weight: bold; color: #666; }
        .info-value { color: #333; }
        .payment-summary { background: #f8f9fa; border: 2px solid #007bff; border-radius: 8px; padding: 20px; }
        .total-amount { font-size: 1.5rem; font-weight: bold; color: #007bff; }
        .payment-methods { display: flex; gap: 15px; margin: 15px 0; }
        .payment-method { flex: 1; }
        .payment-option { display: block; width: 100%; padding: 12px; border: 2px solid #ddd; border-radius: 8px; text-align: center; text-decoration: none; color: #333; transition: all 0.3s; }
        .payment-option:hover, .payment-option.selected { border-color: #007bff; background: #e7f3ff; color: #007bff; }
        .checkout-actions { text-align: center; margin-top: 30px; }
        .btn-checkout { background: #28a745; color: white; border: none; padding: 15px 40px; font-size: 1.1rem; border-radius: 8px; }
        .btn-checkout:hover { background: #218838; }
        .service-status-paid { background: #d4edda; color: #155724; padding: 4px 8px; border-radius: 4px; font-size: 0.8rem; }
        .service-status-unpaid { background: #fff3cd; color: #856404; padding: 4px 8px; border-radius: 4px; font-size: 0.8rem; }
    </style>
</head>
<body>
    <div class="app-container">
        <%@ include file="sidebar.jsp" %>
        <main class="main-content">
            <header class="content-header">
                <div class="header-left">
                    <h1 class="page-title">Checkout Payment</h1>
                </div>
                <div class="header-right">
                    <div class="user-info">
                        <i class="fas fa-user-circle"></i>
                        <span><%= staffName %></span>
                    </div>
                </div>
            </header>
            
            <div class="content-body">
                <div class="checkout-container">
                    
                    <!-- Error Message -->
                    <% if (errorMessage != null) { %>
                        <div class="alert alert-danger">
                            <i class="bi bi-exclamation-triangle"></i> <%= errorMessage %>
                        </div>
                    <% } %>
                    
                    <% if (booking != null) { %>
                        <!-- Booking Info -->
                        <div class="section-card">
                            <div class="section-header">
                                <h4 class="mb-0">
                                    <i class="bi bi-bookmark"></i> 
                                    Booking #<%= booking.getId() %> - Checkout Payment
                                </h4>
                            </div>
                            <div class="section-body">
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="info-row">
                                            <span class="info-label">Check-in:</span>
                                            <span class="info-value"><%= dateFormat.format(booking.getCheckIn()) %></span>
                                        </div>
                                        <div class="info-row">
                                            <span class="info-label">Check-out:</span>
                                            <span class="info-value"><%= dateFormat.format(booking.getCheckOut()) %></span>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="info-row">
                                            <span class="info-label">Status:</span>
                                            <span class="info-value">
                                                <span class="badge bg-success"><%= booking.getStatus() %></span>
                                            </span>
                                        </div>
                                        <div class="info-row">
                                            <span class="info-label">Current Time:</span>
                                            <span class="info-value"><%= dateFormat.format(new java.util.Date()) %></span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Customer Info -->
                        <% if (customer != null) { %>
                            <div class="section-card">
                                <div class="section-header">
                                    <h5 class="mb-0"><i class="bi bi-person"></i> Customer Information</h5>
                                </div>
                                <div class="section-body">
                                    <div class="row">
                                        <div class="col-md-6">
                                            <div class="info-row">
                                                <span class="info-label">Name:</span>
                                                <span class="info-value">
                                                    <%= customer.getFullName() != null ? customer.getFullName() : customer.getUsername() %>
                                                </span>
                                            </div>
                                            <div class="info-row">
                                                <span class="info-label">Email:</span>
                                                <span class="info-value"><%= customer.getEmail() %></span>
                                            </div>
                                        </div>
                                        <div class="col-md-6">
                                            <div class="info-row">
                                                <span class="info-label">Phone:</span>
                                                <span class="info-value">
                                                    <%= customer.getPhonenumber() != null ? customer.getPhonenumber() : "N/A" %>
                                                </span>
                                            </div>
                                            <div class="info-row">
                                                <span class="info-label">Membership:</span>
                                                <span class="info-value">
                                                    <span class="badge bg-primary">
                                                        <%= customer.getRank() != null ? customer.getRank() : "Member" %>
                                                    </span>
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        <% } %>

                        <!-- Room Types -->
                        <% if (roomTypeQuantities != null && !roomTypeQuantities.isEmpty()) { %>
                            <div class="section-card">
                                <div class="section-header">
                                    <h5 class="mb-0"><i class="bi bi-house"></i> Room Types</h5>
                                </div>
                                <div class="section-body">
                                    <div class="table-responsive">
                                        <table class="table table-striped">
                                            <thead>
                                                <tr>
                                                    <th>Room Type</th>
                                                    <th>Quantity</th>
                                                    <th>Price per Room</th>
                                                    <th>Subtotal</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <% for (RoomTypeQuantity rtq : roomTypeQuantities) { %>
                                                    <tr>
                                                        <td><%= rtq.getRoomTypeName() %></td>
                                                        <td><%= rtq.getQuantity() %></td>
                                                        <td><%= currencyFormat.format(rtq.getPricePerRoom()) %> VND</td>
                                                        <td><%= currencyFormat.format(rtq.getQuantity() * rtq.getPricePerRoom()) %> VND</td>
                                                    </tr>
                                                <% } %>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        <% } %>

                        <!-- Assigned Rooms -->
                        <% if (assignedRooms != null && !assignedRooms.isEmpty()) { %>
                            <div class="section-card">
                                <div class="section-header">
                                    <h5 class="mb-0"><i class="bi bi-door-open"></i> Assigned Rooms</h5>
                                </div>
                                <div class="section-body">
                                    <div class="row">
                                        <% for (Room room : assignedRooms) { %>
                                            <div class="col-md-4 mb-3">
                                                <div class="card border-success">
                                                    <div class="card-body text-center">
                                                        <h6 class="card-title">Room <%= room.getRoomNumber() %></h6>
                                                        <p class="card-text">
                                                            <small class="text-muted"><%= room.getRoomTypeName() %></small>
                                                        </p>
                                                        <span class="badge bg-success">Occupied</span>
                                                    </div>
                                                </div>
                                            </div>
                                        <% } %>
                                    </div>
                                </div>
                            </div>
                        <% } %>

                        <!-- Services -->
                        <% if (allServices != null && !allServices.isEmpty()) { %>
                            <div class="section-card">
                                <div class="section-header">
                                    <h5 class="mb-0"><i class="bi bi-gear"></i> Additional Services</h5>
                                </div>
                                <div class="section-body">
                                    <div class="table-responsive">
                                        <table class="table table-striped">
                                            <thead>
                                                <tr>
                                                    <th>Service</th>
                                                    <th>Quantity</th>
                                                    <th>Unit Price</th>
                                                    <th>Total</th>
                                                    <th>Status</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <% for (Service service : allServices) { 
                                                    double serviceTotal = service.getPrice() * service.getQuantity();
                                                %>
                                                    <tr>
                                                        <td><%= service.getName() %></td>
                                                        <td><%= service.getQuantity() %></td>
                                                        <td><%= currencyFormat.format(service.getPrice()) %> VND</td>
                                                        <td><%= currencyFormat.format(serviceTotal) %> VND</td>
                                                        <td>
                                                            <% if ("Paid".equalsIgnoreCase(service.getBookingServiceStatus())) { %>
                                                                <span class="service-status-paid">PAID</span>
                                                            <% } else { %>
                                                                <span class="service-status-unpaid">UNPAID</span>
                                                            <% } %>
                                                        </td>
                                                    </tr>
                                                <% } %>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        <% } %>

                        <!-- Payment Summary and Form -->
                        <% if (checkoutDetails != null) { %>
                            <form action="process-checkout" method="post">
                                <input type="hidden" name="bookingId" value="<%= booking.getId() %>">
                                <input type="hidden" name="amountToPay" value="<%= checkoutDetails.get("amountToPay") %>">
                                
                                <div class="payment-summary">
                                    <h4 class="mb-3"><i class="bi bi-calculator"></i> Payment Summary</h4>
                                    
                                    <div class="row">
                                        <div class="col-md-8">
                                            <div class="info-row">
                                                <span class="info-label">Room Total:</span>
                                                <span class="info-value">
                                                    <%= currencyFormat.format((Double) checkoutDetails.get("totalRoomPrice")) %> VND
                                                </span>
                                            </div>
                                            <div class="info-row">
                                                <span class="info-label">Services Total:</span>
                                                <span class="info-value">
                                                    <%= currencyFormat.format((Double) checkoutDetails.get("totalServicePrice")) %> VND
                                                </span>
                                            </div>
                                            <div class="info-row">
                                                <span class="info-label">Already Paid Services:</span>
                                                <span class="info-value text-success">
                                                    -<%= currencyFormat.format((Double) checkoutDetails.get("paidServicePrice")) %> VND
                                                </span>
                                            </div>
                                            <div class="info-row">
                                                <span class="info-label">
                                                    Membership Discount (<%= customer != null ? customer.getRank() : "Member" %> - <%= checkoutDetails.get("rankDiscountPercent") %>%):
                                                </span>
                                                <span class="info-value text-success">
                                                    -<%= currencyFormat.format((Double) checkoutDetails.get("rankDiscount")) %> VND
                                                </span>
                                            </div>
                                        </div>
                                        <div class="col-md-4 text-center">
                                            <h5>Amount to Pay:</h5>
                                            <div class="total-amount">
                                                <%= currencyFormat.format((Double) checkoutDetails.get("amountToPay")) %> VND
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <hr>
                                    
                                    <h5>Select Payment Method:</h5>
                                    <div class="payment-methods">
                                        <div class="payment-method">
                                            <label>
                                                <input type="radio" name="paymentMethod" value="cash" checked style="display: none;">
                                                <div class="payment-option selected" onclick="selectPayment(this, 'cash')">
                                                    <i class="bi bi-cash-stack"></i><br>
                                                    <strong>Cash</strong>
                                                </div>
                                            </label>
                                        </div>
                                        <div class="payment-method">
                                            <label>
                                                <input type="radio" name="paymentMethod" value="card" style="display: none;">
                                                <div class="payment-option" onclick="selectPayment(this, 'card')">
                                                    <i class="bi bi-credit-card"></i><br>
                                                    <strong>Card</strong>
                                                </div>
                                            </label>
                                        </div>
                                        <div class="payment-method">
                                            <label>
                                                <input type="radio" name="paymentMethod" value="transfer" style="display: none;">
                                                <div class="payment-option" onclick="selectPayment(this, 'transfer')">
                                                    <i class="bi bi-bank"></i><br>
                                                    <strong>Transfer</strong>
                                                </div>
                                            </label>
                                        </div>
                                    </div>
                                    
                                    <div class="checkout-actions">
                                        <a href="view-booking-detail?bookingId=<%= booking.getId() %>" class="btn btn-secondary me-3">
                                            <i class="bi bi-arrow-left"></i> Back to Details
                                        </a>
                                        <button type="submit" class="btn-checkout" onclick="return confirm('Confirm checkout payment of <%= currencyFormat.format((Double) checkoutDetails.get("amountToPay")) %> VND?')">
                                            <i class="bi bi-check-circle"></i> Complete Checkout
                                        </button>
                                    </div>
                                </div>
                            </form>
                        <% } %>
                    <% } %>
                </div>
            </div>
        </main>
    </div>

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
    </script>
</body>
</html>