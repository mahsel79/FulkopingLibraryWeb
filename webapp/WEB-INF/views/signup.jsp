<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Sign Up - Fulköping Library</title>
    <link rel="stylesheet" type="text/css" href="styles.css" />
    <script src="js/form-validation.js" defer></script>
    <style>
      @media (max-width: 768px) {
        .form-row {
          flex-direction: column;
        }
        .form-group {
          width: 100%;
          margin-bottom: 1rem;
        }
        .btn-primary {
          width: 100%;
          padding: 0.75rem;
        }
        .container {
          padding: 1rem;
        }
      }
      @media (min-width: 769px) and (max-width: 1024px) {
        .form-group {
          width: 48%;
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
    <div class="container" style="max-width: 100%">
      <h1>Create an Account</h1>
      <c:if test="${not empty error}">
        <div class="error-message" role="alert">
          ${fn:replace(error, '\n', '<br />')}
        </div>
      </c:if>
      <div
        id="signupErrorContainer"
        class="error-container"
        role="alert"
        aria-live="polite"
      ></div>
      <form
        action="signup"
        method="post"
        class="form-container"
        id="signupForm"
        novalidate
      >
        <div class="form-group" style="width: 100%">
          <label for="username">Username:</label>
          <input
            type="text"
            id="username"
            name="username"
            class="form-control"
            required
            aria-required="true"
            minlength="3"
            maxlength="50"
          />
          <div class="error-message" id="username-error"></div>
          <small class="form-text">Username must be 3-50 characters long</small>
        </div>
        <div class="form-group">
          <label for="email">Email:</label>
          <input
            type="email"
            id="email"
            name="email"
            class="form-control"
            required
            aria-required="true"
            pattern="[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$"
          />
          <div class="error-message" id="email-error"></div>
          <small class="form-text">Please enter a valid email address</small>
        </div>
        <div class="form-group">
          <label for="password">Password:</label>
          <input
            type="password"
            id="password"
            name="password"
            class="form-control"
            required
            aria-required="true"
            pattern="^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$"
          />
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
          <input
            type="password"
            id="confirmPassword"
            name="confirmPassword"
            class="form-control"
            required
            aria-required="true"
          />
          <div class="error-message" id="confirmPassword-error"></div>
        </div>
        <div class="form-actions" style="width: 100%">
          <button type="submit" class="btn-primary" style="width: 100%" 
                  id="signupButton"
                  aria-live="polite"
                  aria-busy="false">
            <span class="button-text">Sign Up</span>
            <span class="spinner" aria-hidden="true"></span>
          </button>
          <p class="login-link">
            Already have an account? <a href="login.jsp">Login here</a>
          </p>
        </div>
      </form>
    </div>
    <script>
      document.getElementById('signupForm').addEventListener('submit', function(e) {
        e.preventDefault();
        const isValid = FormValidator.validateForm(this);
        
        if (isValid) {
          const signupButton = document.getElementById('signupButton');
          signupButton.classList.add('loading');
          signupButton.setAttribute('aria-busy', 'true');
          signupButton.disabled = true;
          
          try {
            this.submit();
          } catch (error) {
            signupButton.classList.remove('loading');
            signupButton.setAttribute('aria-busy', 'false');
            signupButton.disabled = false;
            console.error('Form submission error:', error);
          }
        }
      });
    </script>
  </body>
</html>
