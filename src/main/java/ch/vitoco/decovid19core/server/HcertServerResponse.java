package ch.vitoco.decovid19core.server;

import ch.vitoco.decovid19core.model.HcertDTO;
import ch.vitoco.decovid19core.model.HcertTimeStampDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class HcertServerResponse {

  private String hcertPrefix;
  private HcertDTO hcertContent;
  private String hcertKID;
  private String hcertAlgo;
  private String hcertIssuer;
  private HcertTimeStampDTO hcertTimeStamp;

}
