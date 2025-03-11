<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="se.fulkopinglibraryweb.models.Book" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Book Collection</title>
    <link rel="stylesheet" type="text/css" href="styles.css">
    <script src="js/form-validation.js" defer></script>
</head>
<body>
<div class="container">
    <h1>Library Book Collection</h1>

    <!-- Error Messages Container -->
    <div id="globalErrorContainer" class="error-container" role="alert" aria-live="polite">
        <c:if test="${param.error != null}">
            <div class="error-message">${param.error}</div>
        </c:if>
        <c:if test="${param.success != null}">
            <div class="alert alert-success">${param.success}</div>
        </c:if>
    </div>

    <!-- Search Form -->
    <div class="search-section">
        <form action="books" method="get" class="search-form" role="search" id="searchForm" novalidate>
            <fieldset>
                <legend>Search Books</legend>
                <div class="form-group">
                    <label for="searchType">Search by:</label>
                    <select name="searchType" id="searchType" class="form-control" required aria-required="true">
                        <option value="">Select search type</option>
                        <option value="title">Title</option>
                        <option value="author">Author</option>
                        <option value="isbn">ISBN</option>
                        <option value="general">General Search</option>
                    </select>
                    <div class="error-message" id="searchType-error" aria-live="polite"></div>
                </div>
                <div class="form-group">
                    <label for="searchQuery">Search Query:</label>
                    <input type="text" id="searchQuery" name="searchQuery" class="form-control" required
                           aria-label="Search query" minlength="2" aria-required="true"
                           aria-describedby="searchQuery-help searchQuery-error">
                    <div class="error-message" id="searchQuery-error" aria-live="polite"></div>
                    <small id="searchQuery-help" class="form-text">Enter at least 2 characters</small>
                </div>
                <button type="submit" class="btn-primary" data-original-text="Search">Search</button>
            </fieldset>
        </form>
    </div>

    <!-- Display Book List -->
    <div class="table-responsive" role="region" aria-label="Book list">
        <table class="table" aria-describedby="bookTableDesc">
            <caption id="bookTableDesc">List of available books in the library</caption>
            <thead>
            <tr>
                <th scope="col">Title</th>
                <th scope="col">Author</th>
                <th scope="col">ISBN</th>
                <th scope="col">Publisher</th>
                <th scope="col">Year</th>
            </tr>
            </thead>
            <tbody>
            <%
                List<Book> bookList = (List<Book>) request.getAttribute("bookList");
                if (bookList != null && !bookList.isEmpty()) {
                    for (Book book : bookList) {
            %>
            <tr>
                <td><%= book.getTitle() %></td>
                <td><%= book.getAuthor() %></td>
                <td><%= book.getIsbn() %></td>
                <td><%= book.getPublisher() %></td>
                <td><%= book.getYear() %></td>
            </tr>
            <%
                }
            } else {
            %>
            <tr>
                <td colspan="5">No books found</td>
            </tr>
            <% } %>
            </tbody>
        </table>
    </div>

    <section class="add-book-section">
        <h2>Add New Book</h2>
        <div id="addBookErrorContainer" class="error-container" role="alert" aria-live="polite"></div>
        <form action="books" method="post" class="form-container" id="addBookForm" novalidate>
            <fieldset>
                <legend>Book Information</legend>
                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label for="title">Title:</label>
                        <input type="text" id="title" name="title" class="form-control" required
                               aria-required="true" minlength="1" maxlength="200"
                               aria-describedby="title-help title-error">
                        <div class="error-message" id="title-error" aria-live="polite"></div>
                        <small id="title-help" class="form-text">Maximum 200 characters</small>
                    </div>

                    <div class="form-group col-md-6">
                        <label for="author">Author:</label>
                        <input type="text" id="author" name="author" class="form-control" required
                               aria-required="true" minlength="2" maxlength="100"
                               aria-describedby="author-help author-error">
                        <div class="error-message" id="author-error" aria-live="polite"></div>
                        <small id="author-help" class="form-text">Between 2 and 100 characters</small>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label for="isbn">ISBN:</label>
                        <input type="text" id="isbn" name="isbn" class="form-control" required
                               aria-required="true" pattern="^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$"
                               aria-describedby="isbn-help isbn-error">
                        <div class="error-message" id="isbn-error" aria-live="polite"></div>
                        <small id="isbn-help" class="form-text">Enter a valid ISBN number</small>
                    </div>

                    <div class="form-group col-md-6">
                        <label for="publisher">Publisher:</label>
                        <input type="text" id="publisher" name="publisher" class="form-control" required
                               aria-required="true" minlength="2" maxlength="100"
                               aria-describedby="publisher-help publisher-error">
                        <div class="error-message" id="publisher-error" aria-live="polite"></div>
                        <small id="publisher-help" class="form-text">Between 2 and 100 characters</small>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label for="year">Publication Year:</label>
                        <input type="text" id="year" name="year" class="form-control" required
                               aria-required="true" pattern="^(19|20)\d{2}$"
                               aria-describedby="year-help year-error">
                        <div class="error-message" id="year-error" aria-live="polite"></div>
                        <small id="year-help" class="form-text">Enter a valid year (1900-2099)</small>
                    </div>
                </div>

                <button type="submit" class="btn-primary" data-original-text="Add Book">Add Book</button>
            </fieldset>
        </form>
    </section>
</div>

<script>
    // Initialize form validation for search form
    document.getElementById('searchForm').addEventListener('submit', function(e) {
        e.preventDefault();
        const searchType = document.getElementById('searchType');
        const searchQuery = document.getElementById('searchQuery');
        let isValid = true;

        FormValidator.clearError(searchType);
        FormValidator.clearError(searchQuery);

        if (!searchType.value) {
            FormValidator.showError(searchType, 'Please select a search type');
            isValid = false;
        }

        if (searchQuery.value.length < 2) {
            FormValidator.showError(searchQuery, 'Search query must be at least 2 characters long');
            isValid = false;
        }

        if (isValid) {
            FormValidator.showLoading(this);
            this.submit();
        }
    });

    // Initialize form validation for add book form
    document.getElementById('addBookForm').addEventListener('submit', function(e) {
        e.preventDefault();
        if (FormValidator.validateBooksForm(this)) {
            FormValidator.showLoading(this);
            this.submit();
        }
    });
</script>
</body>
</html>
