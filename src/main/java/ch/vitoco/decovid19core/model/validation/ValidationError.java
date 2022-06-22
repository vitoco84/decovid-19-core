package ch.vitoco.decovid19core.model.validation;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ValidationError {

  private final String fieldName;

  private final String message;

}
