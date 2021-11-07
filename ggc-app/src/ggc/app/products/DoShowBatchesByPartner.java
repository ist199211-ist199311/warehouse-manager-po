package ggc.app.products;

import ggc.WarehouseManager;
import ggc.app.Stringifier;
import ggc.app.exceptions.UnknownPartnerKeyException;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/**
 * Show batches supplied by partner.
 */
class DoShowBatchesByPartner extends Command<WarehouseManager> {

  private final Stringifier stringifier = new Stringifier();

  DoShowBatchesByPartner(WarehouseManager receiver) {
    super(Label.SHOW_BATCHES_SUPPLIED_BY_PARTNER, receiver);
    addStringField("partnerId", Prompt.partnerKey());
  }

  @Override
  public final void execute() throws CommandException {
    try {
      _receiver.getBatchesByPartner(stringField("partnerId"))
          .stream()
          .map(v -> v.accept(stringifier))
          .forEach(_display::popup);
    } catch (ggc.exceptions.UnknownPartnerKeyException e) {
      throw new UnknownPartnerKeyException(e.getKey());
    }
  }

}
