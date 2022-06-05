package ch.vitoco.decovid19core.model.hcert;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Representation class of the Health Certificate Time Stamp information.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class HcertTimeStampDTO {

  /**
   * The expiration time of the Health Certificate.
   */
  private String hcerExpirationTime;
  /**
   * The issued time of the Health Certificate.
   */
  private String hcertIssuedAtTime;

}
