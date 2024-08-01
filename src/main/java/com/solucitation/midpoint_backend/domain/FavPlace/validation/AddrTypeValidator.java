package com.solucitation.midpoint_backend.domain.FavPlace.validation;

import com.solucitation.midpoint_backend.domain.FavPlace.entity.FavPlace;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AddrTypeValidator implements ConstraintValidator<ValidAddrType, String> {

    @Override
    public void initialize(ValidAddrType constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // @NotNull 애노테이션에 의해 처리
        }
        try {
            FavPlace.AddrType.valueOf(value);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}