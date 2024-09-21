package com.inventory_and_order_system.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotEmptyIfPresentValidator implements ConstraintValidator<NotEmptyIfPresent, String> {
    @Override
    public void initialize(NotEmptyIfPresent constraintAnnotation) {
        // No initialization required
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // If the field is null, it's considered valid (because it's not present)
        if (value == null) {
            return true;
        }
        // If the field is present, ensure it's not empty
        return !value.trim().isEmpty();
    }
}
