package se.fulkopinglibraryweb.service.impl;

import org.springframework.stereotype.Service;
import se.fulkopinglibraryweb.model.Book;
import se.fulkopinglibraryweb.model.User;
import se.fulkopinglibraryweb.service.interfaces.BookService;
import se.fulkopinglibraryweb.service.interfaces.LibraryService;
import se.fulkopinglibraryweb.service.interfaces.UserService;
import se.fulkopinglibraryweb.service.interfaces.LoanService;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class LibraryServiceImpl implements LibraryService {
    private final BookService bookService;
    private final UserService userService;
    private final LoanService loanService;

    public LibraryServiceImpl(BookService bookService, UserService userService, LoanService loanService) {
        this.bookService = bookService;
        this.userService = userService;
        this.loanService = loanService;
    }

    @Override
    public void borrowBook(String userId, String bookId) {
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            bookService.getById(bookId);
            user.addBorrowedItem(bookId);
            loanService.createLoan(userId, bookId);
        }
    }

    @Override
    public void returnBook(String userId, String bookId) {
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.hasBorrowed(bookId)) {
                user.removeBorrowedItem(bookId);
                loanService.returnLoan(userId, bookId);
            }
        }
    }

    @Override
    public List<String> getBorrowedBooks(String userId) {
        Optional<User> userOptional = userService.findById(userId);
        return userOptional.map(User::getBorrowedItems).orElse(List.of());
    }

    @Override
    public boolean isBookAvailable(String bookId) {
        return bookService.isAvailable(bookId);
    }

    @Override
    public void reserveBook(String userId, String bookId) {
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.addReservedItem(bookId);
            bookService.reserveBook(bookId);
        }
    }

    @Override
    public void cancelReservation(String userId, String bookId) {
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.removeReservedItem(bookId);
            bookService.cancelReservation(bookId);
        }
    }

    @Override
    public List<String> getReservedBooks(String userId) {
        Optional<User> userOptional = userService.findById(userId);
        return userOptional.map(User::getReservedItems).orElse(List.of());
    }
}
