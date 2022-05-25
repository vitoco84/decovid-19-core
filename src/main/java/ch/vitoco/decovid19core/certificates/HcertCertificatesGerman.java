package ch.vitoco.decovid19core.certificates;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Representation class for the Health Certificates Trust List.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class HcertCertificatesGerman {

  /**
   * List of Health Certificates.
   */
  private List<HcertCertificateGerman> certificates;

}
