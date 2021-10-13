package ggc.exceptions;

public class InvalidDateException extends Exception {

    private int date;

    public InvalidDateException(int date) {
        this.date = date;
    }

    public int getDate() {
        return date;
    }
}
