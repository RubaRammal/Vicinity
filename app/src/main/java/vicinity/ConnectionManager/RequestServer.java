package vicinity.ConnectionManager;


import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import vicinity.model.Globals;

public class RequestServer extends Thread{

    final static String TAG ="Request";
    ServerSocket requestSocket;
    Socket clientSocket;
    DataOutputStream toPeer;
    DataInputStream fromPeer;

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


            } catch (IOException e) {


                e.printStackTrace();
                break;
            }
        }
    }




}
