package ch.vitoco.decovid19core.service;

import static ch.vitoco.decovid19core.constants.ExceptionMessages.CERTIFICATES_RETRIEVE_EXCEPTION;
import static ch.vitoco.decovid19core.constants.ExceptionMessages.INVALID_SIGNATURE;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

import ch.vitoco.decovid19core.config.ConfigProperties;
import ch.vitoco.decovid19core.enums.HcertAlgoKeys;
import ch.vitoco.decovid19core.enums.HcertCBORKeys;
import ch.vitoco.decovid19core.enums.HcertSignatureAlgoKeys;
import ch.vitoco.decovid19core.exception.ServerException;
import ch.vitoco.decovid19core.model.certificates.*;
import ch.vitoco.decovid19core.server.HcertVerificationServerRequest;
import ch.vitoco.decovid19core.server.HcertVerificationServerResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.impl.ECDSA;
import com.upokecenter.cbor.CBORObject;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service class for the Health Certificate verification process.
 */
@Service
@RequiredArgsConstructor
public class HcertVerificationService {

  private static final String CONTEXT_STRING = "Signature1";
  private static final byte[] EXTERNAL_DATA = new byte[0];
  private static final String SWISS_REGION = "SWISS";
  private static final String EU_REGION = "EU";
  private static final int SIG_NUM = 1;
  private static final int JWT_HEADER = 0;
  private static final String GERMAN_CERTS_REGEX_SPLITTER = "\n";
  private static final String SWISS_CERTS_REGEX_SPLITTER = "\\.";

  private final TrustListService trustListService;
  private final HcertDecodingService hcertDecodingService;
  private final ConfigProperties configProperties;


  /**
   * Gets the HcertVerificationServerResponse
   *
   * @param hcertVerificationServerRequest the HcertVerificationServerRequest
   * @return HcertVerificationServerResponse
   */
  public ResponseEntity<HcertVerificationServerResponse> verifyHealthCertificate(HcertVerificationServerRequest hcertVerificationServerRequest) {
    HcertVerificationServerResponse hcertVerificationServerResponse = new HcertVerificationServerResponse();
    if (!hcertVerificationServerRequest.getBearerToken().isBlank()) {
      if (isKeyIdActive(hcertVerificationServerRequest, SWISS_REGION)) {
        boolean swissVerified = isVerified(hcertVerificationServerRequest, SWISS_REGION);
        boolean trustChainVerified = isTrustChainVerified(hcertVerificationServerRequest, SWISS_REGION);
        hcertVerificationServerResponse.setHcertVerified(swissVerified);
        hcertVerificationServerResponse.setTrustChainVerified(trustChainVerified);
      }
    } else {
      if (isKeyIdActive(hcertVerificationServerRequest, EU_REGION)) {
        boolean euVerified = isVerified(hcertVerificationServerRequest, EU_REGION);
        boolean trustChainVerified = isTrustChainVerified(hcertVerificationServerRequest, EU_REGION);
        hcertVerificationServerResponse.setHcertVerified(euVerified);
        hcertVerificationServerResponse.setTrustChainVerified(trustChainVerified);
      }
    }
    return ResponseEntity.ok().body(hcertVerificationServerResponse);
  }

  private boolean isKeyIdActive(HcertVerificationServerRequest hcertVerificationServerRequest, String region) {
    if (hcertVerificationServerRequest.getKeyId().isBlank()) {
      return false;
    } else {
      try {
        if (region.equals(SWISS_REGION)) {
          ResponseEntity<String> certificates = trustListService.getHcertCertificates(
              configProperties.getSwissCertsApi(), hcertVerificationServerRequest.getBearerToken());
          SwissCertificates swissCertificates = trustListService.buildSwissHcertCertificates(
              Objects.requireNonNull(certificates.getBody()));
          return swissCertificates.getCerts()
              .stream()
              .anyMatch(cert -> cert.getKeyId().equals(hcertVerificationServerRequest.getKeyId()));
        } else if (region.equals(EU_REGION)) {
          ResponseEntity<String> certificates = trustListService.getHcertCertificates(
              configProperties.getGermanCertsApi());
          EUCertificates euCertificates = trustListService.buildEUHcertCertificates(
              Objects.requireNonNull(certificates.getBody()));
          return euCertificates.getCertificates()
              .stream()
              .anyMatch(cert -> cert.getKid().equals(hcertVerificationServerRequest.getKeyId()));
        } else {
          return false;
        }
      } catch (NoSuchElementException | ServerException e) {
        throw new ServerException(INVALID_SIGNATURE, e);
      }
    }
  }

