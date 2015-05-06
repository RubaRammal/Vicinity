package vicinity.model;

/**
 * This class contains the frequently used variables
 * and constants among classes
 */
public class Globals {
    public static final String TXTRECORD_PROP_AVAILABLE = "available";
    public static final String SERVICE_NAME = "_VicinityApp";
    public static final String SERVICE_REG_TYPE = "_presence._tcp";
    public static final int SERVER_PORT = 4142;
    public static final int ADDRESSES_PORT = 4143;
    public static final int MESSAGE_READ = 0x400 + 1;
    public static final int MY_HANDLE = 0x400 + 2;
    public static boolean Notification = true;
    public static boolean isConnectedToANetwork = false;
    public static String MY_MAC = null;


    public static final int MSG_SIZE = 50;    // the lastest 50 messages
    public static final String MSG_SENDER = "sender";
    public static final String MSG_CONTENT = "body";
    public static final String MSG_ID = "id";
    public static final String MSG_MINE = "isMyMsg";


}
