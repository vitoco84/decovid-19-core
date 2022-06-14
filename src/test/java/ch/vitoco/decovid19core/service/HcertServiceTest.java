package ch.vitoco.decovid19core.service;

import static ch.vitoco.decovid19core.constants.ExceptionMessages.QR_CODE_CORRUPTED_EXCEPTION;
import static org.junit.jupiter.api.Assertions.*;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import ch.vitoco.decovid19core.enums.HcertAlgoKeys;
import ch.vitoco.decovid19core.exception.ServerException;
import ch.vitoco.decovid19core.model.hcert.HcertContentDTO;
import ch.vitoco.decovid19core.model.hcert.HcertPublicKeyDTO;
import ch.vitoco.decovid19core.model.hcert.HcertTimeStampDTO;
import ch.vitoco.decovid19core.server.HcertServerRequest;
import ch.vitoco.decovid19core.server.HcertServerResponse;
import ch.vitoco.decovid19core.server.PEMCertServerRequest;
import ch.vitoco.decovid19core.server.PEMCertServerResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

class HcertServiceTest {

  private static final String NAME = "foo";
  private static final String FILE_NAME_PNG_EXT_ALLOWED = "TestFile.png";
  private static final String FILE_NAME_PNG_EXT_NOT_ALLOWED = "TestFile.gif";

  private static final Path SWISS_QR_CODE_VACC_CERT_IMG_PATH = Paths.get(
      "src/test/resources/swissQRCodeVaccinationCertificate.png");
  private static final Path SWISS_QR_CODE_VACC_CERT_JSON_PATH = Paths.get(
      "src/test/resources/swissQRCodeVaccinationCertificate.json");

  private static final Path SWISS_QR_CODE_TEST_CERT_IMG_PATH = Paths.get(
      "src/test/resources/swissQRCodeTestCertificate.png");
  private static final Path SWISS_QR_CODE_TEST_CERT_JSON_PATH = Paths.get(
      "src/test/resources/swissQRCodeTestCertificate.json");

  private static final Path SWISS_QR_CODE_RECOVERY_CERT_IMG_PATH = Paths.get(
      "src/test/resources/swissQRCodeRecoveryCertificate.png");
  private static final Path SWISS_QR_CODE_RECOVERY_CERT_JSON_PATH = Paths.get(
      "src/test/resources/swissQRCodeRecoveryCertificate.json");

  private static final String SWISS_QR_CODE_VACC_HC1_PREFIX = "HC1:NCFS605G0/3WUWGSLKH47GO0KNJ9DSWQIIWT9CK4600XKY-CE59-G80:84F35RIV R2F3FMMTTBY50.FK6ZK7:EDOLOPCO8F6%E3.DA%EOPC1G72A6YM83G7NA7H:6JM8D%6I:61S8ZW6HL6C460S8VF6VX6UPC0JCZ69FVCPD0LVC6JD846Y96A466W5B56+EDG8F3I80/D6$CBECSUER:C2$NS346$C2%E9VC- CSUE145GB8JA5B$D% D3IA4W5646946%96X47.JCP9EJY8L/5M/5546.96D463KC.SC4KCD3DX47B46IL6646H*6Z/E5JD%96IA74R6646407GVC*JC1A6/Q63W5KF6746TPCBEC7ZKW.CU2DNXO VD5$C JC3/DMP8$ILZEDZ CW.C9WE.Y9AY8+S9VIAI3D8WEVM8:S9C+9$PC5$CUZCY$5Y$5FBBM00T%LTAT1MOQYR8GUN$K15LIGG2P27%A46BT52VUTL.1*B89Y5B428HRSR3I/E5DS/8NBY4H2BCN8NP1D4B:0K9UQQ67BLTH21AF0V8G52R 62+5BQYCV03SO79O6K+8UXL$T4$%RT150DUHZK+Q9TIE+IMQU4E/Q4T303TKWNXTSORE.4WNPCJX66NN-2F9IHTYLR6IR UAB98RR1A0P9DL0CS5KZ*HEGT1%TQWELFQHG5/JO9TI:.T1JQF.K7 EJ 2/CI5GASQP7ULRX4-07%9W2139E2HMGW99Q DQJADB3UAJKUCOVLG+9T+J:15.12U+OBMCJ1KZ+C+87I8I9JGA0T%U2CMFHI5U:L400C.CC/K3KJZ3OM/D59TBL5AZFMPIW4";

