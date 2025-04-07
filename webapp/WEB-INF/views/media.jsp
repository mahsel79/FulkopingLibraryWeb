<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Media Collection</title>
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
    <h1>Library Media Collection</h1>

    <!-- Error Messages Container -->
    <div id="globalErrorContainer" class="error-container" role="alert" aria-live="polite">
        <c:if test="${param.error != null}">
            <div class="error-message"><c:out value="${param.error}"/></div>
        </c:if>
        <c:if test="${param.success != null}">
            <div class="alert alert-success"><c:out value="${param.success}"/></div>
        </c:if>
        <c:if test="${not empty errors}">
            <c:forEach items="${errors}" var="error">
                <div class="error-message"><c:out value="${error}"/></div>
            </c:forEach>
        </c:if>
    </div>

    <!-- Search Form -->
    <div class="search-section">
        <form action="media" method="get" class="search-form" role="search" id="searchForm" novalidate>
            <fieldset>
                <legend>Search Media</legend>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <div class="form-group">
                    <label for="searchType">Search by:</label>
                    <select name="searchType" id="searchType" class="form-control" required aria-required="true">
                        <option value="">Select search type</option>
                        <option value="title">Title</option>
                        <option value="publisher">Publisher</option>
                        <option value="catalogNumber">Catalog Number</option>
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
                    <span class="button-text">Search</span>
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

    <!-- Display Media List -->
    <div class="table-responsive" role="region" aria-label="Media list" style="overflow-x: auto;">
        <table class="table" aria-describedby="mediaTableDesc" style="min-width: 600px;">
            <caption id="mediaTableDesc">List of available media items in the library</caption>
            <thead>
            <tr>
                <th scope="col">Title</th>
                <th scope="col">Type</th>
                <th scope="col">Publisher</th>
                <th scope="col">Catalog Number</th>
                <th scope="col">Year</th>
            </tr>
            </thead>
            <tbody>
            <c:choose>
                <c:when test="${not empty mediaList}">
                    <c:forEach items="${mediaList}" var="media">
                        <tr>
                            <td><c:out value="${media.title}"/></td>
                            <td><c:out value="${media.type}"/></td>
                            <td><c:out value="${media.publisher}"/></td>
                            <td><c:out value="${media.catalogNumber}"/></td>
                            <td><c:out value="${media.year}"/></td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td colspan="5">No media items found</td>
                    </tr>
                </c:otherwise>
            </c:choose>
            </tbody>
        </table>
    </div>

    <section class="add-media-section" style="margin-top: 2rem;">
        <h2>Add New Media</h2>
        <div id="addMediaErrorContainer" class="error-container" role="alert" aria-live="polite"></div>
        <form action="media" method="post" class="form-container" id="addMediaForm" novalidate style="max-width: 100%;">
            <fieldset>
                <legend>Media Information</legend>
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
                        <label for="type">Type:</label>
                        <select name="type" id="type" class="form-control" required
                                aria-required="true" aria-describedby="type-help type-error">
                            <option value="">Select media type</option>
                            <option value="Book">Book</option>
                            <option value="Magazine">Magazine</option>
                            <option value="DVD">DVD</option>
                            <option value="CD">CD</option>
                        </select>
                        <div class="error-message" id="type-error" aria-live="polite">
                            <c:if test="${not empty fieldErrors.type}">
                                <c:out value="${fieldErrors.type}"/>
                            </c:if>
                        </div>
                        <small id="type-help" class="form-text">Select the media type</small>
                    </div>
                </div>

                <div class="form-row">
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

                    <div class="form-group col-md-6">
                        <label for="catalogNumber">Catalog Number:</label>
                        <input type="text" id="catalogNumber" name="catalogNumber" class="form-control" required
                               aria-required="true" pattern="^[A-Za-z0-9-]+$"
                               aria-describedby="catalogNumber-help catalogNumber-error">
                        <div class="error-message" id="catalogNumber-error" aria-live="polite">
                            <c:if test="${not empty fieldErrors.catalogNumber}">
                                <c:out value="${fieldErrors.catalogNumber}"/>
                            </c:if>
                        </div>
                        <small id="catalogNumber-help" class="form-text">Alphanumeric with hyphens only</small>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group col-md-6">
                        <label for="year">Publication Year:</label>
                        <input type="number" id="year" name="year" class="form-control" required
                               aria-required="true" min="1900" max="2099"
                               aria-describedby="year-help year-error">
                        <div class="error-message" id="year-error" aria-live="polite">
                            <c:if test="${not empty fieldErrors.year}">
                                <c:out value="${fieldErrors.year}"/>
                            </c:if>
                        </div>
                        <small id="year-help" class="form-text">Enter a valid year (1900-2099)</small>
                    </div>
                </div>

                <button type="submit" class="btn-primary" style="width: 100%; margin-top: 1rem;" data-original-text="Add Media" aria-busy="false" id="addMediaButton">
                    <span class="button-text">Add Media</span>
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
    const addMediaForm = document.getElementById('addMediaForm');
    const addMediaButton = document.getElementById('addMediaButton');

    if (searchForm) {
        searchForm.addEventListener('submit', function() {
            const buttonText = searchButton.querySelector('.button-text');
            const spinner = searchButton.querySelector('.spinner');
            
            searchButton.setAttribute('aria-busy', 'true');
            searchButton.disabled = true;
            buttonText.textContent = 'Searching...';
            spinner.style.display = 'inline-block';
        });
    }

    if (addMediaForm) {
        addMediaForm.addEventListener('submit', function() {
            const buttonText = addMediaButton.querySelector('.button-text');
            const spinner = addMediaButton.querySelector('.spinner');
            
            addMediaButton.setAttribute('aria-busy', 'true');
            addMediaButton.disabled = true;
            buttonText.textContent = 'Adding...';
            spinner.style.display = 'inline-block';
        });
    }
});
</script>
</body>
</html>
