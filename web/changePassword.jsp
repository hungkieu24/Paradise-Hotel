<%-- 
    Document   : changePassword
    Created on : Jun 14, 2025, 1:00:17 AM
    Author     : KTC
--%>

<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">

    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta name="description" content="">
        <meta name="author" content="Ansonika">
        <title>PARADISE - Hotel and Bed&Breakfast Site Template</title>

        <!-- Favicons-->
        <link rel="shortcut icon" href="img/favicon.ico" type="image/x-icon">
        <link rel="apple-touch-icon" type="image/x-icon" href="img/apple-touch-icon-57x57-precomposed.png">
        <link rel="apple-touch-icon" type="image/x-icon" sizes="72x72" href="img/apple-touch-icon-72x72-precomposed.png">
        <link rel="apple-touch-icon" type="image/x-icon" sizes="114x114" href="img/apple-touch-icon-114x114-precomposed.png">
        <link rel="apple-touch-icon" type="image/x-icon" sizes="144x144" href="img/apple-touch-icon-144x144-precomposed.png">

        <!-- GOOGLE WEB FONT-->
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Caveat:wght@400;500&family=Montserrat:wght@300;400;500;600;700&display=swap" rel="stylesheet">

        <!-- BASE CSS -->
        <link href="css/bootstrap.min.css" rel="stylesheet">
        <link href="css/style.css" rel="stylesheet">
        <link href="css/vendors.min.css" rel="stylesheet">
        <link rel="stylesheet" href="css/editProfile1.css">
        <!-- YOUR CUSTOM CSS -->
        <link href="css/custom.css" rel="stylesheet">
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

        <div id="preloader">
            <div data-loader="circle-side"></div>
        </div> 
        <div class="layer"></div><!-- Opacity Mask -->


        <div class="nav_panel">
            <a href="#" class="closebt open_close_nav_panel"><i class="bi bi-x"></i></a>
            <div class="logo_panel"><img src="img/logo_sticky.png" width="135" height="45" alt=""></div>

        </div>
        <!-- /nav_panel -->

        <main>

            <div class="hero medium-height jarallax" data-jarallax data-speed="0.2">
                <img class="jarallax-img" src="img/room1.jpg" alt="">
                <div class="wrapper opacity-mask d-flex align-items-center justify-content-center text-center animate_hero" data-opacity-mask="rgba(0, 0, 0, 0.5)">
                    <div class="container">
                        <small class="slide-animated one">Luxury Hotel Experience</small>
                        <h1 class="slide-animated two">Update Password</h1>
                    </div>
                </div>
            </div>
            <!-- /Background Img Parallax -->

            <div class="container margin_120_95">
                <div class="sidebar">
                    <img class="avatar" src="${sessionScope.user.getAvatar_url()}" alt="Avatar"/>
                    <p>Rank: <span>${sessionScope.loyaltypointlp.getLevel()}</span> </p>
                    <p>Accumulated Points: <a href="#">${sessionScope.loyaltypointlp.getPoints()}</a></p>
                    <ul>
                        <li><a href="editProfile">Personal Info</a></li>
                        <li><a href="bookingHistory">Booking History</a></li>
                        <li><a href="#">My Booking</a></li>
                        <li><a href="myWallet">My Wallet</a></li>
                        <li><a href="redeemVoucher">Loyalty Status</a> </li>
                        <li><a href="changePassword.jsp">Change Password</a></li>
                        <li><a href="./homepage?action=logout">Log out</a></li>
                        <li><a href="homepage">Home</a></li>
                    </ul>
                </div>
                <div class="form-wrapper">
                    <h3 class="mb-3">Change Password</h3>
                    <div id="message-contact"></div>
                    <div class="main-content">
                        <form id="changePassword" action="changePassword" method="post">

                            <div class="form-group">
                                <label for="currentPassword">Current Password:</label>
                                <div class="input-with-icon">
                                    <input type="password" id="currentPassword" name="currentPassword" required>
                                    <i class="bi bi-eye-slash toggle-password" toggle="#currentPassword"></i>
                                </div>
                                <!--<p class="form_error"></p>-->
                                <!--                                <c:if test="${not empty currentPasswordError}">
                                                                    <p class="form_error">${currentPasswordError}</p>
                                </c:if> -->
                            </div> 

                            <div class="form-group">
                                <label for="newPassword">New Password:</label>
                                <div class="input-with-icon">
                                    <input type="password" id="newPassword" name="newPassword" required>
                                    <i class="bi bi-eye-slash toggle-password" toggle="#newPassword"></i>
                                </div>
                                <p style="color: red" class="form__error username__error"></p>
                            </div>

                            <div class="form-group">
                                <label for="confirmPassword">Confirm New Password:</label>
                                <div class="input-with-icon">
                                    <input type="password" id="confirmPassword" name="confirmPassword" required>
                                    <i class="bi bi-eye-slash toggle-password" toggle="#confirmPassword"></i>
                                </div>
                                <p style="color: red" class="form__error username__error"></p>
                                <c:if test="${not empty confirmPasswordError}">
                                    <p class="form_error">${confirmPasswordError}</p>
                                </c:if>
                            </div>

                            <div style="display: flex; justify-content: flex-end; gap: 15px; margin-top: 20px;">
                                <button class="openEditProfileModal1" type="submit">Update</button>
                                <!--                                <button class="openEditProfileModal1" type="reset">Cancel</button>-->
                                <a href="forgotPassword.jsp" class="btn_1" style="display: inline-block;">Forgot password?</a>
                            </div>

                        </form>
                    </div>

                </div>

                <!-- /row -->
            </div>

        </main>

        <footer class="revealed">
            <div class="footer_bg">
                <div class="gradient_over"></div>
                <div class="background-image" data-background="url(img/registerbg.jpg)"></div>
            </div>
            <div class="container">
                <div class="row move_content">
                    <div class="col-lg-4 col-md-12">
                        <h5>Contacts</h5>
                        <ul>
                            <li>FPT University<br>HaNoi-VN<br><br></li>
                            <li><strong><a href="#0">hotelparadise.work@gmail.com</a></strong></li>
                            <li><strong><a href="#0">+84 867298400</a></strong></li>
                        </ul>
                        <div class="social">
                            <ul>
                                <li><a href="#0"><i class="bi bi-instagram"></i></a></li>
                                <li><a href="#0"><i class="bi bi-whatsapp"></i></a></li>
                                <li><a href="#0"><i class="bi bi-facebook"></i></a></li>
                                <li><a href="#0"><i class="bi bi-twitter"></i></a></li>
                            </ul>
                        </div>
                    </div>
                    <div class="col-lg-3 col-md-6 ms-lg-auto">
                        <h5>Explore</h5>
                        <div class="footer_links">
                            <ul>
                                <li><a href="homepage">Home</a></li>
                                <li><a href="about.jsp">About Us</a></li>
                            </ul>
                        </div>
                    </div>
                    <div class="col-lg-3 col-md-6">
                        <div id="newsletter">
                            <h5>Newsletter</h5>
                            <div id="message-newsletter"></div>
                            <form method="post" action="phpmailer/newsletter_template_email.php" name="newsletter_form" id="newsletter_form">
                                <div class="form-group">
                                    <input type="email" name="email_newsletter" id="email_newsletter" class="form-control" placeholder="Your email">
                                    <button type="submit" id="submit-newsletter"><i class="bi bi-send"></i></button>
                                </div>
                            </form>
                            <p>Receive latest offers and promos without spam. You can cancel anytime.</p>
                        </div>
                    </div>
                </div>
                <!--/row-->
            </div>
            <!--/container-->
            <div class="copy">
                <div class="container">
                    © Paradise - by <a href="#">SE1912_Group2</a>
                </div>
            </div>
        </footer>

        <div class="progress-wrap">
            <svg class="progress-circle svg-content" width="100%" height="100%" viewBox="-1 -1 102 102">
            <path d="M50,1 a49,49 0 0,1 0,98 a49,49 0 0,1 0,-98" />
            </svg>
        </div>
        <!-- /back to top -->

        <!-- COMMON SCRIPTS -->
        <script src="js/common_scripts.js"></script>
        <script src="js/common_functions.js"></script>
        <script src="js/datepicker_inline.js"></script>
        <script src="phpmailer/validate.js"></script>
        <script src="./js/toastMessage.js"></script>                
        <script src="./js/validationForm.js"></script>
        <script>
            document.querySelectorAll('.toggle-password').forEach(function (eyeIcon) {
                eyeIcon.addEventListener('click', function () {
                    const input = document.querySelector(this.getAttribute('toggle'));
                    const type = input.getAttribute('type') === 'password' ? 'text' : 'password';
                    input.setAttribute('type', type);
                    this.classList.toggle('bi-eye');
                    this.classList.toggle('bi-eye-slash');
                });
            });
        </script>

        <script>
            Validator({
                form: '#changePassword',
                formGroupSelector: '.form-group',
                errorSelector: '.form__error',
                rules: [
                    Validator.minLength(' #newPassword', 8),
                    Validator.isRequired('#confirmPassword'),
                    Validator.isConfirmed(' #confirmPassword', function () {
                        return document.querySelector('#changePassword #newPassword').value;
                    }, 'Password re-entered is incorrect')
                ],
                onsubmit: function (formValue) {
                    document.querySelector('#changePassword').submit();
                }
            });
        </script>
        <script>
            window.addEventListener("load", function () {
                const preloader = document.getElementById("preloader");
                if (preloader)
                    preloader.style.display = "none";
            });
        </script>

    </body>
</html>
