package se.fulkopinglibraryweb.utils;

public interface Searchable<T> {
    /**
     * Checks if the item matches the search criteria.
     * @param item The item to check
     * @param query The search query
     * @param searchType The type of search to perform
     * @return true if the item matches the search criteria, false otherwise
     */
    public boolean matches(T item, String query, String searchType);

    /**
     * Checks if the item matches the search criteria using fuzzy matching.
     * @param item The item to check
     * @param query The search query
     * @param maxDistance The maximum allowed Levenshtein distance
     * @return true if the item matches the search criteria, false otherwise
     */
    public boolean fuzzyMatches(T item, String query, int maxDistance);

    /**
     * Checks if the item partially matches the search criteria.
     * @param item The item to check
     * @param query The search query
     * @return true if the item partially matches the search criteria, false otherwise
     */
    public boolean partialMatches(T item, String query);
}
