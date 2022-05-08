package ch.vitoco.decovid19core.utils;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class HcertFileUtilsTest {

  private static final String NAME = "foo";
  private static final String FILE_NAME_PNG_EXT_ALLOWED = "TestFile.png";
  private static final String EMPTY_FILE_NAME = "";
  private static final String FILE_NAME_WITHOUT_EXT = "Test.File";
  private static final String LONG_FILE_NAME_PNG_EXT_ALLOWED = "Test_File_With..._Points_And_#@_Special_Chars.png";
  private static final String FILE_NAME_GIF_EXT_NOT_ALLOWED = "TestFile.gif";
  private static final String PNG_EXT = "png";
  private static final String FILE_CONTENT = "Hello World!";

  @Test
  void shouldReturnFileExtension() {
    String expectedPNGExt1 = HcertFileUtils.getFileExtension(FILE_NAME_PNG_EXT_ALLOWED);
    String expectedPNGExt2 = HcertFileUtils.getFileExtension(LONG_FILE_NAME_PNG_EXT_ALLOWED);

    assertEquals(PNG_EXT, expectedPNGExt1);
    assertEquals(PNG_EXT, expectedPNGExt2);
  }

  @Test
  void shouldCheckIfFileIsAllowed() {
    MockMultipartFile mockFileAllowed = new MockMultipartFile(NAME, FILE_NAME_PNG_EXT_ALLOWED,
        MediaType.MULTIPART_FORM_DATA_VALUE, FILE_CONTENT.getBytes());
    MockMultipartFile mockFileNotAllowed = new MockMultipartFile(NAME, FILE_NAME_GIF_EXT_NOT_ALLOWED,
        MediaType.MULTIPART_FORM_DATA_VALUE, FILE_CONTENT.getBytes());
    MockMultipartFile mockFileEmptyFileName = new MockMultipartFile(NAME, EMPTY_FILE_NAME,
        MediaType.MULTIPART_FORM_DATA_VALUE, FILE_CONTENT.getBytes());
    MockMultipartFile mockFileWithoutExt = new MockMultipartFile(NAME, FILE_NAME_WITHOUT_EXT,
        MediaType.MULTIPART_FORM_DATA_VALUE, FILE_CONTENT.getBytes());

    boolean resultAllowed = HcertFileUtils.isFileAllowed(mockFileAllowed);
    boolean resultNotAllowed = HcertFileUtils.isFileAllowed(mockFileNotAllowed);
    boolean resultEmptyFileName = HcertFileUtils.isFileAllowed(mockFileEmptyFileName);
    boolean resultFileNameWithoutExt = HcertFileUtils.isFileAllowed(mockFileWithoutExt);

    assertTrue(resultAllowed);
    assertFalse(resultNotAllowed);
    assertFalse(resultEmptyFileName);
    assertFalse(resultFileNameWithoutExt);
  }

}
