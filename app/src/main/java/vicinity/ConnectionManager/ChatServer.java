package vicinity.ConnectionManager;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vicinity.model.Globals;

/**
 * A class that implements Runnable.
 * It listens for clients’ requests
 * and executes threads to handle the received packets.
 */
public class ChatServer implements Runnable{

    private final static String TAG ="ChatServer";

    // Manages the execution of ServiceRequest objects
    private ExecutorService executorService = Executors.newFixedThreadPool(20);

    /*---------Overridden Methods------------*/
    @Override
    public void run() {
        try {
            Log.i(TAG, "Starting Server");
            // Initialize a server socket
            ServerSocket serverSocket = new ServerSocket(Globals.CHAT_PORT);

            while(Globals.isChatServerRunning) {
                Log.i(TAG,"Waiting for request");
                try {
                    // Listens for clients' requests
                    Socket s = serverSocket.accept();
                    Log.i(TAG, "Processing request");
                    // Submit a new ServiceRequest with the client socket
                    // to the executeService to manage the thread
                    executorService.submit(new ServiceRequest(s));
                } catch(IOException ioe) {
                    Log.i(TAG, "Error accepting connection");
                    ioe.printStackTrace();
                }
            }
        } catch(IOException e) {
            Log.i(TAG, "Error starting Server on "+Globals.CHAT_PORT);
            e.printStackTrace();
        }
    }

}
