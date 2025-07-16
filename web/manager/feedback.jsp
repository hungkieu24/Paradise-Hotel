<%-- 
    Document   : feedback
    Created on : Jul 10, 2025, 11:44:12 PM
    Author     : hungk
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Manager Feedback</title>
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
                    <a href="./roomType" class="menu-item ">
                        <i class="fas fa-bed"></i>
                        <span class="menu-text">Manage room type</span>
                    </a>
                    <a href="./revenue" class="menu-item ">
                        <i class="fa-solid fa-dollar-sign"></i>
                        <span class="menu-text">Manage Revenue & Expense</span>
                    </a>
                    <a href="./feedback" class="menu-item active">
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
                        <h1 class="page-title">Manage Feedback</h1>
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
                        <div class="page-actions" style="justify-content: unset">
                            <form action="">
                                <input type="hidden" name="action" value="search">
                                <div class="search-box">
                                    <i class="fas fa-search"></i>
                                    <input type="text" name="searchKeyword" id="roomSearch" value="${param.searchKeyword}" placeholder="Search feeback..." >
                                </div>
                            </form>
                            <form id="ratingFilterForm">
                                <input type="hidden" name="action" value="filter">
                                <select id="typeFilter" name="rating" onchange="document.getElementById('ratingFilterForm').submit()">
                                    <option value="0" ${param.rating == 0 ? 'selected' : ''}>All Rating</option>
                                    <c:forEach var="i" begin="1" end="5">
                                        <option value="${i}" ${i == param.rating ? 'selected' : ''}>
                                            ${i} ★
                                        </option>
                                    </c:forEach>
                                </select>
                            </form>
                        </div>

                        <div class="rooms-table" id="roomsTable">
                            <p class="cart-info__desc profile__desc">Quantity: <strong>${feedbackListSize}</strong></p>
                            <table>
                                <thead>
                                    <tr>
                                        <th>User</th>
                                        <th>Room</th>
                                        <th>Rating</th>
                                        <th>Comment</th>
                                        <th>Created At</th>
                                        <th>Status</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${feedbackList}" var="f" >
                                        <tr>
                                            <td>
                                                <div class="user-wrapper">
                                                    <img src="../img/avatar/avatar.jpg" class="user-avatar" >
                                                    ${f.getUserAccount().getUsername()}
                                                </div>
                                            </td>
                                            <td>
                                                ${f.getRoomNumber()} <br>
                                                ${f.getRoomTypeName()}
                                            </td>
                                            <td>
                                                <div class="star-wrapper">
                                                    <!-- Hiển thị sao đầy đủ -->
                                                    <c:forEach var="i" begin="1" end="${f.getRating()}">
                                                        <img src="../img/svg_icons/star.svg" alt="" class="review-card__star" />
                                                    </c:forEach>

                                                    <!-- Hiển thị sao trống (nếu có) -->
                                                    <c:forEach var="j" begin="${f.getRating() + 1}" end="5">
                                                        <img src="../img/svg_icons/star-blank.svg" alt="" class="review-card__star" />
                                                    </c:forEach>
                                                </div>
                                            </td>
                                            <td>
                                                <div class="text-truncate">
                                                    ${f.getComment()}
                                                </div>
                                            </td>
                                            <td>${f.getCreated_at()}</td>
                                            <td >
                                                <span class="status-badge status-${f.getStatus() == "Visible" ? "visible" : "blocked"}">
                                                    ${f.getStatus()}
                                                </span> 
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${f.getStatus() == 'Hidden'}">
                                                        <button class="btn btn-sm restore js-toggle" 
                                                                toggle-target="#restore-modal" 
                                                                data-actor-id="${f.getId()}"
                                                                style="background: #20b28a; color: #fff">
                                                            <i class="fa-solid fa-rotate-right"></i>
                                                        </button>
                                                        <button class="btn btn-sm btn-ban ban js-toggle" 
                                                                toggle-target="#ban-modal" 
                                                                data-actor-id="${f.getId()}">
                                                            <i class="fa-solid fa-ban"></i>
                                                        </button>
                                                        <button class="btn btn-sm btn-danger delete js-toggle" 
                                                                toggle-target="#delete-modal" 
                                                                data-actor-id="${f.getId()}">
                                                            <i class="fas fa-trash"></i>
                                                        </button>
                                                    </c:when>
                                                    <c:when test="${f.getStatus() == 'Blocked'}">
                                                        <button class="btn btn-sm restore js-toggle" 
                                                                toggle-target="#restore-modal" 
                                                                data-actor-id="${f.getId()}"
                                                                style="background: #20b28a; color: #fff">
                                                            <i class="fa-solid fa-rotate-right"></i>
                                                        </button>
                                                        <button class="btn btn-sm btn-danger delete js-toggle" 
                                                                toggle-target="#delete-modal" 
                                                                data-actor-id="${f.getId()}">
                                                            <i class="fas fa-trash"></i>
                                                        </button>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <button class="btn btn-sm btn-primary feedback js-toggle" 
                                                                toggle-target="#feedback-modal" 
                                                                data-actor-id="${f.getId()}">
                                                            <i class="fa-solid fa-comment"></i>
                                                        </button>
                                                        <button class="btn btn-sm btn-ban ban js-toggle" 
                                                                toggle-target="#ban-modal" 
                                                                data-actor-id="${f.getId()}">
                                                            <i class="fa-solid fa-ban"></i>
                                                        </button>
                                                        <button class="btn btn-sm btn-warning warning js-toggle" 
                                                                toggle-target="#warning-modal" 
                                                                data-actor-id="${f.getId()}">
                                                            <i class="fa-solid fa-triangle-exclamation"></i>
                                                        </button>
                                                        <button class="btn btn-sm btn-danger delete js-toggle" 
                                                                toggle-target="#delete-modal" 
                                                                data-actor-id="${f.getId()}">
                                                            <i class="fas fa-trash"></i>
                                                        </button>
                                                    </c:otherwise>
                                                </c:choose>

                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <div class="pagination">
                        <c:set var="queryParams" value="" />
                        <c:if test="${not empty action and not empty keyword}">
                            <c:set var="queryParams" value="&action=${action}&searchKeyword=${keyword}" />
                        </c:if>
                        <c:if test="${not empty action and not empty rating}">
                            <c:set var="queryParams" value="&action=${action}&rating=${rating}" />
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

        <!-- Feedback Modal -->
        <div id="feedback-modal" class="modal hide">
            <div class="modal-content">
                <div class="modal-header">
                    <h3 id="modal-title">Feedback Details</h3>
                    <button class="modal-close js-toggle" toggle-target="#feedback-modal">×</button>
                </div>
                <div class="modal-body">
                    <div id="feedback-details">
                        <div class="detail-row">
                            <span class="detail-label">Customer:</span>
                            <span class="detail-value" id="value-name">Sarah Ahmed (SA001)</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Booking ID:</span>
                            <span class="detail-value" id="value-bookingId">12345</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Rating:</span>
                            <span class="detail-value">
                                <span class="rating-stars" id="value-rating-stars">★★★★★</span>
                            </span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Date:</span>
                            <span class="detail-value" id="value-date">Jan 15, 2024, 05:30 PM</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Status:</span>
                            <span class="detail-value">
                                <span class="status-badge status-visible" id="value-status">
                                    Visible
                                </span>
                            </span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Message:</span>
                            <span class="detail-value" id="value-feeback">Excellent service! The team was professional and delivered exactly what we needed. Highly recommend!</span>
                        </div>
                    </div>
                    <div class="comments-section">
                        <h4>Comments &amp; Replies</h4>
                        <div id="comments-tree">
                            <div class="comment-item" style="margin-left: 0px">
                                <div class="comment-header">
                                    <div class="comment-author">
                                        <div class="comment-avatar" style="background: #059669">M</div>
                                        <div class="comment-name">Manager</div>
                                        <span style="font-size: 0.75rem; color: #059669; font-weight: 500;">Manager</span>
                                    </div>
                                    <div class="comment-date">Jan 15, 2024, 06:00 PM</div>
                                </div>
                                <div class="comment-body">
                                    <div class="comment-content ">
                                        Thank you so much for the wonderful feedback! We really appreciate customers like you.
                                    </div>

                                    <div class="comment-actions">
                                        <button class="btn btn-secondary btn-small" >
                                            Reply
                                        </button>
                                        <button class="btn btn-secondary btn-small" >
                                            Delete
                                        </button>
                                    </div>
                                    <div id="reply-form-1" class="reply-form hidden">
                                        <textarea placeholder="Write your reply..." id="reply-text-1"></textarea>
                                        <div class="reply-form-actions">
                                            <button class="btn btn-primary" >
                                                Post Reply
                                            </button>
                                            <button class="btn btn-secondary" >
                                                Cancel
                                            </button>
                                        </div>
                                    </div>

                                </div>
                            </div>

                            <div class="comment-item" style="margin-left: 20px">
                                <div class="comment-header">
                                    <div class="comment-author">
                                        <div class="comment-avatar" style="background: #3b82f6">SA</div>
                                        <div class="comment-name">Sarah Ahmed</div>

                                    </div>
                                    <div class="comment-date">Jan 15, 2024, 06:30 PM</div>
                                </div>
                                <div class="comment-body">
                                    <div class="comment-content ">
                                        You're welcome! Keep up the great work.
                                    </div>

                                    <div class="comment-actions">
                                        <button class="btn btn-secondary btn-small" >
                                            Reply
                                        </button>
                                        <button class="btn btn-secondary btn-small" >
                                            Delete
                                        </button>
                                    </div>
                                    <div id="reply-form-2" class="reply-form hidden">
                                        <textarea placeholder="Write your reply..." id="reply-text-2"></textarea>
                                        <div class="reply-form-actions">
                                            <button class="btn btn-primary" >
                                                Post Reply
                                            </button>
                                            <button class="btn btn-secondary">
                                                Cancel
                                            </button>
                                        </div>
                                    </div>

                                </div>
                            </div>

                            <div class="reply-form mt-4">
                                <textarea placeholder="Write your response..." id="new-comment-text"></textarea>
                                <div class="reply-form-actions">
                                    <button class="btn btn-primary">
                                        Post Response
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal__overlay js-toggle" toggle-target="#feedback-modal"></div>
        </div>

        <!-- Modal delete -->
        <div id="delete-modal" class="modal hide">
            <div class="modal__content">
                <div class="modal__text">Do you want to delete this? This action cannot be undone </div>
                <div class="modal__bottom">
                    <button class="btn btn--small btn-primary btn--text modal__btn btn--no-margin js-toggle"
                            toggle-target="#delete-modal">
                        Cancel
                    </button>
                    <form action="feedback" method="post">
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

        <!-- Modal Ban -->
        <div id="ban-modal" class="modal hide">
            <div class="modal__content">
                <div class="modal__text">Do you want to ban this feedback?</div>
                <div class="modal__bottom">
                    <button class="btn btn--small btn-primary btn--text modal__btn btn--no-margin js-toggle"
                            toggle-target="#ban-modal">
                        Cancel
                    </button>
                    <form action="feedback" method="post">
                        <input type="hidden" name="IdBan" id="IdBan" value="">
                        <input type="hidden" name="action" value="ban">
                        <button type="submit" class="btn btn--small btn-danger btn--primary modal__btn btn--no-margin" >
                            Ban
                        </button>
                    </form>
                </div>
            </div>
            <div class="modal__overlay js-toggle" toggle-target="#ban-modal"></div>
        </div>

        <!-- Modal Warning -->
        <div id="warning-modal" class="modal hide">
            <div class="modal__content">
                <div class="modal__text">This action will hide this feedback.</div>
                <div class="modal__bottom">
                    <button class="btn btn--small btn-primary btn--text modal__btn btn--no-margin js-toggle"
                            toggle-target="#warning-modal">
                        Cancel
                    </button>
                    <form action="feedback" method="post">
                        <input type="hidden" name="IdWarning" id="IdWarning" value="">
                        <input type="hidden" name="action" value="warning">
                        <button type="submit" class="btn btn--small btn-danger btn--primary modal__btn btn--no-margin" >
                            Warning
                        </button>
                    </form>
                </div>
            </div>
            <div class="modal__overlay js-toggle" toggle-target="#warning-modal"></div>
        </div>

        <!-- Modal restore -->
        <div id="restore-modal" class="modal hide">
            <div class="modal__content">
                <div class="modal__text">Do you want to restore this? </div>
                <div class="modal__bottom">
                    <button class="btn btn--small btn-primary btn--text modal__btn btn--no-margin js-toggle"
                            toggle-target="#restore-modal">
                        Cancel
                    </button>
                    <form action="feedback" method="post">
                        <input type="hidden" name="Idrestore" id="Idrestore" value="">
                        <input type="hidden" name="action" value="restore">
                        <button type="submit" class="btn btn--small btn-danger btn--primary modal__btn btn--no-margin" >
                            Restore
                        </button>
                    </form>
                </div>
            </div>
            <div class="modal__overlay js-toggle" toggle-target="#restore-modal"></div>
        </div>


        <!-- Scripts -->
        <script src="../js/Admin.js"></script>
        <script src="../js/themeAdmin.js"></script>
        <script src="../js/hungkd.js"></script>
        <script src="../js/api.js"></script>
        <script src="../js/validationForm.js"></script>
        <script src="../js/feedbackManageJs.js">
        </script>

        <script>
            function fillModalDelete(id) {
                document.getElementById("IdDelete").value = id;
            }
            function fillModalBan(id) {
                document.getElementById("IdBan").value = id;
            }
            function fillModalWarning(id) {
                document.getElementById("IdWarning").value = id;
            }
            function fillModalRestore(id) {
                document.getElementById("Idrestore").value = id;
            }
            initButtons("delete.js-toggle", "data-actor-id", fillModalDelete);
            initButtons("ban.js-toggle", "data-actor-id", fillModalBan);
            initButtons("warning.js-toggle", "data-actor-id", fillModalWarning);
            initButtons("restore.js-toggle", "data-actor-id", fillModalRestore);
        </script>
    </body>
</html>


