package se.fulkopinglibraryweb.utils;

import se.fulkopinglibraryweb.model.Book;
import se.fulkopinglibraryweb.service.search.SearchCriteria;

public class SearchBook extends GenericSearch<Book> {
    
    @Override
    public boolean matchesCriteria(Book book, SearchCriteria criteria) {
        if (criteria == null) {
            return true;
        }

        // Search term matching
        String searchTerm = criteria.getSearchTerm();
        if (searchTerm != null && !searchTerm.isEmpty()) {
            String lowerSearch = searchTerm.toLowerCase();
            if (!book.getTitle().toLowerCase().contains(lowerSearch) &&
                !book.getAuthor().toLowerCase().contains(lowerSearch) &&
                !book.getIsbn().toLowerCase().contains(lowerSearch)) {
                return false;
            }
        }

        // Filter matching
        String filterField = criteria.getFilterField();
        String filterValue = criteria.getFilterValue();
        if (filterField != null && filterValue != null) {
            switch (filterField.toLowerCase()) {
                case "title":
                    if (!book.getTitle().equalsIgnoreCase(filterValue)) {
                        return false;
                    }
                    break;
                case "author":
                    if (!book.getAuthor().equalsIgnoreCase(filterValue)) {
                        return false;
                    }
                    break;
                case "isbn":
                    if (!book.getIsbn().equalsIgnoreCase(filterValue)) {
                        return false;
                    }
                    break;
                case "year":
                    if (book.getYear() != Integer.parseInt(filterValue)) {
                        return false;
                    }
                    break;
                case "available":
                    if (book.isAvailable() != Boolean.parseBoolean(filterValue)) {
                        return false;
                    }
                    break;
                default:
                    return false;
            }
        }

        return true;
    }
}
