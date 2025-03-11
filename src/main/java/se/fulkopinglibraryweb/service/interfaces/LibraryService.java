package se.fulkopinglibraryweb.service.interfaces;

import java.util.List;

public interface LibraryService {
    void borrowBook(String userId, String bookId);
    void returnBook(String userId, String bookId);
    List<String> getBorrowedBooks(String userId);
    boolean isBookAvailable(String bookId);
    void reserveBook(String userId, String bookId);
    void cancelReservation(String userId, String bookId);
    List<String> getReservedBooks(String userId);
}