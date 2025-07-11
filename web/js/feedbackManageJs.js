/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */


initButtons("feedback.js-toggle", "data-actor-id", fillModalFeedback);

function fillModalFeedback(feedbackId) {
    fetch(`/ParadiseHotel/manager/FeedbackEventHandler?type=comment-tree&feedbackId=` + feedbackId)
            .then(res => res.json())
            .then(data => {
                if (!data || !data.feedback)
                    return;

                const feedback = data.feedback;
                const comments = data.comments || [];
                const modal = document.getElementById("feedback-modal");
                modal.dataset.feedbackId = feedbackId;

                // 1. Gán thông tin feedback vào các thẻ có id cụ thể
                document.getElementById("value-name").textContent = feedback.username;

                document.getElementById("value-bookingId").textContent = feedback.booking_id;

                // Rating stars
                const starElement = document.getElementById("value-rating-stars");
                const rating = parseInt(feedback.rating);
                starElement.textContent = "★".repeat(rating) + "☆".repeat(5 - rating);

                // Date
                document.getElementById("value-date").textContent = formatDate(feedback.created_at);

                // Status
                const statusEl = document.getElementById("value-status");
                statusEl.textContent = feedback.status;
                statusEl.className = `status-badge status-${feedback.status.toLowerCase()}`;

                // Message
                document.getElementById("value-feeback").textContent = feedback.comment;

                // 2. Render comment tree
                renderCommentTree(comments);
            })
            .catch(err => {
                console.error("Lỗi fetch feedback:", err);
            });
}


function renderCommentTree(comments) {
    const treeContainer = document.getElementById("comments-tree");
    treeContainer.innerHTML = "";

    const commentMap = new Map();
    comments.forEach(cmt => {
        cmt.children = [];
        commentMap.set(cmt.id, cmt);
    });

    const rootComments = [];
    comments.forEach(cmt => {
        if (cmt.parent_comment_id == null || cmt.parent_comment_id === 0) {
            rootComments.push(cmt);
        } else {
            const parent = commentMap.get(cmt.parent_comment_id);
            if (parent)
                parent.children.push(cmt);
        }
    });

    rootComments.forEach(cmt => {
        treeContainer.appendChild(buildCommentHTML(cmt, 0));
    });

    // Thêm form mới
    const replyForm = document.createElement("div");
    replyForm.className = "reply-form mt-4";
    replyForm.innerHTML = `
        <textarea placeholder="Write your response..." id="new-comment-text"></textarea>
        <div class="reply-form-actions">
            <button class="btn btn-primary" id="btn-post-root-reply">Post Response</button>
        </div>
    `;
    treeContainer.appendChild(replyForm);
    bindPostRootReplyHandler();
}

function buildCommentHTML(comment, depth) {
    const div = document.createElement("div");
    div.className = "comment-item";
    div.style.marginLeft = `${depth * 20}px`;

    div.innerHTML = buildCommentInnerHTML(comment);

    const commentId = comment.id;
    const replyBtn = div.querySelector(`.btn-reply[data-comment-id="${commentId}"]`);
    const cancelBtn = div.querySelector(`.btn-cancel-reply[data-comment-id="${commentId}"]`);
    const postReplyBtn = div.querySelector(`.btn-post-reply[data-comment-id="${commentId}"]`);
    const replyForm = div.querySelector(`#reply-form-${commentId}`);
    const replyTextarea = div.querySelector(`#reply-text-${commentId}`);
    const deleteBtn = div.querySelector(`.btn-delete[data-comment-id="${commentId}"]`);

    if (replyBtn && replyForm) {
        replyBtn.onclick = () => replyForm.classList.remove("hidden");
    }

    if (cancelBtn && replyForm) {
        cancelBtn.onclick = () => {
            replyForm.classList.add("hidden");
            if (replyTextarea)
                replyTextarea.value = "";
        };
    }

    if (postReplyBtn && replyTextarea) {
        postReplyBtn.onclick = () => handlePostReply(comment, replyTextarea.value.trim());
    }

    if (deleteBtn) {
        deleteBtn.onclick = () => {
            if (!confirm("Are you sure you want to delete this comment?"))
                return;

            fetch("/ParadiseHotel/manager/FeedbackEventHandler", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    action: "delete",
                    comment_id: commentId
                })
            })
                    .then(res => res.json())
                    .then(data => {
                        showJsonToast(data.message, data.messageType);
                        if (data.success) {
                            fillModalFeedback(comment.feedback_id); // reload lại tree
                        }
                    })
                    .catch(err => {
                        console.error("Lỗi khi xóa comment:", err);
                    });
        };
    }

    // Render children recursively
    comment.children.forEach(child => {
        div.appendChild(buildCommentHTML(child, depth + 1));
    });

    return div;
}

