package ch.vitoco.decovid19core.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class HcertPublicKeyDTO {

  private String modulus;
  private String publicExponent;
  private String xCoord;
  private String yCoord;
  private String algo;
  private String bitLength;

}
