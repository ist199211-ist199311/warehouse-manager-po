package ggc.notifications;

public interface NotificationSender {

  void subscribe(Notifiable notifiable);

  void unsubscribe(Notifiable notifiable);

  boolean isSubscribed(Notifiable notifiable);

  void toggleSubscription(Notifiable notifiable);

  void sendNotification(Notification notification);

}
