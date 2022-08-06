package ch.vitoco.decovid19core.server;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Representation class of Base45DecodeServerRequest.
 */
@Data
public class Base45DecodeServerRequest {

  /**
   * String to be decoded from Base45
   */
  @Schema(description = "String to be decoded from Base45", example = "%69 VD82EI2B.KESTC", required = true)
  @NotBlank
  @JsonProperty("base45Decode")
  private String base45Decode;

}
