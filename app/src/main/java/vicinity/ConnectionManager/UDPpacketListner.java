package vicinity.ConnectionManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import vicinity.model.Comment;
import vicinity.model.Globals;
import vicinity.model.Post;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;

/**
 * A Service that listens for incoming UDP broadcasts
 * it listens to posts, comments and addresses broadcasts
 */
public class UDPpacketListner extends Service {

    private static final String TAG = "UDPpacketListner";
    private Integer port = Globals.SERVER_PORT;    //Server port
    private LocalBroadcastManager updateUIThread;     //To update timeline with posts
    private static HashMap<String, InetAddress> addressHashMap;    //HashMap that stores received addresses pairs


    /*---------Overridden Methods------------*/
    @Override
    public void onCreate(){
        super.onCreate();
        updateUIThread= LocalBroadcastManager.getInstance(this);
        addressHashMap = new HashMap<>();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Thread UDPBroadcastThread;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        UDPBroadcastThread = new Thread(new Runnable() {
            public void run() {
                try {
                    DatagramChannel channel = DatagramChannel.open();
                    DatagramSocket socket = channel.socket();

                    //socket = new DatagramSocket(port);
                    socket.setReuseAddress(true);
                    SocketAddress socketAddr = new InetSocketAddress(port);
                    socket.setBroadcast(true);
                    socket.bind(socketAddr);
                    lsnToBroadcast(socket);
                }catch(IOException e){
                    e.printStackTrace();
                }
            }

        });
        UDPBroadcastThread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.i(TAG,"UDP Service destroyed.");
    }

    /**
     * Listens to UDP broadcasts
     * @param socket DatagramSocket
     * @throws IOException
     */
    public void lsnToBroadcast(DatagramSocket socket)throws IOException {

        byte[] buf = new byte[69000];
        try {
            while (true) {

                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.setBroadcast(true);
                socket.receive(packet);
                byte[] data = packet.getData();
                ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                String senderIP = packet.getAddress().getHostAddress();
                //Receiving an object
                Object obj = objectInputStream.readObject();

                /*-----Determine if incoming udp packet contains Post, Comment or HasMap of addresses----*/
                if (obj instanceof Post){
                    Post p = (Post) obj;
                    Log.i(TAG,"received Post object: "+p.getPostBody()+" from: "+senderIP+" "+p.getPostedBy()+" posted at: "+p.getPostedAt());//-Lama
                    updateUIPosts(p);

                }

                else if (obj instanceof Comment){
                    Comment c = (Comment) obj;
                    Log.i(TAG,"received Comment object: "+c.getCommentBody()+" from: "+senderIP+" "+c.getCommentedBy());
                    updateUIComments(c);

                }
                else if(obj instanceof HashMap){
                    Log.i(TAG,"Received addresses hashmap");
                    HashMap <String, InetAddress> receivedAddresses = (HashMap<String,InetAddress>) obj;
                    for (String key : receivedAddresses.keySet()) {
                        Log.i(TAG,"MAC: "+key+" IP: "+receivedAddresses.get(key));

                        //Update current addresses cache (HashMap) with new addresses
                        if (!addressHashMap.containsValue(receivedAddresses.get(key)) && !key.equals(Globals.MY_MAC))
                        {
                            addressHashMap.put(key,receivedAddresses.get(key));
                            Log.i(TAG,"Update hashmap... MAC: "+key+" IP: "+addressHashMap.get(key));
                        }
                        //if it was my IP-MAC pair then update my IP address only
                        else if(key.equals(Globals.MY_MAC)){
                            Log.i(TAG,"This is my MAC address");
                            if(Globals.MY_IP!=null)
                                Globals.MY_IP= addressHashMap.get(Globals.MY_MAC);
                        }

                    }
                }



            }
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Sends intent to the broadcast receiver in Timeline
     * to display Post
     * @param p a new post to update UI (Timeline) thread
     */
    private void updateUIPosts(Post p) {
        Log.i(TAG,"updateUIPosts");
        Intent intent = new Intent("POST");
        intent.putExtra("NEW_POST", p);
        updateUIThread.sendBroadcast(intent);
    }

    /**
     * Sends intent to the broadcast receiver in Timeline
     * and it set comments to post
     * @param c a new comment to update UI (Timeline) thread
     */
    private void updateUIComments(Comment c) {
        Log.i(TAG,"update comments");
        Intent intent = new Intent("COMMENT");
        intent.putExtra("NEW_COMMENT", c);
        updateUIThread.sendBroadcast(intent);
    }

    /**
     * Retrieves the peer's IP from given key (MAC)
     * @param MAC the peer's MAC address
     * @return The mapped IP address of the peer
     */
    public static InetAddress getPeerAddress(String MAC){
        if(addressHashMap.containsKey(MAC)){
            return addressHashMap.get(MAC);
        }
        return null;
    }


    /**
     * Checks if the given MAC address is stored
     * and mapped to the IP address of the peer in the addresses cache
     * @param MAC String contains MAC address of the peer
     * @return a boolean that is true if the address exists, false otherwise
     */
    public static boolean doesAddressExist(String MAC){
        Log.i("Request","Received key= "+MAC);
        return addressHashMap.containsKey(MAC);
    }



}