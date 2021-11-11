package ggc;

import ggc.exceptions.BadEntryException;
import ggc.exceptions.DuplicatePartnerKeyException;
import ggc.exceptions.DuplicateProductKeyException;
import ggc.exceptions.IllegalEntryException;
import ggc.exceptions.InvalidDateException;
import ggc.exceptions.UnavailableProductException;
import ggc.exceptions.UnknownPartnerKeyException;
import ggc.exceptions.UnknownProductKeyException;
import ggc.exceptions.UnknownTransactionKeyException;
import ggc.notifications.Notification;
import ggc.partners.Partner;
import ggc.products.Batch;
import ggc.products.DerivedProduct;
import ggc.products.Product;
import ggc.products.Recipe;
import ggc.products.RecipeComponent;
import ggc.transactions.AcquisitionTransaction;
import ggc.transactions.SaleTransaction;
import ggc.transactions.Transaction;
import ggc.util.AcquisitionTransactionFilter;
import ggc.util.NaturalTextComparator;
import ggc.util.SaleAndBreakdownTransactionFilter;
import ggc.util.Visitor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Class Warehouse implements a warehouse.
 */
public class Warehouse implements Serializable {

  /**
   * Serial number for serialization.
   */
  @Serial
  private static final long serialVersionUID = 202109192006L;

  /**
   * Filter for {@link AcquisitionTransaction}
   */
  private final Visitor<Boolean> acquisitionTransactionFilter = new AcquisitionTransactionFilter();

  /**
   * Filter for {@link ggc.transactions.SaleTransaction} or
   * {@link ggc.transactions.BreakdownTransaction}
   */
  private final Visitor<Boolean> saleAndBreakdownTransactionFilter = new SaleAndBreakdownTransactionFilter();

  /**
   * Stores the warehouse's products, sorted by their key.
   */
  private final Map<String, Product> products = new TreeMap<>(
      new NaturalTextComparator());

  /**
   * Stores the warehouse's partners, sorted by their key.
   */
  private final Map<String, Partner> partners = new TreeMap<>(
      new NaturalTextComparator());

  /**
   * Stores the warehouse's transactions.
   */
  private final Map<Integer, Transaction> transactions = new TreeMap<>();

  /**
   * Stores the transaction ID to be assigned on the next created transaction.
   */
  private int nextTransactionId = 0;

  /**
   * The date, in days, relative to the creation of the warehouse.
   */
  private int date = 0;

  /**
   * The available balance, that is, the balance that can be spent
   */
  private double availableBalance = 0;

  /**
   * Whether the warehouse is in a dirty state, that is, if it was modified
   * since the last time it was saved (or created).
   */
  private boolean dirty = false;

  /**
   * Get the current date in days, relative to the creation of the warehouse.
   *
   * @return Days since warehouse creation
   */
  public int displayDate() {
    return this.date;
  }

  /**
   * Advance the warehouse date by the given days.
   *
   * @param days The number of days to advance the date for
   * @throws InvalidDateException when {@code days} is a non-positive number
   */
  public void advanceDate(int days) throws InvalidDateException {
    if (days <= 0) {
      throw new InvalidDateException(days);
    }
    this.date += days;
    this.dirty();
  }

  /**
   * Get whether the warehouse has been modified since it was last cleaned. The
   * warehouse is cleaned when it is saved to disk.
   *
   * @return the value of the dirty flag
   */
  public boolean isDirty() {
    return this.dirty;
  }

  /**
   * Turn the dirty flag off to indicate the warehouse state has been saved.
   */
  public void clean() {
    this.dirty = false;
  }

  /**
   * Turn the dirty flag on to indicate a modification has occurred since last
   * clean-up (i.e. saved).
   */
  private void dirty() {
    this.dirty = true;
  }

  /**
   * Get all products known to the warehouse, simple or derived, sorted by their
   * case-insensitive key, that is, their key ignoring case.
   *
   * @return A sorted {@link Collection} of products
   */
  public Collection<Product> getAllProducts() {
    return this.products.values();
  }

