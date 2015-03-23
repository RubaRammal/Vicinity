package vicinity.vicinity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.net.wifi.p2p.WifiP2pManager.Channel;

import java.util.ArrayList;

/**
 * Created by macproretina on 3/23/15.
 */
public class WiFiConnection {

    /***********************Connection Attributes*************************/

    public static final String TAG = "Tabs";
    public static final String TXTRECORD_PROP_AVAILABLE = "available";
    public static final String SERVICE_INSTANCE = "_vicinityapp";
    public static final String SERVICE_REG_TYPE = "_presence._tcp";

    public static final int MESSAGE_READ = 0x400 + 1;
    public static final int MY_HANDLE = 0x400 + 2;
    private WifiP2pManager manager;

    static final int SERVER_PORT = 4505;

    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private BroadcastReceiver receiver = null;
    private WifiP2pDnsSdServiceRequest serviceRequest;
    private WiFiDirectServicesFragment servicesList;
    private ArrayList<String> services;

    /*private Handler handler = new Handler(this);
    public Handler getHandler(){
        return this.handler;
    }
    public void setHandler(Handler handler) {
        this.handler = handler;
    }*/
    //static NeighborSectionFragment neighborFragment = new NeighborSectionFragment();
    /************************************************/
    public WiFiConnection(Context context){

    }
}
