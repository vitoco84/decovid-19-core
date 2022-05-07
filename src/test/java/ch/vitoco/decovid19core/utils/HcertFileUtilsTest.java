package ch.vitoco.decovid19core.utils;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class HcertFileUtilsTest {

  private static final String NAME = "foo";
  private static final String FILE_NAME_ALLOWED = "TestFile.png";
  private static final String FILE_NAME_NOT_ALLOWED = "TestFile.gif";
  private static final String FILE_NAME_EXT = "png";
  private static final String FILE_CONTENT = "Hello World";

  @Test
  void shouldReturnFileExtension() {
    String result = HcertFileUtils.getFileExtension(FILE_NAME_ALLOWED);

    assertEquals(FILE_NAME_EXT, result);
  }

  @Test
  void shouldCheckIfFileIsAllowed() {
    MockMultipartFile mockFileAllowed = new MockMultipartFile(NAME, FILE_NAME_ALLOWED, MediaType.MULTIPART_FORM_DATA_VALUE, FILE_CONTENT.getBytes());
    MockMultipartFile mockFileNotAllowed = new MockMultipartFile(NAME, FILE_NAME_NOT_ALLOWED, MediaType.MULTIPART_FORM_DATA_VALUE, FILE_CONTENT.getBytes());
    boolean resultAllowed = HcertFileUtils.isFileAllowed(mockFileAllowed);
    boolean resultNotAllowed = HcertFileUtils.isFileAllowed(mockFileNotAllowed);

    assertTrue(resultAllowed);
    assertFalse(resultNotAllowed);
  }

}
