package ch.vitoco.decovid19core.server;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class HcertServerResponse {

  private String hcertPrefix;
  private String hcertPayload;

}
