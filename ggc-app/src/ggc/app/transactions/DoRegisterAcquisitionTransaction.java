package ggc.app.transactions;

import ggc.WarehouseManager;
import ggc.app.exceptions.UnknownPartnerKeyException;
import ggc.app.exceptions.UnknownProductKeyException;
import ggc.exceptions.DuplicateProductKeyException;
import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/**
 * Register order.
 */
public class DoRegisterAcquisitionTransaction extends Command<WarehouseManager> {

  public DoRegisterAcquisitionTransaction(WarehouseManager receiver) {
    super(Label.REGISTER_ACQUISITION_TRANSACTION, receiver);
    addStringField("partnerId", Prompt.partnerKey());
    addStringField("productId", Prompt.productKey());
    addRealField("price", Prompt.price());
    addIntegerField("quantity", Prompt.amount());
  }

  @Override
  public final void execute() throws CommandException {
    try {
      registerAcquisitionTransaction(stringField("partnerId"), stringField("productId"), realField("price"), integerField("quantity"));
    } catch (ggc.exceptions.UnknownPartnerKeyException e) {
      throw new UnknownPartnerKeyException(e.getKey());
    } catch (ggc.exceptions.UnknownProductKeyException e) {
      throw new UnknownProductKeyException(e.getKey());
    }
  }

  private void registerAcquisitionTransaction(String partnerId, String productId, double value, int quantity)
          throws ggc.exceptions.UnknownPartnerKeyException, ggc.exceptions.UnknownProductKeyException {
    try {
      _receiver.registerAcquisitionTransaction(partnerId, productId, value, quantity);
    } catch (ggc.exceptions.UnknownProductKeyException e) {
      try {
        if (Form.confirm(Prompt.addRecipe())) {
          int components = Form.requestInteger(Prompt.numberOfComponents());
          double aggravatingFactor = Form.requestReal(Prompt.alpha());
          String[] recipeProducts = new String[components];
          int[] recipeQuantities = new int[components];

          Form productsForm = new Form();
          productsForm.addStringField("productId", Prompt.productKey());
          productsForm.addIntegerField("quantity", Prompt.amount());

          for (int i = 0; i < components; ++i) {
            productsForm.parse();
            recipeProducts[i] = productsForm.stringField("productId");
            recipeQuantities[i] = productsForm.integerField("quantity");
            productsForm.clear();
          }

          _receiver.registerDerivedProduct(productId, aggravatingFactor, recipeProducts, recipeQuantities);
        } else {
          _receiver.registerSimpleProduct(productId);
        }
        registerAcquisitionTransaction(partnerId, productId, value, quantity);
      } catch (DuplicateProductKeyException duplicateException) {
        duplicateException.printStackTrace();
      }
    }
  }

}
