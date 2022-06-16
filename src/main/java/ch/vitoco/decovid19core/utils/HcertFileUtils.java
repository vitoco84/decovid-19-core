package ch.vitoco.decovid19core.utils;

import java.util.Set;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Helper class of the Health Certificate file handling.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HcertFileUtils {

  /**
   * Set of allowed image file extensions.
   */
  private static final Set<String> ALLOWED_IMAGE_FORMAT = Set.of("png", "jpg", "jpeg", "gif");

  /**
   * Helper method for verifieng if the given file is allowed.
   *
   * @param imageFile the image file
   * @return boolean
   */
  public static boolean isFileAllowed(MultipartFile imageFile) {
    String originalFilename = imageFile.getOriginalFilename();
    if (StringUtils.hasLength(originalFilename) && originalFilename.contains(".")) {
      String fileNameExt = StringUtils.getFilenameExtension(originalFilename);
      return ALLOWED_IMAGE_FORMAT.contains(fileNameExt);
    }
    return false;
  }

}
