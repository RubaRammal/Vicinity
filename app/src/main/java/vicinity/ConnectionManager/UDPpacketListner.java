package vicinity.ConnectionManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.net.InetSocketAddress;
import vicinity.model.Globals;
import vicinity.vicinity.TimelineSectionFragment;
import java.net.SocketAddress;

public class UDPpacketListner extends Service {

    private static final String TAG = "UDPpacketListner";
    DatagramSocket socket;
    Integer port = Globals.SERVER_PORT;

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
                String s = new String(packet.getData(), 0, packet.getLength());
                String senderIP = packet.getAddress().getHostAddress();
                Log.d(TAG, "Received response: "+s+" senderIP: "+senderIP);
                //broadcastIntent(senderIP,s);//-Lama
            }
        } catch (SocketTimeoutException e) {
            Log.d(TAG, "Receive timed out");
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
}
