package ch.vitoco.decovid19core.service;

import static ch.vitoco.decovid19core.constants.Const.QR_CODE_CORRUPTED_EXCEPTION;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.Nonnull;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import ch.vitoco.decovid19core.enums.HcertAlgoKeys;
import ch.vitoco.decovid19core.exception.ImageNotValidException;
import ch.vitoco.decovid19core.model.HcertContentDTO;
import ch.vitoco.decovid19core.model.HcertTimeStampDTO;
import ch.vitoco.decovid19core.server.HcertServerRequest;
import ch.vitoco.decovid19core.server.HcertServerResponse;

class Decovid19ServiceTest {

  private static final String NAME = "foo";
  private static final String FILE_NAME_PNG_EXT_ALLOWED = "TestFile.png";
  private static final String FILE_NAME_PNG_EXT_NOT_ALLOWED = "TestFile.gif";

  private static final Path SWISS_QR_CODE_VACC_CERT_IMG_PATH = Paths.get(
      "src/test/resources/swissQRCodeVaccinationCertificate.png");
  private static final Path SWISS_QR_CODE_VACC_CERT_JSON_PATH = Paths.get(
      "src/test/resources/swissQRCodeVaccinationCertificateContent.json");

  private static final Path SWISS_QR_CODE_TEST_CERT_IMG_PATH = Paths.get(
      "src/test/resources/swissQRCodeTestCertificate.png");
  private static final Path SWISS_QR_CODE_TEST_CERT_JSON_PATH = Paths.get(
      "src/test/resources/swissQRCodeTestCertificateContent.json");

  private static final Path SWISS_QR_CODE_RECOVERY_CERT_IMG_PATH = Paths.get(
      "src/test/resources/swissQRCodeRecoveryCertificate.png");
  private static final Path SWISS_QR_CODE_RECOVERY_CERT_JSON_PATH = Paths.get(
      "src/test/resources/swissQRCodeRecoveryCertificateContent.json");

  private static final String SWISS_QR_CODE_VACC_HC1_PREFIX = "HC1:NCFS605G0/3WUWGSLKH47GO0KNJ9DSWQIIWT9CK4600XKY-CE59-G80:84F35RIV R2F3FMMTTBY50.FK6ZK7:EDOLOPCO8F6%E3.DA%EOPC1G72A6YM83G7NA7H:6JM8D%6I:61S8ZW6HL6C460S8VF6VX6UPC0JCZ69FVCPD0LVC6JD846Y96A466W5B56+EDG8F3I80/D6$CBECSUER:C2$NS346$C2%E9VC- CSUE145GB8JA5B$D% D3IA4W5646946%96X47.JCP9EJY8L/5M/5546.96D463KC.SC4KCD3DX47B46IL6646H*6Z/E5JD%96IA74R6646407GVC*JC1A6/Q63W5KF6746TPCBEC7ZKW.CU2DNXO VD5$C JC3/DMP8$ILZEDZ CW.C9WE.Y9AY8+S9VIAI3D8WEVM8:S9C+9$PC5$CUZCY$5Y$5FBBM00T%LTAT1MOQYR8GUN$K15LIGG2P27%A46BT52VUTL.1*B89Y5B428HRSR3I/E5DS/8NBY4H2BCN8NP1D4B:0K9UQQ67BLTH21AF0V8G52R 62+5BQYCV03SO79O6K+8UXL$T4$%RT150DUHZK+Q9TIE+IMQU4E/Q4T303TKWNXTSORE.4WNPCJX66NN-2F9IHTYLR6IR UAB98RR1A0P9DL0CS5KZ*HEGT1%TQWELFQHG5/JO9TI:.T1JQF.K7 EJ 2/CI5GASQP7ULRX4-07%9W2139E2HMGW99Q DQJADB3UAJKUCOVLG+9T+J:15.12U+OBMCJ1KZ+C+87I8I9JGA0T%U2CMFHI5U:L400C.CC/K3KJZ3OM/D59TBL5AZFMPIW4";
  private static final String SWISS_QR_CODE_VACC_HC1_PREFIX_WRONG = "foobar";
  private static final String SWISS_QR_CODE_VACC_KID = "mmrfzpMU6xc=";
  private static final String SWISS_QR_CODE_ISSUER = "CH BAG";

