package se.fulkopinglibraryweb.utils;

import com.google.cloud.secretmanager.v1.*;
import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoogleSecretsUtil {
    private static final Logger logger = LoggerUtil.getLogger(GoogleSecretsUtil.class);
    private static final String PROJECT_ID = "your-gcp-project-id"; // Replace with actual GCP project ID
    private static final String SECRET_NAME = "firestore-credentials"; // Replace with actual secret name

    public static String getSecretValue() {
        try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
            String secretPath = SecretVersionName.of(PROJECT_ID, SECRET_NAME, "latest").toString();
            AccessSecretVersionResponse response = client.accessSecretVersion(secretPath);
            ByteString payload = response.getPayload().getData();
            logger.info("Successfully retrieved secret value for Firestore credentials.");
            return payload.toStringUtf8();
        } catch (Exception e) {
            logger.error("Failed to retrieve secret value: {}", e.getMessage());
            throw new RuntimeException("Could not fetch secret from Google Secrets Manager", e);
        }
    }
}