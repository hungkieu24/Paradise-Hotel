<%-- 
    Document   : viewRoomTypeList
    Created on : Jun 6, 2025, 11:40:04 PM
    Author     : KTC
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="Model.RoomType" %>
<%@ page import="Model.CartItem" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
        <link rel="stylesheet" href="css/cart.css">
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

        <main style="padding-bottom: 1px;">
            <div class="hero medium-height jarallax" data-jarallax data-speed="0.2">
                <img class="jarallax-img" src="img/loginBackground.jpg" alt="">
                <div class="wrapper opacity-mask d-flex align-items-center justify-content-center text-center animate_hero" data-opacity-mask="rgba(0, 0, 0, 0.5)">
                    <div class="container">
                        <small class="slide-animated one">Luxury Hotel Experience</small>
                        <h1 class="slide-animated two">Your Shopping Cart</h1>
                    </div>
                </div>
            </div>

            <!-- /Background Img Parallax -->

            <div class="cart-container">
                <h2>Your Shopping Cart</h2>

                <c:choose>
                    <c:when test="${empty sessionScope.cart}">
                        <div style="text-align: center;" class="empty-message">🛒 Your cart is empty.</div>
                    </c:when>

                    <c:otherwise>
                        <c:set var="grandTotal" value="0" scope="page" />

                        <c:forEach var="item" items="${sessionScope.cart}">

                            <c:set var="roomType" value="${item.roomType}" />
                            <c:set var="total" value="${item.quantity * roomType.base_price}" />
                            <c:set var="grandTotal" value="${grandTotal + total}" scope="page" />
                            <c:set var="branchId" value="${roomType.branch.id}" />  

                            <div class="cart-item row cart-row" data-price="${roomType.base_price}" data-branchid="${branchId}" >
                                <div class="col select-room">
                                    <input type="checkbox"
                                           id="checkbox"
                                           class="roomtype-checkbox"
                                           data-roomtypeid="${roomType.roomTypeID}"
                                           data-branchid="${roomType.branch.id}" 
                                           name="roomTypeId" value="${roomType.roomTypeID}"
                                           data-quantity="${item.quantity}"
                                           />
                                </div>

                                <div>
                                    <img src="${roomType.image_url}" alt="Room Image" />
                                </div>

                                <div class="col details">
                                    <h3>${roomType.name}</h3>
                                    <fmt:formatNumber value="${roomType.base_price}" type="number" groupingUsed="true" maxFractionDigits="0" /> VND/night
                                </div>

                                <div class="col quantity">
                                    <div class="quantity-wrapper" data-roomtypeid="${roomType.roomTypeID}">
                                        <button type="button" class="qty-btn minus">-</button>
                                        <input id="quanlity" type="number" class="qty-input" value="${item.quantity}" min="1" max="5" readonly>
                                        <button type="button" class="qty-btn plus">+</button>
                                    </div>
                                </div>

                                <div class="col total item-total">
                                    <fmt:formatNumber value="${total}" type="number" groupingUsed="true" maxFractionDigits="0" /> VND
                                </div>


                                <div class="col actions">
                                    <a href="removeFromCart?roomTypeId=${roomType.roomTypeID}" class="btn-remove">Remove</a>
                                </div>
                            </div>
                        </c:forEach>


                        <div class="cart-summary">
                            <div class="cart-summary-header">
                                <div class="cart-summary-total">
                                    <strong>Grand Total:</strong>
                                    <span id="grand-total">
                                        <fmt:formatNumber value="${grandTotal}" type="number" groupingUsed="true" maxFractionDigits="0" />
                                        VND
                                    </span>
                                </div>
                                <a href="viewRoomTypeList" class="btn-back">Back</a>
                            </div>

                            <form id="proceed" action="booking">
                                <input type="hidden" id="selectedRoomList" name="selectedRoomList" value="">
                                <input type="hidden" id="quanlitySend" name="quanlitySend" value="">
                                <button type="button" onclick="selectedRoomList1()" class="btn-proceed">Proceed to Booking</button>
                            </form>
                        </div>



                    </c:otherwise>
                </c:choose>
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
        <script src="js/datepicker_inline.js"></script>
        <script src="./js/validationForm.js"></script>
        <script>
                                    document.addEventListener("DOMContentLoaded", function () {
                                        function updateCartTotal() {
                                            let grandTotal = 0;

                                            document.querySelectorAll('.cart-item').forEach(item => {
                                                const price = parseFloat(item.dataset.price);
                                                const quantity = parseInt(item.querySelector('.qty-input').value);
                                                const total = price * quantity;

                                                item.querySelector('.item-total').textContent = total.toLocaleString() + " VND";
                                                grandTotal += total;
                                            });

                                            document.getElementById('grand-total').textContent = grandTotal.toLocaleString() + " VND";
                                        }

                                        updateCartTotal();
                                    });
        </script>

        <script>
            document.addEventListener("DOMContentLoaded", function () {
                const checkboxes = document.querySelectorAll(".roomtype-checkbox");

                function updateItemTotal(row) {
                    const price = parseFloat(row.dataset.price);
                    const quantity = parseInt(row.querySelector(".qty-input").value);
                    const total = price * quantity;

                    const formatter = new Intl.NumberFormat('vi-VN');
                    row.querySelector(".item-total").textContent = formatter.format(total) + " VND";
                }

                function updateGrandTotal() {
                    let grandTotal = 0;
                    const checked = document.querySelectorAll(".roomtype-checkbox:checked");

                    checked.forEach(cb => {
                        const row = cb.closest(".cart-row");
                        const price = parseFloat(row.dataset.price);
                        const quantity = parseInt(row.querySelector(".qty-input").value);
                        grandTotal += price * quantity;
                    });

                    const formatter = new Intl.NumberFormat('vi-VN');
                    document.getElementById("grand-total").innerText = formatter.format(grandTotal) + " VND";
                }

                function updateCheckboxStates() {
                    const checked = [...checkboxes].filter(cb => cb.checked);
                    if (checked.length === 0) {
                        checkboxes.forEach(cb => cb.disabled = false);
                    } else {
                        const selectedBranchId = checked[0].dataset.branchid;
                        checkboxes.forEach(cb => {
                            if (cb.dataset.branchid !== selectedBranchId) {
//                                cb.disabled = true;
                                cb.checked = false;
                            } else {
                                cb.disabled = false;
                            }
                        });
                    }
                    updateGrandTotal();
                }

                // ⚠️ Thêm xử lý click để chặn chọn khác branch
                checkboxes.forEach(cb => {
                    cb.addEventListener("click", function (e) {
                        const checked = [...checkboxes].filter(c => c.checked);
                        if (checked.length > 0) {
                            const selectedBranchId = checked[0].dataset.branchid;
                            if (cb.dataset.branchid !== selectedBranchId) {
                                e.preventDefault(); // Ngăn không cho chọn
                                showToast("❌ You can only choose rooms from one branch!", "#e53935");
                            }
                        }
                    });

                    cb.addEventListener("change", updateCheckboxStates);
                });

                // Nút tăng/giảm
                document.querySelectorAll(".qty-btn").forEach(function (btn) {
                    btn.addEventListener("click", function () {
                        const wrapper = btn.closest(".quantity-wrapper");
                        const input = wrapper.querySelector(".qty-input");
                        const row = btn.closest(".cart-row");
                        const roomTypeId = wrapper.getAttribute("data-roomtypeid");

                        const cartItem = btn.closest(".cart-item"); // ✅ Tìm đến .cart-item cha
                        const checkbox = cartItem.querySelector(".roomtype-checkbox"); // ✅ Tìm checkbox trong cart-item

                        let quantity = parseInt(input.value);
                        const max = 5;

                        if (btn.classList.contains("plus") && quantity < max) {
                            quantity++;
                        } else if (btn.classList.contains("minus") && quantity > 1) {
                            quantity--;
                        }

                        input.value = quantity;

                        // Nếu checkbox đang được check, cập nhật quantity trong selectedQuantity
                        if (checkbox && checkbox.checked) {
                            selectedQuantity.set(parseInt(roomTypeId), quantity); // ✅ cập nhật số lượng mới
                        }
                        // ✅ Cập nhật data-quantity của checkbox
                        if (checkbox) {
                            checkbox.dataset.quantity = quantity;
                        }


                        updateItemTotal(row);
                        updateGrandTotal();

                        const xhr = new XMLHttpRequest();
                        xhr.open("POST", "/ParadiseHotel/updateCart", true);
                        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

                        xhr.onreadystatechange = function () {
                            if (xhr.readyState === 4) {
                                if (xhr.status === 200) {
                                    console.log("✅ Cập nhật thành công");
                                } else if (xhr.status === 401) {
                                    alert("❌ Vui lòng đăng nhập lại");
                                    window.location.href = "login.jsp";
                                } else {
                                    console.error("❌ Lỗi khi cập nhật giỏ hàng");
                                }
                            }
                        };

                        xhr.send("roomTypeId=" + encodeURIComponent(roomTypeId) +
                                "&quantity=" + encodeURIComponent(quantity));
                    });
                });


                // Khởi tạo ban đầu
                document.querySelectorAll(".cart-row").forEach(row => updateItemTotal(row));
                updateCheckboxStates();
            });
        </script>



        <script>
            function showToast(message, color = "#FF0000") {
                const toast = document.getElementById("toast-message");
                toast.innerText = message;
                toast.style.backgroundColor = color;
                toast.classList.remove("hidden");
                toast.classList.add("show");

                // Tự ẩn sau 3 giây
                setTimeout(() => {
                    toast.classList.remove("show");
                    toast.classList.add("hidden");
                }, 3000);
            }
        </script>
        <div id="toast-message" class="toast hidden">Thông báo mẫu</div>
        <script>
            let selectedRoom = new Set();
            let selectedQuantity = new Map();
            // Khi DOM đã load xong
            document.addEventListener("DOMContentLoaded", function () {
                // Lấy tất cả các checkbox có class roomtype-checkbox
                const checkboxes = document.querySelectorAll(".roomtype-checkbox");

                checkboxes.forEach(checkbox => {
                    checkbox.addEventListener("change", function () {
                        const roomTypeId = parseInt(this.value);
                        const quantity = parseInt(this.dataset.quantity || "0");

                        if (this.checked) {
                            selectedRoom.add(roomTypeId);
                            selectedQuantity.set(roomTypeId, quantity);
                        } else {
                            selectedRoom.delete(roomTypeId);
                            selectedQuantity.delete(roomTypeId);
                        }

                        // ✅ In ra để kiểm tra
                        console.log("Selected rooms:", Array.from(selectedRoom).join(", "));
                        console.log("Selected quantities:", Object.fromEntries(selectedQuantity));
                    });
                });
            });
            function selectedRoomList1() {
                const input = document.getElementById("selectedRoomList");
                const quanlitySend = document.getElementById("quanlitySend");

                const selectedCheckboxes = document.querySelectorAll(".roomtype-checkbox:checked");

                if (selectedCheckboxes.length === 0) {
                    showToast("❌ Vui lòng chọn ít nhất một phòng để tiếp tục!", "#e53935");
                    return;
                }

                const roomIds = [];
                const quantities = [];

                selectedCheckboxes.forEach(cb => {
                    const roomId = cb.dataset.roomtypeid;
                    const quantity = cb.dataset.quantity || "1";
                    roomIds.push(roomId);
                    quantities.push(quantity);
                });

                input.value = roomIds.join(",");
                quanlitySend.value = quantities.join(",");

                document.getElementById("proceed").submit();
            }




        </script>


    </body>
</html>
