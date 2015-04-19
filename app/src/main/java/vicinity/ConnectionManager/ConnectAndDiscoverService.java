package vicinity.ConnectionManager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.net.wifi.p2p.WifiP2pManager;
import android.content.BroadcastReceiver;
import android.util.Log;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import vicinity.Controller.MainController;
import vicinity.model.DBHandler;
import vicinity.model.Globals;
import vicinity.model.VicinityMessage;
import vicinity.vicinity.ChatActivity;
import vicinity.vicinity.FriendListAdapter;
import vicinity.vicinity.NeighborListAdapter;
import vicinity.vicinity.NeighborSectionFragment.DeviceClickListener;
import vicinity.vicinity.R;
import vicinity.vicinity.TabsActivity;


/**
 * ConnectAndDiscover Service starts running with the app and handles service discovery and
 * WiFi P2P connection.
 */

public class ConnectAndDiscoverService extends Service
        implements  WifiP2pManager.ConnectionInfoListener, DeviceClickListener{


    public final String TAG = "ConService";
    static public Context ctx;
    private WifiP2pManager manager;
    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private BroadcastReceiver receiver = null;
    private WifiP2pDnsSdServiceRequest serviceRequest;
    public static ArrayList<WiFiP2pService> neighbors = new ArrayList<WiFiP2pService>();
    public static ArrayList<WiFiP2pService> friends = new ArrayList<WiFiP2pService>();
    public static NeighborListAdapter neighborListAdapter;
    public static FriendListAdapter friendListAdapter;
    public MainController controller;




    /******************Overridden Methods******************************/
    @Override
    public void onCreate(){
        Log.i(TAG,"Service started: "+ Globals.SERVICE_NAME);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        ctx= ConnectAndDiscoverService.this;
        controller = new MainController(ctx);
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        receiver = new WiFiDirectBroadcastReceiver(manager,channel,ctx);
        registerReceiver(receiver,intentFilter);
        //Changing the username depending on the one in the db
        try {
            changeDeviceName(controller.retrieveCurrentUsername());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        startRegistrationAndDiscovery();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onDestroy(){
        Log.i(TAG,"Service destroyed");
        unregisterReceiver(receiver);

        /***TEST***/
        disconnectPeers();
        DBHandler.deleteDatabase();

    }

    /************************************************************/


    /**
     * This method adds _vicinityapp local service to the network
     * then calls discoverService()
     */
    private void startRegistrationAndDiscovery() {
        Log.i(TAG,"startRegistrationAndDiscovery");
        Map<String, String> record = new HashMap<String, String>();
        record.put(Globals.TXTRECORD_PROP_AVAILABLE, "visible");
        WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(Globals.SERVICE_NAME, Globals.SERVICE_REG_TYPE, record);
        manager.addLocalService(channel, service, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.i(TAG,"_vicinityapp Service registered");
                discoverService();
            }
            @Override
            public void onFailure(int error) {
                Log.i(TAG,"Failed to add a service");
            }
        });
    }//end

    /**
     *First discoverService() registers listeners for DNS-SD services
     * Then it creates a service discovery request and initiates service discovery
     */
    private void discoverService() {
        Log.i(TAG,"discoverService");
        manager.setDnsSdResponseListeners(channel,
                new WifiP2pManager.DnsSdServiceResponseListener() {
                    @Override
                    public void onDnsSdServiceAvailable(String instanceName,
                                                        String registrationType, WifiP2pDevice srcDevice) {
                        Log.i(TAG,"Instance name "+instanceName+" Reg type: "+registrationType);
                        if (instanceName.equals(Globals.SERVICE_NAME)) {

                                WiFiP2pService service = new WiFiP2pService(srcDevice);
                                service.setServiceRegistrationType(registrationType);
                                Log.i(TAG, "Name: " + service.getInstanceName() + " Address: " + service.getDeviceAddress());
                                Log.i(TAG, "is this my friend? "+controller.isThisMyFriend("3a:aa:3c:64:08:b0"));
                                if(controller.isThisMyFriend(srcDevice.deviceAddress))
                                {
                                  friends.add(service);
                                  friendListAdapter.setServices(friends);
                                    friendListAdapter.notifyDataSetChanged();

                                }
                                else{
                                 neighbors.add(service);
                                 neighborListAdapter.setServices(neighbors);
                                 neighborListAdapter.notifyDataSetChanged();

                                }

                        }
                    }
                }, new WifiP2pManager.DnsSdTxtRecordListener() {

                    /*
                     * A new TXT record is available. Pick up the advertised
                     * buddy name.
                     */
                    @Override
                    public void onDnsSdTxtRecordAvailable(
                            String fullDomainName, Map<String, String> record,
                            WifiP2pDevice device) {
                        Log.d(TAG,
                                device.deviceName + " is "
                                        + record.get(Globals.TXTRECORD_PROP_AVAILABLE));


                    }
                });



        //1. Add a service discovery request: addServiceRequest()
        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        manager.addServiceRequest(channel, serviceRequest,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.i(TAG,"Added service discovery request");
                    }
                    @Override
                    public void onFailure(int arg0) {
                        Log.i(TAG,"Failed adding service discovery request");
                    }
                });
        //2. Initiating service discovery.
        manager.discoverServices(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.i(TAG, "Service discovery initiated");
            }

            @Override
            public void onFailure(int arg0) {
                Log.i(TAG, "Service discovery failed");

            }
        });

    }//end of discoverServices



    /**
     * This method connects peers with each other
     * @param service the service you want to connect to
     */
    @Override
    public void connectP2p(WiFiP2pService service) {
        Log.i(TAG,"connectP2P");

        //Wi-Fi P2p configuration for setting up a connection
        WifiP2pConfig config = new WifiP2pConfig();

        config.deviceAddress = service.getDevice().deviceAddress;//device unique MAC address
        final String name=service.getDevice().deviceName;//Device name
        config.wps.setup = WpsInfo.PBC;

        if (serviceRequest != null)
            manager.removeServiceRequest(channel, serviceRequest,
                    new WifiP2pManager.ActionListener() {

                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onFailure(int arg0) {
                        }
                    });

            manager.connect(channel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.i(TAG,"Connecting to "+ name );
            }

            @Override
            public void onFailure(int errorCode) {
                Log.i(TAG,"Failed connecting to service");
            }
        });
    }



    /**
     * After connecting to a P2P group this method is invoked
     * @param p2pInfo
     */
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {
        Log.i(TAG,"onConnectionAvailable");
        Thread handler = null;

         /*
         * The group owner accepts connections using a server socket and then spawns a
         * client socket for every client. This is handled by {@code
         * GroupOwnerSocketHandler}
         */

        try {
        if (p2pInfo.isGroupOwner) {
            Log.i(TAG, "Connected as group owner");

                handler = new GroupOwnerSocketHandler(
                       ChatActivity.handler);
                handler.start();
        }

        else {
            Log.d(TAG, "Connected as peer");

            Thread.sleep(1000);
            handler = new ClientSocketHandler(
                    ChatActivity.handler,
                    p2pInfo.groupOwnerAddress);
            handler.start();
        }
        }catch (IOException e) {
            Log.d(TAG,"Failed to create a server thread - " + e.getMessage());
            return;
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }

        //Starting a new chat activity with a connected peer.
        startChatting();
    }

    public void startChatting(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName cn = new ComponentName(this, ChatActivity.class);
        intent.setComponent(cn);
        startActivity(intent);

    }
    /**
     * This method changes the original device name
     * to the user's username
     * @param username registered username
     */
    public void changeDeviceName(final String username){

        try{
            Log.i(TAG,"Changing name!!");

            Method m = manager.getClass().getMethod("setDeviceName",new Class[] { WifiP2pManager.Channel.class, String.class,
                            WifiP2pManager.ActionListener.class });

            m.invoke(manager,channel, username, new WifiP2pManager.ActionListener() {
                public void onSuccess() {
                    Log.i(TAG,"Device changed name to "+username);
                }

                public void onFailure(int reason) {
                    //Code to be done while name change Fails
                }
            });}
        catch(IllegalAccessException e){
            e.printStackTrace();
        }
        catch(InvocationTargetException e){
            e.printStackTrace();

        }
        catch (NoSuchMethodException e){
            e.printStackTrace();
        }



    }

    /**
     * Disconnects peers
     */
    public void disconnectPeers(){
        if (manager != null && channel != null) {
            manager.removeGroup(channel, new WifiP2pManager.ActionListener() {

                @Override
                public void onFailure(int reasonCode) {
                    Log.i(TAG, "Disconnect failed. Reason :" + reasonCode);
                }

                @Override
                public void onSuccess() {
                    Log.i(TAG,"Peers disconnected");
                }

            });
        }

    }


    static public void setNAdapter(NeighborListAdapter nAdapter){
        neighborListAdapter = nAdapter;
    }
    static public void setFAdapter(FriendListAdapter fAdapter){
        friendListAdapter = fAdapter;
    }



}
