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
    public static final int SERVER_PORT = 41420;
    public static final int ADDRESSES_PORT = 41430;
    public static final int REQUEST_PORT= 41440; //for friend requests among peers
    public static final int CHAT_PORT= 41450;





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
    public static InetAddress MY_IP = null;
    //To check if ChatActivty is active
    public static boolean chatActive = false;

    public static boolean isNewUser=false;

    //Server flags
    public static boolean isGroupOwnerRunning = false;
    public static boolean isRequestServerRunning=false;
    public static boolean isChatServerRunning = false;
    public static boolean stopServer = true;


}