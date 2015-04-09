
package vicinity.ConnectionManager;

import android.net.wifi.p2p.WifiP2pDevice;

import vicinity.Controller.MainController;

/**
 * A structure to hold service information.
 */
public class WiFiP2pService {
    protected WifiP2pDevice device;
    protected String instanceName;
    protected String serviceRegistrationType;
    protected String deviceAddress;

    /**
     * Default constructor
     */
    public WiFiP2pService(){

    }
    /**
     * Public constructor to initiate a full WiFiP2pService
     */
    public WiFiP2pService(WifiP2pDevice device, String instanceName, String serviceRegistrationType){
        this.device=device; this.instanceName=instanceName; this.serviceRegistrationType=serviceRegistrationType;
    }


    public void setDevice(WifiP2pDevice device){
        this.device=device;
    }
    public WifiP2pDevice getDevice(){return this.device;}
    public void setInstanceName(String instanceName){
        this.instanceName=instanceName;
    }
    public void setDeviceAddress(String deviceAddress){this.deviceAddress = deviceAddress;}
    public String getDeviceAddress(){return this.deviceAddress;}
    public String getInstanceName(){return this.instanceName;}
    public void setServiceRegistrationType(String serviceRegistrationType){
        this.serviceRegistrationType=serviceRegistrationType;
    }
    public String getServiceRegistrationType(){return this.serviceRegistrationType;}

    @Override
    public String toString(){
        return instanceName;
    }
}
