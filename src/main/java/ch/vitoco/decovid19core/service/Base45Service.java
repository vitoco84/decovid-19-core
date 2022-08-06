package ch.vitoco.decovid19core.service;

import java.nio.charset.StandardCharsets;

import ch.vitoco.decovid19core.server.Base45DecodeServerRequest;
import ch.vitoco.decovid19core.server.Base45EncodeServerRequest;
import nl.minvws.encoding.Base45;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service class for Encoding and Decoding Base45 Strings.
 */
@Service
public class Base45Service {

  /**
   * Encodes a String to Base45
   *
   * @param base45EncodeServerRequest the Base45EncodeServerRequest
   * @return Base45 encoded String
   */
  public ResponseEntity<String> encodeBase45(Base45EncodeServerRequest base45EncodeServerRequest) {
    String encodedBase45String = Base45.getEncoder()
        .encodeToString(base45EncodeServerRequest.getBase45Encode().getBytes(StandardCharsets.UTF_8));
    return ResponseEntity.ok().body(encodedBase45String);
  }

  /**
   * Decodes a String from Base45
   *
   * @param base45DecodeServerRequest the Base45DecodeServerRequest
   * @return Base45 decoded String
   */
  public ResponseEntity<String> decodeBase45(Base45DecodeServerRequest base45DecodeServerRequest) {
    byte[] decodedInput = Base45.getDecoder().decode(base45DecodeServerRequest.getBase45Decode());
    String decodedBase45String = new String(decodedInput, StandardCharsets.UTF_8);
    return ResponseEntity.ok().body(decodedBase45String);
  }

}
