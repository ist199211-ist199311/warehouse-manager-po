package ggc.products;

import ggc.partners.Partner;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.StringJoiner;

public class Batch implements Comparable<Batch>, Serializable {
  /**
   * Serial number for serialization.
   */
  @Serial
  private static final long serialVersionUID = 202110221420L;

  private int quantity;
  private double price;
  private Product product;
  private Partner partner;

  public Batch(int quantity, double price, Product product, Partner partner) {
    this.quantity = quantity;
    this.price = price;
    this.product = product;
    this.partner = partner;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public double getPrice() {
    return price;
  }

  public Product getProduct() {
    return product;
  }

  public Partner getPartner() {
    return partner;
  }

  @Override
  public int compareTo(Batch batch) {
    // TODO review
    return Comparator.comparing(Batch::getProduct)
        .thenComparing(Batch::getPartner)
        .thenComparingDouble(Batch::getPrice)
        .thenComparingInt(Batch::getQuantity)
        .compare(this, batch);
  }

  @Override
  public String toString() {
    return new StringJoiner("|")
        .add(this.getProduct().getId())
        .add(this.getPartner().getId())
        .add(Long.toString(Math.round(this.getPrice())))
        .add(Integer.toString(this.getQuantity()))
        .toString();
  }
}
