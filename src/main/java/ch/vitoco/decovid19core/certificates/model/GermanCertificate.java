package ch.vitoco.decovid19core.certificates.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Representation class for the German Health Certificate Trust List content from: <a href="https://eu-digital-green-certificates.github.io/dgc-gateway/#/Trust%20Lists/downloadTrustList">eu-dcc-gateway-rest-api</a>.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class GermanCertificate {

  /**
   * The Health Certificate Type.
   */
  private String certificateType;
  /**
   * The Health Certificate country.
   */
  private String country;
  /**
   * The Health Certificate Key Identifier.
   */
  private String kid;
  /**
   * The Raw Data of the Health Certificate.
   */
  private String rawData;
  /**
   * The Signature of the Health Certificate.
   */
  private String signature;
  /**
   * The Thumbprint of the Health Certificate.
   */
  private String thumbprint;
  /**
   * The Time Stamp of the Health Certificate.
   */
  private String timestamp;

}
