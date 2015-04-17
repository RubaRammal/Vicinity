package vicinity.Controller;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import vicinity.model.VicinityMessage;
import vicinity.vicinity.ChatActivity;
import vicinity.vicinity.R;


public class VicinityNotifications {


    private static final String TAG = "Notify";

    /**
     * Notifies receiver about new messages.
     * @param newMsg a message that has arrived
     */
    public static void newMessageNotification(VicinityMessage newMsg) {

        android.app.NotificationManager notificationManager = (android.app.NotificationManager) ChatActivity.ctx.getSystemService(ChatActivity.ctx.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.vicinity_logo, newMsg.getMessageBody(), System.currentTimeMillis());
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        CharSequence title = "Afnan";//change this after you get the friend's name
        CharSequence text = newMsg.getMessageBody();

        Intent notificationIntent = new Intent(ChatActivity.ctx, ChatActivity.class);
        // pendingIntent that will start a new activity.
        PendingIntent contentIntent = PendingIntent.getActivity(ChatActivity.ctx, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        notification.setLatestEventInfo(ChatActivity.ctx, title, text, contentIntent);
        notificationManager.notify(1, notification);
        Log.d(TAG, "Notification: " + newMsg.getMessageBody());
    }

}
