package ggc.transactions;

import ggc.products.Batch;
import ggc.util.Visitor;

public class AcquisitionTransaction extends Transaction {
  public AcquisitionTransaction(int id, int date, Batch batch) {
    // TODO maybe store the batch itself?
    super(id, batch.price(), batch.quantity(), batch.product(),
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
