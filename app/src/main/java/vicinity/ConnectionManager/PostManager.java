package vicinity.ConnectionManager;
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
import java.util.HashMap;

import vicinity.model.Comment;
import vicinity.model.Globals;
import vicinity.model.Post;

/**
 * This class extends AsyncTask and performs broadcasting a post operation in the
 * background, this was implemented as a solution for NetworkOnMainThreadException
 * that occurs when performing network operations on main thread.
 */
public class PostManager extends AsyncTask <Void, Void, Void> {

    //TODO this class shall be named UDPBroadcastManager

    private static final String TAG = "PostManager";
    private Post post;
    private Comment comment;
    private boolean flag;//to determine if it's a comment or a post
    private static final int TIMEOUT_MS = 500;
    DatagramSocket socket;


    /**
     * Set post from the NewPost class
     * @param post a new post to be broadcasted
     */
    public void setPost(Post post){
        this.post=post;
        flag = true;

    }

    /**
     * Sets comment from addComment class
     * @param comment a new comment to be broadcasted
     */
    public void setComment(Comment comment){this.comment = comment;
    flag=false;}


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
            if(flag == true) {//-Lama
                Log.i(TAG,"inside if, flag= "+flag);
                sendPost(post, socket);
            }
            else{
                Log.i(TAG,"inside else, flag= "+flag);
                sendComment(comment, socket);
            }

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

    public void sendComment(Comment comment, DatagramSocket socket){//-Lama
        try{

            Log.i(TAG,"Sending comment: "+comment);
            socket.setBroadcast(true);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(comment);
            byte [] data = outputStream.toByteArray();

            Log.i(TAG,"socket.getInetAddress();: "+socket.getInetAddress());
            InetAddress broadcastIP = InetAddress.getByName("192.168.49.255");
            DatagramPacket datagramPacket = new DatagramPacket(data, data.length,
                    broadcastIP, Globals.SERVER_PORT);
            socket.send(datagramPacket);
            String senderIP = datagramPacket.getAddress().getHostAddress();
            Log.d(TAG, " senderIP: "+senderIP);

        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    public void sendAdresses(HashMap addresses){
        try{
            if(!addresses.isEmpty()) {
                Log.i(TAG, "Sending addresses");
                socket.setBroadcast(true);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(addresses);
                byte[] data = outputStream.toByteArray();

                Log.i(TAG, "socket.getInetAddress();: " + socket.getInetAddress());
                InetAddress broadcastIP = InetAddress.getByName("192.168.49.255");
                DatagramPacket datagramPacket = new DatagramPacket(data, data.length,
                        broadcastIP, Globals.SERVER_PORT);
                socket.send(datagramPacket);
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

}



