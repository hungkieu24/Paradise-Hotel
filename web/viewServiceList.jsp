<%-- 
    Document   : viewListService
    Created on : Jun 27, 2025, 5:37:41 PM
    Author     : KTC
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
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

        <!-- YOUR CUSTOM CSS -->
        <link href="css/custom.css" rel="stylesheet">
        <link href="css/viewServiceList.css" rel="stylesheet">
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


        <main>

            <div class="hero medium-height jarallax" data-jarallax data-speed="0.2">
                <img class="jarallax-img" src="img/loginBackground.jpg" alt="">
                <div class="wrapper opacity-mask d-flex align-items-center justify-content-center text-center animate_hero" data-opacity-mask="rgba(0, 0, 0, 0.5)">
                    <div class="container">
                        <small class="slide-animated one">Luxury Hotel Experience</small>
                        <h1 class="slide-animated two">Our Services</h1>
                    </div>
                </div>
            </div>
            <!-- /Background Img Parallax -->

            <div class="container margin_120_95" style="padding-top: 60px">

                <form id="search" action="viewServiceList" method="get" style="margin-bottom: 80px">
                    <!-- loc theo chi nhanhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh -->
                    <div class="filter-section">
                        <label for="branchId">Filter by Branch:</label>
                        <select name="branchId" id="branchId" onchange="this.form.submit()">
                            <option value="0" ${selectedBranchId == null || selectedBranchId == '0' ? 'selected' : ''}>All Branches</option>
                            <c:forEach var="branch" items="${listBranch}">
                                <option value="${branch.id}" ${selectedBranchId == branch.id ? 'selected' : ''}>${branch.name}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <!-- loc theo tiennnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn -->
                    <div class="filter-section">
                        <div class="form-group">
                            <label for="minPrice">Minimum price (VND)</label>
                            <input type="number" class="form-control" id="minPrice" name="minPrice" value="${param.minPrice}" placeholder="Minimum" min="0" step="100000">
                        </div>
                        <div class="form-group">
                            <label for="maxPrice">Maximum price (VND)</label>
                            <input type="number" class="form-control" id="maxPrice" name="maxPrice" value="${param.maxPrice}" placeholder="Maximum" min="0" step="100000">
                            <p class="form_error"></p>
                        </div>
                        <button type="submit" class="btn btn-primary w-100">Search</button>
                    </div>

                    <!-- loc theo keyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy -->
                    <div class="filter-section">
                        <div class="form-group">
                            <label for="keyword">Search by name</label>
                            <input type="text" class="form-control" id="keyword" name="keyword" value="${param.keyword}" placeholder="Enter keyword">
                        </div>
                        <button type="submit" class="btn btn-primary w-100">Search by Keyword</button>
                    </div>

                    <a href="viewServiceList" class="btn btn-primary w-100" style="grid-column: 1 / -1; margin-top: 20px;">Reset</a>
                </form>



                <div class="isotope-wrapper">
                    <div class="row justify-content-center">
                        <c:if test="${not empty listService}">
                            <c:forEach var="ls" items="${listService}">
                                <div class="item col-xl-4 col-lg-6">
                                    <a href="viewServiceDetail?serviceId=${ls.id}" class="box_contents" data-cue="slideInUp">
                                        <figure>
                                            <img src="${ls.imageUrl}" alt="${ls.name}" class="img-fluid">
                                            <em>${ls.status}</em>
                                        </figure>
                                        <div class="wrapper">
                                            <small>Service<span></span></small>
                                            <h2>${ls.name}</h2>
                                            <em>Read more</em>
                                        </div>
                                    </a>
                                </div>
                            </c:forEach>
                        </c:if>

                    </div>
                </div>
            </div>


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
            <path d="M50,1 a49,49 0 0,1 0,98 a49,49 0 0,1 0,-98"/>
            </svg>
        </div>
        <!-- /back to top -->


        <!-- COMMON SCRIPTS -->
        <script src="js/common_scripts.js"></script>
        <script src="js/common_functions.js"></script>
        <script src="phpmailer/validate.js"></script>
        <script src="./js/validationForm.js"></script>
        <!-- SPECIFIC SCRIPTS -->
        <script src="js/isotope.min.js"></script>
        <script>
                            $(function () {
                                "use strict";
                                $(window).on('load', function () {
                                    var $container = $('.isotope-wrapper');
                                    $container.isotope({itemSelector: '.item', layoutMode: 'masonry', });
                                });
                            });
        </script>

        <script>
            Validator({
                form: '#search',
                formGroupSelector: '.form-group',
                errorSelector: '.form_error',
                rules: [
                    Validator.minLessThanMax('#minPrice', '#maxPrice', 'Maximum price must be more than or equal to minimum price')
                ]
            });
        </script>
    </body>
</html>
