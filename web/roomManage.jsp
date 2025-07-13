<%-- 
    Document   : roomManage
    Created on : Jun 4, 2025, 12:59:05 AM
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Manager rooms</title>
        <link rel="stylesheet" href="./css/managerStyle.css">
        <link rel="stylesheet" href="./css/custom.css"/>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    </head>

    <body>
        <style>
            .modal {
                display: none;
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(0, 0, 0, 0.5);
                z-index: 1000;
                justify-content: center;
                align-items: center;
            }
            .modal.show {
                display: flex;
            }
            .modal-dialog {
                background-color: #fff;
                border-radius: 8px;
                width: 100%;
                max-width: 600px; /* Adjust to desired modal width */
                margin: 0 auto;
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
            }
            .modal-content {
                padding: 20px;
            }
            .modal-header, .modal-footer {
                display: flex;
                justify-content: space-between;
                align-items: center;
            }
            /* Định kiểu cho container pagination */
            .pagination {
                display: flex;
                justify-content: center;
                align-items: center;
                margin-top: 20px;
            }

            /* Định kiểu cho tất cả các nút trong pagination */
            .pagination button {
                margin: 0 5px; /* Khoảng cách giữa các nút */
                padding: 8px 12px; /* Đệm bên trong nút */
                border: 1px solid #007bff; /* Viền xanh */
                background-color: white; /* Nền trắng */
                color: #007bff; /* Chữ xanh */
                cursor: pointer; /* Con trỏ tay khi hover */
                transition: background-color 0.3s, color 0.3s; /* Hiệu ứng chuyển màu mượt mà */
                border-radius: 4px; /* Bo góc nút */
            }

            /* Hiệu ứng hover cho nút (trừ nút bị vô hiệu hóa) */
            .pagination button:hover:not(:disabled) {
                background-color: #007bff; /* Nền xanh khi hover */
                color: white; /* Chữ trắng khi hover */
            }

            /* Định kiểu cho nút bị vô hiệu hóa (trang hiện tại) */
            .pagination button:disabled {
                background-color: #007bff; /* Nền xanh */
                color: white; /* Chữ trắng */
                cursor: default; /* Không hiển thị con trỏ tay */
            }

            /* Loại bỏ viền ngoài khi nút được focus */
            .pagination button:focus {
                outline: none;
            }
        </style>
        <div class="app-container">
            <!-- Sidebar (giữ nguyên như index.html) -->
            <nav class="sidebar" id="sidebar">
                <!-- ... (tương tự sidebar trong index.html, chỉ thay active class cho menu-item Quản lý Phòng) -->
                <div class="sidebar-header">

                    <button class="sidebar-toggle" id="sidebarToggle">
                        <div class="brand">
                            <i class="fas fa-building"></i>
                            <span class="brand-text">${branchname}</span>
                        </div>

                    </button>
                </div>
                <div class="sidebar-menu">
                    <a href="#" class="menu-item ">
                        <i class="fas fa-chart-line"></i>
                        <span class="menu-text">Dashboard</span>
                    </a>
                    <a href="rooms" class="menu-item active">
                        <i class="fas fa-bed"></i>
                        <span class="menu-text">Manage room</span>
                    </a>
                    <a href="#" class="menu-item">
                        <i class="fas fa-comments"></i>
                        <span class="menu-text">Manage feedback</span>
                    </a>
                    <a href="serviceManage" class="menu-item">
                        <i class="fas fa-concierge-bell"></i>
                        <span class="menu-text">Manage service</span>
                    </a>
                    <a href="promotions" class="menu-item">
                        <i class="fas fa-tags"></i>
                        <span class="menu-text">Manage promotion</span>
                    </a>
                    <a href="manager-membership" class="menu-item">
                        <i class="fas fa-users"></i>
                        <span class="menu-text">Manage membership</span>
                    </a>
                    <a href="#" class="menu-item logout">
                        <i class="fas fa-sign-out-alt"></i>
                        <span class="menu-text">logout</span>
                    </a>
                </div>
            </nav>

            <!-- Main Content -->
            <main class="main-content">
                <header class="content-header">
                    <div class="header-left">
                        <h1 class="page-title">Manage room</h1>
                    </div>
                    <div class="header-right">
                        <!-- <button class="theme-toggle" id="themeToggle">
                            <i class="fas fa-moon"></i>
                        </button> -->
                        <!-- Có thể thêm một số icon như thông báo hay light or dark -->
                        <div class="user-info"> <!-- thể hiện user info -->
                            <i class="fas fa-user-circle"></i>
                            <span>${username}</span>
                        </div>
                    </div>
                </header>
                <div class="content-body">
                    <div class="rooms-container">
                        <div class="page-actions">
                            <!--Thanh search-->
                            <form action="searchRooms" method="get">
                                <div class="search-box">
                                    <i class="fas fa-search"></i>
                                    <input type="text" id="roomSearch" name="search" placeholder="Search rooms..." value="${param.search}" onchange="this.form.submit()">
                                </div>
                            </form>
                            <div class="view-toggle">
                                <button class="view-btn active" data-view="grid">
                                    <i class="fas fa-th"></i>
                                </button>
                                <button class="view-btn" data-view="table">
                                    <i class="fas fa-list"></i>
                                </button>
                            </div>

                            <input type="hidden" name="branchId" value="${branchId}">
                            <button class="btn btn-primary" onclick="openRoomModal()">
                                <i class="fas fa-plus"></i>
                                Add new room
                            </button>

                        </div>
                        <!--search bằng fillter-->
                        <form action="searchRooms" method="get">
                            <div class="filters">

                                <select id="statusFilter" name="status" onchange="this.form.submit()">
                                    <option value=""> All Status</option>
                                    <option value="Available"${param.status == 'Available' ? 'selected' : ''}>Available</option>
                                    <option value="Occupied" ${param.status == 'Occupied' ? 'selected' : ''}>Occupied</option>
                                    <option value="Booked" ${param.status == 'Booked' ? 'selected' : ''}>Booked</option>
                                    <option value="Maintenance" ${param.status == 'Maintenance' ? 'selected' : ''}>Maintenance</option>
                                </select>
                                <select id="typeFilter"name="roomType" onchange="this.form.submit()">
                                    <option value="">All Room Type</option>
                                    <c:forEach var="roomtype" items="${roomtypes}">
                                        <option value="${roomtype.roomTypeID}"${param.roomType == roomtype.roomTypeID ? 'selected': ''}>

                                            ${roomtype.name}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </form>
                        <!-- Xem lại đoạn này để code backend -->
                        <div class="rooms-grid" id="roomsGrid" style="display: none;">
                            <c:if test="${not empty rooms}">
                                <c:forEach var="room" items="${rooms}">
                                    <div class="room-card" data-status="${room.status}" data-type="${room.roomType.name}" data-room="${room.roomNumber}"
                                         data-room-id="${room.id}">
                                        <div class="room-header">
                                            <div class="room-number">${room.roomNumber}</div>
                                            <div class="room-status status-${room.status.toLowerCase()}">${room.status}</div>
                                        </div>
                                        <div class="room-info">
                                            <h4>${room.roomType.name}</h4>
                                            <div class="room-details">
                                                <div class="detail-item">
                                                    <i class="fas fa-dollar-sign"></i>
                                                    <span>${room.roomType.base_price} VND</span>
                                                </div>
                                                <div class="detail-item">
                                                    <i class="fas fa-male"></i>
                                                    <span>${room.roomType.capacity_adult} Adult</span>
                                                </div>
                                                <div class="detail-item">
                                                    <i class="fas fa-child"></i>
                                                    <span>${room.roomType.capacity_child} Child</span>
                                                </div>
                                            </div>
                                            <div class="amenities">
                                                <small>${room.roomType.description}</small>
                                            </div>
                                        </div>
                                        <div class="room-actions">
                                            <button class="btn btn-sm btn-secondary" onclick="editRoom(${room.id})">
                                                <i class="fas fa-edit"></i>
                                                Edit
                                            </button>
                                            <button class="btn btn-sm btn-danger" onclick="deleteRoom(${room.id})">
                                                <i class="fas fa-trash"></i>
                                                Delete
                                            </button>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:if>
                            <c:if test="${empty rooms}">
                                <div class="empty-state">
                                    <i class="fas fa-bed">
                                        <h3>Don't see room</h3>
                                        <p>Can you create new room</p>
                                </div>
                            </c:if>

                            <!-- Thêm các phòng mẫu khác nếu cần -->
                        </div>

                        <div class="rooms-table" id="roomsTable" ">
                            <table>
                                <thead>
                                    <tr>
                                        <th>Room number</th>
                                        <th>Room type</th>
                                        <th>Status</th>
                                        <th>Price</th>
                                        <th>Capacity adult</th>
                                        <th>Capacity child</th>
                                        <th>Image</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="room" items="${rooms}">
                                        <tr data-status="${room.status}" data-type="${room.roomType.name}" data-room="${room.roomNumber}" data-room-id="${room.id}">
                                            <!-- đoạn này sẽ lấy data từ backend -->
                                            <td><strong>${room.roomNumber}</strong></td>
                                            <td>${room.roomType.name}</td>
                                            <td><span class="status-badge status-${room.status.toLowerCase()}">${room.status}</span></td>
                                            <td>${room.roomType.base_price} VND</td>
                                            <td>${room.roomType.capacity_adult} pax</td>
                                            <td>${room.roomType.capacity_child} pax</td>
                                            <td>
                                                <c:set var="imgs" value="${roomImageMap[room.roomNumber]}" />
                                                <c:if test="${not empty imgs}">
                                                    <img src="${imgs[0]}" alt="Room Image" width="40" height="30"
                                                         style="object-fit: cover; border-radius: 4px; margin-right: 2px"/>
                                                </c:if>
                                            </td>
                                            <td>
                                                <button class="btn btn-sm btn-secondary" onclick="editRoom(this)"
                                                        data-id = "${room.id}"
                                                        data-number="${room.roomNumber}"
                                                        data-type="${room.roomType.roomTypeID}"
                                                        data-status="${room.status}"
                                                        data-capacity_adult="${room.roomType.capacity_adult}"
                                                        data-capacity_child="${room.roomType.capacity_child}"
                                                        data-price="${room.roomType.base_price}"
                                                        data-description="${room.roomType.description}"
                                                        data-images='<c:forEach var="img" items="${roomImageMap[room.roomNumber]}">${img},</c:forEach>'>
                                                            <i class="fas fa-edit"></i>
                                                        </button>
                                                        <button class="btn btn-sm btn-danger" onclick="deleteRoom(${room.id})">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <!-- Pagination -->
                        <div class="pagination">
                            <c:if test="${currentPage > 1}">
                                <form action="rooms" method="get" style="display:inline;">
                                    <input type="hidden" name="page" value="${currentPage - 1}">
                                    <input type="hidden" name="size" value="${pageSize}">
                                    <input type="hidden" name="branchId" value="${branchId}">
                                    <button type="submit">Previous</button>
                                </form>
                            </c:if>
                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <form action="rooms" method="get" style="display:inline;">
                                    <input type="hidden" name="page" value="${i}">
                                    <input type="hidden" name="size" value="${pageSize}">
                                    <input type="hidden" name="branchId" value="${branchId}">
                                    <button type="submit" ${i == currentPage ? 'disabled' : ''}>${i}</button>
                                </form>
                            </c:forEach>
                            <c:if test="${currentPage < totalPages}">
                                <form action="rooms" method="get" style="display:inline;">
                                    <input type="hidden" name="page" value="${currentPage + 1}">
                                    <input type="hidden" name="size" value="${pageSize}">
                                    <input type="hidden" name="branchId" value="${branchId}">
                                    <button type="submit">Next</button>
                                </form>
                            </c:if>
                        </div>
                    </div>
                </div>
            </main>
        </div>
        <!--modal add new room-->                        
        <div class="modal" id="roomModal">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h3 id="modalTitle" >Add new room</h3>
                        <button class="modal-close" onclick="closeModal()">×</button>
                    </div>
                    <form id="roomForm" action="rooms" method="post" enctype="multipart/form-data">

                        <div class="modal-body">
                            <div class="form-row">
                                <div class="form-group">
                                    <label for="room_number">Room number *</label>
                                    <input type="text" id="room_number" name="room_number"required>
                                </div>
                                <div class="form-group">
                                    <label for="room_type">Room type *</label>
                                    <select id="room_type" name="room_type" required>
                                        <option value="">Select room type</option>
                                        <c:forEach var="roomtype" items="${roomtypes}">
                                            <option
                                                value="${roomtype.roomTypeID}"
                                                data-capacity_adult = "${roomtype.capacity_adult}"
                                                data-capacity_child = "${roomtype.capacity_child}"
                                                data-price = "${roomtype.base_price}"
                                                data-description = "${roomtype.description}">
                                                ${roomtype.name}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                            <div class="form-row">
                                <div class="form-group">
                                    <label for="status">Status *</label>
                                    <select id="status" name="status" required>
                                        <option value="Available">Available</option>
                                        <option value="Occupied">Occupied</option>
                                        <option value="Booked">Booked</option>
                                        <option value="Maintenance">Maintenance</option>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label for="capacity_adult">Adult Capacity *</label>
                                    <input type="number" id="capacity_adult" name="capacity_adult" min="0" required>
                                </div>

                                <div class="form-group">
                                    <label for="capacity_child">Child Capacity *</label>
                                    <input type="number" id="capacity_child" name="capacity_child" min="0" required>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="price">Price (VND) *</label>
                                <input type="number" id="price" name="price" min="0" step="1000" required>
                            </div>
                            <div class="form-group">
                                <label for="image">Image *</label>
                                <input type="file" id="image-add" name="images" accept="image/*" multiple required>
                            </div>
                            <div class="form__group">
                                <label>Image Preview</label>
                                <div class="wrapper-images" id="imagePreviewWrapper-add">
                                    <div class="images">
                                    </div>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="description">Description</label>
                                <textarea id="description" name="description" rows="3"
                                          placeholder="Description..."></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" onclick="closeModal()">Cancel</button>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save"></i>
                                Save
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        <!--modal edit-->
        <div class="modal" id="editRoomModal">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h3 id="modalEditTitle">Edit room</h3>
                        <button class="modal-close" onclick="closeEditModal()">×</button>
                    </div>
                    <form id="editRoomForm" action="editRoom" method="post" enctype="multipart/form-data">
                        <!-- Hidden room id -->
                        <input type="hidden" id="edit_room_id" name="room_id">
                        <div class="modal-body">
                            <div class="form-row">
                                <div class="form-group">
                                    <label for="edit_room_number">Room number *</label>
                                    <input type="text" id="edit_room_number" name="room_number" required>
                                </div>
                                <div class="form-group">
                                    <label for="edit_room_type">Room type *</label>
                                    <select id="edit_room_type" name="room_type" required>
                                        <option value="">Select room type</option>
                                        <c:forEach var="roomtype" items="${roomtypes}">
                                            <option
                                                value="${roomtype.roomTypeID}"
                                                data-capacity_adult = "${roomtype.capacity_adult}"
                                                data-capacity_child = "${roomtype.capacity_child}"
                                                data-price = "${roomtype.base_price}"
                                                data-description = "${roomtype.description}">
                                                ${roomtype.name}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                            <div class="form-row">
                                <div class="form-group">
                                    <label for="edit_status">Status *</label>
                                    <select id="edit_status" name="status" required>
                                        <option value="Available">Available</option>
                                        <option value="Occupied">Occupied</option>
                                        <option value="Booked">Booked</option>
                                        <option value="Maintenance">Maintenance</option>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label for="edit_capacity_adult">Adult Capacity *</label>
                                    <input type="number" id="edit_capacity_adult" name="capacity_adult" min="0" require">
                                </div>

                                <div class="form-group">
                                    <label for="edit_capacity_child">Child Capacity *</label>
                                    <input type="number" id="edit_capacity_child" name="capacity_child" min="0" required">
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="edit_price">Price (VND) *</label>
                                <input type="number" id="edit_price" name="price" min="0" step="1000" required>
                            </div>
                            <div class="form-group">
                                <label for="image">Image *</label>
                                <input type="file" id="image-edit" name="images" accept="image/*" multiple required>
                            </div>
                            <div class="form__group">
                                <label>Image Preview</label>
                                <div class="wrapper-images" id="imagePreviewWrapper-edit">
                                    <div class="images">
                                    </div>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="edit_description">Description</label>
                                <textarea id="edit_description" name="description" rows="3"
                                          placeholder="Description..."></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" onclick="closeEditModal()">Cancel</button>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save"></i>
                                Update
                            </button>
                        </div>
                    </form>
                </div>
            </div>
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

        <!--    <script src="./js/managerMain.js"></script>
            <script src="./js/managerRooms.js"></script>-->
        <script>
            // function to open modal
            function openRoomModal() {
                const modal = document.getElementById('roomModal');
                if (modal) {
                    modal.style.display = 'block';
                    modal.classList.add('show');
                    modal.classList.remove('hide');
                    document.body.style.overflow = 'hidden'; // Prevent background scrolling
                }
            }
            // Function to close the modal
            function closeModal() {
                const modal = document.getElementById('roomModal');
                if (modal) {
                    modal.style.display = 'none';
                    modal.classList.remove('show');
                    modal.classList.add('hide');
                    document.body.style.overflow = ''; // Restore scrolling
                }
            }
            // Optional: Close modal when clicking outside the modal content
            document.addEventListener('click', function (event) {
                const modal = document.getElementById('roomModal');
                if (modal && event.target === modal) {
                    closeModal();
                }
            });
            // Auto-fill form fields based on room type selection
            document.getElementById('room_type').addEventListener('change', function () {
                const selectedOption = this.options[this.selectedIndex];
                const capacity_adult = selectedOption.getAttribute('data-capacity_adult') || '';
                const capacity_child = selectedOption.getAttribute('data-capacity_child') || '';
                const price = selectedOption.getAttribute('data-price') || '';
                const description = selectedOption.getAttribute('data-description') || '';

                document.getElementById('capacity_adult').value = capacity_adult;
                document.getElementById('capacity_child').value = capacity_child;
                document.getElementById('price').value = price;
                document.getElementById('description').value = description;
            });
        </script>
        // js để gửi form soft delete
        <script>
            function deleteRoom(roomId) {
                if (confirm("Are you sure you want to delete this room?")) {
                    let form = document.createElement('form');
                    form.method = 'post';
                    form.action = 'deleteRoom';

                    let inputAction = document.createElement('input');
                    inputAction.type = 'hidden';
                    inputAction.name = 'action';
                    inputAction.value = 'delete';
                    form.appendChild(inputAction);

                    let inputId = document.createElement('input');
                    inputId.type = 'hidden';
                    inputId.name = 'roomId';
                    inputId.value = roomId;
                    form.appendChild(inputId);

                    document.body.appendChild(form);
                    form.submit();
                }
            }
        </script>
        //edit
        <script src="./js/reviewImages.js"></script>
        <script>
            function editRoom(btn) {
                document.getElementById('edit_room_id').value = btn.getAttribute('data-id');
                document.getElementById('edit_room_number').value = btn.getAttribute('data-number');
                document.getElementById('edit_room_type').value = btn.getAttribute('data-type');
                document.getElementById('edit_status').value = btn.getAttribute('data-status');
                document.getElementById('edit_capacity_adult').value = btn.getAttribute('data-capacity_adult');
                document.getElementById('edit_capacity_child').value = btn.getAttribute('data-capacity_child');
                document.getElementById('edit_price').value = btn.getAttribute('data-price');
                document.getElementById('edit_description').value = btn.getAttribute('data-description');
                // Populate image preview
                const imageWrapper = document.getElementById("imagePreviewWrapper-edit");
                imageWrapper.innerHTML = ''; // Clear existing previews
                const imageUrls = btn.getAttribute('data-images') ? btn.getAttribute('data-images').split(',').filter(url => url) : [];

                imageUrls.forEach(url => {
                    const div = document.createElement("div");
                    div.className = "images";
                    const imgElement = document.createElement('img');
                    imgElement.className = "images_img";
                    imgElement.src = url;
                    imgElement.alt = "";
                    div.appendChild(imgElement);
                    imageWrapper.appendChild(div);
                });
                applyImageTransforms();
                updateImagesAndEvents();
                // Hiện modal
                const modal = document.getElementById('editRoomModal');
                modal.style.display = 'block';
                modal.classList.add('show');
                modal.classList.remove('hide');
                document.body.style.overflow = 'hidden';
            }
            function closeEditModal() {
                const modal = document.getElementById('editRoomModal');
                modal.style.display = 'none';
                modal.classList.remove('show');
                modal.classList.add('hide');
                document.body.style.overflow = '';
            }
            document.getElementById('edit_room_type').addEventListener('change', function () {
                const selectedOption = this.options[this.selectedIndex];
                const capacity_adult = selectedOption.getAttribute('data-capacity_adult') || '';
                const capacity_child = selectedOption.getAttribute('data-capacity_child') || '';
                const price = selectedOption.getAttribute('data-price') || '';
                const description = selectedOption.getAttribute('data-description') || '';

                document.getElementById('edit_capacity_adult').value = capacity_adult;
                document.getElementById('edit_capacity_child').value = capacity_child;
                document.getElementById('edit_price').value = price;
                document.getElementById('edit_description').value = description;
            });
        </script>
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
        <script>
            var errorMsg = "${error != null ? error : ''}";
            var successMsg = "${success != null ? success : ''}";
            var warningMsg = "${warning != null ? warning : ''}";
            if (errorMsg && errorMsg.trim() !== "") {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: errorMsg
                }).then(()=>{
                    document.getElementById("typeFilter").value = '';
                    document.getElementById("statusFilter").value = '';
                    document.querySelector('form[action="searchRooms"]').submit();
                });
            } else if (successMsg && successMsg.trim() !== "") {
                Swal.fire({
                    icon: 'success',
                    title: 'Success',
                    text: successMsg
                });
            } else if (warningMsg && warningMsg.trim() !== "") {
                Swal.fire({
                    icon: 'warning',
                    title: 'Warning',
                    text: warningMsg
                }).then(()=>{
                    document.getElementById("typeFilter").value = '';
                    document.getElementById("statusFilter").value = '';
                    document.querySelector('form[action="searchRooms"]').submit();
                });
            }
        </script>
        <script>
            // Form validation function
            function validateForm(formId) {
                let isValid = true;
                const form = document.getElementById(formId);
                const roomNumber = form.querySelector('[name="room_number"]');
                const roomType = form.querySelector('[name="room_type"]');
                const status = form.querySelector('[name="status"]');
                const capacity_adult = form.querySelector('[name="capacity_adult"]');
                const price = form.querySelector('[name="price"]');
                const images = form.querySelector('[name="images"]');

                // Reset error messages
                form.querySelectorAll('.error-message').forEach(el => el.style.display = 'none');

                // Validate room number
                if (!roomNumber.value.trim()) {
                    form.querySelector('#' + roomNumber.id + '_error').textContent = 'Room number cannot be empty.';
                    form.querySelector('#' + roomNumber.id + '_error').style.display = 'block';
                    isValid = false;
                }

                // Validate room type
                if (!roomType.value) {
                    form.querySelector('#' + roomType.id + '_error').textContent = 'Please select a room type.';
                    form.querySelector('#' + roomType.id + '_error').style.display = 'block';
                    isValid = false;
                }

                // Validate status
                if (!status.value || !['Available', 'Occupied', 'Booked', 'Maintenance'].includes(status.value)) {
                    form.querySelector('#' + status.id + '_error').textContent = 'Please select a valid status.';
                    form.querySelector('#' + status.id + '_error').style.display = 'block';
                    isValid = false;
                }

                // Validate capacity
                if (!capacity_adult.value || isNaN(capacity_adult.value) || parseInt(capacity_adult.value) <= 0) {
                    form.querySelector('#' + capacity_adult.id + '_error').textContent = 'Capacity adult must be a number greater than 0.';
                    form.querySelector('#' + capacity_adult.id + '_error').style.display = 'block';
                    isValid = false;
                }

                // Validate price
                if (!price.value || isNaN(price.value) || parseFloat(price.value) <= 0) {
                    form.querySelector('#' + price.id + '_error').textContent = 'Price must be a number greater than 0.';
                    form.querySelector('#' + price.id + '_error').style.display = 'block';
                    isValid = false;
                }

                // Validate images (only required for add form, optional for edit)
                if (formId === 'roomForm' && (!images.files || images.files.length === 0)) {
                    form.querySelector('#' + images.id + '_error').textContent = 'At least one image is required.';
                    form.querySelector('#' + images.id + '_error').style.display = 'block';
                    isValid = false;
                }

                return isValid;
            }

            // Attach validation to form submission
            document.getElementById('roomForm').addEventListener('submit', function (e) {
                if (!validateForm('roomForm')) {
                    e.preventDefault();
                }
            });

            document.getElementById('editRoomForm').addEventListener('submit', function (e) {
                if (!validateForm('editRoomForm')) {
                    e.preventDefault();
                }
            });
        </script>

    </body>

</html>
