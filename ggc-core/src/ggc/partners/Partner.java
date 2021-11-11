package ggc.partners;

import ggc.transactions.BreakdownTransaction;
import ggc.transactions.SaleTransaction;
import ggc.transactions.Transaction;
import ggc.notifications.Notifiable;
import ggc.notifications.Notification;
import ggc.notifications.NotificationDeliveryMethod;
import ggc.util.NaturalTextComparator;
import ggc.util.Visitable;
import ggc.util.Visitor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

public class Partner implements Comparable<Partner>, Serializable, Visitable,
    Notifiable {
  /**
   * Serial number for serialization.
   */
  @Serial
  private static final long serialVersionUID = 202110221420L;

  private final Comparator<String> idComparator = new NaturalTextComparator();
  private final String id;
  // TODO add statute, notifications
  private final Queue<Notification> inAppNotifications = new LinkedList<>();
  private String name;
  private String address;
  private Statute statute;
  private NotificationDeliveryMethod notificationDeliveryMethod = inAppNotifications::add;
  private double acquisitionsValue;
  private double salesValue;
  private double paidSalesValue;

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

  public void increaseAcquisitionsValue(double value) {
    this.acquisitionsValue += value;
  }

  public void increaseSalesValue(double value) {
    this.salesValue += value;
  }

  public void increasePaidSalesValue(double value) {
    this.paidSalesValue = value;
  }

  public double getPurchasesValue() {
    return acquisitionsValue;
  }

  public double getSalesValue() {
    return salesValue;
  }

  public double getPaidSalesValue() {
    return paidSalesValue;
  }

  @Override
  public void notify(Notification notification) {
    notificationDeliveryMethod.deliver(notification);
  }

  @Override
  public void setNotificationDeliveryMethod(
      NotificationDeliveryMethod deliveryMethod) {
    this.notificationDeliveryMethod = deliveryMethod;
  }

  public Collection<Notification> readInAppNotifications() {
    Collection<Notification> notifications = new LinkedList<>(
        this.inAppNotifications);
    this.inAppNotifications.clear();
    return notifications;
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
