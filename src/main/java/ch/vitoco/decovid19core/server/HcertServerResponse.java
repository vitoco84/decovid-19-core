package ch.vitoco.decovid19core.server;

import ch.vitoco.decovid19core.model.hcert.HcertDTO;
import ch.vitoco.decovid19core.model.hcert.HcertTimeStampDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Representation class of the Health Certificate Server Response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class HcertServerResponse {

  /**
   * Health Certificate String Prefix starting with "HC1:"
   */
  @JsonProperty("hcertPrefix")
  private String hcertPrefix;
  /**
   * Health Certificate Payload.
   */
  @JsonProperty("hcertContent")
  private HcertDTO hcertContent;
  /**
   * Health Certificate Key Identifier.
   */
  @JsonProperty("hcertKID")
  private String hcertKID;
  /**
   * Health Certificate Signature Algorithm.
   */
  @JsonProperty("hcertAlgo")
  private String hcertAlgo;
  /**
   * Health Certificate Issuer.
   */
  @JsonProperty("hcertIssuer")
  private String hcertIssuer;
  /**
   * Health Certificate Time Stamp.
   */
  @JsonProperty("hcertTimeStamp")
  private HcertTimeStampDTO hcertTimeStamp;
  /**
   * Health Certificate Signature.
   */
  @JsonProperty("hcertSignature")
  private String hcertSignature;

}
