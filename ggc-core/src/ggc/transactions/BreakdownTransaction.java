package ggc.transactions;

import ggc.partners.Partner;
import ggc.products.Batch;
import ggc.products.Product;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class BreakdownTransaction extends Transaction {

  private final Set<Batch> batches = new TreeSet<>();

  public BreakdownTransaction(int id, double value, int quantity,
                              Product product, Partner partner,
                              Collection<Batch> batches) {
    super(id, value, quantity, product, partner);
    this.batches.addAll(batches);
  }

}
