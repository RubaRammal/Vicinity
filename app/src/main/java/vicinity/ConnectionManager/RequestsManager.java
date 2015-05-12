package vicinity.ConnectionManager;


import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import vicinity.Controller.MainController;
import vicinity.model.Globals;
import vicinity.model.Neighbor;
import vicinity.vicinity.TabsActivity;


/**
 * This class performs send a friend request to a neighbor
 *
 */
public class RequestsManager extends AsyncTask<Neighbor,Void,Boolean>{

    private static final String TAG ="RequestsManager";
    private Socket requestSocket;
    private ObjectOutputStream outToServer;
    private ObjectInputStream inputStream;
    private MainController controller;
    private boolean reply;
    private Neighbor  requestedTo;

    @Override
    protected void onPreExecute(){
    }

    @Override
    protected Boolean doInBackground(Neighbor... param){

        try{
            controller = new MainController(TabsActivity.ctx);
        //Peer
            requestedTo = (Neighbor) param[0];
        //My info
            //Getting current device info to send it as an object of Neighbor in the request
            Neighbor me = WiFiDirectBroadcastReceiver.getMyP2pInfo();

            //Getting neighbor's IP address
            if(UDPpacketListner.doesAddressExist(requestedTo.getDeviceAddress()))
            {   requestedTo.setIpAddress(UDPpacketListner.getPeerAddress(requestedTo.getDeviceAddress()));
                Log.i(TAG, "Sending request to.." + requestedTo.toString());
            }

            //Initializing sockets and streams
            requestSocket = new Socket (requestedTo.getIpAddress(), Globals.REQUEST_PORT);
            outToServer = new ObjectOutputStream(requestSocket.getOutputStream());
            inputStream = new ObjectInputStream(requestSocket.getInputStream());

            //Sending the object
            outToServer.writeObject(me);
            outToServer.flush();
            //Receiving acceptance or rejection
            reply= inputStream.readBoolean();
            Log.i(TAG,"isAccepted: "+reply);

            if(reply){
                Thread chatServerSocket = new ChatServer();
                chatServerSocket.start();
            }




            //Closing sockets and streams
            outToServer.close();
            inputStream.close();
            requestSocket.close();

        }
        catch(NullPointerException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return reply;
    }

    @Override
    protected void onPostExecute (Boolean result){

        //Alert user if request was accepted
        //and update the friends list accordingly
        try{
        controller.alertUserOfRequestReply(result,requestedTo);
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }

    }


}
