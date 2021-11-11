package ggc.transactions;

import java.io.Serial;

import ggc.partners.Partner;
import ggc.products.Product;
import ggc.util.Visitor;

public class SaleTransaction extends Transaction {
  /**
   * Serial number for serialization.
   */
  @Serial
  private static final long serialVersionUID = 202111092050L;

  private int paymentDeadline;
  private Double adjustedValue = null;

  public SaleTransaction(int id, double value, int quantity, Product product,
      Partner partner, int paymentDeadline) {
    super(id, value, quantity, product, partner);
    this.paymentDeadline = paymentDeadline;
  }

  public void pay(int date) {
    this.adjustedValue = this.getPartner().applySaleBenefits(this, date);
    this.setPaymentDate(date);
  }

  public void calculateAdjustedValue(int date) {
    this.adjustedValue = this.getPartner().calculateAdjustedPrice(this, date);
  }

  public double adjustedValue() {
    return adjustedValue == null ? this.baseValue() : adjustedValue;
  }

  public int getPaymentDeadline() {
    return paymentDeadline;
  }

  @Override
  public <T> T accept(Visitor<T> visitor) {
    return visitor.visit(this);
  }
}
