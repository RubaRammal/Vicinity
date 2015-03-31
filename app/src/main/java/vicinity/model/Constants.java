package vicinity.model;


/**
 * This is a java practice, it's a class full of global constants
 * it prevents some code redundancies..
 */
public class Constants {


    public static final String TXTRECORD_PROP_AVAILABLE = "available";

    //Our service's name and protocol
    public static final String SERVICE_NAME = "_VicinityApp";
    public static final String SERVICE_REG_TYPE = "_presence._tcp";
    public static final int SERVER_PORT = 4142;
    public static final int MESSAGE_READ = 0x400 + 1;
    public static final int MY_HANDLE = 0x400 + 2;
}