function buildCommentInnerHTML(comment) {
    const initials = getInitials(comment.account.fullname);
    const role = comment.account.role || "User";
    const color = getColorByRole(role);
    const commentId = comment.id;

    return `
        <div class="comment-header">
            <div class="comment-author">
                <div class="comment-avatar" style="background: ${color}">${initials}</div>
                <div class="comment-name">${comment.account.fullname}</div>
                <span style="font-size: 0.75rem; color: ${color}; font-weight: 500;">${role}</span>
            </div>
            <div class="comment-date">${formatDate(comment.created_at)}</div>
        </div>
        <div class="comment-body">
            <div class="comment-content">${comment.content}</div>
            <div class="comment-actions">
                <button class="btn btn-primary btn-small btn-reply" data-comment-id="${commentId}">Reply</button>
                <button class="btn btn-delete btn-small" data-comment-id="${commentId}">Delete</button>
            </div>
            <div class="reply-form hidden" id="reply-form-${commentId}">
                <textarea placeholder="Write your reply..." id="reply-text-${commentId}"></textarea>
                <div class="reply-form-actions">
                    <button class="btn btn-primary btn-post-reply" data-comment-id="${commentId}">Post Reply</button>
                    <button class="btn btn-secondary btn-cancel-reply" data-comment-id="${commentId}">Cancel</button>
                </div>
            </div>
        </div>
    `;
}

function handlePostReply(comment, content) {
    if (!content) {
        showJsonToast("Reply content cannot be empty!", "error");
        return;
    }

    const feedbackId = comment.feedback_id;
    const parentCommentId = comment.id;

    fetch("/ParadiseHotel/manager/FeedbackEventHandler", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({
            action: "reply",
            feedback_id: feedbackId,
            parent_comment_id: parentCommentId,
            content: content
        })
    })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    showJsonToast(data.message, data.messageType);
                    fillModalFeedback(feedbackId);
                } else {
                    showJsonToast(data.message, data.messageType || 'error');
                }
            })
            .catch(err => {
                console.error("Error posting reply:", err);
            });
}

function bindPostRootReplyHandler() {
    const postRootBtn = document.getElementById("btn-post-root-reply");
    const textarea = document.getElementById("new-comment-text");

    if (postRootBtn && textarea) {
        postRootBtn.onclick = () => {
            const content = textarea.value.trim();
            if (!content) {
                showJsonToast("Response content cannot be empty!", "error");
                return;
            }

            const feedbackId = document.getElementById("feedback-modal").dataset.feedbackId;
            if (!feedbackId) {
                console.error("Không tìm thấy feedbackId");
                return;
            }

            fetch("/ParadiseHotel/manager/FeedbackEventHandler", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    action: "postRoot",
                    feedback_id: feedbackId,
                    content: content
                })
            })
                    .then(res => res.json())
                    .then(data => {
                        if (data.success) {
                            showJsonToast(data.message, data.messageType);
                            textarea.value = ""; // clear input
                            fillModalFeedback(feedbackId); // reload comments
                        } else {
                            showJsonToast(data.message || "Đã xảy ra lỗi", data.messageType || "error");
                        }
                    })
                    .catch(err => {
                        console.error("Lỗi khi gửi phản hồi:", err);
                    });
        };
    }
}

function formatDate(dateStr) {
    const date = new Date(dateStr);
    return date.toLocaleString("en-GB", {
        year: "numeric", month: "short", day: "numeric",
        hour: "2-digit", minute: "2-digit"
    });
}

function getInitials(name) {
    if (!name)
        return "?";
    return name.split(" ").map(part => part[0]).join("").toUpperCase().slice(0, 2);
}

function getColorByRole(role) {
    switch (role) {
        case "Manager":
            return "#059669";
        case "Admin":
            return "#b91c1c";
        case "Customer":
            return "#3b82f6";
        default:
            return "#6b7280";
    }
}

function showJsonToast(message, type = 'success') {
    // Xóa toast cũ nếu có
    const oldToast = document.getElementById('toastMessage');
    if (oldToast)
        oldToast.remove();

    // Tạo toast mới
    const toast = document.createElement('div');
    toast.id = 'toastMessage';
    toast.className = `toast-message ${type}`;

    // Icon tương ứng
    const icon = document.createElement('i');
    if (type === 'success')
        icon.className = 'fa fa-check-circle';
    else if (type === 'error')
        icon.className = 'fa fa-times-circle';
    else
        icon.className = 'fa fa-info-circle';

    toast.appendChild(icon);
    toast.appendChild(document.createTextNode(` ${message}`));

    document.body.appendChild(toast);

    // Show animation
    setTimeout(() => {
        toast.classList.add('show');
    }, 100);

    // Auto hide
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => {
            toast.remove();
        }, 500);
    }, 5000);
}
