package ch.vitoco.decovid19core.enums;

/**
 * Representation enum of supported CBOR content claim keys.
 */
public enum HcertCBORKeys {

  /**
   * CBOR content protected header key.
   */
  PROTECTED_HEADER(0),
  /**
   * CBOR content unprotected header key.
   */
  UNPROTECTED_HEADER(1),
  /**
   * CBOR message content key.
   */
  MESSAGE_CONTENT(2),
  /**
   * CBOR signature content key.
   */
  SIGNATUR(3);

  /**
   * The cbor content key.
   */
  private final int cborKey;

  /**
   * Constructor.
   *
   * @param cborKey the value of the CBOR content keys as int
   */
  HcertCBORKeys(int cborKey) {
    this.cborKey = cborKey;
  }

  /**
   * Gets the CBOR content key as int.
   *
   * @return CBRO content key as int
   */
  public int getCborKey() {
    return this.cborKey;
  }

}
