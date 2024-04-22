package com.yvolabs.streamapi.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * @author Yvonne N
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = MovieUpdateValidateNotEmpty.class)
public @interface ValidateUpdateFieldNotEmpty {
    String message() default "Invalid field value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
