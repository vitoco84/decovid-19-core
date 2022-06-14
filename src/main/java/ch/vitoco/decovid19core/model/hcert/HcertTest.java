package ch.vitoco.decovid19core.model.hcert;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Representation class of the Health Certificate Test information.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties("dr")
@Data
public class HcertTest extends Hcert {

  /**
   * Disease or agent targeted.
   */
  @JsonProperty("tg")
  private String target;
  /**
   * The type of test.
   */
  @JsonProperty("tt")
  private String typeOfTest;
  /**
   * Test name of the nucleic acid amplification test (NAAT).
   */
  @JsonProperty("nm")
  private String nucleicAcidAmplName;
  /**
   * Rapid antigen test (RAT) device identifier.
   */
  @JsonProperty("ma")
  private String manufacturer;
  /**
   * Date and time of the test sample collection.
   */
  @JsonProperty("sc")
  private String sampleCollectionDate;
  /**
   * Result of the test.
   */
  @JsonProperty("tr")
  private String testResult;
  /**
   * Testing centre or facility.
   */
  @JsonProperty("tc")
  private String testingCentre;
  /**
   * Member state or third country in which the test was carried out.
   */
  @JsonProperty("co")
  private String country;
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
