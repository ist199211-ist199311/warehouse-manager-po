package ggc.app.main;

/** Messages for interaction. */
interface Message {

  /** @return string showing current date. */
  static String currentDate(int date) {
    return "Data actual: " + date;
  }

  /**
   * @param available  available balance
   * @param accounting accounting balance
   * @return string describing balance.
   */
  static String currentBalance(double available, double accounting) {
    return "Saldo disponível: " + Math.round(accounting) + "\n" + "Saldo contabilístico: " + Math.round(accounting);
  }

}