  private boolean isTrustChainVerified(HcertVerificationServerRequest hcertVerificationServerRequest, String region) {
    boolean isTrustChainVerified = false;
    try {
      if (region.equals(SWISS_REGION)) {
        SwissJwtHeader swissJwtHeader = getSwissJwtHeader(hcertVerificationServerRequest);
        X509Certificate signedCert = trustListService.convertCertificateToX509(swissJwtHeader.getX5c().get(0));
        X509Certificate issuerCert = trustListService.convertCertificateToX509(swissJwtHeader.getX5c().get(1));
        X509Certificate swissRootCert = getSwissRootCert(hcertVerificationServerRequest);

        List<X509Certificate> trustChain = List.of(signedCert, issuerCert, swissRootCert);
        List<Boolean> trustChainVerified = isSwissTrustChainVerified(trustChain);
        isTrustChainVerified = trustChainVerified.stream().allMatch(trustChainVerified.get(0)::equals);
      }
      if (region.equals(EU_REGION)) {
        PublicKey publicKey = getGermanPublicKey();
        return isGermanTrustChainVerified(publicKey);
      }
    } catch (ServerException e) {
      return false;
    }
    return isTrustChainVerified;
  }

  private SwissJwtHeader getSwissJwtHeader(HcertVerificationServerRequest hcertVerificationServerRequest) {
    try {
      ResponseEntity<String> jwt = trustListService.getJWT(configProperties.getSwissCertsApi(),
          hcertVerificationServerRequest.getBearerToken());
      String[] split = Objects.requireNonNull(jwt.getBody()).split(SWISS_CERTS_REGEX_SPLITTER);
      String header = new String(Base64.decodeBase64(split[JWT_HEADER]));
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(header, SwissJwtHeader.class);
    } catch (NoSuchElementException | JsonProcessingException e) {
      throw new ServerException(CERTIFICATES_RETRIEVE_EXCEPTION, e);
    }
  }

  private X509Certificate getSwissRootCert(HcertVerificationServerRequest hcertVerificationServerRequest) {
    try {
      ResponseEntity<String> rootCertResponse = trustListService.getHcertCertificates(
          configProperties.getSwissRootCertApi(), hcertVerificationServerRequest.getBearerToken());
      return trustListService.convertCertificateToX509(rootCertResponse.getBody());
    } catch (NoSuchElementException e) {
      throw new ServerException(CERTIFICATES_RETRIEVE_EXCEPTION, e);
    }
  }

  private List<Boolean> isSwissTrustChainVerified(List<X509Certificate> trustChain) {
    List<Boolean> isTrustChainVerified = new ArrayList<>();
    try {
      for (int i = 0; i < trustChain.size() - 1; i++) {
        trustChain.get(i).verify(trustChain.get(i + 1).getPublicKey());
        isTrustChainVerified.add(true);
      }
      trustChain.get(2).verify(trustChain.get(2).getPublicKey());
      isTrustChainVerified.add(true);
    } catch (CertificateException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException |
             InvalidKeyException e) {
      isTrustChainVerified.add(false);
    }
    return isTrustChainVerified;
  }

  private PublicKey getGermanPublicKey() {
    try {
      ResponseEntity<String> publicKeyResponse = trustListService.getPublicKey(
          configProperties.getGermanPublicKeyApi());
      return trustListService.getPublicKey(publicKeyResponse.getBody(), HcertSignatureAlgoKeys.ECDSA.getName());
    } catch (NoSuchElementException e) {
      throw new ServerException(CERTIFICATES_RETRIEVE_EXCEPTION, e);
    }
  }

  private boolean isGermanTrustChainVerified(PublicKey publicKey) {
    try {
      ResponseEntity<String> certificates = trustListService.getHcertCertificates(configProperties.getGermanCertsApi());
      String[] split = Objects.requireNonNull(certificates.getBody()).split(GERMAN_CERTS_REGEX_SPLITTER);
      String signatureBase64encoded = split[0];
      String content = split[1];
      byte[] signatureBase64decoded = Base64.decodeBase64(signatureBase64encoded);
      byte[] signature = ECDSA.transcodeSignatureToDER(signatureBase64decoded);
      return verifyGermanTrustChain(publicKey, content, signature);
    } catch (NoSuchElementException | JOSEException e) {
      throw new ServerException(CERTIFICATES_RETRIEVE_EXCEPTION, e);
    }
  }

