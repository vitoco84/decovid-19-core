package ch.vitoco.decovid19core.service;

import static ch.vitoco.decovid19core.constants.ExceptionMessages.INVALID_SIGNATURE;

import java.util.NoSuchElementException;
import java.util.Objects;

import ch.vitoco.decovid19core.certificates.model.SwissActiveKeyIds;
import ch.vitoco.decovid19core.constants.HcertEndpointsApi;
import ch.vitoco.decovid19core.exception.ServerException;
import ch.vitoco.decovid19core.server.SwissHcertVerificationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service class for the Health Certificate verification process.
 */
@Service
public class HcertVerificationService {

  private final TrustListService trustListService;
  private final HcertDecodingService hcertDecodingService;

  public HcertVerificationService(TrustListService trustListService, HcertDecodingService hcertDecodingService) {
    this.trustListService = trustListService;
    this.hcertDecodingService = hcertDecodingService;
  }

  public ResponseEntity<String> verifyHealthCertificate(SwissHcertVerificationRequest swissHcertVerificationRequest) {
    if (isActiveKeyId(swissHcertVerificationRequest)) {
      return ResponseEntity.ok().body("isPresent");
    } else {
      return ResponseEntity.badRequest().build();
    }
  }

  private boolean isActiveKeyId(SwissHcertVerificationRequest swissHcertVerificationRequest) {
    if (swissHcertVerificationRequest.getKeyId().isBlank()) {
      return false;
    } else {
      try {
        ResponseEntity<String> certificates = trustListService.getHcertCertificates(
            HcertEndpointsApi.SWISS_ACTIVE_KID_API, swissHcertVerificationRequest.getBearerToken());
        SwissActiveKeyIds swissActiveKeyIds = trustListService.buildSwissHcertActiveKeyIds(
            Objects.requireNonNull(certificates.getBody()));
        return swissActiveKeyIds.getActiveKeyIds().contains(swissHcertVerificationRequest.getKeyId());
      } catch (NoSuchElementException e) {
        throw new ServerException(INVALID_SIGNATURE, e);
      }
    }
  }

}
