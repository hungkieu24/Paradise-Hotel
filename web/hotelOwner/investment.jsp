<%-- 
    Document   : investment
    Created on : Jul 14, 2025, 7:40:53 AM
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
                    <a href="./investment" class="nav-item active" data-page="upload">
                        <i class="fa-solid fa-dollar-sign"></i>
                        <span>Investment</span>
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
                            <h1 id="page-title">Investment</h1>
                        </a>
                        <p id="page-description">View investments of each branch, add and adjust for all hotel branche</p>
                    </div>
                    <div class="header-right">
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

                <!-- Dashboard Page -->
                <div class="page-content active" id="dashboard">

                    <!-- Filters -->
                    <div class="card">
                        <div class="filters">
                            <form action="">
                                <input type="hidden" name="action" value="filterByMonthYear">
                                <div class="page-action-wrapper">
                                    <div class="page-action">
                                        <label class="page-actions-label">Branch: </label>
                                        <select name="branchID" class="page-actions-select" required>
                                            <option value="">Choose branch</option>
                                            <c:forEach items="${branchList}" var="br">
                                                <option value="${br.getId()}">${br.getName()}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="page-action">
                                        <label class="page-actions-label">Month: </label>
                                        <select name="month" class="page-actions-select" required>
                                            <c:forEach var="i" begin="1" end="12">
                                                <option value="${i}" ${i == month ? 'selected' : ''}>
                                                    ${monthNames[i - 1]}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="page-action">
                                        <label class="page-actions-label">Year: </label>
                                        <input type="text" name="year" class="page-actions-input" value="${year}" required />
                                    </div>
                                    <div class="page-action">
                                        <button type="submit" class="btn btn-primary btn-filter">Filter</button>
                                    </div>
                                </div>
                            </form>
                            <button id="add-branch-btn" class="btn btn-primary js-toggle" toggle-target="#add-modal">
                                <i class="fas fa-plus"></i>
                                Add new investment
                            </button>
                        </div>
                    </div>

                    <!-- Detailed Table -->
                    <div class="card">
                        <div class="card-header">
                            <h3><i class="fas fa-table"></i>Initial Investment</h3>
                        </div>
                        <p>Total: <strong><fmt:formatNumber value="${totalInitialCapital}" type="currency"/></strong></p>
                        <div class="table-container">
                            <table class="financial-table" id="financialTable">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Branch Name</th>
                                        <th>Investment date</th>
                                        <th>Capital</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:if test="${empty investmentList}">
                                        <tr class="empty-state">
                                            <td colspan="8">
                                                <i class="fas fa-chart-line"></i>
                                                <p>No data available.</p>
                                            </td>
                                        </tr>
                                    </c:if>
                                    <c:forEach items="${investmentList}" var="data" >
                                        <tr data-room-id="1">
                                            <td>${data.getId()}</td>
                                            <td>${data.getBranch().getName()}</td>
                                            <td>${data.getInvestedDate()}</td>
                                            <td><fmt:formatNumber value="${data.getCapital()}" type="currency"/></td>
                                            <td>
                                                <button class="btn btn-sm btn-secondary edit js-toggle" 
                                                        toggle-target="#edit-modal" 
                                                        data-actor-id="${data.getId()}">
                                                    <i class="fas fa-edit"></i>
                                                </button>
                                                <button class="btn btn-sm btn-danger delete js-toggle" 
                                                        toggle-target="#delete-modal" 
                                                        data-actor-id="${data.getId()}">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </main>
        </div>

        <!-- Modal: Edit Product -->
        <div id="edit-modal" class="modal hide">
            <div class="modal__content">
                <div class="modal__heading">Edit Investment</div>
                <form action="investmentEventHandler" method="post" id="edit-form" class="form form-card">
                    <input type="hidden" name="investmentID" id="investmentID">
                    <input type="hidden" name="branchID" id="branchID">
                    <input type="hidden" name="action" value="edit">

                    <!-- Form row 2 -->
                    <div class="form__row">
                        <div class="form__group">
                            <label for="branchName" class="form__label form-card__label">Branch Name</label>
                            <div class="form__text-input form__nochange">
                                <input type="text" name="branchName" id="branchName" class="form__input form__nochange" placeholder="Branch Name" readonly/>
                            </div>
                            <p class="form__error"></p>
                        </div>
                    </div>

                    <div class="cart-info__separate"></div>

                    <!-- Form row 3 -->
                    <div class="form__row">
                        <div class="form__group">
                            <label for="investedDate" class="form__label form-card__label">Investment date</label>
                            <div class="form__text-input">
                                <input type="date" name="investedDate" id="investedDate" class="form__input" placeholder="Investment date"/>
                            </div>
                            <p class="form__error"></p>
                        </div>
                        <div class="form__group">
                            <label for="capital" class="form__label form-card__label">Capital</label>
                            <div class="form__text-input">
                                <input type="text" name="capital" id="capital" placeholder="Capital" class="form__input"/>
                            </div>
                            <p class="form__error"></p>
                        </div>
                    </div>

                    <div class="form-card__bottom">
                        <a href="./investment" class="btn btn--text" style="text-decoration: none">
                            <div class=" btn--rounded btn-normal">Cancel</div>
                        </a>
                        <button type="submit" class="btn btn-primary btn--rounded">Change</button>
                    </div>
                </form>
            </div>
            <div class="modal__overlay js-toggle" toggle-target="#edit-modal"></div>
        </div>

        <!-- Modal: Add modal -->
        <div id="add-modal" class="modal hide">
            <div class="modal__content">
                <div class="modal__heading">Add Investment</div>
                <form action="investmentEventHandler" method="post" id="add-form" class="form form-card">
                    <input type="hidden" name="action" value="add">
                    <!-- Form row 2 -->
                    <div class="form__row">
                        <div class="form__group ">
                            <label for="districtInput" class="form__label form-card__label">Branch</label>
                            <div class="form__text-input">
                                <select class="form__select district address__select" id="branchChoose" name="branchID">
                                    <option value="">Choose branch</option>
                                    <c:forEach items="${branchList}" var="br">
                                        <option value="${br.getId()}">${br.getName()}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <p class="form__error"></p>
                        </div>
                    </div>

                    <div class="cart-info__separate"></div>

                    <!-- Form row 3 -->
                    <div class="form__row">
                        <div class="form__group">
                            <label for="investedDate" class="form__label form-card__label">Investment date</label>
                            <div class="form__text-input">
                                <input type="date" name="investedDate" id="investedDate-add" class="form__input" placeholder="Investment date"/>
                            </div>
                            <p class="form__error"></p>
                        </div>
                        <div class="form__group">
                            <label for="capital" class="form__label form-card__label">Capital</label>
                            <div class="form__text-input">
                                <input type="text" name="capital" id="capital-add" placeholder="Capital" class="form__input"/>
                            </div>
                            <p class="form__error"></p>
                        </div>
                    </div>

                    <div class="form-card__bottom">
                        <a href="./investment" class="btn btn--text" style="text-decoration: none">
                            <div class=" btn--rounded btn-normal">Cancel</div>
                        </a>
                        <button type="submit" id="btn-Addform-submit" class="btn btn-primary btn--rounded">Add</button>
                    </div>
                </form>
            </div>
            <div class="modal__overlay js-toggle" toggle-target="#add-modal"></div>
        </div>

        <!-- Modal delete -->
        <div id="delete-modal" class="modal modal--small hide">
            <div class="modal__content">

                <div class="modal__text">Do you want to delete this?</div>
                <div class="modal__bottom">
                    <button
                        class="btn btn--small btn-primary btn--text modal__btn btn--no-margin js-toggle"
                        toggle-target="#delete-modal"
                        >
                        Cancel
                    </button>
                    <form action="investmentEventHandler" method="post">
                        <input type="hidden" name="IdDelete" id="IdDelete" value="">
                        <input type="hidden" name="action" value="delete">
                        <button
                            type="submit"
                            class="btn btn--small btn-danger btn--primary modal__btn btn--no-margin"
                            >
                            Delete
                        </button>
                    </form>
                </div>
            </div>
            <div class="modal__overlay js-toggle" toggle-target="#delete-modal"></div>
        </div>

        <!-- Scripts -->
        <script src="../js/themeAdmin.js"></script>
        <script src="../js/hungkd.js"></script>
        <script src="../js/validationForm.js"></script>

        <!-- JS format tiền việt -->
        <script>
            function formatCurrencyVND(value) {
                // Loại bỏ tất cả ký tự không phải số
                let number = value.replace(/\D/g, '');

                if (!number)
                    return '';

                // Chuyển về dạng số nguyên rồi format
                return Number(number).toLocaleString('vi-VN');
            }

            document.getElementById('capital-add').addEventListener('change', function (e) {
                const caretPosition = this.selectionStart;

                const rawValue = this.value;
                const formattedValue = formatCurrencyVND(rawValue);

                this.value = formattedValue;

                // Đặt lại con trỏ cuối chuỗi (hoặc bạn có thể xử lý chính xác hơn nếu muốn)
                this.setSelectionRange(this.value.length, this.value.length);
            });
            document.getElementById('capital').addEventListener('change', function (e) {
                const caretPosition = this.selectionStart;

                const rawValue = this.value;
                const formattedValue = formatCurrencyVND(rawValue);

                this.value = formattedValue;

                // Đặt lại con trỏ cuối chuỗi (hoặc bạn có thể xử lý chính xác hơn nếu muốn)
                this.setSelectionRange(this.value.length, this.value.length);
            });

        </script>


        <!--Js điền dữ liệu vào Edit modal -->
        <script>
            function fillModalEdit(investmentID) {
                fetch("/ParadiseHotel/hotelOwner/investmentEventHandler?investmentID=" + investmentID)
                        .then(res => res.json())
                        .then(data => {
                            if (!data)
                                return;

                            document.getElementById("branchID").value = data.branch.id;
                            document.getElementById("branchName").value = data.branch.name;
                            document.getElementById("investmentID").value = data.id;
                            document.getElementById("investedDate").value = data.InvestedDate;
                            document.getElementById("capital").value = data.Capital;
                        })
                        .catch(err => console.error("Lỗi fetch:", err));
            }

            function fillModalDelete(investmentID) {
                console.log("branchID: ", investmentID);
                fetch("/ParadiseHotel/hotelOwner/investmentEventHandler?investmentID=" + investmentID)
                        .then(res => res.json())
                        .then(data => {
                            if (!data)
                                return;

                            document.getElementById("IdDelete").value = data.id;
                        })
                        .catch(err => console.error("Lỗi fetch:", err));
            }
            initButtons("edit.js-toggle", "data-actor-id", fillModalEdit);
            initButtons("delete.js-toggle", "data-actor-id", fillModalDelete);

        </script>
        <script>
            Validator({
                form: '#add-form',
                formGroupSelector: '.form__group',
                errorSelector: '.form__error',
                rules: [
                    Validator.isSelectRequired('#branchChoose', 'Please select the branch you want to add'),
                    Validator.isRequired('#investedDate-add', 'Please enter Investment Date'),
                    Validator.isRequired('#capital-add', 'Please enter capital'),
                ],
                onsubmit: function (formValue) {
                    document.querySelector('#add-form').submit();
                }
            })

            Validator({
                form: '#edit-form',
                formGroupSelector: '.form__group',
                errorSelector: '.form__error',
                rules: [
                    Validator.isRequired('#investedDate', 'Please enter the full name of the branch'),
                    Validator.isRequired('#capital', 'Please enter capital'),
                ],
                onsubmit: function (formValue) {
                    document.querySelector('#edit-form').submit();
                }
            })
        </script>
    </body>
</html>

