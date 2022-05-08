package ch.vitoco.decovid19core.server;

import ch.vitoco.decovid19core.model.HcertDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class HcertServerResponse {

  private String hcertPrefix;
  private HcertDTO hcertPayload;
  private String hcertKID;
  private String hcertAlgo;

}