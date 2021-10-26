package ggc.products;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Recipe implements Serializable {
  /**
   * Serial number for serialization.
   */
  @Serial
  private static final long serialVersionUID = 202110221420L;

  private final List<RecipeProduct> recipeProducts = new ArrayList<>();
  private final double aggravatingFactor;

  public Recipe(double aggravatingFactor, List<RecipeProduct> products) {
    this.aggravatingFactor = aggravatingFactor;
    recipeProducts.addAll(products);
  }

  public double getAggravatingFactor() {
    return aggravatingFactor;
  }

  public List<RecipeProduct> getRecipeProducts() {
    return recipeProducts;
  }

  @Override
  public String toString() {
    return this.getAggravatingFactor() + "|"
        + this.getRecipeProducts()
            .stream()
            .map(RecipeProduct::toString)
            .collect(Collectors.joining("#"));
  }
}
