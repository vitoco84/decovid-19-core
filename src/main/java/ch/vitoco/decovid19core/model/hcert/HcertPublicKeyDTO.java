package ch.vitoco.decovid19core.model.hcert;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Representation class of the Health Certificate Public Keys.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class HcertPublicKeyDTO {

  /**
   * RSA Public Key Modulus.
   */
  @JsonProperty("modulus")
  private String modulus;
  /**
   * RSA Public Key Public Exponent.
   */
  @JsonProperty("publicExponent")
  private String publicExponent;
  /**
   * EC Public Key Point X-Coordinate.
   */
  @JsonProperty("publicXCoord")
  private String publicXCoord;
  /**
   * EC Public Key Point Y-Coordinate.
   */
  @JsonProperty("publicYCoord")
  private String publicYCoord;
  /**
   * The Digital Signature Algorithm.
   */
  @JsonProperty("algo")
  private String algo;
  /**
   * Length of the Public Key.
   */
  @JsonProperty("bitLength")
  private String bitLength;

}
