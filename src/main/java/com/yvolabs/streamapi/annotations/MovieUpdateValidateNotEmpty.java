package com.yvolabs.streamapi.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @author Yvonne N
 */
public class MovieUpdateValidateNotEmpty implements ConstraintValidator<ValidateUpdateFieldNotEmpty, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value != null) {
            return value.length() >= 3;
        }
        return true;
    }
}
