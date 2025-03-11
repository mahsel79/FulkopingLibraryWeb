package se.fulkopinglibraryweb.service;

import se.fulkopinglibraryweb.model.Book;
import se.fulkopinglibraryweb.model.User;
import java.util.concurrent.ExecutionException;

public interface LibraryService {
    void borrowBook(String userId, String bookId) throws ExecutionException, InterruptedException;
    void returnBook(String userId, String bookId) throws ExecutionException, InterruptedException;
}
