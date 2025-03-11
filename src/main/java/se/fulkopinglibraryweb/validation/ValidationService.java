package se.fulkopinglibraryweb.validation;

import java.util.Map;
import java.util.Set;
import jakarta.validation.ConstraintViolation;

/**
 * Service interface for handling validation of form data and entities.
 */
public interface ValidationService {
    
    /**
     * Validates an object and returns any constraint violations.
     *
     * @param object The object to validate
     * @param groups Validation groups to apply (optional)
     * @return Set of constraint violations
     */
    <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups);
    
    /**
     * Validates specific fields of an object.
     *
     * @param object The object to validate
     * @param propertyNames The names of properties to validate
     * @return Map of property names to their constraint violations
     */
    <T> Map<String, Set<ConstraintViolation<T>>> validateProperties(T object, String... propertyNames);
    
    /**
     * Validates a single property value.
     *
     * @param object The object containing the property
     * @param propertyName The name of the property to validate
     * @param value The value to validate
     * @return Set of constraint violations
     */
    <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Object value);
    
    /**
     * Converts constraint violations to a map of field names and error messages.
     *
     * @param violations The set of constraint violations
     * @return Map of field names to error messages
     */
    <T> Map<String, String> getValidationErrors(Set<ConstraintViolation<T>> violations);
}
