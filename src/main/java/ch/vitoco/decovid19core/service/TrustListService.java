package ch.vitoco.decovid19core.service;

import static ch.vitoco.decovid19core.constants.ExceptionMessages.JSON_DESERIALIZE_EXCEPTION;
import static ch.vitoco.decovid19core.constants.ExceptionMessages.KEY_SPEC_EXCEPTION;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.*;
import java.time.Duration;

import ch.vitoco.decovid19core.model.certificates.EUCertificates;
import ch.vitoco.decovid19core.model.certificates.SwissActiveKeyIds;
import ch.vitoco.decovid19core.model.certificates.SwissCertificates;
import ch.vitoco.decovid19core.model.certificates.SwissRevokedCertificates;
import ch.vitoco.decovid19core.exception.ServerException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

/**
 * Service class TrustListService.
 */
@Service
public class TrustListService {

  private static final int MAX_IN_MEMORY_SIZE = 16 * 1024 * 1024;
  private static final int TIMEOUT_DURATION_IN_SECONDS = 20;

  private static final String SIGNATURE_ALGO_RSA = "RSA";
  private static final String SIGNATURE_ALGO_ECDSA = "EC";
  private static final String EC_PROVIDER = "SunEC";
  private static final String EC_DOMAIN_PARAM_NAME = "secp256r1";

  private static final String X509_CERT_TYPE = "X.509";
  private static final String PEM_PREFIX = "-----BEGIN CERTIFICATE-----";
  private static final String PEM_POSTFIX = "-----END CERTIFICATE-----";
  private static final String PUBLIC_KEY_PREFIX = "-----BEGIN PUBLIC KEY-----";
  private static final String PUBLIC_KEY_POSTFIX = "-----END PUBLIC KEY-----";

