package se.fulkopinglibraryweb.security.filters;

import se.fulkopinglibraryweb.security.ratelimit.RateLimiter;
import se.fulkopinglibraryweb.security.audit.AuditLogger;
import se.fulkopinglibraryweb.security.validation.InputValidator;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SecurityFilter implements Filter {
    private final RateLimiter rateLimiter;

    public SecurityFilter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Add security headers
        addSecurityHeaders(httpResponse);

        // Rate limiting
        String clientIp = getClientIp(httpRequest);
        if (!rateLimiter.tryAcquire(clientIp)) {
            httpResponse.setStatus(RateLimiter.TOO_MANY_REQUESTS);
            AuditLogger.logEvent(clientIp, "RATE_LIMIT_EXCEEDED", "Too many requests");
            return;
        }

        // Input validation for common parameters
        if (!validateRequest(httpRequest)) {
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            AuditLogger.logEvent(clientIp, "INVALID_INPUT", "Request validation failed");
            return;
        }

        // Continue with the request
        chain.doFilter(request, response);
    }

    private void addSecurityHeaders(HttpServletResponse response) {
        // Content Security Policy
        response.setHeader("Content-Security-Policy", 
            "default-src 'self'; " +
            "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
            "style-src 'self' 'unsafe-inline';");

        // XSS Protection
        response.setHeader("X-XSS-Protection", "1; mode=block");

        // Prevent MIME type sniffing
        response.setHeader("X-Content-Type-Options", "nosniff");

        // Clickjacking protection
        response.setHeader("X-Frame-Options", "SAMEORIGIN");

        // HSTS (uncomment if using HTTPS)
        // response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

        // Referrer Policy
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        // Permissions Policy
        response.setHeader("Permissions-Policy", "geolocation=(), microphone=(), camera=()");
    }

    private boolean validateRequest(HttpServletRequest request) {
        // Validate common parameters
        java.util.Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            String value = request.getParameter(name);
            if (InputValidator.containsXSS(value) || InputValidator.containsSQLInjection(value)) {
                return false;
            }
        }
        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @Override
    public void destroy() {
    }
}
