package ggc.products;

import ggc.partners.Partner;

public class Batch implements Comparable<Batch> {

    private int quantity;
    private double price;
    private Product product;
    private Partner partner;

    public Batch(int quantity, double price, Product product, Partner partner) {
        this.quantity = quantity;
        this.price = price;
        this.product = product;
        this.partner = partner;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public Product getProduct() {
        return product;
    }

    public Partner getPartner() {
        return partner;
    }

    @Override
    public int compareTo(Batch batch) {
        // TODO review
        int productCmp = getProduct().compareTo(batch.getProduct());
        if (productCmp != 0) return productCmp;
        int partnerCmp = getPartner().compareTo(batch.getPartner());
        if (partnerCmp != 0) return partnerCmp;
        int priceCmp = Double.compare(getPrice(), batch.getPrice());
        if (priceCmp != 0) return priceCmp;
        return Integer.compare(getQuantity(), batch.getQuantity());
    }
}
