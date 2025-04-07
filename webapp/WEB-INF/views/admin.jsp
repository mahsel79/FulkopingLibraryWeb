<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard - Fulköping Library</title>
    <link rel="stylesheet" type="text/css" href="styles.css">
</head>
<body>
    <% 
        if (session.getAttribute("user") == null || !"ADMIN".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.jsp");
            return;
        }
    %>
    <div class="admin-dashboard-container">
        <header class="dashboard-header">
            <h1>Admin Dashboard</h1>
            <nav class="dashboard-nav">
                <a href="dashboard.jsp" class="nav-item">Main Dashboard</a>
                <form action="login" method="post" style="display: inline;">
                    <input type="hidden" name="_method" value="DELETE">
                    <button type="submit" class="btn-logout">Logout</button>
                </form>
            </nav>
        </header>

        <main class="admin-main">
            <section class="admin-section">
                <h2>User Management</h2>
                <div class="user-list">
                    <table>
                        <thead>
                            <tr>
                                <th>Username</th>
                                <th>Email</th>
                                <th>Role</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="user" items="${users}">
                                <tr>
                                    <td>${user.username}</td>
                                    <td>${user.email}</td>
                                    <td>${user.role}</td>
                                    <td>${user.active ? 'Active' : 'Inactive'}</td>
                                    <td>
                                        <form action="admin/users" method="post" style="display: inline;">
                                            <input type="hidden" name="userId" value="${user.id}">
                                            <input type="hidden" name="action" value="${user.active ? 'deactivate' : 'activate'}">
                                            <button type="submit" class="btn-${user.active ? 'warning' : 'success'}">
                                                ${user.active ? 'Deactivate' : 'Activate'}
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </section>

            <section class="admin-section">
                <h2>Loan Management</h2>
                <div class="overdue-loans">
                    <h3>Overdue Loans</h3>
                    <table>
                        <thead>
                            <tr>
                                <th>User</th>
                                <th>Item</th>
                                <th>Due Date</th>
                                <th>Days Overdue</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="loan" items="${overdueLoans}">
                                <tr>
                                    <td>${loan.userId}</td>
                                    <td>${loan.itemId}</td>
                                    <td>${loan.dueDate}</td>
                                    <td>${loan.daysOverdue}</td>
                                    <td>
                                        <form action="admin/loans" method="post" style="display: inline;">
                                            <input type="hidden" name="loanId" value="${loan.id}">
                                            <input type="hidden" name="action" value="extend">
                                            <button type="submit" class="btn-primary">Extend Loan</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </section>

            <section class="admin-section">
                <h2>Library Statistics</h2>
                <div class="stats-grid">
                    <div class="stat-card">
                        <h3>Total Users</h3>
                        <p class="stat-number">${totalUsers}</p>
                    </div>
                    <div class="stat-card">
                        <h3>Active Loans</h3>
                        <p class="stat-number">${activeLoans}</p>
                    </div>
                    <div class="stat-card">
                        <h3>Overdue Items</h3>
                        <p class="stat-number">${overdueItems}</p>
                    </div>
                    <div class="stat-card">
                        <h3>Total Items</h3>
                        <p class="stat-number">${totalItems}</p>
                    </div>
                </div>
            </section>
        </main>
    </div>
</body>
</html>