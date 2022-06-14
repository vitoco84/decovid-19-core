package ch.vitoco.decovid19core.model.certificates;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

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
  @JsonProperty("activeKeyIds")
  private List<String> activeKeyIds;
  /**
   * The List valid duration.
   */
  @JsonProperty("validDuration")
  private String validDuration;
  /**
   * The List up to.
   */
  @JsonProperty("upTo")
  private String upTo;

}
