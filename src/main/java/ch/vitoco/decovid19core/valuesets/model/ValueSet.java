package ch.vitoco.decovid19core.valuesets.model;

import java.util.Map;

import lombok.Data;

@Data
public class ValueSet {

  private String valueSetId;
  private String valueSetDate;
  private Map<String, ValueSetValues> valueSetValues;

}
