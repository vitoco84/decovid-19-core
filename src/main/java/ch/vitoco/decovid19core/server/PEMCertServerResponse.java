package ch.vitoco.decovid19core.server;

import ch.vitoco.decovid19core.model.HcertPublicKeyDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Representation class of the PEM formatted Server Response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PEMCertServerResponse {

  /**
   * Certificate Public Key.
   */
  private String publicKey;
  /**
   * Certificate Subject.
   */
  private String subject;
  /**
   * Certificate Signature Algorithm.
   */
  private String signatureAlgorithm;
  /**
   * Certificate Validity To.
   */
  private String validTo;
  /**
   * Certificate Validity From.
   */
  private String validFrom;
  /**
   * Certificate Unique Serial Number.
   */
  private String serialNumber;
  /**
   * Certificate Issuer.
   */
  private String issuer;
  /**
   * Certificate Public Key Parameters.
   */
  private HcertPublicKeyDTO publicKeyParams;

}
