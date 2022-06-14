package ch.vitoco.decovid19core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.image.BufferedImage;
import java.util.List;

import ch.vitoco.decovid19core.model.hcert.HcertContentDTO;
import ch.vitoco.decovid19core.model.hcert.HcertHolder;
import ch.vitoco.decovid19core.model.hcert.HcertTest;
import ch.vitoco.decovid19core.server.QRCodeServerRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class QRCodeGeneratorServiceTest {

  private static final String URL_VALID = "https://www.google.ch/";
  private static final String URL_INVALID = "foobar";
  private static final int IMG_WIDTH_HEIGHT = 250;

  private final QRCodeGeneratorService qrCodeGeneratorService = new QRCodeGeneratorService();

  @Test
  void shouldGenerateQRCode() {
    QRCodeServerRequest qrCodeServerRequest = new QRCodeServerRequest();
    qrCodeServerRequest.setUrl(URL_VALID);

    ResponseEntity<BufferedImage> bufferedImageResponseEntity = qrCodeGeneratorService.createURLQRCode(
        qrCodeServerRequest);
    HttpStatus statusCode = bufferedImageResponseEntity.getStatusCode();
    BufferedImage bufferedImage = bufferedImageResponseEntity.getBody();

    assertEquals(HttpStatus.OK, statusCode);
    assertEquals(IMG_WIDTH_HEIGHT, bufferedImage.getHeight());
    assertEquals(IMG_WIDTH_HEIGHT, bufferedImage.getWidth());
  }

  @Test
  void shouldReturnBadRequestForInvalidURL() {
    QRCodeServerRequest qrCodeServerRequest = new QRCodeServerRequest();
    qrCodeServerRequest.setUrl(URL_INVALID);

    ResponseEntity<BufferedImage> bufferedImageResponseEntity = qrCodeGeneratorService.createURLQRCode(
        qrCodeServerRequest);
    HttpStatus statusCode = bufferedImageResponseEntity.getStatusCode();

    assertEquals(HttpStatus.BAD_REQUEST, statusCode);
  }

  @Test
  void shouldGenerateFakeQRCodeCovid() {
    HcertHolder hcertHolder = new HcertHolder();
    hcertHolder.setSurname("Uncle");
    hcertHolder.setForename("Bob");
    hcertHolder.setStandardSurname("UNCLE");
    hcertHolder.setStandardForename("BOB");

    HcertTest hcertTest = new HcertTest();
    hcertTest.setTarget("COVID-19");
    hcertTest.setTypeOfTest("Test");
    hcertTest.setNucleicAcidAmplName("Test Name");
    hcertTest.setTestDeviceManufacturer("Test Identifier");
    hcertTest.setSampleCollectionDate("2021-04-30");
    hcertTest.setTestResult("Not Detected");
    hcertTest.setTestingCentre("Testing Centre");
    hcertTest.setCountry("Switzerland");
    hcertTest.setIssuer("BAG");

    HcertContentDTO hcertContentDTO = new HcertContentDTO();
    hcertContentDTO.setDateOfBirth("1943-02-01");
    hcertContentDTO.setVersion("1.0.0");
    hcertContentDTO.setName(hcertHolder);
    hcertContentDTO.setTest(List.of(hcertTest));

    ResponseEntity<BufferedImage> bufferedImageResponseEntity = qrCodeGeneratorService.createTestCovidQRCode(
        hcertContentDTO);

    assertEquals(HttpStatus.OK, bufferedImageResponseEntity.getStatusCode());
    assertEquals(IMG_WIDTH_HEIGHT, bufferedImageResponseEntity.getBody().getHeight());
    assertEquals(IMG_WIDTH_HEIGHT, bufferedImageResponseEntity.getBody().getWidth());
  }

}
