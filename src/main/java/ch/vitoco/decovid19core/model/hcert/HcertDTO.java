package ch.vitoco.decovid19core.model.hcert;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
  @Schema(description = "Holder date of birth", example = "1943-10-15", required = true)
  @NotBlank
  @Pattern(message = "Date format should be YYYY-MM-DD", regexp = "^\\d{4}-\\d{2}-\\d{2}$")
  @JsonProperty("dob")
  private String dateOfBirth;
  /**
   * Health Certificate version information.
   */
  @Schema(description = "Health Certificate version information", example = "1.0.0", required = true)
  @NotBlank
  @JsonProperty("ver")
  private String version;

}
