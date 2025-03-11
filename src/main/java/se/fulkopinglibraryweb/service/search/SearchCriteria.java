package se.fulkopinglibraryweb.service.search;

public class SearchCriteria {
    private String searchTerm;
    private String filterField;
    private String filterValue;
    private String sortField;
    private SortDirection sortDirection;

    public enum SortDirection {
        ASC,
        DESC
    }

    public SearchCriteria() {
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String getFilterField() {
        return filterField;
    }

    public void setFilterField(String filterField) {
        this.filterField = filterField;
    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    public String field() {
        return filterField;
    }

    public String value() {
        return filterValue;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public SortDirection getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(SortDirection sortDirection) {
        this.sortDirection = sortDirection;
    }
}
