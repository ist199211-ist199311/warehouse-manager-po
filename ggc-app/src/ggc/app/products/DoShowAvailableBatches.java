package ggc.app.products;

import ggc.WarehouseManager;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/**
 * Show available batches.
 */
class DoShowAvailableBatches extends Command<WarehouseManager> {

  DoShowAvailableBatches(WarehouseManager receiver) {
    super(Label.SHOW_AVAILABLE_BATCHES, receiver);
  }

  @Override
  public final void execute() throws CommandException {
    _receiver.getAllBatches().forEach(_display::popup);
  }

}
