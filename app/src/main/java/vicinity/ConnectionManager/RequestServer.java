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
import vicinity.model.Neighbor;

public class RequestServer extends Thread{

    final static String TAG ="Request";
    ServerSocket requestSocket;
    Socket clientSocket;
    DataOutputStream toPeer;
    DataInputStream fromPeer;
    MainController controller = new MainController(ConnectAndDiscoverService.ctx);

    public RequestServer(){

        try {
            Log.i(TAG,"Requests thread has started...");
            requestSocket = new ServerSocket(Globals.REQUEST_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        while (true) {
            try {
                clientSocket = requestSocket.accept();
                InetAddress requestIP = clientSocket.getInetAddress();
                Log.i(TAG,"A friend's request from: "+requestIP);
                ObjectInputStream inputStream =  new ObjectInputStream(clientSocket.getInputStream());
                Neighbor requestFrom = (Neighbor)inputStream.readObject();
                requestFrom.setIpAddress(requestIP);
                Log.i(TAG,"Received a request from: "+requestFrom.toString()+" IP: "+requestFrom.getIpAddress());

                //First check if the user is muted
                if(controller.isUserMuted(requestFrom)){

                }
                else{

                }

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
