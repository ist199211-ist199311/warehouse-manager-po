package ggc.app.partners;

import ggc.WarehouseManager;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/**
 * Show all partners.
 */
class DoShowAllPartners extends Command<WarehouseManager> {

  DoShowAllPartners(WarehouseManager receiver) {
    super(Label.SHOW_ALL_PARTNERS, receiver);
  }

  @Override
  public void execute() throws CommandException {
    _receiver.getAllPartners().forEach(_display::popup);
  }

}
