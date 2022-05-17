package ch.vitoco.decovid19core.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 * Representation class of the Health Certificate Recovery information.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class HcertRecovery extends Hcert {

  /**
   * Disease or agent targeted.
   */
  private String tg;
  /**
   * Date of the holder's first positive test result.
   */
  private String fr;
  /**
   * Member state or third country in which the test was carried out.
   */
  private String co;
  /**
   * Health Certificate valid from.
   */
  private String df;
  /**
   * Health Certificate valid until.
   */
  private String du;
  /**
   * Certificate issuer.
   */
  private String is;
  /**
   * Unique certificate identifier.
   */
  private String ci;

}
