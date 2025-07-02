<%-- 
    Document   : login
    Created on : May 24, 2025, 10:00:32 PM
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css"/>
        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;500;600&display=swap"/>
        <link rel="stylesheet" href="./css/loginStyles.css"/>
        <link rel="stylesheet" href="./css/custom.css"/>
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
            <%
                session.invalidate();
            %>
        </c:if>
        <form action="login" method="post">

            <h3>Login here</h3>
            <label for="username">User name</label>
            <input type="text" placeholder="Username" id="username" name="username"value="${param.username}" required>

            <label for="password"> Password</label>
            <input type="password" placeholder="Password" id="password" name="password" required> 

            <div class="forgot-password">
                <a href="forgotPassword.jsp">Forgot Password?</a>
            </div>

            <button id="login">Log In</button>
            <div class="social">
                <a href="https://accounts.google.com/o/oauth2/auth?scope=email%20profile&redirect_uri=http://localhost:8080/ParadiseHotel/login&response_type=code&client_id=202740089898-biog485gnu7f0i8v8q0sma4bjtl6effc.apps.googleusercontent.com&approval_prompt=force">
                    <div class="go"><i class="fab fa-google"></i> Google</div>
                </a>
            </div>
            <h4>You don't have an account ?<a href="register.jsp">Register</a></h4>
        </form>

        <script src="./js/toastMessage.js"></script>  

        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
        <script>
            var errorMsg = "${error != null ? error : ''}";
            var successMsg = "${success != null ? success : ''}";
            if (errorMsg && errorMsg.trim() !== "") {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: errorMsg
                });
            } else if (successMsg && successMsg.trim() !== "") {
                Swal.fire({
                    icon: 'success',
                    title: 'Success',
                    text: successMsg
                });
            }
        </script>
    </body>
</html>
