package ch.vitoco.decovid19core.model.certificates;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Representation class for the Swiss Revoked Health Certificate Trust List.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class SwissRevokedCertificates {

  /**
   * List of Revoked Unique Certificate Identifier.
   */
  @JsonProperty("revokedCerts")
  private List<String> revokedCerts;
  /**
   * The List valid duration.
   */
  @JsonProperty("validDuration")
  private String validDuration;

}
