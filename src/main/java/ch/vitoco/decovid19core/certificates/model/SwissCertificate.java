package ch.vitoco.decovid19core.certificates.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Representation class for the Swiss Health Certificate Trust List.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class SwissCertificate {

  /**
   * The Active Key ID.
   */
  private String keyId;
  /**
   * The Use of the Certificate (Vaccination, Test, Recovery).
   */
  private String use;
  /**
   * The Signature Algorithm.
   */
  private String alg;
  /**
   * RSA Public Key Modulus.
   */
  private String n;
  /**
   * RSA Public Key Public Exponent.
   */
  private String e;
  /**
   * The Subject Public Key Info.
   */
  private String subjectPublicKeyInfo;
  /**
   * The Elliptic Curve.
   */
  private String crv;
  /**
   * EC Public Key Point X-Coordinate.
   */
  private String x;
  /**
   * EC Public Key Point Y-Coordinate.
   */
  private String y;

}
