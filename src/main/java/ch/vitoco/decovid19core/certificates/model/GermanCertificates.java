package ch.vitoco.decovid19core.certificates.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Representation class for the German Health Certificates Trust List.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class GermanCertificates {

  /**
   * List of Health Certificates.
   */
  private List<GermanCertificate> certificates;

}
