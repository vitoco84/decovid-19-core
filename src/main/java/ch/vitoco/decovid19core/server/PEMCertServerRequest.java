package ch.vitoco.decovid19core.server;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Representation class of the PEM formatted Server Request.
 */
@Data
public class PEMCertServerRequest {

  /**
   * PEM formatted String.
   */
  @NotBlank(message = "Must not be blank")
  @Pattern(message = "Should start with MII", regexp = "^MII.*")
  @JsonProperty("pemCertificate")
  private String pemCertificate;

}
