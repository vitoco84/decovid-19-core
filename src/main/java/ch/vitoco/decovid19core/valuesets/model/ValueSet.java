package ch.vitoco.decovid19core.valuesets.model;

import java.util.Map;

import lombok.Data;

/**
 * Representation class of the Health Certificate value set.
 */
@Data
public class ValueSet {

  /**
   * Value set id.
   */
  private String valueSetId;
  /**
   * Value set date version.
   */
  private String valueSetDate;
  /**
   * Value set values.
   */
  private Map<String, ValueSetValues> valueSetValues;

}