  private WebClient getWebclient(String baseUrl) {
    return WebClient.builder()
        .baseUrl(baseUrl)
        .exchangeStrategies(ExchangeStrategies.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE))
            .build())
        .build();
  }

  /**
   * Gets the public keys from the given endpoint.
   *
   * @param baseUrl the base URL for retrieving public keys as PEM formatted String
   * @return Public Key
   */
  public ResponseEntity<String> getPublicKey(String baseUrl) {
    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true)))
        .baseUrl(baseUrl)
        .build()
        .get()
        .retrieve()
        .toEntity(String.class)
        .blockOptional(Duration.ofSeconds(TIMEOUT_DURATION_IN_SECONDS))
        .orElseThrow();
  }

  /**
   * Gets the Health Certificates from the given endpoint.
   *
   * @param baseUrl the base URL for retrieving the Health Certificates
   * @return Health Certificates
   */
  public ResponseEntity<String> getHcertCertificates(String baseUrl) {
    return getWebclient(baseUrl).get()
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .toEntity(String.class)
        .blockOptional(Duration.ofSeconds(TIMEOUT_DURATION_IN_SECONDS))
        .orElseThrow();
  }

  /**
   * Gets the Health Certificates from the given endpoint.
   *
   * @param baseUrl the base URL for retrieving the Health Certificates
   * @param token   the Bearer Token for Authentication
   * @return Health Certificates
   */
  public ResponseEntity<String> getHcertCertificates(String baseUrl, String token) {
    return getWebclient(baseUrl).get()
        .headers(h -> h.setBearerAuth(token))
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .toEntity(String.class)
        .blockOptional(Duration.ofSeconds(TIMEOUT_DURATION_IN_SECONDS))
        .orElseThrow();
  }

  /**
   * Helper method for mapping the content of the Swiss Health Certificate to Java Object.
   *
   * @param certificates the Health Certificate content retrieved from the given endpoint
   * @return SwissCertificates
   */
  public SwissCertificates buildSwissHcertCertificates(String certificates) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(certificates, SwissCertificates.class);
    } catch (JsonProcessingException e) {
      throw new ServerException(JSON_DESERIALIZE_EXCEPTION, e);
    }
  }

  /**
   * Helper method for mapping the content of the Swiss Health Certificate Active Key IDs to Java Object.
   *
   * @param activeKeys the Active Key IDs content retrieved from the given endpoint
   * @return SwissActiveKeyIds
   */
  public SwissActiveKeyIds buildSwissHcertActiveKeyIds(String activeKeys) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(activeKeys, SwissActiveKeyIds.class);
    } catch (JsonProcessingException e) {
      throw new ServerException(JSON_DESERIALIZE_EXCEPTION, e);
    }
  }

  /**
   * Helper method for mapping the content of the Revoked Swiss Health Certificate to Java Object.
   *
   * @param revokedHcert the Revoked Health Certificates content retrieved from the given endpoint
   * @return SwissRevokedCertificates
   */
  public SwissRevokedCertificates buildSwissRevokedHcert(String revokedHcert) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(revokedHcert, SwissRevokedCertificates.class);
    } catch (JsonProcessingException e) {
      throw new ServerException(JSON_DESERIALIZE_EXCEPTION, e);
    }
  }

  /**
   * Helper method for mapping the content of the EU Health Certificate to Java Object.
   *
   * @param certificates the Health Certificate content retrieved from the given endpoint
   * @return EUCertificates
   */
  public EUCertificates buildEUHcertCertificates(String certificates) {
    try {
      String substring = certificates.substring(certificates.indexOf("{"));
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(substring, EUCertificates.class);
    } catch (JsonProcessingException e) {
      throw new ServerException(JSON_DESERIALIZE_EXCEPTION, e);
    }
  }

  /**
   * Converts a PEM formatted String to a X509Certificate.
   *
   * @param pem PEM formatted String
   * @return X509Certificate
   */
  public X509Certificate convertCertificateToX509(String pem) {
    String pemTmp = addCertificatePrefix(pem);
    try (InputStream inputStream = new ByteArrayInputStream(pemTmp.getBytes())) {
      CertificateFactory certFactory = CertificateFactory.getInstance(X509_CERT_TYPE);
      return (X509Certificate) certFactory.generateCertificate(inputStream);
    } catch (IOException | CertificateException e) {
      throw new ServerException(KEY_SPEC_EXCEPTION, e);
    }
  }

  /**
   * Reads a PublicKey.
   *
   * @param publicKey Base64 encoded Public Key as String
   * @param algorithm Algorithm as String e.g. RSA or ECDSA
   * @return PublicKey
   */
  public PublicKey getPublicKey(String publicKey, String algorithm) {
    String publicKeyTmp = addPublicKeyPrefix(publicKey);
    try (StringReader keyReader = new StringReader(publicKeyTmp); PemReader pemReader = new PemReader(keyReader)) {
      PemObject pemObject = pemReader.readPemObject();
      byte[] content = pemObject.getContent();
      X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
      KeyFactory factory = KeyFactory.getInstance(algorithm, new BouncyCastleProvider());
      return factory.generatePublic(pubKeySpec);
    } catch (Exception e) {
      throw new ServerException(KEY_SPEC_EXCEPTION, e);
    }
  }

  /**
   * Gets RSA Public Key from given params.
   *
   * @param modulus  RSA Public Key Modulus
   * @param exponent RSA Public Key Public Exponent
   * @return RSA PublicKey
   */
  public PublicKey getRSAPublicKey(BigInteger modulus, BigInteger exponent) {
    try {
      RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
      KeyFactory factory = KeyFactory.getInstance(SIGNATURE_ALGO_RSA);
      return factory.generatePublic(spec);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new ServerException(KEY_SPEC_EXCEPTION, e);
    }
  }

  /**
   * Gets EC Public Key from given params.
   *
   * @param publicXCoord EC Public Key Point X-Coordinate
   * @param publicYCoord EC Public Key Point Y-Coordinate
   * @return EC PublicKey
   */
  public PublicKey getECPublicKey(BigInteger publicXCoord, BigInteger publicYCoord) {
    try {
      ECPoint publicECPoint = new ECPoint(publicXCoord, publicYCoord);
      AlgorithmParameters parameters = AlgorithmParameters.getInstance(SIGNATURE_ALGO_ECDSA, EC_PROVIDER);
      parameters.init(new ECGenParameterSpec(EC_DOMAIN_PARAM_NAME));
      ECParameterSpec ecParameterSpec = parameters.getParameterSpec(ECParameterSpec.class);
      ECPublicKeySpec publicSpec = new ECPublicKeySpec(publicECPoint, ecParameterSpec);
      KeyFactory factory = KeyFactory.getInstance(SIGNATURE_ALGO_ECDSA);
      return factory.generatePublic(publicSpec);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException |
             InvalidParameterSpecException e) {
      throw new ServerException(KEY_SPEC_EXCEPTION, e);
    }
  }

  private String addPublicKeyPrefix(String publicKey) {
    if (publicKey.startsWith(PUBLIC_KEY_PREFIX)) {
      return publicKey;
    } else {
      return PUBLIC_KEY_PREFIX + "\n" + publicKey + "\n" + PUBLIC_KEY_POSTFIX;
    }
  }

  private String addCertificatePrefix(String pem) {
    if (pem.startsWith(PEM_PREFIX)) {
      return pem;
    } else {
      return PEM_PREFIX + "\n" + pem + "\n" + PEM_POSTFIX;
    }
  }

}
