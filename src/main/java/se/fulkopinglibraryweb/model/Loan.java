package se.fulkopinglibraryweb.model;

import java.util.Date;

public class Loan {
    private String id;
    private String userId;
    private String itemId;
    private String bookId;
    private Book book;
    private String magazineId;
    private Magazine magazine;
    private String mediaId;
    private Media media;
    private LoanStatus status;
    private Date loanDate;
    private Date dueDate;

    public Loan() {
    }

    public Loan(String userId, String itemId, Date loanDate, Date dueDate, LoanStatus status) {
        this.userId = userId;
        this.itemId = itemId;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.status = status;
    }

    public void setBook(String bookId) {
        this.bookId = bookId;
    }

    public void setBookDetails(Book book) {
        this.book = book;
        this.bookId = book.getId();
    }

    public void setMagazine(String magazineId) {
        this.magazineId = magazineId;
    }

    public void setMagazineDetails(Magazine magazine) {
        this.magazine = magazine;
        this.magazineId = magazine.getId();
    }

    public void setMedia(String mediaId) {
        this.mediaId = mediaId;
    }

    public void setMediaDetails(Media media) {
        this.media = media;
        this.mediaId = media.getId();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getMagazineId() {
        return magazineId;
    }

    public void setMagazineId(String magazineId) {
        this.magazineId = magazineId;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public Book getBook() {
        return book;
    }

    public Magazine getMagazine() {
        return magazine;
    }

    public Media getMedia() {
        return media;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public Date getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
}
