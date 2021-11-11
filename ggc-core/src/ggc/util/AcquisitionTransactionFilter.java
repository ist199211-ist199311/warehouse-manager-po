package ggc.util;

import ggc.notifications.BargainProductNotification;
import ggc.notifications.NewProductNotification;
import ggc.partners.Partner;
import ggc.products.Batch;
import ggc.products.DerivedProduct;
import ggc.products.Product;
import ggc.products.Recipe;
import ggc.transactions.AcquisitionTransaction;
import ggc.transactions.BreakdownTransaction;
import ggc.transactions.SaleTransaction;

public class AcquisitionTransactionFilter extends Visitor<Boolean> {

  @Override
  public Boolean visit(AcquisitionTransaction transaction) {
    return true;
  }

  @Override
  public Boolean visit(BreakdownTransaction transaction) {
    return false;
  }

  @Override
  public Boolean visit(SaleTransaction transaction) {
    return false;
  }

  @Override
  public Boolean visit(Batch batch) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean visit(Recipe recipe) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean visit(Product product) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean visit(DerivedProduct product) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean visit(Partner partner) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean visit(NewProductNotification notification) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Boolean visit(BargainProductNotification notification) {
    throw new UnsupportedOperationException();
  }
}
