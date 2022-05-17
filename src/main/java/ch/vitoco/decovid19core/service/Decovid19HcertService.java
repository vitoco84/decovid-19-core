package ch.vitoco.decovid19core.service;

import static ch.vitoco.decovid19core.constants.Const.COSE_FORMAT_EXCEPTION;
import static ch.vitoco.decovid19core.constants.Const.IMAGE_DECODE_EXCEPTION;
import static ch.vitoco.decovid19core.constants.Const.JSON_DESERIALIZE_EXCEPTION;
import static ch.vitoco.decovid19core.constants.Const.MESSAGE_DECODE_EXCEPTION;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import javax.imageio.ImageIO;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import com.google.iot.cbor.CborMap;
import com.google.iot.cbor.CborParseException;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.upokecenter.cbor.CBORObject;

import ch.vitoco.decovid19core.enums.HcertAlgo;
import ch.vitoco.decovid19core.exception.ImageDecodeException;
import ch.vitoco.decovid19core.exception.JsonDeserializeException;
import ch.vitoco.decovid19core.exception.MessageDecodeException;
import ch.vitoco.decovid19core.model.HcertTimeStampDTO;

import COSE.CoseException;
import COSE.HeaderKeys;
import COSE.Message;
import nl.minvws.encoding.Base45;

/**
 * Service class for the Health Certificate decoding process.
 */
@Service
public class Decovid19HcertService {

