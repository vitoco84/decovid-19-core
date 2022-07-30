package ch.vitoco.decovid19core.model.valueset;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Representation class of the Health Certificate value set values.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ValueSetValues {

  /**
   * The display name of the value.
   */
  @JsonProperty("display")
  private String display;
  /**
   * The language code.
   */
  @JsonProperty("lang")
  private String lang;
  /**
   * Wheter this value is "active".
   */
  @JsonProperty("active")
  private boolean active;
  /**
   * The system.
   */
  @JsonProperty("system")
  private String system;
  /**
   * The version of the value.
   */
  @JsonProperty("version")
  private String version;

}
