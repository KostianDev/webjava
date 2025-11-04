package com.webjava.lab1.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Locale;

public class CosmicWordValidator
  implements ConstraintValidator<CosmicWordCheck, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) return true; // @NotBlank will catch null/empty
    String lower = value.toLowerCase(Locale.ROOT);
    return (
      lower.contains("star") ||
      lower.contains("galaxy") ||
      lower.contains("comet")
    );
  }
}
