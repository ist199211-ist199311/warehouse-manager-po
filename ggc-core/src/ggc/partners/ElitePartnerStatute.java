package ggc.partners;

import java.io.Serial;

import ggc.transactions.BreakdownTransaction;
import ggc.transactions.SaleTransaction;

public class ElitePartnerStatute extends Partner.Statute {
  /**
   * Serial number for serialization.
   */
  @Serial
  private static final long serialVersionUID = 202111111200L;

  public ElitePartnerStatute(Partner partner, long points) {
    partner.super(points);
  }

  public double calculateAdjustedPrice(SaleTransaction saleTransaction,
      int date) {
    final int delta = date - saleTransaction.getPaymentDeadline();
    final int radius = this
        .getTransactionPeriodRadius(saleTransaction);
    double price = saleTransaction.baseValue();
    if (-delta >= radius) { // P1
      return 0.9 * price;
    } else if (2 <= -delta && -delta < radius) { // P2 & >=2 days
      return 0.95 * price;
    } else if (1 < delta && delta <= radius) { // P3 & >1 day
      return (1 + (delta * 0.02)) * price;
    } else if (delta > radius) { // P4
      return (1 + (delta * 0.05)) * price;
    }
    return price;
  }

  public double applySaleBenefits(SaleTransaction saleTransaction, int date) {
    final int delta = date - saleTransaction.getPaymentDeadline();
    final double adjusted = this.calculateAdjustedPrice(saleTransaction, date);
    if (delta < 0) { // on time
      this.increasePoints(Math.round(10 * adjusted));
    } else if (delta > 15) { // very late
      this.increasePoints(Math.round(-0.75 * this.getPoints()));
      this.setStatute(
          new SelectionPartnerStatute(this.getPartner(), this.getPoints()));
    }
    return adjusted;
  }

  public void applyBreakdownBenefits(
      BreakdownTransaction breakdownTransaction, int date) {
    final double value = breakdownTransaction.baseValue();
    if (value > 0) {
      this.increasePoints(Math.round(10 * breakdownTransaction.baseValue()));
    }
  }
}