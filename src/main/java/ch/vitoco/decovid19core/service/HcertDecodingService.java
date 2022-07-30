package ch.vitoco.decovid19core.service;

import static ch.vitoco.decovid19core.constants.ExceptionMessages.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import COSE.HeaderKeys;
import ch.vitoco.decovid19core.enums.HcertAlgoKeys;
import ch.vitoco.decovid19core.enums.HcertCBORKeys;
import ch.vitoco.decovid19core.enums.HcertClaimKeys;
import ch.vitoco.decovid19core.exception.ServerException;
import ch.vitoco.decovid19core.model.hcert.HcertTimeStampDTO;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.upokecenter.cbor.CBORObject;
import nl.minvws.encoding.Base45;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * Service class for the Health Certificate decoding process.
 */
@Service
public class HcertDecodingService {

  private static final int START_INDEX_OF_HCERT_CONTENT = 4;
  private static final int START_INDEX_OF_HEX_STRING = 2;

  /**
   * Gets the content of the Health Certificate.
   *
   * @param imageFileInputStream the Health Certificate as InputStream
   * @return Health Certificate content
   */
  public String getHealthCertificateContent(InputStream imageFileInputStream) {
    try {
      BufferedImage bufferedImage = ImageIO.read(imageFileInputStream);
      LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
      BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
      Result result = new MultiFormatReader().decode(bitmap);
      return result.getText();
    } catch (IOException | NotFoundException e) {
      throw new ServerException(BARCODE_NOT_FOUND_EXCEPTION, e);
    }
  }

  private byte[] decodeBase45HealthCertificate(String hcert) {
    try {
      String hcertWithoutPrefix = removeHcertHC1Prefix(hcert);
      return Base45.getDecoder().decode(hcertWithoutPrefix);
    } catch (IllegalArgumentException e) {
      throw new ServerException(QR_CODE_DECODE_EXCEPTION, e);
    }
  }

  private String removeHcertHC1Prefix(String hcert) {
    return hcert.substring(START_INDEX_OF_HCERT_CONTENT);
  }

