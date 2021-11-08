package ggc;

import ggc.exceptions.BadEntryException;
import ggc.exceptions.DuplicatePartnerKeyException;
import ggc.exceptions.DuplicateProductKeyException;
import ggc.exceptions.IllegalEntryException;
import ggc.exceptions.ImportFileException;
import ggc.exceptions.InvalidDateException;
import ggc.exceptions.MissingFileAssociationException;
import ggc.exceptions.UnavailableFileException;
import ggc.exceptions.UnavailableProductException;
import ggc.exceptions.UnknownPartnerKeyException;
import ggc.exceptions.UnknownProductKeyException;
import ggc.exceptions.UnknownTransactionKeyException;
import ggc.products.Batch;
import ggc.util.Visitable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;

/**
 * Façade for access.
 */
public class WarehouseManager {

  /**
   * Name of file storing current store.
   */
  private String _filename = "";

  /**
   * The warehouse itself.
   */
  private Warehouse _warehouse = new Warehouse();

  /**
   * @see Warehouse#advanceDate(int)
   */
  public void advanceDate(int days) throws InvalidDateException {
    this._warehouse.advanceDate(days);
  }

  /**
   * @see Warehouse#displayDate()
   */
  public int displayDate() {
    return this._warehouse.displayDate();
  }

  /**
   * @see Warehouse#getAllProducts()
   */
  public Collection<? extends Visitable> getAllProducts() {
    return this._warehouse.getAllProducts();
  }

  /**
   * @see Warehouse#getAllPartners()
   */
  public Collection<? extends Visitable> getAllPartners() {
    return this._warehouse.getAllPartners();
  }

  /**
   * @see Warehouse#getAllBatches()
   */
  public Collection<? extends Visitable> getAllBatches() {
    return this._warehouse.getAllBatches();
  }

  /**
   * @see Warehouse#getBatchesByPartner(String)
   */
  public Collection<? extends Visitable> getBatchesByPartner(String partnerId)
      throws UnknownPartnerKeyException {
    return this._warehouse.getBatchesByPartner(partnerId);
  }

  /**
   * @see Warehouse#getBatchesByProduct(String)
   */
  public Collection<Batch> getBatchesByProduct(String productId)
      throws UnknownProductKeyException {
    return this._warehouse.getBatchesByProduct(productId);
  }

  /**
   * @see Warehouse#lookupProductBatchesUnderGivenPrice(double)
   */
  public Collection<? extends Visitable> lookupProductBatchesUnderGivenPrice(
      double priceLimit) {
    return this._warehouse.lookupProductBatchesUnderGivenPrice(priceLimit);
  }

  /**
   * @see Warehouse#getPartner(String)
   */
  public Visitable getPartner(String key) throws UnknownPartnerKeyException {
    return this._warehouse.getPartner(key);
  }

  /**
   * @see Warehouse#getTransaction(int)
   */
  public Visitable getTransaction(int id)
      throws UnknownTransactionKeyException {
    return this._warehouse.getTransaction(id);
  }

  /**
   * @see Warehouse#registerSimpleProduct(String)
   */
  public void registerSimpleProduct(String id)
      throws DuplicateProductKeyException {
    this._warehouse.registerSimpleProduct(id);
  }

  /**
   * @see Warehouse#registerDerivedProduct(String, double, String[], int[])
   */
  public void registerDerivedProduct(String id, double aggravatingFactor,
      String[] recipeProducts, int[] recipeQuantities)
      throws UnknownProductKeyException, DuplicateProductKeyException {
    this._warehouse.registerDerivedProduct(id, aggravatingFactor,
        recipeProducts, recipeQuantities);
  }

  /**
   * @see Warehouse#registerPartner(String, String, String)
   */
  public void registerPartner(String id, String name, String address)
      throws DuplicatePartnerKeyException {
    this._warehouse.registerPartner(id, name, address);
  }

  /**
   * @see Warehouse#getAvailableBalance()
   */
  public double getAvailableBalance() {
    return this._warehouse.getAvailableBalance();
  }

  /**
   * @see Warehouse#getAccountingBalance()
   */
  public double getAccountingBalance() {
    return this._warehouse.getAccountingBalance();
  }

