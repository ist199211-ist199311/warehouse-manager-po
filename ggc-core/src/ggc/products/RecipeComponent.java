package ggc.products;

import java.io.Serial;
import java.io.Serializable;

public record RecipeComponent(int quantity, Product product)
        implements Serializable {
  /**
   * Serial number for serialization.
   */
  @Serial
  private static final long serialVersionUID = 202110221420L;

}
