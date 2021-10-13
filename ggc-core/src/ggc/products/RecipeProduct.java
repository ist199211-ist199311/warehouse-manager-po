package ggc.products;

public class RecipeProduct {

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
