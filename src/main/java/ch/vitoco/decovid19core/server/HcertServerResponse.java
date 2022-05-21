package ch.vitoco.decovid19core.server;

import ch.vitoco.decovid19core.model.HcertDTO;
import ch.vitoco.decovid19core.model.HcertTimeStampDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
  private String hcertPrefix;
  /**
   * Health Certificate Payload.
   */
  private HcertDTO hcertContent;
  /**
   * Health Certificate Key Identifier.
   */
  private String hcertKID;
  /**
   * Health Certificate Signature Algorithm.
   */
  private String hcertAlgo;
  /**
   * Health Certificate Issuer.
   */
  private String hcertIssuer;
  /**
   * Health Certificate Time Stamp.
   */
  private HcertTimeStampDTO hcertTimeStamp;

}
