package ch.vitoco.decovid19core.model;

import java.util.List;

import lombok.Data;

@Data
public class HcertVaccinationDTO extends HcertDTO {

  private List<HcertVaccination> v;

}
