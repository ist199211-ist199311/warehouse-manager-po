package ggc.transactions;

import ggc.products.Batch;

import java.util.StringJoiner;

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
  public String toString() {
    return new StringJoiner("|")
            .add("COMPRA")
            .add(Integer.toString(getId()))
            .add(getPartner().getId())
            .add(getProduct().getId())
            .add(Integer.toString(getQuantity()))
            .add(Long.toString(Math.round(totalValue())))
            .add(Integer.toString(getPaymentDate().orElse(0)))
            .toString();
  }
}
