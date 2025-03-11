package se.fulkopinglibraryweb.utils;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for optimizing Firestore queries.
 * This class provides methods for efficient querying of Firestore collections.
 */
public class QueryOptimizer {
    
    private static final Logger logger = Logger.getLogger(QueryOptimizer.class.getName());
    private static final ExecutorService queryExecutor = Executors.newFixedThreadPool(5);
    
    // Default page size for paginated queries
    private static final int DEFAULT_PAGE_SIZE = 20;
    
    /**
     * Execute a query with pagination.
     * This method executes a query with pagination to avoid loading large result sets at once.
     *
     * @param <T> The type of the result
     * @param query The query to execute
     * @param pageSize The number of results per page
     * @param mapper The function to map the query snapshot to the result type
     * @return A CompletableFuture containing the result
     */
    public static <T> CompletableFuture<T> executePagedQuery(Query query, int pageSize, Function<QuerySnapshot, T> mapper) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                QuerySnapshot snapshot = query.limit(pageSize).get().get();
                return mapper.apply(snapshot);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error executing paged query", e);
                throw new RuntimeException("Error executing paged query", e);
            }
        }, queryExecutor);
    }
    
    /**
     * Execute a query with pagination using the default page size.
     *
     * @param <T> The type of the result
     * @param query The query to execute
     * @param mapper The function to map the query snapshot to the result type
     * @return A CompletableFuture containing the result
     */
    public static <T> CompletableFuture<T> executePagedQuery(Query query, Function<QuerySnapshot, T> mapper) {
        return executePagedQuery(query, DEFAULT_PAGE_SIZE, mapper);
    }
    
    /**
     * Execute a batch query.
     * This method executes multiple queries in parallel and combines the results.
     *
     * @param <T> The type of the result
     * @param queries The list of queries to execute
     * @param combiner The function to combine the query snapshots into a single result
     * @return A CompletableFuture containing the combined result
     */
    public static <T> CompletableFuture<T> executeBatchQuery(List<Query> queries, Function<List<QuerySnapshot>, T> combiner) {
        List<CompletableFuture<QuerySnapshot>> futures = queries.stream()
                .map(query -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return query.get().get();
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Error executing batch query", e);
                        throw new RuntimeException("Error executing batch query", e);
                    }
                }, queryExecutor))
                .toList();
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> combiner.apply(futures.stream().map(CompletableFuture::join).toList()));
    }
    
    /**
     * Create an optimized query for a collection.
     * This method creates a query with the specified field and value, and orders the results by the specified field.
     *
     * @param collectionName The name of the collection
     * @param field The field to filter on
     * @param value The value to filter for
     * @param orderBy The field to order by
     * @return The optimized query
     */
    public static Query createOptimizedQuery(String collectionName, String field, Object value, String orderBy) {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference collection = db.collection(collectionName);
        
        Query query = collection.whereEqualTo(field, value);
        
        if (orderBy != null && !orderBy.isEmpty()) {
            query = query.orderBy(orderBy);
        }
        
        return query;
    }
    
    /**
     * Create an optimized query for a collection with a range filter.
     * This method creates a query with the specified field and range, and orders the results by the specified field.
     *
     * @param collectionName The name of the collection
     * @param field The field to filter on
     * @param start The start value of the range
     * @param end The end value of the range
     * @param orderBy The field to order by
     * @return The optimized query
     */
    public static Query createOptimizedRangeQuery(String collectionName, String field, Object start, Object end, String orderBy) {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference collection = db.collection(collectionName);
        
        Query query = collection.whereGreaterThanOrEqualTo(field, start).whereLessThanOrEqualTo(field, end);
        
        if (orderBy != null && !orderBy.isEmpty()) {
            query = query.orderBy(orderBy);
        }
        
        return query;
    }
    
    /**
     * Create an optimized query for a collection with multiple conditions.
     * This method creates a query with the specified fields and values, and orders the results by the specified field.
     *
     * @param collectionName The name of the collection
     * @param fields The fields to filter on
     * @param values The values to filter for
     * @param orderBy The field to order by
     * @return The optimized query
     */
    public static Query createOptimizedMultiConditionQuery(String collectionName, List<String> fields, List<Object> values, String orderBy) {
        if (fields.size() != values.size()) {
            throw new IllegalArgumentException("Fields and values must have the same size");
        }
        
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference collection = db.collection(collectionName);
        
        Query query = collection;
        
        for (int i = 0; i < fields.size(); i++) {
            query = query.whereEqualTo(fields.get(i), values.get(i));
        }
        
        if (orderBy != null && !orderBy.isEmpty()) {
            query = query.orderBy(orderBy);
        }
        
        return query;
    }
    
    /**
     * Shutdown the query executor.
     * This method should be called when the application is shutting down.
     */
    public static void shutdown() {
        queryExecutor.shutdown();
    }
}
