<%-- 
    Document   : expense
    Created on : Jul 12, 2025, 4:36:30 PM
    Author     : hungk
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN" />

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Manager Revenue</title>
        <link rel="stylesheet" href="../css/managerStyle.css">
        <link rel="stylesheet" href="../css/custom.css"/>
        <link rel="stylesheet" href="../css/customManagerStyle.css"/>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
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

        <div class="app-container">
            <!-- Sidebar -->
            <nav class="sidebar" id="sidebar">
                <div class="sidebar-header">
                    <button class="sidebar-toggle" id="sidebarToggle">
                        <div class="brand">
                            <i class="fas fa-building"></i>
                            <span class="brand-text">${branch.name}</span>
                        </div>
                    </button>
                </div>
                <div class="sidebar-menu">
                    <a href="./dashboard" class="menu-item ">
                        <i class="fas fa-chart-line"></i>
                        <span class="menu-text">Dashboard</span>
                    </a>
                    <a href="../rooms" class="menu-item">
                        <i class="fas fa-bed"></i>
                        <span class="menu-text">Manage room</span>
                    </a>
                    <a href="roomType" class="menu-item ">
                        <i class="fas fa-bed"></i>
                        <span class="menu-text">Manage room type</span>
                    </a>
                    <a href="./revenue" class="menu-item active">
                        <i class="fa-solid fa-dollar-sign"></i>
                        <span class="menu-text">Manage Revenue & Expense</span>
                    </a>
                    <a href="./feedback" class="menu-item">
                        <i class="fas fa-comments"></i>
                        <span class="menu-text">Manage feedback</span>
                    </a>
                    <a href="../serviceManage" class="menu-item">
                        <i class="fas fa-concierge-bell"></i>
                        <span class="menu-text">Manage service</span>
                    </a>
                    <a href="../promotions" class="menu-item">
                        <i class="fas fa-tags"></i>
                        <span class="menu-text">Manage promotion</span>
                    </a>
                    <a href="../manager-membership" class="menu-item">
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
                        <h1 class="page-title">Manage Revenue</h1>
                    </div>
                    <div class="header-right">
                        <!-- <button class="theme-toggle" id="themeToggle">
                            <i class="fas fa-moon"></i>
                        </button> -->
                        <!-- Có thể thêm một số icon như thông báo hay light or dark -->
                        <a href="../editProfile" style="text-decoration: none"> 
                            <div class="user-info"> <!-- thể hiện user info -->
                                <i class="fas fa-user-circle"></i>
                                <span>${sessionScope.user.getUsername()}</span>
                            </div>
                        </a>
                    </div>
                </header>

                <div class="flash-messages" id="flashMessages"></div>

                <div class="content-body">
                    <div class="rooms-container">
                        <div class="tab-buttons">
                            <a href="./revenue" class="tab-button__link">
                                <div class="tab-button ">Revenue</div>
                            </a>
                            <a href="./expense" class="tab-button__link">
                                <div class="tab-button active">Expense</div>
                            </a>
                        </div>
                        <div class="page-actions">
                            <form action="">
                                <input type="hidden" name="action" value="filterByMonthRange">
                                <div class="page-action-wrapper">
                                    <div class="page-action">
                                        <label class="page-actions-label">Month From: </label>
                                        <select name="monthFrom" class="page-actions-select" onchange="fixDateRange()" required>
                                            <c:forEach var="i" begin="1" end="12">
                                                <option value="${i}" ${i == monthFrom ? 'selected' : ''}>
                                                    ${monthNames[i - 1]}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="page-action">
                                        <label class="page-actions-label">Year From: </label>
                                        <input type="text" name="yearFrom" oninput="fixDateRange()" class="page-actions-input" value="${yearFrom}" required />
                                    </div>
                                    <div class="page-action">
                                        <label class="page-actions-label">Month To: </label>
                                        <select name="monthTo" class="page-actions-select" onchange="fixDateRange()" required>
                                            <c:forEach var="i" begin="1" end="12">
                                                <option value="${i}" ${i == monthTo ? 'selected' : ''}>
                                                    ${monthNames[i - 1]}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="page-action">
                                        <label class="page-actions-label">Year To: </label>
                                        <input type="text" name="yearTo" oninput="fixDateRange()" class="page-actions-input" value="${yearTo}" required />
                                    </div>
                                    <div class="page-action">
                                        <button type="submit" class="btn btn-primary btn-filter">Filter</button>
                                    </div>
                                </div>
                            </form>
                            <button id="add-branch-btn" class="btn btn-primary js-toggle" toggle-target="#add-modal">
                                <i class="fas fa-plus"></i>
                                Add Expense
                            </button>
                        </div>

                        <div class="rooms-table" id="roomsTable">
                            <p class="cart-info__desc profile__desc">
                                Total Expense:  <strong> <fmt:formatNumber value="${totalExpense}" type="currency"/> </strong>
                            </p>
                            <table>
                                <thead>
                                    <tr>
                                        <th>Expense type</th>
                                        <th>Amount</th>
                                        <th>Expense date</th>
                                        <th>Description</th>
                                        <th>Created by</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${expenseList}" var="e" >
                                        <tr >
                                            <td>${e.getExpense_type()}</td>
                                            <td><fmt:formatNumber value="${e.getAmount()}" type="currency"/></td>
                                            <td>${e.getExpense_date()}</td>
                                            <td>${e.getDescription()}</td>
                                            <td>${e.getCreated_by() == user.id ? "Me": ""}</td>
                                            <td>
                                                <button class="btn btn-sm btn-secondary edit js-toggle" 
                                                        toggle-target="#edit-modal" 
                                                        data-actor-id="${e.getId()}">
                                                    <i class="fas fa-edit"></i>
                                                </button>
                                                <button class="btn btn-sm btn-danger delete js-toggle" 
                                                        toggle-target="#delete-modal" 
                                                        data-actor-id="${e.getId()}">
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

        <!-- Modal: Edit -->
        <div id="edit-modal" class="modal hide">
            <div class="modal__content" style="padding: 30px">
                <div class="modal__heading">Edit Expense</div>
                <form action="expenseEventHandler" method="post" id="edit-form" class="form form-card">
                    <input type="hidden" name="expenseID" id="expenseID">
                    <input type="hidden" name="branchID" value="${branch.id}">
                    <input type="hidden" name="action" value="edit">
                    <!-- Form row 1 -->
                    <div class="form__row">
                        <div class="form__group">
                            <label for="expenseType" class="form__label form-card__label">Expense Type</label>
                            <div class="form__text-input">
                                <input type="text" name="expenseType" id="expenseType" class="form__input" placeholder="Expense Type"/>
                            </div>
                            <p class="form__error"></p>
                        </div>
                    </div>

                    <!-- Form row 2 -->
                    <div class="form__row">
                        <div class="form__group">
                            <label for="expenseDate" class="form__label form-card__label">Expense Date</label>
                            <div class="form__text-input">
                                <input type="date" name="expenseDate" id="expenseDate" class="form__input" placeholder="Expense Date"/>
                            </div>
                            <p class="form__error"></p>
                        </div>
                        <div class="form__group">
                            <label for="amount" class="form__label form-card__label">Amount</label>
                            <div class="form__text-input">
                                <input type="text" name="amount" id="amount" class="form__input" placeholder="Amount"/>₫
                            </div>
                            <p class="form__error"></p>
                        </div>
                    </div>


                    <!-- Form row 4 -->
                    <div class="form__row">
                        <div class="form__group">
                            <label for="description" class="form__label form-card__label">Description</label>
                            <div class="form__text-area">
                                <textarea name="description" id="description" placeholder="Description"
                                          class="form__text-area-input" required ></textarea>
                            </div>
                            <p class="form__error"></p>
                        </div>
                    </div>

                    <div class="form-card__bottom">
                        <a href="./amenity" class="btn btn--text">
                            <div class=" btn--rounded btn-normal">Cancel</div>
                        </a>
                        <button type="submit" class="btn btn-primary btn--rounded">Save</button>
                    </div>
                </form>
            </div>
            <div class="modal__overlay js-toggle" toggle-target="#edit-modal"></div>
        </div>

        <!-- Modal: Add -->
        <div id="add-modal" class="modal hide">
            <div class="modal__content" style="padding: 30px">
                <div class="modal__heading">Add Expense</div>
                <form action="expenseEventHandler" method="post" id="add-form" class="form form-card">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="branchID" value="${branch.id}">
                    <!-- Form row 1 -->
                    <div class="form__row">
                        <div class="form__group">
                            <label for="expenseType" class="form__label form-card__label">Expense Type</label>
                            <div class="form__text-input">
                                <input type="text" name="expenseType" id="expenseType-add" class="form__input" placeholder="Expense Type"/>
                            </div>
                            <p class="form__error"></p>
                        </div>
                    </div>

                    <!-- Form row 2 -->
                    <div class="form__row">
                        <div class="form__group">
                            <label for="expenseDate" class="form__label form-card__label">Expense Date</label>
                            <div class="form__text-input">
                                <input type="date" name="expenseDate" id="expenseDate-add" class="form__input" placeholder="Expense Date"/>
                            </div>
                            <p class="form__error"></p>
                        </div>
                        <div class="form__group">
                            <label for="amount" class="form__label form-card__label">Amount</label>
                            <div class="form__text-input">
                                <input type="text" name="amount" id="amount-add" class="form__input" placeholder="Amount"/>
                                ₫
                            </div>
                            <p class="form__error"></p>
                        </div>
                    </div>

                    <!-- Form row 4 -->
                    <div class="form__row">
                        <div class="form__group">
                            <label for="description-add" class="form__label form-card__label">Description</label>
                            <div class="form__text-area">
                                <textarea name="description" id="description-add" placeholder="Description" class="form__text-area-input" required></textarea>
                            </div>
                            <p class="form__error"></p>
                        </div>
                    </div>

                    <div class="form-card__bottom">
                        <a href="./amenity" class="btn btn--text">
                            <div class="btn--rounded btn-normal">Cancel</div>
                        </a>
                        <button type="submit" class="btn btn-primary btn--rounded">Add</button>
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
                    <button class="btn btn--small btn-primary btn--text modal__btn btn--no-margin js-toggle"
                            toggle-target="#delete-modal">
                        Cancel
                    </button>
                    <form action="expenseEventHandler" method="post">
                        <input type="hidden" name="IdDelete" id="IdDelete" value="">
                        <input type="hidden" name="action" value="delete">
                        <button type="submit" class="btn btn--small btn-danger btn--primary modal__btn btn--no-margin" >
                            Delete
                        </button>
                    </form>
                </div>
            </div>
            <div class="modal__overlay js-toggle" toggle-target="#delete-modal"></div>
        </div>

        <!-- Scripts -->
        <script src="../js/Admin.js"></script>
        <script src="../js/themeAdmin.js"></script>
        <script src="../js/hungkd.js"></script>
        <script src="../js/api.js"></script>
        <script src="../js/validationForm.js"></script>
        <script>
            function fixDateRange() {
                const monthFromSelect = document.querySelector('[name="monthFrom"]');
                const yearFromInput = document.querySelector('[name="yearFrom"]');
                const monthToSelect = document.querySelector('[name="monthTo"]');
                const yearToInput = document.querySelector('[name="yearTo"]');

                const monthFrom = parseInt(monthFromSelect.value);
                const yearFrom = parseInt(yearFromInput.value);
                let monthTo = parseInt(monthToSelect.value);
                const yearTo = parseInt(yearToInput.value);

                if (isNaN(monthFrom) || isNaN(yearFrom) || isNaN(monthTo) || isNaN(yearTo)) {
                    // Nếu người dùng chưa nhập đủ thì không kiểm tra
                    return;
                }

                if (yearTo < yearFrom) {
                    // Nếu năm To nhỏ hơn năm From => reset monthTo về ""
                    monthToSelect.value = "";
                } else if (yearTo === yearFrom && monthTo < monthFrom) {
                    // Nếu cùng năm nhưng monthTo < monthFrom => reset monthTo = monthFrom
                    monthToSelect.value = monthFrom;
                }
            }
        </script>
        <script>
            function formatCurrencyVND(value) {
                // Loại bỏ tất cả ký tự không phải số
                let number = value.replace(/\D/g, '');

                if (!number)
                    return '';

                // Chuyển về dạng số nguyên rồi format
                return Number(number).toLocaleString('vi-VN');
            }

            document.getElementById('amount-add').addEventListener('input', function (e) {
                const caretPosition = this.selectionStart;

                const rawValue = this.value;
                const formattedValue = formatCurrencyVND(rawValue);

                this.value = formattedValue;

                // Đặt lại con trỏ cuối chuỗi (hoặc bạn có thể xử lý chính xác hơn nếu muốn)
                this.setSelectionRange(this.value.length, this.value.length);
            });
            document.getElementById('amount').addEventListener('input', function (e) {
                const caretPosition = this.selectionStart;

                const rawValue = this.value;
                const formattedValue = formatCurrencyVND(rawValue);

                this.value = formattedValue;

                // Đặt lại con trỏ cuối chuỗi (hoặc bạn có thể xử lý chính xác hơn nếu muốn)
                this.setSelectionRange(this.value.length, this.value.length);
            });

        </script>
        <!--Js điền dữ liệu vào Edit admin modal -->
        <script>
            function fillModalEdit(ID) {
                fetch("/ParadiseHotel/manager/expenseEventHandler?expenseID=" + ID)
                        .then(res => res.json())
                        .then(data => {
                            if (!data)
                                return;
                            document.getElementById("expenseID").value = data.id;
                            document.getElementById("expenseType").value = data.expense_type;
                            document.getElementById("expenseDate").value = data.expense_date;
                            document.getElementById("amount").value = Number(data.amount).toLocaleString('vi-VN');
                            document.getElementById("description").value = data.description ? data.description : "";
                        })
                        .catch(err => console.error("Lỗi fetch:", err));
            }

            function fillModalDelete(ID) {
                fetch("/ParadiseHotel/manager/expenseEventHandler?expenseID=" + ID)
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
                    Validator.isRequired('#expenseType-add', 'Please enter expense type'),
                    Validator.isRequired('#expenseDate-add', 'Please enter expense date'),
                    Validator.isRequired('#amount-add', 'Please enter amount'),
                    Validator.isRequired('#description-add', 'Please enter description')
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
                    Validator.isRequired('#expenseType', 'Please enter expense type'),
                    Validator.isRequired('#expenseDate', 'Please enter expense date'),
                    Validator.isRequired('#amount', 'Please enter amount'),
                    Validator.isRequired('#description', 'Please enter description'),
                ],
                onsubmit: function (formValue) {
                    document.querySelector('#edit-form').submit();
                }
            })
        </script>
    </body>
</html>



