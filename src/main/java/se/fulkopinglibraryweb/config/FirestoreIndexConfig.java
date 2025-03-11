package se.fulkopinglibraryweb.config;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Configuration class for Firestore indexes.
 * This class defines and creates the necessary indexes for optimizing queries in Firestore.
 * 
 * Note: In a production environment, indexes should be defined in the Firebase console or using
 * the Firebase CLI with a firestore.indexes.json file. This class is primarily for development
 * and documentation purposes to make the indexing strategy explicit in the codebase.
 */
@Configuration
public class FirestoreIndexConfig {
    
    private static final Logger logger = Logger.getLogger(FirestoreIndexConfig.class.getName());
    
    // Collection names
    private static final String BOOKS_COLLECTION = "books";
    private static final String MAGAZINES_COLLECTION = "magazines";
    private static final String MEDIA_COLLECTION = "media";
    private static final String USERS_COLLECTION = "users";
    private static final String LOANS_COLLECTION = "loans";
    
    /**
     * Initialize Firestore indexes when the application context is refreshed.
     * This method logs the indexing strategy for documentation purposes.
     * 
     * @param event The context refreshed event
     */
    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.info("Initializing Firestore indexing strategy");
        
        // Log the indexing strategy
        logIndexingStrategy();
    }
    
    /**
     * Log the indexing strategy for documentation purposes.
     * This method defines the recommended indexes for each collection.
     */
    private void logIndexingStrategy() {
        // Book collection indexes
        Map<String, String> bookIndexes = new HashMap<>();
        bookIndexes.put("author, title", "For searching books by author and sorting by title");
        bookIndexes.put("genre, publishYear", "For filtering books by genre and sorting by publication year");
        bookIndexes.put("isbn", "For exact ISBN lookups");
        bookIndexes.put("title", "For title search and sorting");
        bookIndexes.put("available, genre", "For finding available books in a specific genre");
        
        // Magazine collection indexes
        Map<String, String> magazineIndexes = new HashMap<>();
        magazineIndexes.put("publisher, title", "For searching magazines by publisher and sorting by title");
        magazineIndexes.put("category, publicationYear", "For filtering magazines by category and sorting by publication year");
        magazineIndexes.put("issn", "For exact ISSN lookups");
        magazineIndexes.put("frequency, publicationYear", "For finding magazines with specific frequency and sorting by year");
        
        // Media collection indexes
        Map<String, String> mediaIndexes = new HashMap<>();
        mediaIndexes.put("type, title", "For searching media by type and sorting by title");
        mediaIndexes.put("director, releaseYear", "For filtering media by director and sorting by release year");
        mediaIndexes.put("genre, format", "For finding media in a specific genre and format");
        mediaIndexes.put("creator, type", "For searching by creator and filtering by type");
        
        // User collection indexes
        Map<String, String> userIndexes = new HashMap<>();
        userIndexes.put("email", "For email lookups and authentication");
        userIndexes.put("role, active", "For finding users with specific roles and status");
        userIndexes.put("lastName, firstName", "For sorting users by name");
        
        // Loan collection indexes
        Map<String, String> loanIndexes = new HashMap<>();
        loanIndexes.put("userId, dueDate", "For finding a user's loans sorted by due date");
        loanIndexes.put("itemId, status", "For checking if an item is on loan");
        loanIndexes.put("status, dueDate", "For finding overdue loans");
        loanIndexes.put("dueDate", "For finding loans due before a specific date");
        
        // Log the indexes
        logger.info("Recommended indexes for " + BOOKS_COLLECTION + ": " + bookIndexes);
        logger.info("Recommended indexes for " + MAGAZINES_COLLECTION + ": " + magazineIndexes);
        logger.info("Recommended indexes for " + MEDIA_COLLECTION + ": " + mediaIndexes);
        logger.info("Recommended indexes for " + USERS_COLLECTION + ": " + userIndexes);
        logger.info("Recommended indexes for " + LOANS_COLLECTION + ": " + loanIndexes);
    }
    
    /**
     * Get the Firestore instance.
     * 
     * @return The Firestore instance
     */
    private Firestore getFirestore() {
        return FirestoreClient.getFirestore();
    }
}
