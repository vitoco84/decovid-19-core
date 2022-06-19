package ch.vitoco.decovid19core.model.certificates;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
  @JsonProperty("keyId")
  private String keyId;
  /**
   * The Use of the Certificate (Vaccination, Test, Recovery).
   */
  @JsonProperty("use")
  private String use;
  /**
   * The Signature Algorithm.
   */
  @JsonProperty("alg")
  private String alg;
  /**
   * RSA Public Key Modulus.
   */
  @JsonProperty("n")
  private String n;
  /**
   * RSA Public Key Public Exponent.
   */
  @JsonProperty("e")
  private String e;
  /**
   * The Subject Public Key Info.
   */
  @JsonProperty("subjectPublicKeyInfo")
  private String subjectPublicKeyInfo;
  /**
   * The Elliptic Curve.
   */
  @JsonProperty("crv")
  private String crv;
  /**
   * EC Public Key Point X-Coordinate.
   */
  @JsonProperty("x")
  private String x;
  /**
   * EC Public Key Point Y-Coordinate.
   */
  @JsonProperty("y")
  private String y;

}