  /**
   * Get all partners known to the warehouse, sorted by their case-insensitive
   * key, that is, their key ignoring case.
   *
   * @return A sorted {@link Collection} of partners
   */
  public Collection<Partner> getAllPartners() {
    return this.partners.values();
  }

  /**
   * Get all batches available to the warehouse
   *
   * @return A sorted {@link Collection} of batches
   */
  public Collection<Batch> getAllBatches() {
    return this.products.values().stream()
        .flatMap(Product::getBatches)
        .collect(Collectors.toList());
  }

  /**
   * Get all batches by the given partner available to the warehouse
   *
   * @param partnerId The key of the partner that owns the batches
   * @return A sorted {@link Collection} of the partner's batches
   * @throws UnknownPartnerKeyException if the given partner is unknown
   */
  public Collection<Batch> getBatchesByPartner(String partnerId)
      throws UnknownPartnerKeyException {
    final Partner partner = getPartner(partnerId);

    return this.products.values().stream()
        .flatMap(Product::getBatches)
        .filter(batch -> partner.equals(batch.partner()))
        .collect(Collectors.toList());
  }

  /**
   * Get all batches of the given product available to the warehouse
   *
   * @param productId The key of the product whose batches belong to
   * @return A sorted {@link Collection} of the products's batches
   * @throws UnknownProductKeyException if the given product is unknown
   */
  public Collection<Batch> getBatchesByProduct(String productId)
      throws UnknownProductKeyException {
    final Product product = getProduct(productId);

    return product.getBatches().collect(Collectors.toList());
  }

  /**
   * Get a partner by its key. Two partners are the same if their keys are the
   * same (or only differ by case).
   *
   * @param key The key of the partner to get
   * @return The {@link Partner} associated with the given key
   * @throws UnknownPartnerKeyException if there is no {@link Partner} with the
   *                                    given key
   */
  public Partner getPartner(String key) throws UnknownPartnerKeyException {
    Partner p = this.partners.get(key);
    if (p == null) {
      throw new UnknownPartnerKeyException(key);
    }
    return p;
  }

  /**
   * Get a product by its key. Two products are the same if their keys are the
   * same (or only differ by case).
   *
   * @param key The key of the product to get
   * @return The {@link Product} associated with the given key
   * @throws UnknownProductKeyException if there is no {@link Product} with the
   *                                    given key
   */
  public Product getProduct(String key) throws UnknownProductKeyException {
    Product p = this.products.get(key);
    if (p == null) {
      throw new UnknownProductKeyException(key);
    }
    return p;
  }

  /**
   * Get a transaction by its ID. Two transactions are the same if numeric IDs
   * are the same.
   *
   * @param id The ID of the product to get
   * @return The {@link Transaction} with the given ID
   * @throws UnknownTransactionKeyException if there is no {@link Transaction}
   *                                        with the given ID
   */
  public Transaction getTransaction(int id)
      throws UnknownTransactionKeyException {
    Transaction t = this.transactions.get(id);
    if (t == null) {
      throw new UnknownTransactionKeyException(id);
    }
    return t;
  }

  /**
   * Import data (partners, batches, products) from a plain text file.
   *
   * @param textFile The name of the file to be loaded
   * @throws IOException           if any sort of IO error occurs, such as the
   *                               file not existing, or is a directory
   * @throws BadEntryException     if an entry (line) in the file has an unknown
   *                               type
   * @throws IllegalEntryException if an entry (line) in the file is not
   *                               correctly formatted for its type
   */
  void importFile(String textFile)
      throws IOException, BadEntryException, IllegalEntryException {
    try (BufferedReader s = new BufferedReader(new FileReader(textFile))) {
      String line;
      while ((line = s.readLine()) != null) {
        importFromFields(line.split("\\|"));
      }
    }
  }

