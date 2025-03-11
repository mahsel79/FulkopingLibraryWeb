package se.fulkopinglibraryweb.validation.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Validator implementation for the ValidInput annotation.
 * Handles validation of string inputs based on the constraints specified in the annotation.
 */
public class ValidInputValidator implements ConstraintValidator<ValidInput, String> {

    private int minLength;
    private int maxLength;
    private Pattern pattern;
    private boolean required;
    private boolean trim;

    @Override
    public void initialize(ValidInput constraintAnnotation) {
        this.minLength = constraintAnnotation.minLength();
        this.maxLength = constraintAnnotation.maxLength();
        this.pattern = Pattern.compile(constraintAnnotation.pattern());
        this.required = constraintAnnotation.required();
        this.trim = constraintAnnotation.trim();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Handle null values
        if (value == null) {
            return !required;
        }

        // Apply trimming if configured
        String processedValue = trim ? value.trim() : value;

        // Check if empty string is allowed
        if (processedValue.isEmpty()) {
            return !required;
        }

        // Check length constraints
        if (processedValue.length() < minLength || processedValue.length() > maxLength) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                String.format("Length must be between %d and %d", minLength, maxLength)
            ).addConstraintViolation();
            return false;
        }

        // Check pattern match
        if (!pattern.matcher(processedValue).matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Input does not match the required pattern"
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}
