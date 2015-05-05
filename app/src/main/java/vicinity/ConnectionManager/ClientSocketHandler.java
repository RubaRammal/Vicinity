
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

    private static final String TAG = "ClientSocketHandler";
    private Handler handler;
    private ChatManager chat;
    private InetAddress mAddress;
    private DataOutputStream toGO;

    public ClientSocketHandler(Handler handler, InetAddress groupOwnerAddress) {
        this.handler = handler;
        this.mAddress = groupOwnerAddress;
    }

    @Override
    public void run() {
        Socket macSocket = new Socket();
        Socket socket = new Socket();
        try {
            /*-----test----*/
            //Writing mac address to group owner:
            if(Globals.MY_MAC!=null){
                //macSocket.bind(null);
                macSocket.connect(new InetSocketAddress(mAddress.getHostAddress(),
                        Globals.ADDRESSES_PORT), 5000);
                toGO = new DataOutputStream(macSocket.getOutputStream());
                toGO.writeBytes(Globals.MY_MAC+'\n');
                toGO.flush();
                toGO.close();
                macSocket.close();
            }

            socket.bind(null);
            socket.connect(new InetSocketAddress(mAddress.getHostAddress(),
                    Globals.SERVER_PORT), 5000);
            Log.d(TAG, "Launching the I/O handler");
            chat = new ChatManager(socket, handler);
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

}
