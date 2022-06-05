package ch.vitoco.decovid19core.model.hcert;

import com.fasterxml.jackson.annotation.JsonInclude;
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
  private String modulus;
  /**
   * RSA Public Key Public Exponent.
   */
  private String publicExponent;
  /**
   * EC Public Key Point X-Coordinate.
   */
  private String publicXCoord;
  /**
   * EC Public Key Point Y-Coordinate.
   */
  private String publicYCoord;
  /**
   * The Digital Signature Algorithm.
   */
  private String algo;
  /**
   * Length of the Public Key.
   */
  private String bitLength;

}
