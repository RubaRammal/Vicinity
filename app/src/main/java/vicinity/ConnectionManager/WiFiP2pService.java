
package vicinity.ConnectionManager;

import android.net.wifi.p2p.WifiP2pDevice;

/**
 * A structure to hold service information.
 */
public class WiFiP2pService {
    protected WifiP2pDevice device;
    protected String instanceName;
    protected String serviceRegistrationType;
    protected String deviceAddress;
    private String _aliasName;//for friends only


    /**
     * Default constructor
     */
    public WiFiP2pService(){

    }
    /**
     * Public constructor to initiate a full WiFiP2pService
     */
    public WiFiP2pService(WifiP2pDevice device){
        this.device=device;
        instanceName = device.deviceName;
        deviceAddress=device.deviceAddress;
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
    public String getAliasName(){
        return this._aliasName;
    }
    public void setAliasName(String newName){_aliasName=newName;}

    @Override
    public String toString(){
        return instanceName;
    }
}
