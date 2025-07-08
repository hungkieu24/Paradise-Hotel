<%-- 
    Document   : sidebarAdmin
    Created on : Jun 9, 2025, 12:37:09 PM
    Author     : hungk
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<nav class="sidebar" id="sidebar">
    <!-- ... (tương tự sidebar trong index.html, chỉ thay active class cho menu-item Quản lý Phòng) -->
    <div class="sidebar-header">

        <button class="sidebar-toggle" id="sidebarToggle">
            <div class="brand">
                <i class="fas fa-building"></i>
                <span class="brand-text"></span>
            </div>

        </button>
    </div>
    <div class="sidebar-menu">
        <a href="/index.html" class="menu-item ">
            <i class="fas fa-chart-line"></i>
            <span class="menu-text">Dashboard</span>
        </a>
        <a href="/rooms.html" class="menu-item active">
            <i class="fa-solid fa-hotel"></i>
            <span class="menu-text">Branch</span>
        </a>
        <a href="/feedback.html" class="menu-item">
            <i class="fas fa-comments"></i>
            <span class="menu-text">Feedback</span>
        </a>
        <a href="/membership.html" class="menu-item">
            <i class="fas fa-users"></i>
            <span class="menu-text">Account</span>
        </a>
        <!--                    <a href="#" class="menu-item logout">
                                <i class="fas fa-sign-out-alt"></i>
                                <span class="menu-text">Đăng xuất</span>
                            </a>-->
    </div>
</nav>