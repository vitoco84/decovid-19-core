package ch.vitoco.decovid19core.model;

import com.fasterxml.jackson.annotation.JsonInclude;

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
  private String fn;
  /**
   * Holder standardised surname.
   */
  private String fnt;
  /**
   * Holder forename.
   */
  private String gn;
  /**
   * Holder standardised forename.
   */
  private String gnt;

}
