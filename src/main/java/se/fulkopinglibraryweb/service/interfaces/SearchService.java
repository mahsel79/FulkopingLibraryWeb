package se.fulkopinglibraryweb.service.interfaces;

import java.util.List;
import java.util.Map;
import se.fulkopinglibraryweb.service.search.SearchCriteria;

public interface SearchService<T> {
    List<T> search(String query, String type, Map<String, Object> filters) throws Exception;
    List<T> fuzzySearch(String query, String type, double threshold) throws Exception;
    List<T> advancedSearch(SearchCriteria criteria, String itemType) throws Exception;
}
