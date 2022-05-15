package ch.vitoco.decovid19core.service;

import static ch.vitoco.decovid19core.constants.Const.IMAGE_CORRUPTED_EXCEPTION;
import static ch.vitoco.decovid19core.constants.Const.JSON_DESERIALIZE_EXCEPTION;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import COSE.Message;
import ch.vitoco.decovid19core.exception.ImageNotValidException;
import ch.vitoco.decovid19core.exception.JsonDeserializeException;
import ch.vitoco.decovid19core.model.*;
import ch.vitoco.decovid19core.server.HcertServerRequest;
import ch.vitoco.decovid19core.server.HcertServerResponse;
import ch.vitoco.decovid19core.utils.HcertFileUtils;
import ch.vitoco.decovid19core.utils.HcertStringUtils;
import ch.vitoco.decovid19core.utils.HcertUtils;
import ch.vitoco.decovid19core.valuesets.HcertValueSet;
import ch.vitoco.decovid19core.valuesets.model.ValueSetValues;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class Decovid19Service {

  private static final Logger LOGGER = LoggerFactory.getLogger(Decovid19Service.class);

  private static final String HCERT_HC1_PREFIX = "HC1:";

  public ResponseEntity<HcertServerResponse> getHealthCertificateContent(MultipartFile imageFile) {
    if (HcertFileUtils.isFileAllowed(imageFile)) {
      try (InputStream imageFileInputStream = imageFile.getInputStream()) {
        String hcertContent = HcertUtils.getHealthCertificateContent(imageFileInputStream);
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
    Message hcertCoseMessage = HcertUtils.getCOSEMessageFromHcert(hcertContent);
    String hcertCborMessage = HcertUtils.getCBORMessage(hcertCoseMessage);
    String hcertIssuer = HcertUtils.getIssuer(hcertCborMessage);
    HcertDTO hcertDTO = getHcertdDTO(hcertCborMessage);
    HcertTimeStampDTO hcertTimeStampDTO = HcertUtils.getHcertTimeStamp(hcertCborMessage);
    String hcertKID = HcertUtils.getKID(hcertCoseMessage);
    String hcertAlgo = HcertUtils.getAlgo(hcertCoseMessage);
    HcertServerResponse hcertResponse = buildHcertResponse(hcertContent, hcertDTO, hcertKID, hcertAlgo, hcertIssuer,
        hcertTimeStampDTO);
    LOGGER.info("Health Certificate Content: {}, KID: {}, Algo: {}, Issuer: {} ", hcertDTO, hcertKID, hcertAlgo,
        hcertIssuer);
    return ResponseEntity.ok().body(hcertResponse);
  }

  private HcertDTO getHcertdDTO(String hcertCborMessage) {
    String jsonPayloadFromCBORMessage = HcertUtils.getContent(hcertCborMessage);
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

    mappingVaccinationValueSet(hcertContentDTO.getV());
    mappingTestValueSet(hcertContentDTO.getT());
    mappingRecoveryValueSet(hcertContentDTO.getR());

    return hcertContentDTO;
  }

  private void mappingVaccinationValueSet(List<HcertVaccination> hcertVaccinationList) {
    Map<String, ValueSetValues> countryCodesValueMap = HcertValueSet.getCountryCodes().getValueSetValues();
    Map<String, ValueSetValues> diseaseAgentValueMap = HcertValueSet.getDiseaseAgentTarget().getValueSetValues();
    Map<String, ValueSetValues> vaccineMarketingAuthValueMap = HcertValueSet.getVaccineMarketingAuthorisations()
        .getValueSetValues();
    Map<String, ValueSetValues> vaccineMedicinalProdValueMap = HcertValueSet.getVaccineMedicinalProduct()
        .getValueSetValues();
    Map<String, ValueSetValues> vaccineProphylaxisValueMap = HcertValueSet.getVaccineProphylaxis().getValueSetValues();

    if (hcertVaccinationList != null) {
      for (HcertVaccination hcertVacc : hcertVaccinationList) {
        if (countryCodesValueMap.containsKey(hcertVacc.getCo())) {
          hcertVacc.setCo(countryCodesValueMap.get(hcertVacc.getCo()).getDisplay());
        }
        if (diseaseAgentValueMap.containsKey(hcertVacc.getTg())) {
          hcertVacc.setTg(diseaseAgentValueMap.get(hcertVacc.getTg()).getDisplay());
        }
        if (vaccineMarketingAuthValueMap.containsKey(hcertVacc.getMa())) {
          hcertVacc.setMa(vaccineMarketingAuthValueMap.get(hcertVacc.getMa()).getDisplay());
        }
        if (vaccineMedicinalProdValueMap.containsKey(hcertVacc.getMp())) {
          hcertVacc.setMp(vaccineMedicinalProdValueMap.get(hcertVacc.getMp()).getDisplay());
        }
        if (vaccineProphylaxisValueMap.containsKey(hcertVacc.getVp())) {
          hcertVacc.setVp(vaccineProphylaxisValueMap.get(hcertVacc.getVp()).getDisplay());
        }
      }
    }
  }

  private void mappingTestValueSet(List<HcertTest> hcertTestList) {
    Map<String, ValueSetValues> countryCodesValueMap = HcertValueSet.getCountryCodes().getValueSetValues();
    Map<String, ValueSetValues> diseaseAgentValueMap = HcertValueSet.getDiseaseAgentTarget().getValueSetValues();
    Map<String, ValueSetValues> testDeviceValueMap = HcertValueSet.getTestDevice().getValueSetValues();
    Map<String, ValueSetValues> testTypeValueMap = HcertValueSet.getTestType().getValueSetValues();
    Map<String, ValueSetValues> testResultValueMap = HcertValueSet.getTestResult().getValueSetValues();

    if (hcertTestList != null) {
      for (HcertTest hcertTest : hcertTestList) {
        if (countryCodesValueMap.containsKey(hcertTest.getCo())) {
          hcertTest.setCo(countryCodesValueMap.get(hcertTest.getCo()).getDisplay());
        }
        if (diseaseAgentValueMap.containsKey(hcertTest.getTg())) {
          hcertTest.setTg(diseaseAgentValueMap.get(hcertTest.getTg()).getDisplay());
        }
        if (testDeviceValueMap.containsKey(hcertTest.getMa())) {
          hcertTest.setMa(testDeviceValueMap.get(hcertTest.getMa()).getDisplay());
        }
        if (testTypeValueMap.containsKey(hcertTest.getTt())) {
          hcertTest.setTt(testTypeValueMap.get(hcertTest.getTt()).getDisplay());
        }
        if (testResultValueMap.containsKey(hcertTest.getTr())) {
          hcertTest.setTr(testResultValueMap.get(hcertTest.getTr()).getDisplay());
        }
      }
    }
  }

  private void mappingRecoveryValueSet(List<HcertRecovery> hcertRecoveryList) {
    Map<String, ValueSetValues> countryCodesValueMap = HcertValueSet.getCountryCodes().getValueSetValues();
    Map<String, ValueSetValues> diseaseAgentValueMap = HcertValueSet.getDiseaseAgentTarget().getValueSetValues();

    if (hcertRecoveryList != null) {
      for (HcertRecovery hcertRecovery : hcertRecoveryList) {
        if (countryCodesValueMap.containsKey(hcertRecovery.getCo())) {
          hcertRecovery.setCo(countryCodesValueMap.get(hcertRecovery.getCo()).getDisplay());
        }
        if (diseaseAgentValueMap.containsKey(hcertRecovery.getTg())) {
          hcertRecovery.setTg(diseaseAgentValueMap.get(hcertRecovery.getTg()).getDisplay());
        }
      }
    }
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
