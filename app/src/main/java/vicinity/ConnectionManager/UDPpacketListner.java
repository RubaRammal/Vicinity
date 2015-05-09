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
 */
public class UDPpacketListner extends Service {

    private static final String TAG = "UDPpacketListner";
    //private DatagramSocket socket;

    //Server port
    private Integer port = Globals.SERVER_PORT;
    //To update timeline with posts
    private LocalBroadcastManager updateUIThread;
    //HashMap that stores received addresses pairs
    private HashMap<String, InetAddress> addressHashMap;

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
     * @param socket
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

                }
                else if(obj instanceof HashMap){
                    Log.i(TAG,"Received addresses hashmap");
                    HashMap <String, InetAddress> receivedAddresses = (HashMap<String,InetAddress>) obj;
                    //Update existing hashmap with new addresses if there is any
                    for (String key : receivedAddresses.keySet()) {
                        Log.i(TAG,"MAC: "+key+" IP: "+receivedAddresses.get(key));

                        if (addressHashMap.isEmpty()){
                            //Add it to the addresses cache if the addresses don't already exist.
                            addressHashMap.put(key,receivedAddresses.get(key));
                            Log.i(TAG,"Hashmap is empty.. MAC: "+key+" IP: "+addressHashMap.get(key));
                        }
                        else if (!addressHashMap.containsValue(receivedAddresses.get(key)))
                        {
                            addressHashMap.put(key,receivedAddresses.get(key));
                            Log.i(TAG,"Update hashmap... MAC: "+key+" IP: "+addressHashMap.get(key));
                        }
                    }
                    /*
                    for(String key : Globals.peersAddresses.keySet()) {
                        Log.i(TAG, "Peers addresses " + key + " " + Globals.peersAddresses.get(key));
                    }*/

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






}