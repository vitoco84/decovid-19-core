package ch.vitoco.decovid19core.model.hcert;

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
  @JsonProperty("fn")
  private String surname;
  /**
   * Holder standardised surname.
   */
  @JsonProperty("fnt")
  private String standardSurname;
  /**
   * Holder forename.
   */
  @JsonProperty("gn")
  private String forename;
  /**
   * Holder standardised forename.
   */
  @JsonProperty("gnt")
  private String standardForename;

}
