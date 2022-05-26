package ch.vitoco.decovid19core.service;

import static ch.vitoco.decovid19core.constants.Const.URL_ENCODE_EXCEPTION;

import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import ch.vitoco.decovid19core.exception.URLEncodeException;
import ch.vitoco.decovid19core.server.QRCodeServerRequest;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service class QRCodeGeneratorService.
 */
@Service
public class QRCodeGeneratorService {

  /**
   * Generates a QR-Code (BufferedImage) given a QRCodeServerRequest.
   *
   * @param url the QRCodeServerRequest
   * @return BufferedImage
   */
  public ResponseEntity<BufferedImage> getQRCode(QRCodeServerRequest url) {
    if (isValidURL(url.getUrl())) {
      try {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(
            new String(url.getUrl().getBytes(), StandardCharsets.UTF_8), BarcodeFormat.QR_CODE, 250, 250);
        return ResponseEntity.ok().body(MatrixToImageWriter.toBufferedImage(bitMatrix));
      } catch (WriterException e) {
        throw new URLEncodeException(URL_ENCODE_EXCEPTION, e);
      }
    } else {
      return ResponseEntity.badRequest().build();
    }
  }

  private boolean isValidURL(String url) {
    try {
      new URL(url).toURI();
    } catch (MalformedURLException | URISyntaxException e) {
      return false;
    }
    return true;
  }

}
