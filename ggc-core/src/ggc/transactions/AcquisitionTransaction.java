package ggc.transactions;

import java.io.Serial;

import ggc.products.Batch;
import ggc.util.Visitor;

public class AcquisitionTransaction extends Transaction {
  /**
   * Serial number for serialization.
   */
  @Serial
  private static final long serialVersionUID = 202111092052L;

  public AcquisitionTransaction(int id, int date, Batch batch) {
    super(id,
        batch.price(),
        batch.quantity(),
        batch.product(),
        batch.partner());
    this.setPaymentDate(date);
  }

  public double totalValue() {
    return this.baseValue() * this.getQuantity();
  }

  @Override
  public <T> T accept(Visitor<T> visitor) {
    return visitor.visit(this);
  }
}