  private final Decovid19ValueSetService decovid19ValueSetService = new Decovid19ValueSetService();
  private final Decovid19HcertService decovid19HcertService = new Decovid19HcertService();
  private final Decovid19Service decovid19Service = new Decovid19Service(decovid19ValueSetService,
      decovid19HcertService);

  @Test
  void shouldReturnVaccHealthCertificateResponseFromImageFile() throws IOException, ParseException {
    InputStream testVaccImageInputStream = Files.newInputStream(SWISS_QR_CODE_VACC_CERT_IMG_PATH);
    MockMultipartFile mockMultipartFile = new MockMultipartFile(NAME, FILE_NAME_PNG_EXT_ALLOWED,
        MediaType.MULTIPART_FORM_DATA_VALUE, testVaccImageInputStream);
    testVaccImageInputStream.close();

    JSONObject jsonObject = getJsonObjectFromResources(SWISS_QR_CODE_VACC_CERT_JSON_PATH);
    String expectedHcertPrefix = (String) jsonObject.get("PREFIX");

    JSONObject jsonHcertPaylod = (JSONObject) jsonObject.get("JSON");
    String expectedVersion = (String) jsonHcertPaylod.get("ver");
    String expectedDateOfBirth = (String) jsonHcertPaylod.get("dob");

    ResponseEntity<HcertServerResponse> healthCertificateContent = decovid19Service.getHealthCertificateContent(
        mockMultipartFile);

    HttpStatus statusCode = healthCertificateContent.getStatusCode();
    String hcertPrefix = healthCertificateContent.getBody().getHcertPrefix();
    String hcertKID = healthCertificateContent.getBody().getHcertKID();
    String hcertAlgo = healthCertificateContent.getBody().getHcertAlgo();
    HcertContentDTO hcertContent = (HcertContentDTO) healthCertificateContent.getBody().getHcertContent();
    String hcertIssuer = healthCertificateContent.getBody().getHcertIssuer();
    HcertTimeStampDTO hcertTimeStamp = healthCertificateContent.getBody().getHcertTimeStamp();

    assertEquals(HttpStatus.OK, statusCode);
    assertEquals(expectedHcertPrefix, hcertPrefix);
    assertEquals(SWISS_QR_CODE_VACC_KID, hcertKID);
    assertEquals(HcertAlgoKeys.RSA_PSS_256.toString(), hcertAlgo);
    assertEquals(expectedVersion, hcertContent.getVer());
    assertEquals(expectedDateOfBirth, hcertContent.getDob());
    assertFalse(hcertContent.getV().isEmpty());
    assertEquals(SWISS_QR_CODE_ISSUER, hcertIssuer);
    assertTrue(hcertTimeStamp.getHcerExpirationTime().contains("2022-05-29"));
    assertTrue(hcertTimeStamp.getHcertIssuedAtTime().contains("2021-05-29"));
  }

