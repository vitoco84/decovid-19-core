package ch.vitoco.decovid19core.service;

import static ch.vitoco.decovid19core.constants.ExceptionMessages.INVALID_SIGNATURE;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.impl.ECDSA;
import com.upokecenter.cbor.CBORObject;

import ch.vitoco.decovid19core.certificates.model.GermanCertificate;
import ch.vitoco.decovid19core.certificates.model.GermanCertificates;
import ch.vitoco.decovid19core.certificates.model.SwissActiveKeyIds;
import ch.vitoco.decovid19core.certificates.model.SwissCertificate;
import ch.vitoco.decovid19core.certificates.model.SwissCertificates;
import ch.vitoco.decovid19core.constants.HcertEndpointsApi;
import ch.vitoco.decovid19core.enums.HcertCBORKeys;
import ch.vitoco.decovid19core.exception.ServerException;
import ch.vitoco.decovid19core.server.HcertVerificationServerRequest;

/**
 * Service class for the Health Certificate verification process.
 */
@Service
public class HcertVerificationService {

  private static final String CONTEXT_STRING = "Signature1";
  private static final byte[] EXTERNAL_DATA = new byte[0];

  private final TrustListService trustListService;
  private final HcertDecodingService hcertDecodingService;

  public HcertVerificationService(TrustListService trustListService, HcertDecodingService hcertDecodingService) {
    this.trustListService = trustListService;
    this.hcertDecodingService = hcertDecodingService;
  }

  public ResponseEntity<String> verifyHealthCertificate(HcertVerificationServerRequest hcertVerificationServerRequest) {
    if (!hcertVerificationServerRequest.getBearerToken().isBlank()) {
      if (isSwissActiveKeyId(hcertVerificationServerRequest)) {

        String swissVerified = isSwissVerified(hcertVerificationServerRequest);

        return ResponseEntity.ok().body("isPresent");
      } else {
        return ResponseEntity.badRequest().build();
      }
    } else {
      if (isEUActiveKeyId(hcertVerificationServerRequest)) {

        String euVerified = isEUVerified(hcertVerificationServerRequest);

        return ResponseEntity.ok().body("isPresent");
      } else {
        return ResponseEntity.badRequest().build();
      }
    }
  }

  private boolean isSwissActiveKeyId(HcertVerificationServerRequest hcertVerificationServerRequest) {
    if (hcertVerificationServerRequest.getKeyId().isBlank()) {
      return false;
    } else {
      try {
        ResponseEntity<String> certificates = trustListService.getHcertCertificates(
            HcertEndpointsApi.SWISS_ACTIVE_KID_API, hcertVerificationServerRequest.getBearerToken());
        SwissActiveKeyIds swissActiveKeyIds = trustListService.buildSwissHcertActiveKeyIds(
            Objects.requireNonNull(certificates.getBody()));
        return swissActiveKeyIds.getActiveKeyIds().contains(hcertVerificationServerRequest.getKeyId());
      } catch (NoSuchElementException e) {
        throw new ServerException(INVALID_SIGNATURE, e);
      }
    }
  }

  private boolean isEUActiveKeyId(HcertVerificationServerRequest hcertVerificationServerRequest) {
    if (hcertVerificationServerRequest.getKeyId().isBlank()) {
      return false;
    } else {
      ResponseEntity<String> certificates = trustListService.getHcertCertificates(
          HcertEndpointsApi.GERMAN_TEST_CERTS_API);
      GermanCertificates germanCertificates = trustListService.buildGermanHcertCertificates(
          Objects.requireNonNull(certificates.getBody()));
      return germanCertificates.getCertificates()
          .stream()
          .anyMatch(cert -> cert.getKid().equals(hcertVerificationServerRequest.getKeyId()));
    }
  }

