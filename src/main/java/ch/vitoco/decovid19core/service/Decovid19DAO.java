package ch.vitoco.decovid19core.service;

import COSE.Message;
import ch.vitoco.decovid19core.domain.HcertPayloadDTO;
import ch.vitoco.decovid19core.domain.HcertPayloadRecoveryDTO;
import ch.vitoco.decovid19core.domain.HcertPayloadTestedDTO;
import ch.vitoco.decovid19core.domain.HcertPayloadVaccinationDTO;
import ch.vitoco.decovid19core.exception.ImageCorruptedException;
import ch.vitoco.decovid19core.server.HcertServerRequest;
import ch.vitoco.decovid19core.server.HcertServerResponse;
import ch.vitoco.decovid19core.utils.HcertFileUtils;
import ch.vitoco.decovid19core.utils.HcertUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

  private static final String VACCINATION_PREFIX_PAYLOAD = "{\"v";
  private static final String RECOVERY_PREFIX_PAYLOAD = "{\"r";
  private static final String TESTED_PREFIX_PAYLOAD = "{\"t";

  public ResponseEntity<HcertServerResponse> getHealthCertificateContent(MultipartFile imageFile) {
    if (HcertFileUtils.isFileAllowed(imageFile)) {
      try (InputStream imageFileInputStream = imageFile.getInputStream()) {
        String hcertContent = HcertUtils.getHealthCertificateContent(imageFileInputStream);
        HcertPayloadDTO hcertPayloadDTO = getHcertPayloadDTO(hcertContent);
        HcertServerResponse hcertResponse = buildHealthCertificateResponse(hcertContent, hcertPayloadDTO);
        LOGGER.info("Health Certificate Payload: {} ", hcertPayloadDTO);
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
      HcertPayloadDTO hcertPayloadDTO = getHcertPayloadDTO(hcertContent);
      HcertServerResponse hcertResponse = buildHealthCertificateResponse(hcertContent, hcertPayloadDTO);
      LOGGER.info("Health Certificate Payload: {} ", hcertPayloadDTO);
      return ResponseEntity.ok().body(hcertResponse);
    } else {
      return ResponseEntity.badRequest().build();
    }
  }

  private HcertPayloadDTO getHcertPayloadDTO(String hcertContent) {
    Message hcertCoseMessage = HcertUtils.getCOSEMessageFromHcert(hcertContent);
    String hcertCbor = HcertUtils.getCBORMessage(hcertCoseMessage);
    String jsonPayloadFromCBORMessage = HcertUtils.getJsonPayloadFromCBORMessage(hcertCbor);
    return getHcertPayloadResponse(jsonPayloadFromCBORMessage);
  }

  private HcertPayloadDTO getHcertPayloadResponse(String jsonPayloadFromCBORMessage) {
    ObjectMapper objectMapper = new ObjectMapper();
    HcertPayloadDTO hcertPayloadDTO = new HcertPayloadDTO();
    try {
      if (jsonPayloadFromCBORMessage.startsWith(VACCINATION_PREFIX_PAYLOAD)) {
        hcertPayloadDTO = objectMapper.readValue(jsonPayloadFromCBORMessage, HcertPayloadVaccinationDTO.class);
      }
      if (jsonPayloadFromCBORMessage.startsWith(TESTED_PREFIX_PAYLOAD)) {
        hcertPayloadDTO = objectMapper.readValue(jsonPayloadFromCBORMessage, HcertPayloadTestedDTO.class);
      }
      if (jsonPayloadFromCBORMessage.startsWith(RECOVERY_PREFIX_PAYLOAD)) {
        hcertPayloadDTO = objectMapper.readValue(jsonPayloadFromCBORMessage, HcertPayloadRecoveryDTO.class);
      }
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return hcertPayloadDTO;
  }

  private HcertServerResponse buildHealthCertificateResponse(String hcertContent, HcertPayloadDTO hcertPayloadDTO) {
    HcertServerResponse hcertResponse = new HcertServerResponse();
    hcertResponse.setHcertPrefix(hcertContent);
    hcertResponse.setHcertPayload(hcertPayloadDTO);
    return hcertResponse;
  }

}
