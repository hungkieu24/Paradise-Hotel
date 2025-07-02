<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="Model.UserAccount" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.Booking" %>
<%@ page import="Model.Service" %>
<%
    UserAccount user = (UserAccount) request.getAttribute("user");
    String rank = (String) request.getAttribute("rank");
    if (rank == null && user != null && user.getRank() != null) rank = user.getRank();
    String fullName = (user != null && user.getFullName() != null) ? user.getFullName() : "";
    // Bây giờ chúng ta nhận một đối tượng booking duy nhất
    Booking booking = (Booking) request.getAttribute("booking");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Booking Service Detail</title>
    <link rel="stylesheet" href="css/style_1.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        .avatar-img { width: 110px; height: 110px; object-fit: cover; border-radius: 50%; margin-bottom: 12px;}
        .main-content { margin-left: 220px; padding: 0; min-height: 100vh; background: #f4f5fa; }
        .content-body { margin: 35px 40px 0 40px; }
        .service-table th, .service-table td { vertical-align: middle; }
        .special-request {
            background: #ffe9cc;
            color: #b45f06;
            font-size: 1rem;
            display: inline-block;
            padding: 4px 12px;
            border-radius: 5px;
            margin-top: 3px;
            font-weight: 500;
        }
        .service-status-paid { background: #d4edda; color: #155724; }
        .service-status-unpaid { background: #fff3cd; color: #856404; }
        .service-status-unknown { background: #e2e3e5; color: #383d41; }
    </style>
</head>
<body>
<div class="app-container">
    <%-- Sidebar include: Đảm bảo branchName được khai báo trong sidebar.jsp hoặc khai báo ở đây nếu cần dùng --%>
    <%@ include file="sidebar.jsp" %>
    <main class="main-content">
        <div class="content-body">
            <a href="javascript:history.back()" class="btn btn-outline-secondary mb-3">
                <i class="bi bi-arrow-left"></i> Back
            </a>
            <div class="card shadow-sm mb-4">
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
                            <div class="mt-2">
                                <span class="badge bg-secondary fs-6">
                                    <i class="bi bi-star"></i> <%= (rank != null && !rank.isEmpty()) ? rank : "N/A" %>
                                </span>
                            </div>
                        </div>
                        <div class="col-md-9">
                            <h4><i class="bi bi-person"></i> <%= user.getUsername() %></h4>
                            <p><i class="bi bi-card-text"></i> <strong>Full Name:</strong>
                                <%= (fullName != null && !fullName.isEmpty()) ? fullName : "N/A" %>
                            </p>
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
            
            <!-- Booking and Service Details -->
            <div class="card shadow-sm">
                <div class="card-header bg-primary text-white">
                    <h3 class="mb-0"><i class="bi bi-calendar-check"></i> Booking Services & Special Requests</h3>
                </div>
                <div class="card-body">
                <% if (booking == null) { %>
                    <div class="alert alert-info mb-0">Booking not found.</div>
                <% } else { %>
                    <%-- Không còn vòng lặp, hiển thị trực tiếp chi tiết của booking --%>
                    <div class="pb-3">
                        <div class="mb-2">
                            <span class="ms-3">
                                <i class="bi bi-door-closed"></i>
                                <strong>Room Types:</strong>
                                <%= booking.getRoomTypes() != null ? booking.getRoomTypes() : "-" %>
                            </span>
                            <span class="ms-3">
                                <i class="bi bi-calendar-event"></i>
                                <strong>Check-in:</strong>
                                <%= booking.getCheckIn() != null ? new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(booking.getCheckIn()) : "-" %>
                            </span>
                            <span class="ms-3">
                                <i class="bi bi-calendar-x"></i>
                                <strong>Check-out:</strong>
                                <%= booking.getCheckOut() != null ? new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(booking.getCheckOut()) : "-" %>
                            </span>
                            <span class="ms-3">
                                <i class="bi bi-cash-stack"></i>
                                <strong>Total:</strong>
                                <%= booking.getTotalPrice() != 0 ? String.format("%,.0f", booking.getTotalPrice()) : "0" %> đ
                            </span>
                        </div>
                        <div class="mt-2">
                            <strong>Status:</strong>
                            <% String stat = booking.getStatus() != null ? booking.getStatus().toLowerCase() : ""; %>
                            <span class="badge
                                <% if(stat.contains("checkedin")) { %>bg-success
                                <% } else if(stat.contains("checkedout")) { %>bg-secondary
                                <% } else if(stat.contains("cancel")) { %>bg-danger
                                <% } else if(stat.contains("confirmed")) { %>bg-primary
                                <% } else { %>bg-dark<% } %>">
                                <%= booking.getStatus() %>
                            </span>
                        </div>
                        <div class="mt-3">
                            <h6>Services in this booking:</h6>
                            <%
                                List<Service> services = booking.getServices();
                                if (services != null && !services.isEmpty()) {
                            %>
                            <div class="table-responsive">
                                <table class="table table-bordered table-sm align-middle mb-0">
                                    <thead class="table-light">
                                        <tr>
                                            <th>Service Name</th>
                                            <th>Quantity</th>
                                            <th>Status</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% for (Service s : services) { %>
                                        <tr>
                                            <td><%= s.getName() %></td>
                                            <td><%= s.getQuantity() %></td>
                                            <td>
                                                <%
                                                    String svStatus = s.getBookingServiceStatus();
                                                    String statusClass = "service-status-unknown";
                                                    if ("Paid".equalsIgnoreCase(svStatus)) statusClass = "service-status-paid";
                                                    else if ("Unpaid".equalsIgnoreCase(svStatus)) statusClass = "service-status-unpaid";
                                                %>
                                                <span class="<%= statusClass %> badge px-3 py-2">
                                                    <%= (svStatus != null && !svStatus.isEmpty()) ? svStatus : "Unknown" %>
                                                </span>
                                            </td>
                                        </tr>
                                        <% } %>
                                    </tbody>
                                </table>
                            </div>
                            <% } else { %>
                                <span class="text-muted">No services in this booking</span>
                            <% } %>
                        </div>
                        <div class="mt-2">
                            <strong>Special Request:</strong>
                            <% if (booking.getNote() != null && !booking.getNote().isEmpty()) { %>
                                <span class="special-request"><i class="bi bi-chat-left-text"></i> <%= booking.getNote() %></span>
                            <% } else { %>
                                <span class="text-muted">-</span>
                            <% } %>
                        </div>
                    </div>
                <% } %>
                </div>
            </div>
        </div>
    </main>
</div>
<script src="js/main.js"></script>
</body>
</html>