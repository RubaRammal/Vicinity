package vicinity.model;


import java.net.InetAddress;

/**
 * This class contains the frequently used variables
 * and constants among classes, it reduces code redundancy
 */
public class Globals {

    /**
     * Global Constants
     */
    /*---Service discovery and registration variables----*/
    public static final String TXTRECORD_PROP_AVAILABLE = "available";
    public static final String SERVICE_NAME = "_VicinityApp";
    public static final String SERVICE_REG_TYPE = "_presence._tcp";
    /*-----Port numbers---------*/
    public static final int SERVER_PORT = 4142;
    public static final int ADDRESSES_PORT = 4143;
    public static final int REQUEST_PORT= 4144; //for friend requests among peers
    public static final int CHAT_PORT= 4145;

    public static final int MESSAGE_READ = 0x400 + 1;
    public static final int MY_HANDLE = 0x400 + 2;
    public static final int MSG_SIZE = 50;    // the lastest 50 messages
    public static final String MSG_SENDER = "sender";
    public static final String MSG_CONTENT = "body";
    public static final String MSG_ID = "id";
    public static final String MSG_MINE = "isMyMsg";
    public static final String MSG_IMG = "img";



    /*-----Request protocol code messages----*/
    public static boolean isRequestAccepted=false;


    /**
     * Global Variables
     */


    //A variable for notifications switch if it's on/off
    public static boolean Notification = true;
    //A variable to check whether the peer is connected to a network or not
    public static boolean isConnectedToANetwork = false;
    //A variable to check whether WiFi P2P is enabled or not
    public static boolean isWifiEnabled = true;
    //Current device's P2P MAC address
    public static String MY_MAC = null;
    //Current device's local IP
    public static InetAddress MY_IP=null;

    public static int KRYO_TCP_PORT = 54555;
    public static int KRYO_UDP_PORT = 54777;


}
