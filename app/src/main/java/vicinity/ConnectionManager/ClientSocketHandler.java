
package vicinity.ConnectionManager;

import android.os.Handler;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import vicinity.model.Globals;


public class ClientSocketHandler extends Thread {

    private static final String TAG = "ClientSocket";
    private Handler handler;
    private ChatManager chat;
    private InetAddress mAddress;

    public ClientSocketHandler(InetAddress groupOwnerAddress) {
        this.mAddress = groupOwnerAddress;
    }

    /*---------Overridden Methods------------*/
    @Override
    public void run() {

        Socket socket = new Socket();
        try {

            sendMyMAC();
            socket.bind(null);
            socket.connect(new InetSocketAddress(mAddress.getHostAddress(),
                    Globals.SERVER_PORT), 5000);
            chat = new ChatManager(socket);

            new Thread(chat).start();

        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }
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
