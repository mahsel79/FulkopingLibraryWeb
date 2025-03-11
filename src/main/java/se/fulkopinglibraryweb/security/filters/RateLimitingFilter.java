package se.fulkopinglibraryweb.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import se.fulkopinglibraryweb.security.ratelimit.RateLimiter;

import java.io.IOException;

public class RateLimitingFilter extends OncePerRequestFilter {
    private static final String[] PROTECTED_PATHS = {
        "/login",
        "/signup",
        "/api/auth/**"
    };

    private final RateLimiter rateLimiter;

    public RateLimitingFilter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        String clientIp = getClientIp(request);

        if (isProtectedPath(path)) {
            if (!rateLimiter.tryAcquire(clientIp)) {
                response.setStatus(429); // 429 Too Many Requests
                response.getWriter().write("Too many requests");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isProtectedPath(String path) {
        for (String protectedPath : PROTECTED_PATHS) {
            if (path.startsWith(protectedPath)) {
                return true;
            }
        }
        return false;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
