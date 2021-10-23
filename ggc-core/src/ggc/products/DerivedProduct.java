package ggc.products;

import ggc.exceptions.OutOfStockException;
import ggc.partners.Partner;

public class DerivedProduct extends Product {

  private final Recipe recipe;

  public DerivedProduct(String id, Recipe recipe) {
    super(id);
    this.recipe = recipe;
  }

  @Override
  public int getTotalQuantity() {
    return super.getTotalQuantity() + this.getBuildableQuantity();
  }

  public int getBuildableQuantity() {
    // TODO
    return 0;
  }

  public double getPriceForBreakdown() {
    try {
      return this.getCheapestPrice();
    } catch (OutOfStockException e) {
      // TODO find highest price on transactions
      return 0;
    }
  }

  public void buildFromRecipe(int quantity, Partner partner) {
    // TODO
  }

  public Recipe getRecipe() {
    return recipe;
  }

  @Override
  public String toString() {
    return super.toString() + "|" + this.recipe.toString();
  }
}
