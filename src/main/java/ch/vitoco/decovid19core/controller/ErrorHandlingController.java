package ch.vitoco.decovid19core.controller;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import ch.vitoco.decovid19core.server.ValidationErrorServerResponse;
import ch.vitoco.decovid19core.validation.ValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandlingController {

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ValidationErrorServerResponse onConstraintValidationException(ConstraintViolationException e) {
    ValidationErrorServerResponse error = new ValidationErrorServerResponse();
    for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
      error.getValidationErrors()
          .add(new ValidationError(violation.getPropertyPath().toString(), violation.getMessage()));
    }
    return error;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ValidationErrorServerResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    ValidationErrorServerResponse error = new ValidationErrorServerResponse();
    for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
      error.getValidationErrors().add(new ValidationError(fieldError.getField(), fieldError.getDefaultMessage()));
    }
    return error;
  }

}
