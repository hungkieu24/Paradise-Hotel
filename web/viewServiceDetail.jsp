<%-- 
    Document   : viewServiceDetail
    Created on : Jun 27, 2025, 6:58:02 PM
    Author     : KTC
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="en" />
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
        <link href="css/viewServiceDetail.css" rel="stylesheet">
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
            <div class="sidebar-navigation">
                <nav>
                    <ul class="level-1">
                        <li class="parent"><a href="#0">Home</a>
                            <ul class="level-2">
                                <li class="back"><a href="#0">Back</a></li>
                                <li><a href="index.html">Home Video Bg</a></li>
                                <li><a href="index-2.html">Home Carousel</a></li>
                                <li><a href="index-3.html">Home FlexSlider</a></li>
                                <li><a href="index-4.html">Home Youtube/Vimeo</a></li>
                                <li><a href="index-5.html">Home Parallax</a></li>
                                <li><a href="index-6.html">Home Parallax 2</a></li>
                            </ul>
                        </li>
                        <li class="parent"><a href="#0">Rooms & Suites</a>
                            <ul class="level-2">
                                <li class="back"><a href="#0">Back</a></li>
                                <li><a href="room-list-1.html">Room list 1</a></li>
                                <li><a href="room-list-2.html">Room list 2</a></li>
                                <li><a href="room-list-3.html">Room list 3</a></li>
                                <li><a href="room-details.html">Room details</a></li>
                                <li><a href="room-details-booking.html">Working Booking Request</a></li>
                            </ul>
                        </li>
                        <li><a href="about.html">About</a></li>
                        <li><a href="restaurant.html">Restaurant</a></li>
                        <li><a href="news-1.html">News & events</a></li>
                        <li><a href="contacts.html">Contact</a></li>
                        <li class="parent"><a href="#0">Other Pages</a>
                            <ul class="level-2">
                                <li class="back"><a href="#0">Back</a></li>
                                <li><a href="404.html">Error Page</a></li>
                                <li><a href="gallery.html">Masonry Gallery</a></li>
                                <li><a href="menu-of-the-day.html">Menu of the day</a></li>
                                <li><a href="modal-advertise-1.html">Modal Advertise</a></li>
                                <li><a href="cookie-bar.html">GDPR Cookie Bar</a></li>
                                <li><a href="coming-soon.html">Coming Soon</a></li>
                            </ul>
                        </li>
                        <li class="parent"><a href="#0">Menu Versions</a>
                            <ul class="level-2">
                                <li class="back"><a href="#0">Back</a></li>
                                <li><a href="menu-2.html">Menu Version 2 <span class="custom_badge">Hot</span></a></li>
                                <li><a href="menu-3.html">Menu Version 3</a></li>
                                <li><a href="menu-4.html">Menu Version 4</a></li>
                                <li><a href="menu-5.html">Menu Version 5</a></li>
                            </ul>
                        </li>
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
                <img class="jarallax-img" src="img/loginBackground.jpg" alt="">
                <div class="wrapper opacity-mask d-flex align-items-center justify-content-center text-center animate_hero" data-opacity-mask="rgba(0, 0, 0, 0.5)">
                    <div class="container">
                        <small class="slide-animated one">Luxury Hotel Experience</small>
                        <h1 class="slide-animated two">${service.name}</h1>
                    </div>
                </div>
            </div>
            <!-- /Background Img Parallax -->

            <div class="container margin_120_95" style="padding-top: 0px; padding-bottom: 0px">
                <div class="row justify-content-center">
                    <div class="col-lg-10">
                        <figure><img src="${service.imageUrl}" alt="${service.name}" class="img-fluid"></figure>
                    </div>

                    <div class="col-lg-8 mt-4">
                        <div class="box_contents_in">
                            <h2 class="mb-4">${service.name}</h2>
                            <p><strong>Describe:</strong> ${service.description}</p>
                            <p><strong>Price:</strong>
                                <c:choose>
                                    <c:when test="${service.price != null}">
                                        <fmt:formatNumber value="${service.price}" type="number" groupingUsed="true" /> VND
                                    </c:when>
                                </c:choose>
                            </p>
                            <p><strong>Status:</strong> ${service.status}</p>
                            <p><strong>Branches provide:</strong> ${branch.name}</p>
                            <p><strong>Contact support:</strong> 
                                <a href="tel:${branch.phone}">${branch.phone}</a> | 
                                <a href="mailto:${branch.email}">${branch.email}</a>
                            </p>
                        </div>
                    </div>
                    <div class="bg_white">
                        <div class="container margin_120_95" style="padding-top: 0px; padding-bottom: 0px"  >
                            <div data-cue="slideInUp">
                                <div class="title">
                                    <small>Paradise Hotel</small>
                                    <h2>Services in the same branch</h2>
                                </div>
                                <div class="row" data-cues="slideInUp" data-delay="800">
                                    <c:forEach items="${relatedServices}" var="r">

                                        <div class="col-xl-4 col-lg-6 col-md-6 col-sm-6">
                                            <a href="viewServiceDetail?serviceId=${r.id}" class="box_cat_rooms">
                                                <figure>
                                                    <div class="background-image" data-background="url(${r.imageUrl})"></div>
                                                    <div class="info">
                                                        <small>
                                                            From 
                                                            <fmt:formatNumber value="${r.price}" type="number" groupingUsed="true" maxFractionDigits="0" />
                                                            VND
                                                        </small>
                                                        <h3>${r.name}</h3>
                                                        <span>Read more</span>
                                                    </div>
                                                </figure>
                                            </a>
                                        </div>

                                    </c:forEach>
                                </div>
                                <!-- /row-->
                            </div>
                        </div>
                    </div>

                </div>
            </div>

            <!--/container -->
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
            <path d="M50,1 a49,49 0 0,1 0,98 a49,49 0 0,1 0,-98"/>
            </svg>
        </div>
        <!-- /back to top -->

        <!-- COMMON SCRIPTS -->
        <script src="js/common_scripts.js"></script>
        <script src="js/common_functions.js"></script>
        <script src="phpmailer/validate.js"></script>

    </body>
</html>
