package ch.vitoco.decovid19core.service;

import static ch.vitoco.decovid19core.constants.ExceptionMessages.KEY_SPEC_EXCEPTION;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Objects;

import ch.vitoco.decovid19core.constants.HcertEndpointsApi;
import ch.vitoco.decovid19core.exception.ServerException;
import ch.vitoco.decovid19core.model.certificates.*;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class TrustListServiceTest {

  private static final Path FAKE_GERMAN_CERTIFICATES_RESPONSE_BODY = Paths.get(
      "src/test/resources/fakeGermanCertificateResponseBody.txt");
  private static final Path FAKE_GERMAN_PUBLIC_KEY_RESPONSE_BODY = Paths.get(
      "src/test/resources/fakeGermanPublicKeyResponseBody.txt");
  private static final Path FAKE_SWISS_CERTIFICATES_RESPONSE_BODY = Paths.get(
      "src/test/resources/fakeSwissCertificateResponseBody.txt");
  private static final Path FAKE_SWISS_ACTIVE_KEY_IDS_RESPONSE_BODY = Paths.get(
      "src/test/resources/fakeSwissActiveKeyIdsResponseBody.txt");
  private static final Path FAKE_SWISS_REVOKED_CERTIFICATES_RESPONSE_BODY = Paths.get(
      "src/test/resources/fakeSwissRevokedCertificatesResponseBody.txt");

  private static final String SWISS_QR_CODE_CERTIFICATE = "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";
  private static final String SWISS_QR_CODE_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4uZO4/7tneZ3XD5OAiTyoANOohQZC+DzZ4YC0AoLnEO+Z3PcTialCuRKS1zHfujNPI0GGG09DRVVXdv+tcKNXFDt/nRU1zlWDGFf4/63l5RIjkWFD3JFKqR8IlcJjrYYxstuZs3May3SGQJ+kZaeH5GFZMRvE0waHqMxbfwakvjf8qyBXCrZ1WsK+XJf7iYbJS2dO1a5HnegxPuRA7Zz8ikO7QRzmSongqOlkejEaIkFjx7gLGTUsOrBPYa5sdZqinDwmnjtKi52HLWarMXs+t1MN4etIp7GE7/zarjBNxk1Efiiwl+RdcwJ2uVwfrgzxfv3/TekZF8IUykV2Geu3QIDAQAB";
  private static final String SWISS_PUBLIC_KEY_PARAMS_MODULUS = "e2e64ee3feed9de6775c3e4e0224f2a0034ea214190be0f3678602d00a0b9c43be6773dc4e26a50ae44a4b5cc77ee8cd3c8d06186d3d0d15555ddbfeb5c28d5c50edfe7454d739560c615fe3feb79794488e45850f72452aa47c2257098eb618c6cb6e66cdcc6b2dd219027e91969e1f918564c46f134c1a1ea3316dfc1a92f8dff2ac815c2ad9d56b0af9725fee261b252d9d3b56b91e77a0c4fb9103b673f2290eed0473992a2782a3a591e8c46889058f1ee02c64d4b0eac13d86b9b1d66a8a70f09a78ed2a2e761cb59aacc5ecfadd4c3787ad229ec613bff36ab8c137193511f8a2c25f9175cc09dae5707eb833c5fbf7fd37a4645f08532915d867aedd";
  private static final String SWISS_PUBLIC_KEY_PARAMS_EXPONENT = "010001";

  private static final String GERMAN_QR_CODE_CERTIFICATE = "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";
  private static final String GERMAN_QR_CODE_PUBLIC_KEY = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE8j47QegkiZmEmzSvBpKiSNR/W+ymw5N3nsuur/22f9pLns9dzXz2nIFNVW1vE2S2zg5KCHQF0V+aX+mARNCa9A==";
  private static final String GERMAN_PUBLIC_KEY_PARAMS_X_COORD = "f23e3b41e8248999849b34af0692a248d47f5beca6c393779ecbaeaffdb67fda";
  private static final String GERMAN_PUBLIC_KEY_PARAMS_Y_COORD = "4b9ecf5dcd7cf69c814d556d6f1364b6ce0e4a087405d15f9a5fe98044d09af4";

  private static final String AUSTRIAN_QR_CODE_CERTIFICATE = "MIIBvTCCAWOgAwIBAgIKAXk8i88OleLsuTAKBggqhkjOPQQDAjA2MRYwFAYDVQQDDA1BVCBER0MgQ1NDQSAxMQswCQYDVQQGEwJBVDEPMA0GA1UECgwGQk1TR1BLMB4XDTIxMDUwNTEyNDEwNloXDTIzMDUwNTEyNDEwNlowPTERMA8GA1UEAwwIQVQgRFNDIDExCzAJBgNVBAYTAkFUMQ8wDQYDVQQKDAZCTVNHUEsxCjAIBgNVBAUTATEwWTATBgcqhkjOPQIBBggqhkjOPQMBBwNCAASt1Vz1rRuW1HqObUE9MDe7RzIk1gq4XW5GTyHuHTj5cFEn2Rge37+hINfCZZcozpwQKdyaporPUP1TE7UWl0F3o1IwUDAOBgNVHQ8BAf8EBAMCB4AwHQYDVR0OBBYEFO49y1ISb6cvXshLcp8UUp9VoGLQMB8GA1UdIwQYMBaAFP7JKEOflGEvef2iMdtopsetwGGeMAoGCCqGSM49BAMCA0gAMEUCIQDG2opotWG8tJXN84ZZqT6wUBz9KF8D+z9NukYvnUEQ3QIgdBLFSTSiDt0UJaDF6St2bkUQuVHW6fQbONd731/M4nc=";
  private static final String AUSTRIAN_QR_CODE_PUBLIC_KEY = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAErdVc9a0bltR6jm1BPTA3u0cyJNYKuF1uRk8h7h04+XBRJ9kYHt+/oSDXwmWXKM6cECncmqaKz1D9UxO1FpdBdw==";
  private static final String AUSTRIAN_PUBLIC_KEY_PARAMS_X_COORD = "add55cf5ad1b96d47a8e6d413d3037bb473224d60ab85d6e464f21ee1d38f970";
  private static final String AUSTRIAN_PUBLIC_KEY_PARAMS_Y_COORD = "5127d9181edfbfa120d7c2659728ce9c1029dc9aa68acf50fd5313b516974177";

  private static final String EC_CURVE = "P-256";
  private static final String SIGNATURE = "sig";
  private static final String ALGO_RSA = "RSA";
  private static final String ALGO_ECDSA = "ECDSA";
  private static final String SIGNATURE_ALGO_RSA_SSA_PSS = "RSASSA-PSS";
  private static final String SIGNATURE_ALGO_SHA256_WITH_RSA = "SHA256withRSA";
  private static final String SIGNATURE_ALGO_SHA256_WITH_ECDSA = "SHA256withECDSA";

  private static final String PUBLIC_KEY_PREFIX = "-----BEGIN PUBLIC KEY-----";
  private static final String PUBLIC_KEY_POSTFIX = "-----END PUBLIC KEY-----";

  private static final String BEARER_TOKEN = "token";
  private static final String TARGET_REPLACE_TXT = "\r\n";

  private static final int RADIX_HEX = 16;

  private final TrustListService trustListService = new TrustListService();


  @Disabled("Only used for local testing and verification. MockWebServer is used instead, see Test shouldRetrieveGermanCertificatesWithMockWebServer().")
  @Test
  void shouldRetrieveGermanCertificates() {
    ResponseEntity<String> certificates = trustListService.getHcertCertificates(HcertEndpointsApi.GERMAN_CERTS_API);

    EUCertificates euCertificates = trustListService.buildEUHcertCertificates(
        Objects.requireNonNull(certificates.getBody()));
    List<EUCertificate> certificatesList = euCertificates.getCertificates();

    assertEquals("DSC", certificatesList.get(0).getCertificateType());
    assertFalse(certificatesList.isEmpty());
  }

  @Disabled("Only used for local testing and verification. MockWebServer is used instead, see Test shouldRetrieveGermanPublicKeyWithMockWebServer().")
  @Test
  void shouldRetrieveGermanPublicKey() {
    ResponseEntity<String> publicKeyTest = trustListService.getPublicKey(HcertEndpointsApi.GERMAN_PUBLIC_KEY_API);

    assertEquals(HttpStatus.OK, publicKeyTest.getStatusCode());
    assertTrue(Objects.requireNonNull(publicKeyTest.getBody()).startsWith(PUBLIC_KEY_PREFIX));
    assertTrue(Objects.requireNonNull(publicKeyTest.getBody()).endsWith(PUBLIC_KEY_POSTFIX + "\n"));
  }

  @Test
  void shouldRetrieveGermanCertificatesWithMockWebServer() throws IOException {
    String body = Files.readString(FAKE_GERMAN_CERTIFICATES_RESPONSE_BODY).replace(TARGET_REPLACE_TXT, "");

    try (MockWebServer mockWebServer = new MockWebServer()) {
      mockWebServer.start();
      mockWebServer.enqueue(new MockResponse().setBody(body));

      HttpUrl baseUrl = mockWebServer.url("/decovid/certs/");

      ResponseEntity<String> certificates = trustListService.getHcertCertificates(String.valueOf(baseUrl));

      EUCertificates euCertificates = trustListService.buildEUHcertCertificates(
          Objects.requireNonNull(certificates.getBody()));
      List<EUCertificate> certificatesList = euCertificates.getCertificates();
      EUCertificate euCertificate = certificatesList.get(0);

      assertEquals("DSC", euCertificate.getCertificateType());
      assertEquals("DE", euCertificate.getCountry());
      assertEquals("kid", euCertificate.getKid());
      assertEquals("rawData", euCertificate.getRawData());
      assertEquals("signature", euCertificate.getSignature());
      assertEquals("thumbprint", euCertificate.getThumbprint());
      assertEquals("timestamp", euCertificate.getTimestamp());
      assertEquals(3, certificatesList.size());

      mockWebServer.shutdown();
    }
  }

  @Test
  void shouldRetrieveGermanPublicKeyWithMockWebServer() throws IOException {
    String body = Files.readString(FAKE_GERMAN_PUBLIC_KEY_RESPONSE_BODY).replace(TARGET_REPLACE_TXT, "");

    try (MockWebServer mockWebServer = new MockWebServer()) {
      mockWebServer.start();
      mockWebServer.enqueue(new MockResponse().setBody(body));

      HttpUrl baseUrl = mockWebServer.url("/decovid/pubKey/");

      ResponseEntity<String> publicKey = trustListService.getPublicKey(String.valueOf(baseUrl));

      assertEquals(HttpStatus.OK, publicKey.getStatusCode());
      assertTrue(Objects.requireNonNull(publicKey.getBody()).startsWith(PUBLIC_KEY_PREFIX));

      mockWebServer.shutdown();
    }
  }

  @Disabled("Only used for local testing and verification. MockWebServer is used instead, see Test shouldRetrieveSwissGermanCertificatesWithMockWebServer().")
  @Test
  void shouldRetrieveSwissCertificates() {
    ResponseEntity<String> certificates = trustListService.getHcertCertificates(HcertEndpointsApi.SWISS_CERTS_API,
        BEARER_TOKEN);

    SwissCertificates swissCertificates = trustListService.buildSwissHcertCertificates(
        Objects.requireNonNull(certificates.getBody()));
    List<SwissCertificate> certificatesList = swissCertificates.getCerts();

    assertEquals(EC_CURVE, certificatesList.get(0).getCrv());
    assertEquals(SIGNATURE, certificatesList.get(0).getUse());
  }

  @Disabled("Only used for local testing and verification. MockWebServer is used instead, see Test shouldRetrieveSwissActiveKeyIdsWithMockWebServer().")
  @Test
  void shouldRetrieveSwissActiveKeyIds() {
    ResponseEntity<String> certificates = trustListService.getHcertCertificates(HcertEndpointsApi.SWISS_ACTIVE_KID_API,
        BEARER_TOKEN);

    SwissActiveKeyIds swissActiveKeyIds = trustListService.buildSwissHcertActiveKeyIds(
        Objects.requireNonNull(certificates.getBody()));
    List<String> activeKeyIds = swissActiveKeyIds.getActiveKeyIds();

    assertEquals("e/YRqyv++qY=", activeKeyIds.get(0));
  }

  @Disabled("Only used for local testing and verification. MockWebServer is used instead, see Test shouldRetrieveSwissRevokedCertificatesWithMockWebServer().")
  @Test
  void shouldRetrieveSwissRevokedCertificates() {
    ResponseEntity<String> certificates = trustListService.getHcertCertificates(
        HcertEndpointsApi.SWISS_REVOCATION_LIST_API, BEARER_TOKEN);

    SwissRevokedCertificates swissRevokedCertificates = trustListService.buildSwissRevokedHcert(
        Objects.requireNonNull(certificates.getBody()));
    List<String> revokedCerts = swissRevokedCertificates.getRevokedCerts();

    assertEquals("urn:uvci:01:CH:C1AEDF4331C16C08CD792BAD", revokedCerts.get(0));
  }

  @Test
  void shouldRetrieveSwissActiveKeyIdsWithMockWebServer() throws IOException {
    String body = Files.readString(FAKE_SWISS_ACTIVE_KEY_IDS_RESPONSE_BODY).replace(TARGET_REPLACE_TXT, "");

    try (MockWebServer mockWebServer = new MockWebServer()) {
      mockWebServer.start();
      mockWebServer.enqueue(new MockResponse().setBody(body));

      HttpUrl baseUrl = mockWebServer.url("/decovid/activeKeyIds/");

      ResponseEntity<String> certificates = trustListService.getHcertCertificates(String.valueOf(baseUrl),
          BEARER_TOKEN);

      SwissActiveKeyIds swissActiveKeyIds = trustListService.buildSwissHcertActiveKeyIds(
          Objects.requireNonNull(certificates.getBody()));
      List<String> activeKeyIds = swissActiveKeyIds.getActiveKeyIds();

      assertEquals("e/YRq++89qY=", activeKeyIds.get(0));
      assertEquals("oPpr89HCDiQ=", activeKeyIds.get(1));
      assertEquals("JrTpkJe20/o=", activeKeyIds.get(2));
      assertEquals(5, activeKeyIds.size());
      assertEquals("134500000", swissActiveKeyIds.getValidDuration());
      assertEquals("12589", swissActiveKeyIds.getUpTo());

      mockWebServer.shutdown();
    }
  }

  @Test
  void shouldRetrieveSwissGermanCertificatesWithMockWebServer() throws IOException {
    String body = Files.readString(FAKE_SWISS_CERTIFICATES_RESPONSE_BODY).replace(TARGET_REPLACE_TXT, "");

    try (MockWebServer mockWebServer = new MockWebServer()) {
      mockWebServer.start();
      mockWebServer.enqueue(new MockResponse().setBody(body));

      HttpUrl baseUrl = mockWebServer.url("/decovid/certs/");

      ResponseEntity<String> certificates = trustListService.getHcertCertificates(String.valueOf(baseUrl),
          BEARER_TOKEN);

      SwissCertificates swissCertificates = trustListService.buildSwissHcertCertificates(
          Objects.requireNonNull(certificates.getBody()));
      List<SwissCertificate> certificatesList = swissCertificates.getCerts();
      SwissCertificate swissCertificateFirstEntry = certificatesList.get(0);
      SwissCertificate swissCertificateSecondEntry = certificatesList.get(1);

      assertEquals("keyId", swissCertificateFirstEntry.getKeyId());
      assertEquals("sig", swissCertificateFirstEntry.getUse());
      assertEquals("ES256", swissCertificateFirstEntry.getAlg());
      assertNull(swissCertificateFirstEntry.getN());
      assertNull(swissCertificateFirstEntry.getE());
      assertNull(swissCertificateFirstEntry.getSubjectPublicKeyInfo());
      assertEquals("P-256", swissCertificateFirstEntry.getCrv());
      assertEquals("x", swissCertificateFirstEntry.getX());
      assertEquals("y", swissCertificateFirstEntry.getY());

      assertEquals("keyId", swissCertificateSecondEntry.getKeyId());
      assertEquals("sig", swissCertificateSecondEntry.getUse());
      assertEquals("RS256", swissCertificateSecondEntry.getAlg());
      assertEquals("n", swissCertificateSecondEntry.getN());
      assertEquals("e", swissCertificateSecondEntry.getE());
      assertNull(swissCertificateSecondEntry.getSubjectPublicKeyInfo());
      assertNull(swissCertificateSecondEntry.getCrv());
      assertNull(swissCertificateSecondEntry.getX());
      assertNull(swissCertificateSecondEntry.getY());

      mockWebServer.shutdown();
    }
  }

  @Test
  void shouldRetrieveSwissRevokedCertificatesWithMockWebServer() throws IOException {
    String body = Files.readString(FAKE_SWISS_REVOKED_CERTIFICATES_RESPONSE_BODY).replace(TARGET_REPLACE_TXT, "");

    try (MockWebServer mockWebServer = new MockWebServer()) {
      mockWebServer.start();
      mockWebServer.enqueue(new MockResponse().setBody(body));

      HttpUrl baseUrl = mockWebServer.url("/decovid/certs/revoked");

      ResponseEntity<String> certificates = trustListService.getHcertCertificates(String.valueOf(baseUrl),
          BEARER_TOKEN);

      SwissRevokedCertificates swissRevokedCertificates = trustListService.buildSwissRevokedHcert(
          Objects.requireNonNull(certificates.getBody()));
      List<String> revokedCerts = swissRevokedCertificates.getRevokedCerts();

      assertEquals("urn:uvci:01:CH:C1AKPL4331C158R0CD792BAD", revokedCerts.get(0));
      assertEquals("urn:uvci:01:CH:3H47KOD7451D595563FGGHE9", revokedCerts.get(1));
      assertEquals("urn:uvci:01:CH:0894AC3CFF53CD53KK8523AD", revokedCerts.get(2));
      assertEquals(3, revokedCerts.size());
      assertEquals("134500000", swissRevokedCertificates.getValidDuration());

      mockWebServer.shutdown();
    }
  }

  @Test
  void shouldReturnX509Certificate() {
    X509Certificate swissX509Certificate = trustListService.convertCertificateToX509(SWISS_QR_CODE_CERTIFICATE);
    X509Certificate germanX509Certificate = trustListService.convertCertificateToX509(GERMAN_QR_CODE_CERTIFICATE);
    X509Certificate austrianX509Certificate = trustListService.convertCertificateToX509(AUSTRIAN_QR_CODE_CERTIFICATE);

    String rsaAlgorithm = swissX509Certificate.getSigAlgName();
    String ecAlgorithm = germanX509Certificate.getSigAlgName();
    String ecdsaAlgorithm = austrianX509Certificate.getSigAlgName();

    assertEquals(SIGNATURE_ALGO_SHA256_WITH_RSA, rsaAlgorithm);
    assertEquals(SIGNATURE_ALGO_RSA_SSA_PSS, ecAlgorithm);
    assertEquals(SIGNATURE_ALGO_SHA256_WITH_ECDSA, ecdsaAlgorithm);
  }

  @Test
  void shouldReturnPublicKey() {
    RSAPublicKey rsaPublicKey = (RSAPublicKey) trustListService.getPublicKey(SWISS_QR_CODE_PUBLIC_KEY, ALGO_RSA);
    ECPublicKey ecPublicKey = (ECPublicKey) trustListService.getPublicKey(GERMAN_QR_CODE_PUBLIC_KEY, ALGO_ECDSA);
    ECPublicKey ecDsaPublicKey = (ECPublicKey) trustListService.getPublicKey(AUSTRIAN_QR_CODE_PUBLIC_KEY, ALGO_ECDSA);

    String rsaAlgorithm = rsaPublicKey.getAlgorithm();
    String ecAlgorithm = ecPublicKey.getAlgorithm();
    String ecDsaAlgorithm = ecDsaPublicKey.getAlgorithm();

    assertEquals(ALGO_RSA, rsaAlgorithm);
    assertEquals(ALGO_ECDSA, ecAlgorithm);
    assertEquals(ALGO_ECDSA, ecDsaAlgorithm);
  }

  @Test
  void shouldGetRSAPublicKey() {
    BigInteger modulus = new BigInteger(SWISS_PUBLIC_KEY_PARAMS_MODULUS, RADIX_HEX);
    BigInteger exponent = new BigInteger(SWISS_PUBLIC_KEY_PARAMS_EXPONENT, RADIX_HEX);
    PublicKey rsaPublicKey = trustListService.getRSAPublicKey(modulus, exponent);
    String encodedPubKey = Base64.encodeBase64String(rsaPublicKey.getEncoded());

    assertEquals(SWISS_QR_CODE_PUBLIC_KEY, encodedPubKey);
  }

  @Test
  void shouldGetECPublicKey() {
    BigInteger austrianXCoord = new BigInteger(AUSTRIAN_PUBLIC_KEY_PARAMS_X_COORD, RADIX_HEX);
    BigInteger austrianYCoord = new BigInteger(AUSTRIAN_PUBLIC_KEY_PARAMS_Y_COORD, RADIX_HEX);
    PublicKey austrianECPublicKey = trustListService.getECPublicKey(austrianXCoord, austrianYCoord);

    BigInteger germanXCoord = new BigInteger(GERMAN_PUBLIC_KEY_PARAMS_X_COORD, RADIX_HEX);
    BigInteger germanYCoord = new BigInteger(GERMAN_PUBLIC_KEY_PARAMS_Y_COORD, RADIX_HEX);
    PublicKey germanECPublicKey = trustListService.getECPublicKey(germanXCoord, germanYCoord);

    String austrianEncodedPubKey = Base64.encodeBase64String(austrianECPublicKey.getEncoded());
    String germanEncodedECPubKey = Base64.encodeBase64String(germanECPublicKey.getEncoded());

    assertEquals(AUSTRIAN_QR_CODE_PUBLIC_KEY, austrianEncodedPubKey);
    assertEquals(GERMAN_QR_CODE_PUBLIC_KEY, germanEncodedECPubKey);
  }

  @Test
  void shouldThrowServerException() {
    Exception exception = assertThrows(ServerException.class, () -> {
      trustListService.convertCertificateToX509("foobar");
    });

    String actualMessage = exception.getMessage();

    assertEquals(KEY_SPEC_EXCEPTION, actualMessage);
  }

}
