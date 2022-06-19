package ch.vitoco.decovid19core.config;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("endpoints")
@Validated
@Data
public class ConfigProperties {

  /**
   * Health Certificates Trust List Certificates Endpoint used by German API.
   *
   * The Response is a JSON Object with a rawData field.
   * The rawData field is a PEM formatted X.509 certificate.
   */
  @NotBlank
  private String germanCertsApi;
  /**
   * Health Test Certificates Trust List Certificates Endpoint used by German API.
   */
  @NotBlank
  private String germanTestCertsApi;
  /**
   * Health Certificates Public Key Endpoint used by German API.
   */
  @NotBlank
  private String germanPublicKeyApi;
  /**
   * Health Certificates Active Key Identifiers Endpoint used by Swiss API.
   */
  @NotBlank
  private String swissActiveKidApi;
  /**
   * Health Certificates Trust List Certificates Endpoint used by Swiss API.
   */
  @NotBlank
  private String swissCertsApi;
  /**
   * Health Certificates Revocation List Certificates Endpoint used by Swiss API.
   */
  @NotBlank
  private String swissRevocationListApi;
  /**
   * Swiss Root Certificate Endpoint.
   */
  @NotBlank
  private String swissRootCertApi;

}
