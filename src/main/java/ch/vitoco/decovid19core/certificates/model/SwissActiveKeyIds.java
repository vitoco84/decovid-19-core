package ch.vitoco.decovid19core.certificates.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Representation class for the Swiss Health Certificate Active Key IDs Trust List.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class SwissActiveKeyIds {

  /**
   * List of Health Certificates Active Key IDs.
   */
  private List<String> activeKeyIds;
  /**
   * The List valid duration.
   */
  private String validDuration;
  /**
   * The List up to.
   */
  private String upTo;

}
