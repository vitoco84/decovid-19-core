package ch.vitoco.decovid19core.utils;

import static ch.vitoco.decovid19core.constants.Const.UTILITY_CLASS_EXCEPTION;

import java.util.Set;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public final class HcertFileUtils {

  private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of("png", "jpg", "jpeg");

  private HcertFileUtils() {
    throw new IllegalStateException(UTILITY_CLASS_EXCEPTION);
  }

  public static boolean isFileAllowed(MultipartFile imageFile) {
    String originalFilename = imageFile.getOriginalFilename();
    if (StringUtils.hasLength(originalFilename) && originalFilename.contains(".")) {
      String fileNameExt = getFileExtension(originalFilename);
      return ALLOWED_IMAGE_EXTENSIONS.contains(fileNameExt);
    }
    return false;
  }

  public static String getFileExtension(String fileName) {
    return fileName.substring(fileName.lastIndexOf(".") + 1);
  }

}
