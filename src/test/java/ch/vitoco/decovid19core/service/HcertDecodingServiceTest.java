package ch.vitoco.decovid19core.service;

import static ch.vitoco.decovid19core.constants.ExceptionMessages.QR_CODE_DECODE_EXCEPTION;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import COSE.*;
import ch.vitoco.decovid19core.enums.HcertAlgoKeys;
import ch.vitoco.decovid19core.enums.HcertCBORKeys;
import ch.vitoco.decovid19core.enums.HcertClaimKeys;
import ch.vitoco.decovid19core.exception.ServerException;
import ch.vitoco.decovid19core.model.hcert.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upokecenter.cbor.CBORObject;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

class HcertDecodingServiceTest {

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
  private static final String SIGNATURE_ALGO_SHA256_WITH_RSA = "SHA256withRSA/PSS";
  private static final String SIGNATURE_ALGO_SHA256_WITH_ECDSA = "SHA256withECDSA";


  private final HcertDecodingService hcertDecodingService = new HcertDecodingService();

  @Test
  void shouldReturnHealthCertificatePrefixContent() throws IOException, ParseException {
    InputStream testVaccImageInputStream = Files.newInputStream(SWISS_QR_CODE_VACC_CERT_IMG_PATH);
    String actualHealthCertificateContent = hcertDecodingService.getHealthCertificateContent(testVaccImageInputStream);
    testVaccImageInputStream.close();

    JSONObject jsonObject = getJsonObjectFromResources(SWISS_QR_CODE_VACC_CERT_JSON_PATH);
    String expectedHealthCertificateContent = (String) jsonObject.get("PREFIX");

    assertTrue(actualHealthCertificateContent.startsWith("HC1:"));
    assertEquals(expectedHealthCertificateContent, actualHealthCertificateContent);
  }

  @Test
  void shouldReturnHealthCertificateVaccinationContent() throws IOException, ParseException {
    InputStream testVaccImageInputStream = Files.newInputStream(SWISS_QR_CODE_VACC_CERT_IMG_PATH);
    String hcert = hcertDecodingService.getHealthCertificateContent(testVaccImageInputStream);
    testVaccImageInputStream.close();

    CBORObject cborObject = hcertDecodingService.getCBORObject(hcert);
    String cborPayload = hcertDecodingService.getContent(cborObject);

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
    String hcert = hcertDecodingService.getHealthCertificateContent(testTestImageInputStream);
    testTestImageInputStream.close();

    CBORObject cborObject = hcertDecodingService.getCBORObject(hcert);
    String cborPayload = hcertDecodingService.getContent(cborObject);

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
    String hcert = hcertDecodingService.getHealthCertificateContent(testRecoveryImageInputStream);
    testRecoveryImageInputStream.close();

    CBORObject cborObject = hcertDecodingService.getCBORObject(hcert);
    String cborPayload = hcertDecodingService.getContent(cborObject);

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
  void shouldServerExceptionException() throws IOException {
    InputStream testImageInputStream = Files.newInputStream(FREE_TEST_IMAGE);

    Exception exception = assertThrows(ServerException.class, () -> {
      hcertDecodingService.getHealthCertificateContent(testImageInputStream);
    });

    testImageInputStream.close();
    String actualMessage = exception.getMessage();

    assertEquals(QR_CODE_DECODE_EXCEPTION, actualMessage);
  }

  @Test
  void shouldReturnCorrectAlgo() throws IOException {
    InputStream testVaccImageInputStream = Files.newInputStream(SWISS_QR_CODE_VACC_CERT_IMG_PATH);
    String hcert = hcertDecodingService.getHealthCertificateContent(testVaccImageInputStream);
    testVaccImageInputStream.close();

    CBORObject cborObject = hcertDecodingService.getCBORObject(hcert);
    String actualAlgo = hcertDecodingService.getAlgo(cborObject);

    assertEquals(HcertAlgoKeys.PS256.toString(), actualAlgo);
  }

  @Test
  void shoutReturnKIDFromUnprotectedHeader() throws CoseException, JsonProcessingException, DecoderException {
    String json = generateHcertContentDTO();
    CBORObject cborObject = generateCBORObject(json);
    Sign1Message sign1Message = generateCOSESignature(cborObject);

    CBORObject map = CBORObject.NewMap();
    map.set(CBORObject.FromObject(4), CBORObject.FromObject(sign1Message.getProtectedAttributes().EncodeToBytes()));

    CBORObject cborObjectUnprotected = CBORObject.NewArray();
    cborObjectUnprotected.Add(sign1Message.getUnprotectedAttributes().EncodeToBytes());
    cborObjectUnprotected.Add(map);
    cborObjectUnprotected.Add(new byte[0]);
    cborObjectUnprotected.Add(new byte[0]);

    CBORObject unprotectedHeader = cborObjectUnprotected.get(HcertCBORKeys.UNPROTECTED_HEADER.getCborKey());
    String expectedKID = getActualKID(unprotectedHeader);
    String actualKID = hcertDecodingService.getKID(cborObjectUnprotected);

    assertEquals(expectedKID, actualKID);
  }

  @Test
  void shouldReturnCorrectJcaAlgo() {
    String es256 = hcertDecodingService.getJcaAlgo(HcertAlgoKeys.ES256.getName());
    String ps256 = hcertDecodingService.getJcaAlgo(HcertAlgoKeys.PS256.getName());

    assertEquals(SIGNATURE_ALGO_SHA256_WITH_ECDSA, es256);
    assertEquals(SIGNATURE_ALGO_SHA256_WITH_RSA, ps256);
  }

  @Test
  void shouldReturnCorrectKID() throws IOException {
    InputStream testVaccImageInputStream = Files.newInputStream(SWISS_QR_CODE_VACC_CERT_IMG_PATH);
    String hcert = hcertDecodingService.getHealthCertificateContent(testVaccImageInputStream);
    testVaccImageInputStream.close();

    CBORObject cborObject = hcertDecodingService.getCBORObject(hcert);
    String actualKID = hcertDecodingService.getKID(cborObject);

    assertEquals(SWISS_QR_CODE_VACC_KID, actualKID);
  }

  private JSONObject getJsonObjectFromResources(Path path) throws ParseException, IOException {
    JSONParser jsonParser = new JSONParser();
    Object object = jsonParser.parse(Files.readString(path));
    return (JSONObject) object;
  }

  private Sign1Message generateCOSESignature(CBORObject cbor) throws CoseException {
    OneKey privateKey = OneKey.generateKey(AlgorithmID.ECDSA_256);
    byte[] kid = UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8);
    Sign1Message signature = new Sign1Message();
    signature.addAttribute(HeaderKeys.Algorithm, privateKey.get(KeyKeys.Algorithm), Attribute.UNPROTECTED);
    signature.addAttribute(HeaderKeys.KID, CBORObject.FromObject(kid), Attribute.PROTECTED);
    signature.SetContent(cbor.EncodeToBytes());
    signature.sign(privateKey);
    return signature;
  }