  private static final String SWISS_QR_CODE_CERTIFICATE = "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";
  private static final String SWISS_QR_CODE_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4uZO4/7tneZ3XD5OAiTyoANOohQZC+DzZ4YC0AoLnEO+Z3PcTialCuRKS1zHfujNPI0GGG09DRVVXdv+tcKNXFDt/nRU1zlWDGFf4/63l5RIjkWFD3JFKqR8IlcJjrYYxstuZs3May3SGQJ+kZaeH5GFZMRvE0waHqMxbfwakvjf8qyBXCrZ1WsK+XJf7iYbJS2dO1a5HnegxPuRA7Zz8ikO7QRzmSongqOlkejEaIkFjx7gLGTUsOrBPYa5sdZqinDwmnjtKi52HLWarMXs+t1MN4etIp7GE7/zarjBNxk1Efiiwl+RdcwJ2uVwfrgzxfv3/TekZF8IUykV2Geu3QIDAQAB";
  private static final String GERMAN_QR_CODE_CERTIFICATE = "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";
  private static final String GERMAN_QR_CODE_PUBLIC_KEY = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE8j47QegkiZmEmzSvBpKiSNR/W+ymw5N3nsuur/22f9pLns9dzXz2nIFNVW1vE2S2zg5KCHQF0V+aX+mARNCa9A==";

  private static final String SWISS_QR_CODE_VACC_HC1_PREFIX_WRONG = "foobar";
  private static final String SWISS_QR_CODE_VACC_KID = "mmrfzpMU6xc=";
  private static final String SWISS_QR_CODE_ISSUER = "CH BAG";

  private static final String PREFIX = "PREFIX";
  private static final String JSON = "JSON";
  private static final String VER = "ver";
  private static final String DOB = "dob";
  private static final String EXPECTED_EXPIRATION_TIME = "2022-05-29";
  private static final String EXPECTED_ISSUED_AT_TIME = "2021-05-29";

  private final ValueSetService valueSetService = new ValueSetService();
  private final HcertDecodingService hcertDecodingService = new HcertDecodingService();
  private final TrustListService trustListService = new TrustListService();
  private final HcertService hcertService = new HcertService(valueSetService, hcertDecodingService, trustListService);

  @Test
  void shouldReturnVaccHealthCertificateResponseFromImageFile() throws IOException, ParseException {
    InputStream testVaccImageInputStream = Files.newInputStream(SWISS_QR_CODE_VACC_CERT_IMG_PATH);
    MockMultipartFile mockMultipartFile = new MockMultipartFile(NAME, FILE_NAME_PNG_EXT_ALLOWED,
        MediaType.MULTIPART_FORM_DATA_VALUE, testVaccImageInputStream);
    testVaccImageInputStream.close();

    JSONObject jsonObject = getJsonObjectFromResources(SWISS_QR_CODE_VACC_CERT_JSON_PATH);
    String expectedHcertPrefix = (String) jsonObject.get(PREFIX);

    JSONObject jsonHcertPaylod = (JSONObject) jsonObject.get(JSON);
    String expectedVersion = (String) jsonHcertPaylod.get(VER);
    String expectedDateOfBirth = (String) jsonHcertPaylod.get(DOB);

    ResponseEntity<HcertServerResponse> healthCertificateContent = hcertService.decodeHealthCertificateContent(
        mockMultipartFile);

    HttpStatus statusCode = healthCertificateContent.getStatusCode();
    String hcertPrefix = Objects.requireNonNull(healthCertificateContent.getBody()).getHcertPrefix();
    String hcertKID = healthCertificateContent.getBody().getHcertKID();
    String hcertAlgo = healthCertificateContent.getBody().getHcertAlgo();
    HcertContentDTO hcertContent = (HcertContentDTO) healthCertificateContent.getBody().getHcertContent();
    String hcertIssuer = healthCertificateContent.getBody().getHcertIssuer();
    HcertTimeStampDTO hcertTimeStamp = healthCertificateContent.getBody().getHcertTimeStamp();

    assertEquals(HttpStatus.OK, statusCode);
    assertEquals(expectedHcertPrefix, hcertPrefix);
    assertEquals(SWISS_QR_CODE_VACC_KID, hcertKID);
    assertEquals(HcertAlgoKeys.PS256.toString(), hcertAlgo);
    assertEquals(expectedVersion, hcertContent.getVersion());
    assertEquals(expectedDateOfBirth, hcertContent.getDateOfBirth());
    assertFalse(hcertContent.getVaccination().isEmpty());
    assertEquals(SWISS_QR_CODE_ISSUER, hcertIssuer);
    assertTrue(hcertTimeStamp.getHcertExpirationTime().contains(EXPECTED_EXPIRATION_TIME));
    assertTrue(hcertTimeStamp.getHcertIssuedAtTime().contains(EXPECTED_ISSUED_AT_TIME));
  }

