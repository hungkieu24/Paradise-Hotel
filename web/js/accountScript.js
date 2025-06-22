/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */



let transferStep = 1;

// Utility Functions
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Modal Functions
function openAddAccountModal() {
    document.getElementById("addAccountModal").classList.add("show");
}

function closeAddAccountModal() {
    document.getElementById("addAccountModal").classList.remove("show");
    document.getElementById("addAccountForm").reset();
}

function switchTab(tabName) {
    currentTab = tabName;

    // Update tab buttons
    document.querySelectorAll(".tab-btn").forEach((btn) => {
        btn.classList.remove("active");
    });
    document.querySelector(`[onclick="switchTab('${tabName}')"]`).classList.add("active");

    // Update tab content
    document.querySelectorAll(".tab-content").forEach((content) => {
        content.classList.remove("active");
    });
    document.getElementById(`${tabName}Tab`).classList.add("active");
}

function openRoleChangeModal(selectElement) {
    const newRole = selectElement.value;
    const currentRole = selectElement.dataset.current;
    const fullname = selectElement.dataset.fullname;
    const userId = selectElement.dataset.id;

    // Nếu không có gì thay đổi thì không mở modal
    if (newRole === currentRole) {
        return;
    }

    // Cập nhật nội dung trong modal
    const modalText = `
            Are you sure you want to change <strong>${fullname}</strong>'s role from 
            <span class="badge badge-${currentRole}">${currentRole}</span> to 
            <span class="badge badge-${newRole}">${newRole}</span>?
        `;
    document.getElementById("roleChangeText").innerHTML = modalText;
    // Gán dữ liệu ẩn để submit
    document.getElementById("roleUserId").value = userId;
    document.getElementById("roleName").value = newRole;
    // Mở modal
    document.getElementById("roleChangeModal").classList.add("show");
}

function closeRoleChangeModal() {
    document.getElementById("roleChangeModal").classList.remove("show");
    roleChangeData = null;
}

function openTransferOwnershipModal() {
    transferStep = 1;
    document.getElementById("transferOwnershipModal").classList.add("show");
    updateTransferStep();
}

function closeTransferOwnershipModal() {
    document.getElementById("transferOwnershipModal").classList.remove("show");
    transferStep = 1;
    document.getElementById("verificationCode").value = "";
    document.getElementById("newOwner").value = "";
}

function updateTransferStep() {
    document.querySelectorAll(".transfer-step").forEach((step) => {
        step.classList.remove("active");
    });
    document.getElementById(`transferStep${transferStep}`).classList.add("active");
}

function backToStep1() {
    transferStep = 1;
    updateTransferStep();
}

function backToStep2() {
    transferStep = 2;
    updateTransferStep();
}

function createNewOwner() {
    transferStep = 3;
    updateTransferStep();
}

// File Upload
function handleFileUpload(event) {
    const file = event.target.files[0];
    if (!file)
        return;

    // In a real implementation, you would parse the Excel file
    // For demonstration, we'll show a mock response
    showToast("Bulk upload functionality requires Excel parsing library", "warning");
}

function downloadTemplate() {
    showToast("Template download started", "success");
    // In a real implementation, generate and download Excel template
}

function openActionsModal(actionName, userFullName, userId) {
    // Hiển thị nội dung xác nhận
    const text = `Are you sure you want to <strong>${actionName}</strong> for user <strong>${userFullName}</strong>?`;
    document.getElementById("actionChangeText").innerHTML = text;

    // Gán dữ liệu ẩn để submit
    document.getElementById("actionUserId").value = userId;
    document.getElementById("actionType").value = actionName;

    // Mở modal
    document.getElementById("actionsModal").classList.add("show");
}

function closeActionsModal() {
    document.getElementById("actionsModal").classList.remove("show");
}

let selectedUsers = new Set();
function handleUserSelection(userId, checked) {
    if (checked) {
        selectedUsers.add(userId);
    } else {
        selectedUsers.delete(userId);
    }

    updateBulkActionsBar();
}

