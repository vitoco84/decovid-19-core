package ch.vitoco.decovid19core.model.valueset;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Representation class of the Health Certificate value set.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ValueSet {

  /**
   * Value set id.
   */
  @JsonProperty("valueSetId")
  private String valueSetId;
  /**
   * Value set date version.
   */
  @JsonProperty("valueSetDate")
  private String valueSetDate;
  /**
   * Value set values.
   */
  @JsonProperty("valueSetValues")
  private Map<String, ValueSetValues> valueSetValues;

}
