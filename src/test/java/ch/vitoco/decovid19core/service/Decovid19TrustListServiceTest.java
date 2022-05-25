package ch.vitoco.decovid19core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ch.vitoco.decovid19core.certificates.HcertCertificateGerman;
import ch.vitoco.decovid19core.certificates.HcertCertificatesGerman;
import ch.vitoco.decovid19core.constants.Endpoints;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

class Decovid19TrustListServiceTest {

  private static final Path FAKE_CERTIFICATES_RESPONSE_BODY = Paths.get(
      "src/test/resources/fakeCertificateResponseBody.txt");
  private static final Path FAKE_PUBLIC_KEY_RESPONSE_BODY = Paths.get(
      "src/test/resources/fakePublicKeyResponseBody.txt");

  private static final String SWISS_QR_CODE_CERTIFICATE = "MIIH5zCCBc+gAwIBAgIQLkbRAOTl2NRInzvKILpm3DANBgkqhkiG9w0BAQsFADCBuDELMAkGA1UEBhMCQ0gxHjAcBgNVBGETFVZBVENILUNIRS0yMjEuMDMyLjU3MzE+MDwGA1UEChM1QnVuZGVzYW10IGZ1ZXIgSW5mb3JtYXRpayB1bmQgVGVsZWtvbW11bmlrYXRpb24gKEJJVCkxHTAbBgNVBAsTFFN3aXNzIEdvdmVybm1lbnQgUEtJMSowKAYDVQQDEyFTd2lzcyBHb3Zlcm5tZW50IGFSZWd1bGF0ZWQgQ0EgMDIwHhcNMjEwNTA0MTQxNTUxWhcNMjQwNTA0MTQxNTUxWjCB9TELMAkGA1UEBhMCQ0gxCzAJBgNVBAgMAkJFMQ8wDQYDVQQHDAZLw7ZuaXoxGjAYBgNVBA8MEUdvdmVybm1lbnQgRW50aXR5MR4wHAYDVQRhExVOVFJDSC1DSEUtNDY3LjAyMy41NjgxKDAmBgNVBAoMH0J1bmRlc2FtdCBmw7xyIEdlc3VuZGhlaXQgKEJBRykxCTAHBgNVBAsMADEUMBIGA1UECwwLR0UtMDIyMC1CQUcxHDAaBgNVBAsME0NvdmlkLTE5LVplcnRpZmlrYXQxIzAhBgNVBAMMGkJBRyBDb3ZpZC0xOSBTaWduZXIgQSBURVNUMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4uZO4/7tneZ3XD5OAiTyoANOohQZC+DzZ4YC0AoLnEO+Z3PcTialCuRKS1zHfujNPI0GGG09DRVVXdv+tcKNXFDt/nRU1zlWDGFf4/63l5RIjkWFD3JFKqR8IlcJjrYYxstuZs3May3SGQJ+kZaeH5GFZMRvE0waHqMxbfwakvjf8qyBXCrZ1WsK+XJf7iYbJS2dO1a5HnegxPuRA7Zz8ikO7QRzmSongqOlkejEaIkFjx7gLGTUsOrBPYa5sdZqinDwmnjtKi52HLWarMXs+t1MN4etIp7GE7/zarjBNxk1Efiiwl+RdcwJ2uVwfrgzxfv3/TekZF8IUykV2Geu3QIDAQABo4ICrDCCAqgwHAYDVR0RBBUwE4ERaW5mb0BiYWcuYWRtaW4uY2gwgZMGCCsGAQUFBwEDBIGGMIGDMAoGCCsGAQUFBwsCMAkGBwQAi+xJAQIwCAYGBACORgEEMEsGBgQAjkYBBTBBMD8WOWh0dHA6Ly93d3cucGtpLmFkbWluLmNoL2Nwcy9QRFMtU0dQS0lfUmVndWxhdGVkX0NBXzAyLnBkZhMCRU4wEwYGBACORgEGMAkGBwQAjkYBBgIwDgYDVR0PAQH/BAQDAgeAMIHkBgNVHSAEgdwwgdkwgcsGCWCFdAERAwUCBzCBvTBDBggrBgEFBQcCARY3aHR0cDovL3d3dy5wa2kuYWRtaW4uY2gvY3BzL0NQU18yXzE2Xzc1Nl8xXzE3XzNfNV8wLnBkZjB2BggrBgEFBQcCAjBqDGhUaGlzIGlzIGEgcmVndWxhdGVkIGNlcnRpZmljYXRlIGZvciBsZWdhbCBwZXJzb25zIGFzIGRlZmluZWQgYnkgdGhlIFN3aXNzIGZlZGVyYWwgbGF3IFNSIDk0My4wMyAtIFplcnRFUzAJBgcEAIvsQAEDMHoGCCsGAQUFBwEBBG4wbDA6BggrBgEFBQcwAoYuaHR0cDovL3d3dy5wa2kuYWRtaW4uY2gvYWlhL2FSZWd1bGF0ZWRDQTAyLmNydDAuBggrBgEFBQcwAYYiaHR0cDovL3d3dy5wa2kuYWRtaW4uY2gvYWlhL2Etb2NzcDA/BgNVHR8EODA2MDSgMqAwhi5odHRwOi8vd3d3LnBraS5hZG1pbi5jaC9jcmwvYVJlZ3VsYXRlZENBMDIuY3JsMB8GA1UdIwQYMBaAFPje0l9SouctbOaYopRmLaKt6e7yMB0GA1UdDgQWBBTw07j7sChhumchnbeMuPjdSVvPADANBgkqhkiG9w0BAQsFAAOCAgEASP2AYJVGV5WWHpCXvHf3/ctob7pX1fZHXfwkos5XfX5dArVjqNM4oaiTlB0Fk5KxUCmIhi7lIa92soy564JShPkIhM3jtQygKC/XItTP4UbR/SfjNO4teL5HSD5QddyqHdaJUX/OE1sAhOxIEnFPqOa0DFFOTAEUYWJauRvSJ8MB2KlsUILpkxMx03KfB8bxkFTDdUIPoREVLSWAGKwxKS0OE6ZnmwoLdhvu7HxQO9msx9ci5Q58fb6ApXn6xk9uCMTQr5HiJA4VCZ7oRaH+uk/BqDfb/1lcgLv6cYh0R/6oD5IpT/SpVu1spOGxKR/U6BnAysiiFkFkqbFsf/ZoVDR/hBC0omQtpps6P64LNKq0rv3ZdU918XT42Fdn2hH2+ajJzhix6VjTYKAh+VK+dYyB/qx22XfMP+41Gt5TYz65AauWV9tOWpFKtuXtBWkziV9JYsnokoLGaaZNIojQZx7bJ6KdUnwqMbPUTOkbM++expO+YqFSmundq16TpUuzHBKOe70Lgwytv/WFlveeFR9mJcWfzgiZitNrbQ6teluAK89uy/kR+sqeO5EyIJgsTNp4yAYBb5399ppI2qk0Mea+629wvuEXSaoXQzhiOjx1aXd7Ib2sHj11c16NwQi83D6YcuI/wkcOOemBJPr65aRXFKX6EnwG/Bm6/rMzGTc=";
  private static final String SWISS_QR_CODE_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwFh8HIBXfjcz7G4uPL7ooBpVAwoZofGJgWNr8Nstxhe4CQuGGo7Cl6QrZyQDtZovLOpcR1S5c4VIUAhegJY2lBEtrIzdR/hcooabkOUyDULsPejaOcii4LtE/25rSKF/6sGyQiCLFb1u4JGlZK7CGmJcXE/NYnDQDvJv6K4dhpwGIoeNFhoG2X6fDHjZemO+/BYZZjRh2BuUf/QnSpVYl6bhmOuJP2EAHBiSt+2S6Xn7O+j0qF5EK5Tm6tttSOjnmz59isc35GlwloszjRn5riUR2t8TYt5UEMYJY2N8iEGIpu9HiJBQyMB1kjzwc7ClwYo4/jB3zWqwZGwhjj5wgwIDAQAB";
  private static final String GERMAN_QR_CODE_CERTIFICATE = "MIICNjCCAdwCFBmvlK0tjbnPAGR8JiUl6Mzb3YRpMAoGCCqGSM49BAMCMIGxMQswCQYDVQQGEwJJRDEUMBIGA1UECAwLREtJIEpha2FydGExGDAWBgNVBAcMD0pha2FydGEgU2VsYXRhbjExMC8GA1UECgwoS2VtZW50ZXJpYW4gS2VzZWhhdGFuIFJlcHVibGlrIEluZG9uZXNpYTEmMCQGA1UECwwdRGlnaXRhbCBUcmFuc2Zvcm1hdGlvbiBPZmZpY2UxFzAVBgNVBAMMDkNTQ0FfREdDX0lEXzAzMB4XDTIyMDQwODE2MTA1MloXDTI0MDQwNzE2MTA1MlowgYgxCzAJBgNVBAYTAklEMRQwEgYDVQQIDAtES0kgSmFrYXJ0YTEWMBQGA1UEBwwNU291dGggSmFrYXJ0YTExMC8GA1UECgwoTWluaXN0cnkgb2YgSGVhbHRoIFJlcHVibGljIG9mIEluZG9uZXNpYTEYMBYGA1UEAwwPZXUua2Vta2VzLmdvLmlkMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE9YqgNXmmXgWijLg6ci7eRK/PVWrb1YSBqzpKek8r4L/Qhl+R+4l+hJi2Q+ShyhJtBL2D9yYajyYykkC++LpmxzAKBggqhkjOPQQDAgNIADBFAiAi5MkLQJChbOIYMEpbqiFU8lpt/9TNyyukk2JyHwCtiwIhAJyL2wphMavW/Oo32SLA2Cqft92IcCh5NjPHDz8iBFTz";
  private static final String GERMAN_QR_CODE_PUBLIC_KEY = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAELbIuRiPfw0qvswRNAVvrQJG6yJHqlIAXeWGw26hZ50vzER3as1ZPaSiloE2d2bBixVX4Xa1rFe5rvsJXLN0GCQ==";

