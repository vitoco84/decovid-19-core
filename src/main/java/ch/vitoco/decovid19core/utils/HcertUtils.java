package ch.vitoco.decovid19core.utils;

import COSE.CoseException;
import COSE.Message;
import ch.vitoco.decovid19core.exception.CborMessageException;
import ch.vitoco.decovid19core.exception.CoseMessageException;
import ch.vitoco.decovid19core.exception.ImageDecodeException;
import com.google.iot.cbor.CborMap;
import com.google.iot.cbor.CborParseException;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import nl.minvws.encoding.Base45;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import static ch.vitoco.decovid19core.utils.ExceptionMessages.*;

public class HcertUtils {

  private static final int BUFFER_SIZE = 1024;
  private static final String HCERT_CLAIM_KEY = "-260";
  private static final int INDEX_UNTIL_START_OF_JSON_PAYLOAD = 8;
  private static final int INDEX_UNTIL_START_OF_HC1_PREFIX = 4;
  private static final int START_OFFSET_BYTES_WRITER = 0;

  private HcertUtils() {
    throw new IllegalStateException(UTILITY_CLASS_EXCEPTION_MESSAGE);
  }

  public static String getHealthCertificateContent(InputStream imageFileInputStream) {
    try {
      BufferedImage bufferedImage = ImageIO.read(imageFileInputStream);
      LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
      BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
      Result result = new MultiFormatReader().decode(bitmap);
      return result.getText();
    } catch (IOException | NotFoundException e) {
      throw new ImageDecodeException(IMAGE_DECODE_EXCEPTION_MESSAGE, e);
    }
  }

  private static byte[] decodeBase45HealthCertificate(String hcert) {
    String hcertWithoutPrefix = removeHealthCertificateHC1Prefix(hcert);
    return Base45.getDecoder().decode(hcertWithoutPrefix);
  }

  private static String removeHealthCertificateHC1Prefix(String hcert) {
    return hcert.substring(INDEX_UNTIL_START_OF_HC1_PREFIX);
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
      throw new CoseMessageException(COSE_FORMAT_EXCEPTION_MESSAGE, e);
    }
  }

  public static Message getCOSEMessageFromHcert(String hcert) {
    byte[] hcertBytes = decodeBase45HealthCertificate(hcert);
    ByteArrayOutputStream coseMessageFromHcert = getCOSEMessageFromHcert(hcertBytes);
    try {
      return Message.DecodeFromBytes(coseMessageFromHcert.toByteArray());
    } catch (CoseException e) {
      throw new CoseMessageException(COSE_DECODE_EXCEPTION_MESSAGE, e);
    }
  }

  public static String getCBORMessage(Message hcertCOSEMessage) {
    try {
      return CborMap.createFromCborByteArray(hcertCOSEMessage.GetContent()).toString();
    } catch (CborParseException e) {
      throw new CborMessageException(CBOR_DECODE_EXCEPTION_MESSAGE, e);
    }
  }

  public static String getJsonPayloadFromCBORMessage(String cborMessage) {
    return cborMessage.substring(cborMessage.indexOf(HCERT_CLAIM_KEY) + INDEX_UNTIL_START_OF_JSON_PAYLOAD, cborMessage.lastIndexOf("}") - 1);
  }

}
