package ggc.transactions;

import ggc.partners.Partner;
import ggc.products.Product;
import ggc.util.Visitor;

import java.io.Serial;

public class SaleTransaction extends Transaction {
  /**
   * Serial number for serialization.
   */
  @Serial
  private static final long serialVersionUID = 202111092050L;

  private final int paymentDeadline;
  private Double adjustedValue = null;

  public SaleTransaction(int id, double value, int quantity, Product product,
      Partner partner, int paymentDeadline) {
    super(id, value, quantity, product, partner);
    this.paymentDeadline = paymentDeadline;
  }

  public void pay(int date) {
    this.adjustedValue = this.getPartner().applySaleSideEffects(this, date);
    this.setPaymentDate(date);
  }

  public void calculateAdjustedValue(int date) {
    if (this.isPaid())
      return;
    this.adjustedValue = this.getPartner().calculateAdjustedValue(this, date);
  }

  public double adjustedValue() {
    return this.adjustedValue == null ? this.baseValue() : this.adjustedValue;
  }

  public int getPaymentDeadline() {
    return this.paymentDeadline;
  }

  @Override
  public <T> T accept(Visitor<T> visitor) {
    return visitor.visit(this);
  }
}
