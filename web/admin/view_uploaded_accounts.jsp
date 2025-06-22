<%-- 
    Document   : view_uploaded_accounts
    Created on : Jun 22, 2025, 7:39:51 PM
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
                            <h1>View Uploaded Accounts</h1>
                        </div>
                        <button class="btn btn-primary">
                            <i class="fas fa-plus"></i>
                            Add Accounts
                        </button>
                    </div>
                </div>


                <!-- Users Table -->
                <div class="table-container">
                    <p style="margin-left: 5px; padding: 5px;">Total Accounts: <strong>${userListSize}</strong></p>
                    <table class="users-table" id="usersTable">
                        <thead>
                            <tr>
                                <th>Email</th>
                                <th>Full Name</th>
                                <th>UserName</th>
                                <th>Phone</th>
                                <th>Role</th>
                                <th>Brand Id</th>
                            </tr>
                        </thead>
                        <tbody id="usersTableBody">
                            <c:if test="${empty userList}">
                                <tr>
                                    <td colspan="8" style="text-align: center; padding: 48px; color: #64748b;">
                                        No users found
                                    </td>
                                </tr>
                            </c:if>

                            <c:forEach items="${userList}" var="a">
                                <tr>
                                    <td>${a.email} </td>
                                    <td>${a.fullname} </td>
                                    <td>${a.username} </td>
                                    <td>${a.phonenumber}</td>
                                    <td>${a.role}</td>
                                    <td>${a.branchId}</td>
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
    </body>
</html>
