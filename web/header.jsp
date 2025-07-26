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
                           
                            
                            <li class="submenu">
                                <a href="viewRoomTypeList" class="show-submenu">View Room Type List</a>

                            </li>
                            
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
