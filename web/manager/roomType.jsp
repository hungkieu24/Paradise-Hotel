<%-- 
    Document   : roomType
    Created on : Jul 10, 2025, 8:37:42 AM
    Author     : hungk
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Manager Room Type</title>
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
                    <a href="./roomType" class="menu-item active">
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
                        <h1 class="page-title">Manage Room Type</h1>
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
                                <div class="tab-button active">Room Type</div>
                            </a>
                            <a href="./amenity" class="tab-button__link">
                                <div class="tab-button">Amenity</div>
                            </a>
                        </div>
                        <div class="page-actions">
                            <form action="">
                                <input type="hidden" name="action" value="search">
                                <div class="search-box">
                                    <i class="fas fa-search"></i>
                                    <input type="text" name="searchKeyword" id="roomSearch" value="${param.searchKeyword}" placeholder="Search room type..." >
                                </div>
                            </form>
                            <button id="add-branch-btn" class="btn btn-primary js-toggle" toggle-target="#add-modal">
                                <i class="fas fa-plus"></i>
                                Add new room type
                            </button>
                        </div>

                        <div class="rooms-table" id="roomsTable">
                            <p class="cart-info__desc profile__desc">Quantity: <strong>${brancheListSize}</strong></p>
                            <table>
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Name</th>
                                        <th>Base Price</th>
                                        <th>Description</th>
                                        <th>Capacity Adult</th>
                                        <th>Capacity Child</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${roomTypeList}" var="r" >
                                        <tr>
                                            <td>${r.getRoomTypeID()}</td>
                                            <td>${r.getName()}</td>
                                            <td>${r.getBase_price()} VND</td>
                                            <td>${r.getDescription()}</td>
                                            <td>${r.getCapacity_adult()} pax</td>
                                            <td>${r.getCapacity_child()} pax</td>
                                            <td>
                                                <button class="btn btn-sm btn-secondary edit js-toggle" 
                                                        toggle-target="#edit-modal" 
                                                        data-actor-id="${r.getRoomTypeID()}">
                                                    <i class="fas fa-edit"></i>
                                                </button>
                                                <button class="btn btn-sm btn-danger delete js-toggle" 
                                                        toggle-target="#delete-modal" 
                                                        data-actor-id="${r.getRoomTypeID()}">
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
            <div class="modal__content">
                <div class="modal__heading">Edit Room Type</div>
                <form action="roomTypeEventHandler" method="post" enctype="multipart/form-data" id="edit-form" class="form form-card">
                    <input type="hidden" name="roomTypeID" id="roomTypeID">
                    <input type="hidden" name="branchID" value="${branch.id}">
                    <input type="hidden" name="action" value="edit">
                    <!-- Form row 1 -->
                    <div class="form__row">
                        <div class="form__group">
                            <label for="roomTypeName" class="form__label form-card__label">Room Type Name</label>
                            <div class="form__text-input">
                                <input type="text" name="roomTypeName" id="roomTypeName" class="form__input" placeholder="Room Type Name"/>
                            </div>
                            <p class="form__error"></p>
                        </div>
                        <div class="form__group">
                            <label for="basePrice" class="form__label form-card__label">Base Price</label>
                            <div class="form__text-input">
                                <input type="text" name="basePrice" id="basePrice" placeholder="Base Price" class="form__input"/>
                            </div>
                            <p class="form__error"></p>
                        </div>
                    </div>

                    <!-- Form row 2 -->
                    <div class="form__row">
                        <div class="form__group">
                            <label for="capacityAdult" class="form__label form-card__label">Capacity Adult</label>
                            <div class="form__text-input">
                                <input type="text" name="capacityAdult" id="capacityAdult" class="form__input" placeholder="Capacity Adult"/>
                            </div>
                            <p class="form__error"></p>
                        </div>
                        <div class="form__group">
                            <label for="capacityChild" class="form__label form-card__label">Capacity Child</label>
                            <div class="form__text-input">
                                <input type="text" name="capacityChild" id="capacityChild" placeholder="Capacity Child" class="form__input"/>
                            </div>
                            <p class="form__error"></p>
                        </div>
                    </div>


                    <!-- Form row 3 -->
                    <div class="form__row">
                        <div class="form__group">
                            <label class="form__label form-card__label">Image</label>
                            <div class="wrapper-images" id="imagePreviewWrapper">
                                <div class="images">
                                </div>
                            </div>

                            <p class="form__error"></p>
                        </div>
                        <div class="form__group">
                            <label for="imageInput" class="form__label form-card__label">Add Image</label>
                            <div class="form__text-input">
                                <input type="file" name="roomTypeImgs" id="imageInput" multiple accept="image/*">
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

                    <!-- Form row 5 - Amenity -->
                    <div class="form__row">
                        <div class="form__group" style="width: 100%;">
                            <label class="form__label form-card__label">Amenities</label>
                            <div id="amenityCheckboxWrapper" class=" amenity-checkboxes">
                                <!-- Checkboxes sẽ được JS render vào đây -->
                            </div>
                            <p class="form__error"></p>
                        </div>
                    </div>


                    <div class="form-card__bottom">
                        <a href="./roomType" class="btn btn--text">
                            <div class=" btn--rounded btn-normal">Cancel</div>
                        </a>
                        <button type="submit" class="btn btn-primary btn--rounded">Save</button>
                    </div>
                </form>
            </div>
            <div class="modal__overlay js-toggle" toggle-target="#edit-modal"></div>
        </div>

        <!-- Galley for image -->
        <div class="gallery">
            <i class="close">X</i>
            <div class="gallery_inner">
                <img src="" alt="">
            </div>
            <div class="control_prev"> <= </div>
            <div class="control_after"> => </div>
        </div>

        <!-- Modal: Add Room Type -->
        <div id="add-modal" class="modal hide">
            <div class="modal__content">
                <div class="modal__heading">Add Room Type</div>
                <form action="roomTypeEventHandler" method="post" enctype="multipart/form-data" id="add-form" class="form form-card">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="branchID" value="${branch.id}">
                    <!-- Form row 1 -->
                    <div class="form__row">
                        <div class="form__group">
                            <label for="roomTypeName-add" class="form__label form-card__label">Room Type Name</label>
                            <div class="form__text-input">
                                <input type="text" name="roomTypeName" id="roomTypeName-add" class="form__input" placeholder="Room Type Name"/>
                            </div>
                            <p class="form__error"></p>
                        </div>
                        <div class="form__group">
                            <label for="basePrice-add" class="form__label form-card__label">Base Price</label>
                            <div class="form__text-input">
                                <input type="text" name="basePrice" id="basePrice-add" placeholder="Base Price" class="form__input"/>
                            </div>
                            <p class="form__error"></p>
                        </div>
                    </div>

                    <!-- Form row 2 -->
                    <div class="form__row">
                        <div class="form__group">
                            <label for="capacityAdult-add" class="form__label form-card__label">Capacity Adult</label>
                            <div class="form__text-input">
                                <input type="text" name="capacityAdult" id="capacityAdult-add" class="form__input" placeholder="Capacity Adult"/>
                            </div>
                            <p class="form__error"></p>
                        </div>
                        <div class="form__group">
                            <label for="capacityChild-add" class="form__label form-card__label">Capacity Child</label>
                            <div class="form__text-input">
                                <input type="text" name="capacityChild" id="capacityChild-add" placeholder="Capacity Child" class="form__input"/>
                            </div>
                            <p class="form__error"></p>
                        </div>
                    </div>

                    <!-- Form row 3 -->
                    <div class="form__row">
                        <div class="form__group">
                            <label class="form__label form-card__label">Image</label>
                            <div class="wrapper-images" id="imagePreviewWrapper-add">
                                <div class="images">
                                    <!-- Image preview will go here -->
                                </div>
                            </div>
                            <p class="form__error"></p>
                        </div>
                        <div class="form__group">
                            <label for="imageInput-add" class="form__label form-card__label">Add Image</label>
                            <div class="form__text-input">
                                <input type="file" name="roomTypeImgs" id="imageInput-add" multiple accept="image/*">
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

                    <!-- Form row 5 - Amenity -->
                    <div class="form__row">
                        <div class="form__group" style="width: 100%;">
                            <label class="form__label form-card__label">Amenities</label>
                            <div id="amenityCheckboxWrapper" class=" amenity-checkboxes">
                                <c:forEach items="${allAmenities}" var="a" >
                                    <label class="checkbox-label">
                                        <input type="checkbox" name="amenityIds" value="${a.getId()}">
                                        <span>${a.getName()}</span>
                                    </label>
                                </c:forEach>
                            </div>
                            <p class="form__error"></p>
                        </div>
                    </div>

                    <div class="form-card__bottom">
                        <a href="./roomType" class="btn btn--text">
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
                    <form action="roomTypeEventHandler" method="post" enctype="multipart/form-data">
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
        <script src="../js/imageGallery.js"></script>

        <!--Js điền dữ liệu vào Edit admin modal -->
        <script>
            function fillModalEdit(roomTypeID) {
                fetch("/ParadiseHotel/manager/roomTypeEventHandler?roomTypeID=" + roomTypeID)
                        .then(res => res.json())
                        .then(data => {
                            const actor = data.roomType;
                            const images = data.images;

                            if (!actor)
                                return;

                            document.getElementById("roomTypeID").value = actor.roomTypeID;
                            document.getElementById("roomTypeName").value = actor.name;
                            document.getElementById("basePrice").value = actor.base_price;
                            document.getElementById("capacityAdult").value = actor.capacity_adult;
                            document.getElementById("capacityChild").value = actor.capacity_child;
                            document.getElementById("description").value = actor.description;
                            renderAmenityCheckboxes(data.allAmenities, data.selectedAmenityIds);
                            // Hiển thị danh sách ảnh
                            const wrapper = document.getElementById("imagePreviewWrapper");
                            wrapper.innerHTML = ""; // Xóa ảnh cũ
                            images.forEach(path => {
                                const div = document.createElement("div");
                                div.className = "images";

                                const img = document.createElement("img");
                                img.className = "images_img";
                                img.src = path;
                                img.alt = "";

                                div.appendChild(img);
                                wrapper.appendChild(div);
                            });
                            applyImageTransforms();
                            updateImagesAndEvents();


                        })
                        .catch(err => console.error("Lỗi fetch:", err));
            }

            function fillModalDelete(roomTypeID) {
                fetch("/ParadiseHotel/manager/roomTypeEventHandler?roomTypeID=" + roomTypeID)
                        .then(res => res.json())
                        .then(data => {
                            const actor = data.roomType;
                            const images = data.images;

                            if (!actor)
                                return;

                            document.getElementById("IdDelete").value = actor.roomTypeID;
                        })
                        .catch(err => console.error("Lỗi fetch:", err));
            }
            initButtons("edit.js-toggle", "data-actor-id", fillModalEdit);
            initButtons("delete.js-toggle", "data-actor-id", fillModalDelete);

            function renderAmenityCheckboxes(allAmenities, selectedIds) {
                const wrapper = document.getElementById("amenityCheckboxWrapper");
                wrapper.innerHTML = ""; // Xóa nếu có sẵn

                allAmenities.forEach(amenity => {
                    const label = document.createElement("label");
                    label.className = "checkbox-label";

                    const checkbox = document.createElement("input");
                    checkbox.type = "checkbox";
                    checkbox.name = "amenityIds";
                    checkbox.value = amenity.id;
                    if (selectedIds.includes(amenity.id)) {
                        checkbox.checked = true;
                    }

                    const span = document.createElement("span");
                    span.textContent = amenity.name;

                    label.appendChild(checkbox);
                    label.appendChild(span);
                    wrapper.appendChild(label);
                });
            }

        </script>

        <script>
            Validator({
                form: '#add-form',
                formGroupSelector: '.form__group',
                errorSelector: '.form__error',
                rules: [
                    Validator.isRequired('#roomTypeName-add', 'Please enter room type name'),
                    Validator.isRequired('#basePrice-add', 'Please enter base price'),
                    Validator.isNumber('#basePrice-add'),
                    Validator.isRequired('#capacityAdult-add', 'Please enter capacity adult'),
                    Validator.isNumber('#capacityAdult-add'),
                    Validator.isRequired('#capacityChild-add', 'Please enter capacity child'),
                    Validator.isNumber('#capacityChild-add'),
                    Validator.isRequired('#description-add', 'Please enter description'),
                    Validator.isRequiredFile('#imageInput-add', 'Please select at least one file.'),
                    Validator.isImageFile('#imageInput-add', 'File must be an image (.jpg, .png, .gif, .webp)'),
                    Validator.maxFileCount('#imageInput-add', 5),
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
                    Validator.isRequired('#roomTypeName', 'Please enter room type name'),
                    Validator.isRequired('#basePrice', 'Please enter base price'),
                    Validator.isNumber('#basePrice'),
                    Validator.isRequired('#capacityAdult', 'Please enter capacity adult'),
                    Validator.isNumber('#capacityAdult'),
                    Validator.isRequired('#capacityChild', 'Please enter capacity child'),
                    Validator.isNumber('#capacityChild'),
                    Validator.isRequired('#description', 'Please enter description'),
                    Validator.isImageFile('#imageInput', 'File must be an image (.jpg, .png, .gif, .webp)'),
                    Validator.maxFileCount('#imageInput', 5),
                ],
                onsubmit: function (formValue) {
                    document.querySelector('#edit-form').submit();
                }
            })
        </script>
    </body>
</html>
