package ch.vitoco.decovid19core.service;

import static ch.vitoco.decovid19core.constants.Const.QR_CODE_DECODE_EXCEPTION;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import ch.vitoco.decovid19core.enums.HcertAlgoKeys;
import ch.vitoco.decovid19core.exception.ImageDecodeException;
import ch.vitoco.decovid19core.exception.MessageDecodeException;
import ch.vitoco.decovid19core.model.HcertContentDTO;
import ch.vitoco.decovid19core.model.HcertRecovery;
import ch.vitoco.decovid19core.model.HcertTest;
import ch.vitoco.decovid19core.model.HcertVaccination;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upokecenter.cbor.CBORObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

class Decovid19HcertServiceTest {

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

  private static final Path FREE_TEST_IMAGE = Paths.get("src/test/resources/freeTestImageFromUnsplash.jpg");
  private static final String SWISS_QR_CODE_VACC_KID = "mmrfzpMU6xc=";

  private final Decovid19HcertService decovid19HcertService = new Decovid19HcertService();

  @Test
  void shouldReturnHealthCertificatePrefixContent() throws IOException, ParseException {
    InputStream testVaccImageInputStream = Files.newInputStream(SWISS_QR_CODE_VACC_CERT_IMG_PATH);
    String actualHealthCertificateContent = decovid19HcertService.getHealthCertificateContent(testVaccImageInputStream);
    testVaccImageInputStream.close();

    JSONObject jsonObject = getJsonObjectFromResources(SWISS_QR_CODE_VACC_CERT_JSON_PATH);
    String expectedHealthCertificateContent = (String) jsonObject.get("PREFIX");

    assertTrue(actualHealthCertificateContent.startsWith("HC1:"));
    assertEquals(expectedHealthCertificateContent, actualHealthCertificateContent);
  }

  @Test
  void shouldReturnHealthCertificateVaccinationContent() throws IOException, ParseException {
    InputStream testVaccImageInputStream = Files.newInputStream(SWISS_QR_CODE_VACC_CERT_IMG_PATH);
    String hcert = decovid19HcertService.getHealthCertificateContent(testVaccImageInputStream);
    testVaccImageInputStream.close();

    CBORObject cborObject = decovid19HcertService.getCBORObject(hcert);
    String cborPayload = decovid19HcertService.getContent(cborObject);

    JSONObject jsonObject = getJsonObjectFromResources(SWISS_QR_CODE_VACC_CERT_JSON_PATH);
    JSONObject jsonHcertPaylod = (JSONObject) jsonObject.get("JSON");

    String expectedVersion = (String) jsonHcertPaylod.get("ver");
    String expectedDateOfBirth = (String) jsonHcertPaylod.get("dob");

    JSONArray expectedVaccineInformations = (JSONArray) jsonHcertPaylod.get("v");
    JSONObject expectedVaccinationInformation = (JSONObject) expectedVaccineInformations.get(0);

    JSONObject expectedName = (JSONObject) jsonHcertPaylod.get("nam");
    String expectedLastName = (String) expectedName.get("fn");
    String expectedFirstName = (String) expectedName.get("gn");
    String expectedStandardLastName = (String) expectedName.get("fnt");
    String expectedStandardFirstName = (String) expectedName.get("gnt");

    String expectedDiseaseTarget = (String) expectedVaccinationInformation.get("tg");
    String expectedVaccineType = (String) expectedVaccinationInformation.get("vp");
    String expectedMedicinalProduct = (String) expectedVaccinationInformation.get("mp");
    String expectedAuthorizationHolder = (String) expectedVaccinationInformation.get("ma");
    Long expectedSequenceNumberOfDoses = (Long) expectedVaccinationInformation.get("dn");
    Long expectedTotalNumberOfDoses = (Long) expectedVaccinationInformation.get("sd");
    String expectedDateOfVaccination = (String) expectedVaccinationInformation.get("dt");
    String expectedCountryOfOrigin = (String) expectedVaccinationInformation.get("co");
    String expectedIssuer = (String) expectedVaccinationInformation.get("is");
    String expectedUniqueVaccCertificateIdentifier = (String) expectedVaccinationInformation.get("ci");

    ObjectMapper objectMapper = new ObjectMapper();
    HcertContentDTO hcertContentDTO = objectMapper.readValue(cborPayload, HcertContentDTO.class);
    HcertVaccination hcertVaccination = hcertContentDTO.getV().get(0);

    assertEquals(expectedLastName, hcertContentDTO.getNam().getFn());
    assertEquals(expectedFirstName, hcertContentDTO.getNam().getGn());
    assertEquals(expectedStandardLastName, hcertContentDTO.getNam().getFnt());
    assertEquals(expectedStandardFirstName, hcertContentDTO.getNam().getGnt());
    assertEquals(expectedVersion, hcertContentDTO.getVer());
    assertEquals(expectedDateOfBirth, hcertContentDTO.getDob());
    assertEquals(expectedDiseaseTarget, hcertVaccination.getTg());
    assertEquals(expectedVaccineType, hcertVaccination.getVp());
    assertEquals(expectedMedicinalProduct, hcertVaccination.getMp());
    assertEquals(expectedAuthorizationHolder, hcertVaccination.getMa());
    assertEquals(expectedSequenceNumberOfDoses, hcertVaccination.getDn());
    assertEquals(expectedTotalNumberOfDoses, hcertVaccination.getSd());
    assertEquals(expectedDateOfVaccination, hcertVaccination.getDt());
    assertEquals(expectedCountryOfOrigin, hcertVaccination.getCo());
    assertEquals(expectedIssuer, hcertVaccination.getIs());
    assertEquals(expectedUniqueVaccCertificateIdentifier, hcertVaccination.getCi());
  }

