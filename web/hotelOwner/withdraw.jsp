<%-- 
    Document   : withdraw
    Created on : Jul 25, 2025, 9:18:26 AM
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
        <title>Investment</title>
        <link rel="stylesheet" href="../css/hotelOwnerStyle.css" />
        <link rel="stylesheet" href="../css/custom.css">
        <link rel="stylesheet" href="../css/branchStyle.css">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet" />

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
                    <a href="./investment" class="nav-item" data-page="upload">
                        <i class="fa-solid fa-dollar-sign"></i>
                        <span>Investment</span>
                    </a>
                    <a href="./withdraw" class="nav-item active" data-page="upload">
                        <i class="fa-solid fa-dollar-sign"></i>
                        <span>Withdraw</span>
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
                        <a href="./withdraw" style="text-decoration: none">
                            <h1 id="page-title">Withdraw</h1>
                        </a>
                        <p id="page-description">View and manage withdrawal transactions</p>
                    </div>
                    <div class="header-right">
                        <div class="admin-profile">
                            <div class="profile-dropdown">
                                <div class="profile-avatar">
                                    <i class="fas fa-user"></i>
                                </div>
                                <div class="dropdown-content">
                                    <div class="dropdown-header">
                                        <strong>${sessionScope.user.getFullname()}</strong>
                                        <small>${sessionScope.user.getEmail()}</small>
                                    </div>
                                    <a href="../editProfile">Profile Settings</a>
                                    <hr>
                                    <a href="../login?action=logout" class="sign-out">Sign Out</a>
                                </div>
                            </div>
                        </div>
                    </div>
                </header>

                <!-- Dashboard Page -->
                <div class="page-content active" id="dashboard">

                    <!-- Filters -->
                    <div class="card">
                        <div class="filters">
                            <form action="">
                                <input type="hidden" name="action" value="filterByMonthYear">
                                <div class="page-action-wrapper">
                                    <div class="filter-group">
                                        <label for="statusFilter">Status</label>
                                        <select id="statusFilter" name="status">
                                            <option value="all" ${param.status == 'all' ? 'selected' : ''}>All</option>
                                            <option value="Success" ${param.status == 'Success' ? 'selected' : ''}>Success</option>
                                            <option value="Pending" ${param.status == 'Pending' ? 'selected' : ''}>Pending</option>
                                            <option value="Cancelled" ${param.status == 'Cancelled' ? 'selected' : ''}>Cancelled</option>
                                        </select>
                                    </div>
                                    <div class="filter-group">
                                        <label >Month From: </label>
                                        <select name="monthFrom" class="page-actions-select" required>
                                            <c:forEach var="i" begin="1" end="12">
                                                <option value="${i}" ${i == monthFrom ? 'selected' : ''}>
                                                    ${monthNames[i - 1]}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="filter-group">
                                        <label class="page-actions-label">Year From: </label>
                                        <input type="text" name="yearFrom" class="page-actions-input" value="${yearFrom}" required />
                                    </div>
                                    <div class="filter-group">
                                        <label class="page-actions-label">Month To: </label>
                                        <select name="monthTo" class="page-actions-select" required>
                                            <c:forEach var="i" begin="1" end="12">
                                                <option value="${i}" ${i == monthTo ? 'selected' : ''}>
                                                    ${monthNames[i - 1]}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="filter-group">
                                        <label class="page-actions-label">Year To: </label>
                                        <input type="text" name="yearTo" class="page-actions-input" value="${yearTo}" required />
                                    </div>
                                    <div class="page-action">
                                        <button type="submit" class="btn btn-primary btn-filter">Filter</button>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>

                    <!-- Detailed Table -->
                    <div class="card">
                        <div class="card-header">
                            <h3><i class="fas fa-table"></i>Initial Investment</h3>
                        </div>
                        <p>Total: <strong>${listSize}</strong></p>
                        <div class="table-container">
                            <table class="financial-table" id="financialTable">
                                <thead>
                                    <tr>
                                        <th>Transaction Type</th>
                                        <th>Amount</th>
                                        <th>Description</th>
                                        <th>Status</th>
                                        <th>CreatedAt</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:if test="${empty walletTransactions}">
                                        <tr class="empty-state">
                                            <td colspan="8">
                                                <i class="fas fa-chart-line"></i>
                                                <p>No data available.</p>
                                            </td>
                                        </tr>
                                    </c:if>
                                    <c:forEach items="${walletTransactions}" var="data" >
                                        <tr>
                                            <td>${data.getTransactionType()}</td>
                                            <td><fmt:formatNumber value="${data.getAmount()}" type="currency"/></td>
                                            <td>${data.getDescription()}</td>
                                            <td>${data.getStatus()}</td>
                                            <td>${data.getCreatedAt()}</td>
                                            <td>
                                                <button class="btn btn-sm btn-secondary edit js-toggle" 
                                                        toggle-target="#edit-modal" 
                                                        data-actor-id="${data.getTransactionID()}">
                                                    <i class="fas fa-edit"></i>
                                                </button>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <div class="pagination">
                        <c:set var="queryParams" value="" />
                        <c:if test="${not empty action and not empty status and not empty monthFrom and not empty yearFrom and not empty monthTo and not empty yearTo}">
                            <c:set var="queryParams" value="&action=${action}&status=${status}&monthFrom=${monthFrom}&yearFrom=${yearFrom}&monthTo=${monthTo}&yearTo=${yearTo}" />
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
                </div>
            </main>
        </div>

        <!-- Modal: Edit Product -->
        <div id="edit-modal" class="modal hide">
            <div class="modal__content">
                <div class="modal__heading">Edit Withdraw</div>
                <form action="withdrawEventHandler" method="post" id="edit-form" class="form form-card">
                    <input type="hidden" name="transactionID" id="transactionID">
                    <input type="hidden" name="action" value="edit">

                    <!-- Form row 2 -->
                    <div class="form__row">
                        <div class="form__group">
                            <label for="bankAccountNumber" class="form__label form-card__label">Bank Account Number</label>
                            <div class="form__text-input form__nochange">
                                <input type="text" name="bankAccountNumber" id="bankAccountNumber" class="form__input form__nochange" placeholder="Bank Account Number" readonly/>
                            </div>
                            <p class="form__error"></p>
                        </div>
                        <div class="form__group">
                            <label for="accountHolder" class="form__label form-card__label">Account Holder</label>
                            <div class="form__text-input form__nochange">
                                <input type="text" name="accountHolder" id="accountHolder" class="form__input form__nochange" placeholder="Account Holder" readonly/>
                            </div>
                            <p class="form__error"></p>
                        </div>
                        <div class="form__group">
                            <label for="amount" class="form__label form-card__label">Amount</label>
                            <div class="form__text-input form__nochange">
                                <input type="text" name="amount" id="amount" class="form__input form__nochange" placeholder="Amount" readonly/>
                            </div>
                            <p class="form__error"></p>
                        </div>
                    </div>

                    <div class="cart-info__separate"></div>

                    <div class="form__row">
                        <div class="form__group ">
                            <label for="districtInput" class="form__label form-card__label">Status</label>
                            <div class="form__text-input">
                                <select class="form__select district address__select" id="statusChoose" name="statusChoose">
                                    <option value="Success">Success</option>
                                    <option value="Pending">Pending</option>
                                    <option value="Cancelled">Cancelled</option>
                                </select>
                            </div>
                            <p class="form__error"></p>
                        </div>
                    </div>

                    <!-- Form row 4 -->
                    <div class="form__row">
                        <div class="form__group">
                            <label for="description-add" class="form__label form-card__label">Description</label>
                            <div class="form__text-area">
                                <textarea name="description" id="description" placeholder="Description" class="form__text-area-input" required></textarea>
                            </div>
                            <p class="form__error"></p>
                        </div>
                    </div>

                    <div class="form-card__bottom">
                        <a href="./withdraw" class="btn btn--text" style="text-decoration: none">
                            <div class=" btn--rounded btn-normal">Cancel</div>
                        </a>
                        <button type="submit" class="btn btn-primary btn--rounded">Done</button>
                    </div>
                </form>
            </div>
            <div class="modal__overlay js-toggle" toggle-target="#edit-modal"></div>
        </div>

        <!-- Scripts -->
        <script src="../js/hungkd.js"></script>
        <script src="../js/validationForm.js"></script>

        <!--Js điền dữ liệu vào Edit modal -->
        <script>
            function fillModalEdit(transactionID) {
                fetch("/ParadiseHotel/hotelOwner/withdrawEventHandler?transactionID=" + transactionID)
                        .then(res => res.json())
                        .then(data => {
                            if (!data)
                                return;

                            document.getElementById("transactionID").value = transactionID;
                            document.getElementById("bankAccountNumber").value = data.bankAccount.AccountNumber;
                            document.getElementById("accountHolder").value = data.bankAccount.AccountHolder;
                            document.getElementById("amount").value = data.Amount.toLocaleString("vi-VN");
                            document.getElementById("statusChoose").value = data.Status;
                            document.getElementById("description").value = data.Description;
                        })
                        .catch(err => console.error("Lỗi fetch:", err));
            }

            initButtons("edit.js-toggle", "data-actor-id", fillModalEdit);
        </script>
        <script>
            Validator({
                form: '#edit-form',
                formGroupSelector: '.form__group',
                errorSelector: '.form__error',
                rules: [
                    Validator.isSelectRequired('#statusChoose', 'Please select the status'),
                    Validator.isRequired('#description', 'Please enter description')
                ],
                onsubmit: function (formValue) {
                    document.querySelector('#edit-form').submit();
                }
            })
        </script>
    </body>
</html>


