package ch.vitoco.decovid19core.model;

import java.util.List;

import lombok.Data;

@Data
public class HcertTestDTO extends HcertDTO {

  private List<HcertTest> t;

}
