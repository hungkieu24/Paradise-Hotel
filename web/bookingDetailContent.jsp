<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<style>
    .booking-detail-content {
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        font-size: 15px;
    }

    .booking-detail-content h5 {
        margin-top: 20px;
        font-size: 16px;
        border-bottom: 1px solid #ccc;
        padding-bottom: 4px;
    }

    .booking-detail-content ul {
        margin-left: 20px;
        padding-left: 0;
    }

    .booking-detail-content li {
        margin-bottom: 6px;
    }

    .booking-detail-content .total {
        font-weight: bold;
        font-size: 16px;
        color: #d00000;
        margin-top: 10px;
    }

    .booking-detail-content .discount {
        color: #0077b6;
    }
</style>

<div class="booking-detail-content">
    <p><strong>Branch:</strong> ${booking.branchName}</p>
    <p><strong>Check-in:</strong> <fmt:formatDate value="${booking.checkIn}" pattern="dd/MM/yyyy"/></p>
    <p><strong>Check-out:</strong> <fmt:formatDate value="${booking.checkOut}" pattern="dd/MM/yyyy"/></p>

    <h5>Rooms:</h5>
    <ul>
        <c:forEach var="r" items="${roomList}">
            <li>${r.roomTypeName} - ${r.quantity} x 
                <fmt:formatNumber value="${r.base_price}" type="currency" currencySymbol="₫"/> = 
                <fmt:formatNumber value="${r.pricePerRoom}" type="currency" currencySymbol="₫"/>
            </li>
            </c:forEach>
    </ul>

    <h5>Services:</h5>
    <ul>
        <c:forEach var="s" items="${serviceList}">
            <li>${s.serviceName} - ${s.quantity} x 
                <fmt:formatNumber value="${s.servicePrice}" type="currency" currencySymbol="₫"/> = 
                <fmt:formatNumber value="${s.servicePrice * s.quantity}" type="currency" currencySymbol="₫"/>
            </li>
        </c:forEach>
    </ul>

    <p><strong>Discount:</strong> <span class="discount">${sessionScope.loyaltyPoint.discountPercent}%</span></p>
    <p class="total">Total: 
        <fmt:formatNumber value="${booking.totalPrice}" type="currency" currencySymbol="₫"/>
    </p>
</div>
