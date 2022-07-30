package ch.vitoco.decovid19core.validation;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Representation class of Validation Errors.
 */
@Data
@RequiredArgsConstructor
public class ValidationError {

  /**
   * Error Field Name.
   */
  private final String fieldName;
  /**
   * Error Message.
   */
  private final String message;

}