  private ByteArrayOutputStream decompressCOSEMessage(byte[] hcertBase45) {
    Inflater inflater = new Inflater();
    inflater.setInput(hcertBase45);
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(hcertBase45.length)) {
      byte[] buffer = new byte[2056];
      while (!inflater.finished()) {
        final int count = inflater.inflate(buffer);
        outputStream.write(buffer, 0, count);
      }
      return outputStream;
    } catch (IOException | DataFormatException e) {
      throw new ServerException(MESSAGE_FORMAT_EXCEPTION, e);
    }
  }

  private boolean isCompressed(final byte[] hcert) {
    return hcert[0] == 0x78;
  }

  /**
   * Gets the CBOR Object representation from the Health Certificate content.
   *
   * @param hcert Health Certificate content as String
   * @return CBORObject
   */
  public CBORObject getCBORObject(String hcert) {
    byte[] hcertBytes = decodeBase45HealthCertificate(hcert);
    byte[] coseMessageHcert = isCompressed(hcertBytes) ? decompressCOSEMessage(hcertBytes).toByteArray() : hcertBytes;
    return CBORObject.DecodeFromBytes(coseMessageHcert);
  }

  private CBORObject getProtectedHeader(CBORObject hcertCBORObject) {
    return hcertCBORObject.get(HcertCBORKeys.PROTECTED_HEADER.getCborKey());
  }

  private CBORObject getUnprotectedHeader(CBORObject hcertCBORObject) {
    return hcertCBORObject.get(HcertCBORKeys.UNPROTECTED_HEADER.getCborKey());
  }

  private byte[] getMessageContent(CBORObject hcertCBORObject) {
    return hcertCBORObject.get(HcertCBORKeys.MESSAGE_CONTENT.getCborKey()).GetByteString();
  }

  private byte[] getCOSESignature(CBORObject hcertCBORObject) {
    return hcertCBORObject.get(HcertCBORKeys.SIGNATURE.getCborKey()).GetByteString();
  }

  /**
   * Gets the Signature of the Health Certificate from the CBOR Message.
   *
   * @param cborMessage Health Certificate CBOR Message
   * @return Signature Base64 encoded String
   */
  public String getSignature(CBORObject cborMessage) {
    byte[] coseSignature = getCOSESignature(cborMessage);
    return Base64.encodeBase64String(coseSignature);
  }

  /**
   * Gets the Issuer of the Health Certificate from the CBOR Message.
   *
   * @param cborMessage Health Certificate CBOR Message
   * @return Health Certificate Issuer
   */
  public String getIssuer(CBORObject cborMessage) {
    byte[] messageContent = getMessageContent(cborMessage);
    CBORObject cborObject = CBORObject.DecodeFromBytes(messageContent);
    return cborObject.get(HcertClaimKeys.HCERT_ISSUER_CLAIM_KEY.getClaimKey()).AsString();
  }

  /**
   * Gets the content of the Health Certificate from the CBOR Message.
   *
   * @param cborMessage Health Certificate CBOR Message
   * @return Health Certificate content as String
   */
  public String getContent(CBORObject cborMessage) {
    byte[] messageContent = getMessageContent(cborMessage);
    CBORObject cborObject = CBORObject.DecodeFromBytes(messageContent);
    CBORObject cborObjectContent = cborObject.get(HcertClaimKeys.HCERT_CLAIM_KEY.getClaimKey());
    return cborObjectContent.get(HcertClaimKeys.HCERT_MESSAGE_TAG.getClaimKey()).ToJSONString();
  }

  private String getIssuedAt(CBORObject cborMessage) {
    byte[] messageContent = getMessageContent(cborMessage);
    CBORObject cborObject = CBORObject.DecodeFromBytes(messageContent);
    return cborObject.get(HcertClaimKeys.HCERT_ISSUED_AT_CLAIM_KEY.getClaimKey()).ToJSONString();
  }

  private String getExpiration(CBORObject cborMessage) {
    byte[] messageContent = getMessageContent(cborMessage);
    CBORObject cborObject = CBORObject.DecodeFromBytes(messageContent);
    return cborObject.get(HcertClaimKeys.HCERT_EXPIRATION_CLAIM_KEY.getClaimKey()).ToJSONString();
  }

  /**
   * Gets the Time Stamp of the Health Certificate from the CBOR Message.
   *
   * @param cborMessage Health Certificate CBOR Message
   * @return HcertTimeStampDTO
   */
  public HcertTimeStampDTO getHcertTimeStamp(CBORObject cborMessage) {
    String issuedAt = getIssuedAt(cborMessage);
    String expiration = getExpiration(cborMessage);
    Long expirationTimeStamp = Long.parseLong(expiration);
    Long issuedAtTimeStamp = Long.parseLong(issuedAt);
    return buildHcertTimeStampResponse(expirationTimeStamp, issuedAtTimeStamp);
  }

  private HcertTimeStampDTO buildHcertTimeStampResponse(Long expirationTimeStamp, Long issuedAtTimeStamp) {
    HcertTimeStampDTO hcertTimeStampDTO = new HcertTimeStampDTO();
    hcertTimeStampDTO.setHcertExpirationTime(Instant.ofEpochSecond(expirationTimeStamp).toString());
    hcertTimeStampDTO.setHcertIssuedAtTime(Instant.ofEpochSecond(issuedAtTimeStamp).toString());
    hcertTimeStampDTO.setHcertExpired(Instant.ofEpochSecond(expirationTimeStamp).isBefore(Instant.now()));
    return hcertTimeStampDTO;
  }

  private String getHeader(CBORObject cborObject, CBORObject cborHeaderKey) {
    StringBuilder stringBuilder = new StringBuilder();
    CBORObject protectedHeader = getProtectedHeader(cborObject);
    CBORObject cborObjectProtectedHeader = CBORObject.DecodeFromBytes(protectedHeader.GetByteString());

    if (cborObjectProtectedHeader.get(cborHeaderKey) == null) {
      CBORObject unprotectedHeader = getUnprotectedHeader(cborObject);
      CBORObject unprotectedHeaderContent = unprotectedHeader.get(cborHeaderKey);
      stringBuilder.append(unprotectedHeaderContent.toString());
    } else {
      stringBuilder.append(cborObjectProtectedHeader.get(cborHeaderKey));
    }
    return stringBuilder.toString();
  }

  private String getAlgoFromHeader(CBORObject cborObject) {
    return getHeader(cborObject, HeaderKeys.Algorithm.AsCBOR());
  }

  /**
   * Gets the Algorithm of the Health Certificate from the CBOR Message.
   *
   * @param cborObject Health Certificate CBOR Message
   * @return Algorithm
   */
  public String getAlgo(CBORObject cborObject) {
    int algoId = Integer.parseInt(getAlgoFromHeader(cborObject));
    StringBuilder algo = new StringBuilder();
    for (HcertAlgoKeys hcertAlgoKeys : HcertAlgoKeys.values()) {
      if (algoId == hcertAlgoKeys.getAlgoId()) {
        algo.append(hcertAlgoKeys);
      }
    }
    return algo.toString();
  }

  /**
   * Gets the JCA Algorithm name.
   *
   * @param algoName the algorithm name
   * @return JCA Algorithm name
   */
  public String getJcaAlgo(String algoName) {
    StringBuilder algo = new StringBuilder();
    for (HcertAlgoKeys hcertAlgoKeys : HcertAlgoKeys.values()) {
      if (algoName.equals(hcertAlgoKeys.getName())) {
        algo.append(hcertAlgoKeys.getJcaAlgoName());
      }
    }
    return algo.toString();
  }

  private String getKIDFromHeader(CBORObject cborObject) {
    return getHeader(cborObject, HeaderKeys.KID.AsCBOR());
  }

  private String trimmKID(String kidHex) {
    return kidHex.substring(START_INDEX_OF_HEX_STRING, kidHex.lastIndexOf("'"));
  }

  /**
   * Gets the Key Identifier of the Health Certificate from the CBOR Message.
   *
   * @param cborObject Health Certificate CBOR Message
   * @return Key Identifier Base64 encoded String
   */
  public String getKID(CBORObject cborObject) {
    StringBuilder kid = new StringBuilder();
    try {
      String kidHex = getKIDFromHeader(cborObject);
      if (!StringUtils.isBlank(kidHex)) {
        String kidHexTrimmed = trimmKID(kidHex);
        byte[] kidBytes = Hex.decodeHex(kidHexTrimmed.toCharArray());
        kid.append(Base64.encodeBase64String(kidBytes));
      }
      return kid.toString();
    } catch (DecoderException e) {
      throw new ServerException(MESSAGE_DECODE_EXCEPTION, e);
    }
  }

}
