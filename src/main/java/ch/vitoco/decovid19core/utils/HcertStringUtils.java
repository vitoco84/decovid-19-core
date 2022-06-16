package ch.vitoco.decovid19core.utils;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Helper class of the Health Certificate input handling.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HcertStringUtils {


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
