package vicinity.ConnectionManager;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import vicinity.model.Globals;
import vicinity.model.Neighbor;

/**
 * This class handles received friend's request from neighbors
 * it alerts the user and takes his/her response to the request
 * and replies to the neighbor who sent the request
 */
public class RequestServer extends Thread{

    final static String TAG ="Request";
    ServerSocket requestSocket;
    Socket clientSocket;
    private LocalBroadcastManager alertUser;     //To alert user about the request


    /**
     * Public constructor
     * initiates server socket, and local broadcast manager
     * to receive user's reply from the main thread
     */
    public RequestServer() throws IOException{

            Log.i(TAG,"Requests thread has started...");
            requestSocket = new ServerSocket(Globals.REQUEST_PORT);
            alertUser = LocalBroadcastManager.getInstance(ConnectAndDiscoverService.ctx);
    }


    @Override
    public void run() {
        while (true) {
            try {
                //Client socket to receive requests
                clientSocket = requestSocket.accept();
                InetAddress requestIP = clientSocket.getInetAddress();
                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                final ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                Neighbor requestFrom = (Neighbor) inputStream.readObject();
                requestFrom.setIpAddress(requestIP);

                Log.i(TAG, "Received a request from: " + requestFrom.toString() + " IP: " + requestFrom.getIpAddress());


                    //BroadcastReceiver to receive user reply from the main thread
                    alertUser(requestFrom);
                    BroadcastReceiver requestsReceiver = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, final Intent intent) {
                            final Bundle bundle = intent.getExtras();
                            boolean reply = bundle.getBoolean("REPLY_REQUEST");
                            Log.i("REQUEST", " " + reply);

                            try {
                                outputStream.writeBoolean(reply);
                                outputStream.flush();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    LocalBroadcastManager.getInstance(ConnectAndDiscoverService.ctx).registerReceiver((requestsReceiver),
                            new IntentFilter("REPLY")
                    );


                }catch(IOException e){
                    e.printStackTrace();
                    break;
                }
                catch(ClassNotFoundException e){
                    e.printStackTrace();
                    break;
                }

        }
    }

    /**
     * Alert user on UI thread
     * @param newRequest a Neighbor object
     *                   contains info of the peer
     *                   who sent the request
     */
    public void alertUser(Neighbor newRequest){
        Intent intent = new Intent("REQUEST");
        intent.putExtra("NEW_REQUEST",newRequest);
        alertUser.sendBroadcast(intent);
    }








}
