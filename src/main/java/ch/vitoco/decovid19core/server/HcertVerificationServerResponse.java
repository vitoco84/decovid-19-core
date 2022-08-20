package ch.vitoco.decovid19core.server;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Representation class of the Health Certificate Verification Server Response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class HcertVerificationServerResponse {

  /**
   * Certificate Verified.
   */
  @JsonProperty("isHcertVerified")
  private boolean isHcertVerified;
  /**
   * Certificate Trust Chain Verified.
   */
  @JsonProperty("isTrustChainVerified")
  private boolean isTrustChainVerified;
  /**
   * Certificate Raw Content.
   */
  @JsonProperty("certRawContent")
  private String certRawContent;

}
