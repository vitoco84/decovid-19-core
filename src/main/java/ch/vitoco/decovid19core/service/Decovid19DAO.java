package ch.vitoco.decovid19core.service;

import COSE.Message;
import ch.vitoco.decovid19core.exception.ImageCorruptedException;
import ch.vitoco.decovid19core.server.HcertServerRequest;
import ch.vitoco.decovid19core.server.HcertServerResponse;
import ch.vitoco.decovid19core.utils.HcertFileUtils;
import ch.vitoco.decovid19core.utils.HcertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static ch.vitoco.decovid19core.utils.ExceptionMessages.IMAGE_CORRUPTED_EXCEPTION_MESSAGE;

@Service
public class Decovid19DAO {

  private static final Logger LOGGER = LoggerFactory.getLogger(Decovid19DAO.class);

  public ResponseEntity<HcertServerResponse> getHealthCertificateContent(MultipartFile imageFile) {
    if (HcertFileUtils.isFileAllowed(imageFile)) {
      try (InputStream imageFileInputStream = imageFile.getInputStream()) {
        String hcertContent = HcertUtils.getHealthCertificateContent(imageFileInputStream);
        Message hcertCOSEMessage = HcertUtils.getCOSEMessageFromHcert(hcertContent);
        String hcertCBOR = HcertUtils.getCBORMessage(hcertCOSEMessage);
        HcertServerResponse hcertResponse = buildHealthCertificateResponse(hcertContent, hcertCBOR);
        LOGGER.info("Health Certificate Payload: {} ", hcertCBOR);
        return ResponseEntity.ok().body(hcertResponse);
      } catch (IOException e) {
        throw new ImageCorruptedException(IMAGE_CORRUPTED_EXCEPTION_MESSAGE, e);
      }
    } else {
      return ResponseEntity.badRequest().build();
    }
  }

  public ResponseEntity<HcertServerResponse> getHealthCertificateContent(HcertServerRequest hcertPrefix) {
    if (!hcertPrefix.getHcertPrefix().isBlank() && hcertPrefix.getHcertPrefix().startsWith("HC1:")) {
      String hcertContent = hcertPrefix.getHcertPrefix();
      Message hcertCoseMessage = HcertUtils.getCOSEMessageFromHcert(hcertContent);
      String hcertCbor = HcertUtils.getCBORMessage(hcertCoseMessage);
      HcertServerResponse hcertResponse = buildHealthCertificateResponse(hcertContent, hcertCbor);
      LOGGER.info("Health Certificate Payload: {} ", hcertCbor);
      return ResponseEntity.ok().body(hcertResponse);
    } else {
      return ResponseEntity.badRequest().build();
    }
  }

  private HcertServerResponse buildHealthCertificateResponse(String hcertContent, String hcertCbor) {
    HcertServerResponse hcertResponse = new HcertServerResponse();
    hcertResponse.setHcertPrefix(hcertContent);
    hcertResponse.setHcertPayload(hcertCbor);
    return hcertResponse;
  }

}
