package ggc.app.partners;

import ggc.WarehouseManager;
import ggc.app.exceptions.UnknownPartnerKeyException;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/**
 * Show partner.
 */
class DoShowPartner extends Command<WarehouseManager> {

  DoShowPartner(WarehouseManager receiver) {
    super(Label.SHOW_PARTNER, receiver);
    addStringField("partnerId", Prompt.partnerKey());
  }

  @Override
  public void execute() throws CommandException {
    try {
      _display.popup(_receiver.getPartner(stringField("partnerId")));
    } catch (ggc.exceptions.UnknownPartnerKeyException e) {
      throw new UnknownPartnerKeyException(e.getKey());
    }
  }

}
