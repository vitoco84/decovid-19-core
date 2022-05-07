package ch.vitoco.decovid19core.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class HcertPayloadTestedDTO extends HcertPayloadDTO {

  private List<HcertTested> t;

}
