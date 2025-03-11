package se.fulkopinglibraryweb.utils;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteBatch;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for batch operations in Firestore.
 * This class provides methods for efficient batch operations on Firestore collections.
 */
public class BatchOperations {
    
    private static final Logger logger = Logger.getLogger(BatchOperations.class.getName());
    private static final ExecutorService batchExecutor = Executors.newFixedThreadPool(3);
    
    // Maximum number of operations in a single batch
    private static final int MAX_BATCH_SIZE = 500;
    
    /**
     * Execute a batch write operation.
     * This method executes a batch write operation with the specified documents and data.
     *
     * @param collectionName The name of the collection
     * @param documentIds The list of document IDs
     * @param dataList The list of data to write
     * @return A CompletableFuture containing the list of write results
     */
    public static CompletableFuture<List<WriteResult>> batchWrite(String collectionName, List<String> documentIds, List<Map<String, Object>> dataList) {
        if (documentIds.size() != dataList.size()) {
            throw new IllegalArgumentException("Document IDs and data lists must have the same size");
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                Firestore db = FirestoreClient.getFirestore();
                List<WriteResult> results = new ArrayList<>();
                
                // Split into smaller batches if necessary
                for (int i = 0; i < documentIds.size(); i += MAX_BATCH_SIZE) {
                    int endIndex = Math.min(i + MAX_BATCH_SIZE, documentIds.size());
                    List<String> batchDocumentIds = documentIds.subList(i, endIndex);
                    List<Map<String, Object>> batchDataList = dataList.subList(i, endIndex);
                    
                    WriteBatch batch = db.batch();
                    
                    for (int j = 0; j < batchDocumentIds.size(); j++) {
                        batch.set(db.collection(collectionName).document(batchDocumentIds.get(j)), batchDataList.get(j));
                    }
                    
                    // Commit the batch
                    ApiFuture<List<WriteResult>> future = batch.commit();
                    results.addAll(future.get());
                }
                
                return results;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error executing batch write", e);
                throw new RuntimeException("Error executing batch write", e);
            }
        }, batchExecutor);
    }
    
    /**
     * Execute a batch update operation.
     * This method executes a batch update operation with the specified documents and data.
     *
     * @param collectionName The name of the collection
     * @param documentIds The list of document IDs
     * @param dataList The list of data to update
     * @return A CompletableFuture containing the list of write results
     */
    public static CompletableFuture<List<WriteResult>> batchUpdate(String collectionName, List<String> documentIds, List<Map<String, Object>> dataList) {
        if (documentIds.size() != dataList.size()) {
            throw new IllegalArgumentException("Document IDs and data lists must have the same size");
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                Firestore db = FirestoreClient.getFirestore();
                List<WriteResult> results = new ArrayList<>();
                
                // Split into smaller batches if necessary
                for (int i = 0; i < documentIds.size(); i += MAX_BATCH_SIZE) {
                    int endIndex = Math.min(i + MAX_BATCH_SIZE, documentIds.size());
                    List<String> batchDocumentIds = documentIds.subList(i, endIndex);
                    List<Map<String, Object>> batchDataList = dataList.subList(i, endIndex);
                    
                    WriteBatch batch = db.batch();
                    
                    for (int j = 0; j < batchDocumentIds.size(); j++) {
                        batch.update(db.collection(collectionName).document(batchDocumentIds.get(j)), batchDataList.get(j));
                    }
                    
                    // Commit the batch
                    ApiFuture<List<WriteResult>> future = batch.commit();
                    results.addAll(future.get());
                }
                
                return results;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error executing batch update", e);
                throw new RuntimeException("Error executing batch update", e);
            }
        }, batchExecutor);
    }
    
    /**
     * Execute a batch delete operation.
     * This method executes a batch delete operation with the specified documents.
     *
     * @param collectionName The name of the collection
     * @param documentIds The list of document IDs to delete
     * @return A CompletableFuture containing the list of write results
     */
    public static CompletableFuture<List<WriteResult>> batchDelete(String collectionName, List<String> documentIds) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Firestore db = FirestoreClient.getFirestore();
                List<WriteResult> results = new ArrayList<>();
                
                // Split into smaller batches if necessary
                for (int i = 0; i < documentIds.size(); i += MAX_BATCH_SIZE) {
                    int endIndex = Math.min(i + MAX_BATCH_SIZE, documentIds.size());
                    List<String> batchDocumentIds = documentIds.subList(i, endIndex);
                    
                    WriteBatch batch = db.batch();
                    
                    for (String documentId : batchDocumentIds) {
                        batch.delete(db.collection(collectionName).document(documentId));
                    }
                    
                    // Commit the batch
                    ApiFuture<List<WriteResult>> future = batch.commit();
                    results.addAll(future.get());
                }
                
                return results;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error executing batch delete", e);
                throw new RuntimeException("Error executing batch delete", e);
            }
        }, batchExecutor);
    }
    
    /**
     * Execute a transaction that spans multiple collections.
     * This method executes a transaction that spans multiple collections with the specified operations.
     *
     * @param operations A list of batch operations to execute in a transaction
     * @return A CompletableFuture containing the list of write results
     */
    public static CompletableFuture<List<WriteResult>> executeTransaction(List<BatchOperation> operations) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Firestore db = FirestoreClient.getFirestore();
                WriteBatch batch = db.batch();
                
                for (BatchOperation operation : operations) {
                    switch (operation.getType()) {
                        case SET:
                            batch.set(db.collection(operation.getCollectionName()).document(operation.getDocumentId()), operation.getData());
                            break;
                        case UPDATE:
                            batch.update(db.collection(operation.getCollectionName()).document(operation.getDocumentId()), operation.getData());
                            break;
                        case DELETE:
                            batch.delete(db.collection(operation.getCollectionName()).document(operation.getDocumentId()));
                            break;
                    }
                }
                
                // Commit the batch
                ApiFuture<List<WriteResult>> future = batch.commit();
                return future.get();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error executing transaction", e);
                throw new RuntimeException("Error executing transaction", e);
            }
        }, batchExecutor);
    }
    
    /**
     * Shutdown the batch executor.
     * This method should be called when the application is shutting down.
     */
    public static void shutdown() {
        batchExecutor.shutdown();
    }
    
    /**
     * Enum for batch operation types.
     */
    public enum BatchOperationType {
        SET,
        UPDATE,
        DELETE
    }
    
    /**
     * Class representing a batch operation.
     */
    public static class BatchOperation {
        private final BatchOperationType type;
        private final String collectionName;
        private final String documentId;
        private final Map<String, Object> data;
        
        /**
         * Constructor for a batch operation.
         *
         * @param type The type of operation
         * @param collectionName The name of the collection
         * @param documentId The ID of the document
         * @param data The data for the operation (null for DELETE operations)
         */
        public BatchOperation(BatchOperationType type, String collectionName, String documentId, Map<String, Object> data) {
            this.type = type;
            this.collectionName = collectionName;
            this.documentId = documentId;
            this.data = data;
        }
        
        /**
         * Get the type of operation.
         *
         * @return The type of operation
         */
        public BatchOperationType getType() {
            return type;
        }
        
        /**
         * Get the name of the collection.
         *
         * @return The name of the collection
         */
        public String getCollectionName() {
            return collectionName;
        }
        
        /**
         * Get the ID of the document.
         *
         * @return The ID of the document
         */
        public String getDocumentId() {
            return documentId;
        }
        
        /**
         * Get the data for the operation.
         *
         * @return The data for the operation
         */
        public Map<String, Object> getData() {
            return data;
        }
    }
}
