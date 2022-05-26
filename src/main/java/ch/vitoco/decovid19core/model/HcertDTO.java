package ch.vitoco.decovid19core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
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
  private HcertHolder nam;
  /**
   * Holder date of birth.
   */
  private String dob;
  /**
   * Health Certificate version information.
   */
  private String ver;

}
