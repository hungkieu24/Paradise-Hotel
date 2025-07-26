<%-- 
    Document   : chatUI
    Created on : Jul 9, 2025, 3:57:05 PM
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>ChatAI</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <link rel="stylesheet" href="./css/chatUi.css"/>
    </head>
    <body>
        
        <input type="hidden" id="userId" value="${user.id}">
        <!--Chat icon-->
        <div id="chat-icon" onclick="toggleChatWindow()">
            <img class="chat-icon__img" src="./img/robo.gif">
        </div>

        <!-- Chat Popup -->
        <div id="chat-window">
            <div class="chat-header">
                <span>ChatAI</span>
                <button onclick="toggleChatWindow()" class="close-btn">x</button>
            </div>
            <div class="chat-history-list" id="chat-history-list">
                <!-- Lịch sử chat sẽ được JS render -->
            </div>
            <div class="chat-body" id="chat-body">
                <!-- Nội dung chat sẽ được JS render -->
            </div>
            <div class="chat-footer">
                <input type="text" id="chat-input" placeholder="Send message..." 
                       onkeydown="if (event.key === 'Enter') {
                                   sendMessage();
                               }">
                <button onclick="sendMessage()">Send</button>
            </div>
        </div>
        <script src="./js/chatUi.js"></script>
    </body>
</html>
