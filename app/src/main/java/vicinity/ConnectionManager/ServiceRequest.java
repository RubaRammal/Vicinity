package vicinity.ConnectionManager;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.SQLException;

import vicinity.Controller.MainController;
import vicinity.Controller.VicinityNotifications;
import vicinity.model.Globals;
import vicinity.model.VicinityMessage;
import vicinity.vicinity.ChatActivity;

/**
 * A class that implements Runnable.
 * It listens for packets from a client socket it is connected to
 * and either notify the user of the received packets or send them to the UI thread.
 */
public class ServiceRequest implements Runnable {
    private Socket socket;
    private final static String TAG = "ServiceRequest";
    private LocalBroadcastManager toChat;
    private Intent intent;
    private MainController controller;

    public ServiceRequest(Socket connection) throws IOException {
        this.socket = connection;
        toChat = LocalBroadcastManager.getInstance(ConnectAndDiscoverService.ctx);
        intent = new Intent();
        controller = new MainController(ConnectAndDiscoverService.ctx);
    }

    public void run() {

        try {
            Log.i(TAG, "Chat thread server has started...");

            InetAddress socketIp = socket.getInetAddress();
            Log.i(TAG, "Friend : " + socketIp + " started a chat");

            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            while (Globals.stopServer) {

                VicinityMessage msg = (VicinityMessage) inputStream.readObject();
                msg.setFrom(socket.getInetAddress().getHostAddress());
                msg.setIsMyMsg(false);
                Log.i(TAG, "Received a message: " + msg.toString());

                controller.addMessage(msg);

                if(Globals.chatActive && ChatActivity.friendsIp.equals(msg.getFrom())){
                    Intent intent = new Intent("MESSAGE");
                    intent.putExtra("NEW_MESSAGE", msg);
                    toChat.sendBroadcast(intent);
                } else {
                    VicinityNotifications.newMessageNotification(msg);
                    intent.putExtra("MSG", msg);
                    intent.setAction(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ComponentName cn = new ComponentName(ConnectAndDiscoverService.ctx, ChatActivity.class);
                    intent.setComponent(cn);
                }

            }

            socket.close();

        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}

