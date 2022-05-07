package ch.vitoco.decovid19core.domain;

import lombok.Data;

@Data
public class HcertPayloadDTO {

  private HcertNam nam;
  private String dob;
  private String ver;

}
