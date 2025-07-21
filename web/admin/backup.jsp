<%-- 
    Document   : account
    Created on : Jun 20, 2025, 7:59:12 AM
    Author     : hungk
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

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
                    <a href="./account" class="nav-item ">
                        <i class="fas fa-users"></i>
                        Account Management
                    </a>
                    <a href="./backup" class="nav-item active">
                        <i class="fa-solid fa-database"></i>
                        Backup
                    </a>
                </nav>
            </aside>

            <!-- Main Content -->
            <main class="main-content">
                <!-- Header -->
                <header class="header">
                    <div class="header-left">
                        <h1 id="page-title">Backup Management</h1>
                        <p id="page-description">Create, delete, and download system backups</p>
                    </div>
                    <div class="header-right">
                        <!--                        <div class="notification-bell">
                                                    <i class="fas fa-bell"></i>
                                                    <span class="notification-badge">3</span>
                                                </div>-->
                        <div class="admin-profile">
                            <div class="profile-dropdown">
                                <div class="profile-avatar">
                                    <i class="fas fa-user"></i>
                                </div>
                                <div class="dropdown-content">
                                    <div class="dropdown-header">
                                        <strong>${sessionScope.user.getUsername()}</strong>
                                        <small>${sessionScope.user.getEmail()}</small>
                                    </div>
                                    <a href="#">Profile Settings</a>
                                    <hr>
                                    <a href="../login?action=logout" class="sign-out">Sign Out</a>
                                </div>
                            </div>
                        </div>
                    </div>
                </header>

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
                            <label>Filter by type:</label>
                            <form action="">
                                <input type="hidden" name="action" value="search">
                                <select id="filter-type" name="searchKeyword" onchange="this.form.submit()">
                                    <option value="all" ${param.searchKeyword == "all" ? "selected" : ""}>All Types</option>
                                    <option value="Full" ${param.searchKeyword == "Full" ? "selected" : ""}>Full</option>
                                    <option value="Partial" ${param.searchKeyword == "Partial" ? "selected" : ""}>Partial</option>
                                </select>
                            </form>
                        </div>
                        <div class="filter-group">
                            <label>Sort by Date</label>
                            <form id="sortForm" action="">
                                <input type="hidden" name="action" id="sortAction" value="${"sortNewest".equals(action) ? "sortNewest" : "sortOldest"}">
                                <button type="button" class="btn btn-outline" onclick="toggleSort()">
                                    <i class="fas fa-sort"></i>
                                    <span id="sort-text">${"sortNewest".equals(action) ? "Sort by Date (Newest)" : "Sort by Date (Oldest)"}</span>
                                </button>
                            </form>
                        </div>
                        <div class="filter-group">
                            <button style="margin-top: 29px; width: 150px;" class="btn btn-primary" onclick="openCreateBackupModal()">
                                <i class="fas fa-plus"></i>
                                Create Backup
                            </button>
                        </div>
                    </div>
                </div>

                <!-- Users Table -->
                <div class="table-container">
                    <p style="margin-left: 5px; padding: 5px;">Total Backup file: <strong>${backupHistoryListSize}</strong></p>
                    <table class="users-table" id="usersTable">
                        <thead>
                            <tr>
                                <th>No.</th>
                                <th>Backup Time</th>
                                <th>Backup Type</th>
                                <th>File Size</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody id="usersTableBody">
                            <c:if test="${empty backupHistoryList}">
                                <tr>
                                    <td colspan="8" style="text-align: center; padding: 48px; color: #64748b;">
                                        No backup file found
                                    </td>
                                </tr>
                            </c:if>
                            <c:forEach items="${backupHistoryList}" var="b">
                                <tr class="${b.is_deleted ? 'deleted' : ''}">
                                    <td>${b.id}</td>
                                    <td>${b.backup_time}</td>
                                    <td>${b.backup_type}</td>
                                    <td>
                                        <fmt:formatNumber value="${b.file_size_mb}" type="number" pattern="#0.00" />MB
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${b.is_deleted}">
                                                <button class="btn btn-ghost" title="Restore"
                                                        onclick="openRestoreModal('${b.id}')">
                                                    <i class="fas fa-undo"></i>
                                                    Restore
                                                </button>
                                            </c:when>
                                            <c:otherwise>
                                                <form action="" method="post">
                                                    <input type="hidden" name="action" value="${b.backup_type == "FULL" ? "downloadFullBackup" : "downloadPartialBackup"}">
                                                    <input type="hidden" name="backupPath" value="${b.backup_path}">
                                                    <button id="download" type="submit" class="btn btn-ghost" title="Download" style="color: #10b981;">
                                                        <i class="fas fa-download"></i>
                                                        Download
                                                    </button>
                                                </form>
                                                <button id="delete" class="btn btn-ghost" title="Delete" style="color: #ef4444;"
                                                        onclick="openDeleteModal('${b.id}')">
                                                    <i class="fas fa-trash"></i>
                                                    Delete
                                                </button>
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


        <!-- Create Backup Modal -->
        <div id="create-backup-modal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h3>Create New Backup</h3>
                    <span class="close" onclick="closeCreateBackupModal()">&times;</span>
                </div>
                <div class="modal-body">
                    <form action="backup" method="post">
                        <input type="hidden" name="action" value="backup">
                        <p>Choose the type of backup you want to create.</p>
                        <div class="form-group">
                            <label for="backup-type">Backup Type</label>
                            <select id="backup-type" name="typeBackup">
                                <option value="Full">Full Backup</option>
                                <option value="Differential">Differential Backup</option>
                            </select>
                        </div>

                        <div class="modal-actions">
                            <button type="button" class="btn btn-outline" onclick="closeCreateBackupModal()">Cancel</button>
                            <button type="submit" class="btn btn-primary">
                                <span id="create-btn-text">Create Backup</span>
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Delete Confirmation Modal -->
        <div id="delete-modal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h3>Are you sure?</h3>
                    <span class="close" onclick="closeDeleteModal()" style="cursor: pointer">&times;</span>
                </div>
                <div class="modal-body">
                    <p id="delete-message">This action will delete the backup. This action cannot be undone.</p>
                    <form action="" method="post">
                        <input type="hidden" name="action" value="deleteSoft">
                        <input type="hidden" id="deleteID" name="deleteID" value="">
                        <div class="modal-actions">
                            <button type="button" class="btn btn-outline" onclick="closeDeleteModal()">Cancel</button>
                            <button type="submit" class="btn btn-danger">
                                <span id="delete-btn-text">Delete</span>
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Restore Confirmation Modal -->
        <div id="restore-modal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h3>Are you want to restore</h3>
                    <span class="close" onclick="closeRestoreModal()" style="cursor: pointer">&times;</span>
                </div>
                <div class="modal-body">
                    <p id="delete-message">Are you sure you want to restore this backup? This action will overwrite current data.</p>
                    <form action="" method="post">
                        <input type="hidden" name="action" value="restore">
                        <input type="hidden" id="restoreID" name="restoreID" value="">
                        <div class="modal-actions">
                            <button class="btn btn-outline" onclick="closeRestoreModal()">Cancel</button>
                            <button class="btn btn-primary">
                                <span id="delete-btn-text">Restore</span>
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Toast Notifications -->
        <div id="toastContainer" class="toast-container"></div>

        <script src="../js/accountScript.js">
        </script>
        <script src="../js/toastMessage.js">
        </script>
        <script>
            // Create backup modal functions
            function openCreateBackupModal() {
                document.getElementById('create-backup-modal').classList.add("show");
            }

            function closeCreateBackupModal() {
                document.getElementById('create-backup-modal').classList.remove("show");
            }
            // Delete backup modal functions
            function openDeleteModal(backupId) {
                backupToDelete = backupId;
                const deleteMessage = document.getElementById('delete-message');
                deleteMessage.textContent = "Are you sure? This action will delete the backup.";
                const deleteID = document.getElementById('deleteID');
                deleteID.value = backupId;
                document.getElementById('delete-modal').classList.add("show");
            }

            function closeDeleteModal() {
                document.getElementById('delete-modal').classList.remove("show");
                backupToDelete = null;
            }

            function openRestoreModal(backupId) {
                backupToDelete = backupId;
                const restore = document.getElementById('restoreID');
                restore.value = backupId;
                document.getElementById('restore-modal').classList.add("show");
            }

            function closeRestoreModal() {
                document.getElementById('restore-modal').classList.remove("show");
                backupToDelete = null;
            }

            // Close modals with Escape key
            document.addEventListener('keydown', function (e) {
                if (e.key === 'Escape') {
                    closeCreateBackupModal();
                    closeDeleteModal();
                }
            });
            function toggleSort() {
                const actionInput = document.getElementById("sortAction");
                const sortText = document.getElementById("sort-text");

                if (actionInput.value === "sortOldest") {
                    actionInput.value = "sortNewest";
                    sortText.innerText = "Sort by Date (Newest)";
                } else {
                    actionInput.value = "sortOldest";
                    sortText.innerText = "Sort by Date (Oldest)";
                }

                document.getElementById("sortForm").submit();
            }
            window.addEventListener("DOMContentLoaded", () => {
                const params = new URLSearchParams(window.location.search);
                const action = params.get("action");

                if (action === "sortNewest") {
                    document.getElementById("sortAction").value = "sortNewest";
                    document.getElementById("sort-text").innerText = "Sort by Date (Newest)";
                } else {
                    document.getElementById("sortAction").value = "sortOldest";
                    document.getElementById("sort-text").innerText = "Sort by Date (Oldest)";
                }
            });

        </script>
    </body>
</html>
