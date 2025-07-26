<%-- 
    Document   : serviceManage
    Created on : Jun 24, 2025, 9:40:54 AM
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Services</title>
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
            max-width: 600px;
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
        .pagination {
            display: flex;
            justify-content: center;
            align-items: center;
            margin-top: 20px;
        }
        .pagination button {
            margin: 0 5px;
            padding: 8px 12px;
            border: 1px solid #007bff;
            background-color: white;
            color: #007bff;
            cursor: pointer;
            transition: background-color 0.3s, color 0.3s;
            border-radius: 4px;
        }
        .pagination button:hover:not(:disabled) {
            background-color: #007bff;
            color: white;
        }
        .pagination button:disabled {
            background-color: #007bff;
            color: white;
            cursor: default;
        }
        .pagination button:focus {
            outline: none;
        }
        .services-table table {
            width: 100%;
            border-collapse: collapse;
        }

        .services-table th, .services-table td {
            padding: 0.75rem;
            text-align: left;
            border-bottom: 1px solid hsl(var(--border));
        }

        .services-table th {
            background-color: hsl(var(--muted));
            font-weight: 600;
        }

        .services-table .service-actions {
            display: flex;
            gap: 0.5rem;
            justify-content: center;
        }

        .services-table .status-badge {
            display: inline-block;
            padding: 0.25rem 0.5rem;
            border-radius: 9999px;
            font-size: 0.75rem;
            font-weight: 500;
        }
        .image-preview {
            max-width: 100px;
            max-height: 100px;
            object-fit: cover;
            margin: 5px;
        }
    </style>
    <div class="app-container">
        <!-- Sidebar -->
        <nav class="sidebar" id="sidebar">
            <div class="sidebar-header">
                <button class="sidebar-toggle" id="sidebarToggle">
                    <div class="brand">
                        <i class="fas fa-building"></i>
                        <span class="brand-text">${branchname}</span>
                    </div>
                </button>
            </div>
            <div class="sidebar-menu">
                <a href="./manager/dashboard" class="menu-item ">
                    <i class="fas fa-chart-line"></i>
                    <span class="menu-text">Dashboard</span>
                </a>
                <a href="rooms" class="menu-item ">
                    <i class="fas fa-bed"></i>
                    <span class="menu-text">Manage room</span>
                </a>
                <a href="./manager/roomType" class="menu-item">
                    <i class="fas fa-bed"></i>
                    <span class="menu-text">Manage room type</span>
                </a>
                <a href="./manager/revenue" class="menu-item">
                    <i class="fa-solid fa-dollar-sign"></i>
                    <span class="menu-text">Manage Revenue & Expense</span>
                </a>
                <a href="./manager/feedback" class="menu-item">
                    <i class="fas fa-comments"></i>
                    <span class="menu-text">Manage feedback</span>
                </a>
                <a href="serviceManage" class="menu-item active">
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
                <a href="login?action=logout" class="menu-item logout">
                    <i class="fas fa-sign-out-alt"></i>
                    <span class="menu-text">logout</span>
                </a>
            </div>
        </nav>

        <!-- Main Content -->
        <main class="main-content">
            <header class="content-header">
                <div class="header-left">
                    <h1 class="page-title">Manage Services</h1>
                </div>
                <div class="header-right">
                    <div class="user-info">
                        <i class="fas fa-user-circle"></i>
                        <span>${username}</span>
                    </div>
                </div>
            </header>
            <div class="content-body">
                <div class="services-container">
                    <div class="page-actions">
                        <!--                        thanh search-->
                        <form action="serviceSearch" method="get">
                            <div class="search-box">
                                <i class="fas fa-search"></i>
                                <input type="text" id="serviceSearch" name="search" placeholder="Search services..." value="${param.search}" onchange="this.form.submit()">
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
                        <button class="btn btn-primary" onclick="openServiceModal()">
                            <i class="fas fa-plus"></i>
                            Add new service
                        </button>
                    </div>
                    <form action="serviceSearch" method="get">
                        <div class="filters">
                            <select id="statusFilter" name="status" onchange="this.form.submit()">
                                <option value="">All Status</option>
                                <option value="Active" ${param.status == 'Active' ? 'selected' : ''}>Active</option>
                                <option value="Inactive" ${param.status == 'Inactive' ? 'selected' : ''}>Inactive</option>
                            </select>
                        </div>
                    </form>
                    <div class="services-grid" id="servicesGrid" style="display: none;">
                        <c:if test="${not empty services}">
                            <c:forEach var="service" items="${services}">
                                <div class="service-card" data-status="${service.status}" data-service="${service.name}" data-service-id="${service.id}">
                                    <div class="service-header">
                                        <div class="service-name">${service.name}</div>
                                        <div class="service-status status-${service.status.toLowerCase()}">${service.status}</div>
                                    </div>
                                    <div class="service-info">
                                        <div class="service-details">
                                            <div class="detail-item">
                                                <i class="fas fa-dollar-sign"></i>
                                                <span><fmt:formatNumber value="${service.price}" type="currency" currencyCode="VND" maxFractionDigits="0"/></span>
                                            </div>
                                        </div>
                                        <div class="description">
                                            <small>${service.description}</small>
                                        </div>
                                        <!--                                        <div>
                                                                                   image
                                                                                </div>-->
                                    </div>
                                    <div class="service-actions">
                                        <button class="btn btn-sm btn-secondary" onclick="editService(${service.id})">
                                            <i class="fas fa-edit"></i>
                                            Edit
                                        </button>
                                        <button class="btn btn-sm btn-danger" onclick="deleteService(${service.id})">
                                            <i class="fas fa-trash"></i>
                                            Delete
                                        </button>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:if>
                        <c:if test="${empty services}">
                            <div class="empty-state">
                                <i class="fas fa-concierge-bell"></i>
                                <h3>No services found</h3>
                                <p>Create a new service to get started</p>
                            </div>
                        </c:if>
                    </div>
                    <div class="services-table" id="servicesTable"">
                        <table>
                            <thead>
                                <tr>
                                    <th>Service Name</th>
                                    <th>Status</th>
                                    <th>Price</th>
                                    <th>Description</th>
                                    <!-- image -->
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="service" items="${services}">
                                    <tr data-status="${service.status}" data-service="${service.name}" data-service-id="${service.id}">
                                        <td><strong>${service.name}</strong></td>
                                        <td><span class="status-badge status-${service.status.toLowerCase()}">${service.status}</span></td>
                                        <td><fmt:formatNumber value="${service.price}" type="currency" currencyCode="VND" maxFractionDigits="0"/></td>
                                        <td>${service.description}</td>
                                        <!-- image -->
                                        <td>

                                            <button class="btn btn-sm btn-secondary" onclick="editService(this)"
                                                    data-id="${service.id}"
                                                    data-name="${service.name}"
                                                    data-status="${service.status}"
                                                    data-price="${service.price}"
                                                    data-description="${service.description}"
                                                    >
                                                <i class="fas fa-edit"></i>
                                            </button>
                                            <button class="btn btn-sm btn-danger" onclick="deleteService(${service.id})">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    <div class="pagination">
                        <c:if test="${currentPage > 1}">
                            <form action="serviceManage" method="get" style="display:inline;">
                                <input type="hidden" name="page" value="${currentPage - 1}">
                                <input type="hidden" name="size" value="${pageSize}">
                                <input type="hidden" name="branchId" value="${branchId}">
                                <button type="submit">Previous</button>
                            </form>
                        </c:if>
                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <form action="serviceManage" method="get" style="display:inline;">
                                <input type="hidden" name="page" value="${i}">
                                <input type="hidden" name="size" value="${pageSize}">
                                <input type="hidden" name="branchId" value="${branchId}">
                                <button type="submit" ${i == currentPage ? 'disabled' : ''}>${i}</button>
                            </form>
                        </c:forEach>
                        <c:if test="${currentPage < totalPages}">
                            <form action="serviceManage" method="get" style="display:inline;">
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

    <!-- Add Service Modal -->
    <div class="modal" id="serviceModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h3 id="modalTitle">Add New Service</h3>
                    <button class="modal-close" onclick="closeModal()">×</button>
                </div>
                <form id="serviceForm" action="addService" method="post" enctype="multipart/form-data">
                    <div class="modal-body">
                        <div class="form-row">
                            <div class="form-group">
                                <label for="service_name">Service Name *</label>
                                <input type="text" id="service_name" name="service_name" required>
                                <span id="service_name_error" class="error-message" style="display:none; color:hsl(var(--error));"></span>
                            </div>
                            <div class="form-group">
                                <label for="status">Status *</label>
                                <select id="status" name="status" required>
                                    <option value="Active">Active</option>
                                    <option value="Inactive">Inactive</option>
                                </select>
                                <span id="status_error" class="error-message" style="display:none; color:hsl(var(--error));"></span>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="price">Price (VND) *</label>
                            <input type="number" id="price" name="price" min="0" step="1000" required>
                            <span id="price_error" class="error-message" style="display:none; color:hsl(var(--error));"></span>
                        </div>
                        <div class="form-group">
                            <label for="image">Image</label>
                            <input type="file" id="image-add" name="image" accept="image/*" multiple required>
                            <span id="image-add_error" class="error-message" style="display:none; color:hsl(var(--error));"></span>
                        </div>
                        <div class="form__group">
                            <label>Image Preview</label>
                            <div class="wrapper-images" id="imagePreviewWrapper-add">
                                <div class="images"></div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="description">Description</label>
                            <textarea id="description" name="description" rows="3" placeholder="Description..."></textarea>
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

    <!-- Edit Service Modal -->
    <div class="modal" id="editServiceModal">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h3 id="modalEditTitle">Edit Service</h3>
                    <button class="modal-close" onclick="closeEditModal()">×</button>
                </div>
                <form id="editServiceForm" action="editService" method="post" enctype="multipart/form-data">
                    <input type="hidden" id="edit_service_id" name="service_id">
                    <div class="modal-body">
                        <div class="form-row">
                            <div class="form-group">
                                <label for="edit_service_name">Service Name *</label>
                                <input type="text" id="edit_service_name" name="service_name" required>
                                <span id="edit_service_name_error" class="error-message" style="display:none; color:hsl(var(--error));"></span>
                            </div>
                            <div class="form-group">
                                <label for="edit_status">Status *</label>
                                <select id="edit_status" name="status" required>
                                    <option value="Active">Active</option>
                                    <option value="Inactive">Inactive</option>
                                </select>
                                <span id="edit_status_error" class="error-message" style="display:none; color:hsl(var(--error));"></span>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="edit_price">Price (VND) *</label>
                            <input type="number" id="edit_price" name="price" min="0" step="1000" required>
                            <span id="edit_price_error" class="error-message" style="display:none; color:hsl(var(--error));"></span>
                        </div>
                        <div class="form-group">
                            <label for="image">Image</label>
                            <input type="file" id="image-edit" name="image" accept="image/*">
                            <span id="image-edit_error" class="error-message" style="display:none; color:hsl(var(--error));"></span>
                        </div>
                        <div class="form-group">
                            <label>Image Preview</label>
                            <div class="wrapper-images" id="imagePreviewWrapper-edit">
                                <div class="images"></div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="edit_description">Description</label>
                            <textarea id="edit_description" name="description" rows="3" placeholder="Description..."></textarea>
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
    <script src="./js/reviewImages.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <script>
                            function openServiceModal() {
                                const modal = document.getElementById('serviceModal');
                                if (modal) {
                                    console.log("Opening modal");
                                    modal.style.display = 'block';
                                    modal.classList.add('show');
                                    modal.classList.remove('hide');
                                    document.body.style.overflow = 'hidden';
                                } else {
                                    console.error("Modal element not found");
                                }
                            }

                            function closeModal() {
                                const modal = document.getElementById('serviceModal');
                                if (modal) {
                                    modal.style.display = 'none';
                                    modal.classList.remove('show');
                                    modal.classList.add('hide');
                                    document.body.style.overflow = '';
                                }
                            }

                            document.addEventListener('click', function (event) {
                                const modal = document.getElementById('serviceModal');
                                if (modal && event.target === modal) {
                                    closeModal();
                                }
                            });

                            function deleteService(serviceId) {
                                Swal.fire({
                                    title: 'Are you sure?',
                                    text: "You won't be able to revert this!",
                                    icon: 'warning',
                                    showCancelButton: true,
                                    confirmButtonColor: '#3085d6',
                                    cancelButtonColor: '#d33',
                                    confirmButtonText: 'Yes, delete it!'
                                }).then((result) => {
                                    if (result.isConfirmed) {
                                        let form = document.createElement('form');
                                        form.method = 'post';
                                        form.action = 'deleteService';
                                        let inputAction = document.createElement('input');
                                        inputAction.type = 'hidden';
                                        inputAction.name = 'action';
                                        inputAction.value = 'delete';
                                        form.appendChild(inputAction);
                                        let inputId = document.createElement('input');
                                        inputId.type = 'hidden';
                                        inputId.name = 'serviceId';
                                        inputId.value = serviceId;
                                        form.appendChild(inputId);
                                        document.body.appendChild(form);
                                        form.submit();
                                    }
                                });
                            }

                            function editService(btn) {
                                document.getElementById('edit_service_id').value = btn.getAttribute('data-id');
                                document.getElementById('edit_service_name').value = btn.getAttribute('data-name');
                                document.getElementById('edit_status').value = btn.getAttribute('data-status');
                                document.getElementById('edit_price').value = btn.getAttribute('data-price');
                                document.getElementById('edit_description').value = btn.getAttribute('data-description');
                                const imageWrapper = document.getElementById("imagePreviewWrapper-edit");
                                imageWrapper.innerHTML = '';
                                const imageUrl = btn.getAttribute('data-image');
                                if (imageUrl) {
                                    const div = document.createElement("div");
                                    div.className = "images";
                                    const imgElement = document.createElement('img');
                                    imgElement.className = "images_img";
                                    imgElement.src = imageUrl;
                                    imgElement.alt = "";
                                    div.appendChild(imgElement);
                                    imageWrapper.appendChild(div);
                                }
                                const modal = document.getElementById('editServiceModal');
                                modal.style.display = 'block';
                                modal.classList.add('show');
                                modal.classList.remove('hide');
                                document.body.style.overflow = 'hidden';
                            }

                            function closeEditModal() {
                                const modal = document.getElementById('editServiceModal');
                                modal.style.display = 'none';
                                modal.classList.remove('show');
                                modal.classList.add('hide');
                                document.body.style.overflow = '';
                            }

                            document.getElementById('serviceForm').addEventListener('submit', function (e) {
                                if (!validateForm('serviceForm')) {
                                    e.preventDefault();
                                }
                            });

                            document.getElementById('editServiceForm').addEventListener('submit', function (e) {
                                if (!validateForm('editServiceForm')) {
                                    e.preventDefault();
                                }
                            });

                            function validateForm(formId) {
                                let isValid = true;
                                const form = document.getElementById(formId);
                                const serviceName = form.querySelector('[name="service_name"]');
                                const status = form.querySelector('[name="status"]');
                                const price = form.querySelector('[name="price"]');

                                form.querySelectorAll('.error-message').forEach(el => el.style.display = 'none');

                                if (!serviceName.value.trim()) {
                                    form.querySelector('#' + serviceName.id + '_error').textContent = 'Service name cannot be empty.';
                                    form.querySelector('#' + serviceName.id + '_error').style.display = 'block';
                                    isValid = false;
                                } else if (serviceName.value.trim().length > 100) {
                                    form.querySelector('#' + serviceName.id + '_error').textContent = 'Service name cannot exceed 100 characters.';
                                    form.querySelector('#' + serviceName.id + '_error').style.display = 'block';
                                    isValid = false;
                                }

                                if (!status.value || !['Active', 'Inactive'].includes(status.value)) {
                                    form.querySelector('#' + status.id + '_error').textContent = 'Please select a valid status.';
                                    form.querySelector('#' + status.id + '_error').style.display = 'block';
                                    isValid = false;
                                }

                                if (!price.value || isNaN(price.value) || parseFloat(price.value) <= 0) {
                                    form.querySelector('#' + price.id + '_error').textContent = 'Price must be a number greater than 0.';
                                    form.querySelector('#' + price.id + '_error').style.display = 'block';
                                    isValid = false;
                                }
                                if (formId === 'serviceForm' && images.length > 5) {
                                    form.querySelector('#image-add_error').textContent = 'Maximum 5 images allowed.';
                                    form.querySelector('#image-add_error').style.display = 'block';
                                    isValid = false;
                                } else if (formId === 'editServiceForm' && images.length > 5) {
                                    form.querySelector('#image-edit_error').textContent = 'Maximum 5 images allowed.';
                                    form.querySelector('#image-edit_error').style.display = 'block';
                                    isValid = false;
                                }
                                console.log('Form validation result:', isValid)

                                return isValid;
                            }
    </script>

    <script>
        var errorMsg = "${error != null ? error : ''}";
        var successMsg = "${success != null ? success : ''}";
        var warningMsg = "${warning != null ? warning : ''}";
        if (errorMsg && errorMsg.trim() !== "") {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: errorMsg
            }).then(() => {
                window.location.href = 'serviceManage';
            });
        } else if (successMsg && successMsg.trim() !== "") {
            Swal.fire({
                icon: 'success',
                title: 'Success',
                text: successMsg
            }).then(() => {
                window.location.href = 'serviceManage';

            });
        } else if (warningMsg && warningMsg.trim() !== "") {
            Swal.fire({
                icon: 'warning',
                title: 'Warning',
                text: warningMsg
            }).then(() => {
                window.location.href = 'serviceManage';
            });
        }
    </script>
</body>
</html>