  /**
   * Parse and import an entry (line) from a plain text file.
   *
   * @param fields The fields of the entry to import, that were split by the
   *               separator
   * @throws BadEntryException     if the entry type is unknown and not
   *                               supported by the program
   * @throws IllegalEntryException if the entry does not have the correct fields
   *                               for its type
   */
  private void importFromFields(String[] fields)
      throws BadEntryException, IllegalEntryException {
    switch (fields[0]) {
      case "PARTNER" -> this.importPartner(fields);
      case "BATCH_S" -> this.importSimpleBatch(fields);
      case "BATCH_M" -> this.importMultiBatch(fields);
      default -> throw new BadEntryException(String.join("|", fields));
    }
  }

  /**
   * Parse and import a partner entry from a plain text file.
   * <p>
   * A correct partner entry has the following format:
   * {@code PARTNER|id|name|address}
   *
   * @param fields The fields of the partner to import, that were split by the
   *               separator
   * @throws IllegalEntryException if the entry does not have the correct fields
   *                               for its type
   */
  private void importPartner(String[] fields) throws IllegalEntryException {
    if (fields.length != 4) {
      throw new IllegalEntryException(fields);
    }
    try {
      this.registerPartner(fields[1], fields[2], fields[3]);
    } catch (DuplicatePartnerKeyException e) {
      throw new IllegalEntryException(fields);
    }
  }

  /**
   * Parse and import a simple batch entry from a plain text file. Also imports
   * the associated product if it does not exist.
   * <p>
   * A correct simple batch entry has the following format:
   * {@code BATCH_S|product-id|partner-id|price|current-stock}
   *
   * @param fields The fields of the simple batch to import, that were split by
   *               the separator
   * @throws IllegalEntryException if the entry does not have the correct fields
   *                               for its type
   */
  private void importSimpleBatch(String[] fields) throws IllegalEntryException {
    if (fields.length != 5) {
      throw new IllegalEntryException(fields);
    }
    try {
      Partner partner = this.getPartner(fields[2]);
      Product product;
      try {
        product = this.getProduct(fields[1]);
      } catch (UnknownProductKeyException e) {
        product = this.registerSimpleProduct(fields[1]);
      }
      product.registerBatch(
          Integer.parseInt(fields[4]),
          Double.parseDouble(fields[3]),
          partner);
      this.dirty();
    } catch (UnknownPartnerKeyException
        | NumberFormatException
        | DuplicateProductKeyException e) {
      throw new IllegalEntryException(fields);
    }
  }

  /**
   * Parse and import a multi batch entry from a plain text file. Also imports
   * the associated product if it does not exist.
   * <p>
   * A correct multi batch entry has the following format:
   * {@code BATCH_M|product-id|partner-id|price|current-stock|aggravating
   * -factor|component-1:quantity-1#...#component-n:quantity-n}
   *
   * @param fields The fields of the multi batch to import, that were split by
   *               the separator
   * @throws IllegalEntryException if the entry does not have the correct fields
   *                               for its type
   */
  private void importMultiBatch(String[] fields) throws IllegalEntryException {
    if (fields.length != 7) {
      throw new IllegalEntryException(fields);
    }
    try {
      Partner partner = this.getPartner(fields[2]);
      Product product;
      try {
        product = this.getProduct(fields[1]);
      } catch (UnknownProductKeyException e) {
        Recipe recipe = this.importRecipe(fields[5], fields[6]);
        product = this.registerDerivedProduct(fields[1], recipe);
      }
      product.registerBatch(
          Integer.parseInt(fields[4]),
          Double.parseDouble(fields[3]),
          partner);
      this.dirty();
    } catch (UnknownPartnerKeyException
        | NumberFormatException
        | DuplicateProductKeyException
        | UnknownProductKeyException e) {
      throw new IllegalEntryException(fields);
    }
  }

