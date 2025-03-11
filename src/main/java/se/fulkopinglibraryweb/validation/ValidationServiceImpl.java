package se.fulkopinglibraryweb.validation;

import org.springframework.stereotype.Service;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.Validation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of ValidationService that uses the Java Bean Validation API.
 */
@Service
public class ValidationServiceImpl implements ValidationService {

    private final Validator validator;

    public ValidationServiceImpl() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
        return validator.validate(object, groups);
    }

    @Override
    public <T> Map<String, Set<ConstraintViolation<T>>> validateProperties(T object, String... propertyNames) {
        Map<String, Set<ConstraintViolation<T>>> violations = new HashMap<>();
        for (String propertyName : propertyNames) {
            violations.put(propertyName, validator.validateProperty(object, propertyName));
        }
        return violations;
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Object value) {
        return validator.validateValue((Class<T>) object.getClass(), propertyName, value);
    }

    @Override
    public <T> Map<String, String> getValidationErrors(Set<ConstraintViolation<T>> violations) {
        return violations.stream()
            .collect(Collectors.toMap(
                violation -> violation.getPropertyPath().toString(),
                ConstraintViolation::getMessage,
                (existing, replacement) -> existing + "; " + replacement
            ));
    }
}
