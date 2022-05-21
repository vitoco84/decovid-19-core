package ch.vitoco.decovid19core.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Representation class for the Health Certificates Trust List.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class HcertCertificates {

  /**
   * List of Health Certificates.
   */
  private List<HcertCertificate> certificates;

}
