package ch.vitoco.decovid19core.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class HcertContentDTO extends HcertDTO {

  private List<HcertRecovery> r;
  private List<HcertVaccination> v;
  private List<HcertTest> t;

}
