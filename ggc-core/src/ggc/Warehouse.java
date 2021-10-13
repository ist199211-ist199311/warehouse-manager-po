package ggc;

import ggc.exceptions.BadEntryException;
import ggc.exceptions.InvalidDateException;

import java.io.IOException;
import java.io.Serializable;

/**
 * Class Warehouse implements a warehouse.
 */
public class Warehouse implements Serializable {

    /**
     * Serial number for serialization.
     */
    private static final long serialVersionUID = 202109192006L;

    private int date = 0;

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

    /**
     * @param txtfile filename to be loaded.
     * @throws IOException
     * @throws BadEntryException
     */
    void importFile(String txtfile) throws IOException, BadEntryException /* FIXME maybe other exceptions */ {
        //FIXME implement method
    }

}
