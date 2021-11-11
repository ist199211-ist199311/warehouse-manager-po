package ggc.transactions;

import ggc.notifications.BargainProductNotification;
import ggc.notifications.NewProductNotification;
import ggc.partners.Partner;
import ggc.products.Batch;
import ggc.products.DerivedProduct;
import ggc.products.Product;
import ggc.products.Recipe;
import ggc.util.Visitor;

public class PaymentProcessor extends Visitor<Double> {
  private int date;

  public PaymentProcessor(int date) {
    this.date = date;
  }

  public Double visit(AcquisitionTransaction transaction) {
    return 0D;
  }

  public Double visit(BreakdownTransaction transaction) {
    return 0D;
  }

  public Double visit(SaleTransaction transaction) {
    transaction.pay(date);
    return transaction.adjustedValue();
  }

  public Double visit(Batch batch) {
    throw new UnsupportedOperationException();
  }

  public Double visit(Recipe recipe) {
    throw new UnsupportedOperationException();
  }

  public Double visit(Product product) {
    throw new UnsupportedOperationException();
  }

  public Double visit(DerivedProduct product) {
    throw new UnsupportedOperationException();
  }

  public Double visit(Partner partner) {
    throw new UnsupportedOperationException();
  }

  public Double visit(NewProductNotification notification) {
    throw new UnsupportedOperationException();
  }

  public Double visit(BargainProductNotification notification) {
    throw new UnsupportedOperationException();
  }

}