package com.inventory_and_order_system.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotEmptyBodyValidator implements ConstraintValidator<NotEmptyBody, Object> {

    @Override
    public void initialize(NotEmptyBody constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;  // If request body is completely missing
        }

        // Additional check to ensure it's not an empty object
        return value.toString().trim().length() > 0;
    }
}
