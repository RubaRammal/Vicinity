package vicinity.Controller;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import vicinity.ConnectionManager.ChatClient;
import vicinity.ConnectionManager.ConnectAndDiscoverService;
import vicinity.model.Globals;
import vicinity.model.VicinityMessage;
import vicinity.vicinity.ChatActivity;
import vicinity.vicinity.R;

/**
 * This class handles user notifications
 */
public class VicinityNotifications {


    private static final String TAG = "Notify";
    public static ChatClient chatClient;
    public static boolean isRunning = false;

    /**
     * Notifies receiver about new messages.
     * @param newMsg a VicinityMessage object that has arrived
     */
    public static void newMessageNotification(VicinityMessage newMsg) {

        NotificationManager notificationManager = (NotificationManager) ConnectAndDiscoverService.ctx.getSystemService(ConnectAndDiscoverService.ctx.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.vicinity_logo, newMsg.getMessageBody(), System.currentTimeMillis());
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        CharSequence title = newMsg.getFriendID();//TODO change this to the friend's name
        CharSequence text = newMsg.getMessageBody();



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