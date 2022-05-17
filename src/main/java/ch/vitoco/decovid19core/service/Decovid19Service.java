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
import ch.vitoco.decovid19core.model.HcertContentDTO;
import ch.vitoco.decovid19core.model.HcertDTO;
import ch.vitoco.decovid19core.model.HcertTimeStampDTO;
import ch.vitoco.decovid19core.server.HcertServerRequest;
import ch.vitoco.decovid19core.server.HcertServerResponse;
import ch.vitoco.decovid19core.utils.HcertFileUtils;
import ch.vitoco.decovid19core.utils.HcertStringUtils;

import COSE.Message;

/**
 * Service class Decovid19Service.
 */
@Service
public class Decovid19Service {

  private static final Logger LOGGER = LoggerFactory.getLogger(Decovid19Service.class);

  private static final String HCERT_HC1_PREFIX = "HC1:";

  private final Decovid19ValueSetService decovid19ValueSetService;
  private final Decovid19HcertService decovid19HcertService;

  /**
   * Constructor.
   *
   * @param decovid19ValueSetService the Decovid19ValueSetService
   * @param decovid19HcertService    the Decovid19HcertService
   */
  public Decovid19Service(Decovid19ValueSetService decovid19ValueSetService,
      Decovid19HcertService decovid19HcertService) {
    this.decovid19ValueSetService = decovid19ValueSetService;
    this.decovid19HcertService = decovid19HcertService;
  }

  /**
   * Gets the HcertServerResponse.
   *
   * @param imageFile the Health Certificate QR-Code image file
   * @return HcertServerResponse
   */
  public ResponseEntity<HcertServerResponse> getHealthCertificateContent(MultipartFile imageFile) {
    if (HcertFileUtils.isFileAllowed(imageFile)) {
      try (InputStream imageFileInputStream = imageFile.getInputStream()) {
        String hcertContent = decovid19HcertService.getHealthCertificateContent(imageFileInputStream);
        return getHcertServerResponseResponseEntity(hcertContent);
      } catch (IOException e) {
        throw new ImageNotValidException(IMAGE_CORRUPTED_EXCEPTION, e);
      }
    } else {
      String originalFilename = HcertStringUtils.sanitizeUserInputString(imageFile);
      LOGGER.info("Bad Request for: {}", originalFilename);
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Gets the HcertServerResponse.
   *
   * @param hcertPrefix the HcertServerRequest with the Health Certificate Prefix
   * @return HcertServerResponse
   */
  public ResponseEntity<HcertServerResponse> getHealthCertificateContent(HcertServerRequest hcertPrefix) {
    if (!hcertPrefix.getHcertPrefix().isBlank() && hcertPrefix.getHcertPrefix().startsWith(HCERT_HC1_PREFIX)) {
      String hcertContent = hcertPrefix.getHcertPrefix();
      return getHcertServerResponseResponseEntity(hcertContent);
    } else {
      LOGGER.info("Bad Request for: {}", hcertPrefix);
      return ResponseEntity.badRequest().build();
    }
  }

  private ResponseEntity<HcertServerResponse> getHcertServerResponseResponseEntity(String hcertContent) {
    Message hcertCoseMessage = decovid19HcertService.getCOSEMessageFromHcert(hcertContent);
    String hcertCborMessage = decovid19HcertService.getCBORMessage(hcertCoseMessage);
    String hcertIssuer = decovid19HcertService.getIssuer(hcertCborMessage);
    HcertDTO hcertDTO = getHcertdDTO(hcertCborMessage);
    HcertTimeStampDTO hcertTimeStampDTO = decovid19HcertService.getHcertTimeStamp(hcertCborMessage);
    String hcertKID = decovid19HcertService.getKID(hcertCoseMessage);
    String hcertAlgo = decovid19HcertService.getAlgo(hcertCoseMessage);
    HcertServerResponse hcertResponse = buildHcertResponse(hcertContent, hcertDTO, hcertKID, hcertAlgo, hcertIssuer,
        hcertTimeStampDTO);
    LOGGER.info("Health Certificate Content: {}, KID: {}, Algo: {}, Issuer: {} ", hcertDTO, hcertKID, hcertAlgo,
        hcertIssuer);
    return ResponseEntity.ok().body(hcertResponse);
  }

  private HcertDTO getHcertdDTO(String hcertCborMessage) {
    String jsonPayloadFromCBORMessage = decovid19HcertService.getContent(hcertCborMessage);
    return buildHcertDTO(jsonPayloadFromCBORMessage);
  }

  private HcertDTO buildHcertDTO(String jsonPayloadFromCBORMessage) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return buildHcertContentDTO(jsonPayloadFromCBORMessage, objectMapper);
    } catch (JsonProcessingException e) {
      throw new JsonDeserializeException(JSON_DESERIALIZE_EXCEPTION, e);
    }
  }

  private HcertContentDTO buildHcertContentDTO(String jsonPayloadFromCBORMessage, ObjectMapper objectMapper)
      throws JsonProcessingException {
    HcertContentDTO hcertContentDTO = objectMapper.readValue(jsonPayloadFromCBORMessage, HcertContentDTO.class);

    decovid19ValueSetService.mappingVaccinationValueSet(hcertContentDTO.getV());
    decovid19ValueSetService.mappingTestValueSet(hcertContentDTO.getT());
    decovid19ValueSetService.mappingRecoveryValueSet(hcertContentDTO.getR());

    return hcertContentDTO;
  }

  private HcertServerResponse buildHcertResponse(String hcertContent,
      HcertDTO hcertDTO,
      String hcertKID,
      String hcertAlgo,
      String hcertIssuer,
      HcertTimeStampDTO hcertTimeStampDTO) {
    HcertServerResponse hcertResponse = new HcertServerResponse();
    hcertResponse.setHcertPrefix(hcertContent);
    hcertResponse.setHcertContent(hcertDTO);
    hcertResponse.setHcertKID(hcertKID);
    hcertResponse.setHcertAlgo(hcertAlgo);
    hcertResponse.setHcertIssuer(hcertIssuer);
    hcertResponse.setHcertTimeStamp(hcertTimeStampDTO);
    return hcertResponse;
  }

}
