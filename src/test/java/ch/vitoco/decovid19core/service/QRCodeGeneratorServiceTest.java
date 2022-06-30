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
  void shouldGenerateURLQRCodeAsBufferedImage() {
    QRCodeServerRequest qrCodeServerRequest = new QRCodeServerRequest();
    qrCodeServerRequest.setUrl(URL_VALID);

    ResponseEntity<BufferedImage> bufferedImageResponseEntity = qrCodeGeneratorService.createURLQRCodeImage(
        qrCodeServerRequest);
    HttpStatus statusCode = bufferedImageResponseEntity.getStatusCode();
    BufferedImage bufferedImage = bufferedImageResponseEntity.getBody();

    assertEquals(HttpStatus.OK, statusCode);
    assertEquals(IMG_WIDTH_HEIGHT, bufferedImage.getHeight());
    assertEquals(IMG_WIDTH_HEIGHT, bufferedImage.getWidth());
  }

  @Test
  void shouldGenerateURLQRCodeAsBase64String() {
    QRCodeServerRequest qrCodeServerRequest = new QRCodeServerRequest();
    qrCodeServerRequest.setUrl(URL_VALID);

    ResponseEntity<String> urlqrCodeClient = qrCodeGeneratorService.createURLQRCodeBase64String(qrCodeServerRequest);
    HttpStatus statusCode = urlqrCodeClient.getStatusCode();
    String actualBody = urlqrCodeClient.getBody();

    String expectedBody = "iVBORw0KGgoAAAANSUhEUgAAAPoAAAD6AQAAAACgl2eQAAABRUlEQVR4Xu2XMQ7CMAxFXXVg5Ag9So/WHI2j9AgdGaoaf/9EgoIYkfjKXxw7b6lt7GD+XTc7R07qANUBqgNUB6ifAXeDRt/MljC+pn+VA3A+xvRh1rkFxYD48oN5OCIB61xslAVgFtRZHpjLAE8V8OzqzWbHzYeulgAMYh5YdUgOaNomL77zpkoJuNvkbnaJcpcwEYnm1gMiAXHYL1g1R3DhkdMCWh9b3Ax7noZadSnAYjiRw2INbsFElgOYB3Q1PRo5gF/ujsW6Zx7Q43IA8pC/2nA+drUKgPdRbhzOqOX8TNIAHCGWGz9lvA1xoQVU5X7lcCotpgQ8vwZr8TM5agDOeO1G1fGH9D0PIsDKPo7FWmcUOVWAj14s1vMoVgJCGFVpXrtaA/Da1Z6jeEJYEDBoTJNAm8hawBd1gOoA1QGqA9R/AA8qoCkHXcmkggAAAABJRU5ErkJggg==";

    assertEquals(HttpStatus.OK, statusCode);
    assertEquals(expectedBody, actualBody);
  }

  @Test
  void shouldReturnBadRequestForInvalidURL() {
    QRCodeServerRequest qrCodeServerRequest = new QRCodeServerRequest();
    qrCodeServerRequest.setUrl(URL_INVALID);

    ResponseEntity<BufferedImage> bufferedImageResponseEntity = qrCodeGeneratorService.createURLQRCodeImage(
        qrCodeServerRequest);
    HttpStatus statusCode = bufferedImageResponseEntity.getStatusCode();

    assertEquals(HttpStatus.BAD_REQUEST, statusCode);
  }

  @Test
  void shouldGenerateFakeTestHcertQRCodeAsBufferedImage() {
    HcertContentDTO hcertContentDTO = buildHcertContentDTO();

    ResponseEntity<BufferedImage> bufferedImageResponseEntity = qrCodeGeneratorService.createTestCovidQRCodeImage(
        hcertContentDTO);

    assertEquals(HttpStatus.OK, bufferedImageResponseEntity.getStatusCode());
    assertEquals(IMG_WIDTH_HEIGHT, bufferedImageResponseEntity.getBody().getHeight());
    assertEquals(IMG_WIDTH_HEIGHT, bufferedImageResponseEntity.getBody().getWidth());
  }

  @Test
  void shouldGenerateFakeTestHcertQRCodeAsBase64String() {
    HcertContentDTO hcertContentDTO = buildHcertContentDTO();

    ResponseEntity<String> testCovidQRCodeBase64String = qrCodeGeneratorService.createTestCovidQRCodeBase64String(
        hcertContentDTO);

    assertEquals(HttpStatus.OK, testCovidQRCodeBase64String.getStatusCode());
  }

  private HcertContentDTO buildHcertContentDTO() {
    HcertHolder hcertHolder = new HcertHolder();
    hcertHolder.setSurname("Uncle");
    hcertHolder.setStandardSurname("UNCLE");
    hcertHolder.setForename("Bob");
    hcertHolder.setStandardForename("BOB");

    HcertTest hcertTest = new HcertTest();
    hcertTest.setTarget("COVID-19");
    hcertTest.setCountry("Switzerland");
    hcertTest.setTypeOfTest("Rapid Test");
    hcertTest.setNucleicAcidAmplName("COVID-19");
    hcertTest.setTestDeviceManufacturer("COVID-19 Test");
    hcertTest.setSampleCollectionDate("2021-04-30");
    hcertTest.setTestResult("Not detected");
    hcertTest.setTestingCentre("Test Center");
    hcertTest.setIssuer("Bundesamt für Gesundheit (BAG)");

    HcertContentDTO hcertContentDTO = new HcertContentDTO();
    hcertContentDTO.setDateOfBirth("1943-02-01");
    hcertContentDTO.setVersion("1.0.0");
    hcertContentDTO.setName(hcertHolder);
    hcertContentDTO.setTest(List.of(hcertTest));

    return hcertContentDTO;
  }

}
