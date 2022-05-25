package ch.vitoco.decovid19core.server;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * Representation class of a PEM formatted String.
 */
@Data
public class PEMCertServerRequest {

  /**
   * PEM formatted String.
   */
  @NotBlank
  private String pemCertificate;

}
