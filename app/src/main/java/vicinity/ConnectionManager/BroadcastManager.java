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
 * This class extends AsyncTask and performs broadcasting a UDP packet operation in the
 * background, this was implemented as a solution for NetworkOnMainThreadException
 * that occurs when performing network operations on main thread.
 * It sends a comment, post or addresses hashMap to peers in the group
 */
public class BroadcastManager extends AsyncTask <Void, Void, Void> {

    //TODO this class shall be named UDPBroadcastManager

    private static final String TAG = "PostManager";
    private Post post;
    private Comment comment;
    private static final int TIMEOUT_MS = 500;
    DatagramSocket socket;

    private boolean commentFlag = false;
    private boolean postFlag = false;




    /*----------Overridden Methods---------*/
    @Override
    protected void onPreExecute(){


    }

    @Override
    protected Void doInBackground(Void... param){

        try{

            socket = new DatagramSocket(null);
            SocketAddress socketAddr = new InetSocketAddress(Globals.SERVER_PORT);
            socket.setReuseAddress(true);
            socket.setBroadcast(true);
            socket.bind(socketAddr);
            socket.setSoTimeout(TIMEOUT_MS);


            if(postFlag) {
                Log.i(TAG,"inside if, flag= "+postFlag);
                sendPost(post, socket);

            }
            else if (commentFlag){
                Log.i(TAG,"inside else, flag= "+commentFlag);
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
     * Set post from the NewPost class
     * @param post a new post to be broadcasted
     */
    public void setPost(Post post){
        this.post=post;
        postFlag = true;

    }

    /**
     * Sets comment from addComment class
     * @param comment a new comment to be broadcasted
     */
    public void setComment(Comment comment){this.comment = comment;
        commentFlag=true;}

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

    /**
     * Broadacasts a comment of a specified post
     * @param comment Comment object
     * @param socket DatagramSocket
     */
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


    /**
     * Broadcast (MAC,IP) Addresses to peers in local network
     * @param addresses a HashMap containing addresses from group owner
     */
    public void sendAddresses(HashMap addresses){
        try{
            if(!addresses.isEmpty()) {
                Log.i(TAG, "Sending addresses");
                socket.setBroadcast(true);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(addresses);
                byte[] data = outputStream.toByteArray();
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



