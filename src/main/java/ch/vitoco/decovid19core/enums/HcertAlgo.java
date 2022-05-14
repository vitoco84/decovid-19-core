package ch.vitoco.decovid19core.enums;

/**
 * Represenation of supported signature algorithms.
 */
public enum HcertAlgo {
  /**
   * ECDSA with SHA-256
   */
  ECDSA_256(-7),
  /**
   * ECDSA with SHA-384
   */
  ECDSA_384(-35),
  /**
   * ECDSA with SHA-512
   */
  ECDSA_512(-36),
  /**
   * RSASSA-PSS with SHA-256
   */
  RSA_PSS_256(-37),
  /**
   * RSASSA-PSS with SHA-384
   */
  RSA_PSS_384(-38),
  /**
   * RSASSA-PSS with SHA-512
   */
  RSA_PSS_512(-39);

  private final int algo;

  HcertAlgo(int algo) {
    this.algo = algo;
  }

  public int getAlgo() {
    return this.algo;
  }

}
