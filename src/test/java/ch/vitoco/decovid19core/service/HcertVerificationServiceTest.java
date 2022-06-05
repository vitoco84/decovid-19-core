package ch.vitoco.decovid19core.service;

import static ch.vitoco.decovid19core.constants.ExceptionMessages.KEY_SPEC_EXCEPTION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Objects;

import ch.vitoco.decovid19core.exception.ServerException;
import ch.vitoco.decovid19core.model.certificates.EUCertificate;
import ch.vitoco.decovid19core.model.certificates.EUCertificates;
import ch.vitoco.decovid19core.model.certificates.SwissCertificate;
import ch.vitoco.decovid19core.model.certificates.SwissCertificates;
import ch.vitoco.decovid19core.server.HcertVerificationServerRequest;
import ch.vitoco.decovid19core.server.HcertVerificationServerResponse;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class HcertVerificationServiceTest {

  private static final String PEM_PREFIX = "-----BEGIN CERTIFICATE-----";
  private static final String PEM_POSTFIX = "-----END CERTIFICATE-----";
  private static final String X509_CERT_TYPE = "X.509";

  private static final String SWISS_QR_CODE_VACC_KID = "mmrfzpMU6xc=";
  private static final String SWISS_QR_CODE_VACC_HC1_PREFIX = "HC1:NCFS605G0/3WUWGSLKH47GO0KNJ9DSWQIIWT9CK4600XKY-CE59-G80:84F35RIV R2F3FMMTTBY50.FK6ZK7:EDOLOPCO8F6%E3.DA%EOPC1G72A6YM83G7NA7H:6JM8D%6I:61S8ZW6HL6C460S8VF6VX6UPC0JCZ69FVCPD0LVC6JD846Y96A466W5B56+EDG8F3I80/D6$CBECSUER:C2$NS346$C2%E9VC- CSUE145GB8JA5B$D% D3IA4W5646946%96X47.JCP9EJY8L/5M/5546.96D463KC.SC4KCD3DX47B46IL6646H*6Z/E5JD%96IA74R6646407GVC*JC1A6/Q63W5KF6746TPCBEC7ZKW.CU2DNXO VD5$C JC3/DMP8$ILZEDZ CW.C9WE.Y9AY8+S9VIAI3D8WEVM8:S9C+9$PC5$CUZCY$5Y$5FBBM00T%LTAT1MOQYR8GUN$K15LIGG2P27%A46BT52VUTL.1*B89Y5B428HRSR3I/E5DS/8NBY4H2BCN8NP1D4B:0K9UQQ67BLTH21AF0V8G52R 62+5BQYCV03SO79O6K+8UXL$T4$%RT150DUHZK+Q9TIE+IMQU4E/Q4T303TKWNXTSORE.4WNPCJX66NN-2F9IHTYLR6IR UAB98RR1A0P9DL0CS5KZ*HEGT1%TQWELFQHG5/JO9TI:.T1JQF.K7 EJ 2/CI5GASQP7ULRX4-07%9W2139E2HMGW99Q DQJADB3UAJKUCOVLG+9T+J:15.12U+OBMCJ1KZ+C+87I8I9JGA0T%U2CMFHI5U:L400C.CC/K3KJZ3OM/D59TBL5AZFMPIW4";
  private static final String BEARER_TOKEN = "Token";

  private static final String GERMAN_QR_CODE_VACC_KEY_ID = "DEsVUSvpFAE=";
  private static final String GERMAN_QR_CODE_VACC_HC1_PREFIX = "HC1:6BF+70790T9WJWG.FKY*4GO0.O1CV2 O5 N2FBBRW1*70HS8WY04AC*WIFN0AHCD8KD97TK0F90KECTHGWJC0FDC:5AIA%G7X+AQB9746HS80:54IBQF60R6$A80X6S1BTYACG6M+9XG8KIAWNA91AY%67092L4WJCT3EHS8XJC$+DXJCCWENF6OF63W5NW6WF6%JC QE/IAYJC5LEW34U3ET7DXC9 QE-ED8%E.JCBECB1A-:8$96646AL60A60S6Q$D.UDRYA 96NF6L/5QW6307KQEPD09WEQDD+Q6TW6FA7C466KCN9E%961A6DL6FA7D46JPCT3E5JDLA7$Q6E464W5TG6..DX%DZJC6/DTZ9 QE5$CB$DA/D JC1/D3Z8WED1ECW.CCWE.Y92OAGY8MY9L+9MPCG/D5 C5IA5N9$PC5$CUZCY$5Y$527B+A4KZNQG5TKOWWD9FL%I8U$F7O2IBM85CWOC%LEZU4R/BXHDAHN 11$CA5MRI:AONFN7091K9FKIGIY%VWSSSU9%01FO2*FTPQ3C3F";

  private final TrustListService trustListService = mock(TrustListService.class);
  private final HcertDecodingService hcertDecodingService = new HcertDecodingService();
  private final HcertVerificationService hcertVerificationService = new HcertVerificationService(trustListService,
      hcertDecodingService);

  @Test
  void shouldVerifyGermanTheHealthCertificate() {
    HcertVerificationServerRequest hcertVerificationServerRequest = buildHcertVerificationServerRequest(
        GERMAN_QR_CODE_VACC_KEY_ID, GERMAN_QR_CODE_VACC_HC1_PREFIX, "");
    EUCertificates euCertificates = new EUCertificates();
    EUCertificate euCertificate = buildGermanCertificate();
    euCertificates.setCertificates(List.of(euCertificate));

    ResponseEntity<String> mockResponseEntity = new ResponseEntity<>("Mock", HttpStatus.OK);

    when(trustListService.getHcertCertificates(anyString())).thenReturn(mockResponseEntity);
    when(trustListService.buildEUHcertCertificates(any())).thenReturn(euCertificates);
    when(trustListService.getHcertCertificates(anyString())).thenReturn(mockResponseEntity);
    when(trustListService.buildEUHcertCertificates(any())).thenReturn(euCertificates);
    when(trustListService.convertCertificateToX509(anyString())).thenReturn(convertCertificateToX509(euCertificate));

    ResponseEntity<HcertVerificationServerResponse> hcertVerificationServerResponseResponseEntity = hcertVerificationService.verifyHealthCertificate(
        hcertVerificationServerRequest);

    HttpStatus actualStatusCode = hcertVerificationServerResponseResponseEntity.getStatusCode();
    boolean isVerified = Objects.requireNonNull(hcertVerificationServerResponseResponseEntity.getBody()).isVerified();

    assertEquals(HttpStatus.OK, actualStatusCode);
    assertTrue(isVerified);
  }

  @Test
  void shouldFailToVerifySwissHealthCertificate() {
    HcertVerificationServerRequest hcertVerificationServerRequest = buildHcertVerificationServerRequest(
        SWISS_QR_CODE_VACC_KID, SWISS_QR_CODE_VACC_HC1_PREFIX, BEARER_TOKEN);
    SwissCertificates swissCertificates = new SwissCertificates();
    SwissCertificate swissCertificate = buildSwissCertificate();
    swissCertificates.setCerts(List.of(swissCertificate));

    ResponseEntity<String> mockResponseEntity = new ResponseEntity<>("Mock", HttpStatus.OK);
    PublicKey publicKey = mock(PublicKey.class);

    when(trustListService.getHcertCertificates(anyString(), anyString())).thenReturn(mockResponseEntity);
    when(trustListService.buildSwissHcertCertificates(any())).thenReturn(swissCertificates);
    when(trustListService.getHcertCertificates(anyString(), anyString())).thenReturn(mockResponseEntity);
    when(trustListService.buildSwissHcertCertificates(any())).thenReturn(swissCertificates);
    when(trustListService.getRSAPublicKey(any(), any())).thenReturn(publicKey);

    ResponseEntity<HcertVerificationServerResponse> hcertVerificationServerResponseResponseEntity = hcertVerificationService.verifyHealthCertificate(
        hcertVerificationServerRequest);

    HttpStatus actualStatusCode = hcertVerificationServerResponseResponseEntity.getStatusCode();
    boolean isVerified = Objects.requireNonNull(hcertVerificationServerResponseResponseEntity.getBody()).isVerified();

    assertEquals(HttpStatus.OK, actualStatusCode);
    assertFalse(isVerified);
  }

  private X509Certificate convertCertificateToX509(EUCertificate euCertificate) {
    String pemTmp = PEM_PREFIX + "\n" + euCertificate.getRawData() + "\n" + PEM_POSTFIX;
    try (InputStream inputStream = new ByteArrayInputStream(pemTmp.getBytes())) {
      CertificateFactory certFactory = CertificateFactory.getInstance(X509_CERT_TYPE);
      return (X509Certificate) certFactory.generateCertificate(inputStream);
    } catch (IOException | CertificateException e) {
      throw new ServerException(KEY_SPEC_EXCEPTION, e);
    }
  }

  private SwissCertificate buildSwissCertificate() {
    SwissCertificate swissCertificate = new SwissCertificate();
    swissCertificate.setKeyId("mmrfzpMU6xc=");
    swissCertificate.setUse("sig");
    swissCertificate.setAlg("RS256");
    swissCertificate.setN(encodeBigInteger(
        "28643426111982487747207623696391865660030285296116281368931135391213954768668352065047828429912118191916692504257923329368028311088838078408883080084129559963160337995884099360311353957344177154085251876259945637242612905796207078928202713534542751423598328798837738384151239836355375509203616763555552220663884594037224135450304826164647837036786437817453226592549569164010589451617926240080358582980573952514052869914902012431661081870196603162910102996242616815361523336469918491878181427747105460113764824417992896392117278951884309266414460427411099657327478743127466562461843809921588324464283941631281695534813\n"));
    swissCertificate.setE(encodeBigInteger("65537"));
    swissCertificate.setSubjectPublicKeyInfo(null);
    swissCertificate.setCrv(null);
    swissCertificate.setX(null);
    swissCertificate.setY(null);
    return swissCertificate;
  }

  private String encodeBigInteger(String value) {
    return Base64.encodeBase64String(value.getBytes());
  }

  private EUCertificate buildGermanCertificate() {
    EUCertificate euCertificate = new EUCertificate();
    euCertificate.setCertificateType("DSC");
    euCertificate.setCountry("DE");
    euCertificate.setKid("DEsVUSvpFAE=");
    euCertificate.setRawData(
        "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");
    euCertificate.setSignature(
        "MIAGCSqGSIb3DQEHAqCAMIACAQExDTALBglghkgBZQMEAgEwgAYJKoZIhvcNAQcBAACggDCCAjAwggHXoAMCAQICFAMlvpT4fsWVOn2ZKLAukBwPx947MAoGCCqGSM49BAMCMG4xHDAaBgNVBAMME1Rlc3QgVGVhbSB1cGxvYWQgREUxCzAJBgNVBAYTAkRFMSUwIwYDVQQKDBxULVN5c3RlbXMgSW50ZXJuYXRpb25hbCBHbWJIMRowGAYDVQQLDBFEaWdpdGFsIFNvbHV0aW9uczAeFw0yMjAzMTYxMTU1MzJaFw0yNDAzMTYxMTU1MzJaMG4xHDAaBgNVBAMME1Rlc3QgVGVhbSB1cGxvYWQgREUxCzAJBgNVBAYTAkRFMSUwIwYDVQQKDBxULVN5c3RlbXMgSW50ZXJuYXRpb25hbCBHbWJIMRowGAYDVQQLDBFEaWdpdGFsIFNvbHV0aW9uczBZMBMGByqGSM49AgEGCCqGSM49AwEHA0IABKjHCrep3igNs9vvgaUo/S1JywILo1I7az3DWSGvu025RFWBGPVfpLYfvXIoBYheeD7FImRy0sreoJDTDGizjIGjUzBRMB0GA1UdDgQWBBTmkMcTG4Vp0YJwillQuSmqm3F/PTAfBgNVHSMEGDAWgBTmkMcTG4Vp0YJwillQuSmqm3F/PTAPBgNVHRMBAf8EBTADAQH/MAoGCCqGSM49BAMCA0cAMEQCIACEivAG3njblNcBBZZtoxVQg/9Zbp79fnSMMp4pznojAiAZhsdcUOCMIYG4hB9PA6+C23KM2QcxDWQOhMD4avl22QAAMYIB3DCCAdgCAQEwgYYwbjEcMBoGA1UEAwwTVGVzdCBUZWFtIHVwbG9hZCBERTELMAkGA1UEBhMCREUxJTAjBgNVBAoMHFQtU3lzdGVtcyBJbnRlcm5hdGlvbmFsIEdtYkgxGjAYBgNVBAsMEURpZ2l0YWwgU29sdXRpb25zAhQDJb6U+H7FlTp9mSiwLpAcD8feOzANBglghkgBZQMEAgEFAKCB5DAYBgkqhkiG9w0BCQMxCwYJKoZIhvcNAQcBMBwGCSqGSIb3DQEJBTEPFw0yMjA0MDgxMTMyMjZaMC8GCSqGSIb3DQEJBDEiBCAMSxVRK+kUAW7qI9peroZcY7u5BG3Ur0CeIwICxqV0dzB5BgkqhkiG9w0BCQ8xbDBqMAsGCWCGSAFlAwQBKjALBglghkgBZQMEARYwCwYJYIZIAWUDBAECMAoGCCqGSIb3DQMHMA4GCCqGSIb3DQMCAgIAgDANBggqhkiG9w0DAgIBQDAHBgUrDgMCBzANBggqhkiG9w0DAgIBKDAKBggqhkjOPQQDAgRIMEYCIQDZ6M9s/Fb+0Ztoq2KCbBj3twr0vB+sidp4633p/BUGswIhAI60ydNL2/+UbFFlJdAM+dbFm64PwZHKsonhEDGBIlFaAAAAAAAA");
    euCertificate.setThumbprint("0c4b15512be914016eea23da5eae865c63bbb9046dd4af409e230202c6a57477");
    euCertificate.setTimestamp("2021-06-05T11:43:55+02:00");
    return euCertificate;
  }

  private HcertVerificationServerRequest buildHcertVerificationServerRequest(String keyId,
      String hcertPrefix,
      String token) {
    HcertVerificationServerRequest hcertVerificationServerRequest = new HcertVerificationServerRequest();
    hcertVerificationServerRequest.setBearerToken(token);
    hcertVerificationServerRequest.setKeyId(keyId);
    hcertVerificationServerRequest.setHcertPrefix(hcertPrefix);
    return hcertVerificationServerRequest;
  }

}
