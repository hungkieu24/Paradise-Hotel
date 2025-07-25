<%-- 
    Document   : header
    Created on : Jun 2, 2025, 11:00:59 PM
    Author     : hungk
--%>


<header class="fixed_header menu_v4 submenu_version">
    <div class="layer"></div><!-- Opacity Mask -->
    <div class="container">
        <div class="row align-items-center">
            <div class="col-3">
                <a href="./homepage" class="logo_normal"><img src="img/logo.png" width="135" height="45" alt=""></a>
                <a href="./homepage" class="logo_sticky"><img src="img/logo_sticky.png" width="135" height="45" alt=""></a>
            </div>
            <div class="col-9">
                <div class="main-menu">
                    <a href="#" class="closebt open_close_menu"><i class="bi bi-x"></i></a>
                    <div class="logo_panel"><img src="img/logo_sticky.png" width="135" height="45" alt=""></div>
                    <nav id="mainNav">
                        <ul class="navBarList_Hompage">
                            <!--                                    <li class="submenu">
                                                                    <a href="#0" class="show-submenu">Home</a>
                                                                    <ul>
                                                                        <li><a href="index222.html">Home Video Bg</a></li>
                                                                        <li><a href="index-2.html">Home Carousel</a></li>
                                                                        <li><a href="index-3.html">Home FlexSlider</a></li>
                                                                        <li><a href="index-4.html">Home Youtube/Vimeo</a></li>
                                                                        <li><a href="index-5.html">Home Parallax</a></li>
                                                                        <li><a href="index-6.html">Home Parallax 2</a></li>
                                                                    </ul>
                                                                </li>-->
                            
                            <li class="submenu">
                                <a href="viewRoomTypeList" class="show-submenu">View Room Type List</a>
<!--                                <ul>
                                    <li><a href="searchRoomResult22.html">Room list 1</a></li>
                                    <li><a href="room-list-2.html">Room list 2</a></li>
                                    <li><a href="room-list-3.html">Room list 3</a></li>
                                    <li><a href="room-details.html">Room details</a></li>
                                    <li><a href="room-details-booking.html">Working Booking Request</a></li>
                                </ul>-->
                            </li>
                            <!--                                    <li class="submenu">
                                                                    <a href="#0" class="show-submenu">Other Pages</a>
                                                                    <ul>
                                                                        <li><a href="gallery.html">Masonry Gallery</a></li>
                                                                        <li><a href="restaurant.html">Restaurant</a></li>
                                                                        <li><a href="menu-of-the-day.html">Menu of the day</a></li>
                                                                        <li><a href="news-1.html">Blog</a></li>
                                                                        <li><a href="404.html">Error Page</a></li>
                                                                        <li><a href="modal-advertise-1.html">Modal Advertise</a></li>
                                                                        <li><a href="cookie-bar.html">GDPR Cookie Bar</a></li>
                                                                        <li><a href="coming-soon.html">Coming Soon</a></li>
                                                                        <li><a href="menu-2.html">Menu Version 2 <span class="custom_badge">Hot</span></a></li>
                                                                        <li><a href="menu-3.html">Menu Version 3</a></li>
                                                                        <li><a href="menu-4.html">Menu Version 4</a></li>
                                                                    </ul>
                                                                </li>-->
                            <li><a href="viewServiceList">View Service List</a></li>
                            <li><a href="about.jsp">About</a></li>
                            <c:if test="${sessionScope.user == null}">
                                <li><a href="login.jsp">Login</a></li>
                                <li><a href="register.jsp" class="btn_1">Register</a></li>
                            </c:if>
                            <c:if test="${sessionScope.user != null}">
                                <li><a href="editProfile">${sessionScope.user.getUsername()}</a></li>
                                <li>
                                    <a href="./editProfile">
                                        <img src="${sessionScope.user.getAvatar_url()}" alt="" class="top-act__avatar" />  
                                    </a>
                                </li>
                            </c:if>
                        </ul>
                    </nav>
                </div>
                <div class="hamburger_2 open_close_menu float-end">
                    <div class="hamburger__box">
                        <div class="hamburger__inner"></div>
                    </div>
                </div>
            </div>
        </div>
    </div><!-- container -->
</header>
