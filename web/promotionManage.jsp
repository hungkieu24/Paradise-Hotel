<%-- 
    Document   : promotionManage
    Created on : Jun 25, 2025
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Manager Promotion</title>
        <link rel="stylesheet" href="./css/managerStyle.css">
        <link rel="stylesheet" href="./css/custom.css"/>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <style>
            .modal {
                display: none;
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(0, 0, 0, 0.5);
                z-index: 1000;
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
                max-width: 600px; /* Adjust to desired modal width */
                margin: 0 auto;
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
            }
            .modal-content {
                padding: 20px;
            }
            .modal-header, .modal-footer {
                display: flex;
                justify-content: space-between;
                align-items: center;
            }
            .tab-buttons {
                display: flex;
                gap: 1rem;
                margin-bottom: 1rem;
                border-bottom: 1px solid hsl(var(--border));
            }
            .tab-button {
                padding: 0.5rem 1rem;
                cursor: pointer;
                border: 1px solid transparent;
                border-bottom: none;
                background: none;
                color: hsl(var(--muted-foreground));
                transition: all 0.2s ease;
            }
            .tab-button.active {
                border-color: hsl(var(--border));
                border-bottom: 2px solid hsl(var(--primary));
                color: hsl(var(--primary));
                font-weight: 600;
            }
            .tab-content {
                display: none;
            }
            .tab-content.active {
                display: block;
            }
            /*            chỉnh popup ra giữa màn hình */
            /*            stype phan trang*/
            .pagination {
                display: flex;
                justify-content: center;
                align-items: center;
                margin-top: 20px;
            }
            .pagination button {
                margin: 0 5px;
                padding: 8px 12px;
                border: 1px solid #007bff;
                background-color: white;
                color: #007bff;
                cursor: pointer;
                transition: background-color 0.3s, color 0.3s;
                border-radius: 4px;
            }
            .pagination button:hover:not(:disabled) {
                background-color: #007bff;
                color: white;
            }
            .pagination button:disabled {
                background-color: #007bff;
                color: white;
                cursor: default;
            }
            .pagination button:focus {
                outline: none;
            }
            .error-message {
                color: red;
                font-size: 0.8rem;
                margin-top: 5px;
                display: none;
                margin-left: 5px; /* Adjusted for inline display */
            }
            .invalid {
                border-color: red;
            }
        </style>
    </head>

    <body>
        <div class="app-container">
            <!-- Sidebar -->
            <nav class="sidebar" id="sidebar">
                <div class="sidebar-header">
                    <button class="sidebar-toggle" id="sidebarToggle">
                        <div class="brand">
                            <i class="fas fa-building"></i>
                            <span class="brand-text">${branchname}</span>
                        </div>
                    </button>
                </div>
                <div class="sidebar-menu">
                    <a href="#" class="menu-item">
                        <i class="fas fa-chart-line"></i>
                        <span class="menu-text">Dashboard</span>
                    </a>
                    <a href="rooms" class="menu-item">
                        <i class="fas fa-bed"></i>
                        <span class="menu-text">Manage room</span>
                    </a>
                    <a href="#" class="menu-item">
                        <i class="fas fa-comments"></i>
                        <span class="menu-text">Manage feedback</span>
                    </a>
                    <a href="serviceManage" class="menu-item">
                        <i class="fas fa-concierge-bell"></i>
                        <span class="menu-text">Manage service</span>
                    </a>
                    <a href="promotions" class="menu-item active">
                        <i class="fas fa-tags"></i>
                        <span class="menu-text">Manage promotion</span>
                    </a>
                    <a href="#" class="menu-item">
                        <i class="fas fa-users"></i>
                        <span class="menu-text">Manage membership</span>
                    </a>
                    <a href="#" class="menu-item logout">
                        <i class="fas fa-sign-out-alt"></i>
                        <span class="menu-text">logout</span>
                    </a>
                </div>
            </nav>

            <!-- Main Content -->
            <main class="main-content">
                <header class="content-header">
                    <div class="header-left">
                        <h1 class="page-title">Manager Promotion</h1>
                    </div>
                    <div class="header-right">
                        <div class="user-info">
                            <i class="fas fa-user-circle"></i>
                            <span>${username}</span>
                        </div>
                    </div>
                </header>
                <div class="content-body">
                    <div class="rooms-container">
                        <div class="tab-buttons">
                            <div class="tab-button"  data-tab="seasonal" onclick="handleTabClick('seasonal')">Seasonal Promotions</div>
                            <div class="tab-button"   data-tab="vouchers" onclick="handleTabClick('vouchers')">Vouchers</div>
                        </div>

                        <!-- Seasonal Promotions Tab Content -->
                        <div id="seasonal-tab" class="tab-content active">
                            <div class="page-actions">
                                <form action="searchPromotions" method="get"> <!-- thanh search -->
                                    <div class="search-box">
                                        <i class="fas fa-search"></i>
                                        <input type="text" id="promotionSearch" name="search" placeholder="Search promotions..." value="${param.search}" onchange="this.form.submit()">
                                    </div>
                                </form>
                                <!-- button để mở popup add -->
                                <button class="btn btn-primary" onclick="openPromotionModal()">
                                    <i class="fas fa-plus"></i>
                                    Add seasonal Promotion
                                </button>
                            </div>
                            <form action="searchPromotions" method="get"><!-- filter promotion -->
                                <div class="filters">
                                    <select id="statusFilter" name="status" onchange="this.form.submit()">
                                        <option value="">All Status</option>
                                        <option value="Active"${param.status == 'Active' ? 'selected' : ''}>Active</option>
                                        <option value="Inactive" ${param.status == 'Inactive' ? 'selected' : ''}>Inactive</option>
                                    </select>
                                    <input type="Date" style="border:1px solid hsl(220 13% 91%); border-radius: 0.375rem; width: 100px" id="startDate" name="startDate" value="${param.startDate}" onchange="this.form.submit()">
                                    <input type="Date"style="border:1px solid hsl(220 13% 91%); border-radius: 0.375rem; width: 100px"  id="endDate" name="endDate" value="${param.endDate}" onchange="this.form.submit()">
                                </div>
                            </form>
                            <div class="rooms-table"><!-- hien thi list -->
                                <table>
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Name</th>
                                            <th>Description</th>
                                            <th>Discount percent</th>
                                            <th>Discount amount</th>
                                            <th>Start date</th>
                                            <th>End date</th>
                                            <th>Status</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="promotion" items="${promotions}">
                                            <tr>
                                                <td>${promotion.id}</td>
                                                <td>${promotion.name}</td>
                                                <td>${promotion.description}</td>
                                                <td>${promotion.discount_percent}%</td>
                                                <td>${promotion.discount_amount}VND</td>
                                                <td>${promotion.startDate}</td>
                                                <td>${promotion.endDate}</td>
                                                <td><span class="status-badge status-${promotion.status.toLowerCase()}">${promotion.status}</span></td>
                                                <!-- action edit delete -->
                                                <td>
                                                    <button class="btn btn-sm btn-secondary" onclick="editPromotion(this)"
                                                            data-id = "${promotion.id}"
                                                            data-name = "${promotion.name}"
                                                            data-desciption = "${promotion.description}"
                                                            data-percent="${promotion.discount_percent}"
                                                            data-amount="${promotion.discount_amount}"
                                                            data-description="${promotion.description}"
                                                            data-start="${promotion.startDate}"
                                                            data-end="${promotion.endDate}">
                                                        <i class="fas fa-edit"></i>
                                                    </button>
                                                    <button class="btn btn-sm btn-danger" onclick="deletePromotion(${promotion.id})">
                                                        <i class="fas fa-trash"></i>
                                                    </button>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                            <!-- phân trang -->
                            <div class="pagination">
                                <c:if test="${currentPage > 1}">
                                    <form action="promotions" method="get" style="display:inline;">
                                        <input type="hidden" name="page" value="${currentPage - 1}">
                                        <input type="hidden" name="size" value="${pageSize}">
                                        <button type="submit">Previous</button>
                                    </form>
                                </c:if>
                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <form action="promotions" method="get" style="display:inline;">
                                        <input type="hidden" name="page" value="${i}">
                                        <input type="hidden" name="size" value="${pageSize}">
                                        <button type="submit" ${i == currentPage ? 'disabled' : ''}>${i}</button>
                                    </form>
                                </c:forEach>
                                <c:if test="${currentPage < totalPages}">
                                    <form action="promotions" method="get" style="display:inline;">
                                        <input type="hidden" name="page" value="${currentPage + 1}">
                                        <input type="hidden" name="size" value="${pageSize}">
                                        <button type="submit">Next</button>
                                    </form>
                                </c:if>
                            </div>
                        </div>

                        <!-- Vouchers Tab Content -->
                        <div id="vouchers-tab" class="tab-content">
                            <div class="page-actions">
                                <!-- thanh search cho voucher -->
                                <form action="vouchers" method="get">
                                    <div class="search-box">
                                        <i class="fas fa-search"></i>
                                        <input type="text" id="voucherSearch" name="search" placeholder="Search vouchers..." value="${param.search}" onchange="this.form.submit()">
                                    </div>
                                </form>
                                <!-- button để mở popup voucher add -->
                                <button class="btn btn-primary" onclick="openVoucherModal()">
                                    <i class="fas fa-plus"></i>
                                    Add Voucher
                                </button>
                            </div>
                            <!-- fillter search -->
                            <form action="vouchers" method="get">
                                <div class="filters">
                                    <select id="statusFilter" name="status" onchange="this.form.submit()">
                                        <option value="">All Status</option>
                                        <option value="Active"${param.status == 'Active' ? 'selected' : ''}>Active</option>
                                        <option value="Inactive" ${param.status == 'Inactive' ? 'selected' : ''}>Inactive</option>
                                    </select>
                                    <!-- chuyen thanh fillter khoảng thời gian và them filter min_price -->
                                    <input type="date" style="border:1px solid hsl(220 13% 91%); border-radius: 0.375rem" id="fromDateFilter" name="fromDate" value="${param.fromDate}" onchange="this.form.submit()">
                                    <input type="date" style="border:1px solid hsl(220 13% 91%); border-radius: 0.375rem"  id="toDateFilter" name="toDate" value="${param.toDate}" onchange="this.form.submit()">
                                </div>
                            </form>
                            <div class="rooms-table">
                                <table>
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Code</th>
                                            <th>Description</th>
                                            <th>Discount percent</th>
                                            <th>Discount amount</th>
                                            <th>Min price</th>
                                            <th>Total quantity</th>
                                            <th>Used quantity</th>
                                            <th>From</th>
                                            <th>To</th>
                                            <th>Status</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="voucher" items="${vouchers}">
                                            <tr>
                                                <td>${voucher.id}</td>
                                                <td>${voucher.code}</td>
                                                <td>${voucher.description}</td>
                                                <td>${voucher.discount_percent}%</td>
                                                <td>${voucher.discount_amount}VND</td>
                                                <td>${voucher.min_price} VND</td>
                                                <td>${voucher.total_quantity}</td>
                                                <td>${voucher.used_quantity}</td>
                                                <td>${voucher.valid_from}</td>
                                                <td>${voucher.valid_to}</td>
                                                <td>${voucher.status}</td>
                                                <!-- action voucher -->
                                                <td>
                                                    <button class="btn btn-sm btn-secondary" onclick="editVoucher(this)"
                                                            data-id="${voucher.id}"
                                                            data-code="${voucher.code}"
                                                            data-description="${voucher.description}"
                                                            data-percent="${voucher.discount_percent}"
                                                            data-amount="${voucher.discount_amount}"
                                                            data-minPrice="${voucher.min_price}"
                                                            data-total="${voucher.total_quantity}"
                                                            data-used="${voucher.used_quantity}"
                                                            data-from="${voucher.valid_from}"
                                                            data-to="${voucher.valid_to}"
                                                            data-status="${voucher.status}">
                                                        <i class="fas fa-edit"></i>
                                                    </button>
                                                    <button class="btn btn-sm btn-danger" onclick="deleteVoucher(${voucher.id})">
                                                        <i class="fas fa-trash"></i>
                                                    </button>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                            <!-- phân trang cho voucher list -->
                            <div class="pagination">
                                <c:if test="${currentPage > 1}">
                                    <form action="vouchers" method="get" style="display:inline;">
                                        <input type="hidden" name="page" value="${currentPage - 1}">
                                        <input type="hidden" name="size" value="${pageSize}">
                                        <button type="submit">Previous</button>
                                    </form>
                                </c:if>
                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <form action="vouchers" method="get" style="display:inline;">
                                        <input type="hidden" name="page" value="${i}">
                                        <input type="hidden" name="size" value="${pageSize}">
                                        <button type="submit" ${i == currentPage ? 'disabled' : ''}>${i}</button>
                                    </form>
                                </c:forEach>
                                <c:if test="${currentPage < totalPages}">
                                    <form action="vouchers" method="get" style="display:inline;">
                                        <input type="hidden" name="page" value="${currentPage + 1}">
                                        <input type="hidden" name="size" value="${pageSize}">
                                        <button type="submit">Next</button>
                                    </form>
                                </c:if>
                            </div>
                        </div>

                        <!-- Modal for Add Seasonal Promotion -->
                        <div class="modal" id="promotionModal">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h3 id="modalTitle">Add new Promotion</h3>
                                        <button class="modal-close" onclick="closeModal()">×</button>
                                    </div>
                                    <form id="promotionForm" action="addPromotion" method="post" onsubmit="return validateForm('promotionForm')">
                                        <div class="modal-body">
                                            <div class="form-group">
                                                <label for="promotion_name">Promotion Name *</label>
                                                <input type="text" id="promotion_name" name="promotion_name" required>
                                                <span id="promotion_name_error" class="error-message"></span>
                                            </div>
                                            <div class="form-group">
                                                <label for="discount">Discount (%) *</label>
                                                <input type="number" id="discount_percent" name="discount_percent" min="0" max="100" oninput="handleDiscountInput('percent')">
                                                <span id="discount_percent_error" class="error-message"></span>
                                                <label for="discount">Discount amount(VND)</label>
                                                <input type="number" id="discount_amount" name="discount_amount" min="0" oninput="handleDiscountInput('amount')">
                                                <span id="discount_amount_error" class="error-message"></span>
                                            </div>
                                            <div class="form-group">
                                                <label for="description">Description *</label>
                                                <textarea id="description" name="description" rows="3" placeholder="description...."></textarea>
                                                <span id="description_error" class="error-message"></span>
                                            </div>
                                            <div class="form-group">
                                                <label for="start_date">Start Date *</label>
                                                <input type="date" id="start_date" name="start_date" required>
                                                <span id="start_date_error" class="error-message"></span>
                                            </div>
                                            <div class="form-group">
                                                <label for="end_date">End Date *</label>
                                                <input type="date" id="end_date" name="end_date" required>
                                                <span id="end_date_error" class="error-message"></span>
                                            </div>
                                            <div class="form-group">
                                                <label for="status">Status *</label>
                                                <select id="status" name="status" required>
                                                    <option value="Active">Active</option>
                                                    <option value="Inactive">Inactive</option>
                                                </select>
                                                <span id="status_error" class="error-message"></span>
                                            </div>
                                        </div>
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-secondary" onclick="closeModal()">Cancel</button>
                                            <button type="submit" class="btn btn-primary">
                                                <i class="fas fa-save"></i>
                                                Save
                                            </button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>

                        <!-- Modal for Add Voucher -->
                        <div class="modal" id="voucherModal">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h3 id="modalTitle">Add new Voucher</h3>
                                        <button class="modal-close" onclick="closeModal()">×</button>
                                    </div>
                                    <form id="voucherForm" action="addVoucher" method="post" onsubmit="return validateForm('voucherForm')">
                                        <div class="modal-body">
                                            <div class="form-group">
                                                <label for="voucher_code">Voucher Code *</label>
                                                <input type="text" id="voucher_code" name="voucher_code" required>
                                                <span id="voucher_code_error" class="error-message"></span>
                                            </div>
                                            <div class="form-group">
                                                <label for="description">Description *</label>
                                                <input type="text" id="description" name="description" required>
                                                <span id="description_error" class="error-message"></span>
                                            </div>
                                            <div class="form-group">
                                                <label for="discount">Discount (%) *</label>
                                                <input type="number" id="discount_percent" name="discount_percent" min="0" max="100" oninput="handleDiscountInput('percent')">
                                                <span id="discount_percent_error" class="error-message"></span>
                                                <label for="discount">Discount amount(VND)</label>
                                                <input type="number" id="discount_amount" name="discount_amount" min="0" oninput="handleDiscountInput('amount')">
                                                <span id="discount_amount_error" class="error-message"></span>
                                            </div>
                                            <div class="form-group">
                                                <label for="min_price">Min Price (VND) *</label>
                                                <input type="number" id="min_price" name="min_price" min="0" step="1000" required>
                                                <span id="min_price_error" class="error-message"></span>
                                            </div>
                                            <div class="form-group">
                                                <label for="total_quantity">Total Quantity *</label>
                                                <input type="number" id="total_quantity" name="total_quantity" min="0" required>
                                                <span id="total_quantity_error" class="error-message"></span>
                                            </div>
                                            <div class="form-group">
                                                <label for="from_date">From Date *</label>
                                                <input type="date" id="from_date" name="from_date" required>
                                                <span id="from_date_error" class="error-message"></span>
                                            </div>
                                            <div class="form-group">
                                                <label for="to_date">To Date *</label>
                                                <input type="date" id="to_date" name="to_date" required>
                                                <span id="to_date_error" class="error-message"></span>
                                            </div>
                                            <div class="form-group">
                                                <label for="status">Status *</label>
                                                <select id="status" name="status" required>
                                                    <option value="Active">Active</option>
                                                    <option value="Inactive">Inactive</option>
                                                </select>
                                                <span id="status_error" class="error-message"></span>
                                            </div>
                                        </div>
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-secondary" onclick="closeModal()">Cancel</button>
                                            <button type="submit" class="btn btn-primary">
                                                <i class="fas fa-save"></i>
                                                Save
                                            </button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>

                        <!-- Modal for Edit Promotion -->
                        <div class="modal" id="editPromotionModal">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h3 id="modalEditTitle">Edit Promotion</h3>
                                        <button class="modal-close" onclick="closeEditModal()">×</button>
                                    </div>
                                    <form id="editPromotionForm" action="editPromotion" method="post" onsubmit="return validateForm('editPromotionForm')">
                                        <input type="hidden" id="edit_promotion_id" name="promotion_id">
                                        <div class="modal-body">
                                            <div class="form-group">
                                                <label for="edit_promotion_name">Promotion Name *</label>
                                                <input type="text" id="edit_promotion_name" name="promotion_name" required>
                                                <span id="edit_promotion_name_error" class="error-message"></span>
                                            </div>
                                            <div class="form-group">
                                                <label for="edit_discount">Discount (%) *</label>
                                                <input type="number" id="edit_discount_percent" name="discount_percent" min="0" max="100" oninput="handleDiscountInput('percent')">
                                                <span id="edit_discount_percent_error" class="error-message"></span>
                                                <label for="edit_discount">Discount amount(VND)</label>
                                                <input type="number" id="edit_discount_amount" name="discount_amount" min="0" oninput="handleDiscountInput('amount')">
                                                <span id="edit_discount_amount_error" class="error-message"></span>
                                            </div>
                                            <div class="form-group">
                                                <label for="edit_description">Description *</label>
                                                <textarea id="edit_description" name="description" rows="3" placeholder="description...."></textarea>
                                                <span id="edit_description_error" class="error-message"></span>
                                            </div>
                                            <div class="form-group">
                                                <label for="edit_start_date">Start Date *</label>
                                                <input type="date" id="edit_start_date" name="start_date" required>
                                                <span id="edit_start_date_error" class="error-message"></span>
                                            </div>
                                            <div class="form-group">
                                                <label for="edit_end_date">End Date *</label>
                                                <input type="date" id="edit_end_date" name="end_date" required>
                                                <span id="edit_end_date_error" class="error-message"></span>
                                            </div>
                                            <div class="form-group">
                                                <label for="edit_status">Status *</label>
                                                <select id="edit_status" name="status" required>
                                                    <option value="Active">Active</option>
                                                    <option value="Inactive">Inactive</option>
                                                </select>
                                                <span id="edit_status_error" class="error-message"></span>
                                            </div>
                                        </div>
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-secondary" onclick="closeEditModal()">Cancel</button>
                                            <button type="submit" class="btn btn-primary">
                                                <i class="fas fa-save"></i>
                                                Update
                                            </button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>

                        <!-- Modal for Edit Voucher -->
                        <div class="modal" id="editVoucherModal">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h3 id="modalEditTitle">Edit Voucher</h3>
                                        <button class="modal-close" onclick="closeEditModal()">×</button>
                                    </div>
                                    <form id="editVoucherForm" action="editVoucher" method="post" onsubmit="return validateForm('editVoucherForm')">
                                        <input type="hidden" id="edit_voucher_id" name="voucher_id">
                                        <div class="modal-body">
                                            <div class="form-group">
                                                <label for="edit_voucher_code">Voucher Code *</label>
                                                <input type="text" id="edit_voucher_code" name="voucher_code" required>
                                                <span id="edit_voucher_code_error" class="error-message"></span>
                                            </div>
                                            <div class="form-group">
                                                <label for="edit_description">Description *</label>
                                                <textarea id="edit_voucher_description" name="description" rows="3" placeholder="description...."></textarea>
                                                <span id="edit_description_error" class="error-message"></span>
                                            </div>
                                            <div class="form-group">
                                                <label for="edit_discount">Discount (%) *</label>
                                                <input type="number" id="edit_voucher_discount_percent" name="discount_percent" min="0" max="100" oninput="handleDiscountInput('percent')">
                                                <span id="edit_voucher_discount_percent_error" class="error-message"></span>
                                                <label for="edit_discount">Discount amount(VND)</label>
                                                <input type="number" id="edit_voucher_discount_amount" name="discount_amount" min="0" oninput="handleDiscountInput('amount')">
                                                <span id="edit_voucher_discount_amount_error" class="error-message"></span>
                                            </div>
                                            <div class="form-group">
                                                <label for="edit_min_price">Min Price (VND) *</label>
                                                <input type="number" id="edit_min_price" name="min_price" min="0" step="1000" required>
                                                <span id="edit_min_price_error" class="error-message"></span>
                                            </div>
                                            <div class="form-group">
                                                <label for="edit_total_quantity">Total Quantity *</label>
                                                <input type="number" id="edit_total_quantity" name="total_quantity" min="0" required>
                                                <span id="edit_total_quantity_error" class="error-message"></span>
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <label for="edit_from_date">From Date *</label>
                                            <input type="date" id="edit_from_date" name="from_date" required>
                                            <span id="edit_from_date_error" class="error-message"></span>
                                        </div>
                                        <div class="form-group">
                                            <label for="edit_to_date">To Date *</label>
                                            <input type="date" id="edit_to_date" name="to_date" required>
                                            <span id="edit_to_date_error" class="error-message"></span>
                                        </div>
                                        div class="form-group">
                                        <label for="edit_status">Status *</label>
                                        <select id="edit_status" name="status" required>
                                            <option value="Active">Active</option>
                                            <option value="Inactive">Inactive</option>
                                        </select>
                                        <span id="edit_status_error" class="error-message"></span>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" onclick="closeEditModal()">Cancel</button>
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-save"></i>
                                    Update
                                </button>
                            </div>
                            </form>
                        </div>
                    </div>
                </div>
        </div>
    </div>
