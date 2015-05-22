
package vicinity.ConnectionManager;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.util.Log;
import vicinity.model.Globals;
import vicinity.model.Neighbor;

/**
 * this class is a WiFi BroadcastReceiver
 * Listens to wifi events and alerts the system
 * this class will be moved as it is to Vicinity
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    final String TAG ="WiFiBCReceiver";
    private WifiP2pManager manager;
    private Channel channel;
    private Context context;
    private static Neighbor me;

    /**
     * @param manager WifiP2pManager system service
     * @param channel Wifi p2p channel
     * @param context activity associated with the receiver
     */
    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel,
                                       Context context) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.context = context;
    }

    /**
     * WiFi events happen here
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();


        if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            //Broadcast intent action indicating that peer discovery has either started or stopped.
            if (manager == null) {
                return;
            }
            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {
                Globals.isConnectedToANetwork=true;
                Log.i(TAG,"Network info: "+Globals.isConnectedToANetwork);
                manager.requestConnectionInfo(channel,(ConnectionInfoListener) context);

            } else {
                Globals.isConnectedToANetwork=false;
                Log.d(TAG,"NOT Connected to P2P network!");

            }
        }
        else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
                .equals(action)) {
            //Getting information of this current device

            WifiP2pDevice device = (WifiP2pDevice) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);

            //obtaining this device's mac address
            me = new Neighbor(device.deviceName,device.deviceAddress,ConnectAndDiscoverService.getDeviceStatus(device.status));
            if(Globals.MY_IP!=null)
            {
                me.setIpAddress(Globals.MY_IP.getHostAddress());
            }
            if(Globals.MY_MAC == null)
                {
                    Globals.MY_MAC = device.deviceAddress;
                    Log.i(TAG, "My device name: " + device.deviceName + " Device status: " + ConnectAndDiscoverService.getDeviceStatus(device.status) + " My WiFi Direct MAC address: " + Globals.MY_MAC);
                }

        }
        else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            Log.i(TAG,"WIFI_P2P_PEERS_CHANGED_ACTION");
            // The peer list has changed!
            //TODO update devices list here

        }
        else if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
            //Broadcast intent action to indicate whether Wi-Fi p2p is enabled or disabled.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if(state == WifiP2pManager.WIFI_P2P_STATE_DISABLED){
                Globals.isWifiEnabled = false;
                Log.i(TAG,"WiFi Direct is disabled");
            }
            else
            {
                Globals.isWifiEnabled = true;
                Log.i(TAG,"WiFi Direct is enabled");

            }

        }

    }//end onReceive

    /**
     * Gets current device info, this method is
     * used in sending friends requests
     * @return a Neighbor object that
     * contains current device's info
     */
    public static Neighbor getMyP2pInfo(){
        return me;
    }
}
