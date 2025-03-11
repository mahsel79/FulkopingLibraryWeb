package se.fulkopinglibraryweb.service.interfaces;

import se.fulkopinglibraryweb.model.LibraryItem;
import se.fulkopinglibraryweb.service.search.SearchCriteria;

import java.util.List;

public interface UnifiedSearchService {
    /**
     * Searches across all library items using the provided criteria
     * @param criteria Search criteria containing fields and values to search for
     * @return List of matching library items
     */
    List<LibraryItem> search(SearchCriteria criteria);

    /**
     * Searches across a specific type of library item
     * @param criteria Search criteria containing fields and values to search for
     * @param itemType The type of item to search for (Book, Magazine, Media)
     * @return List of matching items of the specified type
     */
    <T extends LibraryItem> List<T> search(SearchCriteria criteria, Class<T> itemType);

    /**
     * Gets all searchable fields for a given item type
     * @param itemType The type of item to get searchable fields for
     * @return List of searchable field names
     */
    List<String> getSearchableFields(Class<? extends LibraryItem> itemType);
}
