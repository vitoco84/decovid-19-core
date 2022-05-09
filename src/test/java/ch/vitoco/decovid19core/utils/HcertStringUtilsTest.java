package ch.vitoco.decovid19core.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

class HcertStringUtilsTest {

  private static final String NAME = "foo";
  private static final String FILE_NAME = "Test\nFi\r\tl\n\r\te.png";
  private static final String FILE_NAME_EMPTY = "";
  private static final String FILE_NAME_EXPECTED = "Test_Fi__l___e.png";
  private static final String FILE_CONTENT = "Hello World!";

  @Test
  void shouldSanitizeUserInputString() {
    MockMultipartFile mockFileAllowed = new MockMultipartFile(NAME, FILE_NAME, MediaType.MULTIPART_FORM_DATA_VALUE,
        FILE_CONTENT.getBytes());
    MockMultipartFile mockFileAllowedNotAllowed = new MockMultipartFile(NAME, FILE_NAME_EMPTY,
        MediaType.MULTIPART_FORM_DATA_VALUE, FILE_CONTENT.getBytes());

    String actualOriginalFileName = HcertStringUtils.sanitizeUserInputString(mockFileAllowed);
    String actualOriginalFileNameEmpty = HcertStringUtils.sanitizeUserInputString(mockFileAllowedNotAllowed);

    assertEquals(FILE_NAME_EXPECTED, actualOriginalFileName);
    assertEquals("", actualOriginalFileNameEmpty);
  }

}
