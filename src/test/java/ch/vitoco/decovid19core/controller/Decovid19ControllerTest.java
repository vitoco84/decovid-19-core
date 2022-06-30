package ch.vitoco.decovid19core.controller;

import java.util.List;

import ch.vitoco.decovid19core.model.hcert.HcertContentDTO;
import ch.vitoco.decovid19core.model.hcert.HcertHolder;
import ch.vitoco.decovid19core.model.hcert.HcertTimeStampDTO;
import ch.vitoco.decovid19core.model.hcert.HcertVaccination;
import ch.vitoco.decovid19core.server.HcertServerResponse;
import ch.vitoco.decovid19core.server.ValidationErrorServerResponse;
import ch.vitoco.decovid19core.validation.ValidationError;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class Decovid19ControllerTest {

  @Autowired
  private MockMvc mockMvc;


  @Test
  void shouldReturnBadRequestForWrongHcertPrefix() throws Exception {
    String mockContent = "{\"hcertPrefix\": \"foobar\"}";

    ValidationErrorServerResponse validationErrorServerResponse = new ValidationErrorServerResponse();
    validationErrorServerResponse.getValidationErrors()
        .add(new ValidationError("hcertPrefix", "Should start with HC1:"));
    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(validationErrorServerResponse);

    mockMvc.perform(MockMvcRequestBuilders.post("/decovid19/hcert/prefix")
            .content(mockContent)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.content().json(json));
  }

  @Test
  void shouldReturnBadRequestForEmptyHcertPrefix() throws Exception {
    String mockContent = "{\"hcertPrefix\": \"\"}";

    ValidationErrorServerResponse validationErrorServerResponse = new ValidationErrorServerResponse();
    validationErrorServerResponse.getValidationErrors()
        .add(new ValidationError("hcertPrefix", "Should start with HC1:"));
    validationErrorServerResponse.getValidationErrors().add(new ValidationError("hcertPrefix", "must not be blank"));
    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(validationErrorServerResponse);

    mockMvc.perform(MockMvcRequestBuilders.post("/decovid19/hcert/prefix")
            .content(mockContent)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.content().json(json));
  }

  @Test
  void shouldReturnOkResponseWithHcertServerResponse() throws Exception {
    String mockContent = "{\"hcertPrefix\": \"HC1:NCFS605G0/3WUWGSLKH47GO0KNJ9DSWQIIWT9CK4600XKY-CE59-G80:84F35RIV R2F3FMMTTBY50.FK6ZK7:EDOLOPCO8F6%E3.DA%EOPC1G72A6YM83G7NA7H:6JM8D%6I:61S8ZW6HL6C460S8VF6VX6UPC0JCZ69FVCPD0LVC6JD846Y96A466W5B56+EDG8F3I80/D6$CBECSUER:C2$NS346$C2%E9VC- CSUE145GB8JA5B$D% D3IA4W5646946%96X47.JCP9EJY8L/5M/5546.96D463KC.SC4KCD3DX47B46IL6646H*6Z/E5JD%96IA74R6646407GVC*JC1A6/Q63W5KF6746TPCBEC7ZKW.CU2DNXO VD5$C JC3/DMP8$ILZEDZ CW.C9WE.Y9AY8+S9VIAI3D8WEVM8:S9C+9$PC5$CUZCY$5Y$5FBBM00T%LTAT1MOQYR8GUN$K15LIGG2P27%A46BT52VUTL.1*B89Y5B428HRSR3I/E5DS/8NBY4H2BCN8NP1D4B:0K9UQQ67BLTH21AF0V8G52R 62+5BQYCV03SO79O6K+8UXL$T4$%RT150DUHZK+Q9TIE+IMQU4E/Q4T303TKWNXTSORE.4WNPCJX66NN-2F9IHTYLR6IR UAB98RR1A0P9DL0CS5KZ*HEGT1%TQWELFQHG5/JO9TI:.T1JQF.K7 EJ 2/CI5GASQP7ULRX4-07%9W2139E2HMGW99Q DQJADB3UAJKUCOVLG+9T+J:15.12U+OBMCJ1KZ+C+87I8I9JGA0T%U2CMFHI5U:L400C.CC/K3KJZ3OM/D59TBL5AZFMPIW4\"}";

    HcertServerResponse hcertServerResponse = buildHcertServerResponse();
    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(hcertServerResponse);

    mockMvc.perform(MockMvcRequestBuilders.post("/decovid19/hcert/prefix")
            .content(mockContent)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().json(json));
  }

  @Test
  void shouldReturnBadRequestForInvalidUrl() throws Exception {
    String mockContent = "{\"url\": \"foobar\"}";

    ValidationErrorServerResponse validationErrorServerResponse = new ValidationErrorServerResponse();
    validationErrorServerResponse.getValidationErrors().add(new ValidationError("url", "Should be a valid URL"));
    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(validationErrorServerResponse);

    mockMvc.perform(MockMvcRequestBuilders.post("/decovid19/hcert/qrcode/url")
            .content(mockContent)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.content().json(json));
  }

  @Test
  void shouldReturnOkResponseForValidUrl() throws Exception {
    String mockContent = "{\"url\": \"https://www.google.ch/\"}";

    mockMvc.perform(MockMvcRequestBuilders.post("/decovid19/hcert/qrcode/url")
        .content(mockContent)
        .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void shouldReturnOkResponseForValidUrlForClient() throws Exception {
    String mockContent = "{\"url\": \"https://www.google.ch/\"}";

    mockMvc.perform(MockMvcRequestBuilders.post("/decovid19/hcert/qrcode/url/client")
        .content(mockContent)
        .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void shouldReturnBadRequestForWrongPEMPrefix() throws Exception {
    String mockContent = "{\"pemCertificate\": \"foobar\"}";

    ValidationErrorServerResponse validationErrorServerResponse = new ValidationErrorServerResponse();
    validationErrorServerResponse.getValidationErrors()
        .add(new ValidationError("pemCertificate", "Should start with MII"));
    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(validationErrorServerResponse);

    mockMvc.perform(MockMvcRequestBuilders.post("/decovid19/hcert/qrcode/pem")
            .content(mockContent)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.content().json(json));
  }

  @Test
  void shouldReturnBadRequestForWrongHcertVerificationServerRequest() throws Exception {
    String mockContent = "{\"bearerToken\": \"\", \"keyId\": \"\", \"hcertPrefix\": \"foobar\"}";

    ValidationErrorServerResponse validationErrorServerResponse = new ValidationErrorServerResponse();
    validationErrorServerResponse.getValidationErrors()
        .add(new ValidationError("hcertPrefix", "Should start with HC1:"));
    validationErrorServerResponse.getValidationErrors().add(new ValidationError("keyId", "must not be blank"));
    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(validationErrorServerResponse);

    mockMvc.perform(MockMvcRequestBuilders.post("/decovid19/hcert/verify")
            .content(mockContent)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.content().json(json));
  }

  @Test
  void shouldReturnBadRequestForWrongHcertContentDTOServerRequest() throws Exception {
    String mockContent = "{\"nam\":{\"fn\":\"Uncle\",\"fnt\":\"UNCLE\",\"gn\":\"Bob\",\"gnt\":\"BOB\"},\"dob\":\"01-02-1943\",\"ver\":\"1.0.0\",\"t\":[]}";

    ValidationErrorServerResponse validationErrorServerResponse = new ValidationErrorServerResponse();
    validationErrorServerResponse.getValidationErrors().add(new ValidationError("test", "Must not be empty"));
    validationErrorServerResponse.getValidationErrors()
        .add(new ValidationError("dateOfBirth", "Date format should be YYYY-MM-DD"));
    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(validationErrorServerResponse);

    mockMvc.perform(MockMvcRequestBuilders.post("/decovid19/hcert/qrcode/hcert")
            .content(mockContent)
            .contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.content().json(json));
  }

  @Test
  void shouldReturnOkResponseForValidHcertContentDTOServerRequest() throws Exception {
    String mockContent = "{\"nam\":{\"fn\":\"Uncle\",\"fnt\":\"UNCLE\",\"gn\":\"Bob\",\"gnt\":\"BOB\"},\"dob\":\"1943-02-01\",\"ver\":\"1.0.0\",\"t\":[{\"tg\":\"COVID-19\",\"co\":\"Switzerland\",\"tt\":\"RapidTest\",\"nm\":\"COVID-19\",\"ma\":\"COVID-19Test\",\"sc\":\"2021-04-30\",\"tr\":\"Notdetected\",\"tc\":\"TestCenter\",\"is\":\"BundesamtfürGesundheit(BAG)\"}]}";

    mockMvc.perform(MockMvcRequestBuilders.post("/decovid19/hcert/qrcode/hcert")
        .content(mockContent)
        .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void shouldReturnOkResponseForValidHcertContentDTOServerRequestForClient() throws Exception {
    String mockContent = "{\"nam\":{\"fn\":\"Uncle\",\"fnt\":\"UNCLE\",\"gn\":\"Bob\",\"gnt\":\"BOB\"},\"dob\":\"1943-02-01\",\"ver\":\"1.0.0\",\"t\":[{\"tg\":\"COVID-19\",\"co\":\"Switzerland\",\"tt\":\"RapidTest\",\"nm\":\"COVID-19\",\"ma\":\"COVID-19Test\",\"sc\":\"2021-04-30\",\"tr\":\"Notdetected\",\"tc\":\"TestCenter\",\"is\":\"BundesamtfürGesundheit(BAG)\"}]}";

    mockMvc.perform(MockMvcRequestBuilders.post("/decovid19/hcert/qrcode/hcert/client")
        .content(mockContent)
        .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void shouldReturnBadRequestForWrongFileExtension() throws Exception {
    MockMultipartFile mockMultipartFile = new MockMultipartFile("imageFile", "fileName.pdf",
        MediaType.MULTIPART_FORM_DATA_VALUE, new byte[0]);

    ValidationErrorServerResponse validationErrorServerResponse = new ValidationErrorServerResponse();
    validationErrorServerResponse.getValidationErrors()
        .add(new ValidationError("decodeHealthCertificateContent.imageFile",
            "Only png, jpg, jpeg or gif files are allowed"));
    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(validationErrorServerResponse);

    mockMvc.perform(MockMvcRequestBuilders.multipart("/decovid19/hcert/qrcode").file(mockMultipartFile))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.content().json(json));
  }

  private HcertServerResponse buildHcertServerResponse() {
    HcertTimeStampDTO hcertTimeStampDTO = new HcertTimeStampDTO();
    hcertTimeStampDTO.setHcertExpirationTime("2022-05-29T07:55:08Z");
    hcertTimeStampDTO.setHcertIssuedAtTime("2021-05-29T07:55:08Z");
    hcertTimeStampDTO.setHcertExpired(true);

    HcertHolder hcertHolder = new HcertHolder();
    hcertHolder.setForename("Céline");
    hcertHolder.setSurname("Müller");
    hcertHolder.setStandardForename("CELINE");
    hcertHolder.setStandardSurname("MUELLER");

    HcertVaccination hcertVaccination = new HcertVaccination();
    hcertVaccination.setCertIdentifier("urn:uvci:01:CH:2987CC9617DD5593806D4285");
    hcertVaccination.setCountry("Switzerland");
    hcertVaccination.setNumberOfDoses(2L);
    hcertVaccination.setVaccinationDate("2021-04-30");
    hcertVaccination.setIssuer("Bundesamt für Gesundheit (BAG)");
    hcertVaccination.setManufacturer("Moderna Biotech Spain S.L.");
    hcertVaccination.setVaccineProduct("Spikevax");
    hcertVaccination.setOverallNumberOfDoses(2L);
    hcertVaccination.setTarget("COVID-19");
    hcertVaccination.setVaccineProphylaxis("SARS-CoV-2 mRNA vaccine");

    HcertContentDTO hcertContentDTO = new HcertContentDTO();
    hcertContentDTO.setName(hcertHolder);
    hcertContentDTO.setVersion("1.0.0");
    hcertContentDTO.setDateOfBirth("1943-02-01");
    hcertContentDTO.setVaccination(List.of(hcertVaccination));

    HcertServerResponse hcertServerResponse = new HcertServerResponse();
    hcertServerResponse.setHcertPrefix(
        "HC1:NCFS605G0/3WUWGSLKH47GO0KNJ9DSWQIIWT9CK4600XKY-CE59-G80:84F35RIV R2F3FMMTTBY50.FK6ZK7:EDOLOPCO8F6%E3.DA%EOPC1G72A6YM83G7NA7H:6JM8D%6I:61S8ZW6HL6C460S8VF6VX6UPC0JCZ69FVCPD0LVC6JD846Y96A466W5B56+EDG8F3I80/D6$CBECSUER:C2$NS346$C2%E9VC- CSUE145GB8JA5B$D% D3IA4W5646946%96X47.JCP9EJY8L/5M/5546.96D463KC.SC4KCD3DX47B46IL6646H*6Z/E5JD%96IA74R6646407GVC*JC1A6/Q63W5KF6746TPCBEC7ZKW.CU2DNXO VD5$C JC3/DMP8$ILZEDZ CW.C9WE.Y9AY8+S9VIAI3D8WEVM8:S9C+9$PC5$CUZCY$5Y$5FBBM00T%LTAT1MOQYR8GUN$K15LIGG2P27%A46BT52VUTL.1*B89Y5B428HRSR3I/E5DS/8NBY4H2BCN8NP1D4B:0K9UQQ67BLTH21AF0V8G52R 62+5BQYCV03SO79O6K+8UXL$T4$%RT150DUHZK+Q9TIE+IMQU4E/Q4T303TKWNXTSORE.4WNPCJX66NN-2F9IHTYLR6IR UAB98RR1A0P9DL0CS5KZ*HEGT1%TQWELFQHG5/JO9TI:.T1JQF.K7 EJ 2/CI5GASQP7ULRX4-07%9W2139E2HMGW99Q DQJADB3UAJKUCOVLG+9T+J:15.12U+OBMCJ1KZ+C+87I8I9JGA0T%U2CMFHI5U:L400C.CC/K3KJZ3OM/D59TBL5AZFMPIW4");
    hcertServerResponse.setHcertContent(hcertContentDTO);
    hcertServerResponse.setHcertKID("mmrfzpMU6xc=");
    hcertServerResponse.setHcertAlgo("PS256");
    hcertServerResponse.setHcertIssuer("CH BAG");
    hcertServerResponse.setHcertTimeStamp(hcertTimeStampDTO);
    hcertServerResponse.setHcertSignature(
        "Fqzo50TBt9un8CakzKb/gXIUOVXPWBUQ0OrKD2BBXi2QEJHYmByWdl/fyreCJalXbkNfDGVXxJ5g0vk4h+khCFQCrYAX1fIRBFgMZQAX2juzM7dGZKwIJOLcZifX75ekbEvrcgWxWCUE1Ucc2OXsu6PitnOV/f5jaDVWugB3KomsrDSPi/O9SSraWgHDaINfAZ8xjXfoQ+wUdHjQYipuwVqThOzz0QKlpXUZFjmQqVHvym+raiWMN4j+2xfqElGCf0jmbUNSixm3mCtkRquoTkmdcCfmECnE/mLVnnRmFzjvj9yB8OVvFT56kSrIrfcABGZapc+Z0r6Cbnrm/ytJfA==");

    return hcertServerResponse;
  }

}
