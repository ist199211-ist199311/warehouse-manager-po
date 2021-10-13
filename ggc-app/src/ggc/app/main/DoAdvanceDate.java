package ggc.app.main;

import ggc.WarehouseManager;
import ggc.app.exceptions.InvalidDateException;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/**
 * Advance current date.
 */
class DoAdvanceDate extends Command<WarehouseManager> {

  DoAdvanceDate(WarehouseManager receiver) {
    super(Label.ADVANCE_DATE, receiver);
    addIntegerField("days", Prompt.daysToAdvance());
  }

  @Override
  public final void execute() throws CommandException {
    try {
      _receiver.advanceDate(integerField("days"));
    } catch (ggc.exceptions.InvalidDateException e) {
      throw new InvalidDateException(e.getDate());
    }
  }

}
