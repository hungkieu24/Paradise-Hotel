<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Feedback Management - HotelBooki Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .feedback-card {
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            margin-bottom: 15px;
            transition: box-shadow 0.2s;
        }
        .feedback-card:hover {
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        .rating-stars {
            color: #ffc107;
            font-size: 18px;
        }
        .status-badge {
            font-size: 12px;
            padding: 4px 8px;
        }
        .avatar {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            object-fit: cover;
        }
        .stats-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 10px;
        }
        .filter-section {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <div class="container-fluid">
        <!-- Header -->
        <div class="row">
            <div class="col-12">
                <div class="d-flex justify-content-between align-items-center py-3 border-bottom">
                    <h2><i class="fas fa-comments"></i> Feedback Management</h2>
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb mb-0">
                            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/feedback">Dashboard</a></li>
                            <li class="breadcrumb-item active">Feedback</li>
                        </ol>
                    </nav>
                </div>
            </div>
        </div>

        <!-- Messages -->
        <c:if test="${not empty sessionScope.message}">
            <div class="alert alert-success alert-dismissible fade show mt-3">
                ${sessionScope.message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <c:remove var="message" scope="session"/>
        </c:if>
        
        <c:if test="${not empty sessionScope.error}">
            <div class="alert alert-danger alert-dismissible fade show mt-3">
                ${sessionScope.error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <c:remove var="error" scope="session"/>
        </c:if>

        <!-- Statistics Cards -->
        <div class="row mt-4">
            <div class="col-md-3">
                <div class="card stats-card">
                    <div class="card-body text-center">
                        <i class="fas fa-comments fa-2x mb-2"></i>
                        <h4>${stats.total}</h4>
                        <p class="mb-0">Total Feedback</p>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card border-success">
                    <div class="card-body text-center text-success">
                        <i class="fas fa-eye fa-2x mb-2"></i>
                        <h4>${stats.visible}</h4>
                        <p class="mb-0">Visible</p>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card border-warning">
                    <div class="card-body text-center text-warning">
                        <i class="fas fa-eye-slash fa-2x mb-2"></i>
                        <h4>${stats.hidden}</h4>
                        <p class="mb-0">Hidden</p>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card border-danger">
                    <div class="card-body text-center text-danger">
                        <i class="fas fa-ban fa-2x mb-2"></i>
                        <h4>${stats.blocked}</h4>
                        <p class="mb-0">Blocked</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Average Rating -->
        <div class="row mt-3">
            <div class="col-md-12">
                <div class="card">
                    <div class="card-body text-center">
                        <h5>Average Rating</h5>
                        <div class="rating-stars">
                            <c:forEach begin="1" end="5" var="i">
                                <c:choose>
                                    <c:when test="${i <= stats.avgRating}">
                                        <i class="fas fa-star"></i>
                                    </c:when>
                                    <c:otherwise>
                                        <i class="far fa-star"></i>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                            <span class="ms-2 text-muted">
                                <fmt:formatNumber value="${stats.avgRating}" maxFractionDigits="1"/> / 5.0
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Filter Section -->
        <div class="filter-section mt-4">
            <form method="GET" action="${pageContext.request.contextPath}/feedback">
                <div class="row">
                    <div class="col-md-4">
                        <label for="status" class="form-label">Filter by Status</label>
                        <select name="status" id="status" class="form-select">
                            <option value="all" ${statusFilter == 'all' ? 'selected' : ''}>All Status</option>
                            <option value="Visible" ${statusFilter == 'Visible' ? 'selected' : ''}>Visible</option>
                            <option value="Hidden" ${statusFilter == 'Hidden' ? 'selected' : ''}>Hidden</option>
                            <option value="Blocked" ${statusFilter == 'Blocked' ? 'selected' : ''}>Blocked</option>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label for="sortBy" class="form-label">Sort by</label>
                        <select name="sortBy" id="sortBy" class="form-select">
                            <option value="date" ${sortBy == 'date' ? 'selected' : ''}>Date</option>
                            <option value="rating" ${sortBy == 'rating' ? 'selected' : ''}>Rating</option>
                            <option value="status" ${sortBy == 'status' ? 'selected' : ''}>Status</option>
                        </select>
                    </div>
                    <div class="col-md-4 d-flex align-items-end">
                        <button type="submit" class="btn btn-primary me-2">
                            <i class="fas fa-filter"></i> Filter
                        </button>
                        <a href="${pageContext.request.contextPath}/feedback" class="btn btn-outline-secondary">
                            <i class="fas fa-times"></i> Clear
                        </a>
                    </div>
                </div>
            </form>
        </div>

        <!-- Feedback List -->
        <div class="mt-4">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h5>Feedback List (${totalCount} total)</h5>
            </div>

            <c:choose>
                <c:when test="${empty feedbacks}">
                    <div class="text-center py-5">
                        <i class="fas fa-comments fa-3x text-muted mb-3"></i>
                        <h5 class="text-muted">No feedback found</h5>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="feedback" items="${feedbacks}">
                        <div class="feedback-card">
                            <div class="card-body">
                                <div class="row">
                                    <div class="col-md-8">
                                        <div class="d-flex align-items-start">
                                            <c:choose>
                                                <c:when test="${not empty feedback.userAvatarUrl}">
                                                    <img src="${feedback.userAvatarUrl}" alt="Avatar" class="avatar me-3">
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="avatar me-3 bg-secondary d-flex align-items-center justify-content-center text-white">
                                                        <i class="fas fa-user"></i>
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>
                                            <div class="flex-grow-1">
                                                <div class="d-flex justify-content-between align-items-start">
                                                    <div>
                                                        <h6 class="mb-1">${feedback.username}</h6>
                                                        <div class="rating-stars mb-2">
                                                            <c:forEach begin="1" end="5" var="i">
                                                                <c:choose>
                                                                    <c:when test="${i <= feedback.rating}">
                                                                        <i class="fas fa-star"></i>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <i class="far fa-star"></i>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </c:forEach>
                                                            <span class="ms-2 text-muted">${feedback.rating}/5</span>
                                                        </div>
                                                    </div>
                                                    <div class="text-end">
                                                        <c:choose>
                                                            <c:when test="${feedback.status == 'Visible'}">
                                                                <span class="badge bg-success status-badge">Visible</span>
                                                            </c:when>
                                                            <c:when test="${feedback.status == 'Hidden'}">
                                                                <span class="badge bg-warning status-badge">Hidden</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge bg-danger status-badge">Blocked</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </div>
                                                <p class="mb-2">${feedback.getShortComment(150)}</p>
                                                <small class="text-muted">
                                                    <i class="fas fa-calendar"></i> 
                                                    <fmt:formatDate value="${feedback.created_at}" pattern="dd/MM/yyyy HH:mm"/>
                                                    | Booking ID: ${feedback.booking_id}
                                                </small>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-4 text-end">
                                        <div class="btn-group" role="group">
                                            <a href="${pageContext.request.contextPath}/feedback?action=view&id=${feedback.id}" 
                                               class="btn btn-outline-primary btn-sm">
                                                <i class="fas fa-eye"></i> View
                                            </a>
                                            
                                            <c:if test="${feedback.status == 'Visible'}">
                                                <a href="${pageContext.request.contextPath}/feedback?action=updateStatus&id=${feedback.id}&status=Hidden&adminAction=Warned" 
                                                   class="btn btn-outline-warning btn-sm"
                                                   onclick="return confirm('Hide this feedback?')">
                                                    <i class="fas fa-eye-slash"></i> Hide
                                                </a>
                                            </c:if>
                                            
                                            <c:if test="${feedback.status == 'Hidden'}">
                                                <a href="${pageContext.request.contextPath}/feedback?action=updateStatus&id=${feedback.id}&status=Visible&adminAction=None" 
                                                   class="btn btn-outline-success btn-sm"
                                                   onclick="return confirm('Show this feedback?')">
                                                    <i class="fas fa-eye"></i> Show
                                                </a>
                                            </c:if>
                                            
                                            <c:if test="${feedback.status != 'Blocked'}">
                                                <a href="${pageContext.request.contextPath}/feedback?action=updateStatus&id=${feedback.id}&status=Blocked&adminAction=Banned" 
                                                   class="btn btn-outline-danger btn-sm"
                                                   onclick="return confirm('Block this feedback? This action cannot be undone easily.')">
                                                    <i class="fas fa-ban"></i> Block
                                                </a>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Pagination -->
        <c:if test="${totalPages > 1}">
            <nav aria-label="Feedback pagination" class="mt-4">
                <ul class="pagination justify-content-center">
                    <c:if test="${currentPage > 1}">
                        <li class="page-item">
                            <a class="page-link" href="?page=${currentPage - 1}&status=${statusFilter}&sortBy=${sortBy}">
                                <i class="fas fa-chevron-left"></i>
                            </a>
                        </li>
                    </c:if>
                    
                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <c:if test="${i >= currentPage - 2 && i <= currentPage + 2}">
                            <li class="page-item ${i == currentPage ? 'active' : ''}">
                                <a class="page-link" href="?page=${i}&status=${statusFilter}&sortBy=${sortBy}">${i}</a>
                            </li>
                        </c:if>
                    </c:forEach>
                    
                    <c:if test="${currentPage < totalPages}">
                        <li class="page-item">
                            <a class="page-link" href="?page=${currentPage + 1}&status=${statusFilter}&sortBy=${sortBy}">
                                <i class="fas fa-chevron-right"></i>
                            </a>
                        </li>
                    </c:if>
                </ul>
            </nav>
        </c:if>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>