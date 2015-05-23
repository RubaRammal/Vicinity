
package vicinity.ConnectionManager;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import vicinity.model.Globals;


/**
 * This class is group owner thread that receives IP-MAC addresses
 * from peers in the group and broadcasts them to the group
 */
public class GroupOwnerSocketHandler extends Thread {

    private ServerSocket socket = null;
    private static final String TAG = "GroupOwner";
    private UdpBroadcastManager udpBroadcastManager = new UdpBroadcastManager();
    private HashMap<String, InetAddress> clientsaddresses = new HashMap<>();
    private ExecutorService executor = Executors.newFixedThreadPool(30);


    /**
     * Public constructor
     * @throws IOException
     */
    public GroupOwnerSocketHandler() throws IOException {
        udpBroadcastManager.execute();
    }

    /*---------Overridden Methods------------*/
    @Override
    public void run() {

        try {
            Log.i(TAG,"Group owner server started...");
            socket = new ServerSocket();
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(Globals.SERVER_PORT));


            while (Globals.isGroupOwnerRunning){
                Socket clientSocket = socket.accept();
                executor.submit(new GroupOwnerSupporter(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Inner class for GroupOwnerSocketHandler
     * to handle client's thread and get it addresses
     */
    public class GroupOwnerSupporter implements Runnable{

        private Socket clientSocket2=null;

        public GroupOwnerSupporter(Socket s){
            this.clientSocket2=s;
        }

        @Override
        public void run() {
            Log.i(TAG,"Group owner supporter is processing request...");
            getClientAddress();

        }


        /**
         * Opens a client socket to receive MAC address
         * and gets the IP of the client from the client socket,
         * maps them by storing them in the "clientAddresses" hashMap
         * then broadcasts the hashmap to the peers in the area
         */
        public void getClientAddress(){
            try{


                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket2.getInputStream()));
                //getting client's IP from the socket
                InetAddress clientIP = clientSocket2.getInetAddress();
                //reading client's MAC address
                String clientMAC = bufferedReader.readLine();
                Log.i(TAG,"Client MAC: "+clientMAC+" Client IP: "+clientIP);
                clientsaddresses.put(clientMAC, clientIP);
                //Adding group owner mac and address
                clientsaddresses.put(Globals.MY_MAC,ConnectAndDiscoverService.getGOAddress());
                //Broadcast all addresses evey time the group owner receives a new address
                //so new peers can have the whole list of other connected peers
                udpBroadcastManager.sendAddresses(clientsaddresses);

                bufferedReader.close();
                clientSocket2.close();

            }
            catch(IOException e){
                e.printStackTrace();
            }
        }

    }

}
