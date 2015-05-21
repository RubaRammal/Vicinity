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
 * Created by macproretina on 5/18/15.
 */
public class ServiceRequest implements Runnable {
    private Socket socket;
    private final static String TAG = "ServiceRequest";
    private LocalBroadcastManager toChat;
    private ObjectInputStream inputStream;
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


            InetAddress serverThreadId = socket.getInetAddress();
            Log.i(TAG, "Friend : " + serverThreadId + " started a chat");


            inputStream = new ObjectInputStream(socket.getInputStream());

            while (Globals.stopServer) {


                VicinityMessage msg = (VicinityMessage) inputStream.readObject();
                msg.setFrom(socket.getInetAddress().getHostAddress());
                msg.setIsMyMsg(false);
                Log.i(TAG, "Received a message: " + msg.toString());

                controller.addMessage(msg);


                //VicinityNotifications.newMessageNotification(msg);


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

