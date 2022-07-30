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

import ch.vitoco.decovid19core.enums.HcertSignatureAlgoKeys;
import ch.vitoco.decovid19core.exception.ServerException;
import ch.vitoco.decovid19core.model.certificates.EUCertificates;
import ch.vitoco.decovid19core.model.certificates.SwissActiveKeyIds;
import ch.vitoco.decovid19core.model.certificates.SwissCertificates;
import ch.vitoco.decovid19core.model.certificates.SwissRevokedCertificates;
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

  private static final String EC_PROVIDER = "SunEC";
  private static final String EC_DOMAIN_PARAM_NAME = "secp256r1";

  private static final String X509_CERT_TYPE = "X.509";
  private static final String PEM_PREFIX = "-----BEGIN CERTIFICATE-----";
  private static final String PEM_POSTFIX = "-----END CERTIFICATE-----";
  private static final String PUBLIC_KEY_PREFIX = "-----BEGIN PUBLIC KEY-----";
  private static final String PUBLIC_KEY_POSTFIX = "-----END PUBLIC KEY-----";

  private static final String ACCEPT = "Accept";
  private static final String APPLICATION_JSON_JWS = "application/json+jws";
  private static final String ACCEPT_ENCODING = "Accept-Encoding";
  private static final String APPLICATION_GZIP = "application/gzip";
  private static final String REGEX_REPLACE_NEW_LINES = "\r\n";
  private static final String REGEX_NEW_LINES = "\n";

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
   * Gets the JWT from the given endpoint.
   *
   * @param baseUrl the base URL for retrieving the JWT
   * @param token   the Bearer Token for Authentication
   * @return JWT
   */
  public ResponseEntity<String> getJWT(String baseUrl, String token) {
    return getWebclient(baseUrl).get()
        .headers(h -> h.setBearerAuth(token))
        .header(ACCEPT, APPLICATION_JSON_JWS)
        .header(ACCEPT_ENCODING, APPLICATION_GZIP)
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
      KeyFactory factory = KeyFactory.getInstance(HcertSignatureAlgoKeys.RSA.getName());
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
      AlgorithmParameters parameters = AlgorithmParameters.getInstance(HcertSignatureAlgoKeys.EC.getName(),
          EC_PROVIDER);
      parameters.init(new ECGenParameterSpec(EC_DOMAIN_PARAM_NAME));
      ECParameterSpec ecParameterSpec = parameters.getParameterSpec(ECParameterSpec.class);
      ECPublicKeySpec publicSpec = new ECPublicKeySpec(publicECPoint, ecParameterSpec);
      KeyFactory factory = KeyFactory.getInstance(HcertSignatureAlgoKeys.EC.getName());
      return factory.generatePublic(publicSpec);
    } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException |
             InvalidParameterSpecException e) {
      throw new ServerException(KEY_SPEC_EXCEPTION, e);
    }
  }

  private String addPublicKeyPrefix(String publicKey) {
    publicKey = publicKey.replace(REGEX_REPLACE_NEW_LINES, "");
    if (publicKey.startsWith(PUBLIC_KEY_PREFIX)) {
      publicKey = publicKey.replace(PUBLIC_KEY_PREFIX, "").replace(PUBLIC_KEY_POSTFIX, "");
    }
    return PUBLIC_KEY_PREFIX + REGEX_NEW_LINES + publicKey + REGEX_NEW_LINES + PUBLIC_KEY_POSTFIX;
  }

  private String addCertificatePrefix(String pem) {
    pem = pem.replace(REGEX_REPLACE_NEW_LINES, "");
    if (pem.startsWith(PEM_PREFIX)) {
      pem = pem.replace(PEM_PREFIX, "").replace(PEM_POSTFIX, "");
    }
    return PEM_PREFIX + REGEX_NEW_LINES + pem + REGEX_NEW_LINES + PEM_POSTFIX;
  }

}
