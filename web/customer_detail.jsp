<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Customer Details - ${selectedCustomer.fullname}</title>
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
                display: flex;
                justify-content: space-between;
                align-items: center;
            }
            .card-body {
                padding: 1.5rem;
            }
            .back-button {
                padding: 0.5rem 1rem;
                background: #6c757d;
                color: white;
                border: none;
                border-radius: 0.375rem;
                text-decoration: none;
                display: inline-flex;
                align-items: center;
                gap: 0.5rem;
                font-size: 0.875rem;
                transition: background-color 0.2s;
            }
            .back-button:hover {
                background: #545b62;
                color: white;
                text-decoration: none;
            }
            .profile-header {
                display: flex;
                align-items: center;
                gap: 1.5em;
                margin-bottom: 2rem;
                padding: 1.5rem;
                background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
                border-radius: 8px;
            }
            .profile-avatar {
                width: 100px;
                height: 100px;
                border-radius: 50%;
                object-fit: cover;
                border: 4px solid #dee2e6;
            }
            .profile-info h2 {
                margin: 0 0 0.5rem 0;
                font-size: 2rem;
                color: #333;
            }
            .profile-info p {
                margin: 0.25rem 0;
                color: #6c757d;
                font-size: 1.1rem;
            }
            .customer-details {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
                gap: 1rem;
                margin-bottom: 2rem;
            }
            .detail-item {
                display: flex;
                align-items: center;
                gap: 0.75rem;
                padding: 1rem;
                background: #f8f9fa;
                border-radius: 8px;
                border-left: 4px solid #007bff;
            }
            .detail-item i {
                color: #007bff;
                width: 20px;
                font-size: 1.2rem;
            }
            .detail-item strong {
                color: #333;
                font-size: 1rem;
            }
            .tier-badge {
                padding: .5em 1em;
                font-size: .9em;
                font-weight: 700;
                border-radius: .5rem;
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
            .stats-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                gap: 1.5rem;
                margin-bottom: 2rem;
            }
            .stat-item {
                background: linear-gradient(135deg, #fff 0%, #f8f9fa 100%);
                padding: 2rem;
                border-radius: 12px;
                text-align: center;
                border: 1px solid #e9ecef;
                transition: transform 0.2s, box-shadow 0.2s;
            }
            .stat-item:hover {
                transform: translateY(-4px);
                box-shadow: 0 8px 25px rgba(0,0,0,0.1);
            }
            .stat-item .value {
                font-size: 2.5rem;
                font-weight: 700;
                color: #007bff;
                margin-bottom: 0.5rem;
            }
            .stat-item .label {
                font-size: 1rem;
                color: #6c757d;
                font-weight: 500;
            }
            .action-buttons {
                display: flex;
                gap: 1rem;
                margin-bottom: 2rem;
                flex-wrap: wrap;
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
                font-size: 1rem;
            }
            .btn-primary {
                background: linear-gradient(135deg, #007bff, #0056b3);
                color: white;
            }
            .btn-primary:hover {
                background: linear-gradient(135deg, #0056b3, #004085);
                transform: translateY(-1px);
                color: white;
            }
            .btn-secondary {
                background: linear-gradient(135deg, #6c757d, #545b62);
                color: white;
            }
            .btn-secondary:hover {
                background: linear-gradient(135deg, #545b62, #3d4449);
                transform: translateY(-1px);
                color: white;
            }
            .history-tabs .tab-buttons {
                display: flex;
                border-bottom: 2px solid #dee2e6;
                margin-bottom: 1.5rem;
            }
            .history-tabs .tab-button {
                padding: 1rem 1.5rem;
                cursor: pointer;
                border: none;
                background: none;
                color: #6c757d;
                font-weight: 500;
                transition: all 0.2s;
                font-size: 1rem;
            }
            .history-tabs .tab-button.active {
                font-weight: 600;
                color: #007bff;
                border-bottom: 3px solid #007bff;
                background: rgba(0, 123, 255, 0.1);
            }
            .history-tabs .tab-content {
                display: none;
            }
            .history-tabs .tab-content.active {
                display: block;
            }
            .table {
                width: 100%;
                border-collapse: collapse;
                margin-bottom: 1rem;
            }
            .table th, .table td {
                padding: 0.75rem;
                border-bottom: 1px solid #dee2e6;
                text-align: left;
            }
            .table th {
                background-color: #f8f9fa;
                font-weight: 600;
                color: #333;
            }
            .table-hover tbody tr:hover {
                background-color: #f8f9fa;
            }
            .table-sm th, .table-sm td {
                padding: 0.5rem;
                font-size: 0.9em;
            }
            .modal {
                display: none;
                position: fixed;
                z-index: 1050;
                left: 0;
                top: 0;
                width: 100%;
                height: 100%;
                overflow: auto;
                background-color: rgba(0,0,0,0.5);
                justify-content: center;
                align-items: center;
            }
            .modal.show {
                display: flex;
            }
            .modal-dialog {
                background-color: #fff;
                border-radius: 8px;
                width: 100%;
                max-width: 500px;
                box-shadow: 0 10px 30px rgba(0,0,0,.3);
            }
            .modal-content {
                padding: 0;
            }
            .modal-header {
                padding: 1.5rem;
                display: flex;
                justify-content: space-between;
                align-items: center;
                border-bottom: 1px solid #dee2e6;
                background-color: #f8f9fa;
            }
            .modal-header h5 {
                margin: 0;
                font-size: 1.25rem;
                color: #333;
            }
            .modal-body {
                padding: 1.5rem;
            }
            .modal-footer {
                padding: 1rem 1.5rem;
                display: flex;
                justify-content: flex-end;
                gap: 0.5rem;
                border-top: 1px solid #dee2e6;
                background-color: #f8f9fa;
            }
            .btn-close {
                border: none;
                background: none;
                font-size: 1.5rem;
                cursor: pointer;
                color: #6c757d;
            }
            .btn-close:hover {
                color: #333;
            }
            .form-group {
                margin-bottom: 1.5rem;
            }
            .form-group label {
                display: block;
                margin-bottom: 0.5rem;
                font-weight: 500;
                color: #333;
            }
            .form-control, .form-select {
                width: 100%;
                padding: 0.75rem;
                border: 1px solid #ced4da;
                border-radius: 0.375rem;
                font-size: 1rem;
                transition: border-color 0.15s;
            }
            .form-control:focus, .form-select:focus {
                outline: none;
                border-color: #007bff;
                box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
            }
            .badge {
                padding: 0.35em 0.6em;
                font-size: 0.75em;
                font-weight: 600;
                border-radius: 0.25rem;
                color: white;
            }
            .bg-success {
                background-color: #28a745;
            }
            .bg-danger {
                background-color: #dc3545;
            }
            .bg-warning {
                background-color: #ffc107;
                color: #212529;
            }
            .fw-bold {
                font-weight: 700;
            }
            .text-success {
                color: #28a745;
            }
            .text-danger {
                color: #dc3545;
            }
            .text-center {
                text-align: center;
            }
            .text-muted {
                color: #6c757d;
            }
            .mx-2 {
                margin-left: 0.5rem;
                margin-right: 0.5rem;
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
                    <a href="#" class="menu-item"><i class="fas fa-chart-line"></i><span class="menu-text">Dashboard</span></a>
                    <a href="rooms" class="menu-item"><i class="fas fa-bed"></i><span class="menu-text">Manage room</span></a>
                    <a href="#" class="menu-item"><i class="fas fa-comments"></i><span class="menu-text">Manage feedback</span></a>
                    <a href="serviceManage" class="menu-item"><i class="fas fa-concierge-bell"></i><span class="menu-text">Manage service</span></a>
                    <a href="promotions" class="menu-item"><i class="fas fa-tags"></i><span class="menu-text">Manage promotion</span></a>
                    <a href="manager-membership" class="menu-item active"><i class="fas fa-users"></i><span class="menu-text">Manage membership</span></a>
                    <a href="#" class="menu-item logout"><i class="fas fa-sign-out-alt"></i><span class="menu-text">Logout</span></a>
                </div>
            </nav>

            <!-- Main Content -->
            <main class="main-content">
                <header class="content-header">
                    <div class="header-left">
                        <h1 class="page-title">Customer Profile Management</h1>
                    </div>
                    <div class="header-right">
                        <div class="user-info">
                            <i class="fas fa-user-circle"></i>
                            <span>${username}</span>
                        </div>
                    </div>
                </header>

                <div class="content-body">
                    <c:if test="${not empty selectedCustomer}">
                        <!-- Customer Profile Card -->
                        <div class="card">
                            <div class="card-header">
                                <div>
                                    <i class="fas fa-user"></i> Customer Profile
                                </div>
                                <a href="manager-membership" class="back-button">
                                    <i class="fas fa-arrow-left"></i> Back to List
                                </a>
                            </div>
                            <div class="card-body">
                                <div class="profile-header">
                                    <img src="${pageContext.request.contextPath}/${not empty selectedCustomer.avatar_url ? selectedCustomer.avatar_url : 'img/default-avatar.png'}" 
                                         alt="Avatar" class="profile-avatar">
                                    <div class="profile-info">
                                        <h2>${selectedCustomer.fullname}</h2>
                                        <p><i class="fas fa-user"></i> @${selectedCustomer.username}</p>
                                        <p><i class="fas fa-calendar"></i> Member since ${selectedCustomer.create_at}</p>
                                        <span class="tier-badge tier-${selectedCustomer.loyaltyPoint.level}">${selectedCustomer.loyaltyPoint.level}</span>
                                    </div>
                                </div>

                                <div class="customer-details">
                                    <div class="detail-item">
                                        <i class="fas fa-envelope"></i>
                                        <span><strong>Email:</strong> ${selectedCustomer.email}</span>
                                    </div>
                                    <div class="detail-item">
                                        <i class="fas fa-phone"></i>
                                        <span><strong>Phone:</strong> ${selectedCustomer.phonenumber}</span>
                                    </div>
                                    <div class="detail-item">
                                        <i class="fas fa-calendar"></i>
                                        <span><strong>Member Since:</strong> ${selectedCustomer.create_at}</span>
                                    </div>
                                    <div class="detail-item">
                                        <i class="fas fa-crown"></i>
                                        <span><strong>Tier:</strong> ${selectedCustomer.loyaltyPoint.level}</span>
                                    </div>
                                </div>

                                <div class="stats-grid">
                                    <div class="stat-item">
                                        <div class="value"><fmt:formatNumber value="${selectedCustomer.loyaltyPoint.points}" type="number"/></div>
                                        <div class="label">Current Points</div>
                                    </div>
                                    <div class="stat-item">
                                        <div class="value">
                                            <fmt:formatNumber value="${selectedCustomer.loyaltyPoint.totalSpending}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                        </div>
                                        <div class="label">Total Spending</div>
                                    </div>
                                    <div class="stat-item">
                                        <div class="value">${selectedCustomer.loyaltyPoint.lifetimePoints}</div>
                                        <div class="label">Lifetime Points</div>
                                    </div>
                                    <div class="stat-item">
                                        <div class="value">${selectedCustomer.loyaltyPoint.pointsUsed}</div>
                                        <div class="label">Points Used</div>
                                    </div>
                                </div>

                                <div class="action-buttons">
                                    <button class="btn btn-primary" onclick="openModal('adjustPointsModal')">
                                        <i class="fas fa-coins"></i> Adjust Points
                                    </button>
                                </div>

                                <div class="history-tabs">
                                    <div class="tab-buttons">
                                        <button class="tab-button active" onclick="showTab(event, 'points')">
                                            <i class="fas fa-coins"></i> Point History
                                        </button>
                                    </div>
                                    <div id="points-tab" class="tab-content active">
                                        <c:if test="${not empty selectedCustomer.pointHistory}">
                                            <table class="table table-sm table-hover">
                                                <thead>
                                                    <tr>
                                                        <th>Date</th>
                                                        <th>Type</th>
                                                        <th>Points</th>
                                                        <th>Reason</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach var="tx" items="${selectedCustomer.pointHistory}">
                                                        <tr>
                                                            <td><fmt:formatDate value="${tx.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                                                            <td>
                                                                <span class="badge bg-${tx.changeType == 'Earn' ? 'success' : (tx.changeType == 'Redeem' ? 'danger' : 'warning')}">
                                                                    ${tx.changeType}
                                                                </span>
                                                            </td>
                                                            <td>
                                                                <span class="fw-bold text-${tx.pointsChanged > 0 ? 'success' : 'danger'}">
                                                                    ${tx.pointsChanged > 0 ? '+' : ''}<fmt:formatNumber value="${tx.pointsChanged}" type="number"/>
                                                                </span>
                                                            </td>
                                                            <td><c:out value="${tx.reason}"/></td>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                            </table>
                                        </c:if>
                                        <c:if test="${empty selectedCustomer.pointHistory}">
                                            <div class="empty-state">
                                                <i class="fas fa-coins"></i>
                                                <p>No point history found</p>
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:if>
                </div>
            </main>
        </div>

        <!-- MODALS -->
        <c:if test="${not empty selectedCustomer}">
            <!-- Adjust Points Modal -->
            <div id="adjustPointsModal" class="modal">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5><i class="fas fa-coins"></i> Adjust Points for ${selectedCustomer.fullname}</h5>
                            <button type="button" class="btn-close" onclick="closeModal('adjustPointsModal')">×</button>
                        </div>
                        <form action="manager-membership" method="post">
                            <input type="hidden" name="action" value="adjustPoints">
                            <input type="hidden" name="userId" value="${selectedCustomer.id}">
                            <div class="modal-body">
                                <div class="form-group">
                                    <label for="points">Points to Add/Subtract</label>
                                    <input type="number" name="points" id="points" class="form-control" required 
                                           placeholder="Use negative number for subtraction (e.g., -50)">
                                    <small class="form-text text-muted">Current points: <strong><fmt:formatNumber value="${selectedCustomer.loyaltyPoint.points}" type="number"/></strong></small>
                                </div>
                                <div class="form-group">
                                    <label for="reasonPoints">Reason (Required)</label>
                                    <textarea name="reason" id="reasonPoints" rows="3" class="form-control" required 
                                              placeholder="Please provide a reason for this adjustment..."></textarea>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" onclick="closeModal('adjustPointsModal')">Cancel</button>
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-save"></i> Save Changes
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </c:if>

        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
        <script>
            function openModal(modalId) {
                document.getElementById(modalId).classList.add('show');
            }
            
            function closeModal(modalId) {
                document.getElementById(modalId).classList.remove('show');
            }
            
            function showTab(evt, tabName) {
                document.querySelectorAll('.tab-content').forEach(tc => tc.classList.remove('active'));
                document.querySelectorAll('.tab-button').forEach(tb => tb.classList.remove('active'));
                document.getElementById(tabName + '-tab').classList.add('active');
                evt.currentTarget.classList.add('active');
            }

            // Close modal when clicking outside
            document.addEventListener('click', function(event) {
                const modals = document.querySelectorAll('.modal.show');
                modals.forEach(modal => {
                    if (event.target === modal) {
                        closeModal(modal.id);
                    }
                });
            });

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