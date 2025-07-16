<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Manage Membership - Customer List</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/managerStyle.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/custom.css"/>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.0/css/all.min.css">
        <style>
            .card {
                background: #fff;
                border-radius: 8px;
                box-shadow: 0 2px 4px rgba(0,0,0,0.05);
                border: 1px solid #e9ecef;
                margin-bottom: 1.5rem;
            }
            .card-header {
                padding: 1rem 1.5rem;
                background-color: #f8f9fa;
                border-bottom: 1px solid #e9ecef;
                font-weight: 600;
                border-radius: 8px 8px 0 0;
            }
            .card-body {
                padding: 1.5rem;
            }
            .search-form {
                display: flex;
                gap: 1rem;
                margin-bottom: 2rem;
                align-items: end;
            }
            .form-group {
                flex: 1;
                margin-bottom: 0;
            }
            .form-group label {
                display: block;
                margin-bottom: 0.5rem;
                font-weight: 500;
                color: #333;
            }
            .form-control {
                width: 100%;
                padding: 0.75rem;
                border: 1px solid #ced4da;
                border-radius: 0.375rem;
                font-size: 1rem;
                transition: border-color 0.15s;
            }
            .form-control:focus {
                outline: none;
                border-color: #007bff;
                box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
            }
            .btn {
                padding: 0.75rem 1.5rem;
                border: none;
                border-radius: 0.5rem;
                cursor: pointer;
                text-decoration: none;
                display: inline-flex;
                align-items: center;
                gap: 0.5rem;
                font-weight: 500;
                transition: all 0.2s;
                color: white;
            }
            .btn-primary {
                background: linear-gradient(135deg, #007bff, #0056b3);
            }
            .btn-primary:hover {
                background: linear-gradient(135deg, #0056b3, #004085);
                transform: translateY(-1px);
            }
            .btn-sm {
                padding: 0.5rem 1rem;
                font-size: 0.875rem;
            }
            .table {
                width: 100%;
                border-collapse: collapse;
                margin-bottom: 1rem;
            }
            .table th, .table td {
                padding: 1rem 0.75rem;
                border-bottom: 1px solid #dee2e6;
                text-align: left;
                vertical-align: middle;
            }
            .table th {
                background-color: #f8f9fa;
                font-weight: 600;
                color: #333;
                position: sticky;
                top: 0;
                z-index: 10;
            }
            .table-hover tbody tr:hover {
                background-color: #f8f9fa;
            }
            .customer-info {
                display: flex;
                align-items: center;
                gap: 0.75rem;
            }
            .customer-avatar {
                width: 48px;
                height: 48px;
                border-radius: 50%;
                object-fit: cover;
                border: 2px solid #dee2e6;
            }
            .customer-details h6 {
                margin: 0 0 0.25rem 0;
                font-weight: 600;
                font-size: 1rem;
            }
            .customer-details small {
                color: #6c757d;
                font-size: 0.875rem;
            }
            .contact-info div {
                margin: 0.25rem 0;
                font-size: 0.9rem;
                color: #555;
            }
            .contact-info i {
                width: 16px;
                color: #007bff;
                margin-right: 0.5rem;
            }
            .tier-badge {
                padding: .4em .8em;
                font-size: .8em;
                font-weight: 700;
                border-radius: .4rem;
                color: #fff;
                text-transform: uppercase;
                letter-spacing: 0.5px;
                display: inline-block;
            }
            .tier-VIP {
                background: linear-gradient(135deg, #8a2be2, #9932cc);
            }
            .tier-Gold {
                background: linear-gradient(135deg, #ffc107, #ffb300);
                color: #212529;
            }
            .tier-Silver {
                background: linear-gradient(135deg, #6c757d, #5a6268);
            }
            .tier-Member {
                background: linear-gradient(135deg, #0dcaf0, #0bb3d8);
                color: #212529;
            }
            .points-display {
                font-weight: 600;
                color: #007bff;
                font-size: 1.1rem;
            }
            .spending-display {
                color: #28a745;
                font-weight: 500;
                font-size: 0.9rem;
            }
            .table-container {
                max-height: calc(100vh - 300px);
                overflow-y: auto;
                border: 1px solid #dee2e6;
                border-radius: 8px;
            }
            .empty-state {
                text-align: center;
                padding: 3rem;
                color: #6c757d;
            }
            .empty-state i {
                font-size: 3rem;
                margin-bottom: 1rem;
                color: #dee2e6;
            }
            .stats-summary {
                display: grid;
                grid-template-columns: repeat(5, 1fr);
                gap: 1rem;
                margin-bottom: 2rem;
            }
            .stat-card {
                background: linear-gradient(135deg, #fff 0%, #f8f9fa 100%);
                padding: 1.5rem;
                border-radius: 8px;
                text-align: center;
                border: 1px solid #e9ecef;
                transition: transform 0.2s;
            }
            .stat-card:hover {
                transform: translateY(-2px);
            }
            .stat-card .number {
                font-size: 2rem;
                font-weight: 700;
                color: #007bff;
                margin-bottom: 0.5rem;
            }
            .stat-card .label {
                font-size: 1rem;
                color: #6c757d;
                font-weight: 500;
            }
        </style>
    </head>

    <body>
        <div class="app-container">
            <!-- Sidebar -->
            <nav class="sidebar" id="sidebar">
                <div class="sidebar-header">
                    <button class="sidebar-toggle" id="sidebarToggle">
                        <div class="brand"><i class="fas fa-building"></i><span class="brand-text">${branchname}</span></div>
                    </button>
                </div>
                <div class="sidebar-menu">
                    <a href="./manager/dashboard" class="menu-item ">
                        <i class="fas fa-chart-line"></i>
                        <span class="menu-text">Dashboard</span>
                    </a>
                    <a href="rooms" class="menu-item ">
                        <i class="fas fa-bed"></i>
                        <span class="menu-text">Manage room</span>
                    </a>
                    <a href="./manager/roomType" class="menu-item">
                        <i class="fas fa-bed"></i>
                        <span class="menu-text">Manage room type</span>
                    </a>
                    <a href="./manager/revenue" class="menu-item">
                        <i class="fa-solid fa-dollar-sign"></i>
                        <span class="menu-text">Manage Revenue & Expense</span>
                    </a>
                    <a href="./manager/feedback" class="menu-item">
                        <i class="fas fa-comments"></i>
                        <span class="menu-text">Manage feedback</span>
                    </a>
                    <a href="serviceManage" class="menu-item">
                        <i class="fas fa-concierge-bell"></i>
                        <span class="menu-text">Manage service</span>
                    </a>
                    <a href="promotions" class="menu-item">
                        <i class="fas fa-tags"></i>
                        <span class="menu-text">Manage promotion</span>
                    </a>
                    <a href="manager-membership" class="menu-item active">
                        <i class="fas fa-users"></i>
                        <span class="menu-text">Manage membership</span>
                    </a>
                    <a href="../login?action=logout" class="menu-item logout">
                        <i class="fas fa-sign-out-alt"></i>
                        <span class="menu-text">logout</span>
                    </a>
                </div>
            </nav>

            <!-- Main Content -->
            <main class="main-content">
                <header class="content-header">
                    <div class="header-left">
                        <h1 class="page-title">Manage Membership</h1>
                    </div>
                    <div class="header-right">
                        <div class="user-info">
                            <i class="fas fa-user-circle"></i>
                            <span>${username}</span>
                        </div>
                    </div>
                </header>

                <div class="content-body">
                    <!-- Search Section -->
                    <div class="card">
                        <div class="card-header">
                            <i class="fas fa-search"></i> Customer Search & Management
                        </div>
                        <div class="card-body">
                            <form action="manager-membership" method="post" class="search-form">
                                <input type="hidden" name="action" value="search">
                                <div class="form-group">
                                    <label for="searchTerm">Search by Name, Email, or Phone</label>
                                    <input type="text" id="searchTerm" name="searchTerm" value="${searchTerm}" 
                                           class="form-control" placeholder="Enter customer name, email, or phone...">
                                </div>
                                <div class="form-group">
                                    <label for="rankFilter">Filter by Rank</label>
                                    <select id="rankFilter" name="rankFilter" class="form-control">
                                        <option value="">-- All Ranks --</option>
                                        <option value="Member" ${rankFilter == 'Member' ? 'selected' : ''}>Member</option>
                                        <option value="Silver" ${rankFilter == 'Silver' ? 'selected' : ''}>Silver</option>
                                        <option value="Gold" ${rankFilter == 'Gold' ? 'selected' : ''}>Gold</option>
                                        <option value="VIP" ${rankFilter == 'VIP' ? 'selected' : ''}>VIP</option>
                                    </select>
                                </div>
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-search"></i> Search
                                </button>
                            </form>
                        </div>
                    </div>

                    <c:set var="customers" value="${not empty searchResults ? searchResults : allCustomers}" />
                    <c:set var="totalCustomer" value="${fn:length(customers)}" />
                    <c:set var="memberCount" value="0" />
                    <c:set var="silverCount" value="0" />
                    <c:set var="goldCount" value="0" />
                    <c:set var="vipCount" value="0" />
                    <c:forEach var="customer" items="${customers}">
                        <c:choose>
                            <c:when test="${customer.loyaltyPoint.level == 'Member'}">
                                <c:set var="memberCount" value="${memberCount + 1}" />
                            </c:when>
                            <c:when test="${customer.loyaltyPoint.level == 'Silver'}">
                                <c:set var="silverCount" value="${silverCount + 1}" />
                            </c:when>
                            <c:when test="${customer.loyaltyPoint.level == 'Gold'}">
                                <c:set var="goldCount" value="${goldCount + 1}" />
                            </c:when>
                            <c:when test="${customer.loyaltyPoint.level == 'VIP'}">
                                <c:set var="vipCount" value="${vipCount + 1}" />
                            </c:when>
                        </c:choose>
                    </c:forEach>

                    <!-- Stats Summary -->
                    <c:if test="${not empty customers}">
                        <div class="stats-summary">
                            <div class="stat-card">
                                <div class="number">${totalCustomer}</div>
                                <div class="label">Total Customers</div>
                            </div>
                            <div class="stat-card">
                                <div class="number">${memberCount}</div>
                                <div class="label">Member</div>
                            </div>
                            <div class="stat-card">
                                <div class="number">${silverCount}</div>
                                <div class="label">Silver</div>
                            </div>
                            <div class="stat-card">
                                <div class="number">${goldCount}</div>
                                <div class="label">Gold</div>
                            </div>
                            <div class="stat-card">
                                <div class="number">${vipCount}</div>
                                <div class="label">VIP</div>
                            </div>
                        </div>
                    </c:if>

                    <!-- Results Table -->
                    <c:if test="${not empty customers}">
                        <div class="card">
                            <div class="card-header">
                                <i class="fas fa-list"></i>
                                <c:choose>
                                    <c:when test="${not empty searchTerm || not empty rankFilter}">
                                        Search Results (${totalCustomer} found)
                                    </c:when>
                                    <c:otherwise>
                                        All Customers (${totalCustomer} total)
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="table-container">
                                <table class="table table-hover">
                                    <thead>
                                        <tr>
                                            <th style="width: 25%;">Customer</th>
                                            <th style="width: 25%;">Contact Info</th>
                                            <th style="width: 15%;">Tier</th>
                                            <th style="width: 15%;">Points</th>
                                            <th style="width: 15%;">Total Spending</th>
                                            <th style="width: 5%;">Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="customer" items="${customers}">
                                            <tr>
                                                <td>
                                                    <div class="customer-info">
                                                        <img src="${pageContext.request.contextPath}/${not empty customer.avatar_url ? customer.avatar_url : 'img/default-avatar.png'}"
                                                             alt="Avatar" class="customer-avatar">
                                                        <div class="customer-details">
                                                            <h6>${customer.fullname}</h6>
                                                            <small>@${customer.username}</small>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td>
                                                    <div class="contact-info">
                                                        <div><i class="fas fa-envelope"></i> ${customer.email}</div>
                                                        <div><i class="fas fa-phone"></i> ${customer.phonenumber}</div>
                                                    </div>
                                                </td>
                                                <td>
                                                    <span class="tier-badge tier-${customer.loyaltyPoint.level}">
                                                        ${customer.loyaltyPoint.level}
                                                    </span>
                                                </td>
                                                <td>
                                                    <div class="points-display">
                                                        <fmt:formatNumber value="${customer.loyaltyPoint.points}" type="number"/> pts
                                                    </div>
                                                </td>
                                                <td>
                                                    <div class="spending-display">
                                                        <fmt:formatNumber value="${customer.loyaltyPoint.totalSpending}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                                    </div>
                                                </td>
                                                <td>
                                                    <a href="manager-membership?action=view&userId=${customer.id}"
                                                       class="btn btn-sm btn-primary" title="Manage Customer">
                                                        <i class="fas fa-cog"></i>
                                                    </a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </c:if>

                    <!-- Empty State -->
                    <c:if test="${empty customers}">
                        <div class="card">
                            <div class="card-body empty-state">
                                <i class="fas fa-users"></i>
                                <h3>No Customers Found</h3>
                                <p>No customer data available.</p>
                            </div>
                        </div>
                    </c:if>
                </div>
            </main>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
        <script>
            // Handle success/error messages
            var errorMsg = "<c:out value='${sessionScope.error}'/>";
            var successMsg = "<c:out value='${sessionScope.success}'/>";

            if (errorMsg && errorMsg.trim() !== "") {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: errorMsg,
                    confirmButtonColor: '#dc3545'
                });
            <% session.removeAttribute("error"); %>
            } else if (successMsg && successMsg.trim() !== "") {
                Swal.fire({
                    icon: 'success',
                    title: 'Success',
                    text: successMsg,
                    confirmButtonColor: '#28a745'
                });
            <% session.removeAttribute("success"); %>
            }
        </script>
    </body>
</html>