package ch.vitoco.decovid19core.model.hcert;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Representation class of the Health Certificate holder information.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class HcertHolder {

  /**
   * Holder surname.
   */
  @Schema(description = "Holder surname", example = "Bob", required = true)
  @NotBlank
  @JsonProperty("fn")
  private String surname;
  /**
   * Holder standardised surname.
   */
  @Schema(description = "Holder standardised surname", example = "BOB", required = true)
  @NotBlank
  @JsonProperty("fnt")
  private String standardSurname;
  /**
   * Holder forename.
   */
  @Schema(description = "Holder forename", example = "Uncle", required = true)
  @NotBlank
  @JsonProperty("gn")
  private String forename;
  /**
   * Holder standardised forename.
   */
  @Schema(description = "Holder standardised forename", example = "UNCLE", required = true)
  @NotBlank
  @JsonProperty("gnt")
  private String standardForename;

}
