package ch.vitoco.decovid19core.enums;

/**
 * Representation enum of supported digital signature algorithms.
 */
public enum HcertSignatureAlgoKeys {

  /**
   * ECDSA.
   */
  ECDSA("ECDSA"),
  /**
   * EC.
   */
  EC("EC"),
  /**
   * RSA.
   */
  RSA("RSA"),

  /**
   * RSASSA-PSS.
   */
  RSASSA_PSS("RSASSA-PSS");

  /**
   * The algorithm name.
   */
  private final String name;

  /**
   * Constructor.
   *
   * @param name the name of the digital signature algorithm
   */
  HcertSignatureAlgoKeys(String name) {
    this.name = name;
  }

  /**
   * Gets the name of the digital signature algorithm.
   *
   * @return digital signature algorithm name
   */
  public String getName() {
    return name;
  }
}
