package se.fulkopinglibraryweb.servlets;

import com.google.gson.Gson;
import se.fulkopinglibraryweb.repository.MagazineRepository;
import se.fulkopinglibraryweb.model.Magazine;
import se.fulkopinglibraryweb.service.MagazineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/magazines/*")
public class MagazineServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(MagazineServlet.class);
    private final MagazineService magazineService;
    private final Gson gson;

    @Inject
    public MagazineServlet(MagazineService magazineService) {
        this.magazineService = magazineService;
        this.gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            String pathInfo = request.getPathInfo();
            String searchType = request.getParameter("searchType");
            String searchQuery = request.getParameter("searchQuery");

            List<Magazine> magazines;
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                if (searchType == null || searchType.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print(gson.toJson("Search type is required when search query is provided"));
                    return;
                }
                logger.info("Searching magazines by: {} = {}", searchType, searchQuery);
                magazines = magazineService.search(searchType, searchQuery);
            } else if (pathInfo != null && !pathInfo.equals("/")) {
                String issn = pathInfo.substring(1);
                magazines = magazineService.search("issn", issn);
                if (magazines.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print(gson.toJson("Magazine not found"));
                    return;
                }
            } else {
                magazines = magazineService.findAll();
            }

            out.print(gson.toJson(magazines));
        } catch (Exception e) {
            logger.error("Error in MagazineServlet.doGet: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson("Internal server error: " + e.getMessage()));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                requestBody.append(line);
            }

            Magazine magazine = gson.fromJson(requestBody.toString(), Magazine.class);
            
            if (magazine.getIssn() == null || magazine.getIssn().trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson("ISSN is required"));
                return;
            }

            magazineService.create(magazine);
            logger.info("New magazine added: {}", magazine.getTitle());
            out.print(gson.toJson(magazine));
        } catch (Exception e) {
            logger.error("Error in MagazineServlet.doPost: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson("Internal server error"));
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson("ISSN is required"));
                return;
            }

            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                requestBody.append(line);
            }

            String issn = pathInfo.substring(1);
            Magazine magazine = gson.fromJson(requestBody.toString(), Magazine.class);
            magazine.setIssn(issn);

            List<Magazine> existingMagazines = magazineService.search("issn", issn);
            if (existingMagazines.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.toJson("Magazine not found"));
                return;
            }

            magazineService.update(magazine);
            logger.info("Magazine updated: {}", magazine.getTitle());
            out.print(gson.toJson(magazine));
        } catch (Exception e) {
            logger.error("Error in MagazineServlet.doPut: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson("Internal server error: " + e.getMessage()));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson("ISSN is required"));
                return;
            }

            String issn = pathInfo.substring(1);
            List<Magazine> magazines = magazineService.search("issn", issn);
            if (!magazines.isEmpty()) {
                magazineService.delete(issn);
                logger.info("Magazine deleted: {}", issn);
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(gson.toJson("Magazine deleted successfully"));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.toJson("Magazine not found"));
            }
        } catch (Exception e) {
            logger.error("Error in MagazineServlet.doDelete: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson("Internal server error: " + e.getMessage()));
        }
    }
}
