package com.solucitation.midpoint_backend.domain.FavPlace.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AddrTypeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAddrType {
    String message() default "Invalid address type. Allowed values are HOME and WORK.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}