  private String isSwissVerified(HcertVerificationServerRequest hcertVerificationServerRequest) {
    ResponseEntity<String> certificates = trustListService.getHcertCertificates(HcertEndpointsApi.SWISS_CERTS_API,
        hcertVerificationServerRequest.getBearerToken());
    SwissCertificates swissCertificates = trustListService.buildSwissHcertCertificates(certificates.getBody());

    SwissCertificate swissCertificate = swissCertificates.getCerts()
        .stream()
        .filter(cert -> cert.getKeyId().equals(hcertVerificationServerRequest.getKeyId()))
        .collect(Collectors.toList())
        .get(0);

    BigInteger modulus = new BigInteger(1, Base64.decode(swissCertificate.getN().getBytes()));
    BigInteger exponent = new BigInteger(1, Base64.decode(swissCertificate.getE().getBytes()));

//    PublicKey publicKey = trustListService.getSwissRSAPublicKey(swissCertificate.getN(), swissCertificate.getE());
    PublicKey publicKey = trustListService.getRSAPublicKey(modulus, exponent);

    CBORObject cborObject = hcertDecodingService.getCBORObject(hcertVerificationServerRequest.getHcertPrefix());
    String algo = hcertDecodingService.getAlgo(cborObject);
    byte[] content = cborObject.get(HcertCBORKeys.MESSAGE_CONTENT.getCborKey()).GetByteString();
    byte[] coseSignature = cborObject.get(HcertCBORKeys.SIGNATUR.getCborKey()).GetByteString();

    byte[] protectedHeader = cborObject.get(HcertCBORKeys.PROTECTED_HEADER.getCborKey()).GetByteString();

    byte[] signedData = getValidationData(protectedHeader, content);

    try {

      //      byte[] signatureToVerify = ECDSA.transcodeSignatureToDER(coseSignature.getBytes());

      Signature signature = Signature.getInstance("SHA256withRSA/PSS", new BouncyCastleProvider());
      signature.initVerify(publicKey);
      signature.update(signedData);


      if (signature.verify(coseSignature)) {
        return "Verified";
      } else {
        return "Not verified";
      }

    } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
      throw new ServerException("Invalid", e);
    }

  }

  private String isEUVerified(HcertVerificationServerRequest hcertVerificationServerRequest) {

    ResponseEntity<String> certificates = trustListService.getHcertCertificates(
        HcertEndpointsApi.GERMAN_TEST_CERTS_API);

    GermanCertificates germanCertificates = trustListService.buildGermanHcertCertificates(
        Objects.requireNonNull(certificates.getBody()));

    GermanCertificate germanCertificate = germanCertificates.getCertificates()
        .stream()
        .filter(cert -> cert.getKid().equals(hcertVerificationServerRequest.getKeyId()))
        .collect(Collectors.toList())
        .get(0);

    X509Certificate x509Certificate = trustListService.convertCertificateToX509(germanCertificate.getRawData());
    PublicKey publicKey = x509Certificate.getPublicKey();

    CBORObject cborObject = hcertDecodingService.getCBORObject(hcertVerificationServerRequest.getHcertPrefix());
    String algo = hcertDecodingService.getAlgo(cborObject);
    byte[] content = cborObject.get(HcertCBORKeys.MESSAGE_CONTENT.getCborKey()).GetByteString();
    byte[] coseSignature = cborObject.get(HcertCBORKeys.SIGNATUR.getCborKey()).GetByteString();

    byte[] protectedHeader = cborObject.get(HcertCBORKeys.PROTECTED_HEADER.getCborKey()).GetByteString();
    byte[] signedData = getValidationData(protectedHeader, content);

    try {

      byte[] signatureToVerify = ECDSA.transcodeSignatureToDER(coseSignature);

      Signature signature = Signature.getInstance("SHA256withECDSA");
      signature.initVerify(publicKey);
      signature.update(signedData);


      if (signature.verify(signatureToVerify)) {
        return "Verified";
      } else {
        return "Not verified";
      }

    } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException | JOSEException e) {
      throw new ServerException("Invalid", e);
    }

  }


  private byte[] getValidationData(byte[] protectedHeader, byte[] content) {
    return CBORObject.NewArray()
        .Add(CONTEXT_STRING)
        .Add(protectedHeader)
        .Add(EXTERNAL_DATA)
        .Add(content)
        .EncodeToBytes();
  }

}
