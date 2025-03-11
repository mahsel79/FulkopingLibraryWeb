package se.fulkopinglibraryweb.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import se.fulkopinglibraryweb.utils.FirestoreConnectionPool;

import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;

/**
 * Spring configuration for Firestore database.
 * Configures Firestore connection, connection pooling, and transaction management.
 */
@Configuration
public class FirestoreDatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(FirestoreDatabaseConfig.class);
    private static final String DEFAULT_CREDENTIALS_PATH = "WEB-INF/serviceAccountKey.json";
    
    @Value("${firestore.credentials.path:" + DEFAULT_CREDENTIALS_PATH + "}")
    private String credentialsPath;
    
    @Value("${firestore.pool.initial-size:5}")
    private int initialPoolSize;
    
    @Value("${firestore.pool.max-size:10}")
    private int maxPoolSize;
    
    @Value("${firestore.pool.connection-timeout:5000}")
    private long connectionTimeout;

    /**
     * Initializes Firebase with the provided credentials.
     * This bean must be created before the Firestore bean.
     *
     * @return The initialized FirebaseApp instance
     * @throws IOException If there is an error reading the credentials file
     */
    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            Resource resource = new ClassPathResource(credentialsPath);
            InputStream serviceAccount = resource.getInputStream();
            
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            
            logger.info("Initializing Firebase application");
            return FirebaseApp.initializeApp(options);
        } else {
            logger.info("Firebase application already initialized");
            return FirebaseApp.getInstance();
        }
    }

    /**
     * Creates a Firestore instance for direct use.
     * This bean depends on the firebaseApp bean to ensure Firebase is initialized first.
     *
     * @return A Firestore instance
     */
    @Bean
    @DependsOn("firebaseApp")
    public Firestore firestore() {
        logger.info("Creating Firestore instance");
        return FirestoreClient.getFirestore();
    }

    /**
     * Configures and initializes the Firestore connection pool.
     * This bean depends on the firestore bean to ensure Firestore is initialized first.
     *
     * @return The configured FirestoreConnectionPool instance
     */
    @Bean
    @Primary
    @DependsOn("firestore")
    public FirestoreConnectionPool firestoreConnectionPool() {
        logger.info("Initializing Firestore connection pool with initial size: {}, max size: {}", 
                initialPoolSize, maxPoolSize);
        
        // Configure the connection pool with the specified parameters
        FirestoreConnectionPool.configure(initialPoolSize, maxPoolSize, connectionTimeout);
        
        return FirestoreConnectionPool.getInstance();
    }

    /**
     * Cleanup method to shut down the connection pool when the application is stopped.
     */
    @PreDestroy
    public void cleanup() {
        logger.info("Shutting down Firestore connection pool");
        FirestoreConnectionPool.getInstance().shutdown();
    }
}
