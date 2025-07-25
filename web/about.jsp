<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="en" />

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta name="description" content="">
        <meta name="author" content="Ansonika">
        <title>PARADISE - About Us</title>

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
        <%@ include file="./header.jsp"%>

        <main>
            <div class="hero medium-height jarallax" data-jarallax data-speed="0.2">
                <img class="jarallax-img" src="img/registerbg.jpg" alt="">
                <div class="wrapper opacity-mask d-flex align-items-center justify-content-center text-center animate_hero" data-opacity-mask="rgba(0, 0, 0, 0.5)">
                    <div class="container">
                        <small class="slide-animated one">Luxury Hotel Experience</small>
                        <h1 class="slide-animated two">About Paradise Hotel</h1>
                    </div>
                </div>
            </div>
            <!-- /Background Img Parallax -->


            <div class="container margin_120_95" id="first_section">
                <div class="row justify-content-center">
                    <div class="col-lg-8">
                        <div class="title text-center mb-5">
                            <small>About Paradise Hotel</small>
                            <h2>Welcome to Paradise Hotel</h2>
                        </div>
                        <div class="about-content">
                            <p>
                                Paradise Hotel is dedicated to providing a unique, luxury hotel experience for all our guests. Nestled in a beautiful and convenient location, we offer tailored services and world-class amenities to make your stay truly unforgettable.
                            </p>
                            <p>
                                Our rooms and suites are designed for comfort and style, equipped with modern facilities and an elegant touch. Whether you are traveling for leisure or business, Paradise Hotel will be your home away from home.
                            </p>
                            <p>
                                <strong>Vision:</strong> To become the leading luxury hotel destination, delivering memorable moments and top-tier hospitality.<br>
                                <strong>Mission:</strong> To provide exceptional services, personalized experiences, and a welcoming atmosphere for all guests.
                            </p>
                            <p>
                                <em>Hung...the Owner</em>
                            </p>
                            <hr>
                            <div class="hotel-policy">
                                <h4>Terms, Policies & Rules</h4>
                                <ul>
                                    <li><strong>Check-in/Check-out:</strong> Standard check-in time is 8:00 AM and check-out is 12:00 PM. Early check-in or late check-out is subject to availability and may incur additional charges.</li>
                                    <li><strong>Payment Methods:</strong> We accept Visa, MasterCard, JCB, bank transfers, selected e-wallets, and direct payment at some partner hotels.</li>
                                    <li>
                                        <strong>Cancellation & Refunds:</strong> <br>
                                        - Cancellations within 12 hours: 100% refund the money you paid.<br>
                                        - Cancellations within 24 hours: 75% refund the money you paid.<br>
                                        - Cancellations after 2 days: No refund.<br>
                                        Refunds are processed within 1-2 business days after eligible cancellations and made via the original payment method.
                                    </li>                                    <li><strong>Security:</strong> Your payment information is protected according to PCI DSS standards. We do not store card information in our system. All transactions are SSL-secured.</li>
                                    <li><strong>Rank & Points:</strong> For every 100,000 VND spent, you earn 1 point. Membership levels:
                                        <ul>
                                            <li>Member: Spending from 0 VND</li>
                                            <li>Silver: Spending from 5,000,001 VND</li>
                                            <li>Gold: Spending from 10,000,001 VND</li>
                                            <li>VIP: Spending above 20,000,001 VND</li>
                                        </ul>
                                        Points are used to determine your rank and for future promotions.
                                    </li>
                                    <li><strong>Guest Behavior:</strong> Guests are expected to respect hotel property and other guests. Any violation may result in service refusal or penalty.</li>
                                    <li><strong>Support:</strong> For any issues regarding payment, booking, or rules, contact our hotline: <b>1900 9999</b> or email: <b>support@bookinghotel.vn</b> (8:00 AM - 10:00 PM, daily).</li>
                                </ul>
                                <p style="color: #e74c3c; font-weight: bold; font-size: 16px; margin-top: 18px;">
                                    <i class="fa fa-exclamation-triangle"></i>
                                    Attention: You will <u>not be eligible for a refund</u> if you violate our policies.
                                </p>
                            </div>
                        </div>

                        <!-- You can add more about or team section here if needed -->
                    </div>
                </div>
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
                            <li>Baker Street 567, Los Angeles 11023<br>California - US<br><br></li>
                            <li><strong><a href="#0">info@paradisehotel.com</a></strong></li>
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
                                <li><a href="index222.html">Home</a></li>
                                <li><a href="about.html">About Us</a></li>
                                <li><a href="searchRoomResult22.html">Rooms &amp; Suites</a></li>
                                <li><a href="news-1.html">News &amp; Events</a></li>
                                <li><a href="contacts.html">Contacts</a></li>
                                <li><a href="about.jsp ">Terms and Conditions</a></li>
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

        <!--chat AI-->
        <jsp:include page="chatUi.jsp"/>

        <!-- COMMON SCRIPTS -->
        <script src="js/common_scripts.js"></script>
        <script src="js/common_functions.js"></script>
        <script src="js/datepicker_inline.js"></script>
        <script src="phpmailer/validate.js"></script>
        <script src="js/toastMessage.js"></script>
    </body>
</html>