package se.fulkopinglibraryweb.service;

import com.google.cloud.firestore.CollectionReference;
import se.fulkopinglibraryweb.model.Book;
import se.fulkopinglibraryweb.model.LibraryItem;
import se.fulkopinglibraryweb.model.Magazine;
import se.fulkopinglibraryweb.model.Media;
import se.fulkopinglibraryweb.model.ItemType;
import se.fulkopinglibraryweb.utils.SearchBook;
import se.fulkopinglibraryweb.utils.SearchMagazine;
import se.fulkopinglibraryweb.utils.SearchMedia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import se.fulkopinglibraryweb.service.search.SearchCriteria;

public class FirestoreSearchService<T extends LibraryItem> implements SearchService<T> {
    private static final Logger logger = LoggerFactory.getLogger(FirestoreSearchService.class);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;
    private final CollectionReference collection;
    private final Class<T> itemClass;
    private final SearchBook searchBook;
    private final SearchMagazine searchMagazine;
    private final SearchMedia searchMedia;

    public FirestoreSearchService(CollectionReference collection, Class<T> itemClass) {
        if (collection == null) {
            throw new IllegalArgumentException("Collection reference cannot be null");
        }
        if (itemClass == null) {
            throw new IllegalArgumentException("Item class cannot be null");
        }
        this.collection = collection;
        this.itemClass = itemClass;
        this.searchBook = new SearchBook();
        this.searchMagazine = new SearchMagazine();
        this.searchMedia = new SearchMedia();
    }

