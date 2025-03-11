package se.fulkopinglibraryweb.utils;

import se.fulkopinglibraryweb.service.search.SearchCriteria;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class GenericSearch<T> {
    
    public boolean matchesCriteria(T item, SearchCriteria criteria) {
        if (criteria == null) {
            return true;
        }
        
        // Default implementation that uses existing matches() method
        return matches(item, criteria.getSearchTerm(), "default");
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface SearchableField {
        String name() default "";
        boolean fullText() default false;
    }

    public List<T> search(List<T> items, String query) {
        List<T> results = new ArrayList<>();
        
        for (T item : items) {
            if (matches(item, query, "default")) {
                results.add(item);
            }
        }
        
        return results;
    }

    public boolean matches(T item, String query, String searchType) {
        if (query == null || query.isEmpty()) {
            return true;
        }

        Class<?> clazz = item.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(SearchableField.class)) {
                SearchableField annotation = field.getAnnotation(SearchableField.class);
                if (searchType.equals("default") || searchType.equals(annotation.name())) {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(item);
                        if (value != null && value.toString().toLowerCase().contains(query.toLowerCase())) {
                            return true;
                        }
                    } catch (IllegalAccessException e) {
                        // Log error and continue
                    }
                }
            }
        }
        return false;
    }

    public boolean fuzzyMatches(T item, String query, int maxDistance) {
        // Basic implementation - can be overridden by subclasses
        if (query == null || query.isEmpty()) {
            return true;
        }

        String searchString = query.toLowerCase();
        Class<?> clazz = item.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(SearchableField.class)) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(item);
                    if (value != null) {
                        String fieldValue = value.toString().toLowerCase();
                        if (levenshteinDistance(fieldValue, searchString) <= maxDistance) {
                            return true;
                        }
                    }
                } catch (IllegalAccessException e) {
                    // Log error and continue
                }
            }
        }
        return false;
    }

    public boolean partialMatches(T item, String query) {
        // Basic implementation - can be overridden by subclasses
        if (query == null || query.isEmpty()) {
            return true;
        }

        String searchString = query.toLowerCase();
        Class<?> clazz = item.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(SearchableField.class)) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(item);
                    if (value != null) {
                        String fieldValue = value.toString().toLowerCase();
                        if (fieldValue.contains(searchString)) {
                            return true;
                        }
                    }
                } catch (IllegalAccessException e) {
                    // Log error and continue
                }
            }
        }
        return false;
    }

    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = min(
                        dp[i - 1][j - 1] + costOfSubstitution(s1.charAt(i - 1), s2.charAt(j - 1)),
                        dp[i - 1][j] + 1,
                        dp[i][j - 1] + 1
                    );
                }
            }
        }

        return dp[s1.length()][s2.length()];
    }

    private int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    private int min(int... numbers) {
        return java.util.Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
    }
}
