package ggc;

import ggc.exceptions.BadEntryException;
import ggc.exceptions.DuplicatePartnerKeyException;
import ggc.exceptions.DuplicateProductKeyException;
import ggc.exceptions.IllegalEntryException;
import ggc.exceptions.InvalidDateException;
import ggc.exceptions.UnknownPartnerKeyException;
import ggc.exceptions.UnknownProductKeyException;
import ggc.partners.Partner;
import ggc.products.Batch;
import ggc.products.DerivedProduct;
import ggc.products.Product;
import ggc.products.Recipe;
import ggc.products.RecipeProduct;
import ggc.util.NaturalTextComparator;

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
   * The date, in days, relative to the creation of the warehouse.
   */
  private int date = 0;

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
   * Get whether the warehouse has been modified since it was last cleaned.
   * The warehouse is cleaned when it is saved to disk.
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
            .flatMap(product -> product.getBatches().stream()).sorted()
            .collect(Collectors.toList());
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
      product.registerBatch(Integer.parseInt(fields[4]),
              Double.parseDouble(fields[3]), partner);
      this.dirty();
    } catch (UnknownPartnerKeyException | NumberFormatException
            | DuplicateProductKeyException e) {
      throw new IllegalEntryException(fields);
    }
  }

  /**
   * Parse and import a multi batch entry from a plain text file. Also imports
   * the associated product if it does not exist.
   * <p>
   * A correct multi batch entry has the following format:
   * {@code BATCH_M|product-id|partner-id|price|current-stock|aggravating-factor|component-1:quantity-1#...#component-n:quantity-n}
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
      product.registerBatch(Integer.parseInt(fields[4]),
              Double.parseDouble(fields[3]), partner);
      this.dirty();
    } catch (UnknownPartnerKeyException | NumberFormatException
            | DuplicateProductKeyException | UnknownProductKeyException e) {
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
   *                            the format
   *                            {@code component-1:quantity-1#...#component-n:quantity-n}
   * @return The {@link Recipe} created from the given parameters
   * @throws NumberFormatException      if the aggravating factor or one of the
   *                                    product quantities is not a number
   * @throws UnknownProductKeyException if the recipe contains any product that
   *                                    does not exist
   */

  private Recipe importRecipe(String aggravatingFactor,
                              String productsDescription)
          throws NumberFormatException, UnknownProductKeyException {
    List<RecipeProduct> products = new ArrayList<>();
    String[] productDescriptors = productsDescription.split("#");
    for (String desc : productDescriptors) {
      String[] fields = desc.split(":");
      String prodKey = fields[0];
      int quantity = Integer.parseInt(fields[1]);
      Product prod;
      prod = this.getProduct(prodKey);
      products.add(new RecipeProduct(quantity, prod));
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
    this.products.put(id, p);
    this.dirty();
    return p;
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
    this.products.put(id, p);
    this.dirty();
    return p;
  }

}
