package ggc;

import ggc.app.exceptions.UnknownPartnerKeyException;
import ggc.app.exceptions.UnknownProductKeyException;
import ggc.exceptions.BadEntryException;
import ggc.exceptions.IllegalEntryException;
import ggc.exceptions.InvalidDateException;
import ggc.partners.Partner;
import ggc.products.Product;

import java.io.BufferedReader;
import java.io.FileReader;
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
    private Map<String, Partner> partners = new HashMap<>();

    // FIXME define attributes
    // FIXME define contructor(s)
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
     * @param txtfile filename to be loaded.
     * @throws IOException
     * @throws BadEntryException
     */
    void importFile(String txtfile) throws IOException, BadEntryException, IllegalEntryException {
        try (BufferedReader s = new BufferedReader(new FileReader(txtfile))) {
            String line;
            while ((line = s.readLine()) != null) {
                importFromFields(line.split("|"));
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
        if (fields.length == 4) {
            this.registerPartner(fields[1], fields[2], fields[3]);
        } else {
            throw new IllegalEntryException(fields);
        }
    }

    private void importSimpleBatch(String[] fields) throws IllegalEntryException {
        try {
            Product product;
            try {
                product = this.getProduct(fields[1]);
            } catch (UnknownProductKeyException e) {
                product = this.registerProduct(fields[1]);
            }
            Partner partner = this.getPartner(fields[2]);
            product.registerBatch(Integer.parseInt(fields[4]), Integer.parseInt(fields[3]), partner);
        } catch (UnknownPartnerKeyException | NumberFormatException e) {
            throw new IllegalEntryException(fields);
        }
    }

    private void importMultiBatch(String[] fields) throws IllegalEntryException {
        // TODO:
    }

    private Partner registerPartner(String id, String name, String address) {
        Partner p = new Partner(id, name, address);
        this.partners.put(p.getId(), p);
        return p;
    }

    private Product registerProduct(String id) {
        Product p = new Product(id);
        this.products.put(p.getId(), p);
        return p;
    }
}