  /**
   * Parse a recipe from its plain text format, to be used when importing a
   * batch of a derived product. If any of the products referenced by the recipe
   * don't exist, a simple product is created in its place.
   *
   * @param aggravatingFactor   The aggravating factor of the recipe, which will
   *                            be converted to a double
   * @param productsDescription The plain text format of the recipe products, in
   *                            the format {@code component-1:quantity-1#...
   *                            #component-n:quantity-n}
   * @return The {@link Recipe} created from the given parameters
   * @throws NumberFormatException      if the aggravating factor or one of the
   *                                    product quantities is not a number
   * @throws UnknownProductKeyException if the recipe contains any product that
   *                                    does not exist
   */

  private Recipe importRecipe(String aggravatingFactor,
      String productsDescription)
      throws NumberFormatException, UnknownProductKeyException {
    List<RecipeComponent> products = new ArrayList<>();
    String[] productDescriptors = productsDescription.split("#");
    for (String desc : productDescriptors) {
      String[] fields = desc.split(":");
      String prodKey = fields[0];
      int quantity = Integer.parseInt(fields[1]);
      Product prod;
      prod = this.getProduct(prodKey);
      products.add(new RecipeComponent(quantity, prod));
    }
    return new Recipe(Double.parseDouble(aggravatingFactor), products);
  }

  /**
   * Register a new partner in this warehouse, which will be created from the
   * given parameters.
   *
   * @param id      The key of the partner
   * @param name    The name of the partner
   * @param address The address of the partner
   * @return The {@link Partner} that was just created
   * @throws DuplicatePartnerKeyException if a partner with the given key
   *                                      (case-insensitive) already exists
   */
  public Partner registerPartner(String id, String name, String address)
      throws DuplicatePartnerKeyException {
    if (this.partners.containsKey(id)) {
      throw new DuplicatePartnerKeyException(id);
    }

    Partner p = new Partner(id, name, address);
    this.partners.put(id, p);
    this.dirty();
    return p;
  }

  /**
   * Register a new simple product in this warehouse, which will be created from
   * the given key.
   *
   * @param id The key of the product
   * @return The {@link Product} that was just created
   * @throws DuplicateProductKeyException if a product with the given key
   *                                      (case-insensitive) already exists
   */
  public Product registerSimpleProduct(String id)
      throws DuplicateProductKeyException {
    if (this.products.containsKey(id)) {
      throw new DuplicateProductKeyException(id);
    }

    Product p = new Product(id);
    this.partners.values().forEach(p::subscribe);
    this.products.put(id, p);
    this.dirty();
    return p;
  }

  /**
   * Creates a new derived product with the given key and with a recipe built
   * from the given parameters. The arrays <code>recipeProducts</code> and
   * <code>recipeQuantities</code> must have the same size.
   *
   * @param id                The key of the product
   * @param aggravatingFactor The aggravating factor of the recipe
   * @param recipeProducts    The key of the products to include in the recipe
   * @param recipeQuantities  The quantities, matching the position in the
   *                          products array, of the products
   * @return The {@link DerivedProduct} that was just created
   * @throws UnknownProductKeyException   if a product in the recipe does not
   *                                      exist
   * @throws DuplicateProductKeyException if a product with the given key
   *                                      (case-insensitive) already exists
   * @throws IllegalArgumentException     if recipeProducts and recipeQuantities
   *                                      don't have the same non-zero length
   * @see Warehouse#registerDerivedProduct(String, Recipe)
   * @see Warehouse#buildRecipe(double, String[], int[])
   */
  public DerivedProduct registerDerivedProduct(String id,
      double aggravatingFactor,
      String[] recipeProducts,
      int[] recipeQuantities)
      throws UnknownProductKeyException, DuplicateProductKeyException {
    final Recipe recipe = buildRecipe(
        aggravatingFactor,
        recipeProducts,
        recipeQuantities);
    return registerDerivedProduct(id, recipe);
  }

