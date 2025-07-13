<%-- 
    Document   : uploadReports
    Created on : Jul 8, 2025, 11:46:51 PM
    Author     : hungk
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Hotel Profit/Loss Management Dashboard</title>
        <link rel="stylesheet" href="../css/hotelOwnerStyle.css" />
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet" />
        <script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.18.5/xlsx.full.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/3.9.1/chart.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
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
        <div class="app-layout">
            <!-- Left Sidebar -->
            <aside class="sidebar" id="sidebar">
                <div class="sidebar-header">
                    <img src="../img/logoHotelOwner.svg" alt="Hotel Management" class="sidebar-logo" />
                    <span class="sidebar-title">Hotel Manager</span>
                </div>

                <nav class="sidebar-nav">
                    <a href="./financialDashboard" class="nav-item " data-page="dashboard">
                        <i class="fas fa-chart-line"></i>
                        <span>Dashboard</span>
                    </a>
                    <a href="./uploadReports.jsp" class="nav-item active" data-page="upload">
                        <i class="fas fa-upload"></i>
                        <span>Upload Reports</span>
                    </a>
                    <a href="./manageBranch" class="nav-item " data-page="upload">
                        <i class="fa-solid fa-hotel"></i>
                        <span>Manage Branch</span>
                    </a>
                </nav>
            </aside>

            <!-- Main Content Area -->
            <main class="main-content" id="mainContent">
                <!-- Header -->
                <header class="header">
                    <div class="header-left">
                        <a href="./financialDashboard" style="text-decoration: none">
                            <h1 id="page-title">Upload Reports</h1>
                        </a>
                        <p id="page-description">Upload Excel files containing revenue and expense data from hotel branches</p>
                    </div>
                    <div class="header-right">
                        <div class="notification-bell">
                            <i class="fas fa-bell"></i>
                            <span class="notification-badge">3</span>
                        </div>
                        <div class="admin-profile">
                            <div class="profile-dropdown">
                                <div class="profile-avatar">
                                    <i class="fas fa-user"></i>
                                </div>
                                <div class="dropdown-content">
                                    <div class="dropdown-header">
                                        <strong>Hotel owner</strong>
                                        <small>admin@system.com</small>
                                    </div>
                                    <a href="#">Profile Settings</a>
                                    <a href="#">Account Security</a>
                                    <a href="#">Preferences</a>
                                    <hr />
                                    <a href="#" class="sign-out">Sign Out</a>
                                </div>
                            </div>
                        </div>
                    </div>
                </header>

                <!-- Upload Reports Page -->
                <div class="page-content active" id="upload">

                    <div class="card">
                        <div class="card-header" style="display: flex; justify-content: space-between">
                            <h3><i class="fas fa-upload"></i> Upload Revenue & Expense Reports</h3>
                            <form action="uploadReports" method="post">
                                <input type="hidden" name="action" value="downloadTemplate" />
                                <button class="btn btn-primary">
                                    <i class="fas fa-download"></i>
                                    Download Template
                                </button>
                            </form>
                        </div>
                        <div class="upload-area" id="uploadArea">
                            <div class="upload-icon">
                                <i class="fas fa-cloud-upload-alt"></i>
                            </div>
                            <p>Drag and drop Excel files here or click to browse</p>
                            <form action="uploadReports" method="post" enctype="multipart/form-data">
                                <input type="hidden" name="action" value="uploadTemplate">
                                <input type="file" id="fileInput" name="file" accept=".xlsx,.xls" style="display: none" onchange="this.form.submit()" />
                                <button class="btn-primary" onclick="document.getElementById('fileInput').click(); return false;">
                                    Choose Report File
                                </button>
                            </form>

                        </div>
                        <div class="file-list" id="fileList"></div>
                    </div>

                    <c:if test="${not empty previewReport}">
                        <div class="card highlight-card">
                            <div class="card-header">
                                <h3><i class="fas fa-eye"></i> Preview: Monthly Report</h3>
                            </div>

                            <div class="info-group">
                                <div class="info-column">
                                    <p><strong>Branch:</strong> ${previewReport.hotelBranch.name}</p>
                                    <p><strong>Report Month:</strong> ${previewReport.reportMonth}</p>
                                    <p><strong>New Investment:</strong> <fmt:formatNumber value="${investment.capital}" type="currency"/></p>
                                    <p><strong>Investment Date:</strong> ${investment.investedDate}</p>
                                    <p><strong>Previous Investment:</strong> <fmt:formatNumber value="${previousCapital}" type="currency"/></p>
                                    <p><strong>Total Investment:</strong> <fmt:formatNumber value="${totalCapital}" type="currency"/></p>
                                </div>
                                <div class="info-column">
                                    <p><strong>Total Revenue:</strong> <fmt:formatNumber value="${previewReport.revenue}" type="currency"/></p>
                                    <p><strong>Total Expenses:</strong> <fmt:formatNumber value="${previewReport.expenses}" type="currency"/></p>
                                    <p><strong>Profit:</strong> <fmt:formatNumber value="${previewReport.profit}" type="currency"/></p>
                                    <p><strong>Profit Rate:</strong> <fmt:formatNumber value="${previewReport.profitRate}" maxFractionDigits="2"/>%</p>
                                </div>
                            </div>
                        </div>
                    </c:if>

                    <c:if test="${not empty previewReport}">
                        <div class="card">
                            <div class="card-header">
                                <h3><i class="fas fa-table"></i> Revenue Breakdown</h3>
                            </div>
                            <div class="table-container">
                                <table class="financial-table">
                                    <thead>
                                        <tr>
                                            <th>Source</th>
                                            <th>Amount</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${revenueList}" var="r">
                                            <tr>
                                                <td>${r.source}</td>
                                                <td><fmt:formatNumber value="${r.amount}" type="currency"/></td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>

                        <div class="card">
                            <div class="card-header">
                                <h3><i class="fas fa-table"></i> Expenses Breakdown</h3>
                            </div>
                            <div class="table-container">
                                <table class="financial-table">
                                    <thead>
                                        <tr>
                                            <th>Source</th>
                                            <th>Amount</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${expenseList}" var="e">
                                            <tr>
                                                <td>${e.source}</td>
                                                <td><fmt:formatNumber value="${e.amount}" type="currency"/></td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                        <div style="display: flex; justify-content: end; margin-bottom: 20px;">
                            <form action="uploadReports" method="post">
                                <input type="hidden" name="action" value="savePreviewReport"/>
                                <button class="btn btn-primary">
                                    <i class="fas fa-save"></i> Confirm and Save Report
                                </button>
                            </form>
                        </div>
                    </c:if>
                </div>

            </main>
        </div>


        <script src="../js/hotelOwner.js">
        </script>
        <script src="../js/toastMessage.js">
        </script>


    </body>
</html>

