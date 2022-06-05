package ch.vitoco.decovid19core.service;

import static ch.vitoco.decovid19core.constants.ExceptionMessages.INVALID_SIGNATURE;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

import ch.vitoco.decovid19core.constants.HcertEndpointsApi;
import ch.vitoco.decovid19core.enums.HcertAlgoKeys;
import ch.vitoco.decovid19core.enums.HcertCBORKeys;
import ch.vitoco.decovid19core.exception.ServerException;
import ch.vitoco.decovid19core.model.certificates.EUCertificate;
import ch.vitoco.decovid19core.model.certificates.EUCertificates;
import ch.vitoco.decovid19core.model.certificates.SwissCertificate;
import ch.vitoco.decovid19core.model.certificates.SwissCertificates;
import ch.vitoco.decovid19core.server.HcertVerificationServerRequest;
import ch.vitoco.decovid19core.server.HcertVerificationServerResponse;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.impl.ECDSA;
import com.upokecenter.cbor.CBORObject;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service class for the Health Certificate verification process.
 */
@Service
public class HcertVerificationService {

  private static final String CONTEXT_STRING = "Signature1";
  private static final byte[] EXTERNAL_DATA = new byte[0];
  private static final String SWISS_REGION = "SWISS";
  private static final String EU_REGION = "EU";
  private static final int SIG_NUM = 1;

  private final TrustListService trustListService;
  private final HcertDecodingService hcertDecodingService;

  public HcertVerificationService(TrustListService trustListService, HcertDecodingService hcertDecodingService) {
    this.trustListService = trustListService;
    this.hcertDecodingService = hcertDecodingService;
  }

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
        hcertVerificationServerResponse.setVerified(swissVerified);
      }
    } else {
      if (isKeyIdActive(hcertVerificationServerRequest, EU_REGION)) {
        boolean euVerified = isVerified(hcertVerificationServerRequest, EU_REGION);
        hcertVerificationServerResponse.setVerified(euVerified);
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
          ResponseEntity<String> certificates = trustListService.getHcertCertificates(HcertEndpointsApi.SWISS_CERTS_API,
              hcertVerificationServerRequest.getBearerToken());
          SwissCertificates swissCertificates = trustListService.buildSwissHcertCertificates(
              Objects.requireNonNull(certificates.getBody()));
          return swissCertificates.getCerts()
              .stream()
              .anyMatch(cert -> cert.getKeyId().equals(hcertVerificationServerRequest.getKeyId()));
        } else if (region.equals(EU_REGION)) {
          ResponseEntity<String> certificates = trustListService.getHcertCertificates(
              HcertEndpointsApi.GERMAN_CERTS_API);
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
    ResponseEntity<String> certificates = trustListService.getHcertCertificates(HcertEndpointsApi.GERMAN_CERTS_API);

    EUCertificates euCertificates = trustListService.buildEUHcertCertificates(
        Objects.requireNonNull(certificates.getBody()));

    EUCertificate euCertificate = euCertificates.getCertificates()
        .stream()
        .filter(cert -> cert.getKid().equals(hcertVerificationServerRequest.getKeyId()))
        .collect(Collectors.toList())
        .get(0);

    X509Certificate x509Certificate = trustListService.convertCertificateToX509(euCertificate.getRawData());
    return x509Certificate.getPublicKey();
  }

  private PublicKey getSwissPublicKey(HcertVerificationServerRequest hcertVerificationServerRequest, String algoName) {
    ResponseEntity<String> certificates = trustListService.getHcertCertificates(HcertEndpointsApi.SWISS_CERTS_API,
        hcertVerificationServerRequest.getBearerToken());
    SwissCertificates swissCertificates = trustListService.buildSwissHcertCertificates(certificates.getBody());

    SwissCertificate swissCertificate = swissCertificates.getCerts()
        .stream()
        .filter(cert -> cert.getKeyId().equals(hcertVerificationServerRequest.getKeyId()))
        .collect(Collectors.toList())
        .get(0);

    PublicKey publicKey;
    if (isECAlgorithm(algoName)) {
      BigInteger xCoord = new BigInteger(SIG_NUM, Base64.decode(swissCertificate.getX().getBytes()));
      BigInteger yCoord = new BigInteger(SIG_NUM, Base64.decode(swissCertificate.getY().getBytes()));
      publicKey = trustListService.getECPublicKey(xCoord, yCoord);
    } else {
      BigInteger modulus = new BigInteger(SIG_NUM, Base64.decode(swissCertificate.getN().getBytes()));
      BigInteger exponent = new BigInteger(SIG_NUM, Base64.decode(swissCertificate.getE().getBytes()));
      publicKey = trustListService.getRSAPublicKey(modulus, exponent);
    }
    return publicKey;
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
