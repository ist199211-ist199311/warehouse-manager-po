package ggc.app.transactions;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
//FIXME import classes
import ggc.exceptions.UnknownTransactionKeyException;

/**
 * Receive payment for sale transaction.
 */
public class DoReceivePayment extends Command<WarehouseManager> {

  public DoReceivePayment(WarehouseManager receiver) {
    super(Label.RECEIVE_PAYMENT, receiver);
    addStringField("transactionId", Prompt.transactionKey());
  }

  @Override
  public final void execute() throws CommandException {
    try {
      this._receiver.receivePayment(this.stringField("transactionId"));
    } catch (UnknownTransactionKeyException e) {
      throw new ggc.app.exceptions.UnknownTransactionKeyException(e.getKey());
    }
  }

}
