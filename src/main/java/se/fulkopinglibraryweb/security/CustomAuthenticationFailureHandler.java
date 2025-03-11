package se.fulkopinglibraryweb.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, 
            HttpServletResponse response, 
            AuthenticationException exception) throws IOException, ServletException {
        
        String username = request.getParameter("username");
        String ip = getClientIP(request);
        
        if (username != null) {
            loginAttemptService.loginFailed(username);
            loginAttemptService.loginFailed(ip);
        }

        int remainingAttempts = loginAttemptService.getRemainingAttempts(username);
        if (remainingAttempts <= 0) {
            exception = new AuthenticationException("Account locked due to too many failed attempts. Please try again later.") {};
        } else {
            exception = new AuthenticationException("Invalid username or password. Remaining attempts: " + remainingAttempts) {};
        }

        super.setDefaultFailureUrl("/login?error");
        super.onAuthenticationFailure(request, response, exception);
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
