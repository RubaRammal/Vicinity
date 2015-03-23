package vicinity.ConnectionManager;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import android.annotation.TargetApi;
import android.content.Context;
import android.widget.Toast;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.InetAddress;

/**
 * This class handles network service discovery operations
 * here we'll advertise our app service so only other devices with the same service
 * can connect to us.
 */

@TargetApi(16)
public class NSDHandler {

    private static final String TAG = "NSDHandler";
    public String service_name = "_vicinityapp";
    public static final String SERVICE_TYPE="_presence.tcp.";

    NsdManager.RegistrationListener registrationListener;
    NsdManager.DiscoveryListener discoveryListener;
    NsdManager nsdManager;
    NsdManager.ResolveListener resolveListener;
    NsdServiceInfo serviceInfo;
    Context context;

    /**
     * Public constructor.
     * @param context .
     */
    public NSDHandler(Context context){
        this.context=context;
        nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    /**
     * This method MUST be called from the activity
     * you want to start service discovery from
     */
    public void initializeNsd() {

        initializeResolveListener();
        initializeDiscoveryListener();
        initializeRegistrationListener();
        Toast toast = Toast.makeText(context, "Initializing NSD", Toast.LENGTH_LONG);//we can customize this Toast later -Afnan
        toast.show();
        //mNsdManager.init(mContext.getMainLooper(), this);
    }
    /**
     * This method registers Vicinity's service on the local network
     * @param portNumber port number
     */
    public void registerService(int portNumber){
        Log.i(TAG,"registerService");
        //This object provides the information that other devices on the network use
        // when they're deciding whether to connect to your service.
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(service_name);
        serviceInfo.setPort(portNumber); // We might hardcode the port number here, we'll c later about that -AFNAN
        serviceInfo.setServiceType(SERVICE_TYPE); //not sure of the service type yet

        nsdManager.registerService(
                serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener);

    }

    /**
     *
     * @throws IOException
     */
    public void initializeServerSocket() throws IOException{
        // Initialize a server socket on the next available port
        // by assigning port zero.
        ServerSocket serverSocket = new ServerSocket(0);
        // Store the chosen port.
        int localPort =  serverSocket.getLocalPort();
    }

    /**
     *  This method contains callbacks used by Android to alert the app
     *  of the success or failure of service registration and unregistration.
     */
    public void initializeRegistrationListener(){
        Log.i(TAG,"Registering service");

        registrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
                // Save the service name.  Android may have changed it in order to
                // resolve a conflict, so update the name you initially requested
                // with the name Android actually used.
                service_name = NsdServiceInfo.getServiceName();
                Log.i(TAG,"onServiceRegistered: "+service_name);
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Registration failed!  Put debugging code here to determine why.
                Log.i(TAG,"onRegistrationFailed");
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
                // Service has been unregistered.  This only happens when you call
                // NsdManager.unregisterService() and pass in this listener.
                Log.i(TAG,"onServiceUnregistered");
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Unregistration failed.  Put debugging code here to determine why.
                Log.i(TAG,"onUnregistrationFailed");
            }
        };

        }

    /**
     * This method makes our app see the available services in the local network
     * Here we can filter services: meaning that we can choose to only connect
     * with devices that advertise _vicinityapp service
     */
        public void initializeDiscoveryListener(){
            Log.i(TAG,"Discovering service");
        discoveryListener = new NsdManager.DiscoveryListener() {


            //The following method is called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
                Toast toast = Toast.makeText(context, "Service discovery started", Toast.LENGTH_LONG);//we can customize this Toast later -Afnan
                toast.show();
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                Log.d(TAG, "Service discovery success" + service);


                if (!service.getServiceType().equals(SERVICE_TYPE)) {
                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                } else if (service.getServiceName().equals(service_name)) {
                    Log.d(TAG, "Same service: " + service_name);
                } else if (service.getServiceName().contains(service_name)){
                    Toast toast = Toast.makeText(context, "Found the same service", Toast.LENGTH_LONG);//we can customize this Toast later -Afnan
                    toast.show();
                    nsdManager.resolveService(service, resolveListener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                Log.e(TAG, "service lost" + service);

            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
               // mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                //mNsdManager.stopServiceDiscovery(this);
            }


        };

        }

    /**
     * This method is called after discovering a service
     * In order to connect to any service
     * the service connection information must be determined by this method
     * By resolving a service, our app receives information
     * including ip address and port number.
     */
    public void initializeResolveListener(){
        Log.i(TAG,"Resolving service");
        resolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Called when the resolve fails.  Use the error code to debug.
                Log.e(TAG, "Resolve failed" + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo2) {
                Log.e(TAG, "Resolve Succeeded. " + serviceInfo);

                if (serviceInfo.getServiceName().equals(service_name)) {
                    Log.d(TAG, "Same IP.");
                    return;
                }
                Toast toast = Toast.makeText(context, "Service resolved", Toast.LENGTH_LONG);//we can customize this Toast later -Afnan
                toast.show();
                serviceInfo = serviceInfo2;
                int port = serviceInfo.getPort();
                InetAddress host = serviceInfo.getHost();
            }
        };
    }

    /**
     * This method passes the service type that Vicinity should look for
     */
    public void discoverServices() {
        nsdManager.discoverServices(
                SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);
    }

    /**
     * Stopping service discovery
     */
    public void stopDiscovery() {
        nsdManager.stopServiceDiscovery(discoveryListener);
    }

    /**
     * Getting service info in order to connect to it
     * @return serviceInfo an object of NsdServiceInfo
     * that contains information about the service
     */
    public NsdServiceInfo getChosenServiceInfo() {
        return serviceInfo;
    }

    /**
     * Unregister the service
     */
    public void tearDown() {
        nsdManager.unregisterService(registrationListener);
    }

}





