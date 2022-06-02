package ch.vitoco.decovid19core.server;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * Representation class of the Swiss Health Certificate Verification Server Request.
 */
@Data
public class SwissHcertVerificationRequest {

  /**
   * Bearer Token provided by BIT.
   */
  @NotBlank
  private String bearerToken;
  /**
   * Health Certificate Key Identifier.
   */
  @NotBlank
  private String keyId;
  /**
   * Health Certificate String Prefix starting with "HC1:"
   */
  @NotBlank
  private String hcertPrefix;

}
