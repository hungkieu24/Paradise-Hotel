<%-- 
    Document   : viewServiceDetail
    Created on : Jun 27, 2025, 6:58:02 PM
    Author     : KTC
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="en" />
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
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
        <link href="css/redeemVoucher.css" rel="stylesheet">
        <!-- YOUR CUSTOM CSS -->
        <link href="css/custom.css" rel="stylesheet">
    </head>

    <body> 

        <div id="preloader">
            <div data-loader="circle-side"></div>
        </div><!-- /Page Preload -->

        <div class="layer"></div><!-- Opacity Mask -->

        <%@ include file="./header.jsp"%>

        <div class="nav_panel">
            <a href="#" class="closebt open_close_nav_panel"><i class="bi bi-x"></i></a>
            <div class="logo_panel"><img src="img/logo_sticky.png" width="135" height="45" alt=""></div>
        </div>
        <!-- /nav_panel -->

        <main>
            <div class="hero medium-height jarallax" data-jarallax data-speed="0.2">
                <img class="jarallax-img" src="img/loginBackground.jpg" alt="">
                <div class="wrapper opacity-mask d-flex align-items-center justify-content-center text-center animate_hero" data-opacity-mask="rgba(0, 0, 0, 0.5)">
                    <div class="container">
                        <small class="slide-animated one">Luxury Hotel Experience</small>
                        <h1 class="slide-animated two">Our Vouchers</h1>
                    </div>
                </div>
            </div>
            <!-- /Background Img Parallax -->

            <div class="container">
                <h2>🎁 Redeem Your Voucher</h2>
                <!-- ✅ Hiển thị thông tin điểm và hạng -->
                <c:if test="${not empty loyaltyPoint}">
                    <p>👤 Hello, <strong>${sessionScope.user.username}</strong> — 
                        Tier: <strong>${loyaltyPoint.level}</strong>, 
                        Points: <strong>${loyaltyPoint.points}</strong></p>
                    </c:if>

                <!-- ✅ Danh sách voucher có thể đổi -->
                <table>
                    <thead>
                        <tr>
                            <th>Code</th>
                            <th>Description</th>
                            <th>Discount</th>
                            <th>Required Tier</th>
                            <th>Adjusted Cost</th>

                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>

                        <c:forEach var="voucher" items="${listVoucher}">
                            <tr>
                                <td>${voucher.code}</td>
                                <td>${voucher.description}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${voucher.discount_percent > 0}">
                                            ${voucher.discount_percent}% OFF
                                        </c:when>
                                        <c:otherwise>
                                            ${voucher.discount_amount} VND OFF
                                        </c:otherwise>
                                    </c:choose>

                                </td>
                                <td>
                                    <c:out value="${voucher.redemptionRule.requiredTier != null ? voucher.redemptionRule.requiredTier : 'Any'}" />
                                </td>
                                <td>
                                    <c:set var="discountRate" value="${discountRateHelper.discountRate(loyaltyPoint.level)}" />
                                    <c:set var="adjustedPoints" value="${voucher.redemptionRule.requiredPoints * (1 - discountRate)}" />
                                    <fmt:formatNumber value="${adjustedPoints}" maxFractionDigits="0"/> pts
                                    <small style="color:gray;">(-<fmt:formatNumber value="${discountRate * 100}" maxFractionDigits="0"/>%)</small>
                                </td>


                                <td>
                                    <c:set var="isRedeemed" value="false" />
                                    <c:forEach var="id" items="${redeemedVoucherIds}">
                                        <c:if test="${id == voucher.id}">
                                            <c:set var="isRedeemed" value="true" />
                                        </c:if>
                                    </c:forEach>


                                    <c:choose>
                                        <c:when test="${isRedeemed}">
                                            <span class="insufficient">Already Redeemed</span>
                                        </c:when>

                                        <c:when test="${tierRankHelper.tierRank(loyaltyPoint.level) < tierRankHelper.tierRank(voucher.redemptionRule.requiredTier)}">
                                            <span class="insufficient">Not enough rank</span>
                                        </c:when>

                                        <c:when test="${loyaltyPoint.points >= adjustedPoints 
                                                        and tierRankHelper.tierRank(loyaltyPoint.level) >= tierRankHelper.tierRank(voucher.redemptionRule.requiredTier)}">
                                                <form action="redeemVoucher" method="post">
                                                    <input type="hidden" name="voucherId" value="${voucher.id}" />
                                                    <input type="hidden" name="pointsUsed" value="${adjustedPoints}" />
                                                    <button type="submit">Redeem</button>
                                                </form>
                                        </c:when>

                                        <c:otherwise>
                                            <span class="insufficient">Not enough points</span>
                                        </c:otherwise>
                                    </c:choose>

                                </td>

                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

            <!--/container -->
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
                                <li><a href="index.html">Home</a></li>
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
            <path d="M50,1 a49,49 0 0,1 0,98 a49,49 0 0,1 0,-98"/>
            </svg>
        </div>
        <!-- /back to top -->

        <!-- COMMON SCRIPTS -->
        <script src="js/common_scripts.js"></script>
        <script src="js/common_functions.js"></script>
        <script src="phpmailer/validate.js"></script>
        <div id="toast-message" class="toast hidden">Thông báo mẫu</div>
        <script>
            function showToast(message, color = "#FF0000") {
                const toast = document.getElementById("toast-message");
                toast.innerText = message;
                toast.style.backgroundColor = color;
                toast.classList.remove("hidden");
                toast.classList.add("show");

                setTimeout(() => {
                    toast.classList.remove("show");
                    toast.classList.add("hidden");
                }, 3000);
            }
        </script>
        <script>
            <c:if test="${param.success eq 'true'}">
            showToast("✅ Voucher redeemed successfully!", "#4CAF50");
            </c:if>
            <c:if test="${param.success eq 'false'}">
            showToast("⚠️ You have already redeemed this voucher.", "#f57c00");
            </c:if>
            <c:if test="${not empty error}">
            showToast("${error}", "#e53935");
            </c:if>
        </script>

    </body>
</html>
