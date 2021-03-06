package ggc.transactions;

import ggc.partners.Partner;
import ggc.products.Batch;
import ggc.products.Product;
import ggc.util.Visitor;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class BreakdownTransaction extends Transaction {
  /**
   * Serial number for serialization.
   */
  @Serial
  private static final long serialVersionUID = 202111092051L;

  private final Collection<Batch> resultingBatches = new ArrayList<>();

  public BreakdownTransaction(int id, int date, double value, int quantity,
                              Product product, Partner partner,
                              Collection<Batch> resultingBatches) {
    super(id, value, quantity, product, partner);
    this.resultingBatches.addAll(resultingBatches);
    this.getPartner().applyBreakdownSideEffects(this, date);
    this.setPaymentDate(date);
  }

  public double paidValue() {
    return Math.max(0, this.baseValue());
  }

  public Collection<Batch> getResultingBatches() {
    return Collections.unmodifiableCollection(this.resultingBatches);
  }

  @Override
  public <T> T accept(Visitor<T> visitor) {
    return visitor.visit(this);
  }
}
