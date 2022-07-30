package ch.vitoco.decovid19core.model.hcert;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
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
  @Schema(description = "Disease or agent targeted", example = "COVID-19", required = true)
  @NotBlank
  @JsonProperty("tg")
  private String target;
  /**
   * The type of test.
   */
  @Schema(description = "The type of test", example = "Rapid Test", required = true)
  @NotBlank
  @JsonProperty("tt")
  private String typeOfTest;
  /**
   * Test name of the nucleic acid amplification test (NAAT).
   */
  @Schema(description = "Test name of the nucleic acid amplification test (NAAT)", example = "Roche LightCycler qPCR", required = true)
  @NotBlank
  @JsonProperty("nm")
  private String nucleicAcidAmplName;
  /**
   * Rapid antigen test (RAT) device identifier.
   */
  @Schema(description = "Rapid antigen test (RAT) device identifier", example = "Abbott Rapid Diagnostics, Panbio COVID-19 Ag Rapid Test", required = true)
  @NotBlank
  @JsonProperty("ma")
  private String testDeviceManufacturer;
  /**
   * Date and time of the test sample collection.
   */
  @Schema(description = "Date and time of the test sample collection", example = "2021-04-30", required = true)
  @NotBlank
  @Pattern(message = "Date format should be YYYY-MM-DD", regexp = "^\\d{4}-\\d{2}-\\d{2}$")
  @JsonProperty("sc")
  private String sampleCollectionDate;
  /**
   * Result of the test.
   */
  @Schema(description = "Result of the test", example = "Not detected", required = true)
  @NotBlank
  @JsonProperty("tr")
  private String testResult;
  /**
   * Testing centre or facility.
   */
  @Schema(description = "Testing centre or facility", example = "Test Center", required = true)
  @NotBlank
  @JsonProperty("tc")
  private String testingCentre;
  /**
   * Member state or third country in which the test was carried out.
   */
  @Schema(description = "Member state or third country in which the test was carried out", example = "CH", required = true)
  @NotBlank
  @JsonProperty("co")
  private String country;
  /**
   * Certificate issuer.
   */
  @Schema(description = "Certificate issuer", example = "Bundesamt für Gesundheit (BAG)", required = true)
  @NotBlank
  @JsonProperty("is")
  private String issuer;
  /**
   * Unique certificate identifier.
   */
  @Hidden
  @JsonProperty("ci")
  private String certIdentifier;

}
