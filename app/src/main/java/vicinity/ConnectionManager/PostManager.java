package vicinity.ConnectionManager;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import vicinity.model.Globals;


public class PostManager extends AsyncTask <Void, Void, Void> {

    private static final String TAG = "PostManager";
    private Context context;
    private String post;
    WifiManager wifiManager;
    DatagramSocket datagramSocket;

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
        sendPost(post);

        //JUST TO CHECK IF IT IS WORKING
        try{
        lsnToPostBroadcast();}
        catch (IOException e){
            e.printStackTrace();
        }
        Void s = null;

        return s;
    }

    @Override
    protected void onPostExecute (Void result){
        Log.i(TAG,"onPostExecute");

    }

    public void sendPost(String post){
        try{
        Log.i(TAG,"Sending post: "+post);
        datagramSocket = new DatagramSocket(Globals.SERVER_PORT);
        datagramSocket.setBroadcast(true);
        byte[] data = post.getBytes();
        DatagramPacket datagramPacket = new DatagramPacket(data, data.length,
                getBroadcastAddress(), Globals.SERVER_PORT);
        datagramSocket.send(datagramPacket);}
        catch(IOException e){
            e.printStackTrace();
        }
    }
    /**
     * @return broadcast address
     * @throws IOException
     */
    public InetAddress getBroadcastAddress() throws IOException {
        DhcpInfo dhcp = wifiManager.getDhcpInfo();
        if (dhcp == null) {
            Log.d(TAG, "Could not get dhcp info");
            return null;
        }

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        InetAddress bcAddress= InetAddress.getByAddress(quads);
        Log.i(TAG,"Broadcast Address: "+bcAddress);
        return bcAddress;

    }

    public void lsnToPostBroadcast()throws IOException{

            byte[] buf = new byte[1024];
            try {
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    datagramSocket.receive(packet);
                    String s = new String(packet.getData(), 0, packet.getLength());
                    Log.d(TAG, "Received response " + s);
                }
            } catch (SocketTimeoutException e) {
                Log.d(TAG, "Receive timed out");
            }
        }


    }




