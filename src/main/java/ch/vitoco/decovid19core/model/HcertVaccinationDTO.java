package ch.vitoco.decovid19core.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class HcertVaccinationDTO extends HcertDTO {

  private List<HcertVaccination> v;

}
