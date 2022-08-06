package ch.vitoco.decovid19core.server;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Representation class of Base45EncodeServerRequest.
 */
@Data
public class Base45EncodeServerRequest {

  /**
   * String to be encoded to Base45
   */
  @Schema(description = "String to be encoded to Base45", example = "Hello World!", required = true)
  @NotBlank
  @JsonProperty("base45Encode")
  private String base45Encode;

}
