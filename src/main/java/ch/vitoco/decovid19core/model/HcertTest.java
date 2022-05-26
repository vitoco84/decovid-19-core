package ch.vitoco.decovid19core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Representation class of the Health Certificate Test information.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties("dr")
@Data
public class HcertTest extends Hcert {

  /**
   * Disease or agent targeted.
   */
  private String tg;
  /**
   * The type of test.
   */
  private String tt;
  /**
   * Test name of the nucleic acid amplification test (NAAT).
   */
  private String nm;
  /**
   * Rapid antigen test (RAT) device identifier.
   */
  private String ma;
  /**
   * Date and time of the test sample collection.
   */
  private String sc;
  /**
   * Result of the test.
   */
  private String tr;
  /**
   * Testing centre or facility.
   */
  private String tc;
  /**
   * Member state or third country in which the test was carried out.
   */
  private String co;
  /**
   * Certificate issuer.
   */
  private String is;
  /**
   * Unique certificate identifier.
   */
  private String ci;

}
