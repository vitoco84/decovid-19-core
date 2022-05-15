package ch.vitoco.decovid19core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class HcertRecovery {

  private String tg;
  private String fr;
  private String co;
  private String df;
  private String du;
  private String is;
  private String ci;

}
