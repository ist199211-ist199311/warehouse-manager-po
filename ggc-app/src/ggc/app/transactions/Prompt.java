package ggc.app.transactions;

/** Transactions menu interaction prompts. */
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

  /** @return string prompting for a date. */
  static String paymentDeadline() {
    return "Data limite de pagamento: ";
  }

  /** @return string prompting for a price. */
  static String price() {
    return "Preço: ";
  }

  /** @return string prompting for alpha. */
  static String alpha() {
    return "Agravamento: ";
  }

  /** @return string prompting for a quantity. */
  static String amount() {
    return "Quantidade: ";
  }

  /** @return string prompting for recipe. */
  static String addRecipe() {
    return "Registar receita? ";
  }

  /** @return string prompting for number of components. */
  static String numberOfComponents() {
    return "Número de componentes: ";
  }

}
