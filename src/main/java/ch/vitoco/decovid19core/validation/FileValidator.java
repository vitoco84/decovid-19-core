package ch.vitoco.decovid19core.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

/**
 * Representation class for validating files.
 */
public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

  @Override
  public void initialize(ValidFile constraintAnnotation) {
    // nothing here
  }

  @Override
  public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
    return isSupportedContentType(multipartFile.getContentType());
  }

  private boolean isSupportedContentType(String contentType) {
    if (contentType != null) {
      return contentType.equals("image/png") || contentType.equals("image/jpg") || contentType.equals("image/jpeg") ||
          contentType.equals("image/gif");
    }
    return false;
  }

}
