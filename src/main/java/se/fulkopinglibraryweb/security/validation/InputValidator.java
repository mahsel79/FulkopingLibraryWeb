package se.fulkopinglibraryweb.security.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class InputValidator {
    private static final Map<String, Pattern> VALIDATION_PATTERNS = new HashMap<>();
    
    static {
        // Common validation patterns
        VALIDATION_PATTERNS.put("email", Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$"));
        VALIDATION_PATTERNS.put("username", Pattern.compile("^[a-zA-Z0-9_-]{3,20}$"));
        VALIDATION_PATTERNS.put("password", Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"));
        VALIDATION_PATTERNS.put("isbn", Pattern.compile("^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$"));
        VALIDATION_PATTERNS.put("date", Pattern.compile("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$"));
    }
    
    public static ValidationResult validate(String input, String type) {
        if (input == null || input.trim().isEmpty()) {
            return new ValidationResult(false, "Input cannot be empty");
        }
        
        Pattern pattern = VALIDATION_PATTERNS.get(type);
        if (pattern == null) {
            return new ValidationResult(false, "Unknown validation type");
        }
        
        boolean isValid = pattern.matcher(input).matches();
        return new ValidationResult(isValid, isValid ? "Valid input" : "Invalid " + type + " format");
    }
    
    public static ValidationResult validateLength(String input, int minLength, int maxLength) {
        if (input == null) {
            return new ValidationResult(false, "Input cannot be null");
        }
        
        int length = input.length();
        if (length < minLength || length > maxLength) {
            return new ValidationResult(false, 
                String.format("Input length must be between %d and %d characters", minLength, maxLength));
        }
        
        return new ValidationResult(true, "Valid length");
    }
    
    public static ValidationResult validateNumericRange(int value, int min, int max) {
        if (value < min || value > max) {
            return new ValidationResult(false, 
                String.format("Value must be between %d and %d", min, max));
        }
        return new ValidationResult(true, "Valid range");
    }
    
    public static boolean containsSQLInjection(String input) {
        if (input == null) return false;
        
        String sqlCheckPattern = "(?i)(.*)(\\b)(SELECT|INSERT|UPDATE|DELETE|DROP|UNION|ALTER|CREATE|WHERE)\\b.*";
        return Pattern.compile(sqlCheckPattern).matcher(input).matches();
    }
    
    public static boolean containsXSS(String input) {
        if (input == null) return false;
        
        String xssPattern = "(?i)<script.*?>|<.*?javascript:.*?>|<.*?\\bon\\w+\\s*=.*?>";
        return Pattern.compile(xssPattern).matcher(input).matches();
    }
}