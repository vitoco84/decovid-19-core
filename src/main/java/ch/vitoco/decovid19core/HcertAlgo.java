package ch.vitoco.decovid19core;

public enum HcertAlgo {
  ECDSA_256(-7), RSA_PSS_256(-37);

  private final int algo;

  HcertAlgo(int algo) {
    this.algo = algo;
  }

  public int getAlgo() {
    return this.algo;
  }

}
