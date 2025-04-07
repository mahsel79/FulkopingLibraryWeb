package se.fulkopinglibraryweb.dtos;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for book borrowing operations.
 * Contains the necessary information to process a book borrow/return request.
 */
public class BorrowRequest {
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotBlank(message = "Book ID is required")
    private String bookId;

    public BorrowRequest() {
    }

    public BorrowRequest(String userId, String bookId) {
        this.userId = userId;
        this.bookId = bookId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    @Override
    public String toString() {
        return "BorrowRequest{" +
                "userId='" + userId + '\'' +
                ", bookId='" + bookId + '\'' +
                '}';
    }
}
