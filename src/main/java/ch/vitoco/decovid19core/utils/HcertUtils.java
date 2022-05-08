package ch.vitoco.decovid19core.utils;

import COSE.CoseException;
import COSE.Message;
import ch.vitoco.decovid19core.enums.HcertAlgo;
import ch.vitoco.decovid19core.exception.ImageDecodeException;
import ch.vitoco.decovid19core.exception.MessageDecodeException;
import com.google.iot.cbor.CborMap;
import com.google.iot.cbor.CborParseException;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.upokecenter.cbor.CBORObject;
import nl.minvws.encoding.Base45;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import static ch.vitoco.decovid19core.utils.Const.*;

public class HcertUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(HcertUtils.class);

  private static final int BUFFER_SIZE = 1024;
  private static final String HCERT_CLAIM_KEY = "-260";
  private static final int START_INDEX_OF_HCERT_JSON_PAYLOAD = 8;
  private static final int START_INDEX_OF_HCERT_HC1_PREFIX = 4;
  private static final int COSE_MESSAGE_PROTECTED_HEADER_KID_CLAIM_KEY = 4;
  private static final int COSE_MESSAGE_PROTECTED_HEADER_ALGO_CLAIM_KEY = 1;
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

  public static String getJSONPayloadFromCBORMessage(String cborMessage) {
    return cborMessage.substring(cborMessage.indexOf(HCERT_CLAIM_KEY) + START_INDEX_OF_HCERT_JSON_PAYLOAD,
        cborMessage.lastIndexOf("}") - 1);
  }

  private static String getAlgoProtectedHeader(Message coseMessage) {
    return coseMessage.getProtectedAttributes().get(COSE_MESSAGE_PROTECTED_HEADER_ALGO_CLAIM_KEY).toString();
  }

  public static String getAlgo(Message coseMessage) {
    int algoId = Integer.parseInt(getAlgoProtectedHeader(coseMessage));
    StringBuilder algo = new StringBuilder();
    if (algoId == HcertAlgo.ECDSA_256.getAlgo()) {
      algo.append(HcertAlgo.ECDSA_256);
    }
    if (algoId == HcertAlgo.RSA_PSS_256.getAlgo()) {
      algo.append(HcertAlgo.RSA_PSS_256);
    }
    return algo.toString();
  }

  private static String getKIDProtectedHeader(Message coseMessage) {
    return coseMessage.getProtectedAttributes().get(COSE_MESSAGE_PROTECTED_HEADER_KID_CLAIM_KEY).toString();
  }

  private static String getTrimmedKID(String kidHex) {
    return kidHex.substring(START_INDEX_OF_HEX_STRING, kidHex.lastIndexOf("'"));
  }

  public static String getKID(Message coseMessage) {
    try {
      String kidHex = getKIDProtectedHeader(coseMessage);
      String kidHexTrimmed = getTrimmedKID(kidHex);
      byte[] kidBytes = Hex.decodeHex(kidHexTrimmed.toCharArray());
      return Base64.encodeBase64String(kidBytes);
    } catch (DecoderException e) {
      throw new MessageDecodeException(MESSAGE_DECODE_EXCEPTION, e);
    }
  }

}
