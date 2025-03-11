package se.fulkopinglibraryweb.service.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enhanced version of SearchCriteria that supports advanced filtering operations.
 * This class extends the basic SearchCriteria with additional methods for range filtering
 * and keyword searching across multiple fields.
 */
public class EnhancedSearchCriteria extends SearchCriteria {
    
    private Map<String, RangeFilter> rangeFilters = new HashMap<>();
    private List<KeywordFilter> keywordFilters = new ArrayList<>();
    
    /**
     * Adds a range filter for a specific field.
     * 
     * @param field The field to filter on
     * @param min The minimum value (inclusive)
     * @param max The maximum value (inclusive)
     * @return This SearchCriteria instance for method chaining
     */
    public EnhancedSearchCriteria addRangeFilter(String field, Object min, Object max) {
        rangeFilters.put(field, new RangeFilter(min, max));
        return this;
    }
    
    /**
     * Adds a keyword filter that searches for a keyword across multiple fields.
     * 
     * @param keyword The keyword to search for
     * @param fields The fields to search in
     * @return This SearchCriteria instance for method chaining
     */
    public EnhancedSearchCriteria addKeywordFilter(String keyword, String... fields) {
        keywordFilters.add(new KeywordFilter(keyword, Arrays.asList(fields)));
        return this;
    }
    
    /**
     * Gets all range filters.
     * 
     * @return Map of field names to RangeFilter objects
     */
    public Map<String, RangeFilter> getRangeFilters() {
        return rangeFilters;
    }
    
    /**
     * Gets all keyword filters.
     * 
     * @return List of KeywordFilter objects
     */
    public List<KeywordFilter> getKeywordFilters() {
        return keywordFilters;
    }
    
    /**
     * Represents a range filter with minimum and maximum values.
     */
    public static class RangeFilter {
        private Object min;
        private Object max;
        
        public RangeFilter(Object min, Object max) {
            this.min = min;
            this.max = max;
        }
        
        public Object getMin() {
            return min;
        }
        
        public Object getMax() {
            return max;
        }
    }
    
    /**
     * Represents a keyword filter that searches for a keyword across multiple fields.
     */
    public static class KeywordFilter {
        private String keyword;
        private List<String> fields;
        
        public KeywordFilter(String keyword, List<String> fields) {
            this.keyword = keyword;
            this.fields = fields;
        }
        
        public String getKeyword() {
            return keyword;
        }
        
        public List<String> getFields() {
            return fields;
        }
    }
}
