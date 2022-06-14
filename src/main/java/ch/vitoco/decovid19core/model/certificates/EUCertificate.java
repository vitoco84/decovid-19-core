package ch.vitoco.decovid19core.model.certificates;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

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
  @JsonProperty("certificateType")
  private String certificateType;
  /**
   * The Health Certificate country.
   */
  @JsonProperty("country")
  private String country;
  /**
   * The Health Certificate Key Identifier.
   */
  @JsonProperty("kid")
  private String kid;
  /**
   * The Raw Data of the Health Certificate.
   */
  @JsonProperty("rawData")
  private String rawData;
  /**
   * The Signature of the Health Certificate.
   */
  @JsonProperty("signature")
  private String signature;
  /**
   * The Thumbprint of the Health Certificate.
   */
  @JsonProperty("thumbprint")
  private String thumbprint;
  /**
   * The Time Stamp of the Health Certificate.
   */
  @JsonProperty("timestamp")
  private String timestamp;

}
