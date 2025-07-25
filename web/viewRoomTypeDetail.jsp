<%-- 
    Document   : viewRoomTypeDetail
    Created on : Jun 6, 2025, 10:40:31 PM
    Author     : KTC
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="en" />
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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

            #editImageInput:invalid {
                border-color: #f44336 !important;
            }



            @keyframes slideIn {
                from {
                    opacity: 0;
                    transform: translateY(-50px) scale(0.9);
                }
                to {
                    opacity: 1;
                    transform: translateY(0) scale(1);
                }
            }

            .btn:hover {
                opacity: 0.9;
                transform: translateY(-1px);
                transition: all 0.2s ease;
            }

            .btn-secondary {
                background-color: #6c757d;
                color: white;
            }

            .btn-danger:hover {
                background-color: #d32f2f !important;
            }
            .modal .btn-secondary {
                background: #6c757d;
                color: #fff;
                padding: 8px 16px;
                border: none;
                margin-right: 10px;
            }
            .modal .btn-danger {
                background: #dc3545;
                color: #fff;
                padding: 8px 16px;
                border: none;
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

            <!-- Xóa message sau khi hiển thị -->
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
                        <!-- Left: Description -->
                        <div class="col-lg-4">
                            <div class="title">
                                <small>Luxury Experience</small>
                                <h2>${roomType.getDescription()}</h2>
                            </div>
                            <br>
                            <h5>Capacity Adult: ${roomType.capacity_adult}</h5>
                            <h5>Capacity Child: ${roomType.capacity_child}</h5>
                        </div>

                        <!-- Right: Services -->
                        <div class="col-lg-6">
                            <div class="room_facilities_list">
                                <h3>Services that this room type has:</h3>
                                <ul data-cues="slideInLeft">
                                    <c:forEach items="${listServices}" var="ls">
                                        <li><h4>${ls.name}</h4></li>
                                            </c:forEach>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
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

                                        <div class="feedback-Image-wrapper" data-feedback-id="${feedback.id}">
                                            <c:forEach var="img" items="${feedback.imageList}">
                                                <img class="feedback-Image" src=".${img}" />
                                            </c:forEach>
                                        </div>

                                        <c:if test="${not empty user and user.id eq feedback.user_id}">

                                            <div class="feedback-actions" style="padding-left: 900px; padding-bottom: 30px;">
                                                <button type="button"
                                                        class="btn btn-warning btn-sm"
                                                        onclick="openEditModal('${feedback.id}', '${feedback.rating}', `${feedback.comment}`, '${feedback.image_url}')">
                                                    Edit
                                                </button>

                                                <form action="DeleteFeedbackServlet" method="post" style="display:inline;" onsubmit="return confirm('Are you sure you want to delete this feedback?');">
                                                    <input type="hidden" name="feedbackId" value="${feedback.id}" />
                                                    <input type="hidden" name="roomTypeId" value="${roomTypeId}" />
                                                    <button type="button"
                                                            class="btn btn-danger btn-sm"
                                                            onclick="openDeleteModal('${feedback.id}')">
                                                        Delete
                                                    </button>
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
                                <form id="editFeedbackForm" action="EditFeedbackServlet" method="post" enctype="multipart/form-data">
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
                                        <textarea class="form-control" placeholder="Message" id="editComment" name="comment"></textarea>

                                        <div class="form-group" style="margin-bottom: 15px;">
                                            <label>Upload New Images (Optional - Max 5 images) <span id="editFileCount" style="color: #666; font-weight: normal;"></span></label>
                                            <input type="file" id="editImageInput" name="images" multiple accept="image/*" style="width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px;">
                                            <p class="form__error" id="edit_image_error" style="color: red; font-size: 14px; margin-top: 5px; display: none;"></p>
                                            <small style="color: #666; font-size: 12px;">You can upload up to 5 images (JPG, PNG, GIF, WEBP). Leave empty to keep current images</small>
                                        </div>

                                        <div style="margin-top: 20px; text-align: right;">
                                            <button type="button" class="btn btn-secondary" onclick="closeEditModal()">Close</button>
                                            <button type="submit" class="btn btn-primary">Save Changes</button>
                                        </div>
                                </form>
                            </div>
                        </div>
                    </div>
                    <!-- DELETE CONFIRM MODAL -->
                    <div id="deleteConfirmModal" class="modal" style="display: none; position: fixed; top: 0; left: 0;
                         width: 100%; height: 100%; background-color: rgba(0,0,0,0.6); z-index: 2000; justify-content: center; align-items: center;">
                        <div class="modal-content" style="background: white; padding: 20px; border-radius: 10px; text-align: center; width: 400px;">
                            <h4>Are you sure you want to delete this feedback?</h4>
                            <form id="deleteFeedbackForm" action="DeleteFeedbackServlet" method="post">
                                <input type="hidden" name="feedbackId" id="deleteFeedbackId" />
                                <input type="hidden" name="roomTypeId" value="${roomTypeId}" />
                                <div style="margin-top: 20px;">
                                    <button type="button" class="btn btn-secondary" onclick="closeDeleteModal()">Cancel</button>
                                    <button type="submit" class="btn btn-danger">Delete</button>
                                </div>
                            </form>
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

        <!-- Lightbox gallery -->
        <div id="image-lightbox" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%;
             background:rgba(0,0,0,0.85); align-items:center; justify-content:center; z-index:9999;">

            <button id="prev-btn" style="position:absolute; left:20px; font-size:40px; color:white; background:none; border:none; cursor:pointer;">&#10094;</button>

            <img id="lightbox-img" src="" style="max-width:90%; max-height:90%; border-radius:8px; box-shadow:0 0 20px #fff;">

            <button id="next-btn" style="position:absolute; right:20px; font-size:40px; color:white; background:none; border:none; cursor:pointer;">&#10095;</button>
        </div>

        <script>
            document.addEventListener("DOMContentLoaded", function () {
                const imageWrappers = document.querySelectorAll('.feedback-Image-wrapper');

                imageWrappers.forEach(wrapper => {
                    const images = wrapper.querySelectorAll('.feedback-Image');
                    const imageArray = Array.from(images);
                    let currentIndex = 0;

                    images.forEach((img, index) => {
                        img.addEventListener('click', function () {
                            currentIndex = index;
                            openLightbox(imageArray, currentIndex);
                        });
                    });
                });

                function openLightbox(images, index) {
                    // Xoá lightbox cũ nếu có
                    let oldLightbox = document.getElementById('lightbox');
                    if (oldLightbox) {
                        document.body.removeChild(oldLightbox);
                    }

                    // Tạo overlay
                    let overlay = document.createElement('div');
                    overlay.id = 'lightbox';
                    overlay.style.position = 'fixed';
                    overlay.style.top = 0;
                    overlay.style.left = 0;
                    overlay.style.width = '100%';
                    overlay.style.height = '100%';
                    overlay.style.backgroundColor = 'rgba(0,0,0,0.8)';
                    overlay.style.display = 'flex';
                    overlay.style.alignItems = 'center';
                    overlay.style.justifyContent = 'center';
                    overlay.style.zIndex = 9999;

                    // Tạo ảnh
                    let img = document.createElement('img');
                    img.src = images[index].src;
                    img.style.maxWidth = '90%';
                    img.style.maxHeight = '90%';
                    img.style.borderRadius = '10px';
                    img.style.boxShadow = '0 0 20px #fff';

                    // Nút Prev
                    let prev = document.createElement('button');
                    prev.innerHTML = '&#10094;';
                    prev.style.position = 'absolute';
                    prev.style.left = '20px';
                    prev.style.top = '50%';
                    prev.style.transform = 'translateY(-50%)';
                    prev.style.fontSize = '40px';
                    prev.style.color = 'white';
                    prev.style.background = 'none';
                    prev.style.border = 'none';
                    prev.style.cursor = 'pointer';

                    // Nút Next
                    let next = document.createElement('button');
                    next.innerHTML = '&#10095;';
                    next.style.position = 'absolute';
                    next.style.right = '20px';
                    next.style.top = '50%';
                    next.style.transform = 'translateY(-50%)';
                    next.style.fontSize = '40px';
                    next.style.color = 'white';
                    next.style.background = 'none';
                    next.style.border = 'none';
                    next.style.cursor = 'pointer';

                    // Nút đóng
                    let closeBtn = document.createElement('span');
                    closeBtn.innerHTML = '&times;';
                    closeBtn.style.position = 'absolute';
                    closeBtn.style.top = '20px';
                    closeBtn.style.right = '30px';
                    closeBtn.style.fontSize = '40px';
                    closeBtn.style.color = 'white';
                    closeBtn.style.cursor = 'pointer';

                    overlay.appendChild(prev);
                    overlay.appendChild(img);
                    overlay.appendChild(next);
                    overlay.appendChild(closeBtn);
                    document.body.appendChild(overlay);

                    // Hàm chuyển ảnh
                    function showImage(idx) {
                        img.src = images[idx].src;
                    }

                    prev.onclick = function (e) {
                        e.stopPropagation();
                        index = (index - 1 + images.length) % images.length;
                        showImage(index);
                    }

                    next.onclick = function (e) {
                        e.stopPropagation();
                        index = (index + 1) % images.length;
                        showImage(index);
                    }

                    closeBtn.onclick = function () {
                        document.body.removeChild(overlay);
                    }

                    overlay.onclick = function (e) {
                        if (e.target === overlay) {
                            document.body.removeChild(overlay);
                        }
                    }

                    // Hỗ trợ phím mũi tên
                    document.onkeydown = function (e) {
                        if (document.getElementById('lightbox')) {
                            if (e.key === "ArrowLeft") {
                                index = (index - 1 + images.length) % images.length;
                                showImage(index);
                            } else if (e.key === "ArrowRight") {
                                index = (index + 1) % images.length;
                                showImage(index);
                            } else if (e.key === "Escape") {
                                document.body.removeChild(overlay);
                            }
                        }
                    }
                }
            });
        </script>

        <!-- COMMON SCRIPTS -->
        <script src="js/common_scripts.js"></script>
        <script src="js/common_functions.js"></script>
        <script src="js/datepicker_inline.js"></script>
        <script src="phpmailer/validate.js"></script>
        <script src="./js/toastMessage.js"></script>
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
            // ✅ Đảm bảo form submit với enctype đúng
            function openEditModal(feedbackId, rating, comment, imageUrl) {
                document.getElementById('editFeedbackId').value = feedbackId;
                document.getElementById('editRating').value = rating;
                document.getElementById('editComment').value = comment;
                document.getElementById('editFeedbackModal').style.display = 'flex';
                updateStars(rating);

                // Reset file input and validation
                const fileInput = document.getElementById('editImageInput');
                const errorElement = document.getElementById('edit_image_error');
                const fileCountElement = document.getElementById('editFileCount');

                if (fileInput) {
                    fileInput.value = '';
                    fileInput.style.borderColor = '';
                }
                if (errorElement) {
                    errorElement.textContent = '';
                    errorElement.style.display = 'none';
                }
                if (fileCountElement) {
                    fileCountElement.textContent = '';
                }

                // ✅ Đảm bảo form có enctype đúng
                const form = document.getElementById('editFeedbackForm');
                form.enctype = 'multipart/form-data';

                // Ensure event listeners are attached (in case modal is opened multiple times)
                const editImageInput = document.getElementById('editImageInput');
                if (editImageInput && !editImageInput.hasAttribute('data-validation-attached')) {
                    editImageInput.addEventListener('change', validateEditImageUpload);
                    editImageInput.setAttribute('data-validation-attached', 'true');
                }
            }
            function closeEditModal() {
                document.getElementById('editFeedbackModal').style.display = 'none';
            }

            // Edit feedback image validation
            let isEditFormValid = true;

            function validateEditImageUpload() {
                const imageInput = document.getElementById('editImageInput');
                const errorElement = document.getElementById('edit_image_error');
                const fileCountElement = document.getElementById('editFileCount');
                const files = imageInput.files;

                // Clear previous error
                errorElement.textContent = '';
                errorElement.style.display = 'none';
                imageInput.style.borderColor = '';
                isEditFormValid = true;



                if (files.length > 5) {
                    errorElement.textContent = '❌ You can only upload maximum 5 images. Currently selected: ' + files.length + ' images. Please remove ' + (files.length - 5) + ' image(s).';
                    errorElement.style.display = 'block';
                    imageInput.style.borderColor = '#f44336';
                    isEditFormValid = false;
                    return false;
                }

                // Validate file types
                const validTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];
                for (let i = 0; i < files.length; i++) {
                    if (!validTypes.includes(files[i].type.toLowerCase())) {
                        errorElement.textContent = '❌ File "' + files[i].name + '" is not a valid image format. Please use JPG, PNG, GIF, or WEBP.';
                        errorElement.style.display = 'block';
                        imageInput.style.borderColor = '#f44336';
                        isEditFormValid = false;
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
        </script>
        <script>
            $(document).ready(function () {
                // Khởi tạo hiển thị sao ban đầu
                updateStars($("#editRating").val());

                // Cập nhật khi thay đổi
                $("#editRating").on("input change", function () {
                    updateStars($(this).val());
                });

                // Add event listener for edit image input change
                const editImageInput = document.getElementById('editImageInput');
                if (editImageInput) {
                    editImageInput.addEventListener('change', validateEditImageUpload);
                }

                // Add form submit validation for edit form
                const editForm = document.getElementById('editFeedbackForm');
                if (editForm) {
                    editForm.addEventListener('submit', function (e) {
                        if (!validateEditImageUpload()) {
                            e.preventDefault();
                            // Scroll to error message
                            document.getElementById('edit_image_error').scrollIntoView({behavior: 'smooth', block: 'center'});

                            // Show toast notification if available
                            if (typeof showToast === 'function') {
                                showToast('Please fix the image upload errors before submitting.', '#FF0000');
                            }

                            return false;
                        }
                    });
                }


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
        <script>
            function openDeleteModal(feedbackId) {
                document.getElementById('deleteFeedbackId').value = feedbackId;
                document.getElementById('deleteConfirmModal').style.display = 'flex';
            }

            function closeDeleteModal() {
                document.getElementById('deleteConfirmModal').style.display = 'none';
            }
        </script>

        <div id="toast-message" class="toast hidden">Thông báo mẫu</div>
    </body>
</html>
