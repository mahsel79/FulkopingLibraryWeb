package se.fulkopinglibraryweb.servlets;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import se.fulkopinglibraryweb.service.interfaces.SearchService;
import se.fulkopinglibraryweb.service.SearchServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fulkopinglibraryweb.service.search.SearchCriteria;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(SearchServlet.class);
    private final SearchServiceFactory searchServiceFactory;

    @Inject
    public SearchServlet(SearchServiceFactory searchServiceFactory) {
        this.searchServiceFactory = searchServiceFactory;
    }
    private static final double FUZZY_SEARCH_THRESHOLD = 0.8;
    private static final int DEFAULT_PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String query = request.getParameter("query");
        String searchType = request.getParameter("searchType"); // title, author, isbn, etc.
        String itemType = request.getParameter("itemType"); // book, magazine, media
        String fuzzyParam = request.getParameter("fuzzy");
        String pageParam = request.getParameter("page");

        boolean fuzzySearch = "true".equalsIgnoreCase(fuzzyParam);
        try {
            SearchService searchService = searchServiceFactory.getSearchService(itemType);
            List<?> searchResults;

            if (fuzzySearch) {
                searchResults = searchService.search(query, searchType, Map.of("fuzzy", true, "threshold", FUZZY_SEARCH_THRESHOLD));
            } else {
                SearchCriteria criteria = new SearchCriteria();
                criteria.setSearchTerm(query);
                criteria.setFilterField(searchType);
                
                searchResults = searchService.advancedSearch(criteria, itemType);
            }

            request.setAttribute("searchResults", searchResults);
            request.setAttribute("searchQuery", query);
            request.setAttribute("searchType", searchType);
            request.setAttribute("itemType", itemType);
            request.setAttribute("fuzzySearch", fuzzySearch);

            request.getRequestDispatcher("/WEB-INF/views/search-results.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid search parameters in SearchServlet: {}", e.getMessage(), e);
            request.setAttribute("error", "Invalid search parameters: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/search-results.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Unexpected error in SearchServlet: {}", e.getMessage(), e);
            request.setAttribute("error", "An error occurred during search");
            request.getRequestDispatcher("search-results.jsp").forward(request, response);
        }
    }
}
