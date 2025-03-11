package se.fulkopinglibraryweb.validation.groups;

/**
 * Marker interfaces for validation groups to categorize different validation scenarios.
 */
public final class ValidationGroups {
    private ValidationGroups() {}

    /** Validation group for create operations */
    public interface Create {}

    /** Validation group for update operations */
    public interface Update {}

    /** Validation group for delete operations */
    public interface Delete {}

    /** Validation group for search operations */
    public interface Search {}

    /** Validation group for login operations */
    public interface Login {}

    /** Validation group for registration operations */
    public interface Registration {}
}