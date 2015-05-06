package vicinity.ConnectionManager;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import vicinity.model.Globals;
import vicinity.model.Post;

/**
 * This class extends AsyncTask and performs broadcasting a post operation in the
 * background, this was implemented as a solution for NetworkOnMainThreadException
 * that occurs when performing network operations on main thread.
 */
public class PostManager extends AsyncTask <Void, Void, Void> {

    private static final String TAG = "PostManager";
    private Post post;
    private static final int TIMEOUT_MS = 500;
    DatagramSocket socket;


    /**
     * Set post from the NewPost class
     * @param post a new post to be broadcasted
     */
    public void setPost(Post post){
        this.post=post;
    }

    @Override
    protected void onPreExecute(){


    }

    @Override
    protected Void doInBackground(Void... param){
        Log.i(TAG,"doInBackground() -> Post: "+post);

        try{

            socket = new DatagramSocket(null);
            SocketAddress socketAddr = new InetSocketAddress(Globals.SERVER_PORT);
            socket.setReuseAddress(true);
            socket.setBroadcast(true);
            socket.bind(socketAddr);
            socket.setSoTimeout(TIMEOUT_MS);
            sendPost(post,socket);

        }
        catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute (Void result){

    }

    /**
     * Broadcasts a new post to the broadcast address 192.168.49.255
     * @param post a new post to be broadcasted
     * @param socket DatagramSocket
     */
    public void sendPost(Post post, DatagramSocket socket){
        try{
            socket.setBroadcast(true);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(post);
            byte [] data = outputStream.toByteArray();
            InetAddress broadcastIP = InetAddress.getByName("192.168.49.255");
            DatagramPacket datagramPacket = new DatagramPacket(data, data.length,
                    broadcastIP, Globals.SERVER_PORT);
            socket.send(datagramPacket);
            String senderIP = datagramPacket.getAddress().getHostAddress();
            Log.d(TAG, "Sending post: "+post+" senderIP: "+senderIP);

        }
        catch(IOException e){
            e.printStackTrace();
        }
    }


}



