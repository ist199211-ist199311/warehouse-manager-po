package ggc.app.transactions;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
import ggc.app.exceptions.UnknownTransactionKeyException;

/**
 * Receive payment for sale transaction.
 */
public class DoReceivePayment extends Command<WarehouseManager> {

  public DoReceivePayment(WarehouseManager receiver) {
    super(Label.RECEIVE_PAYMENT, receiver);
    addIntegerField("transactionId", Prompt.transactionKey());
  }

  @Override
  public final void execute() throws CommandException {
    try {
      this._receiver.receivePayment(this.integerField("transactionId"));
    } catch (ggc.exceptions.UnknownTransactionKeyException e) {
      throw new UnknownTransactionKeyException(e.getKey());
    }
  }

}
