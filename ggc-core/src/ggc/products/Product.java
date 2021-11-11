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
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Product implements Comparable<Product>, Serializable, Visitable {
  /**
   * Serial number for serialization.
   */
  @Serial
  private static final long serialVersionUID = 202110221420L;

  private static final Comparator<String> ID_COMPARATOR = new NaturalTextComparator();
  private static final Comparator<Batch> BATCH_COMPARATOR = new BatchPriceComparator();

  private final String id;
  private final Map<String, Collection<Batch>> batchesByPartner = new TreeMap<>(
      ID_COMPARATOR);
  private final PriorityQueue<Batch> batches = new PriorityQueue<>(
      BATCH_COMPARATOR);
  private final Set<Partner> subscribers = new HashSet<>();
  private double allTimeMaxPrice = 0D;

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
        .flatMap(Collection::stream)
        .sorted();
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
    this.allTimeMaxPrice = Math.max(this.allTimeMaxPrice, batch.price());
    this.batches.add(batch);
    this.batchesByPartner.computeIfAbsent(batch.partner().getId(),
        (v) -> new PriorityQueue<>(BATCH_COMPARATOR)).add(batch);
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
   * Calculates whether this product is presently available, directly OR
   * INDIRECTLY (if applicable).
   *
   * @throws UnavailableProductException if there is not enough of this product
   */
  public void assertPossibleAvailability(int quantity)
      throws UnavailableProductException {
    final int available = this.getQuantityInBatches();
    if (available < quantity) {
      throw new UnavailableProductException(this.getId(), quantity, available);
    }
  }

  /**
   * Tries to guarantee real availability in product stock of the given
   * quantity. Note that it is impossible for this to accomplish anything for
   * simple products.
   * 
   * @throws UnavailableProductException if unsucessful
   */
  public void ensureAvailableInBatches(int quantity, Partner partner)
      throws UnavailableProductException {
    this.assertPossibleAvailability(quantity);
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
  public Collection<Batch> pollBatchesForSale(int quantity)
      throws UnavailableProductException {
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
  public double getAllTimeMaxPrice() {
    return this.allTimeMaxPrice;
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
      return this.getAllTimeMaxPrice();
    }
  }

  public AcquisitionTransaction acquire(int date, Partner partner, int quantity,
      double price,
      Supplier<Integer> idSupplier) {
    final Batch batch = this.registerBatch(quantity, price, partner);
    return new AcquisitionTransaction(
        idSupplier.get(),
        date,
        batch);
  }

  public SaleTransaction sell(int paymentDeadline, Partner partner,
      int quantity, Supplier<Integer> idSupplier)
      throws UnavailableProductException {
    this.assertPossibleAvailability(quantity);
    this.ensureAvailableInBatches(quantity, partner);
    final Collection<Batch> batchesForSale = this.pollBatchesForSale(quantity);
    final double saleValue = batchesForSale.stream()
        .map(Batch::totalPrice)
        .reduce(Double::sum)
        .orElse(0D);
    return new SaleTransaction(idSupplier.get(),
        saleValue,
        quantity,
        this,
        partner,
        paymentDeadline);
  }

  public Optional<BreakdownTransaction> breakdown(int date, Partner partner,
      int quantity,
      Supplier<Integer> idSupplier)
      throws UnavailableProductException {
    final int available = this.getTotalQuantity();
    if (available < quantity) {
      throw new UnavailableProductException(this.getId(), quantity, available);
    }
    // do nothing for simple products
    return Optional.empty();
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
    return ID_COMPARATOR.compare(this.getId(), product.getId());
  }

  @Override
  public <T> T accept(Visitor<T> visitor) {
    return visitor.visit(this);
  }
}
