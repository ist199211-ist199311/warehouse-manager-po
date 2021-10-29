package ggc.app.products;

import ggc.WarehouseManager;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/**
 * Show all products.
 */
class DoShowAllProducts extends Command<WarehouseManager> {

  DoShowAllProducts(WarehouseManager receiver) {
    super(Label.SHOW_ALL_PRODUCTS, receiver);
  }

  @Override
  public final void execute() throws CommandException {
    _receiver.getAllProducts().forEach(_display::popup);
  }

}