  private boolean verifyGermanTrustChain(PublicKey publicKey, String content, byte[] ecdsaSignature) {
    boolean isValid;
    try {
      Signature signature = Signature.getInstance(HcertAlgoKeys.ES256.getJcaAlgoName(), new BouncyCastleProvider());
      signature.initVerify(publicKey);
      signature.update(content.getBytes());
      signature.verify(ecdsaSignature);
      isValid = true;
    } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
      isValid = false;
    }
    return isValid;
  }

  private boolean isVerified(HcertVerificationServerRequest hcertVerificationServerRequest, String region) {
    CBORObject cborObject = hcertDecodingService.getCBORObject(hcertVerificationServerRequest.getHcertPrefix());
    String algo = hcertDecodingService.getAlgo(cborObject);
    byte[] content = cborObject.get(HcertCBORKeys.MESSAGE_CONTENT.getCborKey()).GetByteString();
    byte[] coseSignature = cborObject.get(HcertCBORKeys.SIGNATURE.getCborKey()).GetByteString();
    byte[] protectedHeader = cborObject.get(HcertCBORKeys.PROTECTED_HEADER.getCborKey()).GetByteString();
    byte[] signedData = getValidationData(protectedHeader, content);

    PublicKey publicKey = null;
    if (region.equals(SWISS_REGION)) {
      publicKey = getSwissPublicKey(hcertVerificationServerRequest, algo);

    }
    if (region.equals(EU_REGION)) {
      publicKey = getEUPublicKey(hcertVerificationServerRequest);
    }
    try {
      if (isECAlgorithm(algo)) {
        coseSignature = ECDSA.transcodeSignatureToDER(coseSignature);
      }
      String jcaAlgoName = hcertDecodingService.getJcaAlgo(algo);
      Signature signature = Signature.getInstance(jcaAlgoName, new BouncyCastleProvider());
      signature.initVerify(publicKey);
      signature.update(signedData);

      return signature.verify(coseSignature);
    } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException | JOSEException e) {
      return false;
    }
  }

  private PublicKey getEUPublicKey(HcertVerificationServerRequest hcertVerificationServerRequest) {
    try {
      ResponseEntity<String> certificates = trustListService.getHcertCertificates(configProperties.getGermanCertsApi());

      EUCertificates euCertificates = trustListService.buildEUHcertCertificates(
          Objects.requireNonNull(certificates.getBody()));

      EUCertificate euCertificate = euCertificates.getCertificates()
          .stream()
          .filter(cert -> cert.getKid().equals(hcertVerificationServerRequest.getKeyId()))
          .collect(Collectors.toList())
          .get(0);

      X509Certificate x509Certificate = trustListService.convertCertificateToX509(euCertificate.getRawData());
      return x509Certificate.getPublicKey();
    } catch (NoSuchElementException e) {
      throw new ServerException(CERTIFICATES_RETRIEVE_EXCEPTION, e);
    }
  }

  private PublicKey getSwissPublicKey(HcertVerificationServerRequest hcertVerificationServerRequest, String algoName) {
    try {
      ResponseEntity<String> certificates = trustListService.getHcertCertificates(configProperties.getSwissCertsApi(),
          hcertVerificationServerRequest.getBearerToken());
      SwissCertificates swissCertificates = trustListService.buildSwissHcertCertificates(certificates.getBody());

      SwissCertificate swissCertificate = swissCertificates.getCerts()
          .stream()
          .filter(cert -> cert.getKeyId().equals(hcertVerificationServerRequest.getKeyId()))
          .collect(Collectors.toList())
          .get(0);

      PublicKey publicKey;
      if (isECAlgorithm(algoName)) {
        BigInteger xCoord = new BigInteger(SIG_NUM, Base64.decodeBase64(swissCertificate.getX().getBytes()));
        BigInteger yCoord = new BigInteger(SIG_NUM, Base64.decodeBase64(swissCertificate.getY().getBytes()));
        publicKey = trustListService.getECPublicKey(xCoord, yCoord);
      } else {
        BigInteger modulus = new BigInteger(SIG_NUM, Base64.decodeBase64(swissCertificate.getN().getBytes()));
        BigInteger exponent = new BigInteger(SIG_NUM, Base64.decodeBase64(swissCertificate.getE().getBytes()));
        publicKey = trustListService.getRSAPublicKey(modulus, exponent);
      }
      return publicKey;
    } catch (NoSuchElementException e) {
      throw new ServerException(CERTIFICATES_RETRIEVE_EXCEPTION, e);
    }
  }

  private boolean isECAlgorithm(String algoName) {
    return algoName.equals(HcertAlgoKeys.ES256.getName()) || algoName.equals(HcertAlgoKeys.ES384.getName()) ||
        algoName.equals(HcertAlgoKeys.ES512.getName());
  }

  private byte[] getValidationData(byte[] protectedHeader, byte[] content) {
    CBORObject cborObject = CBORObject.NewArray();
    cborObject.Add(CONTEXT_STRING);
    cborObject.Add(protectedHeader);
    cborObject.Add(EXTERNAL_DATA);
    if (content != null) {
      cborObject.Add(content);
    } else {
      cborObject.Add(null);
    }
    return cborObject.EncodeToBytes();
  }

}
