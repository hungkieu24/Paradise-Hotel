<%-- 
    Document   : bookingHistory
    Created on : Jul 10, 2025, 5:12:40 PM
    Author     : KTC
--%>


<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

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
        <link rel="stylesheet" href="css/editProfile1.css">
        <!-- YOUR CUSTOM CSS -->
        <link href="css/custom.css" rel="stylesheet">
        <link href="css/bookingHistory.css" rel="stylesheet">

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
        </div> 
        <div class="layer"></div><!-- Opacity Mask -->



        <div class="nav_panel">
            <a href="#" class="closebt open_close_nav_panel"><i class="bi bi-x"></i></a>
            <div class="logo_panel"><img src="img/logo_sticky.png" width="135" height="45" alt=""></div>
            <div class="sidebar-navigation">
                <nav>
                    <ul class="level-1">
                        <li><a href="#">Personal Info</a></li>
                        <li><a href="editProfile">Change Personal Info</a></li>
                        <li><a href="#">Booking History</a></li>
                        <li><a href="myBooking">My Booking</a></li>
                        <li><a href="#">Loyalty Status</a> </li>
                        <li><a href="#">Change Password</a></li>
                        <li class="parent"><a href="#0">Feedback</a>
                            <ul class="level-2">
                                <li class="back"><a href="#0">Back</a></li>
                                <li><a href="viewFeedback">View Feedback</a></li>
                                <li><a href="sendFeedback.jsp">Send Feedback</a></li>
                            </ul> 
                        </li>
                        <li><a href="./homepage?action=logout">Log out</a></li>
                        <li><a href="homepage" class="home-link">Home</a></li>
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
                <img class="jarallax-img" src="img/room1.jpg" alt="">
                <div class="wrapper opacity-mask d-flex align-items-center justify-content-center text-center animate_hero" data-opacity-mask="rgba(0, 0, 0, 0.5)">
                    <div class="container">
                        <small class="slide-animated one">Luxury Hotel Experience</small>
                        <h1 class="slide-animated two">My Booking</h1>
                    </div>
                </div>
            </div>
            <!-- /Background Img Parallax -->

            <div class="container margin_120_95">
                <div class="sidebar" style="margin-right: 0px">
                    <img class="avatar" src="${sessionScope.user.getAvatar_url()}" alt="Avatar"/>
                    <p>Rank: <span>${sessionScope.loyaltypointlp.getLevel()}</span></p>
                    <p>Accumulated Points: <a href="#">${sessionScope.loyaltypointlp.getPoints()}</a></p>
                    <ul>
                        <li><a href="editProfile">Personal Info</a></li>
                        <li><a href="bookingHistory">Booking History</a></li>
                        <li><a href="myBooking">My Booking</a></li>
                        <li><a href="redeemVoucher">Loyalty Status</a> </li>
                        <li><a href="changePassword.jsp">Change Password</a></li>
                        <li><a href="./homepage?action=logout">Log out</a></li>
                        <li><a href="homepage">Home</a></li>
                    </ul>
                </div>

                <div class="form-wrapper">
                    <h3 class="mb-4">My Booking</h3>
                    <div class="row">
                        <form action="myBooking" method="post">
                            <c:forEach var="b" items="${bookings}">
                                <div class="col-lg-4 col-md-6 mb-4">
                                    <div class="card shadow-sm p-3 h-100" >
                                        <c:choose>
                                            <c:when test="${not empty b.roomTypeImage}">
                                                <img src="${b.roomTypeImage}" class="card-img-top" alt=""/>
                                            </c:when>
                                        </c:choose>

                                        <div class="card-body">
                                            <h5 class="card-title">
                                                <c:choose>
                                                    <c:when test="${b.branchName != null}">${b.branchName}</c:when>
                                                    <c:otherwise>Chi nhánh không xác định</c:otherwise>
                                                </c:choose>
                                            </h5>

                                            <p class="card-text mb-1">Check In - Check Out: 
                                                <fmt:formatDate value="${b.checkIn}" pattern="dd/MM/yyyy" /> - 
                                                <fmt:formatDate value="${b.checkOut}" pattern="dd/MM/yyyy" />
                                            </p>
                                            <p class="card-text mb-1">RoomType: ${b.roomTypeName}</p>
                                            <p class="card-text mb-1">Total Price: <fmt:formatNumber value="${b.totalPrice}" type="currency" currencySymbol="₫"/></p>

                                            <c:if test="${b.paymentStatus == 'Refunded'}">
                                                <div class="refund-info" style="background: #e8f5e8; padding: 10px; border-radius: 5px; margin-top: 10px;">
                                                    <h4 style="color: #28a745; margin: 0 0 5px 0;">✅ ĐÃ HOÀN TIỀN</h4>
                                                    <p><strong>🏦 Mã giao dịch VNPay:</strong> ${b.refundTransactionNo}</p>
                                                    <p><strong>📅 Ngày hoàn tiền:</strong> ${b.refundDate}</p>
                                                    <p><strong>💰 Số tiền hoàn:</strong> <fmt:formatNumber value="${b.totalPrice}" type="currency" currencySymbol="VND"/></p>
                                                    <p style="color: #666; font-size: 12px;">⏰ Tiền đã được VNPay xử lý và sẽ về tài khoản trong 1-3 ngày làm việc</p>
                                                </div>
                                            </c:if>

                                            <p class="card-text mt-2" style="font-weight: bold; color:
                                               <c:choose>
                                                   <c:when test="${b.status eq 'Pending'}">orange</c:when>
                                                   <c:otherwise>#555</c:otherwise>
                                               </c:choose>">
                                                <c:choose>
                                                    <c:when test="${b.status eq 'Pending'}">Pending</c:when>
                                                    <c:otherwise>${b.status}</c:otherwise>
                                                </c:choose>
                                            </p>
                                            <div class="d-flex justify-content-between mt-3">

                                                <a href="#" class="btn_1 small open-detail" data-id="${b.id}">Chi tiết</a>
                                                <button type="button" class="btn_1 small" onclick="openEditModal('${b.id}', '${fn:escapeXml(b.note)}')">Edit Special Request</button>

                                                <c:if test="${b.status == 'Pending'}">
                                                    <button type="button" class="btn_1 small danger" onclick="showCancelForm('${b.id}')">Cancel Booking</button>
                                                </c:if>   
                                                <!-- Thêm nút hoàn tiền cho booking đã thanh toán -->
                                                <c:if test="${b.status == 'Paid'}">
                                                    <button type="button" class="btn_1 small warning" onclick="showRefundForm('${b.id}', '${b.totalPrice}')">Hoàn tiền</button>
                                                </c:if>
                                            </div>


                                        </div>
                                    </div>
                                </div>
                            </c:forEach>

                            <div id="editModal" class="modal" style="display: none;">
                                <div class="modal-content">
                                    <span class="close" onclick="closeEditModal()">×</span>
                                    <h3>Edit Special Request</h3>
                                    <form action="myBooking" method="post">
                                        <input type="hidden" name="action" value="editSpecialRequest" />
                                        <input type="hidden" name="bookingId" id="modalBookingId" />
                                        <textarea name="specialRequest" id="modalSpecialRequest" rows="4" class="form-control" required></textarea>
                                        <div style="margin-top: 15px;">
                                            <button type="submit" class="btn_1">Save</button>
                                            <button type="button" class="btn_1 gray" onclick="closeEditModal()">Cancel</button>
                                        </div>
                                    </form>
                                </div>
                            </div>


                            <!-- Cancel Form Modal -->
                            <div id="cancelModal" class="modal">
                                <div class="modal-content">
                                    <span class="close" onclick="closeCancelForm()">×</span>
                                    <h2>Cancel Booking</h2>
                                    <form action="myBooking" method="post">
                                        <input type="hidden" name="action" value="cancel">
                                        <input type="hidden" name="bookingId" id="cancelBookingId">
                                        <p>Are you sure you want to cancel this booking?</p>
                                        <label>Reason for Cancellation:</label>
                                        <textarea name="cancelReason" rows="4" placeholder="Enter cancellation reason (required)" required></textarea>
                                        <button type="submit">Confirm Cancellation</button>
                                        <button type="button" onclick="closeCancelForm()">Close</button>
                                    </form>
                                </div>
                            </div>
                        </form>
                    </div>
                    <!-- Refund Form Modal -->
                    <div id="refundModal" class="modal" style="display: none;">
                        <div class="modal-content">
                            <span class="close" onclick="closeRefundForm()">×</span>
                            <h2>Hoàn tiền Booking</h2>
                            <form id="refundForm" method="post" action="vnpay-refund">
                                <input type="hidden" name="bookingId" id="refundBookingId">
                                <input type="hidden" name="amount" id="refundAmount">

                                <p><strong>Booking ID:</strong> #<span id="refundBookingIdDisplay"></span></p>
                                <p><strong>Số tiền hoàn:</strong> <span id="refundAmountDisplay"></span> VND</p>

                                <label for="refundReason">Lý do hoàn tiền: <span style="color: red;">*</span></label>
                                <textarea name="refundReason" id="refundReason" rows="4" 
                                          placeholder="Vui lòng nhập lý do hoàn tiền (bắt buộc)" 
                                          required minlength="10"></textarea>

                                <div style="margin-top: 15px;">
                                    <button type="submit" class="btn_1">Xác nhận hoàn tiền</button>
                                    <button type="button" class="btn_1" onclick="closeRefundForm()">Hủy</button>
                                </div>
                            </form>
                        </div>
                    </div>
                    <% 
  String refundMsg = (String) session.getAttribute("refundMsg");
  if (refundMsg != null) { 
                    %>
                    <div id="refundSuccessModal" class="modal" style="display: block;">
                        <div class="modal-content" style="max-width: 500px;">
                            <span class="close" onclick="closeRefundSuccessModal()">×</span>
                            <div style="text-align: center;">
                                <h2 style="color: #28a745; margin-bottom: 20px;">
                                    <i class="fa fa-check-circle"></i> Thông báo hoàn tiền
                                </h2>
                                <div style="text-align: left; line-height: 1.6;">
                                    <%= refundMsg %>
                                </div>
                                <button onclick="closeRefundSuccessModal()" 
                                        style="margin-top: 20px; padding: 10px 20px; background: #28a745; color: white; border: none; border-radius: 5px; cursor: pointer;">
                                    Đã hiểu
                                </button>
                            </div>
                        </div>
                    </div>
                    <% 
                    session.removeAttribute("refundMsg"); 
                    } 
                    %>
                    <!-- Pagination -->
                    <div class="pagination d-flex justify-content-center mt-4"> 
                        <c:if test="${currentPage > 1}">
                            <a href="myBooking?page=${currentPage - 1}" class="prev">Previous</a>
                        </c:if>

                        <c:forEach var="i" begin="1" end="${totalPages}">
                            <a href="myBooking?page=${i}" class="${i == currentPage ? 'active' : ''}">${i}</a>
                        </c:forEach>

                        <c:if test="${currentPage < totalPages}">
                            <a href="myBooking?page=${currentPage + 1}" class="next">Next</a>
                        </c:if>
                    </div>

                </div>
            </div>
            <!-- Modal -->
            <div id="bookingDetailModal" class="modal-overlay" style="display:none;">
                <div class="modal-content">
                    <span class="close-button" onclick="closeModal()">×</span>
                    <div id="modal-body-content"></div>
                </div>
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
                                <li><a href="homepage">Home</a></li>
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
        <script src="phpmailer/validate.js"></script>
        <script src="./js/toastMessage.js"></script>                
        <script src="./js/validationForm.js"></script>

        <script>
                        document.querySelectorAll('.open-detail').forEach(btn => {
                            btn.addEventListener('click', function (e) {
                                e.preventDefault();
                                const bookingId = this.getAttribute('data-id');

                                fetch('bookingDetail?id=' + bookingId)
                                        .then(response => response.text())
                                        .then(html => {
                                            document.getElementById('modal-body-content').innerHTML = html;
                                            document.getElementById('bookingDetailModal').style.display = 'flex';
                                        })
                                        .catch(error => {
                                            console.error("Error loading booking detail:", error);
                                        });
                            });
                        });

                        function closeModal() {
                            document.getElementById('bookingDetailModal').style.display = 'none';
                        }

        </script>
        <script>
            function showCancelForm(bookingId) {
                document.getElementById('cancelBookingId').value = bookingId;
                document.getElementById('cancelModal').style.display = 'flex';
            }

            function closeCancelForm() {
                document.getElementById('cancelModal').style.display = 'none';
            }
        </script>
        <script>
            function openEditModal(bookingId, currentRequest) {
                document.getElementById("modalBookingId").value = bookingId;
                document.getElementById("modalSpecialRequest").value = currentRequest;
                document.getElementById("editModal").style.display = "block";
            }

            function closeEditModal() {
                document.getElementById("editModal").style.display = "none";
            }
        </script>
        <!--        <script>
                    function showRefundForm(bookingId, totalPrice) {
                        const confirmMsg = "Bạn có chắc chắn muốn hoàn tiền cho booking #" + bookingId + "?\n" +
                                "Số tiền: " + totalPrice + " VND\n" +
                                "Lưu ý: Quá trình hoàn tiền có thể mất 3-5 ngày làm việc.";
        
                        if (confirm(confirmMsg)) {
                            const button = event.target;
                            const originalText = button.innerHTML;
                            button.innerHTML = '<i class="fa fa-spinner fa-spin"></i> Đang xử lý...';
                            button.disabled = true;
        
                            const form = document.createElement('form');
                            form.method = 'POST';
                            form.action = 'vnpay-refund';
        
                            const bookingIdInput = document.createElement('input');
                            bookingIdInput.type = 'hidden';
                            bookingIdInput.name = 'bookingId';
                            bookingIdInput.value = bookingId;
        
                            const amountInput = document.createElement('input');
                            amountInput.type = 'hidden';
                            amountInput.name = 'amount';
                            amountInput.value = totalPrice;
        
                            const reasonInput = document.createElement('input');
                            reasonInput.type = 'hidden';
                            reasonInput.name = 'refundReason';
                            reasonInput.value = 'Customer requested refund';
        
                            form.appendChild(bookingIdInput);
                            form.appendChild(amountInput);
                            form.appendChild(reasonInput);
        
                            document.body.appendChild(form);
                            form.submit();
                        }
                    }
        
        // Show refund button only for eligible bookings
                    function canShowRefundButton(booking) {
                        return booking.status === 'Paid' &&
                                booking.payment_status === 'Paid' &&
                                booking.status !== 'CheckedIn' &&
                                booking.status !== 'Completed' &&
                                booking.status !== 'Cancelled' &&
                                new Date(booking.check_in) > new Date(); // Before check-in date
                    }
                </script>-->
        <script>
            function showRefundForm(bookingId, totalPrice) {
                // Set values cho modal
                document.getElementById('refundBookingId').value = bookingId;
                document.getElementById('refundAmount').value = totalPrice;
                document.getElementById('refundAmountDisplay').textContent = new Intl.NumberFormat('vi-VN').format(totalPrice);

                // Hiển thị modal
                document.getElementById('refundModal').style.display = 'block';
            }

            function closeRefundForm() {
                document.getElementById('refundModal').style.display = 'none';
                // Reset form
                document.getElementById('refundForm').reset();
            }

