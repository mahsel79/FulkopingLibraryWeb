package se.fulkopinglibraryweb.service;

import se.fulkopinglibraryweb.service.interfaces.SearchService;

/**
 * Factory class for creating appropriate search service instances based on item type
 */
public class SearchServiceFactory {
    private final SearchService searchService;

    public SearchServiceFactory(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Returns the search service
     * @param itemType The type of item to search for (book, magazine, media)
     * @return SearchService implementation
     * @throws IllegalArgumentException if itemType is null
     */
    public SearchService getSearchService(String itemType) {
        if (itemType == null) {
            throw new IllegalArgumentException("Item type cannot be null");
        }
        return searchService;
    }
}
