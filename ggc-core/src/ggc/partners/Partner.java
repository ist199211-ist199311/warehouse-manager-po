package ggc.partners;

import ggc.transactions.BreakdownTransaction;
import ggc.transactions.SaleTransaction;
import ggc.transactions.Transaction;
import ggc.util.NaturalTextComparator;
import ggc.util.Visitable;
import ggc.util.Visitor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;

public class Partner implements Comparable<Partner>, Serializable, Visitable {
  /**
   * Serial number for serialization.
   */
  @Serial
  private static final long serialVersionUID = 202110221420L;

  private final Comparator<String> idComparator = new NaturalTextComparator();

  // TODO add notifications
  private final String id;
  private String name;
  private String address;
  private Statute statute;

  public Partner(String id, String name, String address) {
    this.id = id;
    this.name = name;
    this.address = address;
    this.statute = new NormalPartnerStatute(this, 0);
  }

  public String getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return this.address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public double calculateAdjustedValue(
      SaleTransaction saleTransaction,
      int date) {
    return this.statute.calculateAdjustedValue(saleTransaction, date);
  }

  public double applySaleBenefits(SaleTransaction saleTransaction,
      int date) {
    return this.statute.applySaleBenefits(saleTransaction, date);
  }

  public void applyBreakdownBenefits(
      BreakdownTransaction breakdownTransaction, int date) {
    this.statute.applyBreakdownBenefits(breakdownTransaction, date);
  }

  @Override
  public int compareTo(Partner partner) {
    return idComparator.compare(this.getId(), partner.getId());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o instanceof Partner) {
      Partner partner = (Partner) o;
      return this.compareTo(partner) == 0;
    }
    return false;
  }

  @Override
  public <T> T accept(Visitor<T> visitor) {
    return visitor.visit(this);
  }

  public abstract class Statute implements Serializable {
    private long points = 0;

    private final TransactionPeriodRadiusProvider transactionPeriodRadiusProvider = new TransactionPeriodRadiusProvider();

    public Statute(long points) {
      this.points = points;
    }

    public Partner getPartner() {
      return Partner.this;
    }

    public long getPoints() {
      return this.points;
    }

    /**
     * Increases points by a given amount, guaranteeing resulting amount is
     * non-negative.
     * 
     * @param delta how much to increase - may be negative!
     */
    protected void increasePoints(long delta) {
      this.points = Math.max(0, this.points + delta);
    }

    protected void setStatute(Statute statute) {
      Partner.this.statute = statute;
    }

    protected int getTransactionPeriodRadius(Transaction transaction) {
      return transaction.getProduct().accept(transactionPeriodRadiusProvider);
    }

    public abstract double calculateAdjustedValue(
        SaleTransaction saleTransaction,
        int date);

    public abstract double applySaleBenefits(SaleTransaction saleTransaction,
        int date);

    public abstract void applyBreakdownBenefits(
        BreakdownTransaction breakdownTransaction, int date);

  }
}
