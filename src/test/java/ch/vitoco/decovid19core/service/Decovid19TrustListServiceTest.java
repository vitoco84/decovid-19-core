package ch.vitoco.decovid19core.service;

import static ch.vitoco.decovid19core.constants.Const.KEY_SPEC_EXCEPTION;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Objects;

import ch.vitoco.decovid19core.certificates.GermanCertificate;
import ch.vitoco.decovid19core.certificates.GermanCertificates;
import ch.vitoco.decovid19core.constants.Endpoints;
import ch.vitoco.decovid19core.exception.KeySpecsException;
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

  private static final String SWISS_QR_CODE_CERTIFICATE = "MIIH5zCCBc+gAwIBAgIQLkbRAOTl2NRInzvKILpm3DANBgkqhkiG9w0BAQsFADCBuDELMAkGA1UEBhMCQ0gxHjAcBgNVBGETFVZBVENILUNIRS0yMjEuMDMyLjU3MzE+MDwGA1UEChM1QnVuZGVzYW10IGZ1ZXIgSW5mb3JtYXRpayB1bmQgVGVsZWtvbW11bmlrYXRpb24gKEJJVCkxHTAbBgNVBAsTFFN3aXNzIEdvdmVybm1lbnQgUEtJMSowKAYDVQQDEyFTd2lzcyBHb3Zlcm5tZW50IGFSZWd1bGF0ZWQgQ0EgMDIwHhcNMjEwNTA0MTQxNTUxWhcNMjQwNTA0MTQxNTUxWjCB9TELMAkGA1UEBhMCQ0gxCzAJBgNVBAgMAkJFMQ8wDQYDVQQHDAZLw7ZuaXoxGjAYBgNVBA8MEUdvdmVybm1lbnQgRW50aXR5MR4wHAYDVQRhExVOVFJDSC1DSEUtNDY3LjAyMy41NjgxKDAmBgNVBAoMH0J1bmRlc2FtdCBmw7xyIEdlc3VuZGhlaXQgKEJBRykxCTAHBgNVBAsMADEUMBIGA1UECwwLR0UtMDIyMC1CQUcxHDAaBgNVBAsME0NvdmlkLTE5LVplcnRpZmlrYXQxIzAhBgNVBAMMGkJBRyBDb3ZpZC0xOSBTaWduZXIgQSBURVNUMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4uZO4/7tneZ3XD5OAiTyoANOohQZC+DzZ4YC0AoLnEO+Z3PcTialCuRKS1zHfujNPI0GGG09DRVVXdv+tcKNXFDt/nRU1zlWDGFf4/63l5RIjkWFD3JFKqR8IlcJjrYYxstuZs3May3SGQJ+kZaeH5GFZMRvE0waHqMxbfwakvjf8qyBXCrZ1WsK+XJf7iYbJS2dO1a5HnegxPuRA7Zz8ikO7QRzmSongqOlkejEaIkFjx7gLGTUsOrBPYa5sdZqinDwmnjtKi52HLWarMXs+t1MN4etIp7GE7/zarjBNxk1Efiiwl+RdcwJ2uVwfrgzxfv3/TekZF8IUykV2Geu3QIDAQABo4ICrDCCAqgwHAYDVR0RBBUwE4ERaW5mb0BiYWcuYWRtaW4uY2gwgZMGCCsGAQUFBwEDBIGGMIGDMAoGCCsGAQUFBwsCMAkGBwQAi+xJAQIwCAYGBACORgEEMEsGBgQAjkYBBTBBMD8WOWh0dHA6Ly93d3cucGtpLmFkbWluLmNoL2Nwcy9QRFMtU0dQS0lfUmVndWxhdGVkX0NBXzAyLnBkZhMCRU4wEwYGBACORgEGMAkGBwQAjkYBBgIwDgYDVR0PAQH/BAQDAgeAMIHkBgNVHSAEgdwwgdkwgcsGCWCFdAERAwUCBzCBvTBDBggrBgEFBQcCARY3aHR0cDovL3d3dy5wa2kuYWRtaW4uY2gvY3BzL0NQU18yXzE2Xzc1Nl8xXzE3XzNfNV8wLnBkZjB2BggrBgEFBQcCAjBqDGhUaGlzIGlzIGEgcmVndWxhdGVkIGNlcnRpZmljYXRlIGZvciBsZWdhbCBwZXJzb25zIGFzIGRlZmluZWQgYnkgdGhlIFN3aXNzIGZlZGVyYWwgbGF3IFNSIDk0My4wMyAtIFplcnRFUzAJBgcEAIvsQAEDMHoGCCsGAQUFBwEBBG4wbDA6BggrBgEFBQcwAoYuaHR0cDovL3d3dy5wa2kuYWRtaW4uY2gvYWlhL2FSZWd1bGF0ZWRDQTAyLmNydDAuBggrBgEFBQcwAYYiaHR0cDovL3d3dy5wa2kuYWRtaW4uY2gvYWlhL2Etb2NzcDA/BgNVHR8EODA2MDSgMqAwhi5odHRwOi8vd3d3LnBraS5hZG1pbi5jaC9jcmwvYVJlZ3VsYXRlZENBMDIuY3JsMB8GA1UdIwQYMBaAFPje0l9SouctbOaYopRmLaKt6e7yMB0GA1UdDgQWBBTw07j7sChhumchnbeMuPjdSVvPADANBgkqhkiG9w0BAQsFAAOCAgEASP2AYJVGV5WWHpCXvHf3/ctob7pX1fZHXfwkos5XfX5dArVjqNM4oaiTlB0Fk5KxUCmIhi7lIa92soy564JShPkIhM3jtQygKC/XItTP4UbR/SfjNO4teL5HSD5QddyqHdaJUX/OE1sAhOxIEnFPqOa0DFFOTAEUYWJauRvSJ8MB2KlsUILpkxMx03KfB8bxkFTDdUIPoREVLSWAGKwxKS0OE6ZnmwoLdhvu7HxQO9msx9ci5Q58fb6ApXn6xk9uCMTQr5HiJA4VCZ7oRaH+uk/BqDfb/1lcgLv6cYh0R/6oD5IpT/SpVu1spOGxKR/U6BnAysiiFkFkqbFsf/ZoVDR/hBC0omQtpps6P64LNKq0rv3ZdU918XT42Fdn2hH2+ajJzhix6VjTYKAh+VK+dYyB/qx22XfMP+41Gt5TYz65AauWV9tOWpFKtuXtBWkziV9JYsnokoLGaaZNIojQZx7bJ6KdUnwqMbPUTOkbM++expO+YqFSmundq16TpUuzHBKOe70Lgwytv/WFlveeFR9mJcWfzgiZitNrbQ6teluAK89uy/kR+sqeO5EyIJgsTNp4yAYBb5399ppI2qk0Mea+629wvuEXSaoXQzhiOjx1aXd7Ib2sHj11c16NwQi83D6YcuI/wkcOOemBJPr65aRXFKX6EnwG/Bm6/rMzGTc=";
  private static final String SWISS_QR_CODE_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4uZO4/7tneZ3XD5OAiTyoANOohQZC+DzZ4YC0AoLnEO+Z3PcTialCuRKS1zHfujNPI0GGG09DRVVXdv+tcKNXFDt/nRU1zlWDGFf4/63l5RIjkWFD3JFKqR8IlcJjrYYxstuZs3May3SGQJ+kZaeH5GFZMRvE0waHqMxbfwakvjf8qyBXCrZ1WsK+XJf7iYbJS2dO1a5HnegxPuRA7Zz8ikO7QRzmSongqOlkejEaIkFjx7gLGTUsOrBPYa5sdZqinDwmnjtKi52HLWarMXs+t1MN4etIp7GE7/zarjBNxk1Efiiwl+RdcwJ2uVwfrgzxfv3/TekZF8IUykV2Geu3QIDAQAB";
  private static final String GERMAN_QR_CODE_CERTIFICATE = "MIIGXjCCBBagAwIBAgIQXg7NBunD5eaLpO3Fg9REnzA9BgkqhkiG9w0BAQowMKANMAsGCWCGSAFlAwQCA6EaMBgGCSqGSIb3DQEBCDALBglghkgBZQMEAgOiAwIBQDBgMQswCQYDVQQGEwJERTEVMBMGA1UEChMMRC1UcnVzdCBHbWJIMSEwHwYDVQQDExhELVRSVVNUIFRlc3QgQ0EgMi0yIDIwMTkxFzAVBgNVBGETDk5UUkRFLUhSQjc0MzQ2MB4XDTIxMDQyNzA5MzEyMloXDTIyMDQzMDA5MzEyMlowfjELMAkGA1UEBhMCREUxFDASBgNVBAoTC1ViaXJjaCBHbWJIMRQwEgYDVQQDEwtVYmlyY2ggR21iSDEOMAwGA1UEBwwFS8O2bG4xHDAaBgNVBGETE0RUOkRFLVVHTk9UUFJPVklERUQxFTATBgNVBAUTDENTTTAxNzE0MzQzNzBZMBMGByqGSM49AgEGCCqGSM49AwEHA0IABPI+O0HoJImZhJs0rwaSokjUf1vspsOTd57Lrq/9tn/aS57PXc189pyBTVVtbxNkts4OSgh0BdFfml/pgETQmvSjggJfMIICWzAfBgNVHSMEGDAWgBRQdpKgGuyBrpHC3agJUmg33lGETzAtBggrBgEFBQcBAwQhMB8wCAYGBACORgEBMBMGBgQAjkYBBjAJBgcEAI5GAQYCMIH+BggrBgEFBQcBAQSB8TCB7jArBggrBgEFBQcwAYYfaHR0cDovL3N0YWdpbmcub2NzcC5kLXRydXN0Lm5ldDBHBggrBgEFBQcwAoY7aHR0cDovL3d3dy5kLXRydXN0Lm5ldC9jZ2ktYmluL0QtVFJVU1RfVGVzdF9DQV8yLTJfMjAxOS5jcnQwdgYIKwYBBQUHMAKGamxkYXA6Ly9kaXJlY3RvcnkuZC10cnVzdC5uZXQvQ049RC1UUlVTVCUyMFRlc3QlMjBDQSUyMDItMiUyMDIwMTksTz1ELVRydXN0JTIwR21iSCxDPURFP2NBQ2VydGlmaWNhdGU/YmFzZT8wFwYDVR0gBBAwDjAMBgorBgEEAaU0AgICMIG/BgNVHR8EgbcwgbQwgbGgga6ggauGcGxkYXA6Ly9kaXJlY3RvcnkuZC10cnVzdC5uZXQvQ049RC1UUlVTVCUyMFRlc3QlMjBDQSUyMDItMiUyMDIwMTksTz1ELVRydXN0JTIwR21iSCxDPURFP2NlcnRpZmljYXRlcmV2b2NhdGlvbmxpc3SGN2h0dHA6Ly9jcmwuZC10cnVzdC5uZXQvY3JsL2QtdHJ1c3RfdGVzdF9jYV8yLTJfMjAxOS5jcmwwHQYDVR0OBBYEFF8VpC1Zm1R44UuA8oDPaWTMeabxMA4GA1UdDwEB/wQEAwIGwDA9BgkqhkiG9w0BAQowMKANMAsGCWCGSAFlAwQCA6EaMBgGCSqGSIb3DQEBCDALBglghkgBZQMEAgOiAwIBQAOCAgEAwRkhqDw/YySzfqSUjfeOEZTKwsUf+DdcQO8WWftTx7Gg6lUGMPXrCbNYhFWEgRdIiMKD62niltkFI+DwlyvSAlwnAwQ1pKZbO27CWQZk0xeAK1xfu8bkVxbCOD4yNNdgR6OIbKe+a9qHk27Ky44Jzfmu8vV1sZMG06k+kldUqJ7FBrx8O0rd88823aJ8vpnGfXygfEp7bfN4EM+Kk9seDOK89hXdUw0GMT1TsmErbozn5+90zRq7fNbVijhaulqsMj8qaQ4iVdCSTRlFpHPiU/vRB5hZtsGYYFqBjyQcrFti5HdL6f69EpY/chPwcls93EJE7QIhnTidg3m4+vliyfcavVYH5pmzGXRO11w0xyrpLMWh9wX/Al984VHPZj8JoPgSrpQp4OtkTbtOPBH3w4fXdgWMAmcJmwq7SwRTC7Ab1AK6CXk8IuqloJkeeAG4NNeTa3ujZMBxr0iXtVpaOV01uLNQXHAydl2VTYlRkOm294/s4rZ1cNb1yqJ+VNYPNa4XmtYPxh/i81afHmJUZRiGyyyrlmKA3qWVsV7arHbcdC/9UmIXmSG/RaZEpmiCtNrSVXvtzPEXgPrOomZuCoKFC26hHRI8g+cBLdn9jIGduyhFiLAArndYp5US/KXUvu8xVFLZ/cxMalIWmiswiPYMwx2ZP+mIf1QHu/nyDtQ=";
  private static final String GERMAN_QR_CODE_PUBLIC_KEY = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE8j47QegkiZmEmzSvBpKiSNR/W+ymw5N3nsuur/22f9pLns9dzXz2nIFNVW1vE2S2zg5KCHQF0V+aX+mARNCa9A==";
  private static final String AUSTRIAN_QR_CODE_CERTIFICATE = "MIIBvTCCAWOgAwIBAgIKAXk8i88OleLsuTAKBggqhkjOPQQDAjA2MRYwFAYDVQQDDA1BVCBER0MgQ1NDQSAxMQswCQYDVQQGEwJBVDEPMA0GA1UECgwGQk1TR1BLMB4XDTIxMDUwNTEyNDEwNloXDTIzMDUwNTEyNDEwNlowPTERMA8GA1UEAwwIQVQgRFNDIDExCzAJBgNVBAYTAkFUMQ8wDQYDVQQKDAZCTVNHUEsxCjAIBgNVBAUTATEwWTATBgcqhkjOPQIBBggqhkjOPQMBBwNCAASt1Vz1rRuW1HqObUE9MDe7RzIk1gq4XW5GTyHuHTj5cFEn2Rge37+hINfCZZcozpwQKdyaporPUP1TE7UWl0F3o1IwUDAOBgNVHQ8BAf8EBAMCB4AwHQYDVR0OBBYEFO49y1ISb6cvXshLcp8UUp9VoGLQMB8GA1UdIwQYMBaAFP7JKEOflGEvef2iMdtopsetwGGeMAoGCCqGSM49BAMCA0gAMEUCIQDG2opotWG8tJXN84ZZqT6wUBz9KF8D+z9NukYvnUEQ3QIgdBLFSTSiDt0UJaDF6St2bkUQuVHW6fQbONd731/M4nc=";
  private static final String AUSTRIAN_QR_CODE_PUBLIC_KEY = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAErdVc9a0bltR6jm1BPTA3u0cyJNYKuF1uRk8h7h04+XBRJ9kYHt+/oSDXwmWXKM6cECncmqaKz1D9UxO1FpdBdw==";

  private static final String SIGNATURE_ALGO_RSA = "RSA";
  private static final String SIGNATURE_ALGO_ECDSA = "ECDSA";
  private static final String SIGNATURE_ALGO_SHA256_WITH_RSA = "SHA256withRSA";
  private static final String SIGNATURE_ALGO_SHA256_WITH_ECDSA = "SHA256withECDSA";
  private static final String SIGNATURE_ALGO_RSA_SSA_PSS = "RSASSA-PSS";

  private static final String PUBLIC_KEY_PREFIX = "-----BEGIN PUBLIC KEY-----";
  private static final String PUBLIC_KEY_POSTFIX = "-----END PUBLIC KEY-----";

  private final Decovid19TrustListService decovid19TrustListService = new Decovid19TrustListService();
  private final MockWebServer mockWebServer = new MockWebServer();

  @BeforeEach
  void setupMockWebServer() throws IOException {
    mockWebServer.start();
  }

  @Disabled("Only used for local testing and verification. MockWebServer is used instead, see Test shouldRetrieveCertificatesWithMockWebServer().")
  @Test
  void shouldRetrieveCertificates() {
    ResponseEntity<String> certificates = decovid19TrustListService.getHcertCertificates(Endpoints.GERMAN_CERTS_API);

    GermanCertificates germanCertificates = decovid19TrustListService.buildGermanHcertCertificates(
        Objects.requireNonNull(certificates.getBody()));
    List<GermanCertificate> certificatesList = germanCertificates.getCertificates();

    assertEquals("DSC", certificatesList.get(0).getCertificateType());
    assertFalse(certificatesList.isEmpty());
  }

  @Disabled("Only used for local testing and verification. MockWebServer is used instead, see Test shouldRetrievePublicKeyWithMockWebServer().")
  @Test
  void shouldRetrievePublicKey() {
    ResponseEntity<String> publicKeyTest = decovid19TrustListService.getPublicKey(Endpoints.GERMAN_PUBLIC_KEY_API);

    assertEquals(HttpStatus.OK, publicKeyTest.getStatusCode());
    assertTrue(Objects.requireNonNull(publicKeyTest.getBody()).startsWith(PUBLIC_KEY_PREFIX));
    assertTrue(Objects.requireNonNull(publicKeyTest.getBody()).endsWith(PUBLIC_KEY_POSTFIX + "\n"));
  }

  @Test
  void shouldRetrieveCertificatesWithMockWebServer() throws IOException {
    String body = Files.readString(FAKE_CERTIFICATES_RESPONSE_BODY).replace("\r\n", "");

    mockWebServer.enqueue(new MockResponse().setBody(body));

    HttpUrl baseUrl = mockWebServer.url("/decovid/certs/");

    ResponseEntity<String> certificates = decovid19TrustListService.getHcertCertificates(String.valueOf(baseUrl));

    GermanCertificates germanCertificates = decovid19TrustListService.buildGermanHcertCertificates(
        Objects.requireNonNull(certificates.getBody()));
    List<GermanCertificate> certificatesList = germanCertificates.getCertificates();
    GermanCertificate germanCertificate = certificatesList.get(0);

    assertEquals("DSC", germanCertificate.getCertificateType());
    assertEquals("DE", germanCertificate.getCountry());
    assertEquals("kid", germanCertificate.getKid());
    assertEquals("rawData", germanCertificate.getRawData());
    assertEquals("signature", germanCertificate.getSignature());
    assertEquals("thumbprint", germanCertificate.getThumbprint());
    assertEquals("timestamp", germanCertificate.getTimestamp());
    assertEquals(3, certificatesList.size());
  }

  @Test
  void shouldRetrievePublicKeyWithMockWebServer() throws IOException {
    String body = Files.readString(FAKE_PUBLIC_KEY_RESPONSE_BODY).replace("\r\n", "");

    mockWebServer.enqueue(new MockResponse().setBody(body));

    HttpUrl baseUrl = mockWebServer.url("/decovid/pubKey/");

    ResponseEntity<String> publicKey = decovid19TrustListService.getPublicKey(String.valueOf(baseUrl));

    assertEquals(HttpStatus.OK, publicKey.getStatusCode());
    assertTrue(Objects.requireNonNull(publicKey.getBody()).startsWith(PUBLIC_KEY_PREFIX));
  }

  @Test
  void shouldReturnX509Certificate() {
    X509Certificate swissX509Certificate = decovid19TrustListService.convertCertificateToX509(
        SWISS_QR_CODE_CERTIFICATE);
    X509Certificate germanX509Certificate = decovid19TrustListService.convertCertificateToX509(
        GERMAN_QR_CODE_CERTIFICATE);
    X509Certificate austrianX509Certificate = decovid19TrustListService.convertCertificateToX509(
        AUSTRIAN_QR_CODE_CERTIFICATE);

    String rsaAlgorithm = swissX509Certificate.getSigAlgName();
    String ecAlgorithm = germanX509Certificate.getSigAlgName();
    String ecdsaAlgorithm = austrianX509Certificate.getSigAlgName();

    assertEquals(SIGNATURE_ALGO_SHA256_WITH_RSA, rsaAlgorithm);
    assertEquals(SIGNATURE_ALGO_RSA_SSA_PSS, ecAlgorithm);
    assertEquals(SIGNATURE_ALGO_SHA256_WITH_ECDSA, ecdsaAlgorithm);
  }

  @Test
  void shouldReturnPublicKey() {
    RSAPublicKey rsaPublicKey = (RSAPublicKey) decovid19TrustListService.readPublicKey(SWISS_QR_CODE_PUBLIC_KEY,
        SIGNATURE_ALGO_RSA);
    ECPublicKey ecPublicKey = (ECPublicKey) decovid19TrustListService.readPublicKey(GERMAN_QR_CODE_PUBLIC_KEY,
        SIGNATURE_ALGO_ECDSA);
    ECPublicKey ecDsaPublicKey = (ECPublicKey) decovid19TrustListService.readPublicKey(AUSTRIAN_QR_CODE_PUBLIC_KEY,
        SIGNATURE_ALGO_ECDSA);

    String rsaAlgorithm = rsaPublicKey.getAlgorithm();
    String ecAlgorithm = ecPublicKey.getAlgorithm();
    String ecDsaAlgorithm = ecDsaPublicKey.getAlgorithm();

    assertEquals(SIGNATURE_ALGO_RSA, rsaAlgorithm);
    assertEquals(SIGNATURE_ALGO_ECDSA, ecAlgorithm);
    assertEquals(SIGNATURE_ALGO_ECDSA, ecDsaAlgorithm);
  }

  @Test
  void shouldThrowKeySpecsException() {
    Exception exception = assertThrows(KeySpecsException.class, () -> {
      decovid19TrustListService.convertCertificateToX509("foobar");
    });

    String actualMessage = exception.getMessage();

    assertEquals(KEY_SPEC_EXCEPTION, actualMessage);
  }

  @AfterEach
  void cleanUpMockWebServer() throws IOException {
    mockWebServer.shutdown();
  }

}
