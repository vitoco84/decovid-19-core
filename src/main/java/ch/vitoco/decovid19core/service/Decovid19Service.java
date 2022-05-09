package ch.vitoco.decovid19core.service;

import static ch.vitoco.decovid19core.constants.Const.IMAGE_CORRUPTED_EXCEPTION;
import static ch.vitoco.decovid19core.constants.Const.JSON_DESERIALIZE_EXCEPTION;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.vitoco.decovid19core.exception.ImageNotValidException;
import ch.vitoco.decovid19core.exception.JsonDeserializeException;
import ch.vitoco.decovid19core.model.HcertDTO;
import ch.vitoco.decovid19core.model.HcertRecoveryDTO;
import ch.vitoco.decovid19core.model.HcertTestDTO;
import ch.vitoco.decovid19core.model.HcertVaccinationDTO;
import ch.vitoco.decovid19core.server.HcertServerRequest;
import ch.vitoco.decovid19core.server.HcertServerResponse;
import ch.vitoco.decovid19core.utils.HcertFileUtils;
import ch.vitoco.decovid19core.utils.HcertStringUtils;
import ch.vitoco.decovid19core.utils.HcertUtils;

import COSE.Message;

@Service
public class Decovid19Service {

  private static final Logger LOGGER = LoggerFactory.getLogger(Decovid19Service.class);

  private static final String VACCINATION_PREFIX_PAYLOAD = "{\"v";
  private static final String RECOVERY_PREFIX_PAYLOAD = "{\"r";
  private static final String TESTED_PREFIX_PAYLOAD = "{\"t";
  private static final String HCERT_HC1_PREFIX = "HC1:";

  public ResponseEntity<HcertServerResponse> getHealthCertificateContent(MultipartFile imageFile) {
    if (HcertFileUtils.isFileAllowed(imageFile)) {
      try (InputStream imageFileInputStream = imageFile.getInputStream()) {
        String hcertContent = HcertUtils.getHealthCertificateContent(imageFileInputStream);
        Message hcertCoseMessage = HcertUtils.getCOSEMessageFromHcert(hcertContent);
        String hcertAlgo = HcertUtils.getAlgo(hcertCoseMessage);
        HcertDTO hcertDTO = getHcertdDTO(hcertCoseMessage);
        String hcertKID = HcertUtils.getKID(hcertCoseMessage);
        HcertServerResponse hcertResponse = buildHcertResponse(hcertContent, hcertDTO, hcertKID, hcertAlgo);
        LOGGER.info("Health Certificate Payload: {}", hcertDTO);
        return ResponseEntity.ok().body(hcertResponse);
      } catch (IOException e) {
        throw new ImageNotValidException(IMAGE_CORRUPTED_EXCEPTION, e);
      }
    } else {
      String originalFilename = HcertStringUtils.sanitizeUserInputString(imageFile);
      LOGGER.info("Bad Request for: {}", originalFilename);
      return ResponseEntity.badRequest().build();
    }
  }

  public ResponseEntity<HcertServerResponse> getHealthCertificateContent(HcertServerRequest hcertPrefix) {
    if (!hcertPrefix.getHcertPrefix().isBlank() && hcertPrefix.getHcertPrefix().startsWith(HCERT_HC1_PREFIX)) {
      String hcertContent = hcertPrefix.getHcertPrefix();
      Message hcertCoseMessage = HcertUtils.getCOSEMessageFromHcert(hcertContent);
      HcertDTO hcertDTO = getHcertdDTO(hcertCoseMessage);
      String hcertKID = HcertUtils.getKID(hcertCoseMessage);
      String hcertAlgo = HcertUtils.getAlgo(hcertCoseMessage);
      HcertServerResponse hcertResponse = buildHcertResponse(hcertContent, hcertDTO, hcertKID, hcertAlgo);
      LOGGER.info("Health Certificate Payload: {} ", hcertDTO);
      return ResponseEntity.ok().body(hcertResponse);
    } else {
      LOGGER.info("Bad Request for: {}", hcertPrefix);
      return ResponseEntity.badRequest().build();
    }
  }

  private HcertDTO getHcertdDTO(Message hcertCoseMessage) {
    String hcertCbor = HcertUtils.getCBORMessage(hcertCoseMessage);
    String jsonPayloadFromCBORMessage = HcertUtils.getJSONPayloadFromCBORMessage(hcertCbor);
    return buildHcertResponse(jsonPayloadFromCBORMessage);
  }

  private HcertDTO buildHcertResponse(String jsonPayloadFromCBORMessage) {
    ObjectMapper objectMapper = new ObjectMapper();
    HcertDTO hcertDTO = new HcertDTO();
    try {
      if (jsonPayloadFromCBORMessage.startsWith(VACCINATION_PREFIX_PAYLOAD)) {
        hcertDTO = objectMapper.readValue(jsonPayloadFromCBORMessage, HcertVaccinationDTO.class);
      }
      if (jsonPayloadFromCBORMessage.startsWith(TESTED_PREFIX_PAYLOAD)) {
        hcertDTO = objectMapper.readValue(jsonPayloadFromCBORMessage, HcertTestDTO.class);
      }
      if (jsonPayloadFromCBORMessage.startsWith(RECOVERY_PREFIX_PAYLOAD)) {
        hcertDTO = objectMapper.readValue(jsonPayloadFromCBORMessage, HcertRecoveryDTO.class);
      }
    } catch (JsonProcessingException e) {
      throw new JsonDeserializeException(JSON_DESERIALIZE_EXCEPTION, e);
    }
    return hcertDTO;
  }

  private HcertServerResponse buildHcertResponse(String hcertContent,
      HcertDTO hcertDTO,
      String hcertKID,
      String hcertAlgo) {
    HcertServerResponse hcertResponse = new HcertServerResponse();
    hcertResponse.setHcertPrefix(hcertContent);
    hcertResponse.setHcertPayload(hcertDTO);
    hcertResponse.setHcertKID(hcertKID);
    hcertResponse.setHcertAlgo(hcertAlgo);
    return hcertResponse;
  }

}
