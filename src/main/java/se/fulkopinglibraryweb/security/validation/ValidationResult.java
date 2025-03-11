package se.fulkopinglibraryweb.security.validation;

public class ValidationResult {
    private final boolean valid;
    private final String message;
    
    public ValidationResult(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }
    
    public boolean isValid() {
        return valid;
    }
    
    public String getMessage() {
        return message;
    }
    
    @Override
    public String toString() {
        return String.format("ValidationResult{valid=%s, message='%s'}", valid, message);
    }
}