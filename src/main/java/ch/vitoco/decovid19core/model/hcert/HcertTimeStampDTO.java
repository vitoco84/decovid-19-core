package ch.vitoco.decovid19core.model.hcert;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
  @JsonProperty("hcertExpirationTime")
  private String hcertExpirationTime;
  /**
   * The issued time of the Health Certificate.
   */
  @JsonProperty("hcertIssuedAtTime")
  private String hcertIssuedAtTime;

}
