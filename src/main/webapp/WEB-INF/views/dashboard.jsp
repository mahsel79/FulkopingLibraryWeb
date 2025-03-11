<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard - Fulköping Library</title>
    <link rel="stylesheet" type="text/css" href="styles.css">
</head>
<body>
    <% 
        if (session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
    %>
    <div class="dashboard-container">
        <header class="dashboard-header">
            <h1>Welcome, ${sessionScope.username}!</h1>
            <nav class="dashboard-nav">
                <a href="books" class="nav-item">Books</a>
                <a href="magazines" class="nav-item">Magazines</a>
                <a href="media" class="nav-item">Media</a>
                <c:if test="${sessionScope.role == 'ADMIN'}">
                    <a href="admin" class="nav-item admin">Admin Panel</a>
                </c:if>
                <form action="login" method="post" style="display: inline;">
                    <input type="hidden" name="_method" value="DELETE">
                    <button type="submit" class="btn-logout">Logout</button>
                </form>
            </nav>
        </header>

        <main class="dashboard-main">
            <section class="quick-search">
                <h2>Quick Search</h2>
                <form action="books" method="get" class="search-form">
                    <select name="searchType" class="search-select">
                        <option value="title">Title</option>
                        <option value="author">Author</option>
                        <option value="isbn">ISBN</option>
                    </select>
                    <input type="text" name="searchQuery" placeholder="Search..." class="search-input">
                    <button type="submit" class="btn-search">Search</button>
                </form>
            </section>

            <section class="dashboard-grid">
                <div class="dashboard-card">
                    <h3>Books</h3>
                    <p>Browse and borrow from our collection of books</p>
                    <a href="books" class="btn-secondary">View Books</a>
                </div>
                <div class="dashboard-card">
                    <h3>Magazines</h3>
                    <p>Explore our magazine collection</p>
                    <a href="magazines" class="btn-secondary">View Magazines</a>
                </div>
                <div class="dashboard-card">
                    <h3>Media</h3>
                    <p>Access our digital media resources</p>
                    <a href="media" class="btn-secondary">View Media</a>
                </div>
            </section>
        </main>
    </div>
</body>
</html>