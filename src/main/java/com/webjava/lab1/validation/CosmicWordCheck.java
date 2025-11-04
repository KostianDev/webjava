package com.webjava.lab1.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = CosmicWordValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface CosmicWordCheck {
  String message() default "must contain a cosmic word (star, galaxy, comet)";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
