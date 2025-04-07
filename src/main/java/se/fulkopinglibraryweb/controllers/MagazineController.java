package se.fulkopinglibraryweb.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fulkopinglibraryweb.exception.ControllerException;
import se.fulkopinglibraryweb.model.Magazine;
import se.fulkopinglibraryweb.service.interfaces.AsyncMagazineService;
import jakarta.inject.Inject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@WebServlet("/magazines/*")
public class MagazineController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(MagazineController.class);
    private final AsyncMagazineService magazineService;

    @Inject
    public MagazineController(AsyncMagazineService magazineService) {
        this.magazineService = magazineService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long startTime = System.currentTimeMillis();
        logger.debug("GET request received for magazines endpoint");
        
        try {
            String searchType = request.getParameter("searchType");
            String searchQuery = request.getParameter("searchQuery");

            CompletableFuture<List<Magazine>> magazinesFuture;
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                logger.debug("Searching magazines with query: {}", searchQuery);
                magazinesFuture = magazineService.searchMagazines(searchQuery);
            } else {
                magazinesFuture = magazineService.getAllMagazines();
            }

            magazinesFuture.thenAccept(magazines -> {
                try {
                    logger.info("Retrieved {} magazines (took {} ms)", 
                        magazines.size(), System.currentTimeMillis() - startTime);
                    request.setAttribute("magazines", magazines);
                    request.getRequestDispatcher("/magazines.jsp").forward(request, response);
                } catch (Exception e) {
                    logger.error("Error processing magazines response (took {} ms): {}", 
                        System.currentTimeMillis() - startTime, e.getMessage(), e);
                    throw new ControllerException(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "MAGAZINE_PROCESSING_ERROR", 
                        "Error processing magazines response",
                        e.getMessage()
                    );
                }
            }).exceptionally(ex -> {
                logger.error("Error retrieving magazines (took {} ms): {}", 
                    System.currentTimeMillis() - startTime, ex.getMessage(), ex);
                throw new ControllerException(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "MAGAZINE_RETRIEVAL_ERROR", 
                    "Error retrieving magazines",
                    ex.getMessage()
                );
            });
        } catch (Exception e) {
            logger.error("Error retrieving magazines (took {} ms): {}", 
                System.currentTimeMillis() - startTime, e.getMessage(), e);
            throw new ControllerException(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "MAGAZINE_RETRIEVAL_ERROR", 
                "Error retrieving magazines",
                e.getMessage()
            );
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logger.debug("Magazine request processed in {} ms", duration);
        }
    }
}
