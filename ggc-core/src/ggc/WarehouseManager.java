package ggc;

import ggc.exceptions.BadEntryException;
import ggc.exceptions.DuplicatePartnerKeyException;
import ggc.exceptions.IllegalEntryException;
import ggc.exceptions.ImportFileException;
import ggc.exceptions.InvalidDateException;
import ggc.exceptions.MissingFileAssociationException;
import ggc.exceptions.UnavailableFileException;
import ggc.exceptions.UnknownPartnerKeyException;
import ggc.partners.Partner;
import ggc.products.Batch;
import ggc.products.Product;

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

  public void advanceDate(int days) throws InvalidDateException {
    this._warehouse.advanceDate(days);
  }

  public int displayDate() {
    return this._warehouse.displayDate();
  }

  public Collection<Product> getAllProducts() {
    return this._warehouse.getAllProducts();
  }

  public Collection<Partner> getAllPartners() {
    return this._warehouse.getAllPartners();
  }

  public Collection<Batch> getAllBatches() {
    return this._warehouse.getAllBatches();
  }

  public Partner getPartner(String key) throws UnknownPartnerKeyException {
    return this._warehouse.getPartner(key);
  }

  public void registerPartner(String id, String name, String address) throws DuplicatePartnerKeyException {
    this._warehouse.registerPartner(id, name, address);
  }

  /**
   * Saves the current application state to the current file (either that was previously saved
   * to, or loaded from) as binary data. Nothing is written to disk if the application state
   * has not changed since the last save.
   *
   * @throws IOException
   * @throws FileNotFoundException
   * @throws MissingFileAssociationException if the file to save to is unknown
   */
  public void save() throws IOException, FileNotFoundException, MissingFileAssociationException {
    if (_filename == null || _filename.isBlank())
      throw new MissingFileAssociationException();

    try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(_filename)))) {
      out.writeObject(_warehouse);
    }
  }

  /**
   * Saves the current application state to the specified file as binary data.
   * Nothing is written if the application state has not changed since
   * the last save.
   *
   * @param fileName the name or path of the file to save to
   * @throws MissingFileAssociationException if the file to save to is unknown
   * @throws IOException
   * @throws FileNotFoundException
   */
  public void saveAs(String fileName) throws MissingFileAssociationException, FileNotFoundException, IOException {
    _filename = fileName;
    save();
  }

  /**
   * Loads the application state from the specified file, containing binary data saved previously by the application.
   * Additionally, stores the file name/path for future saves using {@link WarehouseManager#save()}.
   *
   * @param fileName the name or path of the file to load from
   * @throws UnavailableFileException if an input/output error occurs, such as the file not existing or not having
   *                                  valid binary data
   */
  public void load(String fileName) throws UnavailableFileException {
    try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(fileName)))) {
      _warehouse = (Warehouse) in.readObject();
      this._filename = fileName;
    } catch (IOException | ClassNotFoundException e) {
      throw new UnavailableFileException(fileName);
    }
  }

  /**
   * Imports data from a plaintext file where each line represents a single object.
   *
   * @param textFile the name or path of the text file to import data from
   * @throws ImportFileException if any I/O error occurs, such as the file not existing, or if the file
   *                             contains malformed data
   */
  public void importFile(String textFile) throws ImportFileException {
    try {
      this._warehouse.importFile(textFile);
    } catch (IOException | BadEntryException | IllegalEntryException e) {
      throw new ImportFileException(textFile);
    }
  }

}
