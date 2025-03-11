package se.fulkopinglibraryweb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.fulkopinglibraryweb.model.LibraryItem;
import se.fulkopinglibraryweb.repository.BookRepository;
import se.fulkopinglibraryweb.repository.FirestoreRepository;
import se.fulkopinglibraryweb.repository.MagazineRepository;
import se.fulkopinglibraryweb.repository.MediaRepository;
import se.fulkopinglibraryweb.model.Book;
import se.fulkopinglibraryweb.model.Magazine;
import se.fulkopinglibraryweb.model.Media;
import se.fulkopinglibraryweb.service.interfaces.UnifiedSearchService;
import se.fulkopinglibraryweb.service.search.SearchCriteria;
import se.fulkopinglibraryweb.utils.LoggingUtils;

import java.util.ArrayList;
import java.util.List;
@Service
public class UnifiedSearchServiceImpl implements UnifiedSearchService {

    private final BookRepository bookRepository;
    private final MagazineRepository magazineRepository;
    private final MediaRepository mediaRepository;
    private final FirestoreRepository<LibraryItem, String> firestoreRepository;
    @Autowired
    public UnifiedSearchServiceImpl(
            BookRepository bookRepository,
            MagazineRepository magazineRepository,
            MediaRepository mediaRepository,
            FirestoreRepository<LibraryItem, String> firestoreRepository) {
        this.bookRepository = bookRepository;
        this.magazineRepository = magazineRepository;
        this.mediaRepository = mediaRepository;
        this.firestoreRepository = firestoreRepository;
    }

    @Override
    public List<LibraryItem> search(SearchCriteria criteria) {
        List<LibraryItem> results = new ArrayList<>();
        
        try {
            // Search local repositories
            results.addAll(bookRepository.searchBooks(criteria));
            results.addAll(magazineRepository.search(criteria).join().getItems());
            results.addAll(mediaRepository.search(criteria).join().getItems());
            
            // Search Firestore
            results.addAll(firestoreRepository.search(criteria).join().getItems());
        } catch (Exception e) {
            LoggingUtils.error("Error during unified search", e);
            throw new RuntimeException("Search operation failed", e);
        }
        
        return results;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends LibraryItem> List<T> search(SearchCriteria criteria, Class<T> itemType) {
        try {
            if (itemType.equals(Book.class)) {
                return (List<T>) bookRepository.searchBooks(criteria);
            } else if (itemType.equals(Magazine.class)) {
                return (List<T>) magazineRepository.search(criteria).join().getItems();
            } else if (itemType.equals(Media.class)) {
                return (List<T>) mediaRepository.search(criteria).join().getItems();
            } else {
                throw new IllegalArgumentException("Unsupported item type: " + itemType.getSimpleName());
            }
        } catch (Exception e) {
            LoggingUtils.error("Error during type-specific search", e);
            throw new RuntimeException("Search operation failed", e);
        }
    }

    @Override
    public List<String> getSearchableFields(Class<? extends LibraryItem> itemType) {
        try {
            if (itemType.equals(Book.class)) {
                return bookRepository.getSearchableFields();
            } else if (itemType.equals(Magazine.class)) {
                return magazineRepository.getSearchableFields();
            } else if (itemType.equals(Media.class)) {
                return mediaRepository.getSearchableFields();
            } else {
                throw new IllegalArgumentException("Unsupported item type: " + itemType.getSimpleName());
            }
        } catch (Exception e) {
            LoggingUtils.error("Error getting searchable fields", e);
            throw new RuntimeException("Failed to get searchable fields", e);
        }
    }
}
