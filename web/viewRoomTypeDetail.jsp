<%-- 
    Document   : viewRoomTypeDetail
    Created on : Jun 6, 2025, 10:40:31 PM
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
        <link rel="stylesheet" href="css/view_feedback.css">
        <!-- YOUR CUSTOM CSS -->
        <link href="css/custom.css" rel="stylesheet">
    </head>

    <body> 

        <div id="preloader">
            <div data-loader="circle-side"></div>
        </div><!-- /Page Preload -->

        <div class="layer"></div><!-- Opacity Mask -->

        <header class="reveal_header">
            <div class="container">
                <div class="row align-items-center">
                    <div class="col-6">
                        <a href="homepage" class="logo_normal"><img src="img/logo.png" width="135" height="45" alt=""></a>
                        <a href="index.html" class="logo_sticky"><img src="img/logo_sticky.png" width="135" height="45" alt=""></a>
                    </div>

                    <div class="col-6">
                        <nav class="second_nav">
                            <ul>
                                <li><a href="#booking_section" class="btn_1 btn_scrollto">Book Now</a></li>
                                <li>
                                    <div class="hamburger_2 open_close_nav_panel">
                                        <div class="hamburger__box">
                                            <div class="hamburger__inner"></div>
                                        </div>
                                    </div>
                                </li>
                            </ul>
                        </nav>
                    </div>
                </div>
            </div><!-- container -->
        </header><!-- End Header -->

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

            <div class="hero full-height jarallax" data-jarallax data-speed="0.2">
                <img class="jarallax-img kenburns" src="${roomType.getImage_url()}" alt="">
                <div class="wrapper opacity-mask d-flex align-items-center  text-center animate_hero" data-opacity-mask="rgba(0, 0, 0, 0.5)">
                    <div class="container">
                        <div class="row justify-content-center">
                            <div class="col-lg-8">
                                <small class="slide-animated one">Luxury Hotel Experience</small>
                                <h1 class="slide-animated two">${roomType.getName()}</h1>
                                <p class="slide-animated three">
                                    From 
                                    <fmt:formatNumber value="${roomType.getBase_price()}" type="number" groupingUsed="true" maxFractionDigits="0" />
                                    VND /night
                                </p>
                            </div>
                        </div>
                    </div>
                    <div class="mouse_wp slide-animated four">
                        <a href="#first_section" class="btn_explore">
                            <div class="mouse"></div>
                        </a>
                    </div>
                    <!-- / mouse -->
                </div>
            </div>
            <!-- /Background Img Parallax -->

            <div class="bg_white" id="first_section">
                <div class="container margin_120_95">
                    <div class="row justify-content-between">
                        <div class="col-lg-4">
                            <div class="title">
                                <small>Luxury Experience</small>
                                <h2>${roomType.getDescription()}</h2>
                            </div>
                            <p>The dark wood panelling and furnishings, deluxe red-draped four-poster bed, and magnificent black stone bathroom evoke the charm of a secluded Sierra Nevada getaway. The intimate scale and finish give the room a distinctly personal feel.</p>
                            <p>The dark wood panelling and furnishings, deluxe red-draped four-poster bed, and magnificent black stone bathroom evoke the charm.</p>
                        </div>
                        <div class="col-lg-6">
                            <div class="room_facilities_list">
                                <ul data-cues="slideInLeft">
                                    <li><i class="icon-hotel-double_bed_2"></i> King Size Bed</li>
                                    <li><i class="icon-hotel-safety_box"></i> Safety Box</li>
                                    <li><i class="icon-hotel-patio"></i>Balcony</li>
                                    <li><i class="icon-hotel-tv"></i> 32 Inch TV</li>
                                    <li><i class="icon-hotel-disable"></i> Disable Access</li>
                                    <li><i class="icon-hotel-dog"></i> Pet Allowed</li>
                                    <li><i class="icon-hotel-bottle"></i> Welcome Bottle</li>
                                    <li><i class="icon-hotel-wifi"></i> Wifi / Netflix access</li>
                                    <li><i class="icon-hotel-hairdryer"></i> Air Dryer</li>
                                    <li><i class="icon-hotel-condition"></i> Air Condition</li>
                                    <li><i class="icon-hotel-loundry"></i>Loundry Service</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                    <!-- /row -->
                </div>
                <!-- /container -->
            </div>
            <!-- /bg_white -->

            <div class="bg_white add_bottom_120">
                <div class="container-fluid p-lg-0">
                    <div data-cues="zoomIn">
                        <div class="owl-carousel owl-theme carousel_item_centered kenburns rounded-img">
                            <div class="item">
                                <img src="img/room2.jpg" alt=""/>
                            </div>
                            <div class="item">
                                <img src="img/room3.jpg" alt=""/>
                            </div>
                            <div class="item">
                                <img src="img/room4.jpg" alt=""/>
                            </div>
                            <div class="item">
                                <img src="img/room5.jpg" alt=""/>
                            </div>
                        </div>
                    </div>
                    <div class="text-center mt-5">
                        <a class="btn_1 outline" data-fslightbox="gallery_1" data-type="image" href="img/room2.jpg">FullScreen Gallery</a>
                        <a data-fslightbox="gallery_1" data-type="image" href="img/room1.jpg"></a>
                        <a data-fslightbox="gallery_1" data-type="image" href="img/room3.jpg"></a>
                        <a data-fslightbox="gallery_1" data-type="image" href="img/room4.jpg"></a>
                        <a data-fslightbox="gallery_1" data-type="image" href="img/room5.jpg"></a>
                    </div>
                </div>
            </div>

            <div class="bg_white">
                <div class="container margin_120_95">
                    <div data-cue="slideInUp">
                        <div class="title">
                            <small>Paradise Hotel</small>
                            <h2>Similar Rooms</h2>
                        </div>
                        <div class="row" data-cues="slideInUp" data-delay="800">
                            <c:forEach items="${listSimilarRoom}" var="r">

                                <div class="col-xl-4 col-lg-6 col-md-6 col-sm-6">
                                    <a href="./viewRoomTypeDetail?roomTypeId=${r.getRoomTypeID()}" class="box_cat_rooms">
                                        <figure>