  @Test
  void shouldReturnHealthCertificateTestContent() throws IOException, ParseException {
    InputStream testTestImageInputStream = Files.newInputStream(SWISS_QR_CODE_TEST_CERT_IMG_PATH);
    String hcert = decovid19HcertService.getHealthCertificateContent(testTestImageInputStream);
    testTestImageInputStream.close();

    CBORObject cborObject = decovid19HcertService.getCBORObject(hcert);
    String cborPayload = decovid19HcertService.getContent(cborObject);

    JSONObject jsonObject = getJsonObjectFromResources(SWISS_QR_CODE_TEST_CERT_JSON_PATH);
    JSONObject jsonHcertPaylod = (JSONObject) jsonObject.get("JSON");

    String expectedVersion = (String) jsonHcertPaylod.get("ver");
    String expectedDateOfBirth = (String) jsonHcertPaylod.get("dob");

    JSONArray expectedVaccineInformations = (JSONArray) jsonHcertPaylod.get("t");
    JSONObject expectedVaccinationInformation = (JSONObject) expectedVaccineInformations.get(0);

    JSONObject expectedName = (JSONObject) jsonHcertPaylod.get("nam");
    String expectedLastName = (String) expectedName.get("fn");
    String expectedFirstName = (String) expectedName.get("gn");
    String expectedStandardLastName = (String) expectedName.get("fnt");
    String expectedStandardFirstName = (String) expectedName.get("gnt");

    String expectedDiseaseTarget = (String) expectedVaccinationInformation.get("tg");
    String expectedTypeOfTest = (String) expectedVaccinationInformation.get("tt");
    String expectedTestName = (String) expectedVaccinationInformation.get("nm");
    String expectedTestDeviceIdentifier = (String) expectedVaccinationInformation.get("ma");
    String expectedDateAndTimeOfTest = (String) expectedVaccinationInformation.get("sc");
    String expectedResultOfTest = (String) expectedVaccinationInformation.get("tr");
    String expectedTestingCenter = (String) expectedVaccinationInformation.get("tc");
    String expectedCountryOfOrigin = (String) expectedVaccinationInformation.get("co");
    String expectedIssuer = (String) expectedVaccinationInformation.get("is");
    String expectedUniqueVaccCertificateIdentifier = (String) expectedVaccinationInformation.get("ci");

    ObjectMapper objectMapper = new ObjectMapper();
    HcertContentDTO hcertContentDTO = objectMapper.readValue(cborPayload, HcertContentDTO.class);
    HcertTest hcertTest = hcertContentDTO.getT().get(0);

    assertEquals(expectedLastName, hcertContentDTO.getNam().getFn());
    assertEquals(expectedFirstName, hcertContentDTO.getNam().getGn());
    assertEquals(expectedStandardLastName, hcertContentDTO.getNam().getFnt());
    assertEquals(expectedStandardFirstName, hcertContentDTO.getNam().getGnt());
    assertEquals(expectedVersion, hcertContentDTO.getVer());
    assertEquals(expectedDateOfBirth, hcertContentDTO.getDob());
    assertEquals(expectedDiseaseTarget, hcertTest.getTg());
    assertEquals(expectedTypeOfTest, hcertTest.getTt());
    assertEquals(expectedTestName, hcertTest.getNm());
    assertEquals(expectedTestDeviceIdentifier, hcertTest.getMa());
    assertEquals(expectedDateAndTimeOfTest, hcertTest.getSc());
    assertEquals(expectedResultOfTest, hcertTest.getTr());
    assertEquals(expectedTestingCenter, hcertTest.getTc());
    assertEquals(expectedCountryOfOrigin, hcertTest.getCo());
    assertEquals(expectedIssuer, hcertTest.getIs());
    assertEquals(expectedUniqueVaccCertificateIdentifier, hcertTest.getCi());
  }