  /**
   * Builds a recipe from the given aggravating factor and product quantity
   * pairs. The arrays <code>recipeProducts</code> and
   * <code>recipeQuantities</code> must have the same size.
   *
   * @param aggravatingFactor The aggravating factor of the recipe
   * @param recipeProducts    The key of the products to include in the recipe
   * @param recipeQuantities  The quantities, matching the position in the
   *                          products array, of the products
   * @return The {@link Recipe} that was just created from the given arguments
   * @throws UnknownProductKeyException if a product in the recipe does not
   *                                    exist
   * @throws IllegalArgumentException   if recipeProducts and recipeQuantities
   *                                    don't have the same non-zero length
   */
  private Recipe buildRecipe(double aggravatingFactor,
      String[] recipeProducts, int[] recipeQuantities)
      throws UnknownProductKeyException {
    if (recipeProducts.length != recipeQuantities.length
        || recipeProducts.length == 0) {
      // TODO maybe use a custom exception (?) this should never happen
      // anyway tho if the interface is used correctly
      throw new IllegalArgumentException("expected recipeProducts and " +
          "recipeQuantities to have the same non-zero length");
    }

    final List<RecipeComponent> components = new ArrayList<>();
    for (int i = 0; i < recipeProducts.length; ++i) {
      final Product product = getProduct(recipeProducts[i]);
      final int quantity = recipeQuantities[i];
      final RecipeComponent component = new RecipeComponent(quantity, product);
      components.add(component);
    }

    return new Recipe(aggravatingFactor, components);
  }

  /**
   * Register a new derived product in this warehouse, which will be created
   * from the given parameters.
   *
   * @param id     The key of the product
   * @param recipe The {@link Recipe} of the derived product
   * @return The {@link DerivedProduct} that was just created
   * @throws DuplicateProductKeyException if a product with the given key
   *                                      (case-insensitive) already exists
   */
  public DerivedProduct registerDerivedProduct(String id, Recipe recipe)
      throws DuplicateProductKeyException {
    if (this.products.containsKey(id)) {
      throw new DuplicateProductKeyException(id);
    }

    DerivedProduct p = new DerivedProduct(id, recipe);
    this.partners.values().forEach(p::subscribe);
    this.products.put(id, p);
    this.dirty();
    return p;
  }

  /**
   * Lookup all the batches under a given price, ordered by their natural order.
   *
   * @param priceLimit the upper price limit, that is, all batches must have a
   *                   price lower than this
   * @return a sorted collection of batches with a lower price than the given
   *         price
   */
  public Collection<Batch> lookupProductBatchesUnderGivenPrice(
      double priceLimit) {
    return this.getAllBatches().stream()
        .filter(batch -> batch.price() < priceLimit)
        .collect(Collectors.toList());
  }

  /**
   * Get the balance available to spend.
   *
   * @return the available balance
   */
  public double getAvailableBalance() {
    return this.availableBalance;
  }

  /**
   * Calculate the accounting balance, that is, the sum of the available balance
   * with the cost of the pending sale transactions.
   *
   * @return the accounting balance
   */
  public double getAccountingBalance() {
    // TODO sum with pending sale transactions
    return this.getAvailableBalance();
  }

  /**
   * Destructively get the next transaction ID and increase the value. Each call
   * to this method will give a different value.
   *
   * @return the next transaction ID
   */
  private int getNextTransactionId() {
    return this.nextTransactionId++;
  }

  /**
   * Register an acquisition transaction, creating an associated batch
   * automatically and updating the warehouse's available balance.
   *
   * @param partnerId The key of the partner to acquire the product from
   * @param productId The key of the product to be acquired
   * @param value     The unitary price to acquire at
   * @param quantity  The product quantity to acquire
   * @throws UnknownPartnerKeyException if the given partner does not exist
   * @throws UnknownProductKeyException if the given product does not exist
   */
  public void registerAcquisitionTransaction(String partnerId,
      String productId, double value, int quantity)
      throws UnknownPartnerKeyException, UnknownProductKeyException {
    final Partner partner = this.getPartner(partnerId);
    final Product product = this.getProduct(productId);

    AcquisitionTransaction transaction = product.acquire(date, partner,
        quantity, value, this::getNextTransactionId);
    this.transactions.put(transaction.getId(), transaction);
    this.availableBalance -= transaction.totalValue();
    partner.increaseAcquisitionsValue(transaction.totalValue());
  }

