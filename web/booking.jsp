<%-- 
    Document   : booking
    Created on : Jun 21, 2025, 8:49:59 PM
    Author     : KTC
--%>


<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="en" />
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.*, model.*" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.CartItem" %>
<%@ page import="Model.RoomType" %>
<%@ page import="Model.Service" %>
<%@ page import="Model.LoyaltyPoint" %>

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
        <link rel="stylesheet" href="css/booking.css">
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
                        <h1 class="slide-animated two">Booking</h1>
                    </div>
                </div>
            </div>
            <!-- /Background Img Parallax -->


            <div class="checkout-container">
                <form action="booking" method="post" id="booking-form">
                    <input type="hidden" name="roomTypeId" value="${empty param.roomTypeId ? '' : param.roomTypeId}">
                    <input type="hidden" name="action" value="${param.action}">
                    <div class="left-column">

                        <!-- Customer info -->
                        <div class="customer-info">
                            <h3>Customer information</h3>
                            <div class="avatar-container">
                                <img class="avatar" src="${sessionScope.user.getAvatar_url()}" alt="Avatar"/>
                                <div>
                                    <label>Username</label>
                                    <input id="username" type="text" value="${sessionScope.user.getUsername()}" name="username" readonly>
                                </div>
                            </div>
                            <label>Email</label>
                            <input id="email" type="email" value="${sessionScope.user.getEmail()}" name="email" readonly>
                            <label>Phone number</label>
                            <input id="phonenumber" type="tel" value="${sessionScope.user.getPhonenumber()}" name="phonenumber" readonly>
                        </div>


                        <div class="booking-details">
                            <label for="checkIn">Check-in:</label>
                            <input type="datetime-local" id="checkIn" name="checkIn" required>

                            <label for="checkOut">Check-out:</label>
                            <input type="datetime-local" id="checkOut" name="checkOut" required>          
                        </div>

                        <!-- Services -->
                        <div style="display: block" class="services">
                            <h3 style="">Choose services</h3>
                            <div class="services-grid">
                                <c:forEach var="s" items="${listServices}">
                                    <label>
                                        ${s.name} - ${s.description} - 
                                        <fmt:formatNumber value="${s.price}" type="number" groupingUsed="true" maxFractionDigits="0" /> VND
                                        <input 
                                            type="number" 
                                            class="service-qty-input"
                                            name="serviceQuantity_${s.id}" 
                                            data-id="${s.id}"
                                            data-name="${s.name}"
                                            data-price="${s.price}"
                                            min="0" 
                                            max="${totalRoomQuantity}" 
                                            value="<c:out value='${selectedServiceMap[s.id] != null ? selectedServiceMap[s.id] : 0}' />"
                                            style="width: 60px; margin-left: 10px"
                                            />
                                    </label>
                                </c:forEach>
                            </div>
                        </div>
                        <!-- Special Request -->
                        <label for="note">Note:</label>
                        <textarea id="note" name="note" placeholder="Special request...">${preNote}</textarea>

                    </div>

                    <input type="hidden" name="selectedServiceIds" id="selectedServiceIds" />
                    <input type="hidden" name="totalServiceCost" id="totalServiceCost" />
                    <input type="hidden" name="totalRoomQuantity" id="totalRoomQuantity" value="${totalRoomQuantity}" />
                    <input type="hidden" name="discountPercent" id="discountPercent" value="${loyaltyPoint.discountPercent}" />
                    <input type="hidden" name="totalRoom" id="totalRoom" value="${totalRoom}" />
                    <input type="hidden" name="finalTotalPrice" id="finalTotalPrice" />

                    <div class="right-column">
                        <!-- Room Summary -->
                        <div class="summary">
                            <h3>Total cost of rooms</h3>
                            <c:if test="${not empty singleRoom}">
                                <ul class="room-summary">
                                    <li class="room-item">
                                        <span class="room-name">${singleRoom.name} x 1</span>
                                        <span class="room-price">
                                            <fmt:formatNumber value="${singleRoom.base_price}" type="number" groupingUsed="true" maxFractionDigits="0" /> VND
                                        </span>
                                    </li>
                                </ul
                            </c:if>

                            <c:if test="${not empty listCartItem}">
                                <ul class="room-summary">
                                    <c:forEach var="r" items="${sessionScope.listCartItem}">
                                        <li class="room-item">
                                            <span class="room-name">${r.roomType.name} x ${r.quantity}</span>
                                            <span class="room-price">
                                                <fmt:formatNumber value="${r.roomType.base_price}" type="number" groupingUsed="true" maxFractionDigits="0" /> VND
                                            </span>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </c:if>

                            <strong>
                                <ul>
                                    <li>
                                        <span style="color: black">Total room cost:  </span> 
                                        <span style="color: black"><fmt:formatNumber value="${totalRoom}" type="number" groupingUsed="true" maxFractionDigits="0" /> VND</span>
                                    </li>
                                </ul>
                            </strong>

                            <hr>

                            <div class="selected-services-summary">
                                <h3>Selected Services</h3>
                                <ul id="service-list"></ul>
                            </div>

                            <div class="service-total" style="margin-top: 10px;">
                                <strong>Total service cost: <span id="service-total">0 VND</span></strong>
                            </div>

                            <br>
                            <hr>

                            <div class="rank">
                                <p>
                                    <strong>Your Rank: ${loyaltyPoint.level}</strong> 
                                    (${loyaltyPoint.discountPercent}% discount)
                                </p>
                                <p>Discount applied: <span id="discount-applied">0 VND</span></p>
                            </div>

                            <div class="total">
                                <strong>Total after discount: 
                                    <span id="final-total">0 VND</span>
                                </strong>
                            </div>

                            <!-- Submit Button inside summary -->
                            <form id="payment-form" action="vnpayajax" method="post">
                                <input type="hidden" name="bookingId" id="bookingId" value="">
                                <button type="submit" class="btn btn-danger">Book now</button>
                            </form>

                        </div>
                    </div>
                </form>
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
            <path d="M50,1 a49,49 0 0,1 0,98 a49,49 0 0,1 0,-98" />
            </svg>
        </div>
        <!-- /back to top -->


        <!-- COMMON SCRIPTS -->
        <script src="js/common_scripts.js"></script>
        <script src="js/common_functions.js"></script>
        <script src="./js/validationForm.js"></script>

        <script>
            document.addEventListener("DOMContentLoaded", function () {
                const form = document.getElementById("booking-form");
                const inputs = document.querySelectorAll(".service-qty-input");
                const selectedServicesList = document.getElementById("service-list");
                const totalCostDisplay = document.getElementById("service-total");
                const selectedServiceIdsInput = document.getElementById("selectedServiceIds");
                const totalServiceCostInput = document.getElementById("totalServiceCost");
                let hasServiceQtyError = false;

                function updateSelectedServices() {
                    let total = 0;
                    let selectedServices = [];
                    selectedServicesList.innerHTML = "";

                    const maxQuantity = parseInt(document.getElementById("totalRoomQuantity")?.value || "1");
                    hasServiceQtyError = false;

                    inputs.forEach(input => {
                        const qty = parseInt(input.value || "0");
                        const id = input.dataset.id;
                        const name = input.dataset.name;
                        const price = parseFloat(input.dataset.price || "0");

                        // Validate
                        if (qty < 0 || qty > maxQuantity) {
                            input.style.border = "2px solid red";
                            hasServiceQtyError = true;
                        } else {
                            input.style.border = "";
                        }

                        if (qty > 0 && qty <= maxQuantity) {
                            const cost = qty * price;
                            total += cost;
                            selectedServices.push(id + ":" + qty);

                            const li = document.createElement("li");
                            li.textContent = name + " x " + qty + " = " + cost.toLocaleString("vi-VN") + " VND ";
                            selectedServicesList.appendChild(li);
                        }
                    });

                    if (hasServiceQtyError) {
                        showToast("⚠ Please input number of service from 0 to " + maxQuantity, "#e53935");
                    }

                    totalCostDisplay.textContent = total.toLocaleString("vi-VN") + " VND";
                    selectedServiceIdsInput.value = selectedServices.join(",");
                    totalServiceCostInput.value = total;

                    // Discount and Final total
                    const totalRoomCost = parseFloat(document.getElementById("totalRoom").value || "0");
                    const discountPercent = parseFloat(document.getElementById("discountPercent").value || "0");

                    const totalAll = totalRoomCost + total;
                    const discountAmount = Math.round(totalAll * discountPercent / 100);
                    const totalAfterDiscount = totalAll - discountAmount;

                    document.getElementById("discount-applied").textContent = discountAmount.toLocaleString("vi-VN") + " VND";
                    document.getElementById("final-total").textContent = totalAfterDiscount.toLocaleString("vi-VN") + " VND";
                    document.getElementById("finalTotalPrice").value = totalAfterDiscount;
                }

                // Attach input event
                inputs.forEach(input => input.addEventListener("input", updateSelectedServices));
                updateSelectedServices();

                // Validate on form submit
                form.addEventListener("submit", function (e) {
                    updateSelectedServices();
                    if (hasServiceQtyError) {
                        showToast("❌ Please fix service quantity errors before booking", "#e53935");
                        return;
                    }
                });
            });

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

        <div id="toast-message" class="toast hidden">Thông báo mẫu</div>

        <!-- Submit logic script -->
        <script>
            document.addEventListener("DOMContentLoaded", function () {
                const form = document.getElementById("booking-form");

                form.addEventListener("submit", function (e) {
                    e.preventDefault();

                    const formData = new FormData(form);
                    const params = new URLSearchParams();
                    formData.forEach((value, key) => {
                        params.append(key, value);
                    });

                    fetch("booking", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/x-www-form-urlencoded"
                        },
                        body: params
                    })
                            .then(res => res.json())
                            .then(data => {
                                console.log(data);
                                if (data.status === "success") {
                                    var amountToPay = document.getElementById('finalTotalPrice').value;
                                    var bookingId = data.bookingId; // Lấy từ response

                                    var form = document.createElement('form');
                                    form.method = 'POST';
                                    form.action = 'vnpayajax';

                                    var input1 = document.createElement('input');
                                    input1.type = 'hidden';
                                    input1.name = 'amountToPay';
                                    input1.value = amountToPay;
                                    form.appendChild(input1);

                                    var input2 = document.createElement('input');
                                    input2.type = 'hidden';
                                    input2.name = 'bookingId';
                                    input2.value = bookingId;
                                    form.appendChild(input2);

                                    document.body.appendChild(form);
                                    form.submit();
                                } else if (data.status === "error" && data.message) {
                                    showToast("❌ " + data.message, "#e53935");
                                } else {
                                    showToast("❌ Booking failed!", "#e53935");
                                }
                            })
                            .catch(error => {
                                console.error("Error:", error);
                                showToast("❌ Error occurred", "#e53935");
                            });
                });
            });
        </script>
    </body>
</html>

