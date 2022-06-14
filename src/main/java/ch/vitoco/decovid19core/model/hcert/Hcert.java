package ch.vitoco.decovid19core.model.hcert;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Helper class of the Health Certificate mutual values.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Hcert {

  /**
   * Disease or agent targeted.
   */
  @JsonProperty("tg")
  private String target;
  /**
   * Country.
   */
  @JsonProperty("co")
  private String country;

}
