package ch.vitoco.decovid19core.constants;

import static ch.vitoco.decovid19core.constants.ExceptionMessages.UTILITY_CLASS_EXCEPTION;

/**
 * Representation class of Document Signing Certificates HcertEndpointsApi.
 */
public final class HcertEndpointsApi {

  /**
   * Health Certificates Trust List Certificates Endpoint used by German API.
   *
   * The Response is a JSON Object with a rawData field.
   * The rawData field is a PEM formatted X.509 certificate.
   */
  public static final String GERMAN_CERTS_API = "https://de.dscg.ubirch.com/trustList/DSC/";
  /**
   * Health Certificates Public Key Endpoint used by German API.
   */
  public static final String GERMAN_PUBLIC_KEY_API = "https://github.com/Digitaler-Impfnachweis/covpass-ios/raw/main/Certificates/PROD_RKI/CA/pubkey.pem";
  /**
   * Health Certificates Active Key Identifiers Endpoint used by Swiss API.
   */
  public static final String SWISS_ACTIVE_KID_API = "https://www.cc.bit.admin.ch/trust/v2/keys/list/";
  /**
   * Health Certificates Trust List Certificates Endpoint used by Swiss API.
   */
  public static final String SWISS_CERTS_API = "https://www.cc.bit.admin.ch/trust/v1/keys/updates?certFormat=ANDROID";
  /**
   * Health Certificates Revocation List Certificates Endpoint used by Swiss API.
   */
  public static final String SWISS_REVOCATION_LIST_API = "https://www.cc.bit.admin.ch/trust/v2/revocationList";

  /**
   * Constructor.
   */
  private HcertEndpointsApi() {
    throw new IllegalStateException(UTILITY_CLASS_EXCEPTION);
  }

}
