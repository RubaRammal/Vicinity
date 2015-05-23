package vicinity.ConnectionManager;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
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

    private final static String TAG = "ServiceRequest";
    // The client socket
    private Socket socket;
    // Broadcasts the received messages to the ChatActivity
    private LocalBroadcastManager toChat;
    // To insert received messages in the database
    private MainController controller;

    /**
     * Public constructor
     */
    public ServiceRequest(Socket connection) throws IOException {
        this.socket = connection;
        toChat = LocalBroadcastManager.getInstance(ConnectAndDiscoverService.ctx);
        controller = new MainController(ConnectAndDiscoverService.ctx);
    }

        /*---------Overridden Methods------------*/

    @Override
    public void run() {

        try {
            Log.i(TAG, "Chat thread server has started...");

            // Initialize object input stream to receive objects from the client socket
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            while (Globals.stopServer)
            {
                // Read new message
                VicinityMessage msg = (VicinityMessage) inputStream.readObject();
                // Set the IP address of the message to the IP address of the sender
                msg.setFrom(socket.getInetAddress().getHostAddress());
                // Set that the message is received from a friend
                msg.setIsMyMsg(false);

                Log.i(TAG, "Received a message: " + msg.toString());

                // Add the message to the database
                controller.addMessage(msg);

                // If the ChatActivity is active and the IP address of
                // the friend in it equals the client's IP address
                if(Globals.chatActive && ChatActivity.friendsIp.equals(socket.getInetAddress().getHostAddress()))
                {
                    // Broadcast the message to the active ChatActivity
                    Intent intent = new Intent("MESSAGE");
                    intent.putExtra("NEW_MESSAGE", msg);
                    toChat.sendBroadcast(intent);
                }
                else
                {
                    // Display the message as a notification to the user
                    VicinityNotifications.newMessageNotification(msg);
                }
            }
            socket.close();
        }
        catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }
}

