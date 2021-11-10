package ggc.partners;

import ggc.transactions.BreakdownTransaction;
import ggc.transactions.SaleTransaction;
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
    private int points = 0;

    public Statute(int points) {
      this.points = points;
    }

    public int getPoints() {
      return this.points;
    }

    public Partner getPartner() {
      return Partner.this;
    }

    protected void setStatute(Statute statute) {
      Partner.this.statute = statute;
    }

    public abstract void calculateSaleBenefits(SaleTransaction saleTransaction,
        int date);

    public abstract void applySaleBenefits(SaleTransaction saleTransaction,
        int date);

    public abstract void applyBreakdownBenefits(
        BreakdownTransaction breakdownTransaction, int date);

  }
}
