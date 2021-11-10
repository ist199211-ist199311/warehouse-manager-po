package ggc.partners;

import java.io.Serial;

import ggc.transactions.BreakdownTransaction;
import ggc.transactions.SaleTransaction;

public class NormalPartnerStatute extends Partner.Statute {
  /**
   * Serial number for serialization.
   */
  @Serial
  private static final long serialVersionUID = 202111101757L;

  public NormalPartnerStatute(Partner partner, int points) {
    partner.super(points);
  }

  public void calculateSaleBenefits(SaleTransaction saleTransaction, int date) {
    final int delta = date - saleTransaction.getPaymentDeadline();
  }

  public void applySaleBenefits(SaleTransaction saleTransaction, int date) {
  }

  public void applyBreakdownBenefits(
      BreakdownTransaction breakdownTransaction, int date) {

  }
}
