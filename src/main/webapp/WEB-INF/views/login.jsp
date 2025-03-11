<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login - Fulkoping Library</title>
    <link rel="stylesheet" href="styles.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
    <div class="container">
        <div class="auth-form">
            <h2>Login to Fulkoping Library</h2>
            
            <div id="loginErrorContainer" class="error-container" role="alert" aria-live="polite">
                <c:if test="${param.error != null}">
                    <div class="error-message">
                        ${param.error}
                    </div>
                </c:if>
                
                <c:if test="${param.registered == 'true'}">
                    <div class="alert alert-success">
                        Registration successful! Please login.
                    </div>
                </c:if>
            </div>
            
            <form action="login" method="POST" class="login-form" id="loginForm" novalidate>
                <!-- CSRF Protection -->
                <input type="hidden" name="csrf_token" value="${pageContext.session.id}">
                
                <div class="form-group">
                    <label for="username">Username</label>
                    <input type="text" id="username" name="username" required 
                           pattern="[a-zA-Z0-9]{3,}" 
                           title="Username must be at least 3 characters long and contain only letters and numbers"
                           class="form-control">
                    <div class="error-message" id="username-error"></div>
                    <small class="form-text">Username must be at least 3 characters long</small>
                </div>
                
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required 
                           minlength="8"
                           class="form-control">
                    <div class="error-message" id="password-error"></div>
                    <small class="form-text">Password must be at least 8 characters long</small>
                </div>
                
                <div class="form-group">
                    <button type="submit" class="btn btn-primary" data-original-text="Login">Login</button>
                </div>
                
                <div class="form-links">
                    <p>Don't have an account? <a href="signup.jsp">Sign up here</a></p>
                    <p><a href="index.jsp">Back to Home</a></p>
                </div>
            </form>
        </div>
    </div>
    
    <script src="js/form-validation.js"></script>
    <script>
        // Prevent form resubmission on page refresh
        if (window.history.replaceState) {
            window.history.replaceState(null, null, window.location.href);
        }
        
        // Initialize form validation
        document.getElementById('loginForm').addEventListener('submit', function(e) {
            e.preventDefault();
            const username = document.getElementById('username');
            const password = document.getElementById('password');
            let isValid = true;
            
            // Clear previous errors
            FormValidator.clearError(username);
            FormValidator.clearError(password);
            
            if (username.value.length < 3) {
                FormValidator.showError(username, 'Username must be at least 3 characters long');
                isValid = false;
            }
            
            if (password.value.length < 8) {
                FormValidator.showError(password, 'Password must be at least 8 characters long');
                isValid = false;
            }
            
            if (isValid) {
                FormValidator.showLoading(this);
                this.submit();
            }
        });
    </script>
</body>
</html>
