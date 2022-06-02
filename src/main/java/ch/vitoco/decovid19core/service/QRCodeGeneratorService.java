package ch.vitoco.decovid19core.service;

import static ch.vitoco.decovid19core.constants.ExceptionMessages.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import COSE.*;
import ch.vitoco.decovid19core.enums.HcertClaimKeys;
import ch.vitoco.decovid19core.exception.ServerException;
import ch.vitoco.decovid19core.model.HcertContentDTO;
import ch.vitoco.decovid19core.server.QRCodeServerRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.upokecenter.cbor.CBORObject;
import nl.minvws.encoding.Base45;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service class QRCodeGeneratorService.
 */
@Service
public class QRCodeGeneratorService {

  private static final int IMG_WIDTH_HEIGHT = 250;
  private static final int ONE_YEAR = 1;
  private static final long TIME_CONVERTER_MILLIS = 1000L;
  private static final int FIRST_ENTRY_HCERT_TEST_LIST = 0;
  private static final String HCERT_HEADER = "HC1:";
  private static final String UNIQUE_ID_HEADER = "uvci:";
  private static final String ISSUER = "ISSUER";


  /**
   * Generates a QR-Code (BufferedImage) given a QRCodeServerRequest.
   *
   * @param url the QRCodeServerRequest
   * @return BufferedImage
   */
  public ResponseEntity<BufferedImage> getURLQRCode(QRCodeServerRequest url) {
    if (isValidURL(url.getUrl())) {
      try {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(
            new String(url.getUrl().getBytes(), StandardCharsets.UTF_8), BarcodeFormat.QR_CODE, IMG_WIDTH_HEIGHT,
            IMG_WIDTH_HEIGHT);
        return ResponseEntity.ok().body(MatrixToImageWriter.toBufferedImage(bitMatrix));
      } catch (WriterException e) {
        throw new ServerException(URL_ENCODE_EXCEPTION, e);
      }
    } else {
      return ResponseEntity.badRequest().build();
    }
  }

  private boolean isValidURL(String url) {
    try {
      new URL(url).toURI();
    } catch (MalformedURLException | URISyntaxException e) {
      return false;
    }
    return true;
  }

  /**
   * Generates a fake Covid Test Health Certificate given a HcertContentDTO.
   *
   * @param hcertContentDTO the HcertContentDTO
   * @return BufferedImage
   */
  public ResponseEntity<BufferedImage> getTestCovidQRCode(HcertContentDTO hcertContentDTO) {
    if (hcertContentDTO.getR() == null && hcertContentDTO.getV() == null) {
      byte[] cbor = getCBORBytes(hcertContentDTO);
      byte[] cose = getCOSESignatureBytes(cbor);
      byte[] zip = getCOSECompressedBytes(cose);
      String hcertPrefix = getCompressedCOSEToBase45(zip);
      try {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(new String(hcertPrefix.getBytes(), StandardCharsets.UTF_8),
            BarcodeFormat.QR_CODE, IMG_WIDTH_HEIGHT, IMG_WIDTH_HEIGHT);
        return ResponseEntity.ok().body(MatrixToImageWriter.toBufferedImage(bitMatrix));
      } catch (WriterException e) {
        throw new ServerException(HCERT_TEST_ENCODE_EXCEPTION, e);
      }
    } else {
      return ResponseEntity.badRequest().build();
    }
  }

  private byte[] getCBORBytes(HcertContentDTO hcertContentDTO) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      hcertContentDTO.getT().get(FIRST_ENTRY_HCERT_TEST_LIST).setCi(UNIQUE_ID_HEADER + UUID.randomUUID());
      String json = mapper.writeValueAsString(hcertContentDTO);
      CBORObject map = CBORObject.NewMap();
      map.set(CBORObject.FromObject(HcertClaimKeys.HCERT_MESSAGE_TAG.getClaimKey()), CBORObject.FromObject(ISSUER));
      map.set(CBORObject.FromObject(HcertClaimKeys.HCERT_ISSUED_AT_CLAIM_KEY.getClaimKey()),
          CBORObject.FromObject(getIssuedAt()));
      map.set(CBORObject.FromObject(HcertClaimKeys.HCERT_EXPIRATION_CLAIM_KEY.getClaimKey()),
          CBORObject.FromObject(getExpiration()));
      CBORObject hcertVersion = CBORObject.NewMap();
      CBORObject hcert = CBORObject.FromJSONString(json);
      hcertVersion.set(CBORObject.FromObject(HcertClaimKeys.HCERT_VERSION_CLAIM_KEY.getClaimKey()), hcert);
      map.set(CBORObject.FromObject(HcertClaimKeys.HCERT_CLAIM_KEY.getClaimKey()), hcertVersion);
      return map.EncodeToBytes();
    } catch (JsonProcessingException e) {
      throw new ServerException(JSON_SERIALIZE_EXCEPTION, e);
    }
  }

  private byte[] getCOSESignatureBytes(byte[] cbor) {
    try {
      OneKey privateKey = OneKey.generateKey(AlgorithmID.ECDSA_256);
      byte[] kid = UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8);
      Sign1Message msg = new Sign1Message();
      msg.addAttribute(HeaderKeys.Algorithm, privateKey.get(KeyKeys.Algorithm), Attribute.PROTECTED);
      msg.addAttribute(HeaderKeys.KID, CBORObject.FromObject(kid), Attribute.PROTECTED);
      msg.SetContent(cbor);
      msg.sign(privateKey);
      return msg.EncodeToBytes();
    } catch (CoseException e) {
      throw new ServerException(CBOR_SIGNATURE_EXCEPTION, e);
    }
  }

  private byte[] getCOSECompressedBytes(byte[] cose) {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    try (CompressorOutputStream deflateOut = new CompressorStreamFactory().createCompressorOutputStream(
        CompressorStreamFactory.DEFLATE, stream)) {
      deflateOut.write(cose);
    } catch (CompressorException | IOException e) {
      throw new ServerException(COSE_COMPRESS_EXCEPTION, e);
    }
    return stream.toByteArray();
  }

  private String getCompressedCOSEToBase45(byte[] zip) {
    String base45 = Base45.getEncoder().encodeToString(zip);
    return HCERT_HEADER + base45;
  }

  private long getIssuedAt() {
    return LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() / TIME_CONVERTER_MILLIS;
  }

  private long getExpiration() {
    return LocalDateTime.now().plusYears(ONE_YEAR).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() /
        TIME_CONVERTER_MILLIS;
  }

}
