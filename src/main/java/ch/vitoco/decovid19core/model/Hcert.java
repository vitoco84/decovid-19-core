package ch.vitoco.decovid19core.model;

import lombok.Data;

/**
 * Helper class of the Health Certificate mutual values.
 */
@Data
public class Hcert {

  /**
   * Disease or agent targeted.
   */
  private String tg;
  /**
   * Country.
   */
  private String co;

}
