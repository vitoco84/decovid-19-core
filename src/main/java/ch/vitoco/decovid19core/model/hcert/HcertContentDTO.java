package ch.vitoco.decovid19core.model.hcert;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Hidden;
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
  @Hidden
  @JsonProperty("r")
  private List<HcertRecovery> recovery;
  /**
   * List of Vaccination Health Certificates information.
   */
  @Hidden
  @JsonProperty("v")
  private List<HcertVaccination> vaccination;
  /**
   * List of Test Health Certificates information.
   */
  @Valid
  @Size(message = "Max size should be 1", max = 1)
  @NotEmpty(message = "Must not be empty")
  @JsonProperty("t")
  private List<HcertTest> test;

}