  @Test
  void shouldReturnTestHealthCertificateResponseFromImageFile() throws IOException, ParseException {
    InputStream testTestImageInputStream = Files.newInputStream(SWISS_QR_CODE_TEST_CERT_IMG_PATH);
    MockMultipartFile mockMultipartFile = new MockMultipartFile(NAME, FILE_NAME_PNG_EXT_ALLOWED,
        MediaType.MULTIPART_FORM_DATA_VALUE, testTestImageInputStream);
    testTestImageInputStream.close();

    JSONObject jsonObject = getJsonObjectFromResources(SWISS_QR_CODE_TEST_CERT_JSON_PATH);
    String expectedHcertPrefix = (String) jsonObject.get(PREFIX);

    JSONObject jsonHcertPaylod = (JSONObject) jsonObject.get(JSON);
    String expectedVersion = (String) jsonHcertPaylod.get(VER);
    String expectedDateOfBirth = (String) jsonHcertPaylod.get(DOB);

    ResponseEntity<HcertServerResponse> healthCertificateContent = hcertService.decodeHealthCertificateContent(
        mockMultipartFile);

    HttpStatus statusCode = healthCertificateContent.getStatusCode();
    String hcertPrefix = Objects.requireNonNull(healthCertificateContent.getBody()).getHcertPrefix();
    String hcertKID = healthCertificateContent.getBody().getHcertKID();
    String hcertAlgo = healthCertificateContent.getBody().getHcertAlgo();
    HcertContentDTO hcertContent = (HcertContentDTO) healthCertificateContent.getBody().getHcertContent();
    String hcertIssuer = healthCertificateContent.getBody().getHcertIssuer();
    HcertTimeStampDTO hcertTimeStamp = healthCertificateContent.getBody().getHcertTimeStamp();

    assertEquals(HttpStatus.OK, statusCode);
    assertEquals(expectedHcertPrefix, hcertPrefix);
    assertEquals(SWISS_QR_CODE_VACC_KID, hcertKID);
    assertEquals(HcertAlgoKeys.PS256.toString(), hcertAlgo);
    assertEquals(expectedVersion, hcertContent.getVersion());
    assertEquals(expectedDateOfBirth, hcertContent.getDateOfBirth());
    assertFalse(hcertContent.getTest().isEmpty());
    assertEquals(SWISS_QR_CODE_ISSUER, hcertIssuer);
    assertTrue(hcertTimeStamp.getHcertExpirationTime().contains(EXPECTED_EXPIRATION_TIME));
    assertTrue(hcertTimeStamp.getHcertIssuedAtTime().contains(EXPECTED_ISSUED_AT_TIME));
  }

