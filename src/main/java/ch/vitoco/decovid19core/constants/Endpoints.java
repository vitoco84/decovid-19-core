package ch.vitoco.decovid19core.constants;

import static ch.vitoco.decovid19core.constants.Const.UTILITY_CLASS_EXCEPTION;

/**
 * Representation class of Document Signing Certificates Endpoints.
 */
public final class Endpoints {

  /**
   * Health Certificates Trust List Certificates Endpoint used by German API.
   *
   * The Response is a JSON Object with a rawData field.
   * The rawData field is a PEM formatted X.509 certificate.
   */
  public static final String GERMAN_CERTS = "https://de.dscg.ubirch.com/trustList/DSC/";
  /**
   * Health Certificates Public Key Endpoint used by German API.
   */
  public static final String GERMAN_PUBLIC_KEY = "https://github.com/Digitaler-Impfnachweis/covpass-ios/raw/main/Certificates/PROD_RKI/CA/pubkey.pem";
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
   * Health Certificates Public Keys used by the Netherlands API.
   *
   * The Response contains a payload which can be Base64 decoded to a JSON Object with European Public Keys.
   */
  public static final String NETHERLANDS_PUBLIC_KEY = "https://verifier-api.coronacheck.nl/v4/verifier/public_keys";

  /**
   * Constructor.
   */
  private Endpoints() {
    throw new IllegalStateException(UTILITY_CLASS_EXCEPTION);
  }

}