<!--                                            <div class="background-image" data-background="url(${r.getImage_url()})"></div>-->
                                            <div class="background-image" data-background="url(${r.getImage_url()}"></div>
                                            <div class="info">
                                                <small>
                                                    From 
                                                    <fmt:formatNumber value="${r.getBase_price()}" type="number" groupingUsed="true" maxFractionDigits="0" />
                                                    VND /night
                                                </small>
                                                <h3>${r.getName()}</h3>
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
            <!-- /bg_white -->

            <div class="container margin_120_95" id="booking_section">
                <div class="row justify-content-between">

                    <div id="feedback-container">
                        <h3 class="mb-3">Feedback History</h3>
                        <c:forEach var="feedback" items="${listFeedback}"> 

                            <div class="review_card">
                                <div class="row">
                                    <div class="col-md-2 user_info">
                                        <figure><img class="avatar" src="${feedback.userAvatarUrl}" alt="Profile Avatar"/></figure>
                                        <h5>${feedback.username}</h5>
                                    </div>
                                    <div class="col-md-10 review_content">
                                        <div class="clearfix mb-3">
                                            <span class="rating">${feedback.rating}<small>★</small> <strong>Rating average</strong></span>
                                            <em><fmt:formatDate value="${feedback.created_at}" pattern="dd-MM-yyyy HH:mm:ss"/></em>
                                        </div>
                                        <h4 style="word-wrap: break-word; overflow-wrap: break-word; word-break: break-word; white-space: pre-wrap;">${feedback.comment}</h4>


                                        <c:if test="${not empty user and user.id eq feedback.user_id}">

                                            <div class="feedback-actions" style="padding-left: 920px; padding-bottom: 30px;">
                                                <button type="button"
                                                        class="btn btn-warning btn-sm"
                                                        onclick="openEditModal('${feedback.id}', '${feedback.rating}', `${feedback.comment}`, '${feedback.image_url}')">
                                                    Edit
                                                </button>


                                                <form action="DeleteFeedbackServlet" method="post" style="display:inline;" onsubmit="return confirm('Are you sure you want to delete this feedback?');">
                                                    <input type="hidden" name="feedbackId" value="${feedback.id}" />
                                                    <input type="hidden" name="roomTypeId" value="${roomTypeId}" />
                                                    <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                                                </form>
                                            </div>
                                        </c:if>



                                    </div>
                                </div>

                            </div>

                        </c:forEach>
                        <!-- EDIT FEEDBACK MODAL -->
                        <div id="editFeedbackModal" class="modal" style="display: none;">
                            <div class="modal-content" style="width: 500px; padding: 20px; border-radius: 10px; background: #fff; position: relative;">
                                <span class="close" onclick="closeEditModal()" style="position: absolute; top: 10px; right: 20px; font-size: 24px; cursor: pointer;">&times;</span>
                                <h2 style="margin-bottom: 20px;">Edit Feedback</h2>
                                <form id="editFeedbackForm" action="EditFeedbackServlet" method="post">
                                    <input type="hidden" name="feedbackId" id="editFeedbackId" />
                                    <input type="hidden" name="roomTypeId" value="${roomTypeId}" />
                                    
                                    <label>Overall Rating (1-5 Stars)</label>
                                    <div class="row">
                                <div class="col-sm-6">
                                    <div class="form-floating mb-4">
                                        
                                    <input
                                        class="form-control"
                                        type="range"
                                        id="editRating"
                                        name="rating"
                                        min="1"
                                        max="5"
                                        step="1"
                                        value="3"    
                                        required
                                        />
                                    <label for="star_rating">Rating (stars)</label>
                                    </div>
                                </div>

                                <div class="col-sm-6">
                                    <div class="form-floating mb-4">
                                        <div
                                            id="star_display"
                                            style="font-size: 1.5rem; color: gold; cursor: context-menu"
                                            ></div>
                                    </div>
                                </div>

                                    <label>Detailed Comments</label>
                                    <textarea class="form-control" placeholder="Message" id="editComment" name="comment" required></textarea>

                                    <div style="margin-top: 20px; text-align: right;">
                                        <button type="button" class="btn btn-secondary" onclick="closeEditModal()">Close</button>
                                        <button type="submit" class="btn btn-primary">Save Changes</button>
                                    </div>
                                </form>
                            </div>
                        </div>

                        
                    </div>
                                    <div class="pagination">
                            <c:if test="${currentPage > 1}">
                                <a href="?roomTypeId=${roomTypeId}&&page=${currentPage - 1}"  class="prev"> Previous</a>
                            </c:if>

                            <c:forEach var="i" begin="1" end="${totalPages}">
                                <a href="?roomTypeId=${roomTypeId}&&page=${i}" class="${i == currentPage ? 'active' : ''}">${i}</a>
                            </c:forEach>

                            <c:if test="${currentPage < totalPages}">
                                <a href="?roomTypeId=${roomTypeId}&&page=${currentPage + 1}" class="next">Next</a>
                            </c:if>
                        </div>
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
        <script src="js/datepicker_inline.js"></script>
        <script src="phpmailer/validate.js"></script>
        <script>
                                            // Progress bars animation
                                            $(function () {
                                                "use strict";
                                                var $section = $('#reviews');
                                                $(window).on('scroll', function (ev) {
                                                    var scrollOffset = $(window).scrollTop();
                                                    var containerOffset = $section.offset().top - window.innerHeight;
                                                    if (scrollOffset > containerOffset) {
                                                        $(".progress-bar").each(function () {
                                                            var each_bar_width = $(this).attr('aria-valuenow');
                                                            $(this).width(each_bar_width + '%');
                                                        });
                                                    }
                                                });
                                            });
        </script>
        <script>
            $(document).ready(function () {
                $(document).on('click', '.pagination a', function (e) {
                    e.preventDefault();
                    var url = $(this).attr('href');

                    $.ajax({
                        url: url,
                        type: 'GET',
                        headers: {'X-Requested-With': 'XMLHttpRequest'},
                        success: function (data) {
                            // lấy ra phần feedback-container từ kết quả trả về
                            var newContent = $(data).find('#feedback-container').html();
                            $('#feedback-container').html(newContent);
                        },
                        error: function () {
                            alert("Error loading feedbacks.");
                        }
                    });
                });
            });
        </script>
        <script>
            function openEditModal(feedbackId, rating, comment, imageUrl) {
                document.getElementById('editFeedbackId').value = feedbackId;
                document.getElementById('editRating').value = rating;
                document.getElementById('editComment').value = comment;
                document.getElementById('editFeedbackModal').style.display = 'flex';
                updateStars(rating);
            }

            function closeEditModal() {
                document.getElementById('editFeedbackModal').style.display = 'none';
            }
        </script>
        <script>
                            $(document).ready(function () {
                                

                                // Khởi tạo hiển thị sao ban đầu
                                updateStars($("#editRating").val());

                                // Cập nhật khi thay đổi
                                $("#editRating").on("input change", function () {
                                    updateStars($(this).val());
                                });
                            });
    </script>
    <script>
        function updateStars(value) {
                                    let stars = "";
                                    for (let i = 0; i < value; i++) {
                                        stars += "⭐";
                                    }
                                    $("#star_display").html(stars);
                                }
    </script>
    </body>
</html>
