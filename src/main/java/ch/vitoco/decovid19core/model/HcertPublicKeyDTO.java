package ch.vitoco.decovid19core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Representation class of the Health Certificate Public Keys.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class HcertPublicKeyDTO {

  /**
   * Public Key Modulus.
   */
  private String modulus;
  /**
   * Public Key Public Exponent.
   */
  private String publicExponent;
  /**
   * Public Key Generator Point X-Coordinate.
   */
  private String xCoord;
  /**
   * Public Key Generator Point Y-Coordinate.
   */
  private String yCoord;
  /**
   * The Digital Signature Algorithm.
   */
  private String algo;
  /**
   * Lenght of the Public Key.
   */
  private String bitLength;

}