  @Test
  void shouldReturnTestHealthCertificateResponseFromImageFile() throws IOException, ParseException {
    InputStream testTestImageInputStream = Files.newInputStream(SWISS_QR_CODE_TEST_CERT_IMG_PATH);
    MockMultipartFile mockMultipartFile = new MockMultipartFile(NAME, FILE_NAME_PNG_EXT_ALLOWED,
        MediaType.MULTIPART_FORM_DATA_VALUE, testTestImageInputStream);
    testTestImageInputStream.close();

    JSONObject jsonObject = getJsonObjectFromResources(SWISS_QR_CODE_TEST_CERT_JSON_PATH);
    String expectedHcertPrefix = (String) jsonObject.get("PREFIX");

    JSONObject jsonHcertPaylod = (JSONObject) jsonObject.get("JSON");
    String expectedVersion = (String) jsonHcertPaylod.get("ver");
    String expectedDateOfBirth = (String) jsonHcertPaylod.get("dob");

    ResponseEntity<HcertServerResponse> healthCertificateContent = decovid19Service.getHealthCertificateContent(
        mockMultipartFile);

    HttpStatus statusCode = healthCertificateContent.getStatusCode();
    String hcertPrefix = healthCertificateContent.getBody().getHcertPrefix();
    String hcertKID = healthCertificateContent.getBody().getHcertKID();
    String hcertAlgo = healthCertificateContent.getBody().getHcertAlgo();
    HcertContentDTO hcertContent = (HcertContentDTO) healthCertificateContent.getBody().getHcertContent();
    String hcertIssuer = healthCertificateContent.getBody().getHcertIssuer();
    HcertTimeStampDTO hcertTimeStamp = healthCertificateContent.getBody().getHcertTimeStamp();

    assertEquals(HttpStatus.OK, statusCode);
    assertEquals(expectedHcertPrefix, hcertPrefix);
    assertEquals(SWISS_QR_CODE_VACC_KID, hcertKID);
    assertEquals(HcertAlgoKeys.RSA_PSS_256.toString(), hcertAlgo);
    assertEquals(expectedVersion, hcertContent.getVer());
    assertEquals(expectedDateOfBirth, hcertContent.getDob());
    assertFalse(hcertContent.getT().isEmpty());
    assertEquals(SWISS_QR_CODE_ISSUER, hcertIssuer);
    assertTrue(hcertTimeStamp.getHcerExpirationTime().contains("2022-05-29"));
    assertTrue(hcertTimeStamp.getHcertIssuedAtTime().contains("2021-05-29"));
  }

  @Test
  void shouldReturnRecoveryHealthCertificateResponseFromImageFile() throws IOException, ParseException {
    InputStream testTestImageInputStream = Files.newInputStream(SWISS_QR_CODE_RECOVERY_CERT_IMG_PATH);
    MockMultipartFile mockMultipartFile = new MockMultipartFile(NAME, FILE_NAME_PNG_EXT_ALLOWED,
        MediaType.MULTIPART_FORM_DATA_VALUE, testTestImageInputStream);
    testTestImageInputStream.close();

    JSONObject jsonObject = getJsonObjectFromResources(SWISS_QR_CODE_RECOVERY_CERT_JSON_PATH);
    String expectedHcertPrefix = (String) jsonObject.get("PREFIX");

    JSONObject jsonHcertPaylod = (JSONObject) jsonObject.get("JSON");
    String expectedVersion = (String) jsonHcertPaylod.get("ver");
    String expectedDateOfBirth = (String) jsonHcertPaylod.get("dob");

    ResponseEntity<HcertServerResponse> healthCertificateContent = decovid19Service.getHealthCertificateContent(
        mockMultipartFile);

    HttpStatus statusCode = healthCertificateContent.getStatusCode();
    String hcertPrefix = healthCertificateContent.getBody().getHcertPrefix();
    String hcertKID = healthCertificateContent.getBody().getHcertKID();
    String hcertAlgo = healthCertificateContent.getBody().getHcertAlgo();
    HcertContentDTO hcertContent = (HcertContentDTO) healthCertificateContent.getBody().getHcertContent();
    String hcertIssuer = healthCertificateContent.getBody().getHcertIssuer();
    HcertTimeStampDTO hcertTimeStamp = healthCertificateContent.getBody().getHcertTimeStamp();

    assertEquals(HttpStatus.OK, statusCode);
    assertEquals(expectedHcertPrefix, hcertPrefix);
    assertEquals(SWISS_QR_CODE_VACC_KID, hcertKID);
    assertEquals(HcertAlgoKeys.RSA_PSS_256.toString(), hcertAlgo);
    assertEquals(expectedVersion, hcertContent.getVer());
    assertEquals(expectedDateOfBirth, hcertContent.getDob());
    assertFalse(hcertContent.getR().isEmpty());
    assertEquals(SWISS_QR_CODE_ISSUER, hcertIssuer);
    assertTrue(hcertTimeStamp.getHcerExpirationTime().contains("2022-05-29"));
    assertTrue(hcertTimeStamp.getHcertIssuedAtTime().contains("2021-05-29"));
  }

