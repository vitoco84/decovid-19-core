package ch.vitoco.decovid19core.model.hcert;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

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
  @NotBlank
  @JsonProperty("tg")
  private String target;
  /**
   * The type of test.
   */
  @NotBlank
  @JsonProperty("tt")
  private String typeOfTest;
  /**
   * Test name of the nucleic acid amplification test (NAAT).
   */
  @NotBlank
  @JsonProperty("nm")
  private String nucleicAcidAmplName;
  /**
   * Rapid antigen test (RAT) device identifier.
   */
  @NotBlank
  @JsonProperty("ma")
  private String testDeviceManufacturer;
  /**
   * Date and time of the test sample collection.
   */
  @NotBlank
  @Pattern(message = "Date format should be YYYY-MM-DD", regexp = "^\\d{4}-\\d{2}-\\d{2}$")
  @JsonProperty("sc")
  private String sampleCollectionDate;
  /**
   * Result of the test.
   */
  @NotBlank
  @JsonProperty("tr")
  private String testResult;
  /**
   * Testing centre or facility.
   */
  @NotBlank
  @JsonProperty("tc")
  private String testingCentre;
  /**
   * Member state or third country in which the test was carried out.
   */
  @NotBlank
  @JsonProperty("co")
  private String country;
  /**
   * Certificate issuer.
   */
  @NotBlank
  @JsonProperty("is")
  private String issuer;
  /**
   * Unique certificate identifier.
   */
  @JsonProperty("ci")
  private String certIdentifier;

}
