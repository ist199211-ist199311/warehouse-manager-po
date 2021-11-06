package ggc.app.partners;

import ggc.WarehouseManager;
import ggc.app.Stringifier;
import ggc.app.exceptions.UnknownPartnerKeyException;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/**
 * Show all transactions for a specific partner.
 */
class DoShowPartnerSales extends Command<WarehouseManager> {

  private final Stringifier stringifier = new Stringifier();

  DoShowPartnerSales(WarehouseManager receiver) {
    super(Label.SHOW_PARTNER_SALES, receiver);
    addStringField("partnerId", Prompt.partnerKey());
  }

  @Override
  public void execute() throws CommandException {
    try {
      _receiver.getPartnerSalesAndBreakdowns(stringField("partnerId"))
              .stream()
              .map(v -> v.accept(stringifier))
              .forEach(_display::popup);
    } catch (ggc.exceptions.UnknownPartnerKeyException e) {
      throw new UnknownPartnerKeyException(e.getKey());
    }
  }

}
