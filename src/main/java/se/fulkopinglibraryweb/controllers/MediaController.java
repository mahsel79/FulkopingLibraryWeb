package se.fulkopinglibraryweb.controllers;

import se.fulkopinglibraryweb.model.Media;
import se.fulkopinglibraryweb.service.interfaces.MediaService;
import org.slf4j.Logger;
import se.fulkopinglibraryweb.utils.FirestoreUtil;
import se.fulkopinglibraryweb.utils.LoggingUtils;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/media")
public class MediaController extends HttpServlet {
    private static final Logger logger = LoggingUtils.getLogger(MediaController.class);
    private final MediaService mediaService;

    @Inject

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("Entering doGet - searchType: {}, searchQuery: {}", 
            request.getParameter("searchType"), request.getParameter("searchQuery"));
        long startTime = System.currentTimeMillis();
        
        try {
            String searchType = request.getParameter("searchType");
            String searchQuery = request.getParameter("searchQuery");

            List<Media> mediaList;
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                logger.debug("Performing search - type: {}, query: {}", searchType, searchQuery);
                mediaList = mediaService.searchMedia(searchType, searchQuery);
                logger.debug("Search completed - results: {}", mediaList.size());
            } else {
                logger.debug("Fetching all media items");
                mediaList = mediaService.getAllMedia();
                logger.debug("Retrieved media items - count: {}", mediaList.size());
            }

            request.setAttribute("mediaList", mediaList);
            request.getRequestDispatcher("/media.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error processing media request", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving media");
        } finally {
            logger.info("Exiting doGet - execution time: {}ms", 
                System.currentTimeMillis() - startTime);
        }
    }
}
