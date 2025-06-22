<%-- 
    Document   : account
    Created on : Jun 20, 2025, 7:59:12 AM
    Author     : hungk
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Hotel Management System - Account Administration</title>
        <link rel="stylesheet" href="../css/accountStyle.css">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    </head>
    <body>
        <c:if test="${not empty sessionScope.message}">
            <div id="toastMessage" class="toast-message ${sessionScope.messageType}">
                <c:choose>
                    <c:when test="${sessionScope.messageType == 'success'}">
                        <i class="fa fa-check-circle"></i>
                    </c:when>
                    <c:when test="${sessionScope.messageType == 'error'}">
                        <i class="fa fa-times-circle"></i>
                    </c:when>
                </c:choose>
                ${sessionScope.message}
            </div>

            <!-- Xóa message sau khi hiển thị -->
            <c:remove var="message" scope="session" />
            <c:remove var="messageType" scope="session" />
        </c:if>
        <div class="app">
            <!-- Sidebar -->
            <aside class="sidebar">
                <div class="sidebar-header">
                    <h1>Hotel Admin</h1>
                    <p>Management System</p>
                </div>
                <nav class="sidebar-nav">
                    <a href="#account-management" class="nav-item active">
                        <i class="fas fa-users"></i>
                        Account Management
                    </a>
                    <a href="#hotels" class="nav-item">
                        <i class="fas fa-building"></i>
                        Hotels
                    </a>
                    <a href="#bookings" class="nav-item">
                        <i class="fas fa-calendar-check"></i>
                        Bookings
                    </a>
                    <a href="#analytics" class="nav-item">
                        <i class="fas fa-chart-bar"></i>
                        Analytics
                    </a>
                </nav>
            </aside>

            <!-- Main Content -->
            <main class="main-content">
                <!-- Header -->
                <div class="page-header">
                    <div class="header-content">
                        <div>
                            <h1>Account Management</h1>
                            <p>Manage user accounts, roles, and permissions</p>
                        </div>
                        <button class="btn btn-primary" onclick="openAddAccountModal()">
                            <i class="fas fa-plus"></i>
                            Add Account
                        </button>
                    </div>
                </div>

                <!-- Filters -->
                <div class="filters-panel">
                    <div class="filters-grid">
                        <div class="filter-group">
                            <label>Search</label>
                            <form action="">
                                <input type="hidden" name="action" value="search">
                                <div class="search-input">
                                    <i class="fas fa-search"></i>
                                    <input type="text" 
                                           name="searchKeyword" id="searchInput" 
                                           value="${param.searchKeyword}" 
                                           placeholder="Search by email, name, or phone...">
                                </div>
                            </form>
                        </div>
                        <div class="filter-group">
                            <label>Role</label>
                            <form id="roleFilterForm">
                                <input type="hidden" name="action" value="search">
                                <select id="roleFilter" name="searchKeyword" onchange="document.getElementById('roleFilterForm').submit()">
                                    <option value="all">All Role</option>
                                    <c:forEach items="${roleList}" var="role">
                                        <option value="${role}" ${param.searchKeyword == role ? 'selected' : ''}>${role}</option>  
                                    </c:forEach>
                                </select>
                            </form>
                        </div>
                        <div class="filter-group">
                            <label>Status</label>
                            <form id="statusFilterForm">
                                <input type="hidden" name="action" value="filerStatus">
                                <select id="statusFilter" name="statusValue" onchange="document.getElementById('statusFilterForm').submit()">
                                    <option value="all">All Status</option>

                                    <c:forEach items="${statusList}" var="status">
                                        <option value="${status}" ${param.statusValue == status ? 'selected' : ''}>${status}</option>  
                                    </c:forEach>
                                    <option value="Deleted">Deleted</option> 
                                </select>
                            </form>
                        </div>
                    </div>
                </div>

                <!-- Bulk Actions Bar -->
                <div id="bulkActionsBar" class="bulk-actions-bar" style="display: none;">
                    <div class="bulk-actions-content">
                        <span class="selected-count">0 accounts selected</span>
                        <p id="announce"></p>
                        <div class="bulk-actions">
                            <button class="btn btn-primary" id="btnActive" data-action="Active"
                                    onclick="openBulkActionsModal(this.dataset.action)">
                                <i class="fas fa-lock"></i>
                                Active Accounts
                            </button>
                            <button class="btn btn-warning" id="btnInactive" data-action="Inactive"
                                    onclick="openBulkActionsModal(this.dataset.action)">
                                <i class="fas fa-lock"></i>
                                Inactive Accounts
                            </button>
                            <button class="btn btn-info" id="btnResetPassword" data-action="Reset Passwords"
                                    onclick="openBulkActionsModal(this.dataset.action)">
                                <i class="fas fa-key"></i>
                                Reset Passwords
                            </button>
                            <button class="btn btn-danger" id="btnSoftDelete" data-action="Delete"
                                    onclick="openBulkActionsModal(this.dataset.action)">
                                <i class="fas fa-trash"></i>
                                Delete
                            </button>
                            <button class="btn btn-info" id="btnRestore" data-action="Restore"
                                    onclick="openBulkActionsModal(this.dataset.action)">
                                <i class="fas fa-undo"></i>
                                Restore
                            </button>
                            <button class="btn btn-danger" id="btnBan" data-action="Ban"
                                    onclick="openBulkActionsModal(this.dataset.action)">
                                <i class="fas fa-ban"></i>
                                Ban
                            </button>
                            <button class="btn btn-warning" id="btnUnBan" data-action="UnBan"
                                    onclick="openBulkActionsModal(this.dataset.action)">
                                <i class="fas fa-ban"></i>
                                UnBan
                            </button>
                        </div>
                    </div>
                </div>

                <!-- Users Table -->
                <div class="table-container">
                    <p style="margin-left: 5px; padding: 5px;">Total Accounts: <strong>${accountListSize}</strong></p>
                    <table class="users-table" id="usersTable">
                        <thead>
                            <tr>
                                <th>
                                    <input type="checkbox" id="selectAll" onchange="handleSelectAll()">
                                </th>
                                <th>Avatar</th>
                                <th>Email, Full Name, Phone</th>
                                <th>Login type</th>
                                <th>Role</th>
                                <th>Status</th>
                                <th>Created → Last Login</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody id="usersTableBody">
                            <c:if test="${empty userAccountList}">
                                <tr>
                                    <td colspan="8" style="text-align: center; padding: 48px; color: #64748b;">
                                        No users found
                                    </td>
                                </tr>
                            </c:if>

                            <c:forEach items="${userAccountList}" var="a">
                                <tr class="${a.is_deleted ? 'deleted' : ''}">
                                    <td>
                                        <input class="checkboxItem" type="checkbox" 
                                               data-user-id="${a.id}" data-status="${a.status}" 
                                               data-is-deleted="${a.is_deleted}" 
                                               data-is-owner="${a.role}"
                                               onchange="handleUserSelection('${a.id}', this.checked)">
                                    </td>
                                    <td>
                                        <img class="avatar_img" src="../img/avatar/avatar.jpg" alt="alt"/>
                                    </td>
                                    <td>
                                        ${a.email} </br>
                                        ${a.fullname} </br>
                                        ${a.phonenumber} </br>
                                    </td>
                                    <td>${a.login_type}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${a.role == 'HotelOwner'}">
                                                <span class="badge badge-hotel-owner">
                                                    <i class="fas fa-crown" style="margin-right: 5px"></i>
                                                    Hotel Owner
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <select class="role-select" 
                                                        data-id="${a.id}" 
                                                        data-fullname="${a.fullname}" 
                                                        data-current="${a.role}"
                                                        onchange="openRoleChangeModal(this)"
                                                        style="${a.status == 'Banned' or a.status == 'Inactive' ? "pointer-events: none;": ""}">
                                                    <option value="Customer" ${a.role == "Customer" ? "selected" : ""}>Customer</option>
                                                    <option value="Staff" ${a.role == "Staff" ? "selected" : ""}>Staff</option>
                                                    <option value="Manager" ${a.role == "Manager" ? "selected" : ""}>Manager</option>
                                                    <option value="Admin" ${a.role == "Admin" ? "selected" : ""}>Admin</option>
                                                </select>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${a.is_deleted}">
                                                <span class="badge badge-deleted">
                                                    <span class="status-dot deleted"></span>
                                                    Deleted
                                                </span>
                                            </c:when>
                                            <c:when test="${a.status == 'Active'}">
                                                <span class="badge badge-active">
                                                    <span class="status-dot active"></span>
                                                    Active
                                                </span>
                                            </c:when>
                                            <c:when test="${a.status == 'Inactive'}">
                                                <span class="badge badge-locked">
                                                    <span class="status-dot locked"></span>
                                                    Inactive
                                                </span>
                                            </c:when>
                                            <c:when test="${a.status == 'Banned'}">
                                                <span class="badge badge-deleted">
                                                    <span class="status-dot deleted"></span>
                                                    Banned
                                                </span>
                                            </c:when>
                                        </c:choose>

                                    </td>
                                    <td>
                                        ${a.create_at} </br>
                                        <i class="fa-solid fa-down-long" style="margin-left: 40%"></i></br>
                                        ${a.last_login_at}
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${a.is_deleted}">
                                                <button class="btn btn-ghost" title="Restore"
                                                        onclick="openActionsModal('Restore Account', '${a.fullname}', '${a.id}')">
                                                    <i class="fas fa-undo"></i>
                                                    Restore
                                                </button>
                                            </c:when>
                                            <c:otherwise>
                                                <c:choose>
                                                    <c:when test="${a.role == 'HotelOwner'}">
                                                        <button class="btn btn-ghost" title="Reset Password" style="color: #0b83f5;"
                                                                onclick="openActionsModal('Reset Password', '${a.fullname}', '${a.id}')">
                                                            <i class="fas fa-key"></i>
                                                            Reset
                                                        </button>
                                                        <button class="btn btn-ghost" title="Transfer Ownership" style="color: #7c3aed;"
                                                                onclick="openTranferModal('${a.id}', '${a.fullname}', '${a.email}')">
                                                            <i class="fas fa-crown"></i>
                                                            Transfer
                                                        </button>
                                                    </c:when>
                                                    <c:when test="${a.status == 'Banned'}">
                                                        <button class="btn btn-ghost" title="Unban" style="color: #10b981;"
                                                                onclick="openActionsModal('Unban Account', '${a.fullname}', '${a.id}')">
                                                            <i class="fas fa-ban"></i>
                                                            UnBan
                                                        </button>
                                                        <button class="btn btn-ghost" title="Delete" style="color: #ef4444;"
                                                                onclick="openActionsModal('Delete Account', '${a.fullname}', '${a.id}')">
                                                            <i class="fas fa-trash"></i>
                                                            Delete
                                                        </button>
                                                    </c:when>
                                                    <c:when test="${a.status == 'Inactive'}">
                                                        <button class="btn btn-ghost" title="Active" style="color: #10b981;"
                                                                onclick="openActionsModal('Active Account', '${a.fullname}', '${a.id}')">
                                                            <i class="fas fa-unlock"></i>
                                                            Active
                                                        </button>
                                                        <button class="btn btn-ghost" title="Delete" style="color: #ef4444;"
                                                                onclick="openActionsModal('Delete Account', '${a.fullname}', '${a.id}')">
                                                            <i class="fas fa-trash"></i>
                                                            Delete
                                                        </button>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <button class="btn btn-ghost" title="Reset Password" style="color: #0b83f5;"
                                                                onclick="openActionsModal('Reset Password', '${a.fullname}', '${a.id}')">
                                                            <i class="fas fa-key"></i>
                                                            Reset
                                                        </button>
                                                        <button class="btn btn-ghost" title="Inactive" style="color: #f59e0b;"
                                                                onclick="openActionsModal('Inactivate Account', '${a.fullname}', '${a.id}')">
                                                            <i class="fas fa-lock"></i>
                                                            Inactive
                                                        </button>
                                                        <button class="btn btn-ghost" title="Ban" style="color: red;"
                                                                onclick="openActionsModal('Ban Account', '${a.fullname}', '${a.id}')">
                                                            <i class="fas fa-ban"></i>
                                                            Ban
                                                        </button>

                                                        <button class="btn btn-ghost" title="Delete" style="color: #ef4444;"
                                                                onclick="openActionsModal('Delete Account', '${a.fullname}', '${a.id}')">
                                                            <i class="fas fa-trash"></i>
                                                            Delete
                                                        </button>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>

                            <!-- Users will be populated here -->
                        </tbody>
                    </table>
                </div>

                <div class="pagination">
                    <c:set var="queryParams" value="" />
                    <c:if test="${not empty action and not empty keyword}">
                        <c:set var="queryParams" value="&action=${action}&searchKeyword=${keyword}" />
                    </c:if>
                    <c:if test="${not empty action and not empty statusValue}">
                        <c:set var="queryParams" value="&action=${action}&statusValue=${statusValue}" />
                    </c:if>

                    <c:if test="${currentPage > 1}">
                        <a href="?page=${currentPage - 1}${queryParams}"  class="prev"> Previous</a>
                    </c:if>

                    <c:forEach var="i" begin="1" end="${totalPages}">
                        <a href="?page=${i}${queryParams}" class="${i == currentPage ? 'active' : ''}">${i}</a>
                    </c:forEach>

                    <c:if test="${currentPage < totalPages}">
                        <a href="?page=${currentPage + 1}${queryParams}" class="next">Next</a>
                    </c:if>
                </div>


            </main>
        </div>

        <!-- Add Account Modal -->
        <div id="addAccountModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h2>Add New Account</h2>
                    <button class="btn btn-ghost" onclick="closeAddAccountModal()">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="tabs">
                        <button class="tab-btn active" onclick="switchTab('manual')">Manual Entry</button>
                        <button class="tab-btn" onclick="switchTab('bulk')">Bulk Upload</button>
                    </div>

                    <!-- Manual Entry Tab -->
                    <div id="manualTab" class="tab-content active">
                        <form id="addAccountForm">
                            <div class="form-grid">
                                <div class="form-group">
                                    <label for="emailAdd">Email Address</label>
                                    <input type="email" id="emailAdd" name="emailAdd" required>
                                </div>
                                <div class="form-group">
                                    <label for="fullNameAdd">Full Name</label>
                                    <input type="text" id="fullNameAdd" name="fullNameAdd" required>
                                </div>
                                <div class="form-group">
                                    <label for="phoneAdd">Phone Number</label>
                                    <input type="tel" id="phoneAdd" name="phoneAdd">
                                </div>
                                <div class="form-group">
                                    <label for="roleAdd">Role</label>
                                    <select id="roleAdd" name="roleAdd" required>
                                        <option value="staff">Staff</option>
                                        <option value="manager">Manager</option>
                                    </select>
                                </div>
                            </div>
                            <div class="modal-actions">
                                <button type="button" class="btn btn-outline" onclick="closeAddAccountModal()">Cancel</button>
                                <button type="submit" class="btn btn-primary">Create Account</button>
                            </div>
                        </form>
                    </div>

                    <!-- Bulk Upload Tab -->
                    <div id="bulkTab" class="tab-content">
                        <div class="upload-info">
                            <div class="alert alert-info">
                                <i class="fas fa-file-excel"></i>
                                <div>
                                    <h3>Excel Upload Requirements</h3>
                                    <ul>
                                        <li>Use .xlsx format with columns: Email, Full Name, Phone, Role</li>
                                        <li>Role values must be: "Manager" or "Staff"</li>
                                        <li>Duplicate emails will be ignored and reported</li>
                                        <li>Maximum 1000 accounts per upload</li>
                                    </ul>
                                    <button class="btn btn-link" onclick="downloadTemplate()">
                                        <i class="fas fa-download"></i>
                                        Download Template
                                    </button>
                                </div>
                            </div>
                        </div>
                        <div class="upload-area">
                            <div class="upload-zone" onclick="document.getElementById('excelFile').click()">
                                <i class="fas fa-file-excel"></i>
                                <p>Drop your Excel file here or click to browse</p>
                                <input type="file" id="excelFile" accept=".xlsx,.xls" style="display: none;" onchange="handleFileUpload(event)">
                                <button type="button" class="btn btn-outline">
                                    <i class="fas fa-upload"></i>
                                    Choose File
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Role Change Modal -->
        <div id="roleChangeModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h2>Confirm Role Change</h2>
                    <button class="btn btn-ghost" onclick="closeRoleChangeModal()">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
                <form method="post" action="accountEventHandler">
                    <div class="modal-body">
                        <div class="confirmation-content">
                            <i class="fas fa-exclamation-triangle"></i>
                            <input type="hidden" name="userId" id="roleUserId">
                            <input type="hidden" name="roleName" id="roleName">
                            <input type="hidden" name="action" value="changeRole">
                            <div>
                                <h3>Change User Role</h3>
                                <p id="roleChangeText"></p>
                                <p class="text-muted">This action will immediately update their permissions and access levels.</p>
                            </div>
                        </div>
                        <div class="modal-actions">
                            <button type="button" class="btn btn-outline" onclick="closeRoleChangeModal()">Cancel</button>
                            <button type="submit" class="btn btn-warning">Confirm Change</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <!-- Confirm Tranfer Modal -->
        <div id="tranferModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h2>Do you want to transfer ownership?</h2>
                    <button class="btn btn-ghost" onclick="closeTranferModal()">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
                <form method="post" action="accountEventHandler">
                    <div class="modal-body">
                        <div class="confirmation-content">
                            <i class="fas fa-exclamation-triangle"></i>
                            <input type="hidden" name="userId" id="ownerID">
                            <input type="hidden" name="userEmail" id="ownerEmail">
                            <input type="hidden" name="action" value="confirmTransfer">
                            <div>
                                <h3>Change of ownership</h3>
                                <p id="transferText"></p>
                                <p class="text-muted">This action will immediately update their permissions and access levels.</p>
                            </div>
                        </div>
                        <div class="modal-actions">
                            <button type="button" class="btn btn-outline" onclick="closeTranferModal()">Cancel</button>
                            <button type="button" class="btn btn-warning" onclick="submitTransferOwnership()">Next</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <!-- Transfer Ownership Modal -->
        <div id="transferOwnershipModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h2>Transfer Hotel Ownership</h2>
                    <button class="btn btn-ghost" onclick="closeTransferOwnershipModal()">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
                <div class="modal-body">
                    <!-- Step indicator will be added here -->
                    <div id="transferStep1" class="transfer-step active">
                        <div class="form-group">
                            <div id="notificationArea"></div>
                            <label for="verificationCode">Enter verification code</label>
                            <input type="text" id="verificationCode" maxlength="6" placeholder="Enter 6-digit code">
                            <p class="help-text">
                                Didn't receive the code? 
                                <button class="btn btn-link" onclick="resendCode()">Resend</button>
                            </p>
                        </div>
                        <div class="modal-actions">
                            <button type="button" class="btn btn-outline" onclick="closeTransferOwnershipModal()">Cancel</button>
                            <button type="button" class="btn btn-primary" onclick="verifyCode()">Verify & Continue</button>
                        </div>
                    </div>
                    <div id="transferStep2" class="transfer-step">
                        <form id="selectOwner" method="post" action="accountEventHandler">
                            <input type="hidden" name="action" value="changeOwnerBySelect">
                            <div class="form-group">
                                <label for="newOwner">Select New Hotel Owner</label>
                                <select name="userId" id="newOwner" required>
                                    <option value="">Choose from manager account...</option>
                                    <c:forEach items="${userAccountList}" var="a">
                                        <c:if test="${a.role eq 'Manager'}">
                                            <option value="${a.id}">${a.id} - ${a.role} - ${a.fullname}</option>
                                        </c:if>
                                    </c:forEach>
                                </select>
                                <p class="form__error"></p>
                            </div>
                            <div class="text-center">
                                <span class="text-muted">or</span>
                            </div>
                            <button type="button" class="btn btn-outline btn-full" onclick="createNewOwner()">
                                <i class="fas fa-plus"></i>
                                Create New Account for Ownership
                            </button>
                            <div class="modal-actions">
                                <button type="button" class="btn btn-outline" onclick="backToStep1()">Back</button>
                                <button type="submit" class="btn btn-danger">Transfer Ownership</button>
                            </div>
                        </form>
                    </div>
                    <div id="transferStep3" class="transfer-step">
                        <input type="hidden" name="action" value="verifyEmailToCreateOwner">
                        <div class="form-grid">
                            <div class="form-group">
                                <label for="email">Email Address</label>
                                <input type="email" id="email" name="email" required>
                                <p class="form__error"></p>
                            </div>
                            <div class="form-group">
                                <label for="fullName">Full Name</label>
                                <input type="text" id="fullName" name="fullName" required>
                                <p class="form__error"></p>
                            </div>
                            <div class="form-group">
                                <label for="phone">Phone Number</label>
                                <input type="tel" id="phone" name="phone">
                                <p class="form__error"></p>
                            </div>
                            <div class="form-group">
                                <label for="username">UserName</label>
                                <input type="text" id="username" name="username">
                                <p class="form__error"></p>
                            </div>
                            <div class="form-group">
                                <label for="password">Password</label>
                                <input type="password" id="password" name="password">
                                <p class="form__error"></p>
                            </div>
                            <div class="form-group">
                                <label for="confirmPassword">Confirm Password</label>
                                <input type="password" id="confirmPassword" name="confirmPassword">
                                <p class="form__error"></p>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="role">Role</label>
                            <select id="role" name="role" style="pointer-events: none; background: #ccc;">
                                <option value="owner">Hotel owner</option>
                            </select>
                        </div>
                        <div class="modal-actions">
                            <button type="button" class="btn btn-outline" onclick="backToStep2()">Back</button>
                            <button type="button" class="btn btn-primary" onclick="sendCodeToCreateOwner()">Verify Email To Tranfer</button>
                        </div>
                    </div>
                    <div id="transferStep4" class="transfer-step">
                        <div class="form-group">
                            <div id="notificationAreaCreate"></div>
                            <label for="verificationCodeCreate">Enter verification code</label>
                            <input type="text" id="verificationCodeCreate" maxlength="6" placeholder="Enter 6-digit code">
                            <p class="help-text">
                                Didn't receive the code? 
                                <button class="btn btn-link" onclick="resendCodeCreate()">Resend</button>
                            </p>
                        </div>
                        <div class="modal-actions">
                            <button type="button" class="btn btn-outline" onclick="backToStep2()">Cancel</button>
                            <button type="button" class="btn btn-primary" onclick="verifyCodeCreate()">Verify & Create</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Actions Modal -->
        <div id="actionsModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h2>Confirm Action</h2>
                    <button class="btn btn-ghost" onclick="closeActionsModal()">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
                <form method="post" action="accountEventHandler">
                    <div class="modal-body">
                        <div class="confirmation-content">
                            <i class="fas fa-exclamation-triangle"></i>
                            <div>
                                <h3>User Action</h3>
                                <p id="actionChangeText"></p>
                                <p class="text-muted">This action will immediately update their permissions and access levels.</p>
                            </div>
                        </div>
                        <input type="hidden" name="userId" id="actionUserId">
                        <input type="hidden" name="action" value="multyActions">
                        <input type="hidden" name="actionType" id="actionType">
                        <div class="modal-actions">
                            <button type="button" class="btn btn-outline" onclick="closeActionsModal()">Cancel</button>
                            <button type="submit" class="btn btn-warning">Confirm Change</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <!--Bulk Actions Modal -->
        <div id="bulkActionsModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h2>Confirm Action</h2>
                    <button class="btn btn-ghost" onclick="closeBulkActionsModal()">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="confirmation-content">
                        <i class="fas fa-exclamation-triangle"></i>
                        <div>
                            <h3>User Action</h3>
                            <p id="bulkActionChangeText"></p>
                            <p class="text-muted">This action will immediately update their permissions and access levels.</p>
                        </div>
                    </div>
                    <input type="hidden" name="listUserId" id="listUserId">
                    <input type="hidden" name="action" value="bulkActions">
                    <input type="hidden" name="bulkActionType" id="bulkActionType">
                    <div class="modal-actions">
                        <button type="button" class="btn btn-outline" onclick="closeBulkActionsModal()">Cancel</button>
                        <button type="submit" class="btn btn-warning">Confirm Change</button>
                    </div>
                </div>
            </div>
        </div>


        <!-- Toast Notifications -->
        <div id="toastContainer" class="toast-container"></div>

        <script src="../js/accountScript.js"></script>
        <script src="../js/toastMessage.js"></script>
        <script src="../js/validationForm.js"></script>



        <script>
                            Validator({
                                form: '#createOwnerForm',
                                formGroupSelector: '.form-group',
                                errorSelector: '.form__error',
                                rules: [
                                    Validator.isRequired('#fullName', 'Please enter full name'),
                                    Validator.isRequired('#username', 'Please enter username'),
                                    Validator.lengthRange('#username', 6, 30, 'Username must be between 6 and 30 characters.'),
                                    Validator.isPhoneNumber('#phone', 'Please enter your phone number'),
                                    Validator.isRequired('#email', 'Please enter your email'),
                                    Validator.isEmail('#email'),
                                    Validator.minLength(' #password', 8),
                                    Validator.isRequired('#confirmPassword'),
                                    Validator.isConfirmed(' #confirmPassword', function () {
                                        return document.querySelector('#createOwnerForm #password').value;
                                    }, 'Confirm password is incorrect'),
                                ],
                                onsubmit: function (formValue) {
                                    document.querySelector('#createOwnerForm').submit();
                                }
                            });
                            Validator({
                                form: '#selectOwner',
                                formGroupSelector: '.form-group',
                                errorSelector: '.form__error',
                                rules: [
                                    Validator.isSelectRequired('#newOwner', 'Please select new owner')
                                ],
                                onsubmit: function (formValue) {
                                    document.querySelector('#selectOwner').submit();
                                }
                            });
        </script>
    </body>
</html>
