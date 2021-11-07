package ggc.products;

import ggc.util.Visitable;
import ggc.util.Visitor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Recipe implements Serializable, Visitable {
  /**
   * Serial number for serialization.
   */
  @Serial
  private static final long serialVersionUID = 202110221420L;

  private final List<RecipeComponent> recipeComponents = new ArrayList<>();
  private final double aggravatingFactor;

  public Recipe(double aggravatingFactor, List<RecipeComponent> products) {
    this.aggravatingFactor = aggravatingFactor;
    this.recipeComponents.addAll(products);
  }

  public double getAggravatingFactor() {
    return this.aggravatingFactor;
  }

  public List<RecipeComponent> getRecipeProducts() {
    return this.recipeComponents;
  }

  @Override
  public String toString() {
    return this.getAggravatingFactor() + "|"
        + this.getRecipeProducts()
            .stream()
            .map(RecipeComponent::toString)
            .collect(Collectors.joining("#"));
  }

  @Override
  public <T> T accept(Visitor<T> visitor) {
    return visitor.visit(this);
  }
}
