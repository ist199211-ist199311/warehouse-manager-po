package ggc.util;

import ggc.partners.Partner;
import ggc.products.Batch;
import ggc.products.DerivedProduct;
import ggc.products.Product;
import ggc.products.Recipe;
import ggc.transactions.AcquisitionTransaction;
import ggc.transactions.BreakdownTransaction;
import ggc.transactions.SaleTransaction;

public abstract class Visitor<T> {

  public abstract T visit(AcquisitionTransaction transaction);

  public abstract T visit(BreakdownTransaction transaction);

  public abstract T visit(SaleTransaction transaction);

  public abstract T visit(Batch batch);

  public abstract T visit(Recipe recipe);

  public abstract T visit(Product product);

  public abstract T visit(DerivedProduct product);

  public abstract T visit(Partner partner);

}
