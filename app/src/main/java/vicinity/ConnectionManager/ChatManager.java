
package vicinity.ConnectionManager;

import android.os.Handler;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import vicinity.model.Globals;


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

    private InputStream iStream;
    private OutputStream oStream;
    private static final String TAG = "ChatHandler";

    @Override
    public void run() {
        try {

            iStream = socket.getInputStream();
            oStream = socket.getOutputStream();
            byte[] buffer = new byte[6900000];
            int bytes;

            handler.obtainMessage(Globals.MY_HANDLE, this)
                    .sendToTarget();

            while (true) {
                try {
                    // Read from the InputStream
                    // VicinityMessage is received from one user
                    bytes = iStream.read(buffer);
                    if (bytes == -1) { // I don't know what -1 means
                        break;
                    }

                    // Send the obtained bytes to the UI Activity
                    // VicinityMessage is sent to WiFiServiceDiscovery to be sent to all users
                    Log.d(TAG, "Rec:" + String.valueOf(buffer));
                    handler.obtainMessage(Globals.MESSAGE_READ,
                            bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                }
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

    public void write(byte[] buffer) {
        try {
            oStream.write(buffer);
        } catch (IOException e) {
            Log.e(TAG, "Exception during write", e);
        }
    }

}
