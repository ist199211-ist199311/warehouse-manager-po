package ggc;

import ggc.exceptions.BadEntryException;
import ggc.exceptions.InvalidDateException;
import ggc.products.Product;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class Warehouse implements a warehouse.
 */
public class Warehouse implements Serializable {

    /**
     * Serial number for serialization.
     */
    private static final long serialVersionUID = 202109192006L;

    private int date = 0;
    private Map<String, Product> products = new HashMap<>();

    // FIXME define attributes
    // FIXME define contructor(s)
    // FIXME define methods

    public int displayDate() {
        return date;
    }

    public void advanceDate(int days) throws InvalidDateException {
        if (days <= 0) {
            throw new InvalidDateException(days);
        }
        this.date += days;
    }

    public Collection<Product> getAllProducts() {
        return products.values().stream().sorted().collect(Collectors.toList());
    }

    /**
     * @param txtfile filename to be loaded.
     * @throws IOException
     * @throws BadEntryException
     */
    void importFile(String txtfile) throws IOException, BadEntryException /* FIXME maybe other exceptions */ {
        //FIXME implement method
    }

}
