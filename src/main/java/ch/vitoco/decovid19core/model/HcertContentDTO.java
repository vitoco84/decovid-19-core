package ch.vitoco.decovid19core.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 * Representation class of the Health Certificate content.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class HcertContentDTO extends HcertDTO {

  /**
   * List of Recovery Health Certificates information.
   */
  private List<HcertRecovery> r;
  /**
   * List of Vaccination Health Certificates information.
   */
  private List<HcertVaccination> v;
  /**
   * List of Test Health Certificates information.
   */
  private List<HcertTest> t;

}