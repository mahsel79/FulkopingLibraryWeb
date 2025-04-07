package se.fulkopinglibraryweb.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fulkopinglibraryweb.exception.ControllerException;
import se.fulkopinglibraryweb.model.Media;
import se.fulkopinglibraryweb.service.interfaces.MediaService;
import se.fulkopinglibraryweb.utils.FirestoreUtil;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/media")
public class MediaController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(MediaController.class);
    private final MediaService mediaService;

    @Inject

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long startTime = System.currentTimeMillis();
        logger.debug("GET request received for media endpoint");
        
        try {
            String searchType = request.getParameter("searchType");
            String searchQuery = request.getParameter("searchQuery");

            List<Media> mediaList;
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                logger.debug("Searching media with type: {} query: {}", searchType, searchQuery);
                mediaList = mediaService.searchMedia(searchType, searchQuery);
                logger.info("Found {} media items matching search (took {} ms)", 
                    mediaList.size(), System.currentTimeMillis() - startTime);
            } else {
                mediaList = mediaService.getAllMedia();
                logger.info("Retrieved all {} media items (took {} ms)", 
                    mediaList.size(), System.currentTimeMillis() - startTime);
            }

            request.setAttribute("mediaList", mediaList);
            request.getRequestDispatcher("/media.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error retrieving media (took {} ms): {}", 
                System.currentTimeMillis() - startTime, e.getMessage(), e);
            throw new ControllerException(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "MEDIA_RETRIEVAL_ERROR",
                "Error retrieving media",
                e.getMessage()
            );
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logger.debug("Media request processed in {} ms", duration);
        }
    }
}
