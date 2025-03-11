package se.fulkopinglibraryweb.servlets;

import se.fulkopinglibraryweb.model.Media;
import se.fulkopinglibraryweb.service.interfaces.MediaService;
import se.fulkopinglibraryweb.utils.LoggingUtils;
import se.fulkopinglibraryweb.model.ItemType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
@WebServlet("/media")
public class MediaServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(MediaServlet.class);
    
    @Autowired
    private MediaService mediaService;

    @Override
    public void init() throws ServletException {
        super.init();
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, 
            getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            String searchType = request.getParameter("searchType");
            String searchQuery = request.getParameter("searchQuery");

            List<Media> mediaList;
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                LoggingUtils.logInfo(logger, "Performing search: " + searchType + " = " + searchQuery);
                mediaList = mediaService.searchMedia(searchType, searchQuery);
            } else {
                mediaList = mediaService.getAllMedia();
            }

            request.setAttribute("mediaList", mediaList);
            request.getRequestDispatcher("media.jsp").forward(request, response);
        } catch (Exception e) {
            LoggingUtils.logError(logger, "Error in doGet: " + e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error processing request");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            String title = request.getParameter("title");
            String creator = request.getParameter("creator");
            int releaseYear = Integer.parseInt(request.getParameter("releaseYear"));

            Media newMedia = new Media();
            newMedia.setTitle(title);
            newMedia.setDirector(creator);
            newMedia.setReleaseYear(releaseYear);
            newMedia.setType(ItemType.MEDIA);

            mediaService.saveMedia(newMedia);

            LoggingUtils.logInfo(logger, "New media item added: " + title);
            response.sendRedirect("media");
        } catch (Exception e) {
            LoggingUtils.logError(logger, "Error in doPost: " + e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error processing request");
        }
    }
}
