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
    ArrayList<Post> posts = new ArrayList<>();
    static PostListAdapter postListAdapter;
    LocalBroadcastManager updateUIThread;

    public void onCreate(){
        super.onCreate();
        updateUIThread= LocalBroadcastManager.getInstance(this);
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
                    Log.d(TAG, "in method onStartCommand");
                    e.printStackTrace();
                }
            }


        });
        UDPBroadcastThread.start();
        return START_STICKY;
    }
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

    private void updateUIPosts(Post p) {
        Log.i(TAG,"updateUIPosts");
        Intent intent = new Intent("POST");
        intent.putExtra("NEW_POST", p);
        updateUIThread.sendBroadcast(intent);
    }


    public UDPpacketListner() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void setPostListAdapter(PostListAdapter pla){
        postListAdapter = pla;
    }
}