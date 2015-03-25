package vicinity.vicinity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Handler;
import android.net.wifi.p2p.WifiP2pManager;
import android.content.BroadcastReceiver;
import android.os.Message;
import android.util.Log;
import android.net.wifi.p2p.WifiP2pManager.Channel;


import java.io.IOException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import vicinity.vicinity.ChatActivity.MessageTarget;
import vicinity.vicinity.NeighborSectionFragment.DeviceClickListener;


import vicinity.model.VicinityMessage;



public class ConnectAndDiscoverService extends Service
        implements Handler.Callback, WifiP2pManager.ConnectionInfoListener, DeviceClickListener{


    public final String TAG = "SERVICE";
    static public Context ctx;
    /*
     *Service attributes
     */
    public static final String TXTRECORD_PROP_AVAILABLE = "available";
    //Our service's name and protocol
    public static final String SERVICE_NAME = "_VicinityApp";
    public static final String SERVICE_REG_TYPE = "_presence._tcp";
    static final int SERVER_PORT = 4142;

    public static final int MESSAGE_READ = 0x400 + 1;
    public static final int MY_HANDLE = 0x400 + 2;
    private WifiP2pManager manager;

    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private BroadcastReceiver receiver = null;
    private WifiP2pDnsSdServiceRequest serviceRequest;

    private Handler handler = new Handler(this);
    private NeighborSectionFragment neighborSectionFragment;
    private VicinityMessage message;
    private ChatActivity chat;

    public Handler getHandler(){
        return this.handler;
    }
    public void setHandler(Handler handler) {
        this.handler = handler;
    }
    public static ArrayList<WiFiP2pService> neighbors = new ArrayList<WiFiP2pService>();
    static public NeighborListAdapter neighborListAdapter;

    public ConnectAndDiscoverService() {

    }
    /******************BroadcastReceiver******************************/
    private final BroadcastReceiver broadcastReceiver= new BroadcastReceiver() {
        private String TAG = "BCReceiver";
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG,"onReceive" );


            if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

                if (manager == null) {
                    return;
                }
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

                if (networkInfo.isConnected()) {

                    // we are connected with the other device, request connection
                    // info to find group owner IP
                    Log.d(TAG,"Connected to p2p network. Requesting network details...");
                    Log.d(TAG,"Network info: "+networkInfo.toString());
                    manager.requestConnectionInfo(channel,(WifiP2pManager.ConnectionInfoListener) context);
                } else {
                    Log.d(TAG,"NOT Connected to P2P network!");
                    // It's a disconnect
                }
            }
            else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
                    .equals(action)) {
                Log.i(TAG,"WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
                WifiP2pDevice device = (WifiP2pDevice) intent
                        .getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                Log.d(TAG, "Device status -" + device.status);

            }
            else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                Log.i(TAG,"WIFI_P2P_PEERS_CHANGED_ACTION");
                // The peer list has changed!

            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                Log.i(TAG,"WIFI_P2P_CONNECTION_CHANGED_ACTION");
                // Connection state changed!
            }
        }
    };

    /*****************************************************************/


    /******************Overridden Methods******************************/
    @Override
    public void onCreate(){
        Log.i(TAG,"Service started");
        Log.i(TAG,"Our service is: "+SERVICE_NAME);
        ctx= ConnectAndDiscoverService.this;

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter
                .addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter
                .addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        //Connecting WiFiBroadcastReceiver
        //receiver = new WiFiDirectBroadcastReceiver(manager,channel,ctx);
        registerReceiver(broadcastReceiver,intentFilter);

        changeDeviceName("Heba");
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
       unregisterReceiver(broadcastReceiver);
    }
    /************************************************************/

    /**
     * This method adds _vicinityapp local service to the network
     * then calls discoverService()
     */
    private void startRegistrationAndDiscovery() {
        Log.i(TAG,"startRegistrationAndDiscovery");

        Map<String, String> record = new HashMap<String, String>();
        record.put(TXTRECORD_PROP_AVAILABLE, "visible");
        //WifiP2pDnsSdServiceInfo is A class for storing Bonjour service information that is advertised over a Wi-Fi peer-to-peer setup.
        WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(SERVICE_NAME, SERVICE_REG_TYPE, record);

        //addLocalService Registers our service as a local service in order to be discovered.
        manager.addLocalService(channel, service, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //What to do if our service got advertised in the local network
                Log.i(TAG,"_vicinityapp Service registered");
            }

            @Override
            public void onFailure(int error) {
                Log.i(TAG,"Failed to add a service");
            }
        });

        discoverService();

    }

    /**
     *First discoverService() registers listeners for DNS-SD services
     * Then it creates a service discovery request and initiates service discovery
     */
    private void discoverService() {
        Log.i(TAG,"discoverService");
        //setDnsSdResponseListeners() Registers a callback to be invoked on receiving Bonjour service discovery response.
        manager.setDnsSdResponseListeners(channel,
                new WifiP2pManager.DnsSdServiceResponseListener() {
                    @Override
                    public void onDnsSdServiceAvailable(String instanceName,
                                                        String registrationType, WifiP2pDevice srcDevice) {

                        // A service has been discovered here, we need to see if it's our app.
                        if (instanceName.equalsIgnoreCase(SERVICE_NAME)) {
                            Log.i(TAG,instanceName+" =? "+ SERVICE_NAME);

                            WiFiP2pService service = new WiFiP2pService();
                            service.device = srcDevice;
                            service.instanceName = srcDevice.deviceName;
                            service.serviceRegistrationType = registrationType;

                            neighbors.add(service);
                            Log.i(TAG,"Device: "+srcDevice.toString());
                            neighborListAdapter.setServices(neighbors);
                            neighborListAdapter.notifyDataSetChanged();

                            Log.d(TAG,
                                    neighbors.get(0) + " : Ruba + Afnan + Element");

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
                                        + record.get(TXTRECORD_PROP_AVAILABLE));

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
     *
     * @param msg
     * @return
     */
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                Log.d(TAG, readMessage);
                message = new VicinityMessage(this, "2", false, readMessage);
                (chat).pushMessage(message);
                break;

            case MY_HANDLE:
                Object obj = msg.obj;
                (chat).setChatManager((ChatManager) obj);

        }
        return true;
    }

    /**
     * This method connects peers with each other
     * @param service the service you want to connect to
     */
    @Override
    public void connectP2p(WiFiP2pService service) {
        Log.i(TAG,"connectP2P");
        //Wi-Fi P2p configuration for setting up a connection
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = service.device.deviceAddress;//device unique MAC address
        final String name=service.device.deviceName;
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


        if (p2pInfo.isGroupOwner) {
            Log.i(TAG, "Connected as group owner");
            try {
                handler = new GroupOwnerSocketHandler(
                       this.getHandler());
                handler.start();
            } catch (IOException e) {
                Log.d(TAG,"Failed to create a server thread - " + e.getMessage());
                return;
            }
        } else {
            Log.d(TAG, "Connected as peer");
            handler = new ClientSocketHandler(
                    this.getHandler(),
                    p2pInfo.groupOwnerAddress);
            handler.start();
        }

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
    public void changeDeviceName(String username){

        try{
            Log.i(TAG,"Changing name!!");

            Method m = manager.getClass().getMethod(
                    "setDeviceName",
                    new Class[] { WifiP2pManager.Channel.class, String.class,
                            WifiP2pManager.ActionListener.class });

            m.invoke(manager,channel, username, new WifiP2pManager.ActionListener() {
                public void onSuccess() {

                    //Code for Success in changing name
                }

                public void onFailure(int reason) {
                    //Code to be done while name change Fails
                }
            });
        }
        catch(Exception e){

        }

    }

    static public ArrayList<WiFiP2pService> getNeighbors(){
        return neighbors;}


    static public void setNAdapter(NeighborListAdapter nAdapter){
        neighborListAdapter = nAdapter;
    }


}
