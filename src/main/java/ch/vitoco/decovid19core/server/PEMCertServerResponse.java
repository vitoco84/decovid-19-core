package ch.vitoco.decovid19core.server;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ch.vitoco.decovid19core.model.HcertPublicKeyDTO;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PEMCertServerResponse {

  private String publicKey;
  private String subject;
  private String signatureAlgorithm;
  private String validTo;
  private String validFrom;
  private String serialNumber;
  private String issuer;
  private HcertPublicKeyDTO publicKeyParams;

}