</main>
</div>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script>
                                    // Khi load trang: tự động mở tab dựa trên URL
                                    window.addEventListener('DOMContentLoaded', function () {
                                        const path = window.location.pathname;
                                        if (path.includes('/vouchers')) {
                                            showTab('vouchers');
                                        } else {
                                            showTab('seasonal'); // mặc định: promotions
                                        }
                                    });
                                    // Hàm CHỈ dùng để HIỂN THỊ tab UI, không load lại
                                    function showTab(tabName) {
                                        // Ẩn tất cả tab content
                                        document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
                                        // Bỏ active tất cả button
                                        document.querySelectorAll('.tab-button').forEach(b => b.classList.remove('active'));
                                        // Hiện đúng tab
                                        const tabContent = document.getElementById(tabName + '-tab');
                                        if (tabContent)
                                            tabContent.classList.add('active');
                                        const selector = '.tab-button[data-tab="' + tabName + '"]';
                                        // Kích hoạt nút
                                        const tabButton = document.querySelector(selector);
                                        if (tabButton)
                                            tabButton.classList.add('active');
                                    }

                                    // Khi người dùng click tab → chuyển trang để gọi servlet
                                    function handleTabClick(tabName) {
                                        let servletUrl = '';
                                        if (tabName === 'seasonal') {
                                            servletUrl = '/ParadiseHotel/promotions';
                                        } else if (tabName === 'vouchers') {
                                            servletUrl = '/ParadiseHotel/vouchers';
                                        } else {
                                            servletUrl = '/ParadiseHotel/promotions';
                                        }

                                        // Chuyển trang chỉ 1 lần để gọi đúng servlet
                                        window.location.href = servletUrl;
                                    }

                                    function openPromotionModal() {
                                        const modal = document.getElementById('promotionModal');
                                        modal.style.display = 'block';
                                        modal.classList.add('show');
                                        document.body.style.overflow = 'hidden';
                                    }
                                    function openVoucherModal() {
                                        const modal = document.getElementById('voucherModal');
                                        modal.style.display = 'block';
                                        modal.classList.add('show');
                                        document.body.style.overflow = 'hidden';
                                    }


                                    function closeModal() {
                                        const modal = document.querySelector('.modal.show');
                                        if (modal) {
                                            modal.style.display = 'none';
                                            modal.classList.remove('show');
                                            document.body.style.overflow = '';
                                        }
                                    }
                                    function closeEditModal() {
                                        const modal = document.querySelector('.modal.show');
                                        if (modal) {
                                            modal.style.display = 'none';
                                            modal.classList.remove('show');
                                            document.body.style.overflow = '';
                                        }
                                    }

                                    function editPromotion(btn) {
                                        document.getElementById('edit_promotion_id').value = btn.getAttribute('data-id');
                                        document.getElementById('edit_promotion_name').value = btn.getAttribute('data-name');
                                        document.getElementById('edit_discount_percent').value = btn.getAttribute('data-percent');
                                        document.getElementById('edit_discount_amount').value = btn.getAttribute('data-amount');
                                        document.getElementById('edit_description').value = btn.getAttribute('data-description');
                                        document.getElementById('edit_start_date').value = btn.getAttribute('data-start');
                                        document.getElementById('edit_end_date').value = btn.getAttribute('data-end');
                                        // hien modal
                                        const modal = document.getElementById('editPromotionModal');
                                        modal.style.display = 'block';
                                        modal.classList.add('show');
                                        document.body.style.overflow = 'hidden';
                                    }

                                    function editVoucher(btn) {
                                        document.getElementById('edit_voucher_id').value = btn.getAttribute('data-id');
                                        document.getElementById('edit_voucher_code').value = btn.getAttribute('data-code');
                                        document.getElementById('edit_voucher_description').value = btn.getAttribute('data-description');
                                        document.getElementById('edit_voucher_discount_percent').value = btn.getAttribute('data-percent');
                                        document.getElementById('edit_voucher_discount_amount').value = btn.getAttribute('data-amount');
                                        document.getElementById('edit_min_price').value = btn.getAttribute('data-minPrice');
                                        document.getElementById('edit_total_quantity').value = btn.getAttribute('data-total');
                                        document.getElementById('edit_from_date').value = btn.getAttribute('data-from');
                                        document.getElementById('edit_to_date').value = btn.getAttribute('data-to');
                                        const modal = document.getElementById('editVoucherModal');
                                        modal.style.display = 'block';
                                        modal.classList.add('show');
                                        document.body.style.overflow = 'hidden';
                                    }

                                    function deletePromotion(promotionId) {
                                        Swal.fire({
                                            title: 'Are you sure?',
                                            text: "You won't be able to revert this!",
                                            icon: 'warning',
                                            showCancelButton: true,
                                            confirmButtonColor: '#3085d6',
                                            cancelButtonColor: '#d33',
                                            confirmButtonText: 'Yes, delete it!'
                                        }).then((result) => {
                                            if (result.isConfirmed) {
                                                let form = document.createElement('form');
                                                form.method = 'post';
                                                form.action = 'deletePromotion';
                                                let inputId = document.createElement('input');
                                                inputId.type = 'hidden';
                                                inputId.name = 'promotionId';
                                                inputId.value = promotionId;
                                                form.appendChild(inputId);
                                                document.body.appendChild(form);
                                                form.submit();
                                            }
                                        });
                                    }

                                    function deleteVoucher(voucherId) {
                                        Swal.fire({
                                            title: 'Are you sure?',
                                            text: "You won't be able to revert this!",
                                            icon: 'warning',
                                            showCancelButton: true,
                                            confirmButtonColor: '#3085d6',
                                            cancelButtonColor: '#d33',
                                            confirmButtonText: 'Yes, delete it!'
                                        }).then((result) => {
                                            if (result.isConfirmed) {
                                                let form = document.createElement('form');
                                                form.method = 'post';
                                                form.action = 'deleteVoucher';
                                                let inputId = document.createElement('input');
                                                inputId.type = 'hidden';
                                                inputId.name = 'voucherId';
                                                inputId.value = voucherId;
                                                form.appendChild(inputId);
                                                document.body.appendChild(form);
                                                form.submit();
                                            }
                                        });
                                    }
                                    // chi nhap 1 trong 2 discount
                                    function handleDiscountInput(changedField) {
                                        const percentField = document.getElementById("discount_percent");
                                        const amountField = document.getElementById("discount_amount");
                                        if (changedField === "percent" && percentField.value) {
                                            amountField.disabled = true;
                                            amountField.value = '';
                                        } else if (changedField === "amount" && amountField.value) {
                                            percentField.disabled = true;
                                            percentField.value = '';
                                        }
                                        if (!percentField.value) {
                                            amountField.disabled = false;
                                        }
                                        if (!amountField.value) {
                                            percentField.disabled = false;
                                        }
                                    }

