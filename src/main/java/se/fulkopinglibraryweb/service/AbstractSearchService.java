package se.fulkopinglibraryweb.service;

import se.fulkopinglibraryweb.service.interfaces.SearchService;
import se.fulkopinglibraryweb.service.search.SearchCriteria;
import se.fulkopinglibraryweb.utils.LoggingUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractSearchService<T> implements SearchService<T> {
    private final LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
    private final String serviceName;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected AbstractSearchService(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public List<T> search(String query, String type, Map<String, Object> filters) throws Exception {
        try {
            if (query == null || query.trim().isEmpty()) {
                return new ArrayList<>();
            }

            List<T> allItems = getAllItems();
            List<T> filteredByType = filterItemsByType(allItems, query.toLowerCase(), type);
            return applyFilters(filteredByType, filters);
        } catch (Exception e) {
            LoggingUtils.logError(logger, "Error performing " + serviceName + " search", e);
            throw new Exception("Error performing " + serviceName + " search", e);
        }
    }

    @Override
    public List<T> fuzzySearch(String query, String type, double threshold) throws Exception {
        try {
            if (query == null || query.trim().isEmpty()) {
                return new ArrayList<>();
            }

            List<T> allItems = getAllItems();
            return allItems.stream()
                    .filter(item -> isFuzzyMatch(item, query.toLowerCase(), type, threshold))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LoggingUtils.logError(logger, "Error performing fuzzy " + serviceName + " search", e);
            throw new Exception("Error performing fuzzy " + serviceName + " search", e);
        }
    }

    @Override
    public List<T> advancedSearch(SearchCriteria criteria, String itemType) throws Exception {
        try {
            if (criteria == null) {
                return new ArrayList<>();
            }

            List<T> allItems = getAllItems();
            List<T> filteredByType = filterItemsByType(allItems, criteria.getSearchTerm(), itemType);
            
            // Create filters map from SearchCriteria
            Map<String, Object> filters = new HashMap<>();
            if (criteria.getFilterField() != null && criteria.getFilterValue() != null) {
                filters.put(criteria.getFilterField(), criteria.getFilterValue());
            }
            
            return applyFilters(filteredByType, filters);
        } catch (Exception e) {
            LoggingUtils.logError(logger, "Error performing advanced " + serviceName + " search", e);
            throw new Exception("Error performing advanced " + serviceName + " search", e);
        }
    }

    protected List<T> filterItemsByType(List<T> items, String query, String type) {
        return items.stream()
                .filter(item -> matchesType(item, query, type))
                .collect(Collectors.toList());
    }

    protected List<T> applyFilters(List<T> items, Map<String, Object> filters) {
        if (filters == null || filters.isEmpty()) {
            return items;
        }
        return items.stream()
                .filter(item -> matchesFilters(item, filters))
                .collect(Collectors.toList());
    }

    protected abstract boolean matchesFilters(T item, Map<String, Object> filters);

    protected boolean isFuzzyMatch(T item, String query, String type, double threshold) {
        String itemValue = getValueByType(item, type);

        if (itemValue.isEmpty()) return false;

        int distance = levenshteinDistance.apply(itemValue, query);
        int maxLength = Math.max(itemValue.length(), query.length());
        double similarity = 1.0 - ((double) distance / maxLength);

        return similarity >= threshold;
    }

    protected abstract List<T> getAllItems();
    protected abstract boolean matchesType(T item, String query, String type);
    protected abstract String getValueByType(T item, String type);
}
