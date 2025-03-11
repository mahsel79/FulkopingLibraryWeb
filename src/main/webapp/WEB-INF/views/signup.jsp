<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Sign Up - Fulköping Library</title>
    <link rel="stylesheet" type="text/css" href="styles.css">
    <script src="js/form-validation.js" defer></script>
</head>
<body>
    <div class="container">
        <h1>Create an Account</h1>
        <% if (request.getAttribute("error") != null) { %>
            <div class="error-message" role="alert">
                <%= request.getAttribute("error").toString().replace("\n", "<br/>") %>
            </div>
        <% } %>
        <div id="signupErrorContainer" class="error-container" role="alert" aria-live="polite"></div>
        <form action="signup" method="post" class="form-container" id="signupForm" novalidate>
            <div class="form-group">
                <label for="username">Username:</label>
                <input type="text" id="username" name="username" class="form-control" required
                       aria-required="true" minlength="3" maxlength="50">
                <div class="error-message" id="username-error"></div>
                <small class="form-text">Username must be 3-50 characters long</small>
            </div>
            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" class="form-control" required
                       aria-required="true" pattern="[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$">
                <div class="error-message" id="email-error"></div>
                <small class="form-text">Please enter a valid email address</small>
            </div>
            <div class="form-group">
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" class="form-control" required
                       aria-required="true"
                       pattern="^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$">
                <div class="error-message" id="password-error"></div>
                <small class="form-text">
                    Password requirements:
                    <ul>
                        <li>At least 8 characters long</li>
                        <li>Contains uppercase and lowercase letters</li>
                        <li>Contains numbers and special characters</li>
                    </ul>
                </small>
            </div>
            <div class="form-group">
                <label for="confirmPassword">Confirm Password:</label>
                <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" required
                       aria-required="true">
                <div class="error-message" id="confirmPassword-error"></div>
            </div>
            <div class="form-actions">
                <button type="submit" class="btn-primary">Sign Up</button>
                <p class="login-link">Already have an account? <a href="login.jsp">Login here</a></p>
            </div>
        </form>
    </div>
</body>
</html>