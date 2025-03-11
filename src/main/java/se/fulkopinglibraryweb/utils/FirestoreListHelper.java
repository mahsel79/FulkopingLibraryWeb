package se.fulkopinglibraryweb.utils;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import se.fulkopinglibraryweb.model.Page;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class FirestoreListHelper<T> {

    public List<T> getAllResults(CollectionReference collectionRef) 
            throws ExecutionException, InterruptedException {
        
        // Get all documents
        List<QueryDocumentSnapshot> documents = collectionRef
            .get()
            .get()
            .getDocuments();
            
        // Convert to entities
        return documents.stream()
            .map(doc -> (T) doc.toObject(Object.class))
            .collect(Collectors.toList());
    }
}