  @Test
  void shouldReturnHealthCertificateRecoveryContent() throws IOException, ParseException {
    InputStream testRecoveryImageInputStream = Files.newInputStream(SWISS_QR_CODE_RECOVERY_CERT_IMG_PATH);
    String hcert = decovid19HcertService.getHealthCertificateContent(testRecoveryImageInputStream);
    testRecoveryImageInputStream.close();

    CBORObject cborObject = decovid19HcertService.getCBORObject(hcert);
    String cborPayload = decovid19HcertService.getContent(cborObject);

    JSONObject jsonObject = getJsonObjectFromResources(SWISS_QR_CODE_RECOVERY_CERT_JSON_PATH);
    JSONObject jsonHcertPaylod = (JSONObject) jsonObject.get("JSON");

    String expectedVersion = (String) jsonHcertPaylod.get("ver");
    String expectedDateOfBirth = (String) jsonHcertPaylod.get("dob");

    JSONArray expectedVaccineInformations = (JSONArray) jsonHcertPaylod.get("r");
    JSONObject expectedVaccinationInformation = (JSONObject) expectedVaccineInformations.get(0);

    JSONObject expectedName = (JSONObject) jsonHcertPaylod.get("nam");
    String expectedLastName = (String) expectedName.get("fn");
    String expectedFirstName = (String) expectedName.get("gn");
    String expectedStandardLastName = (String) expectedName.get("fnt");
    String expectedStandardFirstName = (String) expectedName.get("gnt");

    String expectedDiseaseTarget = (String) expectedVaccinationInformation.get("tg");
    String expectedDateOfFirstPositiveTest = (String) expectedVaccinationInformation.get("fr");
    String expectedCountryOfOrigin = (String) expectedVaccinationInformation.get("co");
    String expectedCertificateValideFrom = (String) expectedVaccinationInformation.get("df");
    String expectedCertificateValideUntil = (String) expectedVaccinationInformation.get("du");
    String expectedIssuer = (String) expectedVaccinationInformation.get("is");
    String expectedUniqueVaccCertificateIdentifier = (String) expectedVaccinationInformation.get("ci");

    ObjectMapper objectMapper = new ObjectMapper();
    HcertContentDTO hcertContentDTO = objectMapper.readValue(cborPayload, HcertContentDTO.class);
    HcertRecovery hcertRecovery = hcertContentDTO.getR().get(0);

    assertEquals(expectedLastName, hcertContentDTO.getNam().getFn());
    assertEquals(expectedFirstName, hcertContentDTO.getNam().getGn());
    assertEquals(expectedStandardLastName, hcertContentDTO.getNam().getFnt());
    assertEquals(expectedStandardFirstName, hcertContentDTO.getNam().getGnt());
    assertEquals(expectedVersion, hcertContentDTO.getVer());
    assertEquals(expectedDateOfBirth, hcertContentDTO.getDob());
    assertEquals(expectedDiseaseTarget, hcertRecovery.getTg());
    assertEquals(expectedDateOfFirstPositiveTest, hcertRecovery.getFr());
    assertEquals(expectedCountryOfOrigin, hcertRecovery.getCo());
    assertEquals(expectedCertificateValideFrom, hcertRecovery.getDf());
    assertEquals(expectedCertificateValideUntil, hcertRecovery.getDu());
    assertEquals(expectedIssuer, hcertRecovery.getIs());
    assertEquals(expectedUniqueVaccCertificateIdentifier, hcertRecovery.getCi());
  }

  @Test
  void shouldThrowImageDecodeException() throws IOException {
    InputStream testImageInputStream = Files.newInputStream(FREE_TEST_IMAGE);

    Exception exception = assertThrows(ImageDecodeException.class, () -> {
      decovid19HcertService.getHealthCertificateContent(testImageInputStream);
    });

    testImageInputStream.close();
    String actualMessage = exception.getMessage();

    assertEquals(QR_CODE_DECODE_EXCEPTION, actualMessage);
  }

  @Test
  void shouldThrowMessageDecodeException() {
    Exception exception = assertThrows(MessageDecodeException.class, () -> {
      decovid19HcertService.decodeBase45HealthCertificate("foobar");
    });

    String actualMessage = exception.getMessage();

    assertEquals(QR_CODE_DECODE_EXCEPTION, actualMessage);
  }

  @Test
  void shouldReturnCorrectAlgo() throws IOException {
    InputStream testVaccImageInputStream = Files.newInputStream(SWISS_QR_CODE_VACC_CERT_IMG_PATH);
    String hcert = decovid19HcertService.getHealthCertificateContent(testVaccImageInputStream);
    testVaccImageInputStream.close();

    CBORObject cborObject = decovid19HcertService.getCBORObject(hcert);
    String actualAlgo = decovid19HcertService.getAlgo(cborObject);

    assertEquals(HcertAlgoKeys.RSA_PSS_256.toString(), actualAlgo);
  }

  @Test
  void shouldReturnCorrectKID() throws IOException {
    InputStream testVaccImageInputStream = Files.newInputStream(SWISS_QR_CODE_VACC_CERT_IMG_PATH);
    String hcert = decovid19HcertService.getHealthCertificateContent(testVaccImageInputStream);
    testVaccImageInputStream.close();

    CBORObject cborObject = decovid19HcertService.getCBORObject(hcert);
    String actualKID = decovid19HcertService.getKID(cborObject);

    assertEquals(SWISS_QR_CODE_VACC_KID, actualKID);
  }

  private JSONObject getJsonObjectFromResources(Path path) throws ParseException, IOException {
    JSONParser jsonParser = new JSONParser();
    Object object = jsonParser.parse(Files.readString(path));
    return (JSONObject) object;
  }

}
