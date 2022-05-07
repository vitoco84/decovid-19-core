package ch.vitoco.decovid19core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class HcertPayload {

  private String nam;
  private String dob;
  private String ver;
  private String v;

}
