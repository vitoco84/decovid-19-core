package ch.vitoco.decovid19core.service;

import static ch.vitoco.decovid19core.constants.Const.JSON_DESERIALIZE_EXCEPTION;
import static ch.vitoco.decovid19core.constants.Const.QR_CODE_CORRUPTED_EXCEPTION;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import ch.vitoco.decovid19core.exception.ServerException;
import ch.vitoco.decovid19core.model.HcertContentDTO;
import ch.vitoco.decovid19core.model.HcertDTO;
import ch.vitoco.decovid19core.model.HcertPublicKeyDTO;
import ch.vitoco.decovid19core.server.HcertServerRequest;
import ch.vitoco.decovid19core.server.HcertServerResponse;
import ch.vitoco.decovid19core.server.PEMCertServerRequest;
import ch.vitoco.decovid19core.server.PEMCertServerResponse;
import ch.vitoco.decovid19core.utils.HcertFileUtils;
import ch.vitoco.decovid19core.utils.HcertStringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upokecenter.cbor.CBORObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service class Decovid19DecoderService.
 */
@Service
public class Decovid19Service {

  private static final Logger LOGGER = LoggerFactory.getLogger(Decovid19Service.class);

  private static final String HCERT_HEADER = "HC1:";
  private static final int RADIX_HEX = 16;
  private static final String SIGNATURE_ALGO_RSA = "RSA";
  private static final String SIGNATURE_ALGO_ECDSA = "EC";

  private final ValueSetService valueSetService;
  private final HcertService hcertService;
  private final TrustListService trustListService;

  /**
   * Constructor.
   *
   * @param valueSetService  the ValueSetService
   * @param hcertService     the HcertService
   * @param trustListService the TrustListService
   */
  public Decovid19Service(ValueSetService valueSetService,
      HcertService hcertService,
      TrustListService trustListService) {
    this.valueSetService = valueSetService;
    this.hcertService = hcertService;
    this.trustListService = trustListService;
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
        String hcertContent = hcertService.getHealthCertificateContent(imageFileInputStream);
        return getHcertServerResponseResponseEntity(hcertContent);
      } catch (IOException e) {
        throw new ServerException(QR_CODE_CORRUPTED_EXCEPTION, e);
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
    HcertServerResponse hcertResponse = buildHcertResponse(hcertService, hcertContent);
    LOGGER.info("Health Certificate Content: {} ", hcertResponse);
    return ResponseEntity.ok().body(hcertResponse);
  }

  private HcertDTO getHcertdDTO(CBORObject cborObject) {
    String jsonPayloadFromCBORMessage = hcertService.getContent(cborObject);
    return buildHcertDTO(jsonPayloadFromCBORMessage);
  }

