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

function sendMessage() {
    const input = document.getElementById("chat-input");
    const text = input.value.trim();
    if (!text) return;
    currentConversation.push({role: "user", text});
    renderChat();
    input.value = "";

    // Gửi message lên servlet
    fetch("ChatServlet", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({message: text})
    })
            .then(res => {
                if (!res.ok)
                    throw new Error("Server error");
                return res.json();
            })
            .then(data => {
                currentConversation.push({role: "ai", text: data.answer});
                renderChat();
                scrollChatToBottom();
            })
            .catch(err => {
                currentConversation.push({role: "ai", text: "Lỗi: Không thể kết nối đến server. Vui lòng thử lại."});
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
        div.textContent = msg.text;
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
    const list = document.getElementById("chat-history-list");
    list.innerHTML = "";
    allConversations.forEach((conv, idx) => {
        const div = document.createElement("div");
        div.textContent = "Chat " + (idx + 1);
        div.onclick = () => loadConversation(idx);
        if (currentConversation === conv)
            div.classList.add("active");
        list.appendChild(div);
    });
    const newDiv = document.createElement("div");
    newDiv.textContent = "+ New";
    newDiv.onclick = newConversation;
    list.appendChild(newDiv);
}

function loadConversation(idx) {
    currentConversation = allConversations[idx];
    renderChat();
    loadHistoryList();
}


