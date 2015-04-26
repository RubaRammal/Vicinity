package vicinity.ConnectionManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
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

        byte[] buf = new byte[1024];
        try {
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.setBroadcast(true);
                socket.receive(packet);
                byte[] data = packet.getData();
                //TESSTTT
                ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                String senderIP = packet.getAddress().getHostAddress();
                Post p = (Post) objectInputStream.readObject();
                Log.i(TAG,"received object: "+p.getPostBody()+" from: "+senderIP+" "+p.getPostedBy()+" posted at: "+p.getPostedAt());

                Boolean isPosted = true;

                /*for(int i=0; i<posts.size(); i++){
                    if(!(p.getPostBody().equals(posts.get(i).getPostBody())) &&
                            !(p.getPostedBy().equals(posts.get(i).getPostedBy()))){
                        isPosted = false;
                    }
                    else{
                        isPosted = true;
                    }
                }

                if(isPosted)*/

                posts.add(p);

                postListAdapter.updatePosts(posts);


                //postListAdapter.addPost(p);

                //String s = new String(packet.getData(), 0, packet.getLength());
                // String senderIP = packet.getAddress().getHostAddress();
                // Log.d(TAG, "Received response: "+s+" senderIP: "+senderIP);
                // postListAdapter.addPost(new Post(new User(senderIP),s));
                //broadcastIntent(senderIP,s);//-Lama
                //TimelineSectionFragment.postToTimeline(new Post(new User(senderIP),s));
            }
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void broadcastIntent(String senderIP, String message) {
        Intent intent = new Intent(this,TimelineSectionFragment.class);
        intent.putExtra("sender", senderIP);
        intent.putExtra("message", message);
        sendBroadcast(intent);
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