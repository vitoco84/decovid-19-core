package ch.vitoco.decovid19core.controller;

import java.awt.image.BufferedImage;

import ch.vitoco.decovid19core.model.HcertContentDTO;
import ch.vitoco.decovid19core.server.*;
import ch.vitoco.decovid19core.service.HcertService;
import ch.vitoco.decovid19core.service.HcertVerificationService;
import ch.vitoco.decovid19core.service.QRCodeGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/decovid19")
public class Decovid19Controller {

  private final HcertService hcertService;
  private final QRCodeGeneratorService qrCodeGeneratorService;
  private final HcertVerificationService hcertVerificationService;


  public Decovid19Controller(HcertService hcertService,
      QRCodeGeneratorService qrCodeGeneratorService,
      HcertVerificationService hcertVerificationService) {
    this.hcertService = hcertService;
    this.qrCodeGeneratorService = qrCodeGeneratorService;
    this.hcertVerificationService = hcertVerificationService;
  }

  @Operation(summary = "Decode Covid-19 Health Certificate with QR-Code")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Decoded Covid-19 HCERT", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = HcertServerResponse.class))}),
      @ApiResponse(responseCode = "400", description = "Invalid QR-Code supplied", content = @Content)})
  @PostMapping(value = "/hcert/qrcode", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {
      MediaType.APPLICATION_JSON_VALUE, "application/json"})
  public ResponseEntity<HcertServerResponse> getHealthCertificateContent(@RequestParam("imageFile") MultipartFile imageFile) {
    return hcertService.getHealthCertificateContent(imageFile);
  }

  @Operation(summary = "Decode Covid-19 Health Certificate with Prefix String 'HC1:'")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Decoded Covid-19 HCERT", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = HcertServerResponse.class))}),
      @ApiResponse(responseCode = "400", description = "Invalid String supplied", content = @Content)})
  @PostMapping(value = "/hcert/prefix", consumes = {MediaType.APPLICATION_JSON_VALUE, "application/json"}, produces = {
      MediaType.APPLICATION_JSON_VALUE, "application/json"})
  public ResponseEntity<HcertServerResponse> getHealthCertificateContent(@RequestBody HcertServerRequest hcert) {
    return hcertService.getHealthCertificateContent(hcert);
  }

  @Operation(summary = "URL QR-Code Generator")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "URL QR-Code", content = {
      @Content(mediaType = "image/png", schema = @Schema(implementation = BufferedImage.class))}),
      @ApiResponse(responseCode = "400", description = "Invalid URL supplied", content = @Content)})
  @PostMapping(value = "/hcert/qrcode/url", produces = {MediaType.IMAGE_PNG_VALUE})
  public ResponseEntity<BufferedImage> getQRCode(@RequestBody QRCodeServerRequest url) {
    return qrCodeGeneratorService.getURLQRCode(url);
  }

  @Operation(summary = "Fake Covid Test Certificate QR-Code Generator")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "URL QR-Code", content = {
      @Content(mediaType = "image/png", schema = @Schema(implementation = BufferedImage.class))}),
      @ApiResponse(responseCode = "400", description = "Invalid JSON supplied", content = @Content)})
  @PostMapping(value = "/hcert/qrcode/hcert", produces = {MediaType.IMAGE_PNG_VALUE})
  public ResponseEntity<BufferedImage> getTestCovidQRCode(@RequestBody HcertContentDTO hcertContentDTO) {
    return qrCodeGeneratorService.getTestCovidQRCode(hcertContentDTO);
  }

  @Operation(summary = "Decode PEM Data")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Decode PEM Data", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = PEMCertServerResponse.class))}),
      @ApiResponse(responseCode = "400", description = "Invalid PEM data supplied", content = @Content)})
  @PostMapping(value = "/hcert/qrcode/pem", consumes = {MediaType.APPLICATION_JSON_VALUE,
      "application/json"}, produces = {MediaType.APPLICATION_JSON_VALUE, "application/json"})
  public ResponseEntity<PEMCertServerResponse> getX509Certificate(@RequestBody PEMCertServerRequest pemCertificate) {
    return hcertService.getX509Certificate(pemCertificate);
  }

  @Operation(summary = "Verification of the Health Certificate")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Verification of the Health Certificate", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = HcertVerificationServerResponse.class))}),
      @ApiResponse(responseCode = "400", description = "Invalid PEM data supplied", content = @Content)})
  @PostMapping(value = "/hcert/verify", consumes = {MediaType.APPLICATION_JSON_VALUE, "application/json"}, produces = {
      MediaType.APPLICATION_JSON_VALUE, "application/json"})
  public ResponseEntity<HcertVerificationServerResponse> getHealthCertificateVerification(@RequestBody HcertVerificationServerRequest hcertVerificationServerRequest) {
    return hcertVerificationService.verifyHealthCertificate(hcertVerificationServerRequest);
  }

}
