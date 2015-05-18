
package vicinity.ConnectionManager;



import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import vicinity.model.Globals;

/**
 * This class is a client thread that send IP and MAC addresses of the client
 * to the group owner once it is connected to a network.
 */
public class ClientSocketHandler extends Thread {

    private InetAddress mAddress;

    public ClientSocketHandler(InetAddress groupOwnerAddress) {
        this.mAddress = groupOwnerAddress;
    }

    /*---------Overridden Methods------------*/
    @Override
    public void run() {
            sendMyMAC();
    }

    /**
     * Sends MAC address to group owner
     * when first connected to a group
     */
    private void sendMyMAC(){
        try{
            Socket macSocket = new Socket();
            DataOutputStream toGO;
            //Writing mac address to group owner:
            if(Globals.MY_MAC!=null){
                //macSocket.bind(null);
                macSocket.connect(new InetSocketAddress(mAddress.getHostAddress(),
                        Globals.SERVER_PORT), 5000);
                toGO = new DataOutputStream(macSocket.getOutputStream());
                toGO.writeBytes(Globals.MY_MAC+'\n');
                toGO.flush();
                toGO.close();
                macSocket.close();
            }}
        catch(IOException e){
            e.printStackTrace();
        }

    }


}
