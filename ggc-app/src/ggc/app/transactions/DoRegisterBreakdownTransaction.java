package ggc.app.transactions;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
import ggc.app.exceptions.UnavailableProductException;
//FIXME import classes
import ggc.app.exceptions.UnknownPartnerKeyException;
import ggc.app.exceptions.UnknownProductKeyException;

/**
 * Register order.
 */
public class DoRegisterBreakdownTransaction extends Command<WarehouseManager> {

  public DoRegisterBreakdownTransaction(WarehouseManager receiver) {
    super(Label.REGISTER_BREAKDOWN_TRANSACTION, receiver);
    addStringField("partnerId", Prompt.partnerKey());
    addStringField("productId", Prompt.productKey());
    addIntegerField("quantity", Prompt.amount());
  }

  @Override
  public final void execute() throws CommandException {
    try {
      this.registerBreakdownTransaction(
          stringField("partnerId"),
          stringField("productId"),
          integerField("quantity"));
    } catch (ggc.exceptions.UnknownPartnerKeyException e) {
      throw new UnknownPartnerKeyException(e.getKey());
    } catch (ggc.exceptions.UnknownProductKeyException e) {
      throw new UnknownProductKeyException(e.getKey());
    }
  }

  private void registerBreakdownTransaction(String partnerId, String productId,
      Integer quantity) throws ggc.exceptions.UnknownPartnerKeyException,
      ggc.exceptions.UnknownProductKeyException {
    try {
      _receiver.registerBreakdownTransaction(partnerId, productId, quantity);
    } catch (ggc.exceptions.UnavailableProductException e) {
      throw new UnavailableProductException(
          e.getKey(),
          e.getRequested(),
          e.getAvailable());
    }
  }

}
