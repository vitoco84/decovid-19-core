package ch.vitoco.decovid19core.server;

import java.util.ArrayList;
import java.util.List;

import ch.vitoco.decovid19core.validation.ValidationError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Representation class for Input Validation Errors Server Response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ValidationErrorServerResponse {

  /**
   * List of Validation Errors.
   */
  private List<ValidationError> validationErrors = new ArrayList<>();

}
