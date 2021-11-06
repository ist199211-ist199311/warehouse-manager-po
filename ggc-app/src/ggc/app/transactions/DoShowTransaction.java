package ggc.app.transactions;

import ggc.WarehouseManager;
import ggc.app.Stringifier;
import ggc.app.exceptions.UnknownTransactionKeyException;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/**
 * Show specific transaction.
 */
public class DoShowTransaction extends Command<WarehouseManager> {

  private final Stringifier stringifier = new Stringifier();

  public DoShowTransaction(WarehouseManager receiver) {
    super(Label.SHOW_TRANSACTION, receiver);
    addIntegerField("transactionId", Prompt.transactionKey());
  }

  @Override
  public final void execute() throws CommandException {
    try {
      _display.popup(_receiver.getTransaction(integerField("transactionId")).accept(stringifier));
    } catch (ggc.exceptions.UnknownTransactionKeyException e) {
      throw new UnknownTransactionKeyException(e.getKey());
    }
  }

}