  @Test
  void shouldReturnRecoveryHealthCertificateResponseFromImageFile() throws IOException, ParseException {
    InputStream testTestImageInputStream = Files.newInputStream(SWISS_QR_CODE_RECOVERY_CERT_IMG_PATH);
    MockMultipartFile mockMultipartFile = new MockMultipartFile(NAME, FILE_NAME_PNG_EXT_ALLOWED,
        MediaType.MULTIPART_FORM_DATA_VALUE, testTestImageInputStream);
    testTestImageInputStream.close();

    JSONObject jsonObject = getJsonObjectFromResources(SWISS_QR_CODE_RECOVERY_CERT_JSON_PATH);
    String expectedHcertPrefix = (String) jsonObject.get(PREFIX);

    JSONObject jsonHcertPaylod = (JSONObject) jsonObject.get(JSON);
    String expectedVersion = (String) jsonHcertPaylod.get(VER);
    String expectedDateOfBirth = (String) jsonHcertPaylod.get(DOB);

    ResponseEntity<HcertServerResponse> healthCertificateContent = hcertService.decodeHealthCertificateContent(
        mockMultipartFile);

    HttpStatus statusCode = healthCertificateContent.getStatusCode();
    String hcertPrefix = Objects.requireNonNull(healthCertificateContent.getBody()).getHcertPrefix();
    String hcertKID = healthCertificateContent.getBody().getHcertKID();
    String hcertAlgo = healthCertificateContent.getBody().getHcertAlgo();
    HcertContentDTO hcertContent = (HcertContentDTO) healthCertificateContent.getBody().getHcertContent();
    String hcertIssuer = healthCertificateContent.getBody().getHcertIssuer();
    HcertTimeStampDTO hcertTimeStamp = healthCertificateContent.getBody().getHcertTimeStamp();

    assertEquals(HttpStatus.OK, statusCode);
    assertEquals(expectedHcertPrefix, hcertPrefix);
    assertEquals(SWISS_QR_CODE_VACC_KID, hcertKID);
    assertEquals(HcertAlgoKeys.PS256.toString(), hcertAlgo);
    assertEquals(expectedVersion, hcertContent.getVersion());
    assertEquals(expectedDateOfBirth, hcertContent.getDateOfBirth());
    assertFalse(hcertContent.getRecovery().isEmpty());
    assertEquals(SWISS_QR_CODE_ISSUER, hcertIssuer);
    assertTrue(hcertTimeStamp.getHcertExpirationTime().contains(EXPECTED_EXPIRATION_TIME));
    assertTrue(hcertTimeStamp.getHcertIssuedAtTime().contains(EXPECTED_ISSUED_AT_TIME));
  }

