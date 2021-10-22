package ggc.products;

import java.io.Serializable;

public class RecipeProduct implements Serializable {
    /**
     * Serial number for serialization.
     */
    private static final long serialVersionUID = 202110221420L;

    private int quantity;
    private Product product;

    public RecipeProduct(int quantity, Product product) {
        this.quantity = quantity;
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public Product getProduct() {
        return product;
    }

    @Override
    public String toString() {
        return this.getProduct().getId() + ":" + this.getQuantity();
    }
}
