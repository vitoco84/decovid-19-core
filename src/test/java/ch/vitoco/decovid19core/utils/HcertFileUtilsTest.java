package ch.vitoco.decovid19core.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

class HcertFileUtilsTest {

  private static final String NAME = "foo";
  private static final String FILE_NAME_PNG_EXT_ALLOWED = "TestFile.png";
  private static final String EMPTY_FILE_NAME = "";
  private static final String FILE_NAME_WITHOUT_EXT = "Test.File";
  private static final String FILE_NAME_WITH_SPECIAL_CHARS = "Test_File_With..._Points_And_#@_Special_Chars.png";
  private static final String FILE_NAME_PDF_EXT_NOT_ALLOWED = "TestFile.pdf";
  private static final String FILE_CONTENT = "Hello World!";


  @Test
  void shouldReturnTrueForValidFileNameExtension() {
    MockMultipartFile mockFile = new MockMultipartFile(NAME, FILE_NAME_PNG_EXT_ALLOWED,
        MediaType.MULTIPART_FORM_DATA_VALUE, FILE_CONTENT.getBytes());
    boolean result = HcertFileUtils.isFileAllowed(mockFile);

    assertTrue(result);
  }

  @Test
  void shouldReturnFalseForNotValidFileNameExtension() {
    MockMultipartFile mockFile = new MockMultipartFile(NAME, FILE_NAME_PDF_EXT_NOT_ALLOWED,
        MediaType.MULTIPART_FORM_DATA_VALUE, FILE_CONTENT.getBytes());
    boolean result = HcertFileUtils.isFileAllowed(mockFile);

    assertFalse(result);
  }

  @Test
  void shouldReturnFalseForEmptyFileName() {
    MockMultipartFile mockFile = new MockMultipartFile(NAME, EMPTY_FILE_NAME, MediaType.MULTIPART_FORM_DATA_VALUE,
        FILE_CONTENT.getBytes());
    boolean result = HcertFileUtils.isFileAllowed(mockFile);

    assertFalse(result);
  }

  @Test
  void shouldReturnFalseForFileNameWithoutExtension() {
    MockMultipartFile mockFile = new MockMultipartFile(NAME, FILE_NAME_WITHOUT_EXT, MediaType.MULTIPART_FORM_DATA_VALUE,
        FILE_CONTENT.getBytes());
    boolean result = HcertFileUtils.isFileAllowed(mockFile);

    assertFalse(result);
  }

  @Test
  void shouldReturnTrueForValidFileNameWithSpecialCharacters() {
    MockMultipartFile mockFile = new MockMultipartFile(NAME, FILE_NAME_WITH_SPECIAL_CHARS,
        MediaType.MULTIPART_FORM_DATA_VALUE, FILE_CONTENT.getBytes());
    boolean result = HcertFileUtils.isFileAllowed(mockFile);

    assertTrue(result);
  }

}
