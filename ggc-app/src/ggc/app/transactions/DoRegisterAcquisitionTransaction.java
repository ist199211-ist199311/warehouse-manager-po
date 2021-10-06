package ggc.app.transactions;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
//FIXME import classes

/**
 * Register order.
 */
public class DoRegisterAcquisitionTransaction extends Command<WarehouseManager> {

  public DoRegisterAcquisitionTransaction(WarehouseManager receiver) {
    super(Label.REGISTER_ACQUISITION_TRANSACTION, receiver);
    addStringField("partnerId", Prompt.partnerKey());
    addStringField("productId", Prompt.productKey());
    addRealField("price", Prompt.price());
    addStringField("quantity", Prompt.amount());
    // TODO add logic for new product
  }

  @Override
  public final void execute() throws CommandException {
    //FIXME implement command
  }

}
