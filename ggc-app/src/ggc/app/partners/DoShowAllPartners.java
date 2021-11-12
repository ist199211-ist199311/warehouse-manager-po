package ggc.app.partners;

import ggc.WarehouseManager;
import ggc.app.Stringifier;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/**
 * Show all partners.
 */
class DoShowAllPartners extends Command<WarehouseManager> {

  private final Stringifier stringifier = new Stringifier();

  DoShowAllPartners(WarehouseManager receiver) {
    super(Label.SHOW_ALL_PARTNERS, receiver);
  }

  @Override
  public void execute() throws CommandException {
    _receiver.getAllPartners()
            .stream()
            .map(v -> v.accept(stringifier))
            .forEach(_display::popup);
  }

}
