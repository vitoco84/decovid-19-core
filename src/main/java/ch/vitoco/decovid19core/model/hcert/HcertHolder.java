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
  @NotBlank(message = "Must not be blank")
  @JsonProperty("fn")
  private String surname;
  /**
   * Holder standardised surname.
   */
  @NotBlank(message = "Must not be blank")
  @JsonProperty("fnt")
  private String standardSurname;
  /**
   * Holder forename.
   */
  @NotBlank(message = "Must not be blank")
  @JsonProperty("gn")
  private String forename;
  /**
   * Holder standardised forename.
   */
  @NotBlank(message = "Must not be blank")
  @JsonProperty("gnt")
  private String standardForename;

}