function updateBulkActionsBar() {
    const bulkBar = document.getElementById("bulkActionsBar");
    const selectedCount = document.querySelector(".selected-count");

    const btnReset = document.getElementById("btnResetPassword");
    const btnDelete = document.getElementById("btnSoftDelete");
    const btnRestore = document.getElementById("btnRestore");
    const btnInactive = document.getElementById("btnInactive");
    const btnActive = document.getElementById("btnActive");
    const btnBan = document.getElementById("btnBan");
    const btnUnBan = document.getElementById("btnUnBan");

    // Reset: ẩn tất cả các nút
    const hideAllButtons = () => {
        [btnReset, btnDelete, btnRestore, btnInactive, btnActive, btnBan, btnUnBan].forEach(btn => {
            if (btn)
                btn.style.display = "none";
        });
    };

    if (selectedUsers.size > 0) {
        bulkBar.style.display = "block";
        selectedCount.textContent = selectedUsers.size + " accounts selected";

        const selectedCheckboxes = document.querySelectorAll(".checkboxItem:checked");

        let allActive = true;
        let allInactive = true;
        let allBanned = true;
        let allDeleted = true;
        let hasDeleted = false;
        let hasOwner = false;
        selectedCheckboxes.forEach(cb => {
            const status = cb.getAttribute("data-status");
            const isDeleted = cb.getAttribute("data-is-deleted") === "true";
            const isOwner = cb.getAttribute("data-is-owner") === 'HotelOwner';
            if (status !== "Active")
                allActive = false;
            if (status !== "Inactive")
                allInactive = false;
            if (status !== "Banned")
                allBanned = false;
            if (!isDeleted)
                allDeleted = false;
            if (isDeleted)
                hasDeleted = true;
            if (isOwner)
                hasOwner = true;
        });

        hideAllButtons(); // Reset lại trạng thái nút


        // Nếu tất cả đều đã bị xóa → chỉ hiện "Restore"
        if (allDeleted) {
            btnRestore.style.display = "inline-block";
        }
        // Nếu có item bị xóa → chỉ hiện "Restore"
        else if (hasDeleted) {
            btnRestore.style.display = "inline-block";
        }
        //Nếu có Hotel owner → chỉ hiện "Reset"
        else if (hasOwner) {
            if (allActive) {
                btnReset.style.display = "inline-block";
                announce.innerHTML = "";
            } else {
                if (announce)
                    announce.innerHTML = "<strong>Can not do anything</strong>";
            }
        }
        // Nếu tất cả là Active → hiện các hành động bình thường
        else if (allActive) {
            btnReset.style.display = "inline-block";
            btnInactive.style.display = "inline-block";
            btnBan.style.display = "inline-block";
            btnDelete.style.display = "inline-block";
        }
        // Nếu tất cả là Inactive → hiện "Active" + "Delete"
        else if (allInactive) {
            btnActive.style.display = "inline-block";
            btnDelete.style.display = "inline-block";
        }
        // Nếu tất cả là Banned → hiện "Unban" + "Delete"
        else if (allBanned) {
            btnUnBan.style.display = "inline-block";
            btnDelete.style.display = "inline-block";
        }
        // Nếu nhiều trạng thái khác nhau → chỉ cho phép xóa mềm
        else {
            announce.innerHTML = "";
            btnDelete.style.display = "inline-block";
        }

    } else {
        bulkBar.style.display = "none";
    }
}

function handleSelectAll() {
    const isChecked = document.getElementById("selectAll").checked;
    const checkboxes = document.querySelectorAll('.checkboxItem');

    checkboxes.forEach(cb => {
        cb.checked = isChecked;
        handleUserSelection(cb.getAttribute('data-user-id'), cb.checked);
    });
    console.log(selectedUsers);

}

function openBulkActionsModal(actionType) {
    const modal = document.getElementById("bulkActionsModal");
    const listUserIdInput = document.getElementById("listUserId");
    const bulkActionTypeInput = document.getElementById("bulkActionType");
    const actionText = document.getElementById("bulkActionChangeText");

    const userIds = Array.from(selectedUsers).join(", ");
    listUserIdInput.value = userIds;
    bulkActionTypeInput.value = actionType;

    actionText.innerHTML = "Are you sure you want to <strong>" + actionType + "</strong> on the following user(s)?";

    modal.classList.add("show");
}

function closeBulkActionsModal() {
    const modal = document.getElementById("bulkActionsModal");
    modal.classList.remove("show");
}

function openTranferModal(userId, fullname, email) {
    // Hiện modal
    const modal = document.getElementById("tranferModal");

    // Điền giá trị vào các input ẩn
    document.getElementById("ownerID").value = userId;
    document.getElementById("ownerEmail").value = email;

    // Cập nhật nội dung mô tả
    const text = document.getElementById("transferText");

    text.innerHTML = "Are you sure you want to transfer ownership of <strong>Mr."
            + fullname + "</strong> to someone else?<br><em>We will send a verification code to <strong>"
            + email + "</strong> to confirm the transfer.</em>";
    modal.classList.add("show");
}

function closeTranferModal() {
    const modal = document.getElementById("tranferModal");
    modal.classList.remove("show");
}

