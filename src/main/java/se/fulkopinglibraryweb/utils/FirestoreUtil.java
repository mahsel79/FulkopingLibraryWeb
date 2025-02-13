package se.fulkopinglibraryweb.utils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class FirestoreUtil {
    private static final Logger logger = LoggerFactory.getLogger(FirestoreUtil.class);
    private static Firestore firestore;

    private FirestoreUtil() {
        // Private constructor to prevent instantiation
    }

    public static Firestore getFirestore() {
        if (firestore == null) {
            synchronized (FirestoreUtil.class) {
                if (firestore == null) {
                    try {
                        logger.info("Initializing Firestore connection...");
                        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
                        FirestoreOptions firestoreOptions = FirestoreOptions.newBuilder()
                                .setCredentials(credentials)
                                .build();
                        firestore = firestoreOptions.getService();
                        logger.info("Firestore connection established successfully.");
                    } catch (IOException e) {
                        logger.error("Failed to initialize Firestore", e);
                        throw new RuntimeException("Error initializing Firestore", e);
                    }
                }
            }
        }
        return firestore;
    }
}
