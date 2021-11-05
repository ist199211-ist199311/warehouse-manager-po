package ggc.products;

import java.io.Serial;
import java.io.Serializable;

public class RecipeComponent implements Serializable {
  /**
   * Serial number for serialization.
   */
  @Serial
  private static final long serialVersionUID = 202110221420L;

  private final int quantity;
  private final Product product;

  public RecipeComponent(int quantity, Product product) {
    this.quantity = quantity;
    this.product = product;
  }

  public int getQuantity() {
    return quantity;
  }

  public Product getProduct() {
    return product;
  }

  @Override
  public String toString() {
    return this.getProduct().getId() + ":" + this.getQuantity();
  }
}
