package ggc.app.products;

import ggc.WarehouseManager;
import ggc.app.Stringifier;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/**
 * Show all products.
 */
class DoShowAllProducts extends Command<WarehouseManager> {

  private final Stringifier stringifier = new Stringifier();

  DoShowAllProducts(WarehouseManager receiver) {
    super(Label.SHOW_ALL_PRODUCTS, receiver);
  }

  @Override
  public final void execute() throws CommandException {
    _receiver.getAllProducts()
        .stream()
        .map(v -> v.accept(stringifier))
        .forEach(_display::popup);
  }

}
