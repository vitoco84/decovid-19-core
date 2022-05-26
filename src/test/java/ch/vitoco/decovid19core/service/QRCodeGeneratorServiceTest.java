package ch.vitoco.decovid19core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.image.BufferedImage;

import ch.vitoco.decovid19core.server.QRCodeServerRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class QRCodeGeneratorServiceTest {

  private static final String URL_VALID = "https://www.google.ch/";
  private static final String URL_INVALID = "foobar";

  private final QRCodeGeneratorService qrCodeGeneratorService = new QRCodeGeneratorService();

  @Test
  void shouldGenerateQRCode() {
    QRCodeServerRequest qrCodeServerRequest = new QRCodeServerRequest();
    qrCodeServerRequest.setUrl(URL_VALID);

    ResponseEntity<BufferedImage> bufferedImageResponseEntity = qrCodeGeneratorService.getQRCode(qrCodeServerRequest);
    HttpStatus statusCode = bufferedImageResponseEntity.getStatusCode();
    BufferedImage bufferedImage = bufferedImageResponseEntity.getBody();
    int actualImgHeight = bufferedImage.getHeight();
    int actualImgWidth = bufferedImage.getWidth();

    assertEquals(HttpStatus.OK, statusCode);
    assertEquals(250, actualImgHeight);
    assertEquals(250, actualImgWidth);
  }

  @Test
  void shouldReturnBadRequestForInvalidURL() {
    QRCodeServerRequest qrCodeServerRequest = new QRCodeServerRequest();
    qrCodeServerRequest.setUrl(URL_INVALID);

    ResponseEntity<BufferedImage> bufferedImageResponseEntity = qrCodeGeneratorService.getQRCode(qrCodeServerRequest);

    HttpStatus statusCode = bufferedImageResponseEntity.getStatusCode();

    assertEquals(HttpStatus.BAD_REQUEST, statusCode);
  }

}
