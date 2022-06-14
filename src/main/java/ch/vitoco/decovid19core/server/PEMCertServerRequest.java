package ch.vitoco.decovid19core.server;

import javax.validation.constraints.NotBlank;

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
  @NotBlank
  @JsonProperty("pemCertificate")
  private String pemCertificate;

}