// Xác nhận là có chuyển và mở modal nhập mã code xác nhận
function submitTransferOwnership() {
    event.preventDefault();
    const userId = document.getElementById("ownerID").value;
    const userEmail = document.getElementById("ownerEmail").value;

    const xhr = new XMLHttpRequest();
    xhr.open("POST", "/ParadiseHotel/admin/accountEventHandler", true);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                // Đóng modal hiện tại
                closeTranferModal();
                const result = JSON.parse(xhr.responseText);
                document.getElementById("notificationArea").innerHTML = result.message;

                // Mở modal kế tiếp
                const nextModal = document.getElementById("transferOwnershipModal");
                if (nextModal) {
                    nextModal.classList.add("show");
                }
            } else {
                console.error("Request failed: ", xhr.responseText);
                alert("An error occurred. Please try again.");
            }
        }
    };

    const params = `userId=${encodeURIComponent(userId)}&userEmail=${encodeURIComponent(userEmail)}&action=confirmTransfer`;
    xhr.send(params);
}

// resend code to old owner
function resendCode() {
    const xhr = new XMLHttpRequest();
    xhr.open("POST", "/ParadiseHotel/admin/accountEventHandler", true);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
            const notification = document.getElementById("notificationArea");
            if (xhr.status === 200) {
                const result = JSON.parse(xhr.responseText);
                notification.innerHTML = result.message;
            } else {
                notification.classList.add("warning");
                notification.innerHTML = "Failed to resend code. Please try again.";
            }
        }
    };

    const params = `action=resendCode`;
    xhr.send(params);
}

// verify code sent to old owner
function verifyCode() {
    const code = document.getElementById("verificationCode").value.trim();
    const notification = document.getElementById("notificationArea");

    if (code.length !== 6 || isNaN(code)) {
        notification.classList.add("warning");
        notification.innerHTML = "Please enter a valid 6-digit code.";
        return;
    }

    const xhr = new XMLHttpRequest();
    xhr.open("POST", "/ParadiseHotel/admin/accountEventHandler", true);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                const result = JSON.parse(xhr.responseText);
                notification.innerHTML = result.message;

                document.getElementById("transferStep1").classList.remove("active");
                document.getElementById("transferStep2").classList.add("active");
            } else {
                notification.classList.add("warning");
                notification.innerHTML = "Verification failed. Please try again.";
            }
        }
    };

    const params = `action=verifyCode&inputCode=${encodeURIComponent(code)}`;
    xhr.send(params);
}

//Gửi code xác nhận email của new owner
function sendCodeToCreateOwner() {
    const email = document.getElementById("email").value.trim();
    const fullName = document.getElementById("fullName").value.trim();
    const phone = document.getElementById("phone").value.trim();
    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value.trim();
    
    const notification = document.getElementById("notificationAreaCreate");

    const xhr = new XMLHttpRequest();
    xhr.open("POST", "/ParadiseHotel/admin/accountEventHandler", true);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                const result = JSON.parse(xhr.responseText);
                notification.innerHTML = result.message;

                // Chuyển sang bước xác nhận mã
                document.getElementById("transferStep3").classList.remove("active");
                document.getElementById("transferStep4").classList.add("active");
            } else {
                notification.classList.add("warning");
                notification.innerHTML = "Failed to send verification email";
            }
        }
    };

    const params = `action=sendCodeToCreateOwner&email=${encodeURIComponent(email)}
                    &fullName=${encodeURIComponent(fullName)}
                    &phone=${encodeURIComponent(phone)}
                    &username=${encodeURIComponent(username)}
                    &password=${encodeURIComponent(password)}`;
    xhr.send(params);
}

// Gửi lại mã code cho new owner
function resendCodeCreate() {
    const xhr = new XMLHttpRequest();
    xhr.open("POST", "/ParadiseHotel/admin/accountEventHandler", true);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
            const notification = document.getElementById("notificationAreaCreate");

            if (xhr.status === 200) {
                const result = JSON.parse(xhr.responseText);
                notification.innerHTML =  result.message;
            } else {
                notification.classList.add("warning");
                notification.innerHTML = "Failed to resend code. Please try again.";
            }
        }
    };

    const params = `action=resendCodeCreate`;
    xhr.send(params);
}


function verifyCodeCreate() {
    const code = document.getElementById("verificationCodeCreate").value.trim();
    const notification = document.getElementById("notificationAreaCreate");

    if (code.length !== 6 || isNaN(code)) {
        notification.innerHTML = `<div class="alert alert-warning">Please enter a valid 6-digit code.</div>`;
        return;
    }

    const xhr = new XMLHttpRequest();
    xhr.open("POST", "/ParadiseHotel/admin/accountEventHandler", true);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                // Mã đúng, reload lại trang account
                window.location.href = "./account";
            } else {
                // Mã sai hoặc lỗi server
                let message = "Verification failed. Please try again.";

                if (xhr.status === 400 || xhr.status === 500) {
                    try {
                        const res = JSON.parse(xhr.responseText);
                        if (res.message) message = res.message;
                    } catch (_) { }
                }

                notification.innerHTML = message;
            }
        }
    };

    const params = `action=verifyCodeCreate&inputCode=${encodeURIComponent(code)}`;
    xhr.send(params);
}

