package se.fulkopinglibraryweb.controllers;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import se.fulkopinglibraryweb.service.interfaces.UserService;
import se.fulkopinglibraryweb.exception.ControllerException;
import se.fulkopinglibraryweb.dtos.ErrorResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> handleLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session) {
        logger.debug("Login attempt for username: {}", username);

        if (username == null || password == null) {
            throw new ControllerException(
                HttpServletResponse.SC_BAD_REQUEST,
                "MISSING_CREDENTIALS",
                "Missing credentials",
                "Username and password are required"
            );
        }

        try {
            boolean isAuthenticated = userService.authenticateUser(username, password);
            if (isAuthenticated) {
                logger.info("Successful login for user: {}", username);
                session.setAttribute("username", username);
                return ResponseEntity.ok().build();
            } else {
                logger.warn("Failed login attempt for user: {}", username);
                throw new ControllerException(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "INVALID_CREDENTIALS",
                    "Invalid credentials",
                    "The provided username or password is incorrect"
                );
            }
        } catch (Exception e) {
            logger.error("Login error for user {}: {}", username, e.getMessage(), e);
            throw new ControllerException(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "AUTHENTICATION_ERROR",
                "Authentication error",
                e.getMessage()
            );
        }
    }

    @PostMapping("/register") 
    public ResponseEntity<?> handleRegistration(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email) {
        logger.debug("Registration attempt for username: {}, email: {}", username, email);

        if (username == null || password == null || email == null) {
            throw new ControllerException(
                HttpServletResponse.SC_BAD_REQUEST,
                "MISSING_FIELDS",
                "Missing required fields",
                "Username, password and email are required"
            );
        }

        boolean userCreated = userService.createUser(username, email, password, "USER") != null;
        if (!userCreated) {
            logger.warn("Registration failed - username already exists: {}", username);
            throw new ControllerException(
                HttpServletResponse.SC_CONFLICT,
                "USERNAME_EXISTS",
                "Username already exists",
                "The requested username is already taken"
            );
        }
        logger.info("Successfully registered new user: {}", username);
        return ResponseEntity.ok().build();
    }
}
