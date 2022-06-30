package ch.vitoco.decovid19core.server;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Representation class of the Health Certificate Verification Server Request.
 */
@Data
public class HcertVerificationServerRequest {

  /**
   * Bearer Token provided by BIT.
   */
  @Schema(description = "Bearer Token provided by BIT", example = "8755gh84L-f9z7-8597-rto4-286c49r78162", required = true)
  @JsonProperty("bearerToken")
  private String bearerToken;
  /**
   * Health Certificate Key Identifier.
   */
  @Schema(description = "Health Certificate Key Identifier", example = "mmrfzpMU6xc=", required = true)
  @NotBlank
  @JsonProperty("keyId")
  private String keyId;
  /**
   * Health Certificate String Prefix starting with "HC1:"
   */
  @Schema(description = "Health Certificate String Prefix starting with \"HC1:\"", example = "HC1:NCFS605G0/3WUWGSLKH47GO0KNJ9DSWQIIWT9CK4600XKY-CE59-G80:84F35RIV R2F3FMMTTBY50.FK6ZK7:EDOLOPCO8F6%E3.DA%EOPC1G72A6YM83G7NA7H:6JM8D%6I:61S8ZW6HL6C460S8VF6VX6UPC0JCZ69FVCPD0LVC6JD846Y96A466W5B56+EDG8F3I80/D6$CBECSUER:C2$NS346$C2%E9VC- CSUE145GB8JA5B$D% D3IA4W5646946%96X47.JCP9EJY8L/5M/5546.96D463KC.SC4KCD3DX47B46IL6646H*6Z/E5JD%96IA74R6646407GVC*JC1A6/Q63W5KF6746TPCBEC7ZKW.CU2DNXO VD5$C JC3/DMP8$ILZEDZ CW.C9WE.Y9AY8+S9VIAI3D8WEVM8:S9C+9$PC5$CUZCY$5Y$5FBBM00T%LTAT1MOQYR8GUN$K15LIGG2P27%A46BT52VUTL.1*B89Y5B428HRSR3I/E5DS/8NBY4H2BCN8NP1D4B:0K9UQQ67BLTH21AF0V8G52R 62+5BQYCV03SO79O6K+8UXL$T4$%RT150DUHZK+Q9TIE+IMQU4E/Q4T303TKWNXTSORE.4WNPCJX66NN-2F9IHTYLR6IR UAB98RR1A0P9DL0CS5KZ*HEGT1%TQWELFQHG5/JO9TI:.T1JQF.K7 EJ 2/CI5GASQP7ULRX4-07%9W2139E2HMGW99Q DQJADB3UAJKUCOVLG+9T+J:15.12U+OBMCJ1KZ+C+87I8I9JGA0T%U2CMFHI5U:L400C.CC/K3KJZ3OM/D59TBL5AZFMPIW4", required = true)
  @NotBlank
  @Pattern(message = "Should start with HC1:", regexp = "^HC1.*")
  @JsonProperty("hcertPrefix")
  private String hcertPrefix;

}