  @Test
  void shouldReturnHealthCertificateResponseFromHC1Prefix() {
    HcertServerRequest hcertServerRequest = new HcertServerRequest();
    hcertServerRequest.setHcertPrefix(SWISS_QR_CODE_VACC_HC1_PREFIX);
    ResponseEntity<HcertServerResponse> healthCertificateContent = decovid19Service.getHealthCertificateContent(
        hcertServerRequest);

    HttpStatus statusCode = healthCertificateContent.getStatusCode();

    assertEquals(HttpStatus.OK, statusCode);
  }

  @Test
  void shouldReturnBadRequestIfFileIsNotAllowed() throws IOException {
    InputStream testVaccImageInputStream = Files.newInputStream(SWISS_QR_CODE_VACC_CERT_IMG_PATH);
    MockMultipartFile mockMultipartFile = new MockMultipartFile(NAME, FILE_NAME_PNG_EXT_NOT_ALLOWED,
        MediaType.MULTIPART_FORM_DATA_VALUE, testVaccImageInputStream);
    testVaccImageInputStream.close();

    ResponseEntity<HcertServerResponse> healthCertificateContent = decovid19Service.getHealthCertificateContent(
        mockMultipartFile);

    HttpStatus statusCode = healthCertificateContent.getStatusCode();

    assertEquals(HttpStatus.BAD_REQUEST, statusCode);
  }

  @Test
  void shouldReturnBadRequestIfWrongHC1Prefix() {
    HcertServerRequest hcertServerRequest = new HcertServerRequest();
    hcertServerRequest.setHcertPrefix(SWISS_QR_CODE_VACC_HC1_PREFIX_WRONG);
    ResponseEntity<HcertServerResponse> healthCertificateContent = decovid19Service.getHealthCertificateContent(
        hcertServerRequest);

    HttpStatus statusCode = healthCertificateContent.getStatusCode();

    assertEquals(HttpStatus.BAD_REQUEST, statusCode);
  }

  @Test
  void shouldThrowImageNotValidException() throws IOException {
    InputStream testVaccImageInputStream = Files.newInputStream(SWISS_QR_CODE_VACC_CERT_IMG_PATH);
    CustomMockMultipartFile customMockMultipartFile = new CustomMockMultipartFile(NAME, FILE_NAME_PNG_EXT_ALLOWED,
        MediaType.MULTIPART_FORM_DATA_VALUE, testVaccImageInputStream);
    testVaccImageInputStream.close();

    Exception exception = assertThrows(ImageNotValidException.class, () -> {
      decovid19Service.getHealthCertificateContent(customMockMultipartFile);
    });

    String actualMessage = exception.getMessage();

    assertEquals(QR_CODE_CORRUPTED_EXCEPTION, actualMessage);
  }

  private static class CustomMockMultipartFile extends MockMultipartFile {

    public CustomMockMultipartFile(String name, String originalFilename, String contentType, InputStream content)
        throws IOException {
      super(name, originalFilename, contentType, content);
    }

    @Override
    @Nonnull
    public InputStream getInputStream() throws IOException {
      throw new IOException();
    }
  }

  private JSONObject getJsonObjectFromResources(Path path) throws ParseException, IOException {
    JSONParser jsonParser = new JSONParser();
    Object object = jsonParser.parse(Files.readString(path));
    return (JSONObject) object;
  }

}
