
package vicinity.ConnectionManager;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;


import vicinity.model.Globals;


/**
 * This class is group owner thread that receives IP-MAC addresses
 * from peers in the group and broadcasts them to the group
 */
public class GroupOwnerSocketHandler extends Thread {

    private ServerSocket socket = null;
    private Socket clientSocket = null;
    private static final String TAG = "GroupOwner";
    private UdpBroadcastManager udpBroadcastManager = new UdpBroadcastManager();
    private HashMap<String, InetAddress> clientsaddresses = new HashMap<>();


    /**
     * Public constructor
     * @throws IOException
     */
    public GroupOwnerSocketHandler() throws IOException {
        udpBroadcastManager.execute();

        try {
          socket = new ServerSocket(Globals.SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

    }

    /*---------Overridden Methods------------*/
    @Override
    public void run() {

        while (true) {
            getClientAddress();

        }}


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
        udpBroadcastManager.sendAddresses(clientsaddresses);

        bufferedReader.close();
        clientSocket.close();

        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

}
