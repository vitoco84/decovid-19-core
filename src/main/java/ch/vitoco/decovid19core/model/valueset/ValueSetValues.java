package ch.vitoco.decovid19core.model.valueset;

import lombok.Data;

/**
 * Representation class of the Health Certificate value set values.
 */
@Data
public class ValueSetValues {

  /**
   * The display name of the value.
   */
  private String display;
  /**
   * The language code.
   */
  private String lang;
  /**
   * Wheter this value is "active".
   */
  private boolean active;
  /**
   * The system.
   */
  private String system;
  /**
   * The version of the value.
   */
  private String version;

}
