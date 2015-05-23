package vicinity.Controller;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import vicinity.ConnectionManager.ConnectAndDiscoverService;
import vicinity.model.VicinityMessage;
import vicinity.vicinity.ChatActivity;
import vicinity.vicinity.R;

/**
 * This class displays and handles message notifications.
 */
public class VicinityNotifications {


    private static final String TAG = "Notify";

    /**
     * Notifies receiver about new messages.
     * @param newMsg a VicinityMessage object that has arrived
     */
    public static void newMessageNotification(VicinityMessage newMsg) {

        // Notifies the user of background events
        NotificationManager notificationManager = (NotificationManager) ConnectAndDiscoverService.ctx.getSystemService(ConnectAndDiscoverService.ctx.NOTIFICATION_SERVICE);
        // Initialize notification instance
        Notification notification = new Notification(R.drawable.vicinity_logo, newMsg.getMessageBody(), System.currentTimeMillis());
        // Device vibrates when notification is received
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        // Title of the notification is the message sender's name
        CharSequence title = newMsg.getFriendName();
        CharSequence text;
        // If the message had a message body
        if(!newMsg.getMessageBody().equals(""))
        {
            // Set the notification text to the message body
            text = newMsg.getMessageBody();
        }
        else
        {
            // The message body is null means the received message is a photo
            text = "Sent a photo";

        }

        Intent notificationIntent = new Intent(ConnectAndDiscoverService.ctx, ChatActivity.class);
        notificationIntent.putExtra("MSG", newMsg);
        // pendingIntent that will start a new activity.
        PendingIntent contentIntent = PendingIntent.getActivity(ConnectAndDiscoverService.ctx, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        notification.setLatestEventInfo(ConnectAndDiscoverService.ctx, title, text, contentIntent);
        notificationManager.notify(1, notification);
        Log.d(TAG, "Notification: " + newMsg.getMessageBody());
    }

    public static void newFriendRequestNotification(String friendName){

    }

}