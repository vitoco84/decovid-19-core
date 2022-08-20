package ch.vitoco.decovid19core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;

import ch.vitoco.decovid19core.server.Base45DecodeServerRequest;
import ch.vitoco.decovid19core.server.Base45EncodeServerRequest;
import org.junit.jupiter.api.Test;

class Base45ServiceTest {

  private static final String SWISS_QR_CODE_VACC_WITHOUT_HC1_PREFIX = "NCFS605G0/3WUWGSLKH47GO0KNJ9DSWQIIWT9CK4600XKY-CE59-G80:84F35RIV R2F3FMMTTBY50.FK6ZK7:EDOLOPCO8F6%E3.DA%EOPC1G72A6YM83G7NA7H:6JM8D%6I:61S8ZW6HL6C460S8VF6VX6UPC0JCZ69FVCPD0LVC6JD846Y96A466W5B56+EDG8F3I80/D6$CBECSUER:C2$NS346$C2%E9VC- CSUE145GB8JA5B$D% D3IA4W5646946%96X47.JCP9EJY8L/5M/5546.96D463KC.SC4KCD3DX47B46IL6646H*6Z/E5JD%96IA74R6646407GVC*JC1A6/Q63W5KF6746TPCBEC7ZKW.CU2DNXO VD5$C JC3/DMP8$ILZEDZ CW.C9WE.Y9AY8+S9VIAI3D8WEVM8:S9C+9$PC5$CUZCY$5Y$5FBBM00T%LTAT1MOQYR8GUN$K15LIGG2P27%A46BT52VUTL.1*B89Y5B428HRSR3I/E5DS/8NBY4H2BCN8NP1D4B:0K9UQQ67BLTH21AF0V8G52R 62+5BQYCV03SO79O6K+8UXL$T4$%RT150DUHZK+Q9TIE+IMQU4E/Q4T303TKWNXTSORE.4WNPCJX66NN-2F9IHTYLR6IR UAB98RR1A0P9DL0CS5KZ*HEGT1%TQWELFQHG5/JO9TI:.T1JQF.K7 EJ 2/CI5GASQP7ULRX4-07%9W2139E2HMGW99Q DQJADB3UAJKUCOVLG+9T+J:15.12U+OBMCJ1KZ+C+87I8I9JGA0T%U2CMFHI5U:L400C.CC/K3KJZ3OM/D59TBL5AZFMPIW4";

  private final Base45Service base45Service = new Base45Service();

  @Test
  void shouldDecodeStringToBase45() {
    Base45DecodeServerRequest base45DecodeServerRequest = new Base45DecodeServerRequest();
    base45DecodeServerRequest.setBase45Decode(SWISS_QR_CODE_VACC_WITHOUT_HC1_PREFIX);
    String actual = base45Service.decodeBase45(base45DecodeServerRequest).getBody();

    assertTrue(Objects.requireNonNull(actual).contains("Bundesamt für Gesundheit"));
  }

  @Test
  void shouldEncodeStringToBase45() {
    Base45EncodeServerRequest base45EncodeServerRequest = new Base45EncodeServerRequest();
    base45EncodeServerRequest.setBase45Encode("Hello World!");
    String actual = base45Service.encodeBase45(base45EncodeServerRequest).getBody();
    String expected = "%69 VD82EI2B.KESTC";

    assertEquals(expected, actual);
  }

}
