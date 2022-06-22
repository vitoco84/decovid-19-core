package ch.vitoco.decovid19core.server;

import java.util.ArrayList;
import java.util.List;

import ch.vitoco.decovid19core.model.validation.ValidationError;

import lombok.Data;

@Data
public class ValidationErrorServerResponse {

  private List<ValidationError> validationErrors = new ArrayList<>();

}
