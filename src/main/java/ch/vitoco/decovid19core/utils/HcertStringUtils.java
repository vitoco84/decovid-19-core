package ch.vitoco.decovid19core.utils;

import static ch.vitoco.decovid19core.constants.ExceptionMessages.UTILITY_CLASS_EXCEPTION;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Helper class of the Health Certificate input handling.
 */
public final class HcertStringUtils {

  /**
   * Constructor.
   */
  private HcertStringUtils() {
    throw new IllegalStateException(UTILITY_CLASS_EXCEPTION);
  }

  /**
   * Helper method for sanitizing the input prefix of the Health Certificate.
   *
   * @param imageFile the image file
   * @return sanitized file name
   */
  public static String sanitizeUserInputString(MultipartFile imageFile) {
    String originalFilename = imageFile.getOriginalFilename();
    StringBuilder stringBuilder = new StringBuilder();
    if (StringUtils.hasLength(originalFilename)) {
      stringBuilder.append(originalFilename.replaceAll("[\n\r\t]", "_"));
    }
    return stringBuilder.toString();
  }

}
