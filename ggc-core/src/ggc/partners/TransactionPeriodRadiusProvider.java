package ggc.partners;

import ggc.products.Batch;
import ggc.products.DerivedProduct;
import ggc.products.Product;
import ggc.products.Recipe;
import ggc.transactions.AcquisitionTransaction;
import ggc.transactions.BreakdownTransaction;
import ggc.transactions.SaleTransaction;
import ggc.util.Visitor;

public class TransactionPeriodRadiusProvider extends Visitor<Integer> {
  public Integer visit(AcquisitionTransaction transaction) {
    throw new UnsupportedOperationException();
  }

  public Integer visit(BreakdownTransaction transaction) {
    throw new UnsupportedOperationException();
  }

  public Integer visit(SaleTransaction transaction) {
    throw new UnsupportedOperationException();
  }

  public Integer visit(Batch batch) {
    throw new UnsupportedOperationException();
  }

  public Integer visit(Recipe recipe) {
    throw new UnsupportedOperationException();
  }

  public Integer visit(Product product) {
    return 5;
  }

  public Integer visit(DerivedProduct product) {
    return 3;
  }

  public Integer visit(Partner partner) {
    throw new UnsupportedOperationException();
  }
}
