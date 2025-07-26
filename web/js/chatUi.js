/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
let chatOpen = false;
let allConversations = [];
let currentConversation = [];

function toggleChatWindow() {
    const chat = document.getElementById("chat-window");
    chatOpen = !chatOpen;
    chat.style.display = chatOpen ? "flex" : "none";
    if (chatOpen) {
        loadHistoryList();
        renderChat();
        scrollChatToBottom();
    }
}
window.onload = function () {
    loadHistoryList();
};

function sendMessage() {
    const input = document.getElementById("chat-input");
    const userId = document.getElementById("userID").value;
    const text = input.value.trim();
    if (!text)
        return;
    if (!userId) {
        currentConversation.push({role: "ai", text: "Vui lòng đăng nhập để sử dụng ChatAI.", timestamp: new Date().toISOString()});
        renderChat();
        scrollChatToBottom();
        return;
    }

    currentConversation.push({role: "user", text, timestamp: new Date().toISOString()});
    renderChat();
    input.value = "";

    // Gửi message và userId lên servlet
    fetch("ChatServlet", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({message: text, userId: userId})
    })
            .then(res => {
                if (!res.ok) {
                    if (res.status === 400) {
                        throw new Error("Vui lòng đăng nhập để sử dụng ChatAI.");
                    } else if (res.status === 403) {
                        throw new Error("Bạn không có quyền truy cập thông tin này.");
                    } else {
                        throw new Error("Server error");
                    }
                }
                return res.json();
            })
            .then(data => {
                currentConversation.push({role: "ai", text: data.answer, timestamp: new Date().toISOString()});
                renderChat();
                scrollChatToBottom();
                loadHistoryList(); // Cập nhật lịch sử sau khi nhận phản hồi
            })
            .catch(err => {
                currentConversation.push({role: "ai", text: err.message, timestamp: new Date().toISOString()});
                renderChat();
                scrollChatToBottom();
            });
}

function renderChat() {
    const chatBody = document.getElementById("chat-body");
    chatBody.innerHTML = "";
    currentConversation.forEach(msg => {
        const div = document.createElement("div");
        div.className = "msg " + (msg.role === "user" ? "user" : "ai");
        const time = new Date(msg.timestamp).toLocaleTimeString();
        div.innerHTML = `<div class="message-content"><div class="message-text">${msg.text}</div><div class="message-time">${time}</div></div>`;

        chatBody.appendChild(div);
    });
}

function scrollChatToBottom() {
    const chatBody = document.getElementById("chat-body");
    chatBody.scrollTop = chatBody.scrollHeight;
}

function newConversation() {
    if (currentConversation.length) {
        allConversations.push([...currentConversation]);
        currentConversation = [];
        renderChat();
        loadHistoryList();
    }
}

function loadHistoryList() {
    const userId = document.getElementById("userID").value;
    if (!userId) {
        document.getElementById("chat-history-list").innerHTML = "<div>Vui lòng đăng nhập để xem lịch sử trò chuyện.</div>";
        return;
    }

    fetch(`ChatHistoryServlet?userID=${userId}`, {
        method: "GET",
        headers: {"Content-Type": "application/json"}
    })
            .then(res => {
                if (!res.ok)
                    throw new Error("Không thể tải lịch sử trò chuyện.");
                return res.json();
            })
            .then(data => {
                allConversations = data.conversations || [];
                const list = document.getElementById("chat-history-list");
                list.innerHTML = "";
                allConversations.forEach((conv, idx) => {
                    const div = document.createElement("div");
                    const firstMessage = conv[0]?.text.substring(0, 20) + (conv[0]?.text.length > 20 ? "..." : "");
                    const time = conv[0]?.timestamp ? new Date(conv[0].timestamp).toLocaleString() : "";
                    div.innerHTML = `<span>Chat chatHistory: ${firstMessage}</span><small>${time}</small>`;
                    div.onclick = () => loadConversation(idx);
                    if (currentConversation === conv)
                        div.classList.add("active");
                    list.appendChild(div);
                });
                const newDiv = document.createElement("div");
                newDiv.textContent = "↻";
                newDiv.onclick = newConversation;
                list.appendChild(newDiv);
            })
            .catch(err => {
                const list = document.getElementById("chat-history-list");
                list.innerHTML = `<div>${err.message}</div>`;
            });
}

function loadConversation(idx) {
    currentConversation = allConversations[idx];
    renderChat();
    loadHistoryList();
}