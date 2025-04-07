package se.fulkopinglibraryweb.model;

import se.fulkopinglibraryweb.model.ItemType;
import se.fulkopinglibraryweb.model.LibraryItem;

public class Book extends LibraryItem {
    private String id;
    private String title;
    private String isbn;
    private String author;
    private int year;
    private boolean available;
    private boolean reserved;
    private String type = "Book";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getItemDetails() {
        return String.format("Book: %s by %s (ISBN: %s)", title, author, isbn);
    }

    @Override
    public String getItemType() {
        return ItemType.BOOK.toString();
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

 

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

}
