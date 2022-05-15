package ch.vitoco.decovid19core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class HcertTest {

  private String tg;
  private String tt;
  private String nm;
  private String ma;
  private String sc;
  private String tr;
  private String tc;
  private String co;
  private String is;
  private String ci;

}
