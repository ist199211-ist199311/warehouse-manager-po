package ggc.transactions;

import ggc.partners.Partner;
import ggc.products.Batch;
import ggc.products.Product;
import ggc.util.Visitable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;

public abstract class Transaction implements Serializable, Visitable {
  /**
   * Serial number for serialization.
   */
  @Serial
  private static final long serialVersionUID = 202111092049L;

  private final int id;
  private final double value;
  private final int quantity;
  private final Product product;
  private final Partner partner;
  private Integer paymentDate = null;

  protected Transaction(int id, double value, int quantity, Product product,
      Partner partner) {
    this.id = id;
    this.value = value;
    this.quantity = quantity;
    this.product = product;
    this.partner = partner;
  }

  public double baseValue() {
    return this.value;
  }

  public Batch asBatch() {
    return new Batch(
        this.quantity,
        this.baseValue(),
        this.product,
        this.partner);
  }

  public int getId() {
    return this.id;
  }

  public int getQuantity() {
    return this.quantity;
  }

  public Product getProduct() {
    return this.product;
  }

  public Partner getPartner() {
    return this.partner;
  }

  public boolean isPaid() {
    return paymentDate != null;
  }

  public Optional<Integer> getPaymentDate() {
    return Optional.ofNullable(paymentDate);
  }

  protected void setPaymentDate(int date) {
    this.paymentDate = date;
  }
}
