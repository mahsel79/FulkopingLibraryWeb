package se.fulkopinglibraryweb.controllers;

import se.fulkopinglibraryweb.model.Magazine;
import se.fulkopinglibraryweb.service.MagazineService;
import se.fulkopinglibraryweb.utils.LoggingUtils;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/magazines/*")
public class MagazineController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(MagazineController.class);
    private final MagazineService magazineService;

    @Inject
    public MagazineController(MagazineService magazineService) {
        this.magazineService = magazineService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("Entering doGet - searchType: {}, searchQuery: {}", 
            request.getParameter("searchType"), request.getParameter("searchQuery"));
        long startTime = System.currentTimeMillis();
        
        try {
            String searchType = request.getParameter("searchType");
            String searchQuery = request.getParameter("searchQuery");

            List<Magazine> magazines;
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                logger.debug("Performing search - type: {}, query: {}", searchType, searchQuery);
                magazines = magazineService.search(searchType, searchQuery);
                logger.debug("Found {} magazines matching search", magazines.size());
            } else {
                logger.debug("Fetching all magazines");
                magazines = magazineService.getAll();
                logger.debug("Retrieved {} magazines", magazines.size());
            }

            request.setAttribute("magazines", magazines);
            request.getRequestDispatcher("/magazines.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error processing magazine request", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving magazines");
        } finally {
            logger.info("Exiting doGet - execution time: {}ms", 
                System.currentTimeMillis() - startTime);
        }
    }
}