// Xử lý submit form refund
            // Xử lý submit form refund
            document.addEventListener('DOMContentLoaded', function () {
                const refundForm = document.getElementById('refundForm');
                if (refundForm) {
                    refundForm.addEventListener('submit', function (e) {
                        const submitBtn = this.querySelector('button[type="submit"]');

                        // Show VNPay processing steps
                        submitBtn.innerHTML = '<i class="fa fa-spinner fa-spin"></i> Đang kết nối VNPay...';
                        submitBtn.disabled = true;

                        // Thêm progress indicator
                        const progressDiv = document.createElement('div');
                        progressDiv.id = 'refund-progress';
                        progressDiv.innerHTML = `
                <div style="margin-top: 10px; padding: 10px; background: #f0f8ff; border-radius: 5px;">
                    <p>🔄 Đang xử lý hoàn tiền qua VNPay...</p>
                    <p>⏳ Vui lòng đợi trong giây lát</p>
                </div>
            `;
                        this.appendChild(progressDiv);

                        // Form sẽ tự submit
                    });
                }
            });
        </script>
        <script>
            function closeRefundSuccessModal() {
                document.getElementById('refundSuccessModal').style.display = 'none';
            }

// Tự động đóng modal sau 10 giây
            document.addEventListener('DOMContentLoaded', function () {
                const modal = document.getElementById('refundSuccessModal');
                if (modal) {
                    setTimeout(function () {
                        modal.style.display = 'none';
                    }, 10000); // 10 giây
                }
            });

// Đóng modal khi click outside
            window.onclick = function (event) {
                const modal = document.getElementById('refundSuccessModal');
                if (event.target == modal) {
                    modal.style.display = 'none';
                }
            }
        </script>
    </body>
</html>
