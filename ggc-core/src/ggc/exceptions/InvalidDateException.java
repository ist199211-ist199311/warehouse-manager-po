package ggc.exceptions;

public class InvalidDateException extends Exception {
    /**
     * Serial number for serialization.
     */
    private static final long serialVersionUID = 202110221420L;

    private int date;

    public InvalidDateException(int date) {
        this.date = date;
    }

    public int getDate() {
        return date;
    }
}
