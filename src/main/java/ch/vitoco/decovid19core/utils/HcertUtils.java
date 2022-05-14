package ch.vitoco.decovid19core.utils;

import static ch.vitoco.decovid19core.constants.Const.COSE_FORMAT_EXCEPTION;
import static ch.vitoco.decovid19core.constants.Const.IMAGE_DECODE_EXCEPTION;
import static ch.vitoco.decovid19core.constants.Const.MESSAGE_DECODE_EXCEPTION;
import static ch.vitoco.decovid19core.constants.Const.UTILITY_CLASS_EXCEPTION;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import javax.imageio.ImageIO;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;

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
import ch.vitoco.decovid19core.exception.MessageDecodeException;

import COSE.CoseException;
import COSE.HeaderKeys;
import COSE.Message;
import nl.minvws.encoding.Base45;

public final class HcertUtils {

  private static final int BUFFER_SIZE = 1024;
  private static final String HCERT_CLAIM_KEY = "-260";
  private static final String ISSUER_CLAIM_KEY = "1";
  private static final int START_INDEX_OF_HCERT_CONTENT = 8;
  private static final int START_INDEX_OF_ISSUER = 3;
  private static final int START_INDEX_OF_HCERT_HC1_PREFIX = 4;
  private static final int START_INDEX_OF_HEX_STRING = 2;
  private static final int START_OFFSET_BYTES_WRITER = 0;


  private HcertUtils() {
    throw new IllegalStateException(UTILITY_CLASS_EXCEPTION);
  }

  public static String getHealthCertificateContent(InputStream imageFileInputStream) {
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

  private static byte[] decodeBase45HealthCertificate(String hcert) {
    String hcertWithoutPrefix = removeHealthCertificateHC1Prefix(hcert);
    return Base45.getDecoder().decode(hcertWithoutPrefix);
  }

  private static String removeHealthCertificateHC1Prefix(String hcert) {
    return hcert.substring(START_INDEX_OF_HCERT_HC1_PREFIX);
  }

  private static ByteArrayOutputStream getCOSEMessageFromHcert(byte[] hcertBase45) {
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

  public static Message getCOSEMessageFromHcert(String hcert) {
    byte[] hcertBytes = decodeBase45HealthCertificate(hcert);
    ByteArrayOutputStream coseMessageFromHcert = getCOSEMessageFromHcert(hcertBytes);
    try {
      return Message.DecodeFromBytes(coseMessageFromHcert.toByteArray());
    } catch (CoseException e) {
      throw new MessageDecodeException(MESSAGE_DECODE_EXCEPTION, e);
    }
  }

  public static CBORObject getCBORObject(String hcert) {
    byte[] hcertBytes = decodeBase45HealthCertificate(hcert);
    ByteArrayOutputStream coseMessageFromHcert = getCOSEMessageFromHcert(hcertBytes);
    return CBORObject.DecodeFromBytes(coseMessageFromHcert.toByteArray());
  }

  public static CBORObject getCOSESignature(CBORObject hcertCBORObject) {
    return hcertCBORObject.get(3);
  }

  public static String getCBORMessage(Message hcertCOSEMessage) {
    try {
      return CborMap.createFromCborByteArray(hcertCOSEMessage.GetContent()).toString();
    } catch (CborParseException e) {
      throw new MessageDecodeException(MESSAGE_DECODE_EXCEPTION, e);
    }
  }

  public static String getIssuer(String cborMessage) {
    return cborMessage.substring(cborMessage.indexOf(ISSUER_CLAIM_KEY) + START_INDEX_OF_ISSUER,
        cborMessage.indexOf(",") - 1);
  }

  public static String getContent(String cborMessage) {
    return cborMessage.substring(cborMessage.indexOf(HCERT_CLAIM_KEY) + START_INDEX_OF_HCERT_CONTENT,
        cborMessage.lastIndexOf("}") - 1);
  }

  private static String getHeader(Message coseMessage, CBORObject cborHeaderKey) {
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

  private static String getAlgoFromHeader(Message coseMessage) {
    return getHeader(coseMessage, HeaderKeys.Algorithm.AsCBOR());
  }

  public static String getAlgo(Message coseMessage) {
    int algoId = Integer.parseInt(getAlgoFromHeader(coseMessage));
    StringBuilder algo = new StringBuilder();
    if (algoId == HcertAlgo.ECDSA_256.getAlgo()) {
      algo.append(HcertAlgo.ECDSA_256);
    }
    if (algoId == HcertAlgo.RSA_PSS_256.getAlgo()) {
      algo.append(HcertAlgo.RSA_PSS_256);
    }
    return algo.toString();
  }

  private static String getKIDFromHeader(Message coseMessage) {
    return getHeader(coseMessage, HeaderKeys.KID.AsCBOR());
  }

  private static String trimmKID(String kidHex) {
    return kidHex.substring(START_INDEX_OF_HEX_STRING, kidHex.lastIndexOf("'"));
  }

  public static String getKID(Message coseMessage) {
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
