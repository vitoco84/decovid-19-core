package ch.vitoco.decovid19core.utils;

import static ch.vitoco.decovid19core.utils.Const.UTILITY_CLASS_EXCEPTION;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class HcertStringUtils {

  private HcertStringUtils() {
    throw new IllegalStateException(UTILITY_CLASS_EXCEPTION);
  }

  public static String sanitizeUserInputString(MultipartFile imageFile) {
    String originalFilename = imageFile.getOriginalFilename();
    StringBuilder stringBuilder = new StringBuilder();
    if (StringUtils.hasLength(originalFilename)) {
      stringBuilder.append(originalFilename.replaceAll("[\n\r\t]", "_"));
    }
    return stringBuilder.toString();
  }

}
