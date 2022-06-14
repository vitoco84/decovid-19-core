package ch.vitoco.decovid19core.model.certificates;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Representation class for the EU Health Certificates Trust List.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class EUCertificates {

  /**
   * List of Health Certificates.
   */
  @JsonProperty("certificates")
  private List<EUCertificate> certificates;

}
