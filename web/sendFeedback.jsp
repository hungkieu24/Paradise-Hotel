<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

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
        <link rel="stylesheet" href="css/send_feedback.css">
        <!-- YOUR CUSTOM CSS -->
        <link href="css/custom.css" rel="stylesheet">

        <style>
            .form__error {
                background-color: #ffebee;
                border: 1px solid #f44336;
                border-radius: 4px;
                padding: 8px 12px;
                margin-top: 8px;
                font-size: 14px;
                color: #d32f2f;
                display: none;
                animation: fadeIn 0.3s ease-in;
            }

            .form__error:not(:empty) {
                display: block;
            }

            @keyframes fadeIn {
                from {
                    opacity: 0;
                    transform: translateY(-10px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }

            #imageInput:invalid {
                border-color: #f44336;
            }

            .text-muted {
                color: #6c757d;
                font-size: 12px;
                margin-top: 4px;
                display: block;
            }
        </style>
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
            <script>
                setTimeout(function () {
                    document.getElementById('toastMessage').style.display = 'none';
                <% session.removeAttribute("message"); %>
                <% session.removeAttribute("messageType"); %>
                }, 3000);
            </script>
        </c:if>


        <div id="preloader">
            <div data-loader="circle-side"></div>
        </div><!-- /Page Preload -->

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
                        <li><a href="myBooking">Your Booking</a></li>
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
                        <h1 class="slide-animated two">Share Your Opinion</h1>
                    </div>
                </div>
            </div>
            <!-- /Background Img Parallax -->

            <div class="container margin_120_95">
                <div class="row justify-content-between">

                    <div class="col-xl-7 col-lg-7 order-lg-1">
                        <h3 class="mb-3">Send Feedback</h3>
                        <div id="message-contact"></div>

                        <form id="sendFeedback" action="sendFeedback" method="post" enctype="multipart/form-data">

                            <input type="hidden" name="bookingId" value="${param.bookingId}" />


                            <div class="form-group">
                                <label>Username</label>
                                <input id="username" type="text" value="${sessionScope.user.getUsername()}" name="username" readonly>
                            </div>
                            <div class="form-group">
                                <label>Overall Rating (1-5 Stars)</label>

                                <div class="row">
                                    <div class="col-sm-6">
                                        <div class="form-floating mb-4">
                                            <input
                                                class="form-control"
                                                type="range"
                                                id="star_rating"
                                                name="rating"
                                                min="1"
                                                max="5"
                                                step="1"
                                                value="3"                             
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
                                </div>


                            </div>

                            <div class="form-group">
                                <label>Detailed Comments</label><!--
                                <textarea id="comment" name="comment" rows="5" cols="50" required></textarea>-->
                                <textarea class="form-control" placeholder="Message" id="comment" name="comment"></textarea>
                                <p class="form_error"></p>
                            </div>


                            <button type="button" class="cancel-btn" onclick="window.location.reload();">Cancel</button>

                            <button type="submit" class="save-btn">Submit a review</button>
                            <p class="text-end"><a href="viewRoomTypeList" class="btn_1">View reviews</a></p>



                    </div>
                    <!--upload anh -->
                    <div class="col-xl-4 col-lg-5 order-lg-2">

                        <div class="form-group">
                            <label>Upload Images (Optional - Max 5 images) <span id="fileCount" style="color: #666; font-weight: normal;"></span></label>
                            <input type="file" id="imageInput" name="images" multiple accept="image/*" class="form-control-file">
                            <p class="form__error" id="image_error" style="color: red; font-size: 14px; margin-top: 5px;"></p>
                            <small class="text-muted">You can upload up to 5 images (JPG, PNG, GIF, WEBP)</small>
                        </div>
                        <div class="wrapper-images" id="imagePreviewWrapper">
                            <div class="images">

                            </div>
                        </div>
                    </div>
                    </form>  
                </div>
                <!-- /row -->
            </div>
            <!--/container -->

            <div class="gallery">
                <i class="close">X</i>
                <div class="gallery_inner">
                    <img src="" alt="">
                </div>
                <div class="control_prev"> <= </div>
                <div class="control_after"> => </div>
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
        <script src="./js/image.js"></script>
        <script src="./js/validationForm.js"></script>
        <script src="./js/toastMessage.js">
        </script>
        <script>
            $(document).ready(function () {
                function updateStars(value) {
                    let stars = "";
                    for (let i = 0; i < value; i++) {
                        stars += "⭐";
                    }
                    $("#star_display").html(stars);
                }

                // Khởi tạo hiển thị sao ban đầu
                updateStars($("#star_rating").val());

                // Cập nhật khi thay đổi
                $("#star_rating").on("input change", function () {
                    updateStars($(this).val());
                });
            });

            // Custom validation for image upload
            let isValidForm = true;

            function validateImageUpload() {
                const imageInput = document.getElementById('imageInput');
                const errorElement = document.getElementById('image_error');
                const fileCountElement = document.getElementById('fileCount');
                const files = imageInput.files;

                // Clear previous error
                errorElement.textContent = '';
                errorElement.style.display = 'none';
                imageInput.style.borderColor = '';
                isValidForm = true;



                if (files.length > 5) {
                    errorElement.textContent = '❌ You can only upload maximum 5 images. Currently selected: ' + files.length + ' images. Please remove ' + (files.length - 5) + ' image(s).';
                    errorElement.style.display = 'block';
                    imageInput.style.borderColor = '#f44336';
                    isValidForm = false;
                    return false;
                }

                // Validate file types
                const validTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];
                for (let i = 0; i < files.length; i++) {
                    if (!validTypes.includes(files[i].type.toLowerCase())) {
                        errorElement.textContent = '❌ File "' + files[i].name + '" is not a valid image format. Please use JPG, PNG, GIF, or WEBP.';
                        errorElement.style.display = 'block';
                        imageInput.style.borderColor = '#f44336';
                        isValidForm = false;
                        return false;
                    }
                }

                // Show success message
                if (files.length > 0) {
                    errorElement.textContent = '✅ ' + files.length + ' image(s) selected successfully.';
                    errorElement.style.display = 'block';
                    errorElement.style.color = '#4caf50';
                    errorElement.style.backgroundColor = '#e8f5e8';
                    errorElement.style.borderColor = '#4caf50';
                    imageInput.style.borderColor = '#4caf50';
                }

                return true;
            }

            // Add event listener for file input change
            document.getElementById('imageInput').addEventListener('change', validateImageUpload);

            // Add form submit validation
            document.getElementById('sendFeedback').addEventListener('submit', function (e) {
                if (!validateImageUpload()) {
                    e.preventDefault();
                    // Scroll to error message
                    document.getElementById('image_error').scrollIntoView({behavior: 'smooth', block: 'center'});

                    // Show toast notification
                    if (typeof showToast === 'function') {
                        showToast('Please fix the image upload errors before submitting.', 'error');
                    }

                    return false;
                }
            });

            Validator({
                form: '#sendFeedback',
                formGroupSelector: '.form__group',
                errorSelector: '.form__error',
                rules: [
                    // Remove the old validation rules as we handle them manually above
                ],
                onsubmit: function (formValue) {
                    if (isValidForm) {
                        document.querySelector('#sendFeedback').submit();
                    }
                }
            })
        </script>

    </body>
</html>