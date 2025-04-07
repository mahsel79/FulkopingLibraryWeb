<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Book Collection</title>
    <link rel="stylesheet" type="text/css" href="styles.css">
    <script src="js/form-validation.js" defer></script>
    <style>
        @media (max-width: 768px) {
            .container {
                padding: 1rem;
            }
            .form-row {
                flex-direction: column;
            }
            .form-group {
                width: 100%;
                margin-bottom: 1rem;
            }
            .table-responsive {
                overflow-x: auto;
                -webkit-overflow-scrolling: touch;
            }
            .btn-primary {
                width: 100%;
                padding: 0.75rem;
                margin-top: 0.5rem;
                display: flex;
                align-items: center;
                justify-content: center;
                gap: 0.5rem;
            }
            .search-section {
                margin-bottom: 1.5rem;
            }
            .error-message {
                font-size: 0.9rem;
                margin-top: 0.25rem;
            }
        }
        @media (min-width: 769px) and (max-width: 1024px) {
            .form-group {
                width: 48%;
            }
        }
    </style>
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
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
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
                <button type="submit" class="btn-primary" style="width: 100%;" data-original-text="Search" aria-busy="false" id="searchButton">
                    <span class="btn-text">Search</span>
                    <span class="spinner" aria-hidden="true" style="display: none;">
                        <svg width="20" height="20" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                            <path d="M12,1A11,11,0,1,0,23,12,11,11,0,0,0,12,1Zm0,19a8,8,0,1,1,8-8A8,8,0,0,1,12,20Z" opacity=".25"/>
                            <path d="M12,4a8,8,0,0,1,7.89,6.7A1.53,1.53,0,0,0,21.38,12h0a1.5,1.5,0,0,0,1.48-1.75,11,11,0,0,0-21.72,0A1.5,1.5,0,0,0,2.62,12h0a1.53,1.53,0,0,0,1.49-1.3A8,8,0,0,1,12,4Z">
                                <animateTransform attributeName="transform" type="rotate" dur="0.75s" values="0 12 12;360 12 12" repeatCount="indefinite"/>
                            </path>
                        </svg>
                    </span>
                </button>
            </fieldset>
        </form>
    </div>

    <!-- Display Book List -->
    <div class="table-responsive" role="region" aria-label="Book list" style="overflow-x: auto;">
        <table class="table" aria-describedby="bookTableDesc" style="min-width: 600px;">
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
            <c:choose>
                <c:when test="${not empty bookList}">
                    <c:forEach items="${bookList}" var="book">
                        <tr>
                            <td><c:out value="${book.title}"/></td>
                            <td><c:out value="${book.author}"/></td>
                            <td><c:out value="${book.isbn}"/></td>
                            <td><c:out value="${book.publisher}"/></td>
                            <td><c:out value="${book.year}"/></td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td colspan="5">No books found</td>
                    </tr>
                </c:otherwise>
            </c:choose>
            </tbody>
        </table>
    </div>

    <section class="add-book-section" style="margin-top: 2rem;">
        <h2>Add New Book</h2>
        <div id="addBookErrorContainer" class="error-container" role="alert" aria-live="polite"></div>
        <form action="books" method="post" class="form-container" id="addBookForm" novalidate style="max-width: 100%;">
            <fieldset>
                <legend>Book Information</legend>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label for="title">Title:</label>
                        <input type="text" id="title" name="title" class="form-control" required
                               aria-required="true" minlength="1" maxlength="200"
                               aria-describedby="title-help title-error">
                        <div class="error-message" id="title-error" aria-live="polite">
                            <c:if test="${not empty fieldErrors.title}">
                                <c:out value="${fieldErrors.title}"/>
                            </c:if>
                        </div>
                        <small id="title-help" class="form-text">Maximum 200 characters</small>
                    </div>

                    <div class="form-group col-md-6">
                        <label for="author">Author:</label>
                        <input type="text" id="author" name="author" class="form-control" required
                               aria-required="true" minlength="2" maxlength="100"
                               aria-describedby="author-help author-error">
                        <div class="error-message" id="author-error" aria-live="polite">
                            <c:if test="${not empty fieldErrors.author}">
                                <c:out value="${fieldErrors.author}"/>
                            </c:if>
                        </div>
                        <small id="author-help" class="form-text">Between 2 and 100 characters</small>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label for="isbn">ISBN:</label>
                        <input type="text" id="isbn" name="isbn" class="form-control" required
                               aria-required="true" pattern="^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$"
                               aria-describedby="isbn-help isbn-error">
                        <div class="error-message" id="isbn-error" aria-live="polite">
                            <c:if test="${not empty fieldErrors.isbn}">
                                <c:out value="${fieldErrors.isbn}"/>
                            </c:if>
                        </div>
                        <small id="isbn-help" class="form-text">Enter a valid ISBN number</small>
                    </div>

                    <div class="form-group col-md-6">
                        <label for="publisher">Publisher:</label>
                        <input type="text" id="publisher" name="publisher" class="form-control" required
                               aria-required="true" minlength="2" maxlength="100"
                               aria-describedby="publisher-help publisher-error">
                        <div class="error-message" id="publisher-error" aria-live="polite">
                            <c:if test="${not empty fieldErrors.publisher}">
                                <c:out value="${fieldErrors.publisher}"/>
                            </c:if>
                        </div>
                        <small id="publisher-help" class="form-text">Between 2 and 100 characters</small>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label for="year">Publication Year:</label>
                        <input type="text" id="year" name="year" class="form-control" required
                               aria-required="true" pattern="^(19|20)\d{2}$"
                               aria-describedby="year-help year-error">
                        <div class="error-message" id="year-error" aria-live="polite">
                            <c:if test="${not empty fieldErrors.year}">
                                <c:out value="${fieldErrors.year}"/>
                            </c:if>
                        </div>
                        <small id="year-help" class="form-text">Enter a valid year (1900-2099)</small>
                    </div>
                </div>

                <button type="submit" class="btn-primary" style="width: 100%; margin-top: 1rem;" data-original-text="Add Book" aria-busy="false" id="addBookButton">
                    <span class="btn-text">Add Book</span>
                    <span class="spinner" aria-hidden="true" style="display: none;">
                        <svg width="20" height="20" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                            <path d="M12,1A11,11,0,1,0,23,12,11,11,0,0,0,12,1Zm0,19a8,8,0,1,1,8-8A8,8,0,0,1,12,20Z" opacity=".25"/>
                            <path d="M12,4a8,8,0,0,1,7.89,6.7A1.53,1.53,0,0,0,21.38,12h0a1.5,1.5,0,0,0,1.48-1.75,11,11,0,0,0-21.72,0A1.5,1.5,0,0,0,2.62,12h0a1.53,1.53,0,0,0,1.49-1.3A8,8,0,0,1,12,4Z">
                                <animateTransform attributeName="transform" type="rotate" dur="0.75s" values="0 12 12;360 12 12" repeatCount="indefinite"/>
                            </path>
                        </svg>
                    </span>
                </button>
            </fieldset>
        </form>
    </section>
</div>

<script src="js/form-validation.js" defer></script>
<script>
document.addEventListener('DOMContentLoaded', function() {
    const searchForm = document.getElementById('searchForm');
    const searchButton = document.getElementById('searchButton');
    const addBookForm = document.getElementById('addBookForm');
    const addBookButton = document.getElementById('addBookButton');

    if (searchForm) {
        searchForm.addEventListener('submit', function() {
            const buttonText = searchButton.querySelector('.btn-text');
            const spinner = searchButton.querySelector('.spinner');
            
            searchButton.setAttribute('aria-busy', 'true');
            searchButton.disabled = true;
            buttonText.textContent = 'Searching...';
            spinner.style.display = 'inline-block';
        });
    }

    if (addBookForm) {
        addBookForm.addEventListener('submit', function() {
            const buttonText = addBookButton.querySelector('.btn-text');
            const spinner = addBookButton.querySelector('.spinner');
            
            addBookButton.setAttribute('aria-busy', 'true');
            addBookButton.disabled = true;
            buttonText.textContent = 'Adding...';
            spinner.style.display = 'inline-block';
        });
    }
});
</script>
</body>
</html>
