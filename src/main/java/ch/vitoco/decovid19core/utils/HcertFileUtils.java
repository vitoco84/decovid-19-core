package ch.vitoco.decovid19core.utils;

import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

import static ch.vitoco.decovid19core.utils.ExceptionMessages.UTILITY_CLASS_EXCEPTION_MESSAGE;

public class HcertFileUtils {

  private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of("png", "jpg");

  private HcertFileUtils() {
    throw new IllegalStateException(UTILITY_CLASS_EXCEPTION_MESSAGE);
  }

  public static boolean isFileAllowed(MultipartFile imageFile) {
    String originalFilename = imageFile.getOriginalFilename();
    if (originalFilename != null) {
      String fileNameExt = getFileExtension(originalFilename);
      return ALLOWED_IMAGE_EXTENSIONS.contains(fileNameExt);
    }
    return false;
  }

  public static String getFileExtension(String fileName) {
    return fileName.substring(fileName.lastIndexOf(".") + 1);
  }

}
