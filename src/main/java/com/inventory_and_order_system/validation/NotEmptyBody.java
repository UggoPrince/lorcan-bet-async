package com.inventory_and_order_system.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotEmptyBodyValidator.class)
public @interface NotEmptyBody {
    String message() default "Request body must not be empty";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
