package ch.vitoco.decovid19core.model;

import lombok.Data;

@Data
public class HcertTimeStampDTO {

  private String hcerExpirationTime;
  private String hcertIssuedAtTime;

}
