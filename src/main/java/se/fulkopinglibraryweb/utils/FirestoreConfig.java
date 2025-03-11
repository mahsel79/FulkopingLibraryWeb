package se.fulkopinglibraryweb.utils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FirestoreConfig {
    private static Firestore db;
    private static final String CREDENTIALS_FILE_PATH = "WEB-INF/serviceAccountKey.json";

    public static Firestore getInstance() {
        if (db == null) {
            initializeFirestore();
        }
        return db;
    }

    private static void initializeFirestore() {
        try {
            // Load the service account key JSON file
            InputStream serviceAccount = FirestoreConfig.class.getClassLoader()
                    .getResourceAsStream(CREDENTIALS_FILE_PATH);

            if (serviceAccount == null) {
                throw new IllegalStateException("Service account key file not found: " + CREDENTIALS_FILE_PATH);
            }

            // Initialize Firebase with credentials
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            // Initialize Firebase if not already initialized
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            // Get Firestore instance
            db = FirestoreClient.getFirestore();
        } catch (IOException e) {
            throw new RuntimeException("Error initializing Firestore", e);
        }
    }
}