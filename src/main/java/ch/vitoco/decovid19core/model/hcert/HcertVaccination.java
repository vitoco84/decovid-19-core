package ch.vitoco.decovid19core.model.hcert;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Representation class of the Health Certificate Vaccination information.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class HcertVaccination extends Hcert {

  /**
   * Unique certificate identifier.
   */
  @JsonProperty("ci")
  private String certIdentifier;
  /**
   * Member state or third country in which the vaccine was administered.
   */
  @JsonProperty("co")
  private String country;
  /**
   * Number in a series of doses.
   */
  @JsonProperty("dn")
  private Long numberOfDoses;
  /**
   * Date of vaccination.
   */
  @JsonProperty("dt")
  private String vaccinationDate;
  /**
   * Certificate issuer.
   */
  @JsonProperty("is")
  private String issuer;
  /**
   * Covid-19 vaccine marketing authorisation holder or manufacturer.
   */
  @JsonProperty("ma")
  private String manufacturer;
  /**
   * Covid-19 vaccine product.
   */
  @JsonProperty("mp")
  private String vaccineProduct;
  /**
   * The overall number of doses in the series.
   */
  @JsonProperty("sd")
  private Long overallNumberOfDoses;
  /**
   * Disease or agent targeted.
   */
  @JsonProperty("tg")
  private String target;
  /**
   * Covid-19 vaccine or prophylaxis.
   */
  @JsonProperty("vp")
  private String vaccineProphylaxis;

}
