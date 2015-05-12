
package vicinity.ConnectionManager;

import android.os.Handler;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


import vicinity.model.Globals;

/**
 * The implementation of a ServerSocket handler. This is used by the wifi p2p
 * group owner.
 * the group owner acts as a server
 */


public class GroupOwnerSocketHandler extends Thread {

    ServerSocket socket = null;
    Socket clientSocket = null;
    private final int THREAD_COUNT = 10;
    private Handler handler;
    private static final String TAG = "GroupOwner";
    BroadcastManager broadcastManager = new BroadcastManager();
    private HashMap<String, InetAddress> clientsaddresses = new HashMap<>();


    /**
     * Public constructor
     * @param handler/
     * @throws IOException
     */
    public GroupOwnerSocketHandler(Handler handler) throws IOException {
        broadcastManager.execute();
        this.handler = handler;

        try {
          socket = new ServerSocket(Globals.SERVER_PORT);
           // socket = new ServerSocket();
           // socket.setReuseAddress(true);
           // socket.bind(new InetSocketAddress(Globals.SERVER_PORT));
        } catch (IOException e) {
            e.printStackTrace();
            pool.shutdownNow();
            throw e;
        }

    }


    /**
     * A ThreadPool for client sockets.
     */
    private final ThreadPoolExecutor pool = new ThreadPoolExecutor(
            THREAD_COUNT, THREAD_COUNT, 10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());


    /*---------Overridden Methods------------*/
    @Override
    public void run() {
        while (true) {
            try {



                getClientAddress();
                pool.execute(new ChatManager(socket.accept(), handler));


            } catch (IOException e) {
                try {
                    if (socket != null && !socket.isClosed())
                        socket.close();
                } catch (IOException ioe) {

                }
                e.printStackTrace();
                pool.shutdownNow();
                break;
            }
        }
    }
    /*---------------------------------------*/

    /**
     * Opens a client socket to receive MAC address
     * and gets the IP of the client from the client socket,
     * maps them by storing them in the "clientAddresses" hashMap
     * then broadcasts the hashmap to the peers in the area
     */
    public void getClientAddress(){
        try{

        clientSocket = socket.accept();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        //getting client's IP from the socket
        InetAddress clientIP = clientSocket.getInetAddress();
        //reading client's MAC address
        String clientMAC = bufferedReader.readLine();
        Log.i(TAG,"Client MAC: "+clientMAC+" Client IP: "+clientIP);
        clientsaddresses.put(clientMAC, clientIP);
        //Adding group owner mac and address
        clientsaddresses.put(Globals.MY_MAC,ConnectAndDiscoverService.getGOAddress());
        //Broadcast all addresses evey time the group owner receives a new address
        //so new peers can have the whole list of other connected peers
        broadcastManager.sendAddresses(clientsaddresses);

        bufferedReader.close();
        clientSocket.close();

        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

}