  /**
   * @see Warehouse#registerAcquisitionTransaction(String, String, double, int)
   */
  public void registerAcquisitionTransaction(String partnerId,
      String productId, double value, int quantity)
      throws UnknownPartnerKeyException, UnknownProductKeyException {
    this._warehouse.registerAcquisitionTransaction(partnerId, productId,
        value, quantity);
  }

  /**
   * @see Warehouse#registerSaleTransaction(String, String, int, int)
   */
  public void registerSaleTransaction(String partnerId, String productId,
      int paymentDeadline, int quantity)
      throws UnknownPartnerKeyException, UnknownProductKeyException,
      UnavailableProductException {
    this._warehouse.registerSaleTransaction(partnerId, productId,
        paymentDeadline, quantity);
  }

  /**
   * @see Warehouse#registerBreakdownTransaction(String, String, int)
   */
  public void registerBreakdownTransaction(String partnerId, String productId,
      int quantity) throws UnknownProductKeyException,
      UnknownPartnerKeyException, UnavailableProductException {
    this._warehouse.registerBreakdownTransaction(
        partnerId,
        productId,
        quantity);
  }

  /**
   * @see Warehouse#getPartnerAcquisitions(String)
   */
  public Collection<? extends Visitable> getPartnerAcquisitions(
      String partnerId)
      throws UnknownPartnerKeyException {
    return this._warehouse.getPartnerAcquisitions(partnerId);
  }

  /**
   * @see Warehouse#getPartnerSalesAndBreakdowns(String)
   */
  public Collection<? extends Visitable> getPartnerSalesAndBreakdowns(
      String partnerId)
      throws UnknownPartnerKeyException {
    return this._warehouse.getPartnerSalesAndBreakdowns(partnerId);
  }

  /**
   * Saves the current application state to the current file (either that was
   * previously saved to, or loaded from) as binary data. Nothing is written to
   * disk if the application state has not changed since the last save.
   *
   * @throws IOException                     if any kind of I/O error occurs
   * @throws FileNotFoundException           if the parent folders of the file
   *                                         to be saved do not exist or the
   *                                         file path is invalid
   * @throws MissingFileAssociationException if the file to save to is unknown
   */
  public void save() throws IOException, FileNotFoundException,
      MissingFileAssociationException {
    if (_filename == null || _filename.isBlank())
      throw new MissingFileAssociationException();

    if (_warehouse.isDirty()) {
      try (ObjectOutputStream out = new ObjectOutputStream(
          new BufferedOutputStream(new FileOutputStream(_filename)))) {
        out.writeObject(_warehouse);
      }
      _warehouse.clean();
    }
  }

  /**
   * Saves the current application state to the specified file as binary data.
   * Nothing is written if the application state has not changed since the last
   * save.
   *
   * @param fileName the name or path of the file to save to
   * @throws IOException                     if any kind of I/O error occurs
   * @throws FileNotFoundException           if the parent folders of the file
   *                                         to be saved do not exist or the
   *                                         file path is invalid
   * @throws MissingFileAssociationException if the file to save to is unknown
   */
  public void saveAs(String fileName) throws MissingFileAssociationException,
      FileNotFoundException, IOException {
    _filename = fileName;
    save();
  }

  /**
   * Loads the application state from the specified file, containing binary data
   * saved previously by the application. Additionally, stores the file
   * name/path for future saves using {@link WarehouseManager#save()}.
   *
   * @param fileName the name or path of the file to load from
   * @throws UnavailableFileException if an input/output error occurs, such as
   *                                  the file not existing or not having valid
   *                                  binary data
   */
  public void load(String fileName) throws UnavailableFileException {
    try (ObjectInputStream in = new ObjectInputStream(
        new BufferedInputStream(new FileInputStream(fileName)))) {
      _warehouse = (Warehouse) in.readObject();
      this._filename = fileName;
    } catch (IOException | ClassNotFoundException e) {
      throw new UnavailableFileException(fileName);
    }
  }

  /**
   * Imports data from a plaintext file where each line represents a single
   * object.
   *
   * @param textFile the name or path of the text file to import data from
   * @throws ImportFileException if any I/O error occurs, such as the file not
   *                             existing, or if the file contains malformed
   *                             data
   */
  public void importFile(String textFile) throws ImportFileException {
    try {
      this._warehouse.importFile(textFile);
    } catch (IOException | BadEntryException | IllegalEntryException e) {
      throw new ImportFileException(textFile);
    }
  }

}
