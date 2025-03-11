package se.fulkopinglibraryweb.validation.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom validation annotation for input validation with configurable rules.
 * This can be used to validate strings with specific patterns, length constraints,
 * and required/optional status.
 */
@Documented
@Constraint(validatedBy = ValidInputValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidInput {
    String message() default "Invalid input";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * Minimum length of the input string.
     */
    int minLength() default 0;
    
    /**
     * Maximum length of the input string.
     */
    int maxLength() default Integer.MAX_VALUE;
    
    /**
     * Regular expression pattern the input must match.
     */
    String pattern() default ".*";
    
    /**
     * Whether the field is required (cannot be null or empty).
     */
    boolean required() default true;
    
    /**
     * Whether to trim the input before validation.
     */
    boolean trim() default true;
}
