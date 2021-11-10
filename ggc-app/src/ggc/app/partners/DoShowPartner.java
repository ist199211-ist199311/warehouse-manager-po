package ggc.app.partners;

import ggc.WarehouseManager;
import ggc.app.Stringifier;
import ggc.app.exceptions.UnknownPartnerKeyException;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/**
 * Show partner.
 */
class DoShowPartner extends Command<WarehouseManager> {

  private final Stringifier stringifier = new Stringifier();

  DoShowPartner(WarehouseManager receiver) {
    super(Label.SHOW_PARTNER, receiver);
    addStringField("partnerId", Prompt.partnerKey());
  }

  @Override
  public void execute() throws CommandException {
    try {
      _display.popup(_receiver.getPartner(stringField("partnerId"))
              .accept(stringifier));
      _receiver.readPartnerInAppNotifications(stringField("partnerId"))
              .stream()
              .map(v -> v.accept(stringifier))
              .forEach(_display::popup);
    } catch (ggc.exceptions.UnknownPartnerKeyException e) {
      throw new UnknownPartnerKeyException(e.getKey());
    }
  }

}
