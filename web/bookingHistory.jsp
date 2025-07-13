<%-- 
    Document   : bookingHistory
    Created on : Jul 10, 2025, 5:12:40 PM
    Author     : KTC
--%>


<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

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
        <link href="css/bookingHistory.css" rel="stylesheet">
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
            <div class="sidebar-navigation">
                <nav>
                    <ul class="level-1">
                        <li><a href="#">Personal Info</a></li>
                        <li><a href="editProfile">Change Personal Info</a></li>
                        <li><a href="#">Booking History</a></li>
                        <li><a href="myBooking">Your Booking</a></li>
                        <li><a href="#">Loyalty Status</a> </li>
                        <li><a href="#">Change Password</a></li>
                        <li class="parent"><a href="#0">Feedback</a>
                            <ul class="level-2">
                                <li class="back"><a href="#0">Back</a></li>
                                <li><a href="viewFeedback">View Feedback</a></li>
                                <li><a href="sendFeedback.jsp">Send Feedback</a></li>
                            </ul> 
                        </li>
                        <li><a href="./homepage?action=logout">Log out</a></li>
                        <li><a href="homepage" class="home-link">Home</a></li>
                    </ul>
                    <div class="panel_footer">
                        <div class="phone_element"><a href="tel://423424234"><i class="bi bi-telephone"></i><span><em>Info and bookings</em>+41 934 121 1334</span></a></div>
                    </div>
                    <!-- /panel_footer -->
                </nav>
            </div>
            <!-- /sidebar-navigation -->
        </div>
        <!-- /nav_panel -->

        <main>

            <div class="hero medium-height jarallax" data-jarallax data-speed="0.2">
                <img class="jarallax-img" src="img/room1.jpg" alt="">
                <div class="wrapper opacity-mask d-flex align-items-center justify-content-center text-center animate_hero" data-opacity-mask="rgba(0, 0, 0, 0.5)">
                    <div class="container">
                        <small class="slide-animated one">Luxury Hotel Experience</small>
                        <h1 class="slide-animated two">Booking History</h1>
                    </div>
                </div>
            </div>
            <!-- /Background Img Parallax -->

            <div class="container margin_120_95">
                <div class="sidebar" style="margin-right: 0px">
                    <img class="avatar" src="${sessionScope.user.getAvatar_url()}" alt="Avatar"/>
                    <p>Rank: <span>${sessionScope.loyaltypointlp.getLevel()}</span></p>
                    <p>Accumulated Points: <a href="#">${sessionScope.loyaltypointlp.getPoints()}</a></p>
                    <ul>
                        <li><a href="editProfile">Personal Info</a></li>
                        <li><a href="bookingHistory">Booking History</a></li>
                        <li><a href="myBooking">My Booking</a></li>
                        <li><a href="redeemVoucher">Loyalty Status</a> </li>
                        <li><a href="changePassword.jsp">Change Password</a></li>
                        <li><a href="./homepage?action=logout">Log out</a></li>
                        <li><a href="homepage">Home</a></li>
                    </ul>
                </div>

                <div class="form-wrapper">
                    <h3 class="mb-4">My Booking History</h3>
                    <div class="row">
                        <c:forEach var="b" items="${bookings}">
                            <div class="col-lg-4 col-md-6 mb-4">
                                <div class="card shadow-sm p-3 h-100" >
                                    <c:choose>
                                        <c:when test="${not empty b.roomTypeImage}">
                                            <img src="${b.roomTypeImage}" class="card-img-top" alt=""/>
                                        </c:when>
                                    </c:choose>

                                    <div class="card-body">
                                        <h5 class="card-title">
                                            <c:choose>
                                                <c:when test="${b.branchName != null}">${b.branchName}</c:when>
                                                <c:otherwise>Chi nhánh không xác định</c:otherwise>
                                            </c:choose>
                                        </h5>

                                        <p class="card-text mb-1">Check In - Check Out: 
                                            <fmt:formatDate value="${b.checkIn}" pattern="dd/MM/yyyy" /> - 
                                            <fmt:formatDate value="${b.checkOut}" pattern="dd/MM/yyyy" />
                                        </p>
                                        <p class="card-text mb-1">RoomType: ${b.roomTypeName}</p>
                                        <p class="card-text mb-1">Total Price: <fmt:formatNumber value="${b.totalPrice}" type="currency" currencySymbol="₫"/></p>
                                        <p class="card-text mt-2" style="font-weight: bold; color:
                                           <c:choose>
                                               <c:when test="${b.status eq 'Completed'}">green</c:when>
                                               <c:when test="${b.status eq 'Pending'}">orange</c:when>
                                               <c:when test="${b.status eq 'Cancelled'}">red</c:when>
                                               <c:otherwise>#555</c:otherwise>
                                           </c:choose>">
                                            <c:choose>
                                                <c:when test="${b.status eq 'Completed'}">Completed</c:when>
                                                <c:when test="${b.status eq 'Pending'}">Pending</c:when>
                                                <c:when test="${b.status eq 'Cancelled'}">Cancelled</c:when>
                                                <c:otherwise>${b.status}</c:otherwise>
                                            </c:choose>
                                        </p>

                                        <div class="d-flex justify-content-between mt-3">
                                            <a style="margin-left: 325px" href="#" class="btn_1 small open-detail" data-id="${b.id}">Chi tiết</a>

                                            <a href="rebook?id=${b.id}" class="btn_1 small outline">Đặt lại</a>



                                        </div>
                                        <div class="d-flex justify-content-between mt-3">
                                            <c:if test="${b.status eq 'Completed'}">
                                                <a style="margin-left: 350px" href="sendFeedback?bookingId=${b.id}" class="btn_1 small">Gửi Feedback</a>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                    <!-- Pagination -->
                    <div class="pagination d-flex justify-content-center mt-4"> 
                        <c:if test="${currentPage > 1}">
                            <a href="bookingHistory?page=${currentPage - 1}" class="prev">Previous</a>
                        </c:if>

                        <c:forEach var="i" begin="1" end="${totalPages}">
                            <a href="bookingHistory?page=${i}" class="${i == currentPage ? 'active' : ''}">${i}</a>
                        </c:forEach>

                        <c:if test="${currentPage < totalPages}">
                            <a href="bookingHistory?page=${currentPage + 1}" class="next">Next</a>
                        </c:if>
                    </div>

                </div>
            </div>
            <!-- Modal -->
            <div id="bookingDetailModal" class="modal-overlay" style="display:none;">
                <div class="modal-content">
                    <span class="close-button" onclick="closeModal()">×</span>
                    <div id="modal-body-content"></div>
                </div>
            </div>

        </main>

        <footer class="revealed">
            <div class="footer_bg">
                <div class="gradient_over"></div>
                <div class="background-image" data-background="url(img/rooms/3.jpg)"></div>
            </div>
            <div class="container">
                <div class="row move_content">
                    <div class="col-lg-4 col-md-12">
                        <h5>Contacts</h5>
                        <ul>
                            <li>Baker Street 567, Los Angeles 11023<br>California - US<br><br></li>
                            <li><strong><a href="#0">info@Paradisehotel.com</a></strong></li>
                            <li><strong><a href="#0">+434 43242232</a></strong></li>
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
                                <li><a href="about.html">About Us</a></li>
                                <li><a href="room-list-1.html">Rooms &amp; Suites</a></li>
                                <li><a href="news-1.html">News &amp; Events</a></li>
                                <li><a href="contacts.html">Contacts</a></li>
                                <li><a href="about.html">Terms and Conditions</a></li>
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
                    © Paradise - by <a href="#">Ansonika</a>
                </div>
            </div>
        </footer>
        <!-- /footer -->

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
                        document.querySelectorAll('.open-detail').forEach(btn => {
                            btn.addEventListener('click', function (e) {
                                e.preventDefault();
                                const bookingId = this.getAttribute('data-id');

                                fetch('bookingDetail?id=' + bookingId)
                                        .then(response => response.text())
                                        .then(html => {
                                            document.getElementById('modal-body-content').innerHTML = html;
                                            document.getElementById('bookingDetailModal').style.display = 'flex';
                                        })
                                        .catch(error => {
                                            console.error("Error loading booking detail:", error);
                                        });
                            });
                        });

                        function closeModal() {
                            document.getElementById('bookingDetailModal').style.display = 'none';
                        }

        </script>

    </body>
</html>
