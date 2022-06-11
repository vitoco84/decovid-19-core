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

    ResponseEntity<BufferedImage> bufferedImageResponseEntity = qrCodeGeneratorService.createTestCovidQRCode(
        hcertContentDTO);

    assertEquals(HttpStatus.OK, bufferedImageResponseEntity.getStatusCode());
    assertEquals(IMG_WIDTH_HEIGHT, bufferedImageResponseEntity.getBody().getHeight());
    assertEquals(IMG_WIDTH_HEIGHT, bufferedImageResponseEntity.getBody().getWidth());
  }

}
