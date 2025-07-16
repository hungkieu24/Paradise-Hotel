<%-- 
    Document   : searchRoomResult
    Created on : Jun 1, 2025, 1:45:34 PM
    Author     : hungk
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="en" />

<!DOCTYPE html>
<html lang="zxx">

    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta name="description" content="">
        <meta name="author" content="Ansonika">
        <title>PARADISE - Hotel</title>

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

        <!-- YOUR CUSTOM CSS -->
        <link href="css/custom.css" rel="stylesheet">
    </head>

    <body> 

        <div id="preloader">
            <div data-loader="circle-side"></div>
        </div><!-- /Page Preload -->

        <div class="layer"></div><!-- Opacity Mask -->

        <%@ include file="./header.jsp"%>
        <!-- Header --><!-- /Header -->
        <main>

            <div class="hero medium-height jarallax" data-jarallax data-speed="0.2">
                <img class="jarallax-img" src="./img/registerbg.jpg" alt="">
                <div class="wrapper opacity-mask d-flex align-items-center justify-content-center text-center animate_hero" data-opacity-mask="rgba(0, 0, 0, 0.5)">
                    <div class="container">
                        <small class="slide-animated one">Luxury Hotel Experience</small>
                        <h1 class="slide-animated two">Available Rooms Based on Your Search</h1>
                    </div>
                </div>
            </div>
            <!-- /Background Img Parallax -->

            <div class="container margin_120_95 pb-0" id="first_section">
                <c:choose>
                    <c:when test="${empty availableRoomTypes}">
                        <div class="no-results" style="text-align: center; margin-top: 2rem;">
                            <h4>No suitable rooms found based on your search criteria or the hotel may be fully booked.</h4>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${availableRoomTypes}" var="r">
                           

                            <div class="row_list_version_2">
                                <div class="row g-0 align-items-center">
                                    <div class="col-xl-8">
                                        <div class="owl-carousel owl-theme carousel_item_1 kenburns rounded-img owl-loaded owl-drag">
                                            <div class="owl-stage-outer">
                                                <div class="owl-stage" >
                                                    <div class="owl-item active center" style="width: 936px;">
                                                        <div class="item">
                                                            <img src="./img/room1.jpg" alt=""></a>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <!-- /carousel -->
                                    </div>
                                    <div class="col-xl-4">
                                        <div class="box_item_info" data-jarallax-element="-25" style="transform: translate3d(0px, 5.36017px, 0px);">
                                            <small>
                                                From 
                                                <fmt:formatNumber value="${r.getBase_price()}" type="number" groupingUsed="true" maxFractionDigits="0" />
                                                VND /night
                                            </small>
                                            <h2>${r.getName()}</h2>
                                            <p>${r.getDescription()}</p>
                                            <p>Branch: <strong>${r.getBranch().getName()}</strong> </p>
                                            <div class="facilities clearfix">
                                                <ul>
                                                    <c:forEach items="${r.getAmenity()}" var="a">
                                                        <li>
                                                            ${a}
                                                        </li>
                                                    </c:forEach>
                                                </ul>
                                            </div>
                                            <div class="box_item_footer d-flex align-items-center justify-content-between">
                                                <a href="booking?roomTypeId=${r.getRoomTypeID()}&action=oneRoom" class="btn_4 learn-more">
                                                    <span class="circle">
                                                        <span class="icon arrow"></span>
                                                    </span>
                                                    <span class="button-text">Book Now</span>
                                                </a>
                                                <a href="viewRoomTypeDetail?roomTypeId=${r.getRoomTypeID()}" class="animated_link">
                                                    <strong>Details</strong>
                                                </a>
                                            </div>
                                            <!-- /box_item_footer -->
                                            <div id="jarallax-container-1" style="position: absolute; top: 0px; left: 0px; width: 100%; height: 100%; overflow: hidden; z-index: -100; clip-path: polygon(0px 0px, 100% 0px, 100% 100%, 0px 100%);"><div style="pointer-events: none; transform-style: preserve-3d; backface-visibility: hidden; position: fixed;"></div></div></div>
                                        <!-- /box_item_info -->
                                    </div>
                                    <!-- /col -->
                                </div>
                                <!-- /row -->
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>

                <!-- /row_list_version_1 -->

            </div>
            <!-- /container -->

            <div class="bg_white">
                <div class="container margin_120_95">
                    <div class="title center mb-5">
                        <small data-cue="slideInUp">Paradise Hotel</small>
                        <h2 data-cue="slideInUp" data-delay="100">Main Facilities</h2>
                    </div>
                    <div class="row mt-4">
                        <div class="col-xl-3 col-lg-6 col-md-6">
                            <div class="box_facilities no-border" data-cue="slideInUp">
                                <i class="customicon-private-parking"></i>
                                <h3>Private Parking</h3>
                                <p>Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam.</p>
                            </div>
                        </div>
                        <div class="col-xl-3 col-lg-6 col-md-6">
                            <div class="box_facilities" data-cue="slideInUp">
                                <i class="customicon-wifi"></i>
                                <h3>High Speed Wifi</h3>
                                <p>At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium.</p>
                            </div>
                        </div>
                        <div class="col-xl-3 col-lg-6 col-md-6">
                            <div class="box_facilities" data-cue="slideInUp">
                                <i class="customicon-cocktail"></i>
                                <h3>Bar & Restaurant</h3>
                                <p>Similique sunt in culpa qui officia deserunt mollitia animi, id est laborum et dolorum fuga.</p>
                            </div>
                        </div>
                        <div class="col-xl-3 col-lg-6 col-md-6">
                            <div class="box_facilities" data-cue="slideInUp">
                                <i class="customicon-swimming-pool"></i>
                                <h3>Swimming Pool</h3>
                                <p>Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus.</p>
                            </div>
                        </div>
                    </div>
                    <!-- /Row -->
                </div>
                <!-- /container-->
                <div class="marquee">
                    <div class="track">
                        <div class="content">&nbsp;Relax Enjoy Luxury Holiday Travel Discover Experience Relax Enjoy Luxury Holiday Travel Discover Experience Relax Enjoy Luxury Holiday Travel Discover Experience Relax Enjoy Luxury Holiday Travel Discover Experience</div>
                    </div>
                </div>
                <!-- /marquee-->
            </div>
            <!-- /bg_white -->

            <div class="container margin_120_95" id="booking_section">
                <div class="row justify-content-between">
                    <div class="col-xl-4">
                        <div data-cue="slideInUp">
                            <div class="title">
                                <small>Paradise Hotel</small>
                                <h2>Check Availability</h2>
                            </div>
                            <p>Mea nibh meis philosophia eu. Duis legimus efficiantur ea sea. Id placerat tacimates definitionem sea, prima quidam vim no. Duo nobis persecuti cu. </p>
                            <p class="phone_element no_borders"><a href="tel://423424234"><i class="bi bi-telephone"></i><span><em>Info and bookings</em>+41 934 121 1334</span></a></p>
                        </div>
                    </div>
                    <div class="col-xl-7">
                        <div data-cue="slideInUp" data-delay="200">
                            <div class="booking_wrapper">
                                <p id="daterangepicker-result" class="d-none"></p>
                                <input id="date_booking" type="hidden">
                                <div id="daterangepicker-embedded-container" class="embedded-daterangepicker clearfix mb-4"></div>
                                <div class="row">
                                    <div class="col-lg-6">
                                        <div class="custom_select">
                                            <select class="wide">
                                                <option>Select Room</option>
                                                <option>Double Room</option>
                                                <option>Deluxe Room</option>
                                                <option>Superior Room</option>
                                                <option>Junior Suite</option>
                                            </select>
                                        </div>
                                    </div>
                                    <div class="col-lg-6">
                                        <div class="row">
                                            <div class="col-6">
                                                <div class="qty-buttons mb-3 version_2">
                                                    <input type="button" value="+" class="qtyplus" name="adults_booking">
                                                    <input type="text" name="adults_booking" id="adults_booking" value="" class="qty form-control" placeholder="Adults">
                                                    <input type="button" value="-" class="qtyminus" name="adults_booking">
                                                </div>
                                            </div>
                                            <div class="col-6">
                                                <div class="mb-3 qty-buttons mb-3 version_2">
                                                    <input type="button" value="+" class="qtyplus" name="childs_booking">
                                                    <input type="text" name="childs_booking" id="childs_booking" value="" class="qty form-control" placeholder="Childs">
                                                    <input type="button" value="-" class="qtyminus" name="childs_booking">
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <!-- / row -->
                            <p class="text-end mt-5"><a href="#0" class="btn_1 outline">Book Now</a></p>
                        </div>
                    </div>
                    <!-- /col -->
                </div>
                <!-- /row -->
            </div>
            <!-- /container -->

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
                                <li><a href="searchRoomResult22.html">Rooms &amp; Suites</a></li>
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

    </body>
</html>