    @Override
    public List<T> search(String query, String type, SearchCriteria criteria) throws Exception {
        validateSearchParameters(query, type);
        List<T> allItems = collection.get().get().toObjects(itemClass);
        
        return allItems.stream()
            .filter(item -> {
                if (item == null) return false;
                
                switch (item.getType()) {
                    case BOOK:
                        if (item instanceof Book book) {
                            return searchBook.matches(book, query, type) && 
                                   searchBook.matchesCriteria(book, criteria);
                        }
                        break;
                    case MAGAZINE:
                        if (item instanceof Magazine magazine) {
                            return searchMagazine.matches(magazine, query, type) && 
                                   searchMagazine.matchesCriteria(magazine, criteria);
                        }
                        break;
                    case MEDIA:
                        if (item instanceof Media media) {
                            return searchMedia.matches(media, query, type) && 
                                   searchMedia.matchesCriteria(media, criteria);
                        }
                        break;
                    default:
                        return false;
                }
                return false;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<T> fuzzySearch(String query, String type, double threshold) throws Exception {
        validateSearchParameters(query, type);
        List<T> allItems = collection.get().get().toObjects(itemClass);
        return allItems.stream()
            .filter(item -> calculateSimilarity(item, query) >= threshold)
            .collect(Collectors.toList());
    }

    @Override
    public List<T> searchPaginated(String query, String type, int page, int size) throws Exception {
        validateSearchParameters(query, type);
        List<T> allItems = collection.get().get().toObjects(itemClass);
        return allItems.stream()
            .skip((page - 1) * size)
            .limit(size)
            .collect(Collectors.toList());
    }

    @Override
    public List<T> search(String query, String searchType, int page, int pageSize, String sortField, String sortOrder) {
        validateSearchParameters(query, searchType, page, pageSize);
        
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                List<T> allItems = collection.get().get().toObjects(itemClass);
                
                List<T> results = allItems.stream()
                    .filter(item -> {
                        if (item == null) return false;
                        
                        switch (item.getType()) {
                            case BOOK:
                                if (item instanceof Book book) {
                                    return searchBook.matches(book, query, searchType);
                                }
                                break;
                            case MAGAZINE:
                                if (item instanceof Magazine magazine) {
                                    return searchMagazine.matches(magazine, query, searchType);
                                }
                                break;
                            case MEDIA:
                                if (item instanceof Media media) {
                                    return searchMedia.matches(media, query, searchType);
                                }
                                break;
                            case MUSIC:
                            case MOVIE:
                            case PODCAST:
                                // Handle these types similarly to other types
                                return false;
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
                
                // Apply pagination
                int offset = (page - 1) * pageSize;
                results = results.stream()
                    .skip(offset)
                    .limit(pageSize)
                    .collect(Collectors.toList());
                
                logger.info("Successfully retrieved {} items for search query: {}", results.size(), query);
                return results;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Search operation interrupted", e);
                throw new RuntimeException("Search operation interrupted", e);
            } catch (Exception e) {
                if (attempt == MAX_RETRIES) {
                    logger.error("Final attempt failed for search operation. Query: {}, Error: {}", query, e.getMessage());
                    return new ArrayList<>();
                }
                logger.warn("Attempt {} failed, retrying after delay. Error: {}", attempt, e.getMessage());
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry delay interrupted", ie);
                }
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<T> fuzzySearch(String query, int maxDistance) {
        if (query == null || query.trim().isEmpty()) {
            logger.warn("Empty query provided for fuzzy search");
            return new ArrayList<>();
        }
        final int effectiveMaxDistance;
        if (maxDistance < 0) {
            logger.warn("Invalid maxDistance ({}), using default value of 2", maxDistance);
            effectiveMaxDistance = 2;
        } else {
            effectiveMaxDistance = maxDistance;
        }

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                List<T> allItems = collection.get().get().toObjects(itemClass);
                
                List<T> results = allItems.stream()
                    .filter(item -> {
                        if (item == null) return false;
                        
                        switch (item.getType()) {
                            case BOOK:
                                if (item instanceof Book book) {
                                    return searchBook.fuzzyMatches(book, query, effectiveMaxDistance);
                                }
                                break;
                            case MAGAZINE:
                                if (item instanceof Magazine magazine) {
                                    return searchMagazine.fuzzyMatches(magazine, query, effectiveMaxDistance);
                                }
                                break;
                            case MEDIA:
                                if (item instanceof Media media) {
                                    return searchMedia.fuzzyMatches(media, query, effectiveMaxDistance);
                                }
                                break;
                            case MUSIC:
                            case MOVIE:
                            case PODCAST:
                                // Handle these types similarly to other types
                                return false;
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
                
                logger.info("Fuzzy search completed. Query: {}, Results: {}", query, results.size());
                return results;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Fuzzy search operation interrupted", e);
                throw new RuntimeException("Fuzzy search operation interrupted", e);
            } catch (Exception e) {
                if (attempt == MAX_RETRIES) {
                    logger.error("Final attempt failed for fuzzy search. Query: {}, Error: {}", query, e.getMessage());
                    return new ArrayList<>();
                }
                logger.warn("Attempt {} failed, retrying after delay. Error: {}", attempt, e.getMessage());
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry delay interrupted", ie);
                }
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<T> partialMatchSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            logger.warn("Empty query provided for partial match search");
            return new ArrayList<>();
        }

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                List<T> allItems = collection.get().get().toObjects(itemClass);
                
                List<T> results = allItems.stream()
                    .filter(item -> {
                        if (item == null) return false;
                        
                        switch (item.getType()) {
                            case BOOK:
                                if (item instanceof Book book) {
                                    return searchBook.partialMatches(book, query);
                                }
                                break;
                            case MAGAZINE:
                                if (item instanceof Magazine magazine) {
                                    return searchMagazine.partialMatches(magazine, query);
                                }
                                break;
                            case MEDIA:
                                if (item instanceof Media media) {
                                    return searchMedia.partialMatches(media, query);
                                }
                                break;
                            case MUSIC:
                            case MOVIE:
                            case PODCAST:
                                // Handle these types similarly to other types
                                return false;
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
                
                logger.info("Partial match search completed. Query: {}, Results: {}", query, results.size());
                return results;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Partial match search operation interrupted", e);
                throw new RuntimeException("Partial match search operation interrupted", e);
            } catch (Exception e) {
                if (attempt == MAX_RETRIES) {
                    logger.error("Final attempt failed for partial match search. Query: {}, Error: {}", query, e.getMessage());
                    return new ArrayList<>();
                }
                logger.warn("Attempt {} failed, retrying after delay. Error: {}", attempt, e.getMessage());
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry delay interrupted", ie);
                }
            }
        }
        return new ArrayList<>();
    }

    private boolean matchesFilters(T item, Map<String, Object> filters) {
        if (filters == null || filters.isEmpty()) return true;
        return filters.entrySet().stream()
            .allMatch(entry -> matchesFilter(item, entry.getKey(), entry.getValue()));
    }

    private boolean matchesFilter(T item, String key, Object value) {
        try {
            Object itemValue = item.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1))
                .invoke(item);
            return value.equals(itemValue);
        } catch (Exception e) {
            logger.error("Error matching filter: {}", e.getMessage());
            return false;
        }
    }

    private double calculateSimilarity(T item, String query) {
        try {
            String itemStr = item.toString().toLowerCase();
            query = query.toLowerCase();
            return 1.0 - ((double) levenshteinDistance(itemStr, query) / Math.max(itemStr.length(), query.length()));
        } catch (Exception e) {
            logger.error("Error calculating similarity: {}", e.getMessage());
            return 0.0;
        }
    }

    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= s2.length(); j++) dp[0][j] = j;
        
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }
        return dp[s1.length()][s2.length()];
    }

    private void validateSearchParameters(String query, String type) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Search query cannot be null or empty");
        }
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Search type cannot be null or empty");
        }
    }

    private void validateSearchParameters(String query, String searchType, int page, int pageSize) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Search query cannot be null or empty");
        }
        if (page < 1) {
            throw new IllegalArgumentException("Page number must be greater than 0");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("Page size must be greater than 0");
        }
    }
}
