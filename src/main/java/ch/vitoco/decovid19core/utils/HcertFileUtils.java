package ch.vitoco.decovid19core.utils;

import static ch.vitoco.decovid19core.constants.Const.UTILITY_CLASS_EXCEPTION;

import java.util.Set;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Helper class of the Health Certificate file handling.
 */
public final class HcertFileUtils {

  /**
   * Set of allowed image file extensions.
   */
  private static final Set<String> ALLOWED_IMAGE_FORMAT = Set.of("png", "jpg", "jpeg");

  /**
   * Constructor.
   */
  private HcertFileUtils() {
    throw new IllegalStateException(UTILITY_CLASS_EXCEPTION);
  }

  /**
   * Helper method for verifieng if the given file is allowed.
   *
   * @param imageFile the image file
   * @return boolean
   */
  public static boolean isFileAllowed(MultipartFile imageFile) {
    String originalFilename = imageFile.getOriginalFilename();
    if (StringUtils.hasLength(originalFilename) && originalFilename.contains(".")) {
      String fileNameExt = getFileExtension(originalFilename);
      return ALLOWED_IMAGE_FORMAT.contains(fileNameExt);
    }
    return false;
  }

  /**
   * Helper method for retrieveing the file extension.
   *
   * @param fileName the file name with extension
   * @return file extension
   */
  public static String getFileExtension(String fileName) {
    return fileName.substring(fileName.lastIndexOf(".") + 1);
  }

}
