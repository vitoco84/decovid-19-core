package ch.vitoco.decovid19core.controller;

import ch.vitoco.decovid19core.server.HcertServerRequest;
import ch.vitoco.decovid19core.server.HcertServerResponse;
import ch.vitoco.decovid19core.service.Decovid19DAO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/decovid19")
public class Decovid19Controller {

  private final Decovid19DAO decovid19DAO;

  public Decovid19Controller(Decovid19DAO decovid19DAO) {
    this.decovid19DAO = decovid19DAO;
  }

  @PostMapping(value = "/hcert/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE, "application/json"})
  public ResponseEntity<HcertServerResponse> getHealthCertificateContent(
      @RequestParam("imageFile") MultipartFile imageFile) {
    return decovid19DAO.getHealthCertificateContent(imageFile);
  }

  @PostMapping(value = "/hcert/prefix", consumes = {MediaType.APPLICATION_JSON_VALUE, "application/json"}, produces = {MediaType.APPLICATION_JSON_VALUE, "application/json"})
  public ResponseEntity<HcertServerResponse> getHealthCertificateContent(
      @RequestBody HcertServerRequest hcert) {
    return decovid19DAO.getHealthCertificateContent(hcert);
  }

}
