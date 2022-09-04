package ch.vitoco.decovid19core.service;

import static ch.vitoco.decovid19core.constants.ExceptionMessages.*;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;

import ch.vitoco.decovid19core.enums.HcertSignatureAlgoKeys;
import ch.vitoco.decovid19core.exception.ServerException;
import ch.vitoco.decovid19core.model.hcert.HcertContentDTO;
import ch.vitoco.decovid19core.model.hcert.HcertDTO;
import ch.vitoco.decovid19core.model.hcert.HcertPublicKeyDTO;
import ch.vitoco.decovid19core.server.HcertServerRequest;
import ch.vitoco.decovid19core.server.HcertServerResponse;
import ch.vitoco.decovid19core.server.PEMCertServerRequest;
import ch.vitoco.decovid19core.server.PEMCertServerResponse;
import ch.vitoco.decovid19core.utils.HcertFileUtils;
import ch.vitoco.decovid19core.utils.HcertStringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upokecenter.cbor.CBORObject;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service class Decovid19DecoderService.
 */
@Service
@RequiredArgsConstructor
public class HcertService {

  private static final Logger LOGGER = LoggerFactory.getLogger(HcertService.class);

  private static final String HCERT_HEADER = "HC1:";
  private static final int RADIX_HEX = 16;

  private final ValueSetService valueSetService;
  private final HcertDecodingService hcertDecodingService;
  private final TrustListService trustListService;


  /**
   * Gets the HcertServerResponse.
   *
   * @param imageFile the Health Certificate QR-Code image file
   * @return HcertServerResponse
   */
  public ResponseEntity<HcertServerResponse> decodeHealthCertificateContent(MultipartFile imageFile) {
    checkMaxFileSize(imageFile);
    if (HcertFileUtils.isFileAllowed(imageFile)) {
      try (InputStream imageFileInputStream = imageFile.getInputStream()) {
        String hcertContent = hcertDecodingService.getHealthCertificateContent(imageFileInputStream);
        return getHcertServerResponseResponseEntity(hcertContent);
      } catch (IOException e) {
        throw new ServerException(QR_CODE_DECODE_EXCEPTION, e);
      }
    } else {
      String originalFilename = HcertStringUtils.sanitizeUserInputString(imageFile);
      LOGGER.info("Bad Request file not supported: {}", originalFilename);
      return ResponseEntity.badRequest().build();
    }
  }

  private void checkMaxFileSize(MultipartFile imageFile) {
    try {
      HcertFileUtils.checkMaxFileSize(imageFile);
    } catch (MaxUploadSizeExceededException e) {
      throw new ServerException(MAX_FILE_SIZE_EXCEEDED, e);
    }
  }

  /**
   * Gets the HcertServerResponse.
   *
   * @param hcertPrefix the HcertServerRequest with the Health Certificate Prefix
   * @return HcertServerResponse
   */
  public ResponseEntity<HcertServerResponse> decodeHealthCertificateContent(HcertServerRequest hcertPrefix) {
    if (!hcertPrefix.getHcertPrefix().isBlank() && hcertPrefix.getHcertPrefix().startsWith(HCERT_HEADER)) {
      String hcertContent = hcertPrefix.getHcertPrefix();
      return getHcertServerResponseResponseEntity(hcertContent);
    } else {
      LOGGER.info("Bad Request invalid Hcert Prefix provided");
      return ResponseEntity.badRequest().build();
    }
  }

  private ResponseEntity<HcertServerResponse> getHcertServerResponseResponseEntity(String hcertContent) {
    HcertServerResponse hcertResponse = buildHcertResponse(hcertDecodingService, hcertContent);
    LOGGER.info("Digital Health Certificate decoded");
    return ResponseEntity.ok().body(hcertResponse);
  }

  private HcertServerResponse buildHcertResponse(HcertDecodingService hcertDecodingService, String hcertContent) {
    HcertServerResponse hcertResponse = new HcertServerResponse();
    CBORObject cborObject = hcertDecodingService.getCBORObject(hcertContent);
    hcertResponse.setHcertPrefix(hcertContent);
    hcertResponse.setHcertContent(getHcertDTO(cborObject));
    hcertResponse.setHcertKID(hcertDecodingService.getKID(cborObject));
    hcertResponse.setHcertAlgo(hcertDecodingService.getJcaAlgo(getAlgoName(hcertDecodingService, cborObject)));
    hcertResponse.setHcertIssuer(hcertDecodingService.getIssuer(cborObject));
    hcertResponse.setHcertTimeStamp(hcertDecodingService.getHcertTimeStamp(cborObject));
    hcertResponse.setHcertSignature(hcertDecodingService.getSignature(cborObject));
    return hcertResponse;
  }

