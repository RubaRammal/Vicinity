
package vicinity.ConnectionManager;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

import vicinity.model.Globals;
import vicinity.model.VicinityMessage;


/**
 * This class handles reading and writing of vicinityMessages with socket buffers.
 * Uses a Handler to post vicinityMessages to UI thread for UI updates.
 */
public class ChatManager implements Runnable {

    private Socket socket = null;
    private Handler handler;

    public ChatManager(Socket socket, Handler handler) {
        this.socket = socket;
        this.handler = handler;
    }

    private ObjectInputStream iStream;
    private ObjectOutputStream oStream;
    private static final String TAG = "ChatHandler";
    VicinityMessage vicinityMessage = new VicinityMessage();

    @Override
    public void run() {
        try {

            iStream = new ObjectInputStream(socket.getInputStream());
            oStream = new ObjectOutputStream(socket.getOutputStream());


            handler.obtainMessage(Globals.MY_HANDLE, this)
                    .sendToTarget();


                try {
                    // Read from the InputStream
                    // VicinityMessage is received from one user
                    vicinityMessage = (VicinityMessage) iStream.readObject(); // De-Serialization 

                    // Send the obtained bytes to the UI Activity
                    // VicinityMessage is sent to WiFiServiceDiscovery to be sent to all users
                    Log.d(TAG, vicinityMessage.getMessageBody());
                    handler.obtainMessage(Globals.MESSAGE_READ,
                            1, -1, vicinityMessage).sendToTarget();// not sure about the arg1 and arg2 values!!! - AMAL
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(VicinityMessage vicinityMessage) {
        try {
            oStream.writeObject(vicinityMessage); //Serialization
        } catch (IOException e) {
            Log.e(TAG, "Exception during write", e);
        }
    }

}
