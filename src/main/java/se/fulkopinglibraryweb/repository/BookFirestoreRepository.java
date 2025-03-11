package se.fulkopinglibraryweb.repository;

import se.fulkopinglibraryweb.service.search.SearchCriteria;
import java.util.List;

public interface BookFirestoreRepository<T, ID> extends FirestoreRepository<T, ID> {
    List<T> searchBooks(SearchCriteria criteria);
}
