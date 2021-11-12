package ggc.app.lookups;

import ggc.WarehouseManager;
import ggc.app.Stringifier;
import ggc.app.exceptions.UnknownPartnerKeyException;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/**
 * Lookup payments by given partner.
 */
public class DoLookupPaymentsByPartner extends Command<WarehouseManager> {

  private final Stringifier stringifier = new Stringifier();

  public DoLookupPaymentsByPartner(WarehouseManager receiver) {
    super(Label.PAID_BY_PARTNER, receiver);
    this.addStringField("partnerId", Prompt.partnerKey());
  }

  @Override
  public void execute() throws CommandException {
    try {
      this._receiver.lookupPaymentsByPartner(this.stringField("partnerId"))
              .stream()
              .map(v -> v.accept(stringifier))
              .forEach(_display::popup);
    } catch (ggc.exceptions.UnknownPartnerKeyException e) {
      throw new UnknownPartnerKeyException(e.getKey());
    }
  }

}
