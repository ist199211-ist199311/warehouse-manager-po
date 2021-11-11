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

  public NormalPartnerStatute(Partner partner, long points) {
    partner.super(points);
  }

  @Override
  public double calculateAdjustedValue(SaleTransaction saleTransaction,
      int date) {
    final int delta = date - saleTransaction.getPaymentDeadline();
    final int radius = this
        .getTransactionPeriodRadius(saleTransaction);
    double value = saleTransaction.baseValue();
    if (-delta >= radius) { // P1
      return 0.9 * value;
    } else if (0 < delta && delta <= radius) { // P3
      return (1 + (delta * 0.05)) * value;
    } else if (delta > radius) { // P4
      return (1 + (delta * 0.1)) * value;
    }
    return value;
  }

  @Override
  public double applySaleBenefits(SaleTransaction saleTransaction, int date) {
    final int delta = date - saleTransaction.getPaymentDeadline();
    final double adjusted = this.calculateAdjustedValue(saleTransaction, date);
    if (delta < 0) { // on time
      this.increasePoints(Math.round(10 * adjusted));
      this.tryForPromotion();
    } else { // late
      this.increasePoints(-this.getPoints());
    }
    return adjusted;
  }

  @Override
  public void applyBreakdownBenefits(
      BreakdownTransaction breakdownTransaction, int date) {
    final double value = breakdownTransaction.baseValue();
    if (value > 0) {
      this.increasePoints(Math.round(10 * breakdownTransaction.baseValue()));
      this.tryForPromotion();
    }
  }

  private void tryForPromotion() {
    final long points = this.getPoints();
    if (points > 2000) {
      this.setStatute(new SelectionPartnerStatute(this.getPartner(), points));
    }
  }
}
