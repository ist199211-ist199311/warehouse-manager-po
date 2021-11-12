package ggc.partners;

import ggc.transactions.BreakdownTransaction;
import ggc.transactions.SaleTransaction;

import java.io.Serial;

public class SelectionPartnerStatute extends Partner.Statute {
  /**
   * Serial number for serialization.
   */
  @Serial
  private static final long serialVersionUID = 202111111156L;

  private final TransactionPeriodRadiusProvider transactionPeriodRadiusProvider
          = new TransactionPeriodRadiusProvider();

  public SelectionPartnerStatute(Partner partner, long points) {
    partner.super(points);
  }

  @Override
  public String getName() {
    return "SELECTION";
  }

  @Override
  public double calculateAdjustedValue(SaleTransaction saleTransaction,
                                       int date) {
    final int delta = date - saleTransaction.getPaymentDeadline();
    final int radius = saleTransaction.getProduct()
            .accept(transactionPeriodRadiusProvider);
    final double value = saleTransaction.baseValue();
    if (-delta >= radius) { // P1
      return 0.9 * value;
    } else if (2 <= -delta && -delta < radius) { // P2 & >=2 days
      return 0.95 * value;
    } else if (1 < delta && delta <= radius) { // P3 & >1 day
      return (1 + (delta * 0.02)) * value;
    } else if (delta > radius) { // P4
      return (1 + (delta * 0.05)) * value;
    }
    return value;
  }

  @Override
  public double applySaleSideEffects(SaleTransaction saleTransaction, int date) {
    final int delta = date - saleTransaction.getPaymentDeadline();
    final double adjusted = this.calculateAdjustedValue(saleTransaction, date);
    if (delta < 0) { // on time
      this.increasePoints(Math.round(10 * adjusted));
      this.tryForPromotion();
    } else if (delta > 2) { // very late
      this.increasePoints(Math.round(-0.9 * this.getPoints()));
      this.setStatute(
              new NormalPartnerStatute(this.getPartner(), this.getPoints()));
    }
    return adjusted;
  }

  @Override
  public void applyBreakdownSideEffects(
          BreakdownTransaction breakdownTransaction, int date) {
    final double value = breakdownTransaction.baseValue();
    if (value > 0) {
      this.increasePoints(Math.round(10 * value));
      this.tryForPromotion();
    }
  }

  private void tryForPromotion() {
    final long points = this.getPoints();
    if (points > 25000) {
      this.setStatute(new ElitePartnerStatute(this.getPartner(), points));
    }
  }
}
