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
import java.net.InetSocketAddress;
import vicinity.model.Globals;
import vicinity.model.Post;
import vicinity.vicinity.PostListAdapter;
import vicinity.vicinity.TimelineSectionFragment;
import java.net.SocketAddress;
import java.util.ArrayList;

public class UDPpacketListner extends Service {

    private static final String TAG = "UDPpacketListner";
    DatagramSocket socket;
    Integer port = Globals.SERVER_PORT;
    LocalBroadcastManager updateUIThread;


    /*---------Overridden Methods------------*/
    @Override
    public void onCreate(){
        super.onCreate();
        updateUIThread= LocalBroadcastManager.getInstance(this);
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

                    socket = new DatagramSocket(null);
                    socket.setReuseAddress(true);
                    SocketAddress socketAddr = new InetSocketAddress(port);
                    socket.setBroadcast(true);
                    socket.bind(socketAddr);
                    lsnToPostBroadcast(socket);
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
     * Listens to broadcasted posts
     * @param socket
     * @throws IOException
     */
    public void lsnToPostBroadcast(DatagramSocket socket)throws IOException {

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
                Post p = (Post) objectInputStream.readObject();
                Log.i(TAG,"received object: "+p.getPostBody()+" from: "+senderIP+" "+p.getPostedBy()+" posted at: "+p.getPostedAt());

                updateUIPosts(p);
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