  private String getAlgoName(HcertDecodingService hcertDecodingService, CBORObject cborObject) {
    return hcertDecodingService.getAlgo(cborObject);
  }

  private HcertDTO getHcertDTO(CBORObject cborObject) {
    String jsonPayloadFromCBORMessage = hcertDecodingService.getContent(cborObject);
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
    valueSetService.mappingVaccinationValueSet(hcertContentDTO.getVaccination());
    valueSetService.mappingTestValueSet(hcertContentDTO.getTest());
    valueSetService.mappingRecoveryValueSet(hcertContentDTO.getRecovery());
    return hcertContentDTO;
  }

  /**
   * Gets the PEMCertServerResponse.
   *
   * @param pemCertificate the PEMCertServerRequest with the Certificate as String.
   * @return PEMCertServerResponse
   */
  public ResponseEntity<PEMCertServerResponse> decodeX509Certificate(PEMCertServerRequest pemCertificate) {
    try {
      X509Certificate x509Certificate = trustListService.convertCertificateToX509(pemCertificate.getPemCertificate());
      PEMCertServerResponse pemCertServerResponse = buildPEMCertServerResponse(x509Certificate);
      LOGGER.info("PEM Certificate decoded");
      return ResponseEntity.ok().body(pemCertServerResponse);
    } catch (ServerException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  private PEMCertServerResponse buildPEMCertServerResponse(X509Certificate x509Certificate) {
    PEMCertServerResponse pemCertServerResponse = new PEMCertServerResponse();
    pemCertServerResponse.setPublicKey(Base64.encodeBase64String(x509Certificate.getPublicKey().getEncoded()));
    pemCertServerResponse.setSubject(x509Certificate.getSubjectDN().getName());
    pemCertServerResponse.setSignatureAlgorithm(x509Certificate.getSigAlgName());
    pemCertServerResponse.setValidTo(x509Certificate.getNotAfter().toInstant().toString());
    pemCertServerResponse.setValidFrom(x509Certificate.getNotBefore().toInstant().toString());
    pemCertServerResponse.setSerialNumber(x509Certificate.getSerialNumber().toString(RADIX_HEX));
    pemCertServerResponse.setIssuer(x509Certificate.getIssuerDN().getName());
    pemCertServerResponse.setPublicKeyParams(buildPublicKeyResponse(x509Certificate));
    pemCertServerResponse.setSignature(Base64.encodeBase64String(x509Certificate.getSignature()));
    pemCertServerResponse.setValid(checkPEMCertValidity(x509Certificate));
    return pemCertServerResponse;
  }

  private boolean checkPEMCertValidity(X509Certificate x509Certificate) {
    try {
      x509Certificate.checkValidity();
      return true;
    } catch (CertificateExpiredException | CertificateNotYetValidException e) {
      return false;
    }
  }

  private HcertPublicKeyDTO buildPublicKeyResponse(X509Certificate x509Certificate) {
    HcertPublicKeyDTO hcertPublicKeyDTO = new HcertPublicKeyDTO();
    if (x509Certificate.getPublicKey().getAlgorithm().equals(HcertSignatureAlgoKeys.RSA.getName())) {
      RSAPublicKey x509RSATemp = (RSAPublicKey) x509Certificate.getPublicKey();
      hcertPublicKeyDTO.setPublicExponent(x509RSATemp.getPublicExponent().toString(RADIX_HEX));
      hcertPublicKeyDTO.setModulus(x509RSATemp.getModulus().toString(RADIX_HEX));
      hcertPublicKeyDTO.setBitLength(String.valueOf(x509RSATemp.getModulus().bitLength()));
      hcertPublicKeyDTO.setAlgo(x509RSATemp.getAlgorithm());
    }
    if (x509Certificate.getPublicKey().getAlgorithm().equals(HcertSignatureAlgoKeys.EC.getName())) {
      ECPublicKey x509ECDSATemp = (ECPublicKey) x509Certificate.getPublicKey();
      hcertPublicKeyDTO.setPublicXCoord(x509ECDSATemp.getW().getAffineX().toString(RADIX_HEX));
      hcertPublicKeyDTO.setPublicYCoord(x509ECDSATemp.getW().getAffineY().toString(RADIX_HEX));
      hcertPublicKeyDTO.setBitLength(String.valueOf(x509ECDSATemp.getW().getAffineX().bitLength()));
      hcertPublicKeyDTO.setAlgo(x509ECDSATemp.getAlgorithm());
    }
    return hcertPublicKeyDTO;
  }

}