  private final Decovid19TrustListService decovid19TrustListService = new Decovid19TrustListService();
  private final MockWebServer mockWebServer = new MockWebServer();

  @BeforeEach
  void setupMockWebServer() throws IOException {
    mockWebServer.start();
  }

  @Disabled("Only used for local testing and verification. MockWebServer is used instead, see Test shouldRetrieveCertificatesWithMockWebServer().")
  @Test
  void shouldRetrieveCertificates() {
    ResponseEntity<String> certificates = decovid19TrustListService.getHcertCertificates(Endpoints.GERMAN_CERTS);

    HcertCertificatesGerman hcertCertificatesGerman = decovid19TrustListService.buildGermanHcertCertificates(
        Objects.requireNonNull(certificates.getBody()));
    List<HcertCertificateGerman> certificatesList = hcertCertificatesGerman.getCertificates();

    assertEquals("DSC", certificatesList.get(0).getCertificateType());
    assertFalse(certificatesList.isEmpty());
  }

  @Disabled("Only used for local testing and verification. MockWebServer is used instead, see Test shouldRetrievePublicKeyWithMockWebServer().")
  @Test
  void shouldRetrievePublicKey() {
    ResponseEntity<String> publicKeyTest = decovid19TrustListService.getPublicKey(Endpoints.GERMAN_PUBLIC_KEY);

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

    HcertCertificatesGerman hcertCertificatesGerman = decovid19TrustListService.buildGermanHcertCertificates(
        Objects.requireNonNull(certificates.getBody()));
    List<HcertCertificateGerman> certificatesList = hcertCertificatesGerman.getCertificates();
    HcertCertificateGerman hcertCertificateGerman = certificatesList.get(0);

    assertEquals("DSC", hcertCertificateGerman.getCertificateType());
    assertEquals("DE", hcertCertificateGerman.getCountry());
    assertEquals("kid", hcertCertificateGerman.getKid());
    assertEquals("rawData", hcertCertificateGerman.getRawData());
    assertEquals("signature", hcertCertificateGerman.getSignature());
    assertEquals("thumbprint", hcertCertificateGerman.getThumbprint());
    assertEquals("timestamp", hcertCertificateGerman.getTimestamp());
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

  @Test
  void shouldReturnX509Certificate() {
    X509Certificate swissX509Certificate = decovid19TrustListService.convertCertificateToX509(
        SWISS_QR_CODE_CERTIFICATE);
    X509Certificate germanX509Certificate = decovid19TrustListService.convertCertificateToX509(
        GERMAN_QR_CODE_CERTIFICATE);

    String swissSigAlgOID = swissX509Certificate.getSigAlgName();
    String germanSigAlgOID = germanX509Certificate.getSigAlgName();

    assertEquals("SHA256withRSA", swissSigAlgOID);
    assertEquals("SHA256withECDSA", germanSigAlgOID);
  }

  //  @Test
  //  void blabla() {
  //
  //    StringBuilder publicKey = new StringBuilder();
  //    publicKey.append("-----BEGIN PUBLIC KEY-----")
  //        .append("\n")
  //        .append(SWISS_QR_CODE_PUBLIC_KEY)
  //        .append("\n")
  //        .append("-----END PUBLIC KEY-----");
  //
  //    StringBuilder publicKey1 = new StringBuilder();
  //    publicKey1.append("-----BEGIN PUBLIC KEY-----")
  //        .append("\n")
  //        .append(GERMAN_QR_CODE_PUBLIC_KEY)
  //        .append("\n")
  //        .append("-----END PUBLIC KEY-----");
  //
  //
  //    X509Certificate x509Certificate2 = decovid19TrustListService.convertCertificateToX509(SWISS_QR_CODE_CERTIFICATE);
  //    String sigAlgName = x509Certificate2.getSigAlgName();
  //    RSAPublicKey rsaPublicKey = (RSAPublicKey) x509Certificate2.getPublicKey();
  //
  //    RSAPublicKey rsa2 = (RSAPublicKey) decovid19TrustListService.readPublicKey(publicKey.toString(), "RSA");
  //    PublicKey ecdsa = decovid19TrustListService.readPublicKey(publicKey1.toString(), "ECDSA");
  //
  //
  //    assertEquals(" ", rsaPublicKey);
  //  }

  @AfterEach
  void cleanUpMockWebServer() throws IOException {
    mockWebServer.shutdown();
  }

}
