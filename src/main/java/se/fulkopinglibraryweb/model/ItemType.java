package se.fulkopinglibraryweb.model;

/**
 * Enum representing the type of a library item.
 */
public enum ItemType {
    BOOK,
    MAGAZINE,
    MEDIA,
    MOVIE,
    MUSIC,
    PODCAST;

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    /**
     * Convert a string value to an ItemType.
     * @param value The string value.
     * @return Corresponding ItemType.
     */
    public static ItemType fromString(String value) {
        return value != null ? ItemType.valueOf(value.toUpperCase()) : null;
    }
}
