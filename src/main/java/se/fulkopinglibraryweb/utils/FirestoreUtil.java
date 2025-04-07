package se.fulkopinglibraryweb.utils;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.*;
import se.fulkopinglibraryweb.utils.LoggerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Utility class for managing Firestore database connections and operations.
 * Provides centralized connection management, caching, and error handling for Firestore operations.
 * Implements connection pooling to optimize resource usage and performance.
 */
public class FirestoreUtil {
    private static final Map<String, String> roleCache = new ConcurrentHashMap<>();
    private static final int CACHE_SIZE = 1000;

    /**
     * Private constructor to prevent instantiation of utility class.
     * This class should only be used through its static methods.
     */
    private FirestoreUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Retrieves a Firestore connection from the connection pool.
     * Implements connection pooling to optimize resource usage and performance.
     *
     * @return A Firestore instance from the connection pool
     * @throws RuntimeException If unable to obtain a Firestore connection
     */
    public static Firestore getFirestore() {
        return FirestoreConnectionPool.getInstance().getConnection();
    }

    /**
     * Releases a Firestore connection back to the connection pool.
     * Should be called when the Firestore instance is no longer needed.
     *
     * @param firestore The Firestore instance to be released
     */
    public static void releaseFirestore(Firestore firestore) {
        FirestoreConnectionPool.getInstance().releaseConnection(firestore);
    }

    /**
     * Fetches the role of a user based on their username.
     * Implements caching to improve performance and reduce database load.
     * Cache entries are limited to CACHE_SIZE to prevent memory issues.
     *
     * @param username The username of the user
     * @return The role name (e.g., "USER", "ADMIN") or null if not found
     * @throws RuntimeException If there is an error accessing the database
     */
    public static String getUserRole(String username) {
        // Check cache first
        String cachedRole = roleCache.get(username);
        if (cachedRole != null) {
            LoggerUtil.logDebug("Cache hit for user role: {}", username);
            return cachedRole;
        }

        Firestore db = null;
        try {
            db = getFirestore();
            LoggerUtil.logInfo("Fetching user roles in batch for username: {}", username);

            // Batch query to get all necessary data
            List<ApiFuture<QuerySnapshot>> futures = new ArrayList<>();

            // Step 1: Get user document by username
            CollectionReference usersRef = db.collection("users");
            futures.add(usersRef.whereEqualTo("username", username).get());

            // Execute all queries in parallel
            List<QuerySnapshot> snapshots = ApiFutures.allAsList(futures).get();

            if (snapshots.get(0).isEmpty()) {
                LoggerUtil.logWarn("User '{}' not found in Firestore.", username);
                return null;
            }

            DocumentSnapshot userDoc = snapshots.get(0).getDocuments().get(0);
            String userId = userDoc.getId();

            // Step 2: Get user role in a single query
            DocumentSnapshot roleDoc = db.collection("user_roles")
                    .document(userId)
                    .get()
                    .get();

            if (roleDoc.exists()) {
                String roleName = roleDoc.getString("role_name");
                // Cache the result
                if (roleCache.size() < CACHE_SIZE) {
                    roleCache.put(username, roleName);
                }
                LoggerUtil.logInfo("User '{}' has role '{}'", username, roleName);
                return roleName;
            } else {
                LoggerUtil.logWarn("No role found for user '{}'", username);
                return null;
            }

        } catch (InterruptedException | ExecutionException e) {
            LoggerUtil.logError("Error fetching role for user '{}': {}", username, e.getMessage());
            return null;
        } finally {
            if (db != null) {
                releaseFirestore(db);
            }
        }
    }
}
