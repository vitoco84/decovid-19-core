package ch.vitoco.decovid19core.certificates.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
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
  private List<String> revokedCerts;
  /**
   * The List valid duration.
   */
  private String validDuration;

}
