package ch.vitoco.decovid19core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class HcertVaccination {

  private String ci;
  private String co;
  private Long dn;
  private String dt;
  private String is;
  private String ma;
  private String mp;
  private Long sd;
  private String tg;
  private String vp;

}
