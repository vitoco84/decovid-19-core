package ch.vitoco.decovid19core.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import ch.vitoco.decovid19core.constants.Endpoints;
import ch.vitoco.decovid19core.model.HcertCertificate;
import ch.vitoco.decovid19core.model.HcertCertificates;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class Decovid19TrustListServiceTest {

  private static final Path FAKE_CERTIFICATES_RESPONSE_BODY = Paths.get(
      "src/test/resources/fakeCertificateResponseBody.txt");
  private static final Path FAKE_PUBLIC_KEY_RESPONSE_BODY = Paths.get(
      "src/test/resources/fakePublicKeyResponseBody.txt");

  private final Decovid19TrustListService decovid19TrustListService = new Decovid19TrustListService();
  private final MockWebServer mockWebServer = new MockWebServer();

  @BeforeEach
  void setupMockWebServer() throws IOException {
    mockWebServer.start();
  }

  @Disabled("Only used for local testing and verification. MockWebServer is used instead, see Test shouldRetrieveCertificatesWithMockWebServer().")
  @Test
  void shouldRetrieveCertificates() {
    ResponseEntity<String> certificates = decovid19TrustListService.getHcertCertificates(Endpoints.DOCUMENT_CERTS);

    HcertCertificates hcertCertificates = decovid19TrustListService.buildHcertCertificates(
        Objects.requireNonNull(certificates.getBody()));
    List<HcertCertificate> certificatesList = hcertCertificates.getCertificates();

    assertEquals("DSC", certificatesList.get(0).getCertificateType());
    assertFalse(certificatesList.isEmpty());
  }

  @Disabled("Only used for local testing and verification. MockWebServer is used instead, see Test shouldRetrievePublicKeyWithMockWebServer().")
  @Test
  void shouldRetrievePublicKey() {
    ResponseEntity<String> publicKeyTest = decovid19TrustListService.getPublicKey(Endpoints.DOCUMENT_PUBLIC_KEY);

    assertEquals(HttpStatus.OK, publicKeyTest.getStatusCode());
    assertTrue(Objects.requireNonNull(publicKeyTest.getBody()).startsWith("-----BEGIN PUBLIC KEY-----"));
    assertTrue(Objects.requireNonNull(publicKeyTest.getBody()).endsWith("-----END PUBLIC KEY-----\n"));
  }

  @Test
  void shouldRetrieveCertificatesWithMockWebServer() throws IOException {
    String body = Files.readString(FAKE_CERTIFICATES_RESPONSE_BODY).replace("\r\n", "");

    mockWebServer.enqueue(new MockResponse().setBody(body));

    HttpUrl baseUrl = mockWebServer.url("/decovid/certs/");

    ResponseEntity<String> certificates = decovid19TrustListService.getHcertCertificates(String.valueOf(baseUrl));

    HcertCertificates hcertCertificates = decovid19TrustListService.buildHcertCertificates(
        Objects.requireNonNull(certificates.getBody()));
    List<HcertCertificate> certificatesList = hcertCertificates.getCertificates();
    HcertCertificate hcertCertificate = certificatesList.get(0);

    assertEquals("DSC", hcertCertificate.getCertificateType());
    assertEquals("DE", hcertCertificate.getCountry());
    assertEquals("kid", hcertCertificate.getKid());
    assertEquals("rawData", hcertCertificate.getRawData());
    assertEquals("signature", hcertCertificate.getSignature());
    assertEquals("thumbprint", hcertCertificate.getThumbprint());
    assertEquals("timestamp", hcertCertificate.getTimestamp());
    assertEquals(3, certificatesList.size());
  }

  @Test
  void shouldRetrievePublicKeyWithMockWebServer() throws IOException {
    String body = Files.readString(FAKE_PUBLIC_KEY_RESPONSE_BODY).replace("\r\n", "");

    mockWebServer.enqueue(new MockResponse().setBody(body));

    HttpUrl baseUrl = mockWebServer.url("/decovid/pubKey/");

    ResponseEntity<String> publicKey = decovid19TrustListService.getPublicKey(String.valueOf(baseUrl));

    assertEquals(HttpStatus.OK, publicKey.getStatusCode());
    assertTrue(Objects.requireNonNull(publicKey.getBody()).startsWith("-----BEGIN PUBLIC KEY-----"));
  }

  @AfterEach
  void cleanUpMockWebServer() throws IOException {
    mockWebServer.shutdown();
  }

}
