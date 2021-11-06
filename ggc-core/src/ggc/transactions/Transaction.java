package ggc.transactions;

import ggc.partners.Partner;
import ggc.products.Product;

import java.util.Optional;

public abstract class Transaction {

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

  public int getId() {
    return id;
  }

  public int getQuantity() {
    return quantity;
  }

  public Product getProduct() {
    return product;
  }

  public Partner getPartner() {
    return partner;
  }

  public Optional<Integer> getPaymentDate() {
    return Optional.ofNullable(paymentDate);
  }

  protected void setPaymentDate(int date) {
    this.paymentDate = date;
  }
}
