
package vicinity.ConnectionManager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import vicinity.model.Globals;

/**
 * The implementation of a ServerSocket handler. This is used by the wifi p2p
 * group owner.
 * the group owner acts as a server
 */


public class GroupOwnerSocketHandler extends Thread {

    ServerSocket socket = null;
    ServerSocket addressesSocket = null;
    Socket clientSocket = null;
    private final int THREAD_COUNT = 10;
    private Handler handler;
    private static final String TAG = "GroupOwnerSocketHandler";
    private BufferedReader bufferedReader;

    public GroupOwnerSocketHandler(Handler handler) throws IOException {
        try {
            socket = new ServerSocket(Globals.SERVER_PORT);

            /*---test---*/
            //another socket with a different port

            this.handler = handler;
            Log.d(TAG, "Socket Started");
            //getClientsAddresses();
        } catch (IOException e) {
            e.printStackTrace();
            pool.shutdownNow();
            throw e;
        }

    }
    /*
    public void getClientsAddresses(){
        // Collect client ip's -Lama

        Runnable r = new Runnable() {
            public void run() {
                Looper.prepare();
                try {
                    //clients.clear();
                    while (true)

                    {
                        clientSocket = addressesSocket.accept();
                        BufferedReader fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        InetAddress clientIP = clientSocket.getInetAddress();
                        String clientMAC = fromClient.readLine();
                        Log.i(TAG,"Client's IP: "+clientIP+" Client's MAC: "+clientMAC);

                       // clients.add(clientSocket.getInetAddress());
                        //clientSocket.close();
                        //Log.d(TAG, "client id" + clients.toString());
                    }
                }catch (IOException e){e.printStackTrace();}
            }
        };
        Thread t = new Thread(r);
        t.start();
    }
    */
    /**
     * A ThreadPool for client sockets.
     */
    private final ThreadPoolExecutor pool = new ThreadPoolExecutor(
            THREAD_COUNT, THREAD_COUNT, 10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    @Override
    public void run() {
        while (true) {
            try {
                // A blocking operation. Initiate a ChatManager instance when
                // there is a new connection
                /*----------------*/
                addressesSocket = new ServerSocket(Globals.ADDRESSES_PORT);
                clientSocket = addressesSocket.accept();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                InetAddress clientIP = clientSocket.getInetAddress();
                String clientMAC = bufferedReader.readLine();
                Log.i(TAG,"Client's IP: "+clientIP+" Client's MAC: "+clientMAC);
                /*----------------*/

                pool.execute(new ChatManager(socket.accept(), handler));
                Log.d(TAG, "Launching the I/O handler");

            } catch (IOException e) {
                try {
                    if (socket != null && !socket.isClosed())
                        socket.close();
                } catch (IOException ioe) {

                }
                e.printStackTrace();
                pool.shutdownNow();
                break;
            }
        }
    }

}
