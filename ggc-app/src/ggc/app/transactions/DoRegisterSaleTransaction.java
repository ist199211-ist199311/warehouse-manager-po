package ggc.app.transactions;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
import ggc.app.exceptions.UnavailableProductException;
import ggc.app.exceptions.UnknownPartnerKeyException;
import ggc.app.exceptions.UnknownProductKeyException;

/**
 * 
 */
public class DoRegisterSaleTransaction extends Command<WarehouseManager> {

  public DoRegisterSaleTransaction(WarehouseManager receiver) {
    super(Label.REGISTER_SALE_TRANSACTION, receiver);
    addStringField("partnerId", Prompt.partnerKey());
    addIntegerField("deadline", Prompt.paymentDeadline());
    addStringField("productId", Prompt.productKey());
    addIntegerField("quantity", Prompt.amount());
  }

  @Override
  public final void execute() throws CommandException {
    try {
      this._receiver.registerSaleTransaction(
          this.stringField("partnerId"),
          this.stringField("productId"),
          this.integerField("deadline"),
          this.integerField("quantity"));
    } catch (ggc.exceptions.UnknownPartnerKeyException e) {
      throw new UnknownPartnerKeyException(e.getKey());
    } catch (ggc.exceptions.UnknownProductKeyException e) {
      throw new UnknownProductKeyException(e.getKey());
    } catch (ggc.exceptions.UnavailableProductException e) {
      throw new UnavailableProductException(
          e.getKey(),
          e.getRequested(),
          e.getAvailable());
    }
  }

}
