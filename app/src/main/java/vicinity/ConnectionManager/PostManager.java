package vicinity.ConnectionManager;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import vicinity.model.Globals;


public class PostManager extends AsyncTask <Void, Void, Void> {

    private static final String TAG = "PostManager";
    private Context context;
    private String post;
    WifiManager wifiManager;
    private static final int TIMEOUT_MS = 500;
    DatagramSocket socket;

    public PostManager(Context context){
        this.context=context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

    }
    public void setPost(String post){

        this.post=post;
    }
    @Override
    protected void onPreExecute(){
        Log.i(TAG,"onPreExecute");


    }

    @Override
    protected Void doInBackground(Void... param){
        Log.i(TAG,"doInBackground");
        Log.i(TAG,"Post: "+post);

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
        Log.i(TAG,"onPostExecute");

    }

    public void sendPost(String post, DatagramSocket socket){
        try{

            Log.i(TAG,"Sending post: "+post);
            socket.setBroadcast(true);
            byte[] data = post.getBytes();
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


}



