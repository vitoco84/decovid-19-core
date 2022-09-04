package ch.vitoco.decovid19core.utils;

import java.util.Set;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

/**
 * Helper class of the Health Certificate file handling.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HcertFileUtils {

  /**
   * Set of allowed image file extensions.
   */
  private static final Set<String> ALLOWED_IMAGE_FORMAT = Set.of("png", "jpg", "jpeg", "gif");
  private static final int MAX_FILE_SIZE = 2097152;

  /**
   * Helper method for verifying if the given file is allowed.
   *
   * @param imageFile the image file
   * @return boolean
   */
  public static boolean isFileAllowed(MultipartFile imageFile) {
    String originalFilename = imageFile.getOriginalFilename();
    if (StringUtils.hasLength(originalFilename) && originalFilename.contains(".")) {
      String fileNameExt = StringUtils.getFilenameExtension(originalFilename);
      if (StringUtils.hasLength(fileNameExt)) {
        fileNameExt = fileNameExt.toLowerCase();
      }
      return ALLOWED_IMAGE_FORMAT.contains(fileNameExt);
    }
    return false;
  }

  /**
   * Helper method for checking max uploaded file size.
   *
   * @param imageFile the image file
   */
  public static void checkMaxFileSize(MultipartFile imageFile) {
    if (imageFile.getSize() > MAX_FILE_SIZE) {
      throw new MaxUploadSizeExceededException(imageFile.getSize());
    }
  }

}