  private HcertDTO buildHcertDTO(String jsonPayloadFromCBORMessage) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return buildHcertContentDTO(jsonPayloadFromCBORMessage, objectMapper);
    } catch (JsonProcessingException e) {
      throw new ServerException(JSON_DESERIALIZE_EXCEPTION, e);
    }
  }

  private HcertContentDTO buildHcertContentDTO(String jsonPayloadFromCBORMessage, ObjectMapper objectMapper)
      throws JsonProcessingException {
    HcertContentDTO hcertContentDTO = objectMapper.readValue(jsonPayloadFromCBORMessage, HcertContentDTO.class);
    valueSetService.mappingVaccinationValueSet(hcertContentDTO.getV());
    valueSetService.mappingTestValueSet(hcertContentDTO.getT());
    valueSetService.mappingRecoveryValueSet(hcertContentDTO.getR());
    return hcertContentDTO;
  }

  private HcertServerResponse buildHcertResponse(HcertService hcertService, String hcertContent) {
    HcertServerResponse hcertResponse = new HcertServerResponse();
    CBORObject cborObject = hcertService.getCBORObject(hcertContent);
    hcertResponse.setHcertPrefix(hcertContent);
    hcertResponse.setHcertContent(getHcertdDTO(cborObject));
    hcertResponse.setHcertKID(hcertService.getKID(cborObject));
    hcertResponse.setHcertAlgo(hcertService.getAlgo(cborObject));
    hcertResponse.setHcertIssuer(hcertService.getIssuer(cborObject));
    hcertResponse.setHcertTimeStamp(hcertService.getHcertTimeStamp(cborObject));
    return hcertResponse;
  }

  /**
   * Gets the PEMCertServerResponse.
   *
   * @param pemCertificate the PEMCertServerRequest with the Certificate as String.
   * @return PEMCertServerResponse
   */
  public ResponseEntity<PEMCertServerResponse> getX509Certificate(PEMCertServerRequest pemCertificate) {
    try {
      X509Certificate x509Certificate = trustListService.convertCertificateToX509(
          pemCertificate.getPemCertificate());
      PEMCertServerResponse pemCertServerResponse = buildPEMCertServerResponse(x509Certificate);
      LOGGER.info("PEM Certificate Content: {} ", pemCertServerResponse);
      return ResponseEntity.ok().body(pemCertServerResponse);
    } catch (ServerException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  private PEMCertServerResponse buildPEMCertServerResponse(X509Certificate x509Certificate) {
    PEMCertServerResponse pemCertServerResponse = new PEMCertServerResponse();
    pemCertServerResponse.setPublicKey(Base64.getEncoder().encodeToString(x509Certificate.getPublicKey().getEncoded()));
    pemCertServerResponse.setSubject(x509Certificate.getSubjectDN().getName());
    pemCertServerResponse.setSignatureAlgorithm(x509Certificate.getSigAlgName());
    pemCertServerResponse.setValidTo(x509Certificate.getNotAfter().toInstant().toString());
    pemCertServerResponse.setValidFrom(x509Certificate.getNotBefore().toInstant().toString());
    pemCertServerResponse.setSerialNumber(x509Certificate.getSerialNumber().toString(RADIX_HEX));
    pemCertServerResponse.setIssuer(x509Certificate.getIssuerDN().getName());
    pemCertServerResponse.setPublicKeyParams(buildPublicKeyResponse(x509Certificate));
    return pemCertServerResponse;
  }

  private HcertPublicKeyDTO buildPublicKeyResponse(X509Certificate x509Certificate) {
    HcertPublicKeyDTO hcertPublicKeyDTO = new HcertPublicKeyDTO();
    if (x509Certificate.getPublicKey().getAlgorithm().equals(SIGNATURE_ALGO_RSA)) {
      RSAPublicKey x509RSATemp = (RSAPublicKey) x509Certificate.getPublicKey();
      hcertPublicKeyDTO.setPublicExponent(x509RSATemp.getPublicExponent().toString(RADIX_HEX));
      hcertPublicKeyDTO.setModulus(x509RSATemp.getModulus().toString(RADIX_HEX));
      hcertPublicKeyDTO.setBitLength(String.valueOf(x509RSATemp.getModulus().bitLength()));
      hcertPublicKeyDTO.setAlgo(x509RSATemp.getAlgorithm());
    }
    if (x509Certificate.getPublicKey().getAlgorithm().equals(SIGNATURE_ALGO_ECDSA)) {
      ECPublicKey x509ECDSATemp = (ECPublicKey) x509Certificate.getPublicKey();
      hcertPublicKeyDTO.setXCoord(x509ECDSATemp.getW().getAffineX().toString(RADIX_HEX));
      hcertPublicKeyDTO.setYCoord(x509ECDSATemp.getW().getAffineY().toString(RADIX_HEX));
      hcertPublicKeyDTO.setBitLength(String.valueOf(x509ECDSATemp.getW().getAffineX().bitLength()));
      hcertPublicKeyDTO.setAlgo(x509ECDSATemp.getAlgorithm());
    }
    return hcertPublicKeyDTO;
  }

}
