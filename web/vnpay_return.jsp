<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Kết quả thanh toán</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 20px;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }
        .container {
            max-width: 600px;
            width: 100%;
            padding: 30px;
            background-color: #ffffff;
            border-radius: 8px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
            border: 1px solid #e0e0e0;
        }
        h1 {
            text-align: center;
            margin-bottom: 25px;
        }
        .success-msg {
            color: #28a745;
        }
        .failure-msg {
            color: #dc3545;
        }
        .details-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        .details-table td {
            padding: 12px 15px;
            border-bottom: 1px solid #eeeeee;
        }
        .details-table tr:last-child td {
            border-bottom: none;
        }
        .details-table td:first-child {
            font-weight: bold;
            color: #555;
            width: 40%;
        }
        .btn-container {
            text-align: center;
            margin-top: 30px;
        }
        .btn {
            display: inline-block;
            padding: 12px 25px;
            font-size: 16px;
            font-weight: bold;
            color: #fff;
            background-color: #007bff;
            border: none;
            border-radius: 5px;
            text-decoration: none;
            cursor: pointer;
            transition: background-color 0.3s ease;
        }
        .btn:hover {
            background-color: #0056b3;
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- Hiển thị tiêu đề dựa trên trạng thái thanh toán -->
        <c:choose>
            <c:when test="${requestScope.status == 'SUCCESS'}">
                <h1 class="success-msg">Giao dịch thành công!</h1>
            </c:when>
            <c:otherwise>
                <h1 class="failure-msg">Giao dịch thất bại!</h1>
            </c:otherwise>
        </c:choose>

        <h3>Chi tiết giao dịch</h3>
        
        <table class="details-table">
            <tr>
                <td>Mã đơn hàng:</td>
                <td>${requestScope.vnp_TxnRef}</td>
            </tr>
            <tr>
                <td>Số tiền:</td>
                <td>
                    <%-- VNPay trả về số tiền ở đơn vị nhỏ nhất (ví dụ: 100000 cho 1000.00 VND). Ta cần chia cho 100. --%>
                    <fmt:setLocale value="vi_VN"/>
                    <fmt:formatNumber value="${requestScope.vnp_Amount / 100}" type="currency" currencySymbol="VND"/>
                </td>
            </tr>
             <tr>
                <td>Nội dung thanh toán:</td>
                <td>${requestScope.vnp_OrderInfo}</td>
            </tr>
            <tr>
                <td>Mã giao dịch VNPAY:</td>
                <td>${requestScope.vnp_TransactionNo}</td>
            </tr>
            <tr>
                <td>Mã Ngân hàng:</td>
                <td>${requestScope.vnp_BankCode}</td>
            </tr>
            <tr>
                <td>Thời gian thanh toán:</td>
                <td>
                   <%-- Định dạng lại ngày từ yyyyMMddHHmmss sang dd/MM/yyyy HH:mm:ss --%>
                   <c:set var="payDateStr" value="${requestScope.vnp_PayDate}"/>
                   <c:if test="${not empty payDateStr}">
                       <c:set var="year" value="${payDateStr.substring(0, 4)}"/>
                       <c:set var="month" value="${payDateStr.substring(4, 6)}"/>
                       <c:set var="day" value="${payDateStr.substring(6, 8)}"/>
                       <c:set var="hour" value="${payDateStr.substring(8, 10)}"/>
                       <c:set var="minute" value="${payDateStr.substring(10, 12)}"/>
                       <c:set var="second" value="${payDateStr.substring(12, 14)}"/>
                       ${day}/${month}/${year} ${hour}:${minute}:${second}
                   </c:if>
                </td>
            </tr>
            <tr>
                <td>Kết quả:</td>
                <td class="${requestScope.status == 'SUCCESS' ? 'success-msg' : 'failure-msg'}">
                    <b>
                        <c:choose>
                            <c:when test="${requestScope.status == 'SUCCESS'}">
                                Thành Công
                            </c:when>
                            <c:when test="${requestScope.status == 'FAILURE'}">
                                Thất Bại (Mã lỗi: ${requestScope.vnp_ResponseCode})
                            </c:when>
                            <c:otherwise>
                                Giao dịch không hợp lệ (Chữ ký sai)
                            </c:otherwise>
                        </c:choose>
                    </b>
                </td>
            </tr>
        </table>

        <div class="btn-container">
            <a href="${pageContext.request.contextPath}/staff-bookings-list" class="btn">Trở về trang quản lý</a>
        </div>
    </div>
</body>
</html>