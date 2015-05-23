package vicinity.ConnectionManager;

import android.app.Service;
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
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import vicinity.Controller.MainController;
import vicinity.model.DBHandler;
import vicinity.model.Globals;
import vicinity.model.Neighbor;
import vicinity.vicinity.FriendListAdapter;
import vicinity.vicinity.NeighborListAdapter;
import vicinity.vicinity.NeighborSectionFragment;
import vicinity.vicinity.NeighborSectionFragment.DeviceClickListener;


/**
 * ConnectAndDiscover Service starts running with the app and handles service discovery and
 * WiFi P2P connection.
 */

public class ConnectAndDiscoverService extends Service
        implements  WifiP2pManager.ConnectionInfoListener, DeviceClickListener{


    private final String TAG = "ConService";
    public static Context ctx;
    private WifiP2pManager manager;
    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private BroadcastReceiver receiver = null;
    private WifiP2pDnsSdServiceRequest serviceRequest;
    private WifiP2pDnsSdServiceInfo localService;
    public static NeighborListAdapter neighborListAdapter;
    public static FriendListAdapter friendListAdapter;
    private MainController controller;
    private static InetAddress GOIP;
    private DBHandler db;






    /*---------Overridden Methods------------*/
    @Override
    public void onCreate(){
        Log.i(TAG,"Service started: "+ Globals.SERVICE_NAME);

        //Registering WiFi P2P intents to broadcastreceiver
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        ctx= ConnectAndDiscoverService.this;
        controller = new MainController(ctx);
        db = new DBHandler(this);



        //Initializing WiFiP2pManager
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);

        //Registers the application with the Wi-Fi framework.
        channel = manager.initialize(this, getMainLooper(), null);

        //Initializing and registering broadcastreceiver and its intents
        receiver = new WiFiDirectBroadcastReceiver(manager,channel,ctx);
        registerReceiver(receiver,intentFilter);

        //Changing the username depending on the registered one in the database
        try {
            changeDeviceName(controller.retrieveCurrentUsername());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        startRegistrationAndDiscovery();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onDestroy(){
        Log.i(TAG,"Service destroyed");
        unregisterReceiver(receiver);

        disconnectPeers();
        db.deleteDatabase();        //TODO delete this line later
        removeLocalRequest();
        cancelConnection();
        stopServers();

    }

    /*-----------------------------------------*/


    /**
     * This method adds _vicinityapp local service to the network
     * then calls discoverService()
     */
    private void startRegistrationAndDiscovery() {
        Map<String, String> record = new HashMap<String, String>();
        record.put(Globals.TXTRECORD_PROP_AVAILABLE, "visible");
        localService = WifiP2pDnsSdServiceInfo.newInstance(Globals.SERVICE_NAME, Globals.SERVICE_REG_TYPE, record);
        manager.addLocalService(channel, localService, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.i(TAG,"_vicinityapp Service registered");
                discoverService();
            }
            @Override
            public void onFailure(int error) {
                Log.i(TAG,"Failed to add a service");
                //TODO Must give user feedback here
            }
        });
    }

    /**
     * First discoverService() registers listeners for DNS-SD services
     * Then it creates a service discovery request and initiates service discovery
     * afterwards, it finds peers with WiFi direct in the area
     * filters them according to their services, then filters them into two lists
     * friends and neighbor.
     * @throws java.lang.NullPointerException
     */
    private void discoverService() throws NullPointerException{
        Log.i(TAG,"discoverService");
        manager.setDnsSdResponseListeners(channel,
                new WifiP2pManager.DnsSdServiceResponseListener() {
                    @Override
                    public void onDnsSdServiceAvailable(String instanceName,
                                                        String registrationType, WifiP2pDevice srcDevice) {
                        /*---Filtering services so the user can only see Vicinity users---*/
                        if (instanceName.equals(Globals.SERVICE_NAME)) {

                            Neighbor service = new Neighbor(srcDevice.deviceName,srcDevice.deviceAddress,getDeviceStatus(srcDevice.status));
                            Log.i(TAG,"Neighbor: "+service.toString());

                            //Check if whether the peer is a friend or not
                            if(controller.isThisMyFriend(srcDevice.deviceAddress))
                            {
                                NeighborSectionFragment.addToFriendsList(service);
                            }
                            else{
                                NeighborSectionFragment.addToNeighborsList(service);
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
                        Log.i(TAG,"Failed adding service discovery request: "+getFailureReason(arg0));
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
                Log.i(TAG, "Service discovery failed: "+getFailureReason(arg0));


            }
        });

    }//end of discoverServices


    /**
     * Connects devices together
     * @param service a Neighbor to be connected to
     */
    @Override
    public void connectP2p(Neighbor service) {
        Log.i(TAG,"connectP2P");

        //Wi-Fi P2p configuration for setting up a connection
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = service.getDeviceAddress();//device unique MAC address
        final String name=service.getInstanceName();//Device name
        config.wps.setup = WpsInfo.PBC;//Wifi permission Push Button



        manager.connect(channel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.i(TAG, "Connected to " + name);

            }

            @Override
            public void onFailure(int errorCode) {
                Log.i(TAG,"Failed connecting to service: "+getFailureReason(errorCode));

                //Since it resulted in a failure, cancel any attempt to connect to the peer
                manager.cancelConnect(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.i(TAG,"Connection cancelled");
                    }


                    @Override
                    public void onFailure(int reason) {
                        Log.i(TAG,"Reason: "+getFailureReason(reason));
                    }
                });
            }
        });
    }



    /**
     * After connecting to a P2P group this method is invoked
     * @param p2pInfo
     */
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {
        Log.i(TAG, "onConnectionAvailable");
        Thread handler = null;

        GOIP = p2pInfo.groupOwnerAddress;
        try {
            //If peer becomes a group owner:
            if (p2pInfo.isGroupOwner) {
                Log.i(TAG, "Connected as group owner");
                //Start the group owner server if it was not running already
                if(!Globals.isGroupOwnerRunning)
                {
                    Globals.isGroupOwnerRunning=true;
                    Thread groupOwnerSocketHandler = new GroupOwnerSocketHandler();
                    groupOwnerSocketHandler.start();
                }
            }
            //If peer is just a peer
            else {
                Log.d(TAG, "Connected as peer");
                Thread.sleep(1000);
                handler = new ClientSocketHandler(p2pInfo.groupOwnerAddress);
                handler.start();
            }

            //Start the requests server if it was not running already
            if(!Globals.isRequestServerRunning){
                RequestServer requestServer = new RequestServer();
                Globals.isRequestServerRunning=true;
                requestServer.start();

            }
            if(!Globals.isChatServerRunning){
                Globals.isChatServerRunning=true;
                ChatServer chatSocket = new ChatServer();
                new Thread(chatSocket).start();
            }

        }catch (IOException e) {
            Log.d(TAG,"Failed to create a server thread - " + e.getMessage());
            return;
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }


    }



    /**
     * Get Group Owner's IP address of the current network
     * @return IP address of group owner
     */
    public static InetAddress getGOAddress(){
        Log.i("Request","GO IP: "+GOIP);
        return GOIP;
    }


    /**
     * This method changes the original device name
     * to the user's username
     * @param username registered username
     */
    public void changeDeviceName(final String username){

        try{
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
        catch(IllegalAccessException | InvocationTargetException | NoSuchMethodException e){
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

    /**
     * Removes advertised service request from the local network
     */
    private void removeLocalRequest(){
        //Remove advertised service request
        if (serviceRequest != null)
            manager.removeServiceRequest(channel, serviceRequest,
                    new WifiP2pManager.ActionListener() {

                        @Override
                        public void onSuccess() {
                            Log.i(TAG,"Removed local service");
                        }

                        @Override
                        public void onFailure(int arg0) {
                        }
                    });
        if(localService!=null)
            manager.removeLocalService(channel,localService,new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.i(TAG,"Local service removed");
                }

                @Override
                public void onFailure(int reason) {

                }
            });
    }


    /**
     * Cancels any attempt to connect to another peer
     * this method is implemented to avoid getting stuck while
     * connection failure
     */
    private void cancelConnection(){
        manager.cancelConnect(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.i(TAG,"Connection cancelled");
            }


            @Override
            public void onFailure(int reason) {
                Log.i(TAG,"Reason: "+getFailureReason(reason));
            }
        });

    }


    /**
     * Converts an integer that indicates device status into
     * a String,this method is mainly used for debugging reasons
     * @param deviceStatus int a WiFiP2pDevice status
     * @return A String that translates that status
     */
    public static String getDeviceStatus(int deviceStatus) {
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown = " + deviceStatus;
        }
    }


    /**
     * Converts an integer that indicates a reason code into
     * a String, this method is mainly used for debugging reasons
     * @param reasonCode int a failure reason code
     * @return A String that translates that status
     */
    public static String getFailureReason(int reasonCode){
        switch (reasonCode) {
            case WifiP2pManager.ERROR:
                return "Error";
            case WifiP2pManager.BUSY:
                return "Busy";
            case WifiP2pManager.P2P_UNSUPPORTED:
                return "P2P Unsupported";
            case WifiP2pManager.NO_SERVICE_REQUESTS:
                return "No Service Requests";

            default:
                return "Unknown = " + reasonCode;
        }
    }

    /**
     * To stop running server threads
     * GroupOwnerSocketHandler
     * RequestServer
     * ChatServer
     */
    public void stopServers(){
        if(Globals.isChatServerRunning&&Globals.isRequestServerRunning&&Globals.isGroupOwnerRunning)
            Log.i(TAG,"All servers are running");
        Globals.isRequestServerRunning=false;
        Globals.isGroupOwnerRunning=false;
        Globals.isChatServerRunning=false;
        if(!Globals.isChatServerRunning&&!Globals.isRequestServerRunning&&!Globals.isGroupOwnerRunning)
            Log.i(TAG,"Servers have been shut down successfully");
    }

    /**
     * Setters for neighbors and friends list adapters.
     */
    static public void setNAdapter(NeighborListAdapter nAdapter){
        neighborListAdapter = nAdapter;
    }
    static public void setFAdapter(FriendListAdapter fAdapter){
        friendListAdapter = fAdapter;
    }



}