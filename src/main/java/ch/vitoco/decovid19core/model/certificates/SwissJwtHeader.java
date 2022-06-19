package ch.vitoco.decovid19core.model.certificates;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class SwissJwtHeader {

  /**
   * The X509 Certificate chain.
   */
  @JsonProperty("x5c")
  private List<String> x5c;
  /**
   * The Signature Algorithm.
   */
  @JsonProperty("alg")
  private String alg;

}
