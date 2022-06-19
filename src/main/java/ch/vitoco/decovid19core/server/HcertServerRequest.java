package ch.vitoco.decovid19core.server;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Representation class of the Health Certificate Server Request.
 */
@Data
public class HcertServerRequest {

  /**
   * Health Certificate String Prefix starting with "HC1:"
   */
  @NotBlank
  @JsonProperty("hcertPrefix")
  private String hcertPrefix;

}
