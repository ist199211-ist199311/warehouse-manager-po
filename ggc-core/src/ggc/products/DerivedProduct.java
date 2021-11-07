package ggc.products;

import ggc.partners.Partner;
import ggc.util.Visitor;

import java.io.Serial;

public class DerivedProduct extends Product {
  /**
   * Serial number for serialization.
   */
  @Serial
  private static final long serialVersionUID = 202110221420L;

  private final Recipe recipe;

  public DerivedProduct(String id, Recipe recipe) {
    super(id);
    this.recipe = recipe;
  }

  /**
   * @return the sum of the in-stock quantity and the
   *         {@link DerivedProduct#getBuildableQuantity() buildable quantity}
   */
  @Override
  public int getTotalQuantity() {
    return super.getTotalQuantity() + this.getBuildableQuantity();
  }

  /**
   * Calculate the quantity of the product that can be built using the recipe.
   *
   * @return the quantity of the product that can be built using the recipe
   */
  public int getBuildableQuantity() {
    // TODO
    return 0;
  }

  /**
   * Use this product's recipe to create a new batch, consuming the recipe
   * products.
   *
   * @param quantity The quantity to be built
   * @param partner  The partner that wants the product to be built
   */
  public void buildFromRecipe(int quantity, Partner partner) {
    // TODO
  }

  public Recipe getRecipe() {
    return this.recipe;
  }

  @Override
  public String toString() {
    return super.toString() + "|" + this.recipe.toString();
  }

  @Override
  public <T> T accept(Visitor<T> visitor) {
    return visitor.visit(this);
  }
}
