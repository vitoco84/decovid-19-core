package ch.vitoco.decovid19core.model.certificates;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Representation class for the Swiss Health Certificates Trust List.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class SwissCertificates {

  /**
   * List of Health Certificates.
   */
  @JsonProperty("certs")
  private List<SwissCertificate> certs;

}
