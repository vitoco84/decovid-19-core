package ch.vitoco.decovid19core.service;

import static ch.vitoco.decovid19core.constants.Const.JSON_DESERIALIZE_EXCEPTION;

import java.time.Duration;

import ch.vitoco.decovid19core.exception.JsonDeserializeException;
import ch.vitoco.decovid19core.model.HcertCertificates;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
   * @param baseUrl the base URL for retrieving public keys
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
  public HcertCertificates buildHcertCertificates(String certificates) {
    try {
      String substring = certificates.substring(certificates.indexOf("{"));
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(substring, HcertCertificates.class);
    } catch (JsonProcessingException e) {
      throw new JsonDeserializeException(JSON_DESERIALIZE_EXCEPTION, e);
    }
  }

}
