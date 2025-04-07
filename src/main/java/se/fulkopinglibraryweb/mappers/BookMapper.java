package se.fulkopinglibraryweb.mappers;

import se.fulkopinglibraryweb.model.Book;
import se.fulkopinglibraryweb.dtos.BookDTO;

public class BookMapper {

    public static BookDTO toDTO(Book book) {
        return new BookDTO(
            book.getId(),
            book.getTitle(),
            book.getIsbn(),
            book.getAuthor(),
            book.getYear(),
            book.isAvailable(),
            book.isReserved()
        );
    }

    public static Book toEntity(BookDTO dto) {
        Book book = new Book();
        book.setId(dto.getId());
        book.setTitle(dto.getTitle());
        book.setIsbn(dto.getIsbn());
        book.setAuthor(dto.getAuthor());
        book.setYear(dto.getYear());
        book.setAvailable(dto.isAvailable());
        book.setReserved(dto.isReserved());
        return book;
    }
}
