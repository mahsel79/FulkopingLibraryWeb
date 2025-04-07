<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Search Results - Fulköping Library</title>
    <link rel="stylesheet" type="text/css" href="styles.css">
</head>
<body>
    <header class="header">
        <a href="index.jsp" class="logo">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M4 19.5C4 18.837 4.26339 18.2011 4.73223 17.7322C5.20107 17.2634 5.83696 17 6.5 17H20" stroke="#6c5ce7" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M6.5 2H20V22H6.5C5.83696 22 5.20107 21.7366 4.73223 21.2678C4.26339 20.7989 4 20.163 4 19.5V4.5C4 3.83696 4.26339 3.20107 4.73223 2.73223C5.20107 2.26339 5.83696 2 6.5 2Z" stroke="#6c5ce7" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            LibraryHaven
        </a>
        <div class="auth-buttons">
            <c:choose>
                <c:when test="${sessionScope.user != null}">
                    <a href="dashboard.jsp" class="btn btn-outline">Dashboard</a>
                    <form action="login" method="post" style="display: inline;">
                        <input type="hidden" name="_method" value="DELETE">
                        <button type="submit" class="btn btn-primary">Logout</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <a href="login.jsp" class="btn btn-outline">Register</a>
                    <a href="login.jsp" class="btn btn-primary">Login</a>
                </c:otherwise>
            </c:choose>
        </div>
    </header>

    <main class="search-results">
        <h1>Search Results for "${searchQuery}"</h1>
        
        <div class="search-filters">
            <form action="search" method="get" class="filter-form">
                <input type="hidden" name="query" value="${searchQuery}">
                <select name="type" class="filter-select" onchange="this.form.submit()">
                    <option value="all" ${param.type == 'all' ? 'selected' : ''}>All Items</option>
                    <option value="books" ${param.type == 'books' ? 'selected' : ''}>Books Only</option>
                    <option value="magazines" ${param.type == 'magazines' ? 'selected' : ''}>Magazines Only</option>
                    <option value="media" ${param.type == 'media' ? 'selected' : ''}>Media Only</option>
                </select>
            </form>
        </div>

        <div class="results-grid">
            <c:forEach var="item" items="${searchResults}">
                <div class="result-card">
                    <c:choose>
                        <c:when test="${item['class'].simpleName == 'Book'}">
                            <div class="result-type">Book</div>
                            <h3>${item.title}</h3>
                            <p>By ${item.author}</p>
                            <p>ISBN: ${item.isbn}</p>
                            <a href="books?id=${item.id}" class="btn btn-primary">View Details</a>
                        </c:when>
                        <c:when test="${item['class'].simpleName == 'Magazine'}">
                            <div class="result-type">Magazine</div>
                            <h3>${item.title}</h3>
                            <p>Issue: ${item.issueNumber}</p>
                            <p>Publisher: ${item.publisher}</p>
                            <a href="magazines?id=${item.id}" class="btn btn-primary">View Details</a>
                        </c:when>
                        <c:when test="${item['class'].simpleName == 'Media'}">
                            <div class="result-type">Media</div>
                            <h3>${item.title}</h3>
                            <p>Director: ${item.director}</p>
                            <p>Type: ${item.mediaType}</p>
                            <a href="media?id=${item.id}" class="btn btn-primary">View Details</a>
                        </c:when>
                    </c:choose>
                </div>
            </c:forEach>

            <c:if test="${empty searchResults}">
                <div class="no-results">
                    <p>No items found matching your search criteria.</p>
                </div>
            </c:if>
        </div>
    </main>

    <footer class="footer">
        <p>&copy; 2024 Fulköping Library. All rights reserved.</p>
    </footer>
</body>
</html>