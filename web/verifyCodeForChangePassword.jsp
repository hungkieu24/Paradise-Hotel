<%-- 
    Document   : verifyCode
    Created on : Jun 1, 2025, 10:24:47 PM
    Author     : hoang
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css"/>
        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;500;600&display=swap"/>
        <link rel="stylesheet" href="./css/forgotPasswordStyles.css"/>
    </head>
    <body>
        <form action="verifyCodeForChangePassword" method="post">
            <h3>Enter Verification Code</h3>
            <label for="code">Verification Code</label>
            <input type="text" placeholder="Enter code" id="code" name="code" required>
            <input type="hidden" name="email" value="${email}">

            <button type="submit">Verify</button>
            <p id="countdown" style=" font-size: 18px"></p>
            <h4><a href="changePassword.jsp">Resend Code</a></h4>
        </form>
    </body>
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
    <script>
        // lấy timestap từ session
        let expiryTimestamp = ${sessionScope.resetExpiry};
        function updateCountdown() {
            const now = new Date().getTime();
            const distance = expiryTimestamp - now;
            if (distance <= 0) {
                document.getElementById("countdown").innerHTML = "The code has expired. Please resend.";
                document.getElementById("countdown").style.color = "red";
                document.querySelector("button[type='submit']").disabled = true;
                return;
            }
            const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
            const seconds = Math.floor((distance % (1000 * 60)) / 1000);
            document.getElementById("countdown").innerHTML = "The code will expire in: " + minutes + ":" + seconds;
        }
        updateCountdown();// lần đầu gọi
        setInterval(updateCountdown, 1000);// cập nhật mỗi giây
    </script>
    <script>
        document.querySelector('form').addEventListener('submit', function (e) {
            var codeInput = document.getElementById('code');
            var code = codeInput.value.trim();
            var codeRegex = /^\d{6}$/; // 6 ký tự số

            if (!codeRegex.test(code)) {
                e.preventDefault();
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'Verification code must be exactly 6 digits.'
                });
                codeInput.focus();
                return false;
            }
            // Nếu hợp lệ, cho submit form bình thường
        });
    </script>
</html>
