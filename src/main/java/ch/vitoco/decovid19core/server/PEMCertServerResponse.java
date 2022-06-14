package ch.vitoco.decovid19core.server;

import ch.vitoco.decovid19core.model.hcert.HcertPublicKeyDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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
  @JsonProperty("publicKey")
  private String publicKey;
  /**
   * Certificate Subject.
   */
  @JsonProperty("subject")
  private String subject;
  /**
   * Certificate Signature Algorithm.
   */
  @JsonProperty("signatureAlgorithm")
  private String signatureAlgorithm;
  /**
   * Certificate Validity To.
   */
  @JsonProperty("validTo")
  private String validTo;
  /**
   * Certificate Validity From.
   */
  @JsonProperty("validFrom")
  private String validFrom;
  /**
   * Certificate Unique Serial Number.
   */
  @JsonProperty("serialNumber")
  private String serialNumber;
  /**
   * Certificate Issuer.
   */
  @JsonProperty("issuer")
  private String issuer;
  /**
   * Certificate Public Key Parameters.
   */
  @JsonProperty("publicKeyParams")
  private HcertPublicKeyDTO publicKeyParams;
  /**
   * Certificate Signature.
   */
  @JsonProperty("signature")
  private String signature;
  /**
   * Certificate Validity.
   */
  @JsonProperty("isValid")
  private boolean isValid;

}
