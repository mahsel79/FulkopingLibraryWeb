package se.fulkopinglibraryweb.service;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Query;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface BaseLibraryService<T, ID> {
    Optional<T> getById(ID id) throws ExecutionException, InterruptedException;
    List<T> getAll() throws ExecutionException, InterruptedException;
    List<T> search(String searchType, String searchQuery) throws ExecutionException, InterruptedException;
    T create(T item) throws ExecutionException, InterruptedException;
    T update(T item) throws ExecutionException, InterruptedException;
    boolean delete(ID id) throws ExecutionException, InterruptedException;
    boolean borrow(ID id, String userId) throws ExecutionException, InterruptedException;
    boolean returnItem(ID id, String userId) throws ExecutionException, InterruptedException;
}