  @Test
  void shouldReturnHealthCertificateResponseFromHC1Prefix() {
    HcertServerRequest hcertServerRequest = new HcertServerRequest();
    hcertServerRequest.setHcertPrefix(SWISS_QR_CODE_VACC_HC1_PREFIX);
    ResponseEntity<HcertServerResponse> healthCertificateContent = hcertService.decodeHealthCertificateContent(
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

    ResponseEntity<HcertServerResponse> healthCertificateContent = hcertService.decodeHealthCertificateContent(
        mockMultipartFile);

    HttpStatus statusCode = healthCertificateContent.getStatusCode();

    assertEquals(HttpStatus.BAD_REQUEST, statusCode);
  }

  @Test
  void shouldReturnBadRequestIfWrongHC1Prefix() {
    HcertServerRequest hcertServerRequest = new HcertServerRequest();
    hcertServerRequest.setHcertPrefix(SWISS_QR_CODE_VACC_HC1_PREFIX_WRONG);
    ResponseEntity<HcertServerResponse> healthCertificateContent = hcertService.decodeHealthCertificateContent(
        hcertServerRequest);

    HttpStatus statusCode = healthCertificateContent.getStatusCode();

    assertEquals(HttpStatus.BAD_REQUEST, statusCode);
  }

  @Test
  void shouldThrowServerException() throws IOException {
    InputStream testVaccImageInputStream = Files.newInputStream(SWISS_QR_CODE_VACC_CERT_IMG_PATH);
    CustomMockMultipartFile customMockMultipartFile = new CustomMockMultipartFile(NAME, FILE_NAME_PNG_EXT_ALLOWED,
        MediaType.MULTIPART_FORM_DATA_VALUE, testVaccImageInputStream);
    testVaccImageInputStream.close();

    Exception exception = assertThrows(ServerException.class, () -> {
      hcertService.decodeHealthCertificateContent(customMockMultipartFile);
    });

    String actualMessage = exception.getMessage();

    assertEquals(QR_CODE_CORRUPTED_EXCEPTION, actualMessage);
  }

  @Test
  void shouldReturnPEMCertServerResponseForRSA() {
    PEMCertServerRequest pemCertServerRequest = new PEMCertServerRequest();
    pemCertServerRequest.setPemCertificate(SWISS_QR_CODE_CERTIFICATE);
    ResponseEntity<PEMCertServerResponse> x509Certificate = hcertService.decodeX509Certificate(pemCertServerRequest);

    PEMCertServerResponse pemCertServerResponse = x509Certificate.getBody();
    HcertPublicKeyDTO hcertPublicKey = pemCertServerResponse.getPublicKeyParams();

    assertEquals(HttpStatus.OK, x509Certificate.getStatusCode());
    assertEquals(SWISS_QR_CODE_PUBLIC_KEY, pemCertServerResponse.getPublicKey());
    assertEquals(
        "CN=BAG Covid-19 Signer A TEST, OU=Covid-19-Zertifikat, OU=GE-0220-BAG, OU=, O=Bundesamt für Gesundheit (BAG), OID.2.5.4.97=NTRCH-CHE-467.023.568, OID.2.5.4.15=Government Entity, L=Köniz, ST=BE, C=CH",
        pemCertServerResponse.getSubject());
    assertEquals("SHA256withRSA", pemCertServerResponse.getSignatureAlgorithm());
    assertEquals("2024-05-04T14:15:51Z", pemCertServerResponse.getValidTo());
    assertEquals("2021-05-04T14:15:51Z", pemCertServerResponse.getValidFrom());
    assertEquals("2e46d100e4e5d8d4489f3bca20ba66dc", pemCertServerResponse.getSerialNumber());
    assertEquals(
        "CN=Swiss Government aRegulated CA 02, OU=Swiss Government PKI, O=Bundesamt fuer Informatik und Telekommunikation (BIT), OID.2.5.4.97=VATCH-CHE-221.032.573, C=CH",
        pemCertServerResponse.getIssuer());
    assertEquals("RSA", hcertPublicKey.getAlgo());
    assertEquals("10001", hcertPublicKey.getPublicExponent());
    assertEquals("2048", hcertPublicKey.getBitLength());
    assertEquals(
        "e2e64ee3feed9de6775c3e4e0224f2a0034ea214190be0f3678602d00a0b9c43be6773dc4e26a50ae44a4b5cc77ee8cd3c8d06186d3d0d15555ddbfeb5c28d5c50edfe7454d739560c615fe3feb79794488e45850f72452aa47c2257098eb618c6cb6e66cdcc6b2dd219027e91969e1f918564c46f134c1a1ea3316dfc1a92f8dff2ac815c2ad9d56b0af9725fee261b252d9d3b56b91e77a0c4fb9103b673f2290eed0473992a2782a3a591e8c46889058f1ee02c64d4b0eac13d86b9b1d66a8a70f09a78ed2a2e761cb59aacc5ecfadd4c3787ad229ec613bff36ab8c137193511f8a2c25f9175cc09dae5707eb833c5fbf7fd37a4645f08532915d867aedd",
        hcertPublicKey.getModulus());
  }

  @Test
  void shouldReturnPEMCertServerResponseForECDSA() {
    PEMCertServerRequest pemCertServerRequest = new PEMCertServerRequest();
    pemCertServerRequest.setPemCertificate(GERMAN_QR_CODE_CERTIFICATE);
    ResponseEntity<PEMCertServerResponse> x509Certificate = hcertService.decodeX509Certificate(pemCertServerRequest);

    PEMCertServerResponse pemCertServerResponse = x509Certificate.getBody();
    HcertPublicKeyDTO hcertPublicKey = pemCertServerResponse.getPublicKeyParams();

    assertEquals(HttpStatus.OK, x509Certificate.getStatusCode());
    assertEquals(GERMAN_QR_CODE_PUBLIC_KEY, pemCertServerResponse.getPublicKey());
    assertEquals(
        "SERIALNUMBER=CSM017143437, OID.2.5.4.97=DT:DE-UGNOTPROVIDED, L=Köln, CN=Ubirch GmbH, O=Ubirch GmbH, C=DE",
        pemCertServerResponse.getSubject());
    assertEquals("RSASSA-PSS", pemCertServerResponse.getSignatureAlgorithm());
    assertEquals("2022-04-30T09:31:22Z", pemCertServerResponse.getValidTo());
    assertEquals("2021-04-27T09:31:22Z", pemCertServerResponse.getValidFrom());
    assertEquals("5e0ecd06e9c3e5e68ba4edc583d4449f", pemCertServerResponse.getSerialNumber());
    assertEquals("OID.2.5.4.97=NTRDE-HRB74346, CN=D-TRUST Test CA 2-2 2019, O=D-Trust GmbH, C=DE",
        pemCertServerResponse.getIssuer());
    assertEquals("EC", hcertPublicKey.getAlgo());
    assertEquals("f23e3b41e8248999849b34af0692a248d47f5beca6c393779ecbaeaffdb67fda", hcertPublicKey.getPublicXCoord());
    assertEquals("4b9ecf5dcd7cf69c814d556d6f1364b6ce0e4a087405d15f9a5fe98044d09af4", hcertPublicKey.getPublicYCoord());
    assertEquals("256", hcertPublicKey.getBitLength());
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
