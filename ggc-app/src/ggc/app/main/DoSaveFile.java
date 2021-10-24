package ggc.app.main;

import ggc.WarehouseManager;
import ggc.exceptions.MissingFileAssociationException;
import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

import java.io.IOException;

/**
 * Save current state to file under current name (if unnamed, query for name).
 */
class DoSaveFile extends Command<WarehouseManager> {

  /**
   * @param receiver
   */
  DoSaveFile(WarehouseManager receiver) {
    super(Label.SAVE, receiver);
  }

  @Override
  public final void execute() throws CommandException {
    try {
      _receiver.save();
    } catch (MissingFileAssociationException e) {
      try {
        _receiver.saveAs(Form.requestString(Prompt.newSaveAs()));
      } catch (MissingFileAssociationException | IOException ex) {
        // TODO ??
        ex.printStackTrace();
      }
    } catch (IOException e) {
      // TODO ??
      e.printStackTrace();
    }
  }

}
