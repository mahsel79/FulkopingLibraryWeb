package se.fulkopinglibraryweb.servlets;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import se.fulkopinglibraryweb.model.User;
import se.fulkopinglibraryweb.service.interfaces.UserService;
import se.fulkopinglibraryweb.utils.LoggingUtils;
import se.fulkopinglibraryweb.utils.PasswordUtils;

import java.io.IOException;

@WebServlet(value = "/login", asyncSupported = true)
public class LoginServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private final UserService userService;

    @Inject
    public LoginServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect("dashboard.jsp");
            return;
        }
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        userService.findByUsername(username).thenAccept(userOpt -> {
            try {
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    if (PasswordUtils.verifyPassword(password, user.getPasswordHash(), user.getSalt())) {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", user);
                        session.setAttribute("username", user.getUsername());
                        session.setAttribute("role", user.getRole());
                        
                        LoggingUtils.logInfo(logger, "User logged in successfully: " + username);
                        response.sendRedirect("dashboard.jsp");
                    } else {
                        String message = "Failed login attempt for username: " + username;
                        LoggingUtils.logWarn(logger, message);
                        request.setAttribute("error", "Invalid username or password");
                        request.getRequestDispatcher("login.jsp").forward(request, response);
                    }
                } else {
                    String message = "Failed login attempt for username: " + username;
                    LoggingUtils.logWarn(logger, message);
                    request.setAttribute("error", "Invalid username or password");
                    request.getRequestDispatcher("login.jsp").forward(request, response);
                }
            } catch (Exception e) {
                LoggingUtils.logError(logger, "Error during login", e);
                try {
                    request.setAttribute("error", "An error occurred during login");
                    request.getRequestDispatcher("login.jsp").forward(request, response);
                } catch (Exception ex) {
                    LoggingUtils.logError(logger, "Error handling login error", ex);
                }
            }
        }).exceptionally(ex -> {
            LoggingUtils.logError(logger, "Error finding user", ex);
            try {
                request.setAttribute("error", "An error occurred during login");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            } catch (Exception e) {
                LoggingUtils.logError(logger, "Error handling login error", e);
            }
            return null;
        });
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String username = (String) session.getAttribute("username");
            LoggingUtils.logInfo(logger, "User logged out: " + username);
            session.invalidate();
        }
        response.sendRedirect("login.jsp");
    }
}
