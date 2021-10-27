package ggc.products;

import ggc.exceptions.OutOfStockException;
import ggc.partners.Partner;
import ggc.util.NaturalTextComparator;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.BinaryOperator;

public class Product implements Comparable<Product>, Serializable {
  /**
   * Serial number for serialization.
   */
  @Serial
  private static final long serialVersionUID = 202110221420L;

  private final Comparator<String> idComparator = new NaturalTextComparator();

  private final String id;
  private final List<Batch> batches = new ArrayList<>();
  private final Set<Partner> subscribers = new HashSet<>();

  public Product(String id) {
    // TODO subscribe all existing
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public List<Batch> getBatches() {
    return batches;
  }

  public Batch registerBatch(int quantity, double price, Partner partner) {
    Batch b = new Batch(quantity, price, this, partner);
    this.batches.add(b);
    return b;
  }

  public int getQuantityInBatches() {
    return this.batches.stream().map(Batch::getQuantity)
            .reduce(Integer::sum).orElse(0);
  }

  public int getTotalQuantity() {
    return this.getQuantityInBatches();
  }

  public Collection<Batch> getBatchesForAcquisition(int quantity) {
    this.ensureBatchesSorted();
    List<Batch> batchesForAcquisition = new ArrayList<>();
    int addedQuantity = 0;
    Iterator<Batch> iterator = this.batches.iterator();

    while (addedQuantity < quantity && iterator.hasNext()) {
      Batch batch = iterator.next();
      batchesForAcquisition.add(batch);
      addedQuantity += batch.getQuantity();
    }

    return batchesForAcquisition;
  }

  public double getCheapestPrice() throws OutOfStockException {
    ensureBatchesSorted();
    if (batches.size() == 0)
      throw new OutOfStockException();
    return batches.get(0).getPrice();
  }

  public double getMostExpensivePrice() {
    // TODO get all time most expensive price (from transactions)
    // TODO is it all time or from existing batches?
    // TODO assuming existing batches right now
    return this.batches.stream().map(Batch::getPrice)
            .reduce(BinaryOperator.maxBy(Double::compareTo)).orElse(0D);
  }

  public void subscribe(Partner partner) {
    this.subscribers.add(partner);
  }

  public void unsubscribe(Partner partner) {
    this.subscribers.remove(partner);
  }

  public boolean isSubscribed(Partner partner) {
    return this.subscribers.contains(partner);
  }

  public void toggleSubscription(Partner partner) {
    if (this.isSubscribed(partner)) {
      this.unsubscribe(partner);
    } else {
      this.subscribe(partner);
    }
  }

  private void ensureBatchesSorted() {
    this.batches.sort(Comparator.comparingDouble(Batch::getPrice)
            .thenComparingInt(Batch::getQuantity));
  }

  @Override
  public int compareTo(Product product) {
    return idComparator.compare(this.getId(), product.getId());
  }

  @Override
  public String toString() {
    return new StringJoiner("|")
            .add(this.getId())
            .add(Long.toString(Math.round(this.getMostExpensivePrice())))
            .add(Integer.toString(this.getQuantityInBatches()))
            .toString();
  }
}
