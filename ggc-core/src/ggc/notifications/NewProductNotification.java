package ggc.notifications;

import ggc.products.Product;
import ggc.util.Visitor;

import java.io.Serial;

public class NewProductNotification extends Notification {
  /**
   * Serial number for serialization.
   */
  @Serial
  private static final long serialVersionUID = 202111102333L;

  private final Product product;
  private final double productPrice;

  public NewProductNotification(Product product, double productPrice) {
    this.product = product;
    this.productPrice = productPrice;
  }

  public Product getProduct() {
    return product;
  }

  public double getProductPrice() {
    return productPrice;
  }

  @Override
  public <T> T accept(Visitor<T> visitor) {
    return visitor.visit(this);
  }
}
