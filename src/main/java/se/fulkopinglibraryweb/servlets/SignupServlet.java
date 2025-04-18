package se.fulkopinglibraryweb.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import se.fulkopinglibraryweb.repository.UserRepository;
import se.fulkopinglibraryweb.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fulkopinglibraryweb.utils.PasswordUtils;

import java.io.IOException;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(SignupServlet.class);
    private final UserService userService;
    private final UserRepository userRepository;

    public SignupServlet(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String email = request.getParameter("email");

        // Validate input
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                confirmPassword == null || confirmPassword.trim().isEmpty() ||
                email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "All fields are required");
            request.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(request, response);
            return;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match");
            request.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(request, response);
            return;
        }

        // Validate password strength
        if (!PasswordUtils.isValidPassword(password)) {
            request.setAttribute("error", PasswordUtils.PASSWORD_REQUIREMENTS);
            request.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(request, response);
            return;
        }

        try {
            // Attempt to create new user
            if (userService.createUser(username, email, password, "USER") != null) {
                logger.info("New user registered successfully: {}", username);
                response.sendRedirect("login.jsp?registered=true");
            } else {
                logger.warn("Failed to register user: {}", username);
                request.setAttribute("error", "Username already exists");
                request.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(request, response);
            }
        } catch (Exception e) {
            logger.error("Error during user registration: {}", e.getMessage(), e);
            request.setAttribute("error", "An error occurred during registration");
            request.getRequestDispatcher("/WEB-INF/views/signup.jsp").forward(request, response);
        }
    }
}
