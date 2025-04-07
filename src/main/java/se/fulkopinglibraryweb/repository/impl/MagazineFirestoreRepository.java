package se.fulkopinglibraryweb.repository.impl;

import com.google.cloud.firestore.DocumentSnapshot;
import org.springframework.stereotype.Repository;
import se.fulkopinglibraryweb.model.Frequency;
import se.fulkopinglibraryweb.model.ItemType;
import se.fulkopinglibraryweb.model.Magazine;
import se.fulkopinglibraryweb.repository.AbstractFirestoreRepository;
import se.fulkopinglibraryweb.repository.MagazineRepository;
import se.fulkopinglibraryweb.service.search.SearchCriteria;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Repository
public class MagazineFirestoreRepository extends AbstractFirestoreRepository<Magazine, String> implements MagazineRepository {
    private static final String PUBLICATION_DATE_FIELD = "publicationDate";
    private static final String COLLECTION_NAME = "magazines";
    
    public MagazineFirestoreRepository() {
        super(COLLECTION_NAME);
    }

    @Override
    public Magazine convertToEntity(DocumentSnapshot document) {
        if (document == null || !document.exists()) {
            return null;
        }
        
        Magazine magazine = new Magazine();
        magazine.setId(document.getId());
        magazine.setTitle(document.getString("title"));
        magazine.setPublisher(document.getString("publisher"));
        magazine.setIssn(document.getString("issn"));
        magazine.setCategory(document.getString("category"));
        magazine.setIssue(document.getString("issue"));
        magazine.setFrequency(Frequency.valueOf(document.getString("frequency")));
        
        Long issueNumber = document.getLong("issueNumber");
        if (issueNumber != null) {
            magazine.setIssueNumber(issueNumber.intValue());
        }
        
        Long year = document.getLong("publicationYear");
        if (year != null) {
            magazine.setPublicationYear(year.intValue());
        }
        
        Boolean available = document.getBoolean("available");
        magazine.setAvailable(available != null ? available : true);
        
        return magazine;
    }

    @Override
    public Map<String, Object> convertToMap(Magazine magazine) {
        Map<String, Object> data = new HashMap<>();
        data.put("title", magazine.getTitle());
        data.put("publisher", magazine.getPublisher());
        data.put("issn", magazine.getIssn());
        data.put("category", magazine.getCategory());
        data.put("issue", magazine.getIssue());
        data.put("issueNumber", magazine.getIssueNumber());
        data.put("frequency", magazine.getFrequency());
        data.put("publicationYear", magazine.getPublicationYear());
        data.put("available", magazine.isAvailable());
        data.put("type", ItemType.MAGAZINE.name());
        return data;
    }

    @Override
    public List<Magazine> search(SearchCriteria criteria) {
        return super.search(criteria);
    }

    @Override
    public List<String> getSearchableFields() {
        return Arrays.asList("title", "publisher", "issn");
    }

    @Override
    public List<Magazine> findByPublisher(String publisher) {
        try {
            return firestore.collection(COLLECTION_NAME)
                .whereEqualTo("publisher", publisher)
                .get()
                .get()
                .getDocuments()
                .stream()
                .map(this::convertToEntity)
                .toList();
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to find magazines by publisher", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while finding magazines by publisher", e);
        }
    }

    @Override
    public List<Magazine> findByIssn(String issn) {
        try {
            return firestore.collection(COLLECTION_NAME)
                .whereEqualTo("issn", issn)
                .get()
                .get()
                .getDocuments()
                .stream()
                .map(this::convertToEntity)
                .toList();
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to find magazines by ISSN", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while finding magazines by ISSN", e);
        }
    }

    @Override
    public List<Magazine> findByCategory(String category) {
        try {
            return firestore.collection(COLLECTION_NAME)
                .whereEqualTo("category", category)
                .get()
                .get()
                .getDocuments()
                .stream()
                .map(this::convertToEntity)
                .toList();
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to find magazines by category", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while finding magazines by category", e);
        }
    }

    @Override
    public List<Magazine> findByPublicationYear(int year) {
        try {
            return firestore.collection(COLLECTION_NAME)
                .whereEqualTo("publicationYear", year)
                .get()
                .get()
                .getDocuments()
                .stream()
                .map(this::convertToEntity)
                .toList();
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to find magazines by publication year", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while finding magazines by publication year", e);
        }
    }

    @Override
    public List<Magazine> findByFrequency(String frequency) {
        try {
            return firestore.collection(COLLECTION_NAME)
                .whereEqualTo("frequency", frequency)
                .get()
                .get()
                .getDocuments()
                .stream()
                .map(this::convertToEntity)
                .toList();
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to find magazines by frequency", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while finding magazines by frequency", e);
        }
    }

    @Override
    public List<Magazine> findLatest(int limit) {
        try {
            return firestore.collection(COLLECTION_NAME)
                .orderBy(PUBLICATION_DATE_FIELD, com.google.cloud.firestore.Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .get()
                .getDocuments()
                .stream()
                .map(this::convertToEntity)
                .toList();
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to find latest magazines", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while finding latest magazines", e);
        }
    }

}
