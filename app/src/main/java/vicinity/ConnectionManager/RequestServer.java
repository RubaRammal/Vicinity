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
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vicinity.model.Globals;
import vicinity.model.Neighbor;

/**
 * This class handles received friend's request from neighbors
 * it alerts the user and takes his/her response to the request
 * and replies to the neighbor who sent the request
 */
public class RequestServer extends Thread{

    final static String TAG ="Request";
    private ServerSocket requestSocket;
    private ExecutorService executor = Executors.newFixedThreadPool(30);
    public static  BroadcastReceiver requestsReceiver;



    /**
     * Public constructor
     * initiates server socket, and local broadcast manager
     * to receive user's reply from the main thread
     */
    public RequestServer() throws IOException{
        Log.i(TAG,"Requests Server has started...");
    }


    @Override
    public void run() {
        try{
            requestSocket = new ServerSocket();
            requestSocket.setReuseAddress(true);
            requestSocket.bind(new InetSocketAddress(Globals.REQUEST_PORT));


            while (Globals.isRequestServerRunning) {
                Socket clientSocket = requestSocket.accept();

                executor.submit(new RequestSupporter(clientSocket));

            }
        }

        catch(IOException e){
            e.printStackTrace();
        }
    }



    /**
     * Inner class of RequestServer
     * to implement a client thread
     */
    public class RequestSupporter implements Runnable{
        private LocalBroadcastManager alertUser;     //To alert user about the request

        private Socket clientSocket2=null;

        /**
         * Public constructor
         * @param s socket
         */
        public RequestSupporter(Socket s){
            clientSocket2=s;
            Log.i(TAG,"Processing a friend request...from: "+clientSocket2.getInetAddress());
            alertUser = LocalBroadcastManager.getInstance(ConnectAndDiscoverService.ctx);
        }

        @Override
        public void run() {
            try {

                InetAddress requestIP = clientSocket2.getInetAddress();
                ObjectInputStream inputStream = new ObjectInputStream(clientSocket2.getInputStream());
                final ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket2.getOutputStream());
                Neighbor requestFrom = (Neighbor) inputStream.readObject();
                requestFrom.setIpAddress(requestIP.getHostAddress());

                Log.i(TAG, "Received a request from: " + requestFrom.toString() + " IP: " + requestFrom.getIpAddress());


                //BroadcastReceiver to receive user reply from the main thread
                alertUser(requestFrom);
                requestsReceiver= new BroadcastReceiver() {
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
                LocalBroadcastManager.getInstance(ConnectAndDiscoverService.ctx).registerReceiver(requestsReceiver,
                        new IntentFilter("REPLY")
                );





            }catch(IOException e){
                e.printStackTrace();

            }
            catch(ClassNotFoundException e){
                e.printStackTrace();
            }
            catch(NullPointerException e){
                e.printStackTrace();
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


}