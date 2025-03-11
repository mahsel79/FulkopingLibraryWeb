<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="se.fulkopinglibraryweb.models.Magazine" %>
<html>
<head>
    <title>Magazine Collection</title>
    <link rel="stylesheet" type="text/css" href="styles.css">
</head>
<body>
<h2>Library Magazine Collection</h2>

<!-- Search Form -->
<form action="magazines" method="get">
    <label>Search by:</label>
    <select name="searchType">
        <option value="title">Title</option>
        <option value="director">Director</option>
        <option value="issn">ISSN</option>
        <option value="general">General Search</option>
    </select>
    <input type="text" name="searchQuery" required>
    <button type="submit">Search</button>
</form>

<!-- Display Magazine List -->
<table border="1">
    <thead>
    <tr>
        <th>Title</th>
        <th>Director</th>
        <th>ISSN</th>
        <th>Publisher</th>
        <th>Year</th>
    </tr>
    </thead>
    <tbody>
    <%
        List<Magazine> magazineList = (List<Magazine>) request.getAttribute("magazineList");
        if (magazineList != null && !magazineList.isEmpty()) {
            for (Magazine magazine : magazineList) {
    %>
    <tr>
        <td><%= magazine.getTitle() %></td>
        <td><%= magazine.getDirector() %></td>
        <td><%= magazine.getIssn() %></td>
        <td><%= magazine.getPublisher() %></td>
        <td><%= magazine.getYear() %></td>
    </tr>
    <%
        }
    } else {
    %>
    <tr>
        <td colspan="5">No magazines found</td>
    </tr>
    <% } %>
    </tbody>
</table>

<h3>Add New Magazine</h3>
<form action="magazines" method="post">
    <label>Title:</label>
    <input type="text" name="title" required><br>

    <label>Director:</label>
    <input type="text" name="director" required><br>

    <label>ISSN:</label>
    <input type="text" name="issn" required><br>

    <label>Publisher:</label>
    <input type="text" name="publisher" required><br>

    <label>Year:</label>
    <input type="number" name="year" required><br>

    <button type="submit">Add Magazine</button>
</form>

<a href="dashboard.jsp">Back to Dashboard</a>
</body>
</html>
