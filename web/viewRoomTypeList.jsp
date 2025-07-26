<%-- 
    Document   : viewRoomTypeList
    Created on : Jun 6, 2025, 11:40:04 PM
    Author     : KTC
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN" />
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
        <link rel="stylesheet" href="css/viewRoomTypeList.css">
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

            <c:remove var="message" scope="session" />
            <c:remove var="messageType" scope="session" />
        </c:if>

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
                        <h1 class="slide-animated two">Our room types</h1>
                    </div>
                </div>
            </div>
            <!-- /Background Img Parallax -->

            <div class="container margin_120_95 pb-0" id="first_section">

                <!-- phan search theo gia tien -->
                <div class="row mb-4">
                    <div class="col-12">
                        <form  id="search" action="./viewRoomTypeList" method="get" class="search-form" novalidate>
                            <div class="row align-items-center">
                                <div class="col-md-3">
                                    <div class="form-group">
                                        <label for="minPrice">Minimum price (VND)</label>
                                        <input type="number" class="form-control" id="minPrice" name="minPrice" 
                                               value="${param.minPrice}" placeholder="Minimum" min="0" step="100000">
                                    </div>  
                                </div>
                                <div class="col-md-3">
                                    <div class="form-group">
                                        <label for="maxPrice">Maximum price (VND)</label>
                                        <input type="number" class="form-control" id="maxPrice" name="maxPrice" 
                                               value="${param.maxPrice}" placeholder="Maximum" min="0" step="100000">
                                        <p style="color: red"class="form_error"></p>

                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="form-group">
                                        <label>Check in / Check out</label>
                                        <input class="form-control" type="text" id="dates" name="dates" value="${param.dates}" placeholder="YYYY-MM-DD - YYYY-MM-DD" readonly="readonly" required>
                                    </div>
                                </div>  
                                <div class="col-md-3">
                                    <div class="form-group">
                                        <label>&nbsp;</label> 
                                        <button type="submit" class="btn btn-primary w-100">Search</button>
                                    </div>
                                </div>

                            </div>
                        </form>
                    </div>
                </div>

                <!-- phan search theo gia tien -->
                <div class="row mb-4">
                    <div class="col-12">
                        <form id="search" action="./viewRoomTypeList" method="get" class="search-form" novalidate>
                            <div class="row g-2 align-items-center">
                                <div class="col-md-6">
                                    <a href="viewRoomTypeList" class="btn btn-primary w-100">Reset</a>
                                </div>
                                <div class="col-md-6">
                                    <a href="viewCart" class="btn btn-primary w-100">🛒 Cart</a>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>

                <div class="container margin_120_0">
                    <c:forEach items="${listRoomType}" var="r" varStatus="status">


                        <div class="row_list_version_2 ${status.index % 2 != 0 ? 'inverted' : ''}">
                            <div class="row g-0 align-items-center">
                                <div class="col-xl-8 ${status.index % 2 != 0 ? 'order-xl-2' : ''}">
                                    <div class="owl-carousel owl-theme carousel_item_1 kenburns rounded-img">
                                        <div class="item">
                                            <img src="${r.getImage_url()}" alt=""/>
                                        </div>
                                    </div>
                                    <!-- /carousel -->
                                </div>
                                <div class="col-xl-4 ${status.index % 2 != 0 ? 'order-xl-1' : ''}">
                                    <div class="box_item_info" data-jarallax-element="-25">
                                        <small>From 
                                            <fmt:formatNumber value="${r.getBase_price()}" type="currency"/>
                                            /night</small>
                                        <h2>${r.getName()}</h2>
                                        <h2>${r.branch.name}</h2>
                                        <p>${r.getDescription()}</p>
                                        <div class="facilities clearfix">
                                            <ul>
                                                <li>
                                                    <i class="customicon-double-bed"></i> King Size Bed
                                                </li>
                                                <li>
                                                    <i class="customicon-television"></i> 32 Inc TV
                                                </li>
                                            </ul>
                                        </div>
                                        <div class="box_item_footer d-flex align-items-center justify-content-between">
                                            <a href="booking?roomTypeId=${r.getRoomTypeID()}&action=oneRoom" 
                                               onclick="return validateDates(event);" 
                                               class="btn_4 learn-more">


                                                <span class="circle">
                                                    <span class="icon arrow"></span>
                                                </span>
                                                <span class="button-text">Book Now</span>
                                            </a>
                                            <a href="javascript:void(0);" 
                                               onclick="validateDates(event, ${r.getRoomTypeID()})" 
                                               class="btn_4 learn-more">                                                <span class="circle"><span class="icon arrow"></span></span>
                                                <span class="button-text">Add Cart</span>
                                            </a>
                                            <a href="./viewRoomTypeDetail?roomTypeId=${r.getRoomTypeID()}" class="animated_link">
                                                <strong>Read more</strong>
                                            </a>
                                        </div>
                                        <!-- /box_item_footer -->
                                    </div>
                                    <!-- /box_item_info -->
                                </div>
                                <!-- /col -->
                            </div>
                            <!-- /row -->
                        </div>
                        <!-- /row_list -->
                    </c:forEach>
                </div>
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
        <script src="./js/validationForm.js"></script>
        <script src="./js/toastMessage.js"></script>
        <script>
                                                   Validator({
                                                       form: '#search',
                                                       formGroupSelector: '.form-group',
                                                       errorSelector: '.form_error',
                                                       rules: [
                                                           Validator.minLessThanMax('#minPrice', '#maxPrice', 'Maximum price must be more than or equal to minimum price')
                                                       ],
                                                       onsubmit: function (formValue) {
                                                           document.querySelector('#search').submit();
                                                       }
                                                   })
        </script>
        <script>
            function addToCart(roomTypeId) {
                fetch('<%=request.getContextPath()%>/addToCart?roomTypeId=' + roomTypeId)
                        .then(response => response.json())
                        .then(data => {
                            if (data.status === "success") {
                                showToast("✅ " + data.message);
                            } else if (data.status === "exists") {
                                showToast("ℹ️ " + data.message, "#ff9800");
                            } else {
                                showToast("❌ Cannot add to cart", "#e53935");
                            }
                        })
                        .catch(error => {
                            console.error("Error:", error);
                            showToast("❌ An error occurred");
                        });
            }
        </script>
        <div id="toast-message" class="toast hidden">Thông báo mẫu</div>
        <script>
            function showToast(message, color = "#4caf50") {
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
            function validateDates(event, roomTypeId = null) {
                const dates = document.getElementById("dates").value.trim();

                if (!dates || dates.indexOf(" - ") === -1) {
                    showToast("Please select Check-in and Check-out dates first.", color = "#ff0000");
                    event.preventDefault(); // chặn hành động mặc định
                    return false;
                }

                // Nếu là addToCart
                if (roomTypeId !== null) {
                    addToCart(roomTypeId);
            }
            }
        </script>

    </body>
</html>
