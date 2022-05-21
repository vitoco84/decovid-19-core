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
      "src/test/resources/swissQRCodeVaccinationCertificateContent.json");

  private static final Path SWISS_QR_CODE_TEST_CERT_IMG_PATH = Paths.get(
      "src/test/resources/swissQRCodeTestCertificate.png");
  private static final Path SWISS_QR_CODE_TEST_CERT_JSON_PATH = Paths.get(
      "src/test/resources/swissQRCodeTestCertificateContent.json");

  private static final Path SWISS_QR_CODE_RECOVERY_CERT_IMG_PATH = Paths.get(
      "src/test/resources/swissQRCodeRecoveryCertificate.png");
  private static final Path SWISS_QR_CODE_RECOVERY_CERT_JSON_PATH = Paths.get(
      "src/test/resources/swissQRCodeRecoveryCertificateContent.json");

  private static final Path FREE_TEST_IMAGE = Paths.get("src/test/resources/freeTestImageFromUnsplash.jpg");
  private static final String WRONG_HCERT_HC1_PREFIX = "HC1:NCFS605G0/3WUWGSLKH47GO0KNJ9DSWQIIWT9CK4600XKY-CE59-G80:84F35RIV R2F3FMMTTBY50.FK6ZK7:EDOLOPCO8F6%E3.DA%EOPC1G72A6YMZFO7NA7H:6JM8D%6I:61S8ZW6HL6C460S8VF6VX6UPC0JCZ69FVCPD0LVKLMD846Y96A466W5B56+EDG8F3I80/D6$CBECSUER:C2$NS346$C2%E9VC- CSUE145GB8JA5B$D% D3IA4W5646946%96X47.JCP9EJY8L/5M/5546.96D463KC.SC4KCD3DX47B46IL6646H*6Z/E5JD%96IA74R6646407GVC*JC1A6/Q63W5KF6746TPCBEC7ZKW.CU2DNXO VD5$C JC3/DMP8$ILZEDZ CW.C9WE.Y9AY8+S9VW4L3D8WEVM8:S9C+9$PC5$CUZCY$5Y$5FBBM00T%LTAT1MOQYR8GUN$K15LIGG2P27%A46BT52VUTL.1*B89Y5B428HRSR3I/E5DS/8NBY4H2BCN8NP1D4B:0K9UQQ67BLTH21AF0V8G52R 62+5BQYCV03SO79O6K+8UXL$T4$%RT150DUHZK+Q9TIE+IMQU4E/Q4T303TKWNXTSORE.4WNPCJX66NN-2F9IHTYLR6IR UAB98RR1A0P9DL0CS5KZ*HEGT1%TQWELFQHG5/JO9TI:.T1JQF.K7 EJ 2/CI5GASQP7ULRX4-07%9W2139E2HMGW99Q DQJADB3UAJKUCOVLG+9T+J:15.12U+OBMCJ1KZ+C+87I8I9JGA0T%U2CMFHI5U:L400C.CC/K3KJZ3OM/D59TBL5AZFMPIW4";
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
