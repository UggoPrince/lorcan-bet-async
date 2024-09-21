package com.inventory_and_order_system.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidIdValidator implements ConstraintValidator<ValidId, Long> {

    @Override
    public void initialize(ValidId constraintAnnotation) {
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        log.info("LONG ID: {}", value);
        // Validate that the ID is not null and greater than 0
        return value != null && value > 0;
    }
}
