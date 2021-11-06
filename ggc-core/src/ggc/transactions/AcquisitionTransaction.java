package ggc.transactions;

import ggc.products.Batch;

public class AcquisitionTransaction extends Transaction {
  public AcquisitionTransaction(int id, Batch batch) {
    // TODO maybe store the batch itself?
    super(id, batch.price(), batch.quantity(), batch.product(),
            batch.partner());
  }

  public double totalValue() {
    return this.baseValue() * this.getQuantity();
  }
}
