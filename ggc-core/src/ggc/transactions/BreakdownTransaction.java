package ggc.transactions;

import ggc.partners.Partner;
import ggc.products.Batch;
import ggc.products.Product;
import ggc.util.Visitor;

import java.io.Serial;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class BreakdownTransaction extends Transaction {
  /**
   * Serial number for serialization.
   */
  @Serial
  private static final long serialVersionUID = 202111092051L;

  private final Set<Batch> resultingBatches = new TreeSet<>();

  public BreakdownTransaction(int id, int date, double value, int quantity,
      Product product, Partner partner, Collection<Batch> resultingBatches) {
    super(id, value, quantity, product, partner);
    this.resultingBatches.addAll(resultingBatches);
    this.setPaymentDate(date);
  }

  @Override
  public <T> T accept(Visitor<T> visitor) {
    return visitor.visit(this);
  }
}
