package ggc.app.products;

/**
 * Prompts.
 */
interface Prompt {

  /** @return string prompting for product identifier */
  static String productKey() {
    return "Identificador do produto: ";
  }

  /** @return string prompting for partner identifier */
  static String partnerKey() {
    return "Identificador do parceiro: ";
  }

}
