<%-- 
    Document   : financialDashboard
    Created on : Jul 8, 2025, 7:50:39 AM
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

        <div class="app-layout">
            <!-- Left Sidebar -->
            <aside class="sidebar" id="sidebar">
                <div class="sidebar-header">
                    <img src="../img/logoHotelOwner.svg" alt="Hotel Management" class="sidebar-logo" />
                    <span class="sidebar-title">Hotel Manager</span>
                </div>

                <nav class="sidebar-nav">
                    <a href="./financialDashboard" class="nav-item active" data-page="dashboard">
                        <i class="fas fa-chart-line"></i>
                        <span>Dashboard</span>
                    </a>
                    <a href="./uploadReports.jsp" class="nav-item " data-page="upload">
                        <i class="fas fa-upload"></i>
                        <span>Upload Reports</span>
                    </a>
                    <a href="./investment" class="nav-item" data-page="upload">
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
                            <h1 id="page-title">Financial Dashboard</h1>
                        </a>
                        <p id="page-description">View revenue, expenses, and profit analysis for all hotel branches</p>
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

                <!-- Dashboard Page -->
                <div class="page-content active" id="dashboard">

                    <!-- Filters -->
                    <div class="card">
                        <div class="filters">
                            <form class="formFilter">
                                <input name="action" value="filter" type="hidden">
                                <div class="filter-group">
                                    <label for="branchFilter">Branch</label>
                                    <select id="branchFilter" name="branchId">
                                        <option value="0" ${param.branchId == '0' ? 'selected' : ''}>All Branches</option>
                                        <c:forEach items="${branchList}" var="br">
                                            <option value="${br.getId()}" ${param.branchId == br.getId() ? 'selected' : ''}>${br.getName()}</option>
                                        </c:forEach>
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
                            </form>
                        </div>
                        <p style="margin-top: 10px ">Total Month: <strong>${totalMonths}</strong></p>
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
                    <div class="charts-container">
                        <div class="card chart-card">
                            <div class="card-header">
                                <h3><i class="fas fa-chart-line"></i> Monthly Profit Trend</h3>
                            </div>
                            <canvas id="profitChart"></canvas>
                        </div>

                        <div class="card chart-card">
                            <div class="card-header">
                                <h3 id="chart-title"> </h3>
                            </div>
                            <canvas id="branchChart"></canvas>
                        </div>
                    </div>

                    <!-- Detailed Table -->
                    <div class="card">
                        <div class="card-header">
                            <h3><i class="fas fa-table"></i>Initial Investment</h3>

                        </div>
                        <p>Total Capital: <strong><fmt:formatNumber value="${totalInitialInvestment}" type="currency" /></strong> </p>
                        <div class="table-container">
                            <table class="financial-table" id="financialTable">
                                <thead>
                                    <tr>
                                        <th>Branch</th>
                                        <th>Month</th>
                                        <th>Capital</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:if test="${empty initialInvestmentList}">
                                        <tr class="empty-state">
                                            <td colspan="8">
                                                <i class="fas fa-chart-line"></i>
                                                <p>No data available. Please upload reports to view data.</p>
                                            </td>
                                        </tr>
                                    </c:if>
                                    <c:forEach items="${initialInvestmentList}" var="b">
                                        <tr>
                                            <td>${b.getBranch().name}</td>
                                            <td>${b.getInvestedDate()}</td>
                                            <td><fmt:formatNumber value="${b.getCapital()}" type="currency" /></td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <!-- Detailed Table -->
                    <div class="card">
                        <div class="card-header">
                            <h3><i class="fas fa-table"></i> Detailed Financial Report</h3>
                        </div>
                        <div class="table-container">
                            <table class="financial-table" id="financialTable">
                                <thead>
                                    <tr>
                                        <th>Branch</th>
                                        <th>Month</th>
                                        <th>Revenue</th>
                                        <th>Expenses</th>
                                        <th>Profit</th>
                                        <th>Profit Rate</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:if test="${empty branchMonthlyReportList}">
                                        <tr class="empty-state">
                                            <td colspan="8">
                                                <i class="fas fa-chart-line"></i>
                                                <p>No financial data available. Please upload reports to view data.</p>
                                            </td>
                                        </tr>
                                    </c:if>
                                    <c:forEach items="${branchMonthlyReportList}" var="b">
                                        <tr>
                                            <td>${b.hotelBranch.name}</td>
                                            <td>${b.getReportMonth()}</td>
                                            <td><fmt:formatNumber value="${b.getRevenue()}" type="currency" /></td>
                                            <td><fmt:formatNumber value="${b.getExpenses()}" type="currency" /></td>
                                            <td class="${b.getProfit() < 0 ? 'negative' : 'positive'}">
                                                <fmt:formatNumber value="${b.getProfit()}" type="currency" />
                                            </td>
                                            <td class="${b.getProfitRate() < 0 ? 'negative' : 'positive'}" >${b.getProfitRate()}%</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <div class="pagination">
                        <c:set var="queryParams" value="" />
                        <c:if test="${not empty action and not empty branchId and not empty monthFrom and not empty yearFrom and not empty monthTo and not empty yearTo}">
                            <c:set var="queryParams" value="&action=${action}&branchId=${branchId}&monthFrom=${monthFrom}&yearFrom=${yearFrom}&monthTo=${monthTo}&yearTo=${yearTo}" />
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


        <script src="../js/hotelOwner.js">
        </script>
        <script>
            document.addEventListener("DOMContentLoaded", function () {
                const fromDateInput = document.getElementById("fromDate");
                const toDateInput = document.getElementById("toDate");

                fromDateInput.addEventListener("change", function () {
                    const fromDate = fromDateInput.value;
                    toDateInput.min = fromDate;

                    // Nếu toDate đang nhỏ hơn fromDate thì reset
                    if (toDateInput.value && toDateInput.value < fromDate) {
                        toDateInput.value = '';
                    }
                });
            });
        </script>

        <script>
            let profitChart;
            let branchChart;
            let branchChartInstance;
            function updateProfitChart(labels, data) {
                const ctx = document.getElementById('profitChart').getContext('2d');

                if (profitChart) {
                    profitChart.destroy();
                }

                profitChart = new Chart(ctx, {
                    type: 'line',
                    data: {
                        labels: labels,
                        datasets: [{
                                label: 'Monthly Profit',
                                data: data,
                                backgroundColor: 'rgba(75, 192, 192, 0.2)',
                                borderColor: 'rgba(75, 192, 192, 1)',
                                borderWidth: 2,
                                tension: 0.3,
                                fill: true,
                                pointRadius: 4
                            }]
                    },
                    options: {
                        responsive: true,
                        scales: {
                            y: {
                                beginAtZero: true,
                                ticks: {
                                    callback: function (value) {
                                        return new Intl.NumberFormat('vi-VN', {
                                            style: 'currency',
                                            currency: 'VND'
                                        }).format(value);
                                    }
                                }
                            }
                        }
                    }
                });
            }

            function updateBranchChart(chartData) {
                const ctx = document.getElementById('branchChart').getContext('2d');

                if (branchChart) {
                    branchChart.destroy();
                }

                const branchNames = Object.keys(chartData);
                const revenues = branchNames.map(name => chartData[name].revenue || 0);
                const expenses = branchNames.map(name => chartData[name].expenses || 0);
                const profits = branchNames.map(name => chartData[name].profit || 0);


                branchChart = new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: branchNames,
                        datasets: [
                            {
                                label: 'Revenue',
                                data: revenues,
                                backgroundColor: 'rgba(54, 162, 235, 0.6)'
                            },
                            {
                                label: 'Expenses',
                                data: expenses,
                                backgroundColor: 'rgba(255, 99, 132, 0.6)'
                            },
                            {
                                label: 'Profit',
                                data: profits,
                                backgroundColor: 'rgba(75, 192, 192, 0.6)'
                            }
                        ]
                    },
                    options: {
                        responsive: true,
                        scales: {
                            y: {
                                beginAtZero: true,
                                ticks: {
                                    callback: function (value) {
                                        return new Intl.NumberFormat('vi-VN', {
                                            style: 'currency',
                                            currency: 'VND'
                                        }).format(value);
                                    }
                                }
                            }
                        }
                    }
                });
            }

            function updateBranchChartHaveBranchID(branchData) {
                const labels = Object.keys(branchData); // ["2025-06", "2025-07", "2025-08"]

                const revenueData = labels.map(month => branchData[month].revenue);
                const expensesData = labels.map(month => branchData[month].expenses);
                const profitData = labels.map(month => branchData[month].profit);

                const ctx = document.getElementById('branchChart').getContext('2d');

                // Destroy chart if exists to prevent duplicate
                if (branchChartInstance) {
                    branchChartInstance.destroy();
                }

                branchChartInstance = new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: labels,
                        datasets: [
                            {
                                label: 'Revenue',
                                data: revenueData,
                                backgroundColor: 'rgba(54, 162, 235, 0.6)'
                            },
                            {
                                label: 'Expenses',
                                data: expensesData,
                                backgroundColor: 'rgba(255, 99, 132, 0.6)'
                            },
                            {
                                label: 'Profit',
                                data: profitData,
                                backgroundColor: 'rgba(75, 192, 192, 0.6)'
                            }
                        ]
                    },
                    options: {
                        responsive: true,
                        plugins: {
                            legend: {
                                position: 'top'
                            },
                            tooltip: {
                                mode: 'index',
                                intersect: false
                            }
                        },
                        interaction: {
                            mode: 'nearest',
                            axis: 'x',
                            intersect: false
                        },
                        scales: {
                            x: {
                                stacked: false
                            },
                            y: {
                                beginAtZero: true
                            }
                        }
                    }
                });
            }

            document.addEventListener("DOMContentLoaded", function () {
                const branchId = document.getElementById('branchFilter')?.value || 0;
                const fromDate = document.getElementById('fromDate')?.value || '';
                const toDate = document.getElementById('toDate')?.value || '';

                const url = `/ParadiseHotel/hotelOwner/chartData?branchId=${branchId}&monthFrom=${monthFrom}&yearFrom=${yearFrom}&monthTo=${monthTo}&yearTo=${yearTo}`;

                fetch(url)
                        .then(res => res.json())
                        .then(json => {
                            console.log("Profit Chart:", json.profitChart);
                            console.log("Branch Chart:", json.branchChart);  // 👈 kiểm tra dữ liệu có rỗng không
                            updateProfitChart(json.profitChart.labels, json.profitChart.data);
                            renderBranchChartWrapper(json.branchId, json.branchChart);
                        })
                        .catch(err => console.error('Lỗi khi tải biểu đồ:', err));

            });

            function renderBranchChartWrapper(branchId, branchChartData) {
                const chartTitle = document.getElementById('chart-title');
                if (branchId == 0) {
                    // So sánh nhiều branch => labels là tên branch
                    updateBranchChart(branchChartData);
                    chartTitle.innerHTML = `<i class="fas fa-chart-bar"></i> Branch Comparison`;
                } else {
                    // So sánh theo tháng cho 1 branch => labels là các tháng
                    updateBranchChartHaveBranchID(branchChartData);
                    chartTitle.innerHTML = `<i class="fas fa-chart-bar"></i> Revenue vs Expenses vs Profit (Monthly)`;
                }
            }

        </script>




    </body>
</html>
