package ch.vitoco.decovid19core.server;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Representation class of the Health Certificate Verification Server Request.
 */
@Data
public class HcertVerificationServerRequest {

  /**
   * Bearer Token provided by BIT.
   */
  @JsonProperty("bearerToken")
  private String bearerToken;
  /**
   * Health Certificate Key Identifier.
   */
  @NotBlank
  @JsonProperty("keyId")
  private String keyId;
  /**
   * Health Certificate String Prefix starting with "HC1:"
   */
  @NotBlank
  @Pattern(message = "Should start with HC1:", regexp = "^HC1.*")
  @JsonProperty("hcertPrefix")
  private String hcertPrefix;

}
