package ggc.app.lookups;

/**
 * Prompts.
 */
interface Prompt {

  /** @return string prompting for product identifier */
  static String productKey() {
    return "Identificador do produto: ";
  }

  /** @return string prompting for a partner identifier. */
  static String partnerKey() {
    return "Identificador do parceiro: ";
  }

  /** @return string prompting for identifier */
  static String transactionKey() {
    return "Identificador da transacção: ";
  }

  /** @return string prompting for a price. */
  static String priceLimit() {
    return "Preço: ";
  }

  /** @return string prompting for a delay. */
  static String delay() {
    return "Atraso: ";
  }

}