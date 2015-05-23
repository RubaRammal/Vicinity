package vicinity.ConnectionManager;

import android.util.Log;

import com.google.common.net.InetAddresses;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import vicinity.model.Globals;
import vicinity.model.VicinityMessage;


/**
 * A class that implements Runnable.
 * It connects to a server and initializes
 * an ObjectOutputStream in order to send packets.
 */
public class ChatClient implements Runnable {


    private String TAG = "ChatClient";
    // Stream to send the message to the server
    private ObjectOutputStream out;
    // IP address of the server
    private InetAddress ip;


    /**
     * Public constructor
     * Initializes the IP address
     */
    public ChatClient(String ip)
    {
        this.ip = InetAddresses.forString(ip);
    }

    /*---------Overridden Methods------------*/
    @Override
    public void run() {
        try {

            // Initialize client socket
            Socket clientSocket = new Socket();

            Log.i(TAG, "Client socket started..." + ip);

            // Connect to server
            clientSocket.connect(new InetSocketAddress(ip,
                    Globals.CHAT_PORT), 5000);

            Log.i(TAG, "Client connected to ip: " + ip);

            // Initialize object output stream to send messages
            out = new ObjectOutputStream(clientSocket.getOutputStream());


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * Sends a message to the server socket it is connected with
     * through an ObjectOutputStream.
     * @param message A to-be deleted Message's ID.
     */
    public void write(VicinityMessage message)
    {
        try {
            out.writeObject(message);
        } catch (IOException e) {
            Log.e(TAG, "Exception during write", e);
        }
    }


}

