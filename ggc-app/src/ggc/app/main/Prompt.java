package ggc.app.main;

/** Messages for interaction. */
interface Prompt {

  /** @return string with prompt for filename to open. */
  static String openFile() {
    return "Ficheiro a abrir: ";
  }

  /** @return string with a warning and a question. */
  static String newSaveAs() {
    return "Ficheiro sem nome. " + saveAs();
  }

  /** @return string asking for a filename. */
  static String saveAs() {
    return "Guardar ficheiro como: ";
  }

  /** @return string confirming that user wants to save. */
  static String saveBeforeExit() {
    return "Guardar antes de fechar? ";
  }

  /** @return string prompting for the number of days to advance (integer). */
  static String daysToAdvance() {
    return "Número de dias a avançar: ";
  }

}