</script>
<!-- validate -->
<script>
    function validateForm(formId) {
        let isValid = true;
        const form = document.getElementById(formId);
        const isPromotionForm = formId === 'promotionForm' || formId === 'editPromotionForm';
        const isVoucherForm = formId === 'voucherForm' || formId === 'editVoucherForm';
        const today = new Date().toString().split('T')[0];
        // Clear all error messages
        form.querySelectorAll('.error-message').forEach(el => {
            el.style.display = 'none';
            el.textContent = '';
        });
        // Get form fields
        const name = isPromotionForm ? form.querySelector('[name="promotion_name"]') : form.querySelector('[name="voucher_code"]');
        const discountPercent = isPromotionForm ? form.querySelector('[name="discount_percent"]') : form.querySelector('[name="discount_percent"]');
        const discountAmount = isPromotionForm ? form.querySelector('[name="discount_amount"]') : form.querySelector('[name="discount_amount"]');
        const description = form.querySelector('[name="description"]');
        const startDate = isPromotionForm ? form.querySelector('[name="start_date"]') : form.querySelector('[name="from_date"]');
        const endDate = isPromotionForm ? form.querySelector('[name="end_date"]') : form.querySelector('[name="to_date"]');
        const status = form.querySelector('[name="status"]');
        const minPrice = isVoucherForm ? form.querySelector('[name="min_price"]') : null;
        const totalQuantity = isVoucherForm ? form.querySelector('[name="total_quantity"]') : null;

        // Validate name or voucher code
        if (isPromotionForm) {
            if (!name.value.trim()) {
                form.querySelector('#' + name.id + '_error').textContent = 'Promotion name cannot be empty.';
                form.querySelector('#' + name.id + '_error').style.display = 'inline';
                name.classList.add('invalid');
                isValid = false;
            } else if (name.value.trim().length > 100) {
                form.querySelector('#' + name.id + '_error').textContent = 'Promotion name cannot exceed 100 characters.';
                form.querySelector('#' + name.id + '_error').style.display = 'inline';
                name.classList.add('invalid');
                isValid = false;
            }
        } else {
            if (!name.value.trim()) {
                form.querySelector('#' + name.id + '_error').textContent = 'Voucher code cannot be empty.';
                form.querySelector('#' + name.id + '_error').style.display = 'inline';
                name.classList.add('invalid');
                isValid = false;
            } else if (name.value.trim().length > 50) {
                form.querySelector('#' + name.id + '_error').textContent = 'Voucher code cannot exceed 50 characters.';
                form.querySelector('#' + name.id + '_error').style.display = 'inline';
                name.classList.add('invalid');
                isValid = false;
            } else if (!/^[A-Z0-9-]+$/.test(name.value.trim())) {
                form.querySelector('#' + name.id + '_error').textContent = 'Voucher code can only contain uppercase letters, numbers, and hyphens.';
                form.querySelector('#' + name.id + '_error').style.display = 'inline';
                name.classList.add('invalid');
                isValid = false;
            }
        }
        // Validate discount
        if (!discountPercent.value && !discountAmount.value) {
            form.querySelector('#' + discountPercent.id + '_error').textContent = 'Either discount percent or amount is required.';
            form.querySelector('#' + discountPercent.id + '_error').style.display = 'inline';
            form.querySelector('#' + discountAmount.id + '_error').textContent = 'Either discount percent or amount is required.';
            form.querySelector('#' + discountAmount.id + '_error').style.display = 'inline';
            discountPercent.classList.add('invalid');
            discountAmount.classList.add('invalid');
            isValid = false;
        } else {
            if (discountPercent.value && (isNaN(discountPercent.value) || parseFloat(discountPercent.value) < 0 || parseFloat(discountPercent.value) > 100)) {
                form.querySelector('#' + discountPercent.id + '_error').textContent = 'Discount percent must be between 0 and 100.';
                form.querySelector('#' + discountPercent.id + '_error').style.display = 'inline';
                discountPercent.classList.add('invalid');
                isValid = false;
            }
            if (discountAmount.value && (isNaN(discountAmount.value) || parseFloat(discountAmount.value) < 0)) {
                form.querySelector('#' + discountAmount.id + '_error').textContent = 'Discount amount cannot be negative.';
                form.querySelector('#' + discountAmount.id + '_error').style.display = 'inline';
                discountAmount.classList.add('invalid');
                isValid = false;
            }
        }
        // Validate description
        if (!description.value.trim()) {
            form.querySelector('#' + description.id + '_error').textContent = 'Description cannot be empty.';
            form.querySelector('#' + description.id + '_error').style.display = 'inline';
            description.classList.add('invalid');
            isValid = false;
        } else if (description.value.trim().length > 500) {
            form.querySelector('#' + description.id + '_error').textContent = 'Description cannot exceed 500 characters.';
            form.querySelector('#' + description.id + '_error').style.display = 'inline';
            description.classList.add('invalid');
            isValid = false;
        }
        // Validate dates
        if (!startDate.value) {
            form.querySelector('#' + startDate.id + '_error').textContent = isPromotionForm ? 'Start date cannot be empty.' : 'From date cannot be empty.';
            form.querySelector('#' + startDate.id + '_error').style.display = 'inline';
            startDate.classList.add('invalid');
            isValid = false;
        } else if ((formId === 'promotionForm' || formId === 'voucherForm') && startDate.value < today) {
            form.querySelector('#' + startDate.id + '_error').textContent = isPromotionForm ? 'Start date cannot be in the past.' : 'From date cannot be in the past.';
            form.querySelector('#' + startDate.id + '_error').style.display = 'inline';
            startDate.classList.add('invalid');
            isValid = false;
        }
        if (!endDate.value) {
            form.querySelector('#' + endDate.id + '_error').textContent = isPromotionForm ? 'End date cannot be empty.' : 'To date cannot be empty.';
            form.querySelector('#' + endDate.id + '_error').style.display = 'inline';
            endDate.classList.add('invalid');
            isValid = false;
        } else if (startDate.value && endDate.value && startDate.value > endDate.value) {
            form.querySelector('#' + endDate.id + '_error').textContent = isPromotionForm ? 'End date must be after start date.' : 'To date must be after from date.';
            form.querySelector('#' + endDate.id + '_error').style.display = 'inline';
            endDate.classList.add('invalid');
            isValid = false;
        }

        // Validate status
        if (!status.value || !['Active', 'Inactive'].includes(status.value)) {
            form.querySelector('#' + status.id + '_error').textContent = 'Please select a valid status.';
            form.querySelector('#' + status.id + '_error').style.display = 'inline';
            status.classList.add('invalid');
            isValid = false;
        }
        // Validate voucher-specific fields
        if (isVoucherForm) {
            if (!minPrice.value || isNaN(minPrice.value) || parseFloat(minPrice.value) < 0) {
                form.querySelector('#' + minPrice.id + '_error').textContent = 'Minimum price must be a non-negative number.';
                form.querySelector('#' + minPrice.id + '_error').style.display = 'inline';
                minPrice.classList.add('invalid');
                isValid = false;
            }
            if (!totalQuantity.value || isNaN(totalQuantity.value) || parseInt(totalQuantity.value) < 0) {
                form.querySelector('#' + totalQuantity.id + '_error').textContent = 'Total quantity must be a non-negative number.';
                form.querySelector('#' + totalQuantity.id + '_error').style.display = 'inline';
                totalQuantity.classList.add('invalid');
                isValid = false;
            }
        }

        console.log('Form validation result:', isValid);
        return isValid;
    }
</script>
<script>
    var errorMsg = "${error != null ? error : ''}";
    var successMsg = "${success != null ? success : ''}";
    var warningMsg = "${warning != null ? warning : ''}";
    var returnPage = "${returnPage != null ? returnPage : ''}";

    if (errorMsg && errorMsg.trim() !== "") {
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: errorMsg
        }).then(() => {
            window.location.href = returnPage;
        });
    } else if (successMsg && successMsg.trim() !== "") {
        Swal.fire({
            icon: 'success',
            title: 'Success',
            text: successMsg
        }).then(() => {
            window.location.href = returnPage;
        });
    } else if (warningMsg && warningMsg.trim() !== "") {
        Swal.fire({
            icon: 'warning',
            title: 'Warning',
            text: warningMsg
        }).then(() => {
            window.location.href = returnPage;
        });
    }
</script>
</body>

</html>