  private CBORObject generateCBORObject(String json) {
    CBORObject cborObject = CBORObject.NewMap();
    CBORObject hcertVersion = CBORObject.NewMap();
    CBORObject hcert = CBORObject.FromJSONString(json);
    hcertVersion.set(CBORObject.FromObject(HcertClaimKeys.HCERT_VERSION_CLAIM_KEY.getClaimKey()), hcert);
    cborObject.set(CBORObject.FromObject(HcertClaimKeys.HCERT_CLAIM_KEY.getClaimKey()), hcertVersion);
    return cborObject;
  }

  private String getActualKID(CBORObject cborObject) throws DecoderException {
    StringBuilder kid = new StringBuilder();
    String kidHexTrimmed = cborObject.toString().substring(6, cborObject.toString().lastIndexOf("'"));
    byte[] kidBytes = Hex.decodeHex(kidHexTrimmed.toCharArray());
    kid.append(Base64.encodeBase64String(kidBytes));
    return kid.toString();
  }

  private String generateHcertContentDTO() throws JsonProcessingException {
    HcertHolder hcertHolder = new HcertHolder();
    hcertHolder.setFn("Uncle");
    hcertHolder.setGn("Bob");
    hcertHolder.setFnt("UNCLE");
    hcertHolder.setGnt("BOB");

    HcertTest hcertTest = new HcertTest();
    hcertTest.setTg("COVID-19");
    hcertTest.setTt("Test");
    hcertTest.setNm("Test Name");
    hcertTest.setMa("Test Identifier");
    hcertTest.setSc("2021-04-30");
    hcertTest.setTr("Not Detected");
    hcertTest.setTc("Testing Centre");
    hcertTest.setCo("Switzerland");
    hcertTest.setIs("BAG");

    HcertContentDTO hcertContentDTO = new HcertContentDTO();
    hcertContentDTO.setDob("1943-02-01");
    hcertContentDTO.setVer("1.0.0");
    hcertContentDTO.setNam(hcertHolder);
    hcertContentDTO.setT(List.of(hcertTest));

    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(hcertContentDTO);
  }

}
