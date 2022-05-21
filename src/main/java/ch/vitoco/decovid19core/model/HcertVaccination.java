package ch.vitoco.decovid19core.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 * Representation class of the Health Certificate Vaccination information.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class HcertVaccination extends Hcert {

  /**
   * Unique certificate identifier.
   */
  private String ci;
  /**
   * Member state or third country in which the vaccine was administered.
   */
  private String co;
  /**
   * Number in a series of doses.
   */
  private Long dn;
  /**
   * Date of vaccination.
   */
  private String dt;
  /**
   * Certificate issuer.
   */
  private String is;
  /**
   * Covid-19 vaccine marketing authorisation holder or manufacturer.
   */
  private String ma;
  /**
   * Covid-19 vaccine product.
   */
  private String mp;
  /**
   * The overall number of doses in the series.
   */
  private Long sd;
  /**
   * Disease or agent targeted.
   */
  private String tg;
  /**
   * Covid-19 vaccine or prophylaxis.
   */
  private String vp;

}
