package ch.vitoco.decovid19core.controller;

import javax.validation.Valid;
import java.awt.image.BufferedImage;

import ch.vitoco.decovid19core.exception.ServerException;
import ch.vitoco.decovid19core.model.hcert.HcertContentDTO;
import ch.vitoco.decovid19core.server.*;
import ch.vitoco.decovid19core.service.Base45Service;
import ch.vitoco.decovid19core.service.HcertService;
import ch.vitoco.decovid19core.service.HcertVerificationService;
import ch.vitoco.decovid19core.service.QRCodeGeneratorService;
import ch.vitoco.decovid19core.validation.ValidFile;
import ch.vitoco.decovid19core.validation.ValidationError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/decovid19")
public class Decovid19Controller {

  private final HcertService hcertService;
  private final QRCodeGeneratorService qrCodeGeneratorService;
  private final HcertVerificationService hcertVerificationService;
  private final Base45Service base45Service;

  @Operation(summary = "Decode Covid-19 Health Certificate with QR-Code")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Decoded Covid-19 HCERT", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = HcertServerResponse.class))}),
      @ApiResponse(responseCode = "400", description = "Invalid QR-Code supplied", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationError.class))}),
      @ApiResponse(responseCode = "500", description = "Server Exception", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ServerException.class))})})
  @PostMapping(value = "/hcert/qrcode", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {
      MediaType.APPLICATION_JSON_VALUE, "application/json"})
  public ResponseEntity<HcertServerResponse> decodeHealthCertificateContent(@ValidFile @RequestParam(value = "imageFile") MultipartFile imageFile) {
    return hcertService.decodeHealthCertificateContent(imageFile);
  }

  @Operation(summary = "Decode Covid-19 Health Certificate with Prefix String 'HC1:'")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Decoded Covid-19 HCERT", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = HcertServerResponse.class))}),
      @ApiResponse(responseCode = "400", description = "Invalid Health Certificate Prefix supplied", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationError.class))}),
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
      @ApiResponse(responseCode = "400", description = "Invalid URL supplied", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationError.class))}),
      @ApiResponse(responseCode = "500", description = "Server Exception", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ServerException.class))})})
  @PostMapping(value = "/hcert/qrcode/url", consumes = {MediaType.APPLICATION_JSON_VALUE,
      "application/json"}, produces = {MediaType.IMAGE_PNG_VALUE})
  public ResponseEntity<BufferedImage> createURLQRCodeImage(@Valid @RequestBody QRCodeServerRequest url) {
    return qrCodeGeneratorService.createURLQRCodeImage(url);
  }

  @Operation(summary = "URL QR-Code Generator")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "URL QR-Code", content = {
      @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))}),
      @ApiResponse(responseCode = "400", description = "Invalid URL supplied", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationError.class))}),
      @ApiResponse(responseCode = "500", description = "Server Exception", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ServerException.class))})})
  @PostMapping(value = "/hcert/qrcode/url/client", consumes = {MediaType.APPLICATION_JSON_VALUE,
      "application/json"}, produces = {MediaType.TEXT_PLAIN_VALUE})
  public ResponseEntity<String> createURLQRCodeBase64String(@Valid @RequestBody QRCodeServerRequest url) {
    return qrCodeGeneratorService.createURLQRCodeBase64String(url);
  }

  @Operation(summary = "Fake Covid Test Certificate QR-Code Generator")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Health Test Certificate QR-Code", content = {
      @Content(mediaType = "image/png", schema = @Schema(implementation = BufferedImage.class))}),
      @ApiResponse(responseCode = "400", description = "Invalid JSON supplied", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationError.class))}),
      @ApiResponse(responseCode = "500", description = "Server Exception", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ServerException.class))})})
  @PostMapping(value = "/hcert/qrcode/hcert", consumes = {MediaType.APPLICATION_JSON_VALUE,
      "application/json"}, produces = {MediaType.IMAGE_PNG_VALUE})
  public ResponseEntity<BufferedImage> createTestCovidQRCodeImage(@Valid @RequestBody HcertContentDTO hcertContentDTO) {
    return qrCodeGeneratorService.createTestCovidQRCodeImage(hcertContentDTO);
  }

  @Operation(summary = "Fake Covid Test Certificate QR-Code Generator")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Health Test Certificate QR-Code", content = {
      @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))}),
      @ApiResponse(responseCode = "400", description = "Invalid JSON supplied", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationError.class))}),
      @ApiResponse(responseCode = "500", description = "Server Exception", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ServerException.class))})})
  @PostMapping(value = "/hcert/qrcode/hcert/client", consumes = {MediaType.APPLICATION_JSON_VALUE,
      "application/json"}, produces = {MediaType.TEXT_PLAIN_VALUE})
  public ResponseEntity<String> createTestCovidQRCodeBase64String(@Valid @RequestBody HcertContentDTO hcertContentDTO) {
    return qrCodeGeneratorService.createTestCovidQRCodeBase64String(hcertContentDTO);
  }

  @Operation(summary = "Decode PEM Data")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Decode PEM Data", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = PEMCertServerResponse.class))}),
      @ApiResponse(responseCode = "400", description = "Invalid PEM supplied", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationError.class))}),
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
      @ApiResponse(responseCode = "400", description = "Invalid Health Certificate supplied", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationError.class))}),
      @ApiResponse(responseCode = "500", description = "Server Exception", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ServerException.class))})})
  @PostMapping(value = "/hcert/verify", consumes = {MediaType.APPLICATION_JSON_VALUE, "application/json"}, produces = {
      MediaType.APPLICATION_JSON_VALUE, "application/json"})
  public ResponseEntity<HcertVerificationServerResponse> verifyHealthCertificate(@Valid @RequestBody HcertVerificationServerRequest hcertVerificationServerRequest) {
    return hcertVerificationService.verifyHealthCertificate(hcertVerificationServerRequest);
  }

  @Operation(summary = "Encode Base45")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Encode Base45", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = Base45EncodeServerRequest.class))}),
      @ApiResponse(responseCode = "400", description = "Could not encode String to Base45", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationError.class))})})
  @PostMapping(value = "/hcert/base45/encode", consumes = {MediaType.APPLICATION_JSON_VALUE,
      "application/json"}, produces = {MediaType.APPLICATION_JSON_VALUE, "application/json"})
  public ResponseEntity<String> encodeBase45(@Valid @RequestBody Base45EncodeServerRequest base45EncodeServerRequest) {
    return base45Service.encodeBase45(base45EncodeServerRequest);
  }

  @Operation(summary = "Decode Base45")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Decode Base45", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = Base45DecodeServerRequest.class))}),
      @ApiResponse(responseCode = "400", description = "Could not decode String from Base45", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationError.class))})})
  @PostMapping(value = "/hcert/base45/decode", consumes = {MediaType.APPLICATION_JSON_VALUE,
      "application/json"}, produces = {MediaType.APPLICATION_JSON_VALUE, "application/json"})
  public ResponseEntity<String> decodeBase45(@Valid @RequestBody Base45DecodeServerRequest base45DecodeServerRequest) {
    return base45Service.decodeBase45(base45DecodeServerRequest);
  }

}
