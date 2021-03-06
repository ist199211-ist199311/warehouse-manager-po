package ggc.partners;

import ggc.transactions.BreakdownTransaction;
import ggc.transactions.SaleTransaction;

import java.io.Serial;

public class ElitePartnerStatute extends Partner.Statute {
  /**
   * Serial number for serialization.
   */
  @Serial
  private static final long serialVersionUID = 202111111200L;

  private final TransactionPeriodRadiusProvider transactionPeriodRadiusProvider
          = new TransactionPeriodRadiusProvider();

  public ElitePartnerStatute(Partner partner, long points) {
    partner.super(points);
  }

  @Override
  public String getName() {
    return "ELITE";
  }

  @Override
  public double calculateAdjustedValue(SaleTransaction saleTransaction,
                                       int date) {
    final int delta = date - saleTransaction.getPaymentDeadline();
    final int radius = saleTransaction.getProduct()
            .accept(transactionPeriodRadiusProvider);
    final double value = saleTransaction.baseValue();
    if (delta <= 0) { // P1 & P2
      return 0.9 * value;
    } else if (delta <= radius) { // P3
      return 0.95 * value;
    }
    return value;
  }

  @Override
  public double applySaleSideEffects(SaleTransaction saleTransaction, int date) {
    final int delta = date - saleTransaction.getPaymentDeadline();
    final double adjusted = this.calculateAdjustedValue(saleTransaction, date);
    if (delta < 0) { // on time
      this.increasePoints(Math.round(10 * adjusted));
    } else if (delta > 15) { // very late
      this.increasePoints(Math.round(-0.75 * this.getPoints()));
      this.setStatute(
              new SelectionPartnerStatute(this.getPartner(), this.getPoints()));
    }
    return adjusted;
  }

  @Override
  public void applyBreakdownSideEffects(
          BreakdownTransaction breakdownTransaction, int date) {
    final double value = breakdownTransaction.baseValue();
    if (value > 0) {
      this.increasePoints(Math.round(10 * value));
    }
  }
}
