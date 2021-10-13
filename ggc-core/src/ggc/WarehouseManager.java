package ggc;

import ggc.exceptions.BadEntryException;
import ggc.exceptions.ImportFileException;
import ggc.exceptions.InvalidDateException;
import ggc.exceptions.MissingFileAssociationException;
import ggc.exceptions.UnavailableFileException;
import ggc.products.Product;

import java.io.FileNotFoundException;
import java.io.IOException;
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

  //FIXME define other attributes
  //FIXME define constructor(s)
  //FIXME define other methods

  public void advanceDate(int days) throws InvalidDateException {
    this._warehouse.advanceDate(days);
  }

  public int displayDate() {
    return this._warehouse.displayDate();
  }

  public Collection<Product> getAllProducts() {
    return this._warehouse.getAllProducts();
  }

  /**
   * @@throws IOException
   * @@throws FileNotFoundException
   * @@throws MissingFileAssociationException
   */
  public void save() throws IOException, FileNotFoundException, MissingFileAssociationException {
    //FIXME implement serialization method
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
    //FIXME implement serialization method
  }

  /**
   * @param textfile
   * @throws ImportFileException
   */
  public void importFile(String textfile) throws ImportFileException {
    try {
	    _warehouse.importFile(textfile);
    } catch (IOException | BadEntryException /* FIXME maybe other exceptions */ e) {
	    throw new ImportFileException(textfile);
    }
  }

}
