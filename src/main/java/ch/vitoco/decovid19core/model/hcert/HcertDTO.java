package ch.vitoco.decovid19core.model.hcert;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Representation class of the Health Certificate content.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class HcertDTO {

  /**
   * Holder name information.
   */
  @JsonProperty("nam")
  private HcertHolder name;
  /**
   * Holder date of birth.
   */
  @JsonProperty("dob")
  private String dateOfBirth;
  /**
   * Health Certificate version information.
   */
  @JsonProperty("ver")
  private String version;

}
