package ggc.transactions;

import ggc.partners.Partner;
import ggc.products.Product;
import ggc.util.Visitor;

public class SaleTransaction extends Transaction {

  private Double adjustedValue = null;
  private boolean paid = false;

  public SaleTransaction(int id, double value, int quantity, Product product,
                         Partner partner) {
    super(id, value, quantity, product, partner);
  }

  public boolean isPaid() {
    return paid;
  }

  public void pay() {
    calculateAdjustedValue();
    this.paid = true;
  }

  public void calculateAdjustedValue() {
    // TODO this.partner.calculateSaleTransactionBenefits(this)
  }

  @Override
  public <T> T accept(Visitor<T> visitor) {
    return visitor.visit(this);
  }
}
