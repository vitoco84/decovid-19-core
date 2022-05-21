package ch.vitoco.decovid19core.constants;

import static ch.vitoco.decovid19core.constants.Const.UTILITY_CLASS_EXCEPTION;

/**
 * Representation class of Document Signing Certificates Endpoints.
 */
public final class Endpoints {

  /**
   * Health Certificates Trust List Certificates Endpoint used by German API.
   */
  public static final String DOCUMENT_CERTS = "https://de.dscg.ubirch.com/trustList/DSC/";
  /**
   * Health Certificates Public Key Endpoint used by German API.
   */
  public static final String DOCUMENT_PUBLIC_KEY = "https://github.com/Digitaler-Impfnachweis/covpass-ios/raw/main/Certificates/PROD_RKI/CA/pubkey.pem";
  /**
   * Health Certificates Active Key Identifiers Endpoint used by Swiss API.
   */
  public static final String SWISS_ACTIVE_KID = "https://www.cc.bit.admin.ch/trust/v2/keys/list/";
  /**
   * Health Certificates Trust List Certificates Endpoint used by Swiss API.
   */
  public static final String SWISS_CERTS = "https://www.cc.bit.admin.ch/trust/v1/keys/updates?certFormat=ANDROID";
  /**
   * Health Certificates Revocation List Certificates Endpoint used by Swiss API.
   */
  public static final String SWISS_REVOCATION_LIST = "https://www.cc.bit.admin.ch/trust/v2/revocationList";

  /**
   * Constructor.
   */
  private Endpoints() {
    throw new IllegalStateException(UTILITY_CLASS_EXCEPTION);
  }

}
