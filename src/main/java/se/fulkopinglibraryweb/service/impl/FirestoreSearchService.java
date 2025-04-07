package se.fulkopinglibraryweb.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import se.fulkopinglibraryweb.service.SearchService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import se.fulkopinglibraryweb.service.search.SearchCriteria;

public class FirestoreSearchService<T> implements SearchService<T> {
    private static final Logger logger = LoggerFactory.getLogger(FirestoreSearchService.class);
    
    private final CollectionReference collection;
    private final Class<T> typeParameterClass;

    public FirestoreSearchService(CollectionReference collection, Class<T> typeParameterClass) {
        logger.debug("Initializing FirestoreSearchService for collection: {}", collection.getPath());
        this.collection = collection;
        this.typeParameterClass = typeParameterClass;
    }

    @Override
    public List<T> search(String query, String type, SearchCriteria criteria) throws Exception {
        logger.debug("Searching with query: {}, type: {}, criteria: {}", query, type, criteria);
        QuerySnapshot querySnapshot = collection.get().get();
        if (criteria != null && criteria.getFilterField() != null && criteria.getFilterValue() != null) {
            querySnapshot = collection.whereEqualTo(criteria.getFilterField(), criteria.getFilterValue()).get().get();
        }
        List<T> results = new ArrayList<>();
        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            results.add(document.toObject(typeParameterClass));
        }
        return results;
    }

    @Override
    public List<T> fuzzySearch(String query, String type, double threshold) throws Exception {
        logger.debug("Fuzzy searching with query: {}, type: {}, threshold: {}", query, type, threshold);
        List<T> results = new ArrayList<>();
        QuerySnapshot querySnapshot = collection.get().get();
        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            String fieldValue = document.getString(type);
            if (fieldValue != null && calculateLevenshteinDistance(query, fieldValue) <= threshold) {
                results.add(document.toObject(typeParameterClass));
            }
        }
        return results;
    }

    @Override
    public List<T> searchPaginated(String query, String type, int page, int size) throws Exception {
        logger.debug("Paginated search with query: {}, type: {}, page: {}, size: {}", query, type, page, size);
        QuerySnapshot querySnapshot = collection
            .whereGreaterThanOrEqualTo(type, query)
            .whereLessThanOrEqualTo(type, query + "\uf8ff")
            .offset((page - 1) * size)
            .limit(size)
            .get()
            .get();
        List<T> results = new ArrayList<>();
        for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
            results.add(document.toObject(typeParameterClass));
        }
        return results;
    }

    @Override
    public List<T> search(String query, String searchType, int page, int pageSize, String sortField, String sortOrder) {
        logger.debug("Searching with query: {}, searchType: {}, page: {}, pageSize: {}, sortField: {}, sortOrder: {}", 
            query, searchType, page, pageSize, sortField, sortOrder);
        try {
            Query baseQuery = collection
                .whereGreaterThanOrEqualTo(searchType, query)
                .whereLessThanOrEqualTo(searchType, query + "\uf8ff");

            if (sortField != null && !sortField.isEmpty()) {
                baseQuery = baseQuery.orderBy(sortField, sortOrder.equalsIgnoreCase("DESC") ? 
                    Query.Direction.DESCENDING : Query.Direction.ASCENDING);
            }

            QuerySnapshot querySnapshot = baseQuery
                .offset((page - 1) * pageSize)
                .limit(pageSize)
                .get()
                .get();

            List<T> results = new ArrayList<>();
            for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
                results.add(document.toObject(typeParameterClass));
            }
            return results;
        } catch (Exception e) {
            logger.error("Error performing search with query: {}", query, e);
            throw new RuntimeException("Error performing search", e);
        }
    }

    @Override
    public List<T> fuzzySearch(String query, int maxDistance) {
        logger.debug("Fuzzy searching with query: {}, maxDistance: {}", query, maxDistance);
        try {
            List<T> results = new ArrayList<>();
            QuerySnapshot querySnapshot = collection.get().get();
            
            for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
                String title = document.getString("title");
                if (title != null && calculateLevenshteinDistance(query.toLowerCase(), title.toLowerCase()) <= maxDistance) {
                    results.add(document.toObject(typeParameterClass));
                }
            }
            
            return results;
        } catch (Exception e) {
            logger.error("Error performing fuzzy search with query: {}", query, e);
            throw new RuntimeException("Error performing fuzzy search", e);
        }
    }

    @Override
    public List<T> partialMatchSearch(String query) {
        logger.debug("Partial match search with query: {}", query);
        try {
            QuerySnapshot querySnapshot = collection
                .whereGreaterThanOrEqualTo("title", query)
                .whereLessThanOrEqualTo("title", query + "\uf8ff")
                .get()
                .get();
            
            List<T> results = new ArrayList<>();
            for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
                results.add(document.toObject(typeParameterClass));
            }
            return results;
        } catch (Exception e) {
            logger.error("Error performing partial match search with query: {}", query, e);
            throw new RuntimeException("Error performing partial match search", e);
        }
    }

    private int calculateLevenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(
                        dp[i - 1][j - 1] + (s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1),
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1)
                    );
                }
            }
        }

        return dp[s1.length()][s2.length()];
    }
}
