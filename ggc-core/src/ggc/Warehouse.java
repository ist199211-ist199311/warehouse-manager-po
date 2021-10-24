package ggc;

import ggc.exceptions.BadEntryException;
import ggc.exceptions.IllegalEntryException;
import ggc.exceptions.InvalidDateException;
import ggc.exceptions.UnknownPartnerKeyException;
import ggc.exceptions.UnknownProductKeyException;
import ggc.partners.Partner;
import ggc.products.DerivedProduct;
import ggc.products.Product;
import ggc.products.Recipe;
import ggc.products.RecipeProduct;

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

  private int date = 0;
  private final Map<String, Product> products = new TreeMap<>();
  private final Map<String, Partner> partners = new TreeMap<>();

  // FIXME define attributes
  // FIXME define constructor(s)
  // FIXME define methods

  public int displayDate() {
    return this.date;
  }

  public void advanceDate(int days) throws InvalidDateException {
    if (days <= 0) {
      throw new InvalidDateException(days);
    }
    this.date += days;
  }

  public Collection<Product> getAllProducts() {
    return this.products.values().stream().sorted().collect(Collectors.toList());
  }

  public Partner getPartner(String key) throws UnknownPartnerKeyException {
    Partner p = this.partners.get(key);
    if (p == null) {
      throw new UnknownPartnerKeyException(key);
    }
    return p;
  }

  public Product getProduct(String key) throws UnknownProductKeyException {
    Product p = this.products.get(key);
    if (p == null) {
      throw new UnknownProductKeyException(key);
    }
    return p;
  }

  /**
   * @param txtFile filename to be loaded.
   * @throws IOException
   * @throws BadEntryException
   */
  void importFile(String txtFile) throws IOException, BadEntryException, IllegalEntryException {
    try (BufferedReader s = new BufferedReader(new FileReader(txtFile))) {
      String line;
      while ((line = s.readLine()) != null) {
        importFromFields(line.split("\\|"));
      }
    }
  }

  private void importFromFields(String[] fields) throws BadEntryException, IllegalEntryException {
    switch (fields[0]) {
      case "PARTNER" -> this.importPartner(fields);
      case "BATCH_S" -> this.importSimpleBatch(fields);
      case "BATCH_M" -> this.importMultiBatch(fields);
      default -> throw new BadEntryException(String.join("|", fields));
    }
  }

  private void importPartner(String[] fields) throws IllegalEntryException {
    if (fields.length != 4) {
      throw new IllegalEntryException(fields);
    }
    this.registerPartner(fields[1], fields[2], fields[3]);
  }

  private void importSimpleBatch(String[] fields) throws IllegalEntryException {
    try {
      Partner partner = this.getPartner(fields[2]);
      Product product;
      try {
        product = this.getProduct(fields[1]);
      } catch (UnknownProductKeyException e) {
        product = this.registerSimpleProduct(fields[1]);
      }
      product.registerBatch(Integer.parseInt(fields[4]), Double.parseDouble(fields[3]), partner);
    } catch (UnknownPartnerKeyException | NumberFormatException e) {
      throw new IllegalEntryException(fields);
    }
  }

  private void importMultiBatch(String[] fields) throws IllegalEntryException {
    try {
      Partner partner = this.getPartner(fields[2]);
      Product product;
      try {
        product = this.getProduct(fields[1]);
      } catch (UnknownProductKeyException e) {
        Recipe recipe = this.importRecipe(fields[5], fields[6]);
        product = this.registerDerivedProduct(fields[1], recipe);
      }
      product.registerBatch(Integer.parseInt(fields[4]), Double.parseDouble(fields[3]), partner);
    } catch (UnknownPartnerKeyException | NumberFormatException e) {
      throw new IllegalEntryException(fields);
    }
  }

  private Recipe importRecipe(String aggravatingFactor, String productsDescription) throws NumberFormatException {
    List<RecipeProduct> products = new ArrayList<>();
    String[] productDescriptors = productsDescription.split("#");
    for (String desc : productDescriptors) {
      String[] fields = desc.split(":");
      String prodKey = fields[0];
      int quantity = Integer.parseInt(fields[1]);
      Product prod;
      try {
        prod = this.getProduct(prodKey);
      } catch (UnknownProductKeyException e) {
        prod = this.registerSimpleProduct(prodKey);
      }
      products.add(new RecipeProduct(quantity, prod));
    }
    return new Recipe(Double.parseDouble(aggravatingFactor), products);
  }

  private Partner registerPartner(String id, String name, String address) {
    Partner p = new Partner(id, name, address);
    this.partners.put(p.getId(), p);
    return p;
  }

  private Product registerSimpleProduct(String id) {
    Product p = new Product(id);
    this.products.put(p.getId(), p);
    return p;
  }

  private DerivedProduct registerDerivedProduct(String id, Recipe recipe) {
    DerivedProduct p = new DerivedProduct(id, recipe);
    this.products.put(p.getId(), p);
    return p;
  }
}
