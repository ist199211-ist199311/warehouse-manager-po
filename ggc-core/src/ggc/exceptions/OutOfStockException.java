package ggc.exceptions;

import ggc.products.Product;

import java.io.Serial;

public class OutOfStockException extends Exception {

  private final Product product;
  private final int quantity;

  /**
   * Class serial number.
   */
  @Serial
  private static final long serialVersionUID = 20211013181804L;

  public OutOfStockException(Product product) {
    this(product, 1);
  }

  public OutOfStockException(Product product, int quantity) {
    this.product = product;
    this.quantity = quantity;
  }

  public Product getProduct() {
    return product;
  }

  public int getQuantity() {
    return quantity;
  }
}
