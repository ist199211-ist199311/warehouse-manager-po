package ggc.products;

import ggc.exceptions.OutOfStockException;
import ggc.exceptions.UnavailableProductException;
import ggc.partners.Partner;
import ggc.transactions.AcquisitionTransaction;
import ggc.transactions.BreakdownTransaction;
import ggc.transactions.SaleTransaction;
import ggc.util.BatchPriceComparator;
import ggc.util.NaturalTextComparator;
import ggc.util.Visitable;
import ggc.util.Visitor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Product implements Comparable<Product>, Serializable, Visitable {
  /**
   * Serial number for serialization.
   */
  @Serial
  private static final long serialVersionUID = 202110221420L;

  private final Comparator<String> idComparator = new NaturalTextComparator();
  private final Comparator<Batch> batchComparator = new BatchPriceComparator();

  private final String id;
  private final Map<String, Set<Batch>> batchesByPartner = new TreeMap<>(
      idComparator);
  private final PriorityQueue<Batch> batches = new PriorityQueue<>(
      batchComparator);
  private final Set<Partner> subscribers = new HashSet<>();

  public Product(String id) {
    // TODO subscribe all existing
    this.id = id;
  }

  public String getId() {
    return this.id;
  }

  public Stream<Batch> getBatches() {
    return this.batchesByPartner.values()
        .stream()
        .flatMap(Collection::stream);
  }

  /**
   * Creates a new batch for this product with the given parameters. Stores the
   * batch in this product.
   *
   * @param quantity the quantity in the batch
   * @param price    the price of the batch
   * @param partner  the partner supplying the batch
   * @return the batch that was just created
   */
  public Batch registerBatch(int quantity, double price, Partner partner) {
    Batch batch = new Batch(quantity, price, this, partner);
    this.insertBatch(batch);
    return batch;
  }

  private void insertBatch(Batch batch) {
    this.batches.add(batch);
    this.batchesByPartner.computeIfAbsent(batch.partner().getId(),
        (v) -> new TreeSet<>()).add(batch);
  }

  private void removeBatchFromPartnerMap(Batch batch) {
    Optional.ofNullable(this.batchesByPartner.get(batch.partner().getId()))
        .ifPresent(set -> set.remove(batch));
  }

  /**
   * @return the available quantity in batches
   */
  public int getQuantityInBatches() {
    return this.batches.stream()
        .map(Batch::quantity)
        .reduce(Integer::sum)
        .orElse(0);
  }

  public int getTotalQuantity() {
    return this.getQuantityInBatches();
  }

  /**
   * Calculate whether this product is presently available, directly OR
   * INDIRECTLY (if applicable).
   * 
   * @throws UnavailableProductException if there is not enough of this product
   */
  public void assertAvailable(int quantity) throws UnavailableProductException {
    final int available = this.getQuantityInBatches();
    if (available < quantity) {
      throw new UnavailableProductException(this.getId(), quantity, available);
    }
  }

  /**
   * Calculates the batches needed to reach to sell a given quantity of this
   * product, minimizing the cost, that is, choosing the cheaper batches first.
   * It is guaranteed that the total quantity in the returned batches is equal
   * to the quantity parameter.
   *
   * @param quantity the quantity of product to sell
   * @return the batches required for sale
   * @throws UnavailableProductException if there is not enough stock to satisfy
   *                                     the request
   */
  public Collection<Batch> getBatchesForSale(int quantity)
      throws UnavailableProductException {
    // TODO maybe make batches immutable (?)
    final int available = this.getQuantityInBatches();
    if (available < quantity)
      throw new UnavailableProductException(this.getId(), quantity, available);

    List<Batch> batchesForSale = new LinkedList<>();
    int addedQuantity = 0;

    Batch batch;
    while (addedQuantity < quantity && (batch = this.batches.poll()) != null) {
      this.removeBatchFromPartnerMap(batch);

      if (batch.quantity() + addedQuantity > quantity) {
        int neededQuantity = quantity - addedQuantity;
        Batch leftoverBatch = batch
            .cloneWithQuantity(batch.quantity() - neededQuantity);
        batch = batch.cloneWithQuantity(neededQuantity);
        this.insertBatch(leftoverBatch);
      }

      batchesForSale.add(batch);
      addedQuantity += batch.quantity();
    }

    return batchesForSale;
  }

  /**
   * @return the price of the cheapest batch
   * @throws OutOfStockException if there are no batches for this product (i.e.
   *                             no product in stock)
   */
  public double getCheapestPrice() throws OutOfStockException {
    if (this.batches.size() == 0)
      throw new OutOfStockException(this);
    return this.batches.peek().price();
  }

  /**
   * Calculate the all-time highest price for this product from previous
   * transactions or current batches.
   *
   * @return the all-time highest price for this product
   */
  public double getMostExpensivePrice() {
    // TODO get all time most expensive price (from transactions)
    return this.batches.stream()
        .map(Batch::price)
        .reduce(BinaryOperator.maxBy(Double::compareTo))
        .orElse(0D);
  }

  /**
   * Get the price of the product when adding a batch creating from a derived
   * product breakdown.
   *
   * @return the price of the batch
   */
  public double getPriceForBreakdown() {
    try {
      return this.getCheapestPrice();
    } catch (OutOfStockException e) {
      return this.getMostExpensivePrice();
    }
  }

  public void acquire(int date, Partner partner, int quantity, double price,
      Supplier<Integer> idSupplier,
      Consumer<AcquisitionTransaction> saveAcquisitionTransaction) {
    final Batch batch = this.registerBatch(quantity, price, partner);
    saveAcquisitionTransaction.accept(new AcquisitionTransaction(
        idSupplier.get(),
        date,
        batch));
  }

  public void sell(int paymentDeadline, Partner partner, int quantity,
      Supplier<Integer> idSupplier,
      Consumer<SaleTransaction> saveSaleTransaction)
      throws UnavailableProductException {
    this.assertAvailable(quantity);
    final Collection<Batch> batchesForSale = this.getBatchesForSale(quantity);
    saveSaleTransaction.accept(new SaleTransaction(idSupplier.get(),
        batchesForSale.stream()
            .map(Batch::price)
            .reduce(Double::sum)
            .orElse(0D),
        quantity,
        this,
        partner,
        paymentDeadline));
  }

  public void breakdown(int date, Partner partner, int quantity,
      Supplier<Integer> idSupplier,
      Consumer<BreakdownTransaction> saveBreakdownTransaction)
      throws UnavailableProductException {
    final int available = this.getTotalQuantity();
    if (available < quantity) {
      throw new UnavailableProductException(this.getId(), quantity, available);
    }
    // do nothing for simple products
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

  @Override
  public int compareTo(Product product) {
    return this.idComparator.compare(this.getId(), product.getId());
  }

  @Override
  public <T> T accept(Visitor<T> visitor) {
    return visitor.visit(this);
  }
}
