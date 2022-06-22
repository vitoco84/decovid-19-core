package ch.vitoco.decovid19core.server;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

/**
 * Representation class of the QR-Code Generator Server Request.
 */
@Data
public class QRCodeServerRequest {

  /**
   * URL String for generating a QR-Code
   */
  @NotBlank
  @URL(message = "Should be a valid URL")
  @JsonProperty("url")
  private String url;

}
