package vicinity.ConnectionManager;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import vicinity.Controller.MainController;
import vicinity.model.Globals;
import vicinity.model.VicinityMessage;


public class ChatServer extends Thread{

    final static String TAG ="ChatServer";
    ServerSocket chatSocket;
    Socket clientSocket;


    public ChatServer() throws IOException{

            Log.i(TAG,"Chat thread has started...");
            chatSocket = new ServerSocket(Globals.CHAT_PORT);


    }

    @Override
    public void run() {
        while (true) {
            try {
                clientSocket = chatSocket.accept();
                InetAddress chatIP = clientSocket.getInetAddress();
                Log.i(TAG,"Friend : "+chatIP+" started a chat");
                ObjectInputStream inputStream =  new ObjectInputStream(clientSocket.getInputStream());
                VicinityMessage msg = (VicinityMessage)inputStream.readObject();
                Log.i(TAG,"Received a msg from: "+msg.getFriendID());

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            catch (ClassNotFoundException e){
                e.printStackTrace();
                break;
            }
        }
    }




}