  public void registerSaleTransaction(String partnerId, String productId,
      int paymentDeadline, int quantity)
      throws UnknownPartnerKeyException, UnknownProductKeyException,
      UnavailableProductException {
    final Partner partner = this.getPartner(partnerId);
    final Product product = this.getProduct(productId);

    SaleTransaction transaction = product.sell(paymentDeadline, partner,
        quantity, this::getNextTransactionId);
    this.transactions.put(transaction.getId(), transaction);
    partner.increaseSalesValue(transaction.baseValue());
  }

  public void registerBreakdownTransaction(String partnerId, String productId,
      int quantity)
      throws UnknownPartnerKeyException, UnknownProductKeyException,
      UnavailableProductException {
    final Partner partner = this.getPartner(partnerId);
    final Product product = this.getProduct(productId);

    product.breakdown(
        this.date,
        partner,
        quantity,
        this::getNextTransactionId).ifPresent(transaction -> {
          transactions.put(transaction.getId(), transaction);
          this.availableBalance += Math.max(0, transaction.baseValue());
        });
  }

  /**
   * Get all acquisition transactions from a partner given its key.
   *
   * @param partnerId The key of the partner
   * @return A collection of acquisition transactions
   * @throws UnknownPartnerKeyException if a partner with the given key does not
   *                                    exist
   */
  public Collection<Transaction> getPartnerAcquisitions(String partnerId)
      throws UnknownPartnerKeyException {
    final Partner partner = this.getPartner(partnerId);

    return this.transactions.values()
        .stream()
        .filter(t -> partner.equals(t.getPartner()))
        .filter(t -> t.accept(acquisitionTransactionFilter))
        .collect(Collectors.toList());
  }

  /**
   * Get all sale and breakdown transactions from a partner given its key.
   *
   * @param partnerId The key of the partner
   * @return A collection of sale and breakdown transactions
   * @throws UnknownPartnerKeyException if a partner with the given key does not
   *                                    exist
   */
  public Collection<Transaction> getPartnerSalesAndBreakdowns(String partnerId)
      throws UnknownPartnerKeyException {
    final Partner partner = this.getPartner(partnerId);

    return this.transactions.values()
        .stream()
        .filter(t -> partner.equals(t.getPartner()))
        .filter(t -> t.accept(saleAndBreakdownTransactionFilter))
        .collect(Collectors.toList());
  }

  /**
   * Registers payment for a sale, updating the Warehouse's balance. Paying for
   * transactions other than sales has no effect.
   * 
   * @param transactionId The key of the transaction
   * @throws UnknownTransactionKeyException if no such transaction is found
   */
  public void receivePayment(int transactionId)
      throws UnknownTransactionKeyException {
    // TODO
  }

  /**
   * Gets a list of the in-app notifications for a given partner, marking them
   * as read.
   *
   * @param partnerId The key of the partner
   * @return A collection of the unread in-app notifications
   * @throws UnknownPartnerKeyException if a partner with the given key does not
   *                                    exist
   */
  public Collection<Notification> readPartnerInAppNotifications(
      String partnerId)
      throws UnknownPartnerKeyException {
    final Partner partner = this.getPartner(partnerId);

    return partner.readInAppNotifications();
  }

  /**
   * Toggles the subscription of notifications for the given partner on the
   * given product.
   *
   * @param partnerId The key of the partner
   * @param productId The key of the product
   * @throws UnknownPartnerKeyException if a partner with the given key does not
   *                                    exist
   * @throws UnknownProductKeyException if a product with the given key does not
   *                                    exist
   */
  public void toggleProductNotificationsForPartner(String partnerId,
      String productId)
      throws UnknownPartnerKeyException, UnknownProductKeyException {
    final Partner partner = this.getPartner(partnerId);
    final Product product = this.getProduct(productId);

    product.toggleSubscription(partner);
  }

}
