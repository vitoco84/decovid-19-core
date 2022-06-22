package ch.vitoco.decovid19core.model.hcert;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
  @NotBlank
  @JsonProperty("fn")
  private String surname;
  /**
   * Holder standardised surname.
   */
  @NotBlank
  @JsonProperty("fnt")
  private String standardSurname;
  /**
   * Holder forename.
   */
  @NotBlank
  @JsonProperty("gn")
  private String forename;
  /**
   * Holder standardised forename.
   */
  @NotBlank
  @JsonProperty("gnt")
  private String standardForename;

}
