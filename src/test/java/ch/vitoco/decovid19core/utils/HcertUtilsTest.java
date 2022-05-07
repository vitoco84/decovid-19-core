package ch.vitoco.decovid19core.utils;


import COSE.Message;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HcertUtilsTest {

  private static final Path SWISS_QR_CODE_VACC_CERT_IMG_PATH = Paths.get("src/test/resources/swissQRCodeVaccinationCertificate.png");
  private static final Path SWISS_QR_CODE_VACC_CERT_JSON_PATH = Paths.get("src/test/resources/swissQRCodeVaccinationCertificateContent.json");

  @Test
  void shouldReturnHealthCertificateContent() throws IOException, ParseException {
    InputStream testVaccImageInputStream = Files.newInputStream(SWISS_QR_CODE_VACC_CERT_IMG_PATH);
    String actualHealthCertificateContent = HcertUtils.getHealthCertificateContent(testVaccImageInputStream);
    testVaccImageInputStream.close();

    JSONParser jsonParser = new JSONParser();
    Object object = jsonParser.parse(Files.readString(SWISS_QR_CODE_VACC_CERT_JSON_PATH));
    JSONObject jsonObject = (JSONObject) object;
    String expectedHealthCertificateContent = (String) jsonObject.get("PREFIX");

    assertTrue(actualHealthCertificateContent.startsWith("HC1:"));
    assertEquals(expectedHealthCertificateContent, actualHealthCertificateContent);
  }

  @Test
  void shouldReturnHealthCertificatePayload() throws IOException, ParseException {
    InputStream testVaccImageInputStream = Files.newInputStream(SWISS_QR_CODE_VACC_CERT_IMG_PATH);
    String hcert = HcertUtils.getHealthCertificateContent(testVaccImageInputStream);
    testVaccImageInputStream.close();
    Message hcertCose = HcertUtils.getCOSEMessageFromHcert(hcert);
    String actualHcertCbor = HcertUtils.getCBORMessage(hcertCose);

    JSONParser jsonParser = new JSONParser();
    Object object = jsonParser.parse(Files.readString(SWISS_QR_CODE_VACC_CERT_JSON_PATH));
    JSONObject jsonObject = (JSONObject) object;
    JSONObject jsonHcertPaylod = (JSONObject) jsonObject.get("JSON");

    // TODO Fixing ObjectMapper
    // ObjectMapper objectMapper = new ObjectMapper();
    // objectMapper.readValue(actualHcertCbor, HcertPayload.class);

    String expectedVersion = (String) jsonHcertPaylod.get("ver");
    String expectedDateOfBirth = (String) jsonHcertPaylod.get("dob");
    JSONArray expectedVaccineInformations = (JSONArray) jsonHcertPaylod.get("v");
    JSONObject expectedVaccinationInformation = (JSONObject) expectedVaccineInformations.get(0);
    String expectedIssuer = (String) expectedVaccinationInformation.get("is");
    String expectedCountryOfOrigin = (String) expectedVaccinationInformation.get("co");
    String expectedUniqueVaccCertificateIdentifier = (String) expectedVaccinationInformation.get("ci");

    JSONObject expectedName = (JSONObject) jsonHcertPaylod.get("nam");
    String expectedLastName = (String) expectedName.get("fn");
    String expectedFirstName = (String) expectedName.get("gn");

    assertTrue(actualHcertCbor.contains(expectedVersion));
    assertTrue(actualHcertCbor.contains(expectedDateOfBirth));
    assertTrue(actualHcertCbor.contains(expectedLastName));
    assertTrue(actualHcertCbor.contains(expectedFirstName));
    assertTrue(actualHcertCbor.contains(expectedIssuer));
    assertTrue(actualHcertCbor.contains(expectedCountryOfOrigin));
    assertTrue(actualHcertCbor.contains(expectedUniqueVaccCertificateIdentifier));
  }

}
