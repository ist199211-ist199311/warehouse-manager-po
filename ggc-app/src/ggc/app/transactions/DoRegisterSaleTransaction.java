package ggc.app.transactions;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
//FIXME import classes

/**
 * 
 */
public class DoRegisterSaleTransaction extends Command<WarehouseManager> {

  public DoRegisterSaleTransaction(WarehouseManager receiver) {
    super(Label.REGISTER_SALE_TRANSACTION, receiver);
    addStringField("partnerId", Prompt.partnerKey());
    addIntegerField("deadline", Prompt.paymentDeadline());
    addStringField("productId", Prompt.productKey());
    addStringField("quantity", Prompt.amount());
  }

  @Override
  public final void execute() throws CommandException {
    //FIXME implement command
  }

}
