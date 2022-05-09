package ch.vitoco.decovid19core.model;

import java.util.List;

import lombok.Data;

@Data
public class HcertRecoveryDTO extends HcertDTO {

  private List<HcertRecovery> r;

}
