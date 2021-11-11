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

/**
 * Gets the adjusted value for sale transactions, falling back to 0 for other
 * transactions.
 */
public class AdjustedValueCalculator extends Visitor<Double> {
  private int date;

  public AdjustedValueCalculator(int date) {
    this.date = date;
  }

  @Override
  public Double visit(AcquisitionTransaction transaction) {
    return 0D;
  }

  @Override
  public Double visit(BreakdownTransaction transaction) {
    return 0D;
  }

  @Override
  public Double visit(SaleTransaction transaction) {
    transaction.calculateAdjustedValue(date);
    return transaction.adjustedValue();
  }

  @Override
  public Double visit(Batch batch) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Double visit(Recipe recipe) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Double visit(Product product) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Double visit(DerivedProduct product) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Double visit(Partner partner) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Double visit(NewProductNotification notification) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Double visit(BargainProductNotification notification) {
    throw new UnsupportedOperationException();
  }
}
