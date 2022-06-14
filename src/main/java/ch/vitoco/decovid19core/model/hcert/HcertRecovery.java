package ch.vitoco.decovid19core.model.hcert;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Representation class of the Health Certificate Recovery information.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class HcertRecovery extends Hcert {

  /**
   * Disease or agent targeted.
   */
  @JsonProperty("tg")
  private String target;
  /**
   * Date of the holder's first positive test result.
   */
  @JsonProperty("fr")
  private String firstPositiveDateResult;
  /**
   * Member state or third country in which the test was carried out.
   */
  @JsonProperty("co")
  private String country;
  /**
   * Health Certificate valid from.
   */
  @JsonProperty("df")
  private String validFrom;
  /**
   * Health Certificate valid until.
   */
  @JsonProperty("du")
  private String validTo;
  /**
   * Certificate issuer.
   */
  @JsonProperty("is")
  private String issuer;
  /**
   * Unique certificate identifier.
   */
  @JsonProperty("ci")
  private String certIdentifier;

}
