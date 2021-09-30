package ggc.app.partners;

/** Messages for partner menu interactions. */
interface Prompt {

  /** @return string prompting for a partner identifier. */
  static String partnerKey() {
    return "Identificador do parceiro: ";
  }

  /** @return string prompting for a partner name. */
  static String partnerName() {
    return "Nome do parceiro: ";
  }

  /** @return string prompting for an address. */
  static String partnerAddress() {
    return "Endereço do parceiro: ";
  }

  /** @return string prompting for a product identifier. */
  static String productKey() {
    return "Identificador do produto: ";
  }

}
