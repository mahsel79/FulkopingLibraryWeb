package se.fulkopinglibraryweb.security.filters;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CsrfRequestMatcher implements RequestMatcher {
    private static final Set<String> ALLOWED_METHODS = new HashSet<>(Arrays.asList("GET", "HEAD", "TRACE", "OPTIONS"));
    private static final Set<String> EXCLUDED_PATHS = new HashSet<>(Arrays.asList(
        "/api/public/**",
        "/api/auth/**",
        "/h2-console/**"
    ));

    @Override
    public boolean matches(HttpServletRequest request) {
        // Skip CSRF check for allowed methods
        if (ALLOWED_METHODS.contains(request.getMethod())) {
            return false;
        }

        // Skip CSRF check for excluded paths
        String path = request.getRequestURI();
        for (String excludedPath : EXCLUDED_PATHS) {
            if (path.startsWith(excludedPath.replace("/**", ""))) {
                return false;
            }
        }

        // Apply CSRF protection to all other requests
        return true;
    }
}
