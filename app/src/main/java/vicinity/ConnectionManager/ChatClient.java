package vicinity.ConnectionManager;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import vicinity.model.Globals;
import vicinity.model.VicinityMessage;


/**
 * Created by macproretina on 5/12/15.
 */
public class ChatClient implements Runnable {


    private InetAddress ip;
    private Context ctx;
    private String TAG = "ChatClient";


    private ObjectOutputStream out;


    public ChatClient(Context c, InetAddress ip) {
        this.ip = ip;
        ctx = c;
    }


    public void run() {
        try {

            Socket clientSocket = new Socket();
            Log.i(TAG, "Client socket started...");
            clientSocket.connect(new InetSocketAddress(ip,
                    Globals.CHAT_PORT), 5000);

            Log.i(TAG, "Client connected to ip: " + ip);

            out = new ObjectOutputStream(clientSocket.getOutputStream());


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void write(VicinityMessage msg) {
        try {
            out.writeObject(msg);
        } catch (IOException e) {
            Log.e(TAG, "Exception during write", e);
        }
    }


}

