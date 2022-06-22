package ch.vitoco.decovid19core.model.hcert;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Representation class of the Health Certificate content.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class HcertDTO {

  /**
   * Holder name information.
   */
  @Valid
  @JsonProperty("nam")
  private HcertHolder name;
  /**
   * Holder date of birth.
   */
  @NotBlank
  @Pattern(message = "Date format should be YYYY-MM-DD", regexp = "^\\d{4}-\\d{2}-\\d{2}$")
  @JsonProperty("dob")
  private String dateOfBirth;
  /**
   * Health Certificate version information.
   */
  @NotBlank
  @JsonProperty("ver")
  private String version;

}
