package vicinity.ConnectionManager;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

import vicinity.Controller.MainController;
import vicinity.Controller.VicinityNotifications;
import vicinity.model.Globals;
import vicinity.model.VicinityMessage;
import vicinity.vicinity.ChatActivity;


public class ChatServer implements Runnable{

    private final static String TAG ="ChatServer";
    private ServerSocket chatSocket;
    private Socket clientSocket;
    private LocalBroadcastManager toChat;
    private ObjectInputStream inputStream;
    private Intent intent;
    private MainController controller;




    public ChatServer() throws IOException{

        Log.i(TAG,"Chat thread has started...");
        chatSocket = new ServerSocket(Globals.CHAT_PORT);
        toChat = LocalBroadcastManager.getInstance(ConnectAndDiscoverService.ctx);
        intent = new Intent();
        controller = new MainController(ConnectAndDiscoverService.ctx);

    }

    @Override
    public void run() {
        try {
            clientSocket = chatSocket.accept();

        InetAddress serverThreadId = clientSocket.getInetAddress();
        Log.i(TAG,"Friend : "+serverThreadId+" started a chat");
        inputStream =  new ObjectInputStream(clientSocket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
        while (Globals.stopServer) {
            try {

                VicinityMessage msg = (VicinityMessage)inputStream.readObject();
                msg.setFrom(clientSocket.getInetAddress().getHostAddress());
                msg.setIsMyMsg(false);
                Log.i(TAG, "Received a message from: " + msg.getFriendID());
                Log.i(TAG,"Message content: "+msg.getMessageBody());
                Log.i(TAG,"Message IP: "+msg.getFrom());
                controller.addMessage(msg);




                if(!Globals.chatActive){
                VicinityNotifications.newMessageNotification(msg);
                intent.putExtra("MSG", msg);
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName cn = new ComponentName(ConnectAndDiscoverService.ctx, ChatActivity.class);
                intent.setComponent(cn);
                }
                else{
                    Intent intent = new Intent("MESSAGE");
                    intent.putExtra("NEW_MESSAGE",msg);
                    toChat.sendBroadcast(intent);
                }

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            catch (ClassNotFoundException e){
                e.printStackTrace();
                break;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}
