package ch.vitoco.decovid19core.server;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Representation class of the PEM formatted Server Request.
 */
@Data
public class PEMCertServerRequest {

  /**
   * PEM formatted String.
   */
  @Schema(description = "PEM formatted String", example = "MIIEDzCCAfegAwIBAgIUdtfIwH49VX3gzjg7sw1okOP/KAIwDQYJKoZIhvcNAQELBQAwSTELMAkGA1UEBhMCSVQxHzAdBgNVBAoMFk1pbmlzdGVybyBkZWxsYSBTYWx1dGUxGTAXBgNVBAMMEEl0YWx5IERHQyBDU0NBIDEwHhcNMjExMTEyMTM0MTQ1WhcNMjMxMTEyMTM0MTQ0WjBIMQswCQYDVQQGEwJJVDEfMB0GA1UECgwWTWluaXN0ZXJvIGRlbGxhIFNhbHV0ZTEYMBYGA1UEAwwPSXRhbHkgREdDIERTQyAzMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEGwlU23NYZQba7t++TVejbvPxgS6wdW8uE7UPe14VkhXoKINhhrCT0EWHP+aPim6apT46Ktd0lfoNVTsbg2QQ2KOBujCBtzAfBgNVHSMEGDAWgBS+VOVpXmeSQImXYEEAB/pLRVCw/zBlBgNVHR8EXjBcMFqgWKBWhlRsZGFwOi8vY2Fkcy5kZ2MuZ292Lml0L0NOPUl0YWx5JTIwREdDJTIwQ1NDQSUyMHhcMSxPPU1pbmlzdGVybyUyMGRlbGxhJTIwU2FsdXRlLEM9SVQwHQYDVR0OBBYEFCR9+jzaaoSYO/7wB8YLfAk4Vw1FMA4GA1UdDwEB/wQEAwIHgDANBgkqhkiG9w0BAQsFAAOCAgEAokc+BGagX9kFseWotrm4fPrXBm6JLAxn9rC/Mz1awCXehbinERs+AVl029UApYXNp3/wiA57/9z1NkmMtZspBn1b0rJ4WP5hcRDhcsXxFvkSNx03/LwTCBi9iJSFNDQSv6bq8+YVSenamvwGJ+wNpemAIthrPshIGU2+MEipWhFmERYE6JaGJVFGs6n8r4IHzOx3gKeyUoGK2p4vM0Aw4LCfaqffEQz47lVMi+dDk3/h6CWWKZ6//+/15DKsuuj+BBAt9HBaqKAgCQke8RVceGJ1dsZFUW0yuCMqCSmoc3pksjQghYki9p4gs8Ex9khXCusRnCw81sG7VGrZgiDCEGHtbny774/CLCFNDpPC/CJLSEiyDP2exV6um2NEtAqQvGPlCdKVxHiLJ1FkunuHV0zYMLQi4+5mjAWbs4DuTgsOfkuZoJ7t8lFp9rqx3maNrew8Dw/w2nTuOHfk5f4xb99nlVXqA0rUPejZ/kf4ajU2AXrszxLAGXZnopryEcCSzxUSu1vRnv9NMtneTn3l0fDQTwPFVV2zquQUwl85Rm672r12Lupisn74UgwKgElxkgHjNRry++64ULoksydKWMz78ElfLV1tws8xoEx8I744pxsq7DTK52tcFuIZjASIfley6AbGLWzu8DUVGA3o/ANbavT56O8sXlT2OGipPx0=", required = true)
  @NotBlank
  @Pattern(message = "Should start with MII or -----BEGIN CERTIFICATE-----", regexp = "^(-----BEGIN CERTIFICATE-----|MII).*")
  @JsonProperty("pemCertificate")
  private String pemCertificate;

}
