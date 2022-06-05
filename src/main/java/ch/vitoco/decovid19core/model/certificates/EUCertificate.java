package ch.vitoco.decovid19core.model.certificates;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Representation class for the EU Health Certificate Trust List Content.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class EUCertificate {

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
