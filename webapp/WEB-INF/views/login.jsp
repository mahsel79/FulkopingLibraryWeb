<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login - Fulkoping Library</title>
    <link rel="stylesheet" href="styles.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        @media (max-width: 768px) {
            .container {
                padding: 1rem;
            }
            .auth-form {
                width: 100%;
            }
            .form-group {
                width: 100%;
                margin-bottom: 1rem;
            }
            .btn {
                width: 100%;
                padding: 0.75rem;
            }
            .form-links {
                text-align: center;
            }
        }
        @media (min-width: 769px) and (max-width: 1024px) {
            .auth-form {
                width: 80%;
                margin: 0 auto;
            }
        }
        .btn .spinner {
            display: none;
            width: 20px;
            height: 20px;
            border: 3px solid rgba(255,255,255,0.3);
            border-radius: 50%;
            border-top-color: #fff;
            animation: spin 1s ease-in-out infinite;
            margin-left: 8px;
            vertical-align: middle;
        }
        .btn.loading .spinner {
            display: inline-block;
        }
        .btn.loading .button-text {
            display: none;
        }
        @keyframes spin {
            to { transform: rotate(360deg); }
        }
    </style>
</head>
<body>
    <%@ include file="includes/header.jsp" %>
    <%@ include file="includes/navigation.jsp" %>
    <%@ include file="includes/error.jsp" %>

    <div class="container">
        <div class="auth-form">
            <h2>Login to Fulkoping Library</h2>
            
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
                
                <div class="form-group" style="width: 100%;">
                    <button type="submit" class="btn btn-primary" style="width: 100%;" 
                            data-original-text="Login"
                            aria-live="polite"
                            aria-busy="false"
                            id="loginButton">
                        <span class="button-text">Login</span>
                        <span class="spinner" aria-hidden="true"></span>
                    </button>
                </div>
                
                <div class="form-links">
                    <p>Don't have an account? <a href="signup.jsp">Sign up here</a></p>
                    <p><a href="index.jsp">Back to Home</a></p>
                </div>
            </form>
        </div>
    </div>
    
    <%@ include file="includes/footer.jsp" %>
    
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
                const loginButton = document.getElementById('loginButton');
                loginButton.classList.add('loading');
                loginButton.setAttribute('aria-busy', 'true');
                loginButton.disabled = true;
                
                try {
                    this.submit();
                } catch (error) {
                    loginButton.classList.remove('loading');
                    loginButton.setAttribute('aria-busy', 'false');
                    loginButton.disabled = false;
                    console.error('Form submission error:', error);
                }
            }
        });
    </script>
</body>
</html>
