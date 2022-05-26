package ch.vitoco.decovid19core.service;

import static ch.vitoco.decovid19core.constants.Const.JSON_DESERIALIZE_EXCEPTION;
import static ch.vitoco.decovid19core.constants.Const.KEY_SPEC_EXCEPTION;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;

import ch.vitoco.decovid19core.certificates.GermanCertificates;
import ch.vitoco.decovid19core.exception.JsonDeserializeException;
import ch.vitoco.decovid19core.exception.KeySpecsException;
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
 * Service class Decovid19TrustListService.
 */
@Service
public class Decovid19TrustListService {

  private static final int MAX_IN_MEMORY_SIZE = 16 * 1024 * 1024;
  private static final int TIMEOUT_DURATION_IN_SECONDS = 20;

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
   * Helper method for mapping the content of the Health Certificate to Java Object.
   *
   * @param certificates the Health Certificate content retrieved from the given endpoint
   * @return HcertCertificates
   */
  public GermanCertificates buildGermanHcertCertificates(String certificates) {
    try {
      String substring = certificates.substring(certificates.indexOf("{"));
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(substring, GermanCertificates.class);
    } catch (JsonProcessingException e) {
      throw new JsonDeserializeException(JSON_DESERIALIZE_EXCEPTION, e);
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
      throw new KeySpecsException(KEY_SPEC_EXCEPTION, e);
    }
  }

  /**
   * Reads a PublicKey.
   *
   * @param publicKey Base64 encoded Public Key as String
   * @param algorithm Algorithm as String e.g. RSA or ECDSA
   * @return PublicKey
   */
  public PublicKey readPublicKey(String publicKey, String algorithm) {
    String publicKeyTmp = addPublicKeyPrefix(publicKey);
    try (StringReader keyReader = new StringReader(publicKeyTmp); PemReader pemReader = new PemReader(keyReader)) {
      PemObject pemObject = pemReader.readPemObject();
      byte[] content = pemObject.getContent();
      X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
      KeyFactory factory = KeyFactory.getInstance(algorithm, new BouncyCastleProvider());
      return factory.generatePublic(pubKeySpec);
    } catch (Exception e) {
      throw new KeySpecsException(KEY_SPEC_EXCEPTION, e);
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
