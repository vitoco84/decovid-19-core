package ch.vitoco.decovid19core.server;

import ch.vitoco.decovid19core.domain.HcertPayloadDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class HcertServerResponse {

  private String hcertPrefix;
  private HcertPayloadDTO hcertPayload;

}
