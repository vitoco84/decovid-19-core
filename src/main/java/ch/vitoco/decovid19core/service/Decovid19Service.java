package ch.vitoco.decovid19core.service;

import static ch.vitoco.decovid19core.constants.Const.JSON_DESERIALIZE_EXCEPTION;
import static ch.vitoco.decovid19core.constants.Const.QR_CODE_CORRUPTED_EXCEPTION;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upokecenter.cbor.CBORObject;

import ch.vitoco.decovid19core.exception.ImageNotValidException;
import ch.vitoco.decovid19core.exception.JsonDeserializeException;
import ch.vitoco.decovid19core.model.HcertContentDTO;
import ch.vitoco.decovid19core.model.HcertDTO;
import ch.vitoco.decovid19core.model.HcertPublicKeyDTO;
import ch.vitoco.decovid19core.model.HcertTimeStampDTO;
import ch.vitoco.decovid19core.server.HcertServerRequest;
import ch.vitoco.decovid19core.server.HcertServerResponse;
import ch.vitoco.decovid19core.server.PEMCertServerRequest;
import ch.vitoco.decovid19core.server.PEMCertServerResponse;
import ch.vitoco.decovid19core.utils.HcertFileUtils;
import ch.vitoco.decovid19core.utils.HcertStringUtils;

/**
 * Service class Decovid19DecoderService.
 */
@Service
public class Decovid19Service {

  private static final Logger LOGGER = LoggerFactory.getLogger(Decovid19Service.class);

  /**
   * Header String that is prefixed to Base45 encoded Health Certificate.
   */
  private static final String HCERT_HEADER = "HC1:";

  private final Decovid19ValueSetService decovid19ValueSetService;
  private final Decovid19HcertService decovid19HcertService;
  private final Decovid19TrustListService decovid19TrustListService;

  /**
   * Constructor.
   *
   * @param decovid19ValueSetService  the Decovid19ValueSetService
   * @param decovid19HcertService     the Decovid19HcertService
   * @param decovid19TrustListService the Decovid19TrustListService
   */
  public Decovid19Service(Decovid19ValueSetService decovid19ValueSetService,
      Decovid19HcertService decovid19HcertService,
      Decovid19TrustListService decovid19TrustListService) {
    this.decovid19ValueSetService = decovid19ValueSetService;
    this.decovid19HcertService = decovid19HcertService;
    this.decovid19TrustListService = decovid19TrustListService;
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
        throw new ImageNotValidException(QR_CODE_CORRUPTED_EXCEPTION, e);
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
    if (!hcertPrefix.getHcertPrefix().isBlank() && hcertPrefix.getHcertPrefix().startsWith(HCERT_HEADER)) {
      String hcertContent = hcertPrefix.getHcertPrefix();
      return getHcertServerResponseResponseEntity(hcertContent);
    } else {
      LOGGER.info("Bad Request for: {}", hcertPrefix);
      return ResponseEntity.badRequest().build();
    }
  }

  private ResponseEntity<HcertServerResponse> getHcertServerResponseResponseEntity(String hcertContent) {
    CBORObject cborObject = decovid19HcertService.getCBORObject(hcertContent);
    String hcertIssuer = decovid19HcertService.getIssuer(cborObject);
    HcertDTO hcertDTO = getHcertdDTO(cborObject);
    HcertTimeStampDTO hcertTimeStampDTO = decovid19HcertService.getHcertTimeStamp(cborObject);
    String hcertKID = decovid19HcertService.getKID(cborObject);
    String hcertAlgo = decovid19HcertService.getAlgo(cborObject);
    HcertServerResponse hcertResponse = buildHcertResponse(hcertContent, hcertDTO, hcertKID, hcertAlgo, hcertIssuer,
        hcertTimeStampDTO);
    LOGGER.info("Health Certificate Content: {}, KID: {}, Algo: {}, Issuer: {} ", hcertDTO, hcertKID, hcertAlgo,
        hcertIssuer);
    return ResponseEntity.ok().body(hcertResponse);
  }

  private HcertDTO getHcertdDTO(CBORObject cborObject) {
    String jsonPayloadFromCBORMessage = decovid19HcertService.getContent(cborObject);
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

  public ResponseEntity<PEMCertServerResponse> getX509Certificate(PEMCertServerRequest pemCertificate) {
    X509Certificate x509Certificate = decovid19TrustListService.convertCertificateToX509(
        pemCertificate.getPemCertificate());

    PEMCertServerResponse pemCertServerResponse = buildPEMCertServerResponse(x509Certificate);

    return ResponseEntity.ok().body(pemCertServerResponse);
  }

  private PEMCertServerResponse buildPEMCertServerResponse(X509Certificate x509Certificate) {
    PEMCertServerResponse pemCertServerResponse = new PEMCertServerResponse();
    pemCertServerResponse.setVersion(String.valueOf(x509Certificate.getVersion()));
    pemCertServerResponse.setSubject(x509Certificate.getSubjectDN().getName());
    pemCertServerResponse.setSignatureAlgorithm(x509Certificate.getSigAlgName());
    pemCertServerResponse.setKey(x509Certificate.getPublicKey().toString());
    pemCertServerResponse.setValidTo(x509Certificate.getNotAfter().toString());
    pemCertServerResponse.setValidFrom(x509Certificate.getNotBefore().toString());
    pemCertServerResponse.setSerialNumber(x509Certificate.getSerialNumber().toString());
    pemCertServerResponse.setIssuer(x509Certificate.getIssuerDN().getName());
    pemCertServerResponse.setHcertPublicKey(buildPublicKeyResponse(x509Certificate));
    return pemCertServerResponse;
  }

  private HcertPublicKeyDTO buildPublicKeyResponse(X509Certificate x509Certificate) {
    HcertPublicKeyDTO hcertPublicKeyDTO = new HcertPublicKeyDTO();
    if (x509Certificate.getSigAlgName().contains("RSA")) {
      RSAPublicKey x509RSATemp = (RSAPublicKey) x509Certificate.getPublicKey();
      hcertPublicKeyDTO.setPublicExponent(String.valueOf(x509RSATemp.getPublicExponent()));
      hcertPublicKeyDTO.setModulus(String.valueOf(x509RSATemp.getModulus()));
      hcertPublicKeyDTO.setAlgo(x509RSATemp.getAlgorithm());
    }
    if (x509Certificate.getSigAlgName().contains("ECDSA")) {
      ECPublicKey x509ECDSATemp = (ECPublicKey) x509Certificate.getPublicKey();
      hcertPublicKeyDTO.setXCoord(x509ECDSATemp.getW().getAffineX().toString());
      hcertPublicKeyDTO.setYCoord(x509ECDSATemp.getW().getAffineY().toString());
      hcertPublicKeyDTO.setAlgo(x509ECDSATemp.getAlgorithm());
    }
    return hcertPublicKeyDTO;
  }

}
