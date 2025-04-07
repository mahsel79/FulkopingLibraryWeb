package se.fulkopinglibraryweb.service;

import se.fulkopinglibraryweb.model.LibraryItem;
import se.fulkopinglibraryweb.service.search.SearchCriteria;
import java.util.List;

public interface SearchService<T> {
    /**
     * Searches for library items with advanced options
     * @param query The search query
     * @param searchType The type of search (title, author, isbn, etc)
     * @param page The page number for pagination (1-based)
     * @param pageSize Number of results per page
     * @param sortField Field to sort by (title, author, etc)
     * @param sortOrder Sort order (ASC/DESC)
     * @return List of matching LibraryItems
     */
    List<T> search(String query, String type, SearchCriteria criteria) throws Exception;

    List<T> fuzzySearch(String query, String type, double threshold) throws Exception;

    List<T> searchPaginated(String query, String type, int page, int size) throws Exception;

    List<T> search(String query, String searchType, 
                           int page, int pageSize, 
                           String sortField, String sortOrder);

    /**
     * Performs fuzzy search using Levenshtein distance
     * @param query The search query
     * @param maxDistance Maximum allowed edit distance
     * @return List of matching LibraryItems
     */
    List<T> fuzzySearch(String query, int maxDistance);

    /**
     * Performs partial matching search
     * @param query The search query
     * @return List of matching LibraryItems
     */
    List<T> partialMatchSearch(String query);
}
