package se.fulkopinglibraryweb.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fulkopinglibraryweb.model.Loan;
import se.fulkopinglibraryweb.model.User;
import se.fulkopinglibraryweb.service.interfaces.BookService;
import se.fulkopinglibraryweb.service.interfaces.LoanService;
import se.fulkopinglibraryweb.service.interfaces.MagazineService;
import se.fulkopinglibraryweb.service.interfaces.MediaService;
import se.fulkopinglibraryweb.utils.LoggingUtils;

import java.io.IOException;

@WebServlet("/loan")
public class LoanServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(LoanServlet.class);

    private LoanService loanService;

    @Override
    public void init() throws ServletException {
        super.init();
        // Services should be injected via dependency injection
        loanService = (LoanService) getServletContext().getAttribute("loanService");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String itemType = request.getParameter("itemType");
        String itemId = request.getParameter("itemId");
        String action = request.getParameter("action"); // borrow or return
        User user = (User) session.getAttribute("user");

        try {
            boolean success = false;
            switch (itemType.toLowerCase()) {
                case "book":
                case "media":
                case "magazine":
                    if ("borrow".equals(action)) {
                        Loan loan = loanService.borrowItem(user.getId(), itemId);
                        success = loan != null;
                    } else if ("return".equals(action)) {
                        success = loanService.returnItem(user.getId(), itemId);
                    }
                    break;
            }

            if (success) {
                LoggingUtils.logInfo(logger, String.format("%s %s %s %s successfully", user.getUsername(), action, itemType, itemId));
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Success");
            } else {
                LoggingUtils.logWarn(logger, String.format("Failed to %s %s %s for user %s", action, itemType, itemId, user.getUsername()));
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Failed to process request");
            }
        } catch (Exception e) {
            LoggingUtils.logError(logger, String.format("Error processing %s request for %s", action, itemType), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("An error occurred");
        }
    }
}
