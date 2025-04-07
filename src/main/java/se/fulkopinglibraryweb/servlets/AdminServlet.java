package se.fulkopinglibraryweb.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import se.fulkopinglibraryweb.model.User;
import se.fulkopinglibraryweb.model.Loan;
import se.fulkopinglibraryweb.model.LoanStatus;
import se.fulkopinglibraryweb.service.interfaces.UserService;
import se.fulkopinglibraryweb.service.interfaces.LoanService;
import se.fulkopinglibraryweb.service.interfaces.BookService;
import se.fulkopinglibraryweb.service.interfaces.MediaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import se.fulkopinglibraryweb.model.Book;
import se.fulkopinglibraryweb.model.Media;

@WebServlet("/admin/*")
public class AdminServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AdminServlet.class);
    private final UserService userService;
    private final LoanService loanService;
    private final BookService bookService;
    private final MediaService mediaService;

    public AdminServlet(UserService userService, LoanService loanService,
                        BookService bookService, MediaService mediaService) {
        this.userService = userService;
        this.loanService = loanService;
        this.bookService = bookService;
        this.mediaService = mediaService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            List<User> users = userService.findAll();
            List<Media> media = mediaService.getAllMedia();
            List<Loan> overdueLoans = loanService.getLoansByStatus(LoanStatus.OVERDUE);
            List<Book> books = bookService.findAll();

            request.setAttribute("users", users);
            request.setAttribute("overdueLoans", overdueLoans);

            int totalUsers = users.size();
            int activeLoans = 0;
            int overdueItems = overdueLoans.size();
            int totalItems = books.size() + media.size();

            request.setAttribute("totalUsers", totalUsers);
            request.setAttribute("activeLoans", activeLoans);
            request.setAttribute("overdueItems", overdueItems);
            request.setAttribute("totalItems", totalItems);

            request.getRequestDispatcher("/WEB-INF/views/admin.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error in AdminServlet.doGet: {}", e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading admin dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            if (pathInfo.equals("/users")) {
                handleUserAction(request, response);
            } else if (pathInfo.equals("/loans")) {
                handleLoanAction(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in AdminServlet.doPost: {}", e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request");
        }
    }

    private void handleUserAction(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userId = request.getParameter("userId");
        String action = request.getParameter("action");

        if (userId == null || action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
            return;
        }

        boolean success = false;
        try {
            if (action.equals("activate")) {
                userService.updateUserStatus(userId, true);
                success = true;
            } else if (action.equals("deactivate")) {
                userService.updateUserStatus(userId, false);
                success = true;
            }
        } catch (Exception e) {
            logger.error("Error updating user status: {}", e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error updating user status");
            return;
        }

        if (success) {
            response.sendRedirect(request.getContextPath() + "/admin");
        } else {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update user status");
        }
    }

    private void handleLoanAction(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String loanId = request.getParameter("loanId");
        String action = request.getParameter("action");

        if (loanId == null || action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
            return;
        }

        if (action.equals("extend")) {
            // Implement loan extension logic here
            response.sendRedirect(request.getContextPath() + "/admin");
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }
}
