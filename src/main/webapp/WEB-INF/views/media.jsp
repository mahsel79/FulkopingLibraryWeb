<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="se.fulkopinglibraryweb.models.Media" %>
<html>
<head>
    <title>Media Collection</title>
    <link rel="stylesheet" type="text/css" href="styles.css">
</head>
<body>
<h2>Library Media Collection</h2>

<!-- Search Form -->
<form action="media" method="get">
    <label>Search by:</label>
    <select name="searchType">
        <option value="title">Title</option>
        <option value="publisher">Publisher</option>
        <option value="catalogNumber">Catalog Number</option>
        <option value="general">General Search</option>
    </select>
    <input type="text" name="searchQuery" required>
    <button type="submit">Search</button>
</form>

<!-- Display Media Items -->
<table border="1">
    <thead>
    <tr>
        <th>Title</th>
        <th>Type</th>
        <th>Publisher</th>
        <th>Catalog Number</th>
        <th>Year</th>
    </tr>
    </thead>
    <tbody>
    <%
        List<Media> mediaList = (List<Media>) request.getAttribute("mediaList");
        if (mediaList != null && !mediaList.isEmpty()) {
            for (Media media : mediaList) {
    %>
    <tr>
        <td><%= media.getTitle() %></td>
        <td><%= media.getType() %></td>
        <td><%= media.getPublisher() %></td>
        <td><%= media.getCatalogNumber() %></td>
        <td><%= media.getYear() %></td>
    </tr>
    <%
        }
    } else {
    %>
    <tr>
        <td colspan="5">No media items found</td>
    </tr>
    <% } %>
    </tbody>
</table>

<h3>Add New Media</h3>
<form action="media" method="post">
    <label>Title:</label>
    <input type="text" name="title" required><br>

    <label>Type:</label>
    <select name="type">
        <option value="Book">Book</option>
        <option value="Magazine">Magazine</option>
        <option value="DVD">DVD</option>
        <option value="CD">CD</option>
    </select><br>

    <label>Publisher:</label>
    <input type="text" name="publisher" required><br>

    <label>Catalog Number:</label>
    <input type="text" name="catalogNumber" required><br>

    <label>Year:</label>
    <input type="number" name="year" required><br>

    <button type="submit">Add Media</button>
</form>

<a href="dashboard.jsp">Back to Dashboard</a>
</body>
</html>
