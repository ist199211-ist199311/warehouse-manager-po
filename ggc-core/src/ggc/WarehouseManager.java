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

  // FIXME define other attributes
  // FIXME define constructor(s)
  // FIXME define other methods

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

  public Partner getPartner(String key) throws UnknownPartnerKeyException {
    return this._warehouse.getPartner(key);
  }

  public void registerPartner(String id, String name, String address) throws DuplicatePartnerKeyException {
    this._warehouse.registerPartner(id, name, address);
  }

  /**
   * @@throws IOException
   * @@throws FileNotFoundException
   * @@throws MissingFileAssociationException
   */
  public void save() throws IOException, FileNotFoundException, MissingFileAssociationException {
    if (_filename == null || _filename.isBlank()) throw new MissingFileAssociationException();

    try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(_filename)))) {
      out.writeObject(_warehouse);
    }
  }

  /**
   * @@param filename
   * @@throws MissingFileAssociationException
   * @@throws IOException
   * @@throws FileNotFoundException
   */
  public void saveAs(String filename) throws MissingFileAssociationException, FileNotFoundException, IOException {
    _filename = filename;
    save();
  }

  /**
   * @@param filename
   * @@throws UnavailableFileException
   */
  public void load(String filename) throws UnavailableFileException {
    try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
      _warehouse = (Warehouse) in.readObject();
      this._filename = filename;
    } catch (IOException | ClassNotFoundException e) {
      throw new UnavailableFileException(filename);
    }
  }

  /**
   * @param textfile
   * @throws ImportFileException
   */
  public void importFile(String textfile) throws ImportFileException {
    try {
      this._warehouse.importFile(textfile);
    } catch (IOException | BadEntryException | IllegalEntryException e) {
      throw new ImportFileException(textfile);
    }
  }

}
