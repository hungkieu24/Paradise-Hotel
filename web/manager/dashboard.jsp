<%-- 
    Document   : dashboard
    Created on : Jul 11, 2025, 11:37:08 PM
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
        <title>DashBoard</title>
        <link rel="stylesheet" href="../css/managerStyle.css">
        <link rel="stylesheet" href="../css/custom.css"/>
        <link rel="stylesheet" href="../css/customManagerStyle.css"/>
        <link rel="stylesheet" href="../css/dashboardMangerStyle.css"/>
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
                    <a href="./dashboard" class="menu-item active">
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
                    <a href="./revenue" class="menu-item">
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
                        <h1 class="page-title">DashBoard</h1>
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



                <div class="content-body">
                    <div class="rooms-container">
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
                        </div>

                        <!-- Summary Cards -->
                        <div class="summary-cards" id="summaryCards">
                            <div class="summary-card revenue">
                                <div class="card-icon">
                                    <i class="fas fa-dollar-sign"></i>
                                </div>
                                <div class="card-content">
                                    <h3>Total Revenue</h3>
                                    <p class="amount" id="totalRevenue">
                                        <fmt:formatNumber value="${totalRevenue}" type="currency" />
                                    </p>
                                </div>
                            </div>

                            <div class="summary-card expenses">
                                <div class="card-icon">
                                    <i class="fas fa-receipt"></i>
                                </div>
                                <div class="card-content">
                                    <h3>Total Expenses</h3>
                                    <p class="amount" id="totalExpenses">
                                        <fmt:formatNumber value="${totalExpense}" type="currency" />
                                    </p>
                                </div>
                            </div>

                            <div class="summary-card profit">
                                <div class="card-icon">
                                    <i class="fas fa-chart-line"></i>
                                </div>
                                <div class="card-content">
                                    <h3>Net Profit</h3>
                                    <p class="amount" id="netProfit" style="color: ${Profit >= 0 ? 'green' : 'red'};">
                                        <fmt:formatNumber value="${Profit}" type="currency" />
                                    </p>
                                </div>
                            </div>

                            <div class="summary-card margin">
                                <div class="card-icon">
                                    <i class="fas fa-percentage"></i>
                                </div>
                                <div class="card-content">
                                    <h3>Profit Margin</h3>
                                    <p class="amount" id="profitMargin">
                                        <fmt:formatNumber value="${ProfitRate}" maxFractionDigits="2" />%
                                    </p>
                                </div>
                            </div>

                        </div>

                        <!-- Charts -->
                        <div class="charts-container" style="display: grid; grid-template-columns: 2.5fr 1fr;" >
                            <div class="card chart-card">
                                <div class="card-header">
                                    <h3><i class="fas fa-chart-line"></i> Monthly Profit Trend</h3>
                                </div>
                                <canvas id="profitChart"></canvas>
                            </div>
                            <div class="rooms-table dashboard-table" id="roomsTable">
                                <div class="dashboard-table__title">Initial Investment</div>
                                <p class="dashboard-table__desc">Total Capital: <strong><fmt:formatNumber value="${totalInital}" type="currency" /></strong></p>
                                <table>
                                    <thead>
                                        <tr>
                                            <th>Month</th>
                                            <th>Capital</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${initialInvestmentList}" var="b">
                                            <tr>
                                                <td>${b.getInvestedDate()}</td>
                                                <td><fmt:formatNumber value="${b.getCapital()}" type="currency" /></td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div> 


                        <div class="summary-cards" id="summaryCards">
                            <div class="summary-card margin">
                                <div class="card-icon">
                                    <i class="fas fa-percentage"></i>
                                </div>
                                <div class="card-content">
                                    <h3>Occupancy Rate</h3>
                                    <p class="amount" id="profitMargin">
                                        <fmt:formatNumber value="${occupancyRate}" maxFractionDigits="2" />%
                                    </p>
                                </div>
                            </div>
                            <div class="summary-card guest">
                                <div class="card-icon">
                                    <i class="fa-solid fa-users"></i>
                                </div>
                                <div class="card-content">
                                    <h3>Total Guests</h3>
                                    <p class="amount" id="profitMargin">
                                        <fmt:formatNumber value="${totalGuest}" maxFractionDigits="2" />
                                    </p>
                                </div>
                            </div>
                            <div class="summary-card feedback">
                                <div class="card-icon">
                                    <i class="fa-solid fa-star"></i>
                                </div>
                                <div class="card-content">
                                    <h3>Feedback Rating</h3>
                                    <p class="amount" id="feedbackMargin">
                                        <fmt:formatNumber value="${averageFeedbackRating}" maxFractionDigits="2" />
                                        <img src="../img/svg_icons/star.svg">
                                    </p>
                                </div>
                            </div>
                        </div>

                        <!-- Charts -->
                        <div class="charts-container">
                            <div class="card chart-card">
                                <div class="card-header" style="margin-bottom: 10px">
                                    <h3><i class="fas fa-chart-line"></i> Booking Status Distribution</h3>
                                    <p>Total Booking: <strong>${totalBooking}</strong></p>
                                </div>
                                <canvas id="bookingChart"></canvas>
                            </div>

                            <div class="card chart-card">
                                <div class="card-header" style="margin-bottom: 10px">
                                    <h3><i class="fas fa-chart-bar"></i> Top Services Used</h3>
                                </div>
                                <canvas id="serviceChart"></canvas>
                            </div>
                        </div>            
                    </div>
                </div>
            </main>
        </div>

        <!-- Modal: Edit -->
        <div id="edit-modal" class="modal modal--small hide">
            <div class="modal__content" style="padding: 30px">
                <div class="modal__heading">Edit Amenity</div>
                <form action="amenityEventHandler" method="post" id="edit-form" class="form form-card">
                    <input type="hidden" name="amenityID" id="amenityID">
                    <input type="hidden" name="branchID" value="${branch.id}">
                    <input type="hidden" name="action" value="edit">
                    <!-- Form row 1 -->
                    <div class="form__row">
                        <div class="form__group">
                            <label for="amenityName" class="form__label form-card__label">Amenity Name</label>
                            <div class="form__text-input">
                                <input type="text" name="amenityName" id="amenityName" class="form__input" placeholder="Amenity Name"/>
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
        <div id="add-modal" class="modal modal--small hide">
            <div class="modal__content" style="padding: 30px">
                <div class="modal__heading">Add Amenity</div>
                <form action="amenityEventHandler" method="post" id="add-form" class="form form-card">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="branchID" value="${branch.id}">
                    <!-- Form row 1 -->
                    <div class="form__row">
                        <div class="form__group">
                            <label for="amenityName" class="form__label form-card__label">Amenity Name</label>
                            <div class="form__text-input">
                                <input type="text" name="amenityName" id="amenityName-add" class="form__input" placeholder="Amenity Name"/>
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
                    <form action="amenityEventHandler" method="post">
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
            function fetchDashboardData(callback) {

                fetch(`/ParadiseHotel/manager/chartData?monthFrom=${monthFrom}&yearFrom=${yearFrom}&monthTo=${monthTo}&yearTo=${yearTo}`)
                        .then(res => res.json())
                        .then(data => callback(data))
                        .catch(err => console.error("Fetch dashboard data error:", err));
            }

            function renderBookingChart(bookingStatusCounts) {
                const labels = Object.keys(bookingStatusCounts);
                const counts = Object.values(bookingStatusCounts);

                const colors = [
                    "#FF6384", // Pending
                    "#36A2EB", // Paid
                    "#FFCE56", // CheckedIn
                    "#4BC0C0", // CheckedOut
                    "#9966FF", // Completed
                    "#FF9F40", // Cancelled
                    "#C9CBCF"  // NoShow
                ];

                const ctx = document.getElementById('bookingChart').getContext('2d');

                if (window.bookingChartInstance) {
                    // Nếu chart đã tồn tại thì update lại data
                    window.bookingChartInstance.data.labels = labels;
                    window.bookingChartInstance.data.datasets[0].data = counts;
                    window.bookingChartInstance.update();
                } else {
                    // Nếu chưa có thì tạo mới
                    window.bookingChartInstance = new Chart(ctx, {
                        type: 'pie',
                        data: {
                            labels: labels,
                            datasets: [{
                                    data: counts,
                                    backgroundColor: colors,
                                    borderWidth: 1
                                }]
                        },
                        options: {
                            plugins: {
                                legend: {
                                    position: 'bottom'
                                },
                            }
                        }
                    });
                }
            }

            function renderProfitChart(profitTrend) {
                const labels = Object.keys(profitTrend);
                const profits = Object.values(profitTrend);

                const ctx = document.getElementById('profitChart').getContext('2d');

                if (window.profitChartInstance) {
                    window.profitChartInstance.data.labels = labels;
                    window.profitChartInstance.data.datasets[0].data = profits;
                    window.profitChartInstance.update();
                } else {
                    window.profitChartInstance = new Chart(ctx, {
                        type: 'line',
                        data: {
                            labels: labels,
                            datasets: [{
                                    label: 'Profit (VND)',
                                    data: profits,
                                    borderColor: 'rgba(75, 192, 192, 1)',
                                    backgroundColor: 'rgba(75, 192, 192, 0.2)',
                                    tension: 0.4,
                                    fill: true
                                }]
                        },
                        options: {
                            responsive: true,
                            plugins: {
                                legend: {
                                    display: true
                                },
                            },
                            scales: {
                                y: {
                                    beginAtZero: true,
                                    ticks: {
                                        callback: function (value) {
                                            return new Intl.NumberFormat('vi-VN').format(value);
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            }

            function renderServiceChart(serviceUsageData) {
                const labels = Object.keys(serviceUsageData);
                const counts = Object.values(serviceUsageData);

                const colors = [
                    "#4BC0C0", "#36A2EB", "#FF6384", "#FFCE56", "#9966FF", "#FF9F40", "#C9CBCF",
                    "#8DD1E1", "#D0ED57", "#FFBB28", "#FF8042", "#A28BE7" // Có thêm để đủ nhiều màu
                ];

                const ctx = document.getElementById('serviceChart').getContext('2d');

                if (window.serviceChartInstance) {
                    // Nếu đã có chart thì update data
                    window.serviceChartInstance.data.labels = labels;
                    window.serviceChartInstance.data.datasets[0].data = counts;
                    window.serviceChartInstance.update();
                } else {
                    // Nếu chưa có thì tạo mới
                    window.serviceChartInstance = new Chart(ctx, {
                        type: 'bar', // Hoặc 'pie' nếu muốn
                        data: {
                            labels: labels,
                            datasets: [{
                                    label: 'Service Usage',
                                    data: counts,
                                    backgroundColor: colors.slice(0, labels.length),
                                    borderWidth: 1
                                }]
                        },
                        options: {
                            plugins: {
                                legend: {
                                    display: false // Bar chart không cần legend
                                },
                                title: {
                                    display: true,
                                    text: 'Top Services Used'
                                }
                            },
                            scales: {
                                y: {
                                    beginAtZero: true,
                                    title: {
                                        display: true,
                                        text: 'Usage Count'
                                    }
                                },
                                x: {
                                    ticks: {
                                        maxRotation: 45,
                                        minRotation: 0
                                    }
                                }
                            }
                        }
                    });
                }
            }


            document.addEventListener("DOMContentLoaded", function () {

                fetchDashboardData(function (data) {
                    renderProfitChart(data.profitTrend);
                    renderBookingChart(data.bookingStatusCounts);
                    renderServiceChart(data.serviceUsage);
                    // Các biểu đồ khác thêm ở đây sau
                });
            });


        </script>

        <!--Js điền dữ liệu vào Edit admin modal -->
        <script>
            function fillModalEdit(amenityID) {
                fetch("/ParadiseHotel/manager/amenityEventHandler?amenityID=" + amenityID)
                        .then(res => res.json())
                        .then(data => {
                            if (!data)
                                return;
                            document.getElementById("amenityID").value = data.id;
                            document.getElementById("amenityName").value = data.name;
                            document.getElementById("description").value = data.description;
                        })
                        .catch(err => console.error("Lỗi fetch:", err));
            }

            function fillModalDelete(amenityID) {
                fetch("/ParadiseHotel/manager/amenityEventHandler?amenityID=" + amenityID)
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
                    Validator.isRequired('#amenityName-add', 'Please enter room type name'),
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
                    Validator.isRequired('#amenityName', 'Please enter room type name'),
                    Validator.isRequired('#description', 'Please enter description'),
                ],
                onsubmit: function (formValue) {
                    document.querySelector('#edit-form').submit();
                }
            })
        </script>
    </body>
</html>


