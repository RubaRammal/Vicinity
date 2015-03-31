
package vicinity.ConnectionManager;

import android.net.wifi.p2p.WifiP2pDevice;

/**
 * A structure to hold service information.
 */
public class WiFiP2pService {
    private WifiP2pDevice device;
    private String instanceName;
    private String serviceRegistrationType;


    public void setDevice(WifiP2pDevice device){
        this.device=device;
    }
    public WifiP2pDevice getDevice(){return this.device;}
    public void setInstanceName(String instanceName){
        this.instanceName=instanceName;
    }
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
