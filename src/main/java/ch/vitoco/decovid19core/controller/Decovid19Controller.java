package ch.vitoco.decovid19core.controller;

import java.awt.image.BufferedImage;

import javax.validation.Valid;

import ch.vitoco.decovid19core.exception.ServerException;
import ch.vitoco.decovid19core.model.hcert.HcertContentDTO;
import ch.vitoco.decovid19core.server.*;
import ch.vitoco.decovid19core.service.HcertService;
import ch.vitoco.decovid19core.service.HcertVerificationService;
import ch.vitoco.decovid19core.service.QRCodeGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/decovid19")
public class Decovid19Controller {

  private final HcertService hcertService;
  private final QRCodeGeneratorService qrCodeGeneratorService;
  private final HcertVerificationService hcertVerificationService;


  @Operation(summary = "Decode Covid-19 Health Certificate with QR-Code")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Decoded Covid-19 HCERT", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = HcertServerResponse.class))}),
      @ApiResponse(responseCode = "400", description = "Invalid QR-Code supplied", content = @Content),
      @ApiResponse(responseCode = "500", description = "Server Exception", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ServerException.class))})})
  @PostMapping(value = "/hcert/qrcode", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {
      MediaType.APPLICATION_JSON_VALUE, "application/json"})
  public ResponseEntity<HcertServerResponse> decodeHealthCertificateContent(@RequestParam("imageFile") MultipartFile imageFile) {
    return hcertService.decodeHealthCertificateContent(imageFile);
  }

  @Operation(summary = "Decode Covid-19 Health Certificate with Prefix String 'HC1:'")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Decoded Covid-19 HCERT", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = HcertServerResponse.class))}),
      @ApiResponse(responseCode = "400", description = "Invalid String supplied", content = @Content),
      @ApiResponse(responseCode = "500", description = "Server Exception", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ServerException.class))})})
  @PostMapping(value = "/hcert/prefix", consumes = {MediaType.APPLICATION_JSON_VALUE, "application/json"}, produces = {
      MediaType.APPLICATION_JSON_VALUE, "application/json"})
  public ResponseEntity<HcertServerResponse> decodeHealthCertificateContent(@Valid @RequestBody HcertServerRequest hcert) {
    return hcertService.decodeHealthCertificateContent(hcert);
  }

  @Operation(summary = "URL QR-Code Generator")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "URL QR-Code", content = {
      @Content(mediaType = "image/png", schema = @Schema(implementation = BufferedImage.class))}),
      @ApiResponse(responseCode = "400", description = "Invalid URL supplied", content = @Content),
      @ApiResponse(responseCode = "500", description = "Server Exception", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ServerException.class))})})
  @PostMapping(value = "/hcert/qrcode/url", produces = {MediaType.IMAGE_PNG_VALUE})
  public ResponseEntity<BufferedImage> createURLQRCode(@Valid @RequestBody QRCodeServerRequest url) {
    return qrCodeGeneratorService.createURLQRCode(url);
  }

  @Operation(summary = "Fake Covid Test Certificate QR-Code Generator")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "URL QR-Code", content = {
      @Content(mediaType = "image/png", schema = @Schema(implementation = BufferedImage.class))}),
      @ApiResponse(responseCode = "400", description = "Invalid JSON supplied", content = @Content),
      @ApiResponse(responseCode = "500", description = "Server Exception", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ServerException.class))})})
  @PostMapping(value = "/hcert/qrcode/hcert", produces = {MediaType.IMAGE_PNG_VALUE})
  public ResponseEntity<BufferedImage> createTestCovidQRCode(@Valid @RequestBody HcertContentDTO hcertContentDTO) {
    return qrCodeGeneratorService.createTestCovidQRCode(hcertContentDTO);
  }

  @Operation(summary = "Decode PEM Data")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Decode PEM Data", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = PEMCertServerResponse.class))}),
      @ApiResponse(responseCode = "400", description = "Invalid PEM data supplied", content = @Content),
      @ApiResponse(responseCode = "500", description = "Server Exception", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ServerException.class))})})
  @PostMapping(value = "/hcert/qrcode/pem", consumes = {MediaType.APPLICATION_JSON_VALUE,
      "application/json"}, produces = {MediaType.APPLICATION_JSON_VALUE, "application/json"})
  public ResponseEntity<PEMCertServerResponse> decodeX509Certificate(@Valid @RequestBody PEMCertServerRequest pemCertificate) {
    return hcertService.decodeX509Certificate(pemCertificate);
  }

  @Operation(summary = "Verification of the Health Certificate")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Verification of the Health Certificate", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = HcertVerificationServerResponse.class))}),
      @ApiResponse(responseCode = "400", description = "Invalid PEM data supplied", content = @Content),
      @ApiResponse(responseCode = "500", description = "Server Exception", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ServerException.class))})})
  @PostMapping(value = "/hcert/verify", consumes = {MediaType.APPLICATION_JSON_VALUE, "application/json"}, produces = {
      MediaType.APPLICATION_JSON_VALUE, "application/json"})
  public ResponseEntity<HcertVerificationServerResponse> verifyHealthCertificate(@Valid @RequestBody HcertVerificationServerRequest hcertVerificationServerRequest) {
    return hcertVerificationService.verifyHealthCertificate(hcertVerificationServerRequest);
  }

}
