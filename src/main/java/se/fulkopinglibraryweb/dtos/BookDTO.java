package se.fulkopinglibraryweb.dtos;

public class BookDTO {
    private String id;
    private String title;
    private String isbn;
    private String author;
    private int year;
    private boolean available;
    private boolean reserved;

    // Constructors
    public BookDTO() {}

    public BookDTO(String id, String title, String isbn, String author, int year, 
                  boolean available, boolean reserved) {
        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.author = author;
        this.year = year;
        this.available = available;
        this.reserved = reserved;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
