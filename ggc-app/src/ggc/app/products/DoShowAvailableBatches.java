package ggc.app.products;

import ggc.WarehouseManager;
import ggc.app.Stringifier;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/**
 * Show available batches.
 */
class DoShowAvailableBatches extends Command<WarehouseManager> {

  private final Stringifier stringifier = new Stringifier();

  DoShowAvailableBatches(WarehouseManager receiver) {
    super(Label.SHOW_AVAILABLE_BATCHES, receiver);
  }

  @Override
  public final void execute() throws CommandException {
    _receiver.getAllBatches()
            .stream()
            .map(v -> v.accept(stringifier))
            .forEach(_display::popup);
  }

}