  private static final int BUFFER_SIZE = 1024;
  private static final String HCERT_CLAIM_KEY = "-260";
  private static final String HCERT_CLAIM_KEY_CONTENT = "1";
  private static final String ISSUER_CLAIM_KEY = "1";
  private static final String EXPIRATION_CLAIM_KEY = "4";
  private static final String ISSUED_AT_CLAIM_KEY = "6";
  private static final int START_INDEX_OF_HCERT_CONTENT = 4;
  private static final int START_INDEX_OF_HEX_STRING = 2;
  private static final int START_OFFSET_BYTES_WRITER = 0;

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
      throw new ImageDecodeException(IMAGE_DECODE_EXCEPTION, e);
    }
  }

  private byte[] decodeBase45HealthCertificate(String hcert) {
    String hcertWithoutPrefix = removeHealthCertificateHC1Prefix(hcert);
    return Base45.getDecoder().decode(hcertWithoutPrefix);
  }

  private String removeHealthCertificateHC1Prefix(String hcert) {
    return hcert.substring(START_INDEX_OF_HCERT_CONTENT);
  }

  private ByteArrayOutputStream getCOSEMessageFromHcert(byte[] hcertBase45) {
    Inflater inflater = new Inflater();
    inflater.setInput(hcertBase45);
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(hcertBase45.length)) {
      byte[] buffer = new byte[BUFFER_SIZE];
      while (!inflater.finished()) {
        final int count = inflater.inflate(buffer);
        outputStream.write(buffer, START_OFFSET_BYTES_WRITER, count);
      }
      return outputStream;
    } catch (IOException | DataFormatException e) {
      throw new MessageDecodeException(COSE_FORMAT_EXCEPTION, e);
    }
  }

  /**
   * Gets the COSE Message from the Health Certificate.
   *
   * @param hcert Health Certificate content as String
   * @return Message
   */
  public Message getCOSEMessageFromHcert(String hcert) {
    try {
      byte[] hcertBytes = decodeBase45HealthCertificate(hcert);
      ByteArrayOutputStream coseMessageFromHcert = getCOSEMessageFromHcert(hcertBytes);
      return Message.DecodeFromBytes(coseMessageFromHcert.toByteArray());
    } catch (CoseException e) {
      throw new MessageDecodeException(MESSAGE_DECODE_EXCEPTION, e);
    }
  }

  /**
   * Gets the CBOR Object representation from the Health Certificate content.
   *
   * @param hcert Health Certificate content as String
   * @return CBORObject
   */
  public CBORObject getCBORObject(String hcert) {
    byte[] hcertBytes = decodeBase45HealthCertificate(hcert);
    ByteArrayOutputStream coseMessageFromHcert = getCOSEMessageFromHcert(hcertBytes);
    return CBORObject.DecodeFromBytes(coseMessageFromHcert.toByteArray());
  }

  /**
   * Gets the COSE Signature from the Health Certificate CBORObject.
   *
   * @param hcertCBORObject Health Certificate CBORObject
   * @return CBORObject
   */
  public CBORObject getCOSESignature(CBORObject hcertCBORObject) {
    return hcertCBORObject.get(3);
  }

  /**
   * Gets the CBOR Message from the Health Certificate COSE Message.
   *
   * @param hcertCOSEMessage Health Certificate COSE Message
   * @return CBOR Message as String
   */
  public String getCBORMessage(Message hcertCOSEMessage) {
    try {
      return CborMap.createFromCborByteArray(hcertCOSEMessage.GetContent()).toJsonString();
    } catch (CborParseException e) {
      throw new MessageDecodeException(MESSAGE_DECODE_EXCEPTION, e);
    }
  }

  /**
   * Gets the Time Stamp of the Health Certificate from the CBOR Message.
   *
   * @param cborMessage Health Certificate CBOR Message as String
   * @return HcertTimeStampDTO
   */
  public HcertTimeStampDTO getHcertTimeStamp(String cborMessage) {
    try {
      JSONObject parseTimeStamp = getJsonObject(cborMessage);
      Long expirationTimeStamp = (Long) parseTimeStamp.get(EXPIRATION_CLAIM_KEY);
      Long issuedAtTimeStamp = (Long) parseTimeStamp.get(ISSUED_AT_CLAIM_KEY);
      return buildHcertTimeStampResponse(expirationTimeStamp, issuedAtTimeStamp);
    } catch (ParseException e) {
      throw new JsonDeserializeException(JSON_DESERIALIZE_EXCEPTION, e);
    }
  }

  private HcertTimeStampDTO buildHcertTimeStampResponse(Long expirationTimeStamp, Long issuedAtTimeStamp) {
    HcertTimeStampDTO hcertTimeStampDTO = new HcertTimeStampDTO();
    hcertTimeStampDTO.setHcerExpirationTime(Instant.ofEpochSecond(expirationTimeStamp).toString());
    hcertTimeStampDTO.setHcertIssuedAtTime(Instant.ofEpochSecond(issuedAtTimeStamp).toString());
    return hcertTimeStampDTO;
  }

  /**
   * Gets the Issuer of the Health Certificate from the CBOR Message.
   *
   * @param cborMessage Health Certificate CBOR Message as String
   * @return Health Certificate issuer
   */
  public String getIssuer(String cborMessage) {
    try {
      JSONObject parseIssuer = getJsonObject(cborMessage);
      return (String) parseIssuer.get(ISSUER_CLAIM_KEY);
    } catch (ParseException e) {
      throw new JsonDeserializeException(JSON_DESERIALIZE_EXCEPTION, e);
    }
  }

  /**
   * Gets the content of the Health Certificate from the CBOR Message.
   *
   * @param cborMessage Health Certificate CBOR Message as String
   * @return Health Certificate content as String
   */
  public String getContent(String cborMessage) {
    try {
      JSONObject parseContent = getJsonObject(cborMessage);
      JSONObject contentObject = (JSONObject) parseContent.get(HCERT_CLAIM_KEY);
      JSONObject content = (JSONObject) contentObject.get(HCERT_CLAIM_KEY_CONTENT);
      return content.toString();
    } catch (ParseException e) {
      throw new JsonDeserializeException(JSON_DESERIALIZE_EXCEPTION, e);
    }
  }

  private JSONObject getJsonObject(String cborMessage) throws ParseException {
    JSONParser jsonParser = new JSONParser();
    return (JSONObject) jsonParser.parse(cborMessage);
  }

  private String getHeader(Message coseMessage, CBORObject cborHeaderKey) {
    StringBuilder stringBuilder = new StringBuilder();
    CBORObject algoUnprotected = coseMessage.getUnprotectedAttributes().get(cborHeaderKey);
    CBORObject algoProtected = coseMessage.getProtectedAttributes().get(cborHeaderKey);
    if (algoUnprotected != null) {
      stringBuilder.append(algoUnprotected);
    } else if (algoProtected != null) {
      stringBuilder.append(algoProtected);
    }
    return stringBuilder.toString();
  }

  private String getAlgoFromHeader(Message coseMessage) {
    return getHeader(coseMessage, HeaderKeys.Algorithm.AsCBOR());
  }

  /**
   * Gets the Signature Algorithm of the Health Certificate from the COSE Message.
   *
   * @param coseMessage Health Certificate COSE Message
   * @return Signature Algorithm
   */
  public String getAlgo(Message coseMessage) {
    int algoId = Integer.parseInt(getAlgoFromHeader(coseMessage));
    StringBuilder algo = new StringBuilder();
    for (HcertAlgo hcertAlgo : HcertAlgo.values()) {
      if (algoId == hcertAlgo.getAlgoId()) {
        algo.append(hcertAlgo);
      }
    }
    return algo.toString();
  }

  private String getKIDFromHeader(Message coseMessage) {
    return getHeader(coseMessage, HeaderKeys.KID.AsCBOR());
  }

  private String trimmKID(String kidHex) {
    return kidHex.substring(START_INDEX_OF_HEX_STRING, kidHex.lastIndexOf("'"));
  }

  /**
   * Gets the Key Identifier from the Health Certificate COSE Message.
   *
   * @param coseMessage Health Certificate COSE Message
   * @return Key Identifier
   */
  public String getKID(Message coseMessage) {
    StringBuilder kid = new StringBuilder();
    try {
      String kidHex = getKIDFromHeader(coseMessage);
      if (!StringUtils.isBlank(kidHex)) {
        String kidHexTrimmed = trimmKID(kidHex);
        byte[] kidBytes = Hex.decodeHex(kidHexTrimmed.toCharArray());
        kid.append(Base64.encodeBase64String(kidBytes));
      }
      return kid.toString();
    } catch (DecoderException e) {
      throw new MessageDecodeException(MESSAGE_DECODE_EXCEPTION, e);
    }
  }

}
