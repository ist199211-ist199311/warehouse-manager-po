package ggc.partners;

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
  private NotificationDeliveryMethod notificationDeliveryMethod =
          inAppNotifications::add;
  private double purchasesValue;
  private double salesValue;
  private double paidSalesValue;

  public Partner(String id, String name, String address) {
    this.id = id;
    this.name = name;
    this.address = address;
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

  public void increasePurchasesValue(double value) {
    this.purchasesValue += value;
  }

  public void increaseSalesValue(double value) {
    this.salesValue += value;
  }

  public void increasePaidSalesValue(double value) {
    this.paidSalesValue = value;
  }

  public double getPurchasesValue() {
    return purchasesValue;
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
  public void setNotificationDeliveryMethod(NotificationDeliveryMethod deliveryMethod) {
    this.notificationDeliveryMethod = deliveryMethod;
  }

  public Collection<Notification> readInAppNotifications() {
    Collection<Notification> notifications =
            new LinkedList<>(this.inAppNotifications);
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
}
