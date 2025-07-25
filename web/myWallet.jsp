<%-- 
    Document   : myWallet
    Created on : Jul 23, 2025, 11:26:10 PM
    Author     : hungk
--%>

<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN" />
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
        <link href="css/myWallet.css" rel="stylesheet">
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

        <!--        <div id="preloader">
                    <div data-loader="circle-side"></div>
                </div> 
                <div class="layer"></div> Opacity Mask -->



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
                        <h1 class="slide-animated two">My Wallet</h1>
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
                        <li><a href="myWallet">My Wallet</a></li>
                        <li><a href="redeemVoucher">Loyalty Status</a> </li>
                        <li><a href="changePassword.jsp">Change Password</a></li>
                        <li><a href="./homepage?action=logout">Log out</a></li>
                        <li><a href="homepage">Home</a></li>
                    </ul>
                </div>

                <div class="form-wrapper">
                    <h3 class="mb-4">My Wallet</h3>
                    <div class="row">
                        <div class="col-12 col-md-6 mb-4">
                            <div class="card shadow-sm p-3 h-100" >
                                <div class="card-body">
                                    <h4 class="card-title"> <fmt:formatNumber value="${wallet.getBalance()}" type="currency"/></h4>
                                    <div class=" justify-content-between">
                                        <button type="button" class="btn_1 " onclick="openDepositModal()">Deposit</button>
                                        <button type="button" class="btn_1 " onclick="openWithdrawModal()">Withdraw</button>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!--deposit modal-->
                        <div id="depositModal" class="modal" style="display: none;">
                            <div class="modal-content">
                                <span class="close" onclick="closeDepositModal()">×</span>
                                <h3>Deposit</h3>
                                <form action="deposit" method="post" id="depositForm">
                                    <input type="hidden" name="action" value="deposit" />
                                    <div class="modal-row">
                                        <div class="form__group">
                                            <label for="amount" class="modal-label">Amount</label>
                                            <input type="text" id="amountDeposit" class="modal-input" name="amountDeposit" />
                                            <p class="form__error"></p>
                                        </div>
                                    </div>
                                    <div class="modal-action">
                                        <button type="submit" class="btn_1">Deposit</button>
                                        <button type="button" class="btn_1 gray" onclick="closeDepositModal()">Cancel</button>
                                    </div>
                                </form>
                            </div>
                        </div>

                        <!--withdraw modal-->
                        <div id="withdrawModal" class="modal" style="display: none;">
                            <div class="modal-content">
                                <span class="close" onclick="closeWithdrawModal()">×</span>
                                <h3>Withdraw</h3>
                                <form action="myWalletEventHandler" method="post" id="withdrawForm">
                                    <input type="hidden" name="action" value="withdraw" />
                                    <p style="text-align: start" >Your money: <strong> <fmt:formatNumber value="${wallet.getBalance()}" type="currency"/> </strong></p>
                                    <div class="modal-row">
                                        <div class="form__group">
                                            <label for="amount" class="modal-label">Amount</label>
                                            <input type="text" id="amountWithdraw" class="modal-input" name="amountWithdraw" />
                                            <p class="form__error"></p>
                                        </div>
                                    </div>
                                    <div class="modal-action">
                                        <button type="submit" class="btn_1">Withdraw</button>
                                        <button type="button" class="btn_1 gray" onclick="closeWithdrawModal()">Cancel</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>

                    <h3 class="mb-4">Bank account number</h3>
                    <div class="row">
                        <div class="col-12 col-md-6 mb-4 d-flex justify-content-between">
                            <div class="d-flex">
                                <img src="./img/svg_icons/checkbox-checked.svg">
                                <div class="desc-wrapper">
                                    <p class="desc bankNum">${defaultBankAccount.getAccountNumber()}</p>
                                    <p class="desc bankHolder">${defaultBankAccount.getBankName()} - ${defaultBankAccount.getAccountHolder()}</p>
                                </div>
                            </div>
                            <div >
                                <button type="submit" class="btn_1" onclick="openEditBankAccountModal()">Edit</button>
                                <button type="button" class="btn_1 gray" onclick="openAddBankAccountModal()">Add</button>
                            </div>
                        </div>

                        <!--Edit bank account modal-->
                        <div id="editBankAccountModal" class="modal" style="display: none;">
                            <div class="modal-content">
                                <span class="close" onclick="closeEditBankAccountModal()">×</span>
                                <h3>Bank Account</h3>
                                <form action="myWalletEventHandler" method="post" id="editBankAccountForm">
                                    <input type="hidden" name="action" value="editBankAccount" />
                                    <div class="modal-row">
                                        <c:forEach var="acc" items="${bankAccounts}">
                                            <div class="form__group form__group-row">
                                                <div class="bankNumWrapper" >
                                                    <input type="radio"
                                                           onchange="this.form.submit()"
                                                           id="bankNumDefault-${acc.getBankAccountID()}" name="bankNumDefault"
                                                           value="${acc.bankAccountID}" <c:if test="${acc.isDefault}">checked</c:if>>

                                                           <label for="bankNumDefault-${acc.getBankAccountID()}" >
                                                        <div class="desc-wrapper" style="">
                                                            <p class="desc bankNum">${acc.accountNumber}</p>
                                                            <p class="desc bankHolder">${acc.bankName} - ${acc.accountHolder}</p>
                                                        </div>
                                                    </label>

                                                </div>
                                                <button class="btn_1 btn-delete"  type="button" onclick="openDeleteModal(${acc.getBankAccountID()})">Delete</button>
                                                <p class="form__error"></p>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </form>
                            </div>
                        </div>

                        <!--Add back account modal-->
                        <div id="addBankAccountModal" class="modal" style="display: none;">
                            <div class="modal-content">
                                <span class="close" onclick="closeAddBankAccountModal()">×</span>
                                <h3>Deposit</h3>
                                <form action="myWalletEventHandler" method="post" id="addBankAccountForm">
                                    <input type="hidden" name="action" value="addBankAccount" />
                                    <div class="modal-row">
                                        <div class="form__group">
                                            <label for="amount" class="modal-label">Bank Account Number</label>
                                            <input type="text" id="bankNumber" class="modal-input" name="bankNumber" />
                                            <p class="form__error"></p>
                                        </div>
                                    </div>
                                    <div class="modal-row">
                                        <div class="form__group">
                                            <label for="amount" class="modal-label">Bank Account Name</label>
                                            <input type="text" id="bankAccountHolder" class="modal-input" name="bankAccountHolder" />
                                            <p class="form__error"></p>
                                        </div>
                                    </div>
                                    <div class="modal-row">
                                        <div class="form__group">
                                            <label for="amount" class="modal-label">Bank Name</label>
                                            <select class="modal-select" id="bankName" name="bankName">
                                                <option value="">Choose Bank Name</option>
                                                <option value="VCB">Vietcombank</option>
                                                <option value="VietinBank">VietinBank</option>
                                                <option value="BIDV">BIDV</option>
                                                <option value="Techcombank">Techcombank</option>
                                                <option value="MB">MBBank</option>
                                                <option value="ACB">ACB</option>
                                                <option value="Sacombank">Sacombank</option>
                                                <option value="VPBank">VPBank</option>
                                                <option value="TPBank">TPBank</option>
                                                <option value="HDBank">HDBank</option>
                                                <option value="SeABank">SeABank</option>
                                                <option value="Eximbank">Eximbank</option>
                                                <option value="SHB">SHB</option>
                                                <option value="SCB">SCB</option>
                                                <option value="VIB">VIB</option>
                                            </select>
                                            <p class="form__error"></p>
                                        </div>
                                    </div>
                                    <div class="modal-action">
                                        <button type="submit" class="btn_1">Add</button>
                                        <button type="button" class="btn_1 gray" onclick="closeAddBankAccountModal()">Cancel</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                        
                        <!--delete modal-->
                        <div id="deleteModal" class="modal" style="display: none;">
                            <div class="modal-content">
                                <span class="close" onclick="closeDeleteModal()">×</span>
                                <h3>Withdraw</h3>
                                <form action="myWalletEventHandler" method="post" id="deleteForm">
                                    <input type="hidden" name="action" value="delete" />
                                    <input type="hidden" id="idDelete" name="idDelete" />
                                    <p style="text-align: start" >Are you sure you want to delete this bank account?</strong></p>
                                    
                                    <div class="modal-action">
                                        <button type="submit" class="btn_1">Delete</button>
                                        <button type="button" class="btn_1 gray" onclick="closeDeleteModal()">Cancel</button>
                                    </div>
                                </form>
                            </div>
                        </div>

                    </div>
                    <h3 class="mb-4">Transaction History</h3>
                    <div class="row">
                        <div class="col-12 col-md-6 mb-4 d-flex justify-content-between">
                            <table>
                                <thead>
                                    <tr>
                                        <th>Transaction Type</th>
                                        <th>Amount</th>
                                        <th>Description</th>
                                        <th>Bank Account Number</th>
                                        <th>Status</th>
                                        <th>CreatedAt</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${transactions}" var="r" >
                                        <tr>
                                            <td>${r.getTransactionType()}</td>
                                            <td><fmt:formatNumber value="${r.getAmount()}" type="currency"/></td>
                                            <td>${r.getDescription()}</td>
                                            <td>${r.getBankAccount().getAccountNumber()}</td>
                                            <td>${r.getStatus()}</td>
                                            <td>${r.getCreatedAt()}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>

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
        <script src="./js/toastMessage.js"></script>                
        <script src="./js/validationForm.js">
        </script>

        <script>
            Validator({
                form: '#depositForm',
                formGroupSelector: '.form__group',
                errorSelector: '.form__error',
                rules: [
                    Validator.isRequired('#amountDeposit', 'Please enter total money you want to deposit'),
                    Validator.isNumber('#amountDeposit'),
                ],
                onsubmit: function (formValue) {
                    document.querySelector('#depositForm').submit();
                }
            })
            Validator({
                form: '#withdrawForm',
                formGroupSelector: '.form__group',
                errorSelector: '.form__error',
                rules: [
                    Validator.isRequired('#amountWithdraw', 'Please enter total money you want to withdraw'),
                    Validator.isNumber('#amountWithdraw'),
                    Validator.maxValue('#amountWithdraw', ${wallet.getBalance()}),
                ],
                onsubmit: function (formValue) {
                    document.querySelector('#withdrawForm').submit();
                }
            })
            Validator({
                form: '#addBankAccountForm',
                formGroupSelector: '.form__group',
                errorSelector: '.form__error',
                rules: [
                    Validator.bankAccountNumber('#bankNumber'),
                    Validator.uppercaseOnly('#bankAccountHolder'),
                    Validator.isSelectRequired('#bankName'),
                ],
                onsubmit: function (formValue) {
                    document.querySelector('#addBankAccountForm').submit();
                }
            })

        </script>

        <script>
            function openDepositModal() {
                document.getElementById("depositModal").style.display = "block";
            }

            function closeDepositModal() {
                document.getElementById("depositModal").style.display = "none";
            }

            function openWithdrawModal() {
                document.getElementById("withdrawModal").style.display = "block";
            }

            function closeWithdrawModal() {
                document.getElementById("withdrawModal").style.display = "none";
            }

            function openEditBankAccountModal() {
                document.getElementById("editBankAccountModal").style.display = "block";
            }

            function closeEditBankAccountModal() {
                document.getElementById("editBankAccountModal").style.display = "none";
            }
            function openAddBankAccountModal() {
                document.getElementById("addBankAccountModal").style.display = "block";
            }

            function closeAddBankAccountModal() {
                document.getElementById("addBankAccountModal").style.display = "none";
            }
            function openDeleteModal(IdDelete) {
            document.getElementById("idDelete").value = IdDelete;
                document.getElementById("deleteModal").style.display = "block";
            }

            function closeDeleteModal() {
                document.getElementById("deleteModal").style.display = "none";
            }
        </script>
          
       
       
    </body>
</html>
