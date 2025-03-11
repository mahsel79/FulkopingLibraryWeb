package se.fulkopinglibraryweb.controllers;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import se.fulkopinglibraryweb.service.interfaces.UserService;
import se.fulkopinglibraryweb.utils.LoggingUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@WebServlet("/user/*")
public class UserController extends HttpServlet {
    private final UserService userService;
    private final LoggingUtils logger = new LoggingUtils(UserController.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
        logger.info(String.format("UserController initialized with service: %s", userService.getClass().getSimpleName()));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.logMethodEntry("doPost", request.getPathInfo());
        
        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null) {
                logger.error("Missing path info in request", null);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            switch (pathInfo) {
                case "/login":
                    handleLogin(request, response);
                    break;
                case "/register":
                    handleRegistration(request, response);
                    break;
                default:
                    logger.error(String.format("Invalid path: %s", pathInfo), null);
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } finally {
            logger.logMethodExit(logger.getLogger(UserController.class), "doPost", Optional.empty());
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.logMethodEntry("handleLogin");
        
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            if (username == null || password == null) {
                logger.error("Login attempt with missing credentials", null);
                response.sendRedirect("login.jsp?error=Username and password required");
                return;
            }

            logger.info(String.format("Login attempt for user: %s", username));
            
            try {
                boolean isAuthenticated = userService.authenticateUser(username, password);
                if (isAuthenticated) {
                    logger.info(String.format("Successful login for user: %s", username));
                    HttpSession session = request.getSession();
                    session.setAttribute("username", username);
                    response.sendRedirect("dashboard.jsp");
                } else {
                    logger.error(String.format("Failed login attempt for user: %s", username), null);
                    response.sendRedirect("login.jsp?error=Invalid credentials");
                }
            } catch (Exception e) {
                logger.error(String.format("Authentication failed for user: %s", username), e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } finally {
            logger.logMethodExit(logger.getLogger(UserController.class), "handleLogin", Optional.empty());
        }
    }

    private void handleRegistration(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.logMethodEntry("handleRegistration");
        
        try {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String email = request.getParameter("email");

            if (username == null || password == null || email == null) {
                logger.error("Registration attempt with missing fields", null);
                response.sendRedirect("register.jsp?error=All fields required");
                return;
            }

            logger.info(String.format("Registration attempt for user: %s", username));
            
            boolean userCreated = userService.createUser(username, email, password, "USER") != null;
            if (userCreated) {
                logger.info(String.format("Successfully created user: %s", username));
                response.sendRedirect("login.jsp?success=Account created");
            } else {
                logger.error(String.format("Failed to create user - username already exists: %s", username), null);
                response.sendRedirect("register.jsp?error=Username already exists");
            }
        } finally {
            logger.logMethodExit(logger.getLogger(UserController.class), "handleRegistration", Optional.empty());
        }
    }
}
