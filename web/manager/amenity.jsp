<%-- 
    Document   : amenity
    Created on : Jul 10, 2025, 8:37:49 PM
    Author     : hungk
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Manager Amenity</title>
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
                    <a href="#" class="menu-item ">
                        <i class="fas fa-chart-line"></i>
                        <span class="menu-text">Dashboard</span>
                    </a>
                    <a href="../rooms" class="menu-item">
                        <i class="fas fa-bed"></i>
                        <span class="menu-text">Manage room</span>
                    </a>
                    <a href="roomType" class="menu-item active">
                        <i class="fas fa-bed"></i>
                        <span class="menu-text">Manage room type</span>
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
                    <a href="#" class="menu-item">
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
                        <h1 class="page-title">Manage Branch</h1>
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
                            <a href="./roomType" class="tab-button__link">
                                <div class="tab-button ">Room Type</div>
                            </a>
                            <a href="./amenity" class="tab-button__link">
                                <div class="tab-button active">Amenity</div>
                            </a>
                        </div>
                        <div class="page-actions">
                            <form action="">
                                <input type="hidden" name="action" value="search">
                                <div class="search-box">
                                    <i class="fas fa-search"></i>
                                    <input type="text" name="searchKeyword" id="roomSearch" value="${param.searchKeyword}" placeholder="Search branch..." >
                                </div>
                            </form>
                            <button id="add-branch-btn" class="btn btn-primary js-toggle" toggle-target="#add-modal">
                                <i class="fas fa-plus"></i>
                                Add new amenity
                            </button>
                        </div>

                        <div class="rooms-table" id="roomsTable">
                            <p class="cart-info__desc profile__desc">Quantity: <strong>${amenitySize}</strong></p>
                            <table>
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Name</th>
                                        <th>Description</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${allAmenities}" var="a" >
                                        <tr>
                                            <td>${a.getId()}</td>
                                            <td>${a.getName()}</td>
                                            <td>${a.getDescription()}</td>
                                            <td>
                                                <button class="btn btn-sm btn-secondary edit js-toggle" 
                                                        toggle-target="#edit-modal" 
                                                        data-actor-id="${a.getId()}">
                                                    <i class="fas fa-edit"></i>
                                                </button>
                                                <button class="btn btn-sm btn-danger delete js-toggle" 
                                                        toggle-target="#delete-modal" 
                                                        data-actor-id="${a.getId()}">
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

        <!--Js điền dữ liệu vào Edit admin modal -->
        <script>
            function fillModalEdit(amenityID) {
                fetch("/ParadiseHotel/manager/amenityEventHandler?amenityID=" + amenityID)
                        .then(res => res.json())
                        .then(data => {
                            if (!data) return;
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
                            if (!data) return;
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

