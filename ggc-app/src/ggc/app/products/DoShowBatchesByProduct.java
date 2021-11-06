package ggc.app.products;

import ggc.WarehouseManager;
import ggc.app.Stringifier;
import ggc.app.exceptions.UnknownProductKeyException;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/**
 * Show all products.
 */
class DoShowBatchesByProduct extends Command<WarehouseManager> {

  private final Stringifier stringifier = new Stringifier();

  DoShowBatchesByProduct(WarehouseManager receiver) {
    super(Label.SHOW_BATCHES_BY_PRODUCT, receiver);
    addStringField("productId", Prompt.productKey());
  }

  @Override
  public final void execute() throws CommandException {
    try {
      _receiver.getBatchesByProduct(stringField("partnerId"))
              .stream()
              .map(v -> v.accept(stringifier))
              .forEach(_display::popup);
    } catch (ggc.exceptions.UnknownProductKeyException e) {
      throw new UnknownProductKeyException(e.getKey());
